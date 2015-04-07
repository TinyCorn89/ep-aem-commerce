/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceFacet;
import com.adobe.cq.commerce.api.CommerceQuery;
import com.adobe.cq.commerce.api.CommerceResult;
import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceSort;
import com.adobe.cq.commerce.api.PaginationInfo;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.common.CommerceSearchProvider;
import com.adobe.cq.commerce.common.DefaultCommerceResult;

import com.elasticpath.aem.commerce.service.UserPropertiesService;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.aem.commerce.ElasticPathCommerceService;
import com.elasticpath.aem.commerce.cortex.CortexContext;
import com.elasticpath.rest.client.jaxrs.JaxRsUtil;
import com.elasticpath.rest.client.CortexClient;
import com.elasticpath.rest.client.CortexClientFactory;
import com.elasticpath.rest.client.CortexResponse;
import com.elasticpath.rest.sdk.views.AuthForm;
import com.elasticpath.rest.sdk.views.PaginatedSearchResults;
import com.elasticpath.rest.sdk.views.SearchForm;
import com.elasticpath.rest.sdk.components.Item;
import com.elasticpath.rest.sdk.components.Pagination;
import com.elasticpath.rest.sdk.service.AuthService;
import com.elasticpath.rest.sdk.service.impl.AuthServiceImpl;

/**
 * ElasticPathSearchProviderImpl provides functionality for search specific to ElasticPath and calls search action.
 */
@Service({ CommerceSearchProvider.class })
@Component
@Property(name = CommerceSearchProvider.NAME_PROPERTY, value = ElasticPathSearchProviderImpl.PROVIDER_NAME)
public class ElasticPathSearchProviderImpl implements CommerceSearchProvider {

	/** The Constant PROVIDER_NAME. */
	public static final String PROVIDER_NAME = "ElasticPathSearch";

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ElasticPathSearchProviderImpl.class);

	@Reference
	private CortexClientFactory cortexClientFactory;

	@Reference
	private Client jaxRsClient;

	@Reference
	private UserPropertiesService userPropertiesService;

	/**
	 * This method searches the result corresponding to keyword.
	 *
	 * @param query CommerceQuery
	 * @param commerceService CommerceService
	 * @return search result
	 * @throws CommerceException CommerceException
	 */
	public CommerceResult search(final CommerceQuery query, final CommerceService commerceService) throws CommerceException {
		ElasticPathCommerceService elasticPathCommerceService;
		if (commerceService instanceof ElasticPathCommerceService) {
			elasticPathCommerceService = (ElasticPathCommerceService) commerceService;
		} else {
			throw new CommerceException("CommerceService not correct type");
		}

		PaginatedSearchResults searchResults = getSearchResults(query, elasticPathCommerceService);

		PaginationInfo paginationInfo = searchResults.getPaginationInfo();
		List<Product> resultProductList = getProductList(searchResults.getResults(), elasticPathCommerceService);

		DefaultCommerceResult result = new DefaultCommerceResult();
		result.setProducts(resultProductList);
		result.setPaginationInfo(paginationInfo);
		result.setFacets(Collections.<CommerceFacet>emptyList());
		result.setSorts(Collections.<CommerceSort>emptyList());

		return result;
	}

	private PaginatedSearchResults getSearchResults(final CommerceQuery query, final ElasticPathCommerceService elasticPathCommerceService)
			throws CommerceException {
		CortexClient client = getCortexClient(elasticPathCommerceService);
		SearchForm form = new SearchForm();
		form.setKeywords(query.getQueryText());
		form.setPageSize(query.getPageSize());

		CortexResponse<PaginatedSearchResults> searchResponseWrapper = client.post(form.asMap(), PaginatedSearchResults.class);

		if (query.getPage() > 0) {
			PaginatedSearchResults firstResultsPage = searchResponseWrapper.getCortexView();
			String pageUri = StringUtils.substringBefore(firstResultsPage.getSelf().getUri(), "?zoom=") + "/pages/"
					+ incrementCortexCurrentPage(query.getPage());
			searchResponseWrapper = client.get(pageUri, PaginatedSearchResults.class);
		}

		decrementCortexPaginationCurrentPage(searchResponseWrapper.getCortexView());

		Response.StatusType statusInfo = searchResponseWrapper.getResponse().getStatusInfo();
		if (JaxRsUtil.isNotSuccessful(statusInfo)) {
			throw new CommerceException("An error occurred performing the search, Status: " + statusInfo.getStatusCode() + " reason: "
					+ statusInfo.getReasonPhrase());
		}
		return searchResponseWrapper.getCortexView();
	}

	private CortexClient getCortexClient(final ElasticPathCommerceService commerceService) throws CommerceException {
		AuthService authService = new AuthServiceImpl(
				commerceService.getCortexConfig(),
				commerceService.getCortexScope(),
				jaxRsClient,
				cortexClientFactory,
				userPropertiesService
				);

		AuthForm loginForm = new AuthForm();
		loginForm.setGrantType("password");
		loginForm.setRole("PUBLIC");
		loginForm.setScope(commerceService.getCortexScope());
		CortexContext context = authService.loginToCortex(loginForm);

		return cortexClientFactory.create(
				context.getAuthenticationToken(),
				commerceService.getCortexScope());
	}

	private List<Product> getProductList(final Iterable<Item> itemResults, final ElasticPathCommerceService elasticpathCommerceService)
			throws CommerceException {
		List<Product> resultProductList = new ArrayList<>();

		for (final Item item : itemResults) {
			String skuCode = item.getMappings().iterator().next().getSkuCode();
			Product baseProduct = elasticpathCommerceService.getProductBySkuCode(elasticpathCommerceService.getResource(), skuCode);

			if (baseProduct == null) {
				LOG.warn("Mismatch between Cortex and AEM, product with skuCode {} does not exist in AEM", skuCode);
			} else {
				resultProductList.add(baseProduct);
			}

		}
		return resultProductList;
	}

	// Adobe page index start from 0, while EP starts from 1
	private int incrementCortexCurrentPage(final int adobeQueryPageId) {
		return adobeQueryPageId + 1;
	}

	// when returning back search results, EP page index must be decremented for 1 to match expected Adobe's page index
	private void decrementCortexPaginationCurrentPage(final PaginatedSearchResults response) {

		Pagination pagination = response.getPagination();
		pagination.setCurrent(pagination.getCurrent() - 1);
	}
}