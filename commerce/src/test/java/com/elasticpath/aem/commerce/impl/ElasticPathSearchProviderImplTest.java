/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceQuery;
import com.adobe.cq.commerce.api.CommerceResult;
import com.adobe.cq.commerce.api.Product;
import com.google.common.collect.Lists;

import org.apache.sling.api.resource.Resource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.aem.commerce.AbstractElasticPathCommerceService;
import com.elasticpath.aem.commerce.cortex.CortexContext;
import com.elasticpath.aem.commerce.cortex.CortexServiceContext;
import com.elasticpath.rest.sdk.views.PaginatedSearchResults;
import com.elasticpath.rest.sdk.views.SearchForm;
import com.elasticpath.commerce.config.DemoPasswordConfiguration;
import com.elasticpath.rest.client.CortexClient;
import com.elasticpath.rest.client.CortexClientFactory;
import com.elasticpath.rest.client.CortexResponse;
import com.elasticpath.rest.sdk.components.Item;
import com.elasticpath.rest.sdk.components.Mappings;
import com.elasticpath.rest.sdk.components.Pagination;
import com.elasticpath.rest.sdk.components.Self;

/**
 * Tests {@link ElasticPathSearchProviderImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ElasticPathSearchProviderImplTest {

	private static final String QUERY_STRING = "snow";
	private static final int PAGE_SIZE = 10;
	private static final int SINGLE = 1;
	private static final int UNDEFINED_PAGE_NUMBER = -SINGLE;
	private static final String TEST_TOKEN = "testToken";
	private static final String TEST_CORTEX_SCOPE = "geometrixx";
	private static final String TEST_CORTEX_URL = "http://testurl.com";
	private static final int LARGE_PAGE_SIZE = 12;

	@Mock
	private CortexClientFactory cortexClientFactory;
	@Mock
	private DemoPasswordConfiguration handler;
	@Mock
	private CortexClient mockCortexClient;
	@Mock
	private Client jaxRsClient;
	@Mock
	private AbstractElasticPathCommerceService mockEpCommerceService;
	@InjectMocks
	private ElasticPathSearchProviderImpl elasticPathSearchProvider;

	@Before
	public void setUp() throws CommerceException {
		when(mockEpCommerceService.getCortexScope()).thenReturn(TEST_CORTEX_SCOPE);
		when(mockEpCommerceService.getProductBySkuCode(any(Resource.class), any(String.class))).thenReturn(mock(Product.class));

		mockLoginRequest();
		when(cortexClientFactory.getCortexURL()).thenReturn(TEST_CORTEX_URL);
		when(cortexClientFactory.create(TEST_TOKEN, TEST_CORTEX_SCOPE)).thenReturn(mockCortexClient);
	}

	@Test
	public void testSearchSinglePage() throws CommerceException {
		CortexResponse<PaginatedSearchResults> searchResults = givenSearchResults();
		CommerceQuery query = givenQuery(QUERY_STRING, PAGE_SIZE, UNDEFINED_PAGE_NUMBER);
		SearchForm form = new SearchForm();
		form.setKeywords(query.getQueryText());
		form.setPageSize(query.getPageSize());
		when(mockCortexClient.post(form.asMap(), PaginatedSearchResults.class)).thenReturn(searchResults);

		CommerceResult result = elasticPathSearchProvider.search(query, mockEpCommerceService);

		assertEquals(2, result.getProducts().size());
		// AEM expects EP_CURRENT_PAGE - 1
		assertEquals(0, result.getPaginationInfo().getCurrentPage());
		assertEquals(2, result.getPaginationInfo().getTotalResults());
	}

	@Test
	public void testSearchMultiplePages() throws CommerceException {
		CortexResponse<PaginatedSearchResults> searchResults = givenSearchResults();
		Pagination pagination = new Pagination();
		pagination.setCurrent(SINGLE);
		pagination.setPages(2);
		pagination.setPageSize(1);
		pagination.setResults(2);
		pagination.setResultsOnPage(1);
		searchResults.getCortexView().setPagination(pagination);
		List<Item> items = Lists.newArrayList();
		items.add(createItem("SKU-ONE"));
		searchResults.getCortexView().setResults(items);

		CommerceQuery query = givenQuery(QUERY_STRING, 1, UNDEFINED_PAGE_NUMBER);
		SearchForm form = new SearchForm();
		form.setKeywords(query.getQueryText());
		form.setPageSize(query.getPageSize());
		when(mockCortexClient.post(form.asMap(), PaginatedSearchResults.class)).thenReturn(searchResults);

		CommerceResult result = elasticPathSearchProvider.search(query, mockEpCommerceService);

		assertEquals(1, result.getProducts().size());
		// AEM expects EP_CURRENT_PAGE - 1
		assertEquals(0, result.getPaginationInfo().getCurrentPage());
		assertEquals(2, result.getPaginationInfo().getTotalResults());
	}

	@Test
	public void testSearchSecondPage() throws CommerceException {
		CortexResponse<PaginatedSearchResults> searchResults = givenSearchResults();
		Pagination pagination = new Pagination();
		pagination.setCurrent(2);
		pagination.setPages(2);
		pagination.setPageSize(PAGE_SIZE);
		pagination.setResults(LARGE_PAGE_SIZE);
		pagination.setResultsOnPage(2);
		searchResults.getCortexView().setPagination(pagination);
		Self self = new Self();
		self.setUri("/search/query");
		searchResults.getCortexView().setSelf(self);

		CommerceQuery query = givenQuery(QUERY_STRING, PAGE_SIZE, 1);
		SearchForm form = new SearchForm();
		form.setKeywords(query.getQueryText());
		form.setPageSize(query.getPageSize());

		when(mockCortexClient.post(form.asMap(), PaginatedSearchResults.class)).thenReturn(searchResults);
		when(mockCortexClient.get("/search/query/pages/2", PaginatedSearchResults.class)).thenReturn(searchResults);

		CommerceResult result = elasticPathSearchProvider.search(query, mockEpCommerceService);

		assertEquals(2, result.getProducts().size());
		// AEM expects EP_CURRENT_PAGE - 1
		assertEquals(1, result.getPaginationInfo().getCurrentPage());
		assertEquals(LARGE_PAGE_SIZE, result.getPaginationInfo().getTotalResults());
	}

	private CortexResponse<PaginatedSearchResults> givenSearchResults() {
		PaginatedSearchResults searchResults = new PaginatedSearchResults();
		Pagination pagination = new Pagination();
		pagination.setCurrent(SINGLE);
		pagination.setPages(SINGLE);
		pagination.setPageSize(PAGE_SIZE);
		pagination.setResults(2);
		pagination.setResultsOnPage(2);
		searchResults.setPagination(pagination);

		List<Item> items = Lists.newArrayList();
		items.add(createItem("SKU-ONE"));
		items.add(createItem("SKU-TWO"));
		searchResults.setResults(items);

		return new CortexResponse<>(searchResults, Response.status(Response.Status.OK).build());
	}

	private Item createItem(final String skuCode) {
		Mappings mappingOne = new Mappings();
		mappingOne.setSkuCode(skuCode);
		List<Mappings> mappings = Lists.newArrayList();
		mappings.add(mappingOne);
		Item item = new Item();
		item.setMappings(mappings);
		return item;
	}

	private CommerceQuery givenQuery(final String query, final int pageSize, final int page) {
		CommerceQuery.Builder queryBuilder = new CommerceQuery.Builder().setPageSize(pageSize).setQueryText(query);

		if (page > 0) {
			queryBuilder.setPage(page);
		}

		return queryBuilder.build();
	}

	private void mockLoginRequest() {
		CortexServiceContext mockCortexServiceContext = mock(CortexServiceContext.class);
		when(mockEpCommerceService.getCortexConfig()).thenReturn(mockCortexServiceContext);
		when(mockCortexServiceContext.getHandler()).thenReturn(handler);
		Invocation.Builder mockBuilder = mock(Invocation.Builder.class);
		Response mockClientResponse = mock(Response.class);
		WebTarget mockWebTarget = mock(WebTarget.class);
		when(jaxRsClient.target(TEST_CORTEX_URL + "/oauth2/tokens")).thenReturn(mockWebTarget);
		when(mockWebTarget.request()).thenReturn(mockBuilder);
		when(mockBuilder.post(isA(Entity.class))).thenReturn(mockClientResponse);
		Response.StatusType statusType = mock(Response.StatusType.class);
		when(mockClientResponse.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);
		CortexContext mockCortexContext = mock(CortexContext.class);
		when(mockClientResponse.readEntity(isA(Class.class))).thenReturn(mockCortexContext);
		when(mockCortexContext.getAuthenticationToken()).thenReturn(TEST_TOKEN);
	}
}