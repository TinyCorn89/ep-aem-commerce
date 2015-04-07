/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce;

import static com.elasticpath.commerce.config.ElasticPathGlobalConstants.PRODUCT_IDENTIFIER;
import static com.elasticpath.aem.commerce.constants.ElasticPathConstants.VARIATION_AXIS;
import static com.elasticpath.aem.commerce.constants.ElasticPathConstants.VARIATION_TITLE;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import com.adobe.cq.commerce.api.CommerceConstants;
import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.api.promotion.Voucher;
import com.adobe.cq.commerce.common.AbstractJcrCommerceService;
import com.adobe.cq.commerce.common.promotion.AbstractJcrVoucher;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

import com.elasticpath.aem.commerce.impl.ElasticPathCommerceSessionImpl;
import com.elasticpath.aem.commerce.impl.ElasticPathProductImpl;
import com.elasticpath.aem.commerce.impl.ElasticPathSearchProviderImpl;
import com.elasticpath.aem.commerce.service.UserPropertiesService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.aem.commerce.util.ElasticPathRolloutProductService;
import com.elasticpath.aem.commerce.constants.ElasticPathConstants;
import com.elasticpath.aem.commerce.cortex.CortexServiceContext;
import com.elasticpath.aem.commerce.cortex.CortexContext;
import com.elasticpath.aem.commerce.util.ElasticPathCommerceUtil;
import com.elasticpath.rest.client.CortexClient;
import com.elasticpath.rest.client.CortexClientFactory;
import com.elasticpath.rest.client.jaxrs.JaxRsUtil;
import com.elasticpath.rest.sdk.service.AuthService;
import com.elasticpath.rest.sdk.CortexSdkServiceFactory;
import com.elasticpath.rest.sdk.service.impl.AuthServiceImpl;

/**
 * ElasticPathCommerceServiceImpl provides functionalities related to Resources like login, product, etc. This is an entry point to get the
 * ElasticPath commerce session {@link com.elasticpath.aem.commerce.ElasticPathCommerceSession}.
 */
public abstract class AbstractElasticPathCommerceService extends AbstractJcrCommerceService implements ElasticPathCommerceService {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractElasticPathCommerceService.class);
	private static final int EXPECTED_CORTEX_PING_STATUS = 401;

	private final Resource resource;

	private final CortexServiceContext cortexConfig;

	private final String cortexScope;

	private final CortexClientFactory cortexClientFactory;

	private final Client jaxRsClient;

	private final ElasticPathRolloutProductService elasticPathRolloutProductService;

	private final UserPropertiesService userPropertiesService;
	/**
	 * Gets the Cortex Configurations.
	 *
	 * @return cortexConfig
	 */
	public CortexServiceContext getCortexConfig() {
		return cortexConfig;
	}

	/**
	 * Gets the resource.
	 *
	 * @return resource
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * Gets the resource resolver.
	 *
	 * @return ResourceResolver
	 */
	public ResourceResolver getResolver() {
		return resolver;
	}

	/**
	 * Get the current store scope.
	 *
	 * @return Scope of the current Store
	 */
	public String getCortexScope() {
		return cortexScope;
	}

	/**
	 * Instantiates a new ElasticPathCommerceServiceImpl.
	 *
	 * @param cortexConfig CortexServiceContext
	 * @param cortexClientFactory CortexClientFactory
	 * @param jaxRsClient JAX-RS Client
	 * @param resource Resource
	 * @param userPropertiesService service used to persist changes to user properties.
	 */
	public AbstractElasticPathCommerceService(final CortexServiceContext cortexConfig,
			final CortexClientFactory cortexClientFactory,
			final Client jaxRsClient,
			final Resource resource,
			final UserPropertiesService userPropertiesService) {

		super(cortexConfig, resource);
		this.resource = resource;
		this.cortexConfig = cortexConfig;
		this.cortexClientFactory = cortexClientFactory;
		this.jaxRsClient = jaxRsClient;
		this.cortexScope = ElasticPathCommerceUtil.findScope(resource);
		this.elasticPathRolloutProductService = getCortexConfig().getElasticPathRolloutProductService();
		this.userPropertiesService = userPropertiesService;
	}

	/**
	 * Login a user in cortex and creates a authentication token for both registered and public user and also add the ElasticPathCommerceSession in
	 * the request scope.
	 *
	 * @param request - Instance of SlingHttpServletRequest
	 * @param response - Instance of SlingHttpServletResponse
	 * @return CommerceSession - CommerceSession specific to ElasticPath
	 * @throws CommerceException commerceException
	 */
	public CommerceSession login(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws CommerceException {

		String invalidToken = (String) request.getAttribute(ElasticPathConstants.INVALID_TOKEN);
		CommerceSession session = (CommerceSession) request.getAttribute(ElasticPathConstants.REQUEST_ATTR_EP_SESSION);
		if (session != null && (invalidToken == null || !"true".equals(invalidToken))) {
			return session;
		}

		AuthService authService = new AuthServiceImpl(
				cortexConfig,
				cortexScope,
				jaxRsClient,
				cortexClientFactory,
				userPropertiesService);
		CortexContext cortexContext = authService.getCortexContext(request, response);

		CortexClient cortexClient = cortexClientFactory.create(
				cortexContext.getAuthenticationToken(),
				cortexScope);
		CortexSdkServiceFactory cortexSdkServiceFactory = getServiceFactory(cortexClient);
		session = new ElasticPathCommerceSessionImpl(this, request, response, resource, cortexSdkServiceFactory);

		request.setAttribute(ElasticPathConstants.REQUEST_ATTR_EP_SESSION, session);
		return session;
	}

	/**
	 * Pings Cortex URL to check if its available. This is used by component's JSPs to show offline page if cortex not available.
	 *
	 * @param serviceType Service.
	 * @return is Cortex Service available.
	 */
	public boolean isAvailable(final String serviceType) {
		if (CommerceConstants.SERVICE_COMMERCE.equals(serviceType)) {
			Response pingResponse = null;
			try {
				pingResponse = jaxRsClient
						.target(cortexClientFactory.getCortexURL())
						.request()
						.get();
				return pingResponse.getStatus() == EXPECTED_CORTEX_PING_STATUS;
			} catch (ProcessingException processingException) {
				LOG.debug("Error while connecting to cortex", processingException);
				LOG.warn("Exception whilst determining cortex availability, cause: " + processingException.getMessage());
				return false;
			} finally {
				JaxRsUtil.closeQuietly(pingResponse);
			}
		}

		return false;
	}

	/**
	 * Gets the product resource from the provided path.
	 *
	 * @param path String product path
	 * @return product
	 * @throws CommerceException CommerceException
	 */
	public Product getProduct(final String path) throws CommerceException {
		Resource resource = resolver.getResource(path);
		if (resource != null && ElasticPathProductImpl.isAProductOrVariant(resource)) {
			return new ElasticPathProductImpl(resource);
		}
		return null;
	}

	/**
	 * Gets the Voucher resource from the provided path.
	 *
	 * @param path String product path
	 * @return Voucher
	 * @throws CommerceException CommerceException
	 */
	@Override
	public Voucher getVoucher(final String path) throws CommerceException {
		Resource resource = resolver.getResource(path);
		if (resource != null) {
			// JCR-based vouchers are cq:Pages
			Resource contentResource = resource.getChild(JcrConstants.JCR_CONTENT);
			if (contentResource != null && contentResource.isResourceType(AbstractJcrVoucher.VOUCHER_RESOURCE_TYPE)) {
				return new AbstractJcrVoucher(resource);
			}
		}
		return null;
	}

	@Override
	public void sectionRolloutHook(final Page blueprint, final Page section) {
		// NO implementation.
	}

	/**
	 * Sets the EP specific properties for Product Rollout Hook.
	 *
	 * @param productData The productData for which the product page was created
	 * @param productPage The product page created from the section blueprint's product template
	 * @param productReference The product reference on the created/updated page
	 * @throws CommerceException CommerceException
	 */
	public void productRolloutHook(final Product productData, final Page productPage, final Product productReference) throws CommerceException {
		try {
			elasticPathRolloutProductService.setSkuCode(productData, productReference);

			setProductVariationPropertiesForGeoColorVariantAxis(productData,
					productReference);

			elasticPathRolloutProductService.setSkuCodeOnProductVariants(productData, productReference);

		} catch (final RepositoryException repositoryException) {
			throw new CommerceException("Product rollout hook failed: ", repositoryException);
		}
	}

	/**
	 * Sets the EP specific properties for Product Rollout Hook.
	 *
	 * @param productData The productData for which the product page was created
	 * @param productReference The product reference on the created/updated page
	 * @throws CommerceException CommerceException
	 */
	private void setProductVariationPropertiesForGeoColorVariantAxis(final Product productData,
			final Product productReference) throws RepositoryException {
		Node productReferenceNode = productReference.adaptTo(Node.class);
		// TODO 2014-12-02 dom: This needs to become more generic to a store,
		// depending on what global requirements are vs geometrixx
		if (productData.axisIsVariant("geo-color")) {
			productReferenceNode.setProperty(VARIATION_AXIS, "geo-color");
			// TODO 2014-12-01 dom: This needs to be localized, if we ever work
			// out how to do that
			productReferenceNode.setProperty(VARIATION_TITLE, "Color");
		}
		// TODO 2014-12-02 dom: Else for deleted variants?
	}

	/**
	 * Gets the list of countries.
	 *
	 * @return list
	 * @throws CommerceException CommerceException
	 */
	public List<String> getCountries() throws CommerceException {
		List<String> countries = new ArrayList<String>();

		countries.add("CA");
		countries.add("US");
		return countries;
	}

	/**
	 * Returns the Credit Card Types.
	 *
	 * @return creditCardType
	 * @throws CommerceException CommerceException
	 */
	public List<String> getCreditCardTypes() throws CommerceException {
		List<String> ccTypes = new ArrayList<String>();

		// A true implementation would likely need to check with its payment
		// processing
		// service to determine what credit cards to accept. This implementation
		// simply
		// accepts them all.
		ccTypes.add("*");

		return ccTypes;
	}

	/**
	 * Gets the product using sku code.
	 *
	 * @param res Resource
	 * @param skuCode String product sku code
	 * @return Product
	 * @throws CommerceException CommerceException
	 */
	public Product getProductBySkuCode(final Resource res, final String skuCode) throws CommerceException {
		try {
			Resource baseStore = ElasticPathCommerceUtil.findBaseStore(res);

			Product product = findProduct(baseStore.getPath(), skuCode);

			if (product == null) {
				ValueMap baseStoreProps = ResourceUtil.getValueMap(ElasticPathCommerceUtil.findBaseStore(res));
				String basePath = baseStoreProps.get("cq:productsPath", String.class);
				if (basePath == null) {
					basePath = "/etc/commerce/products/" + ElasticPathCommerceUtil.findBaseStoreId(res);
				}
				return findProduct(basePath, skuCode);
			}
			return product;
		} catch (final RepositoryException repositoryException) {
			throw new CommerceException("RepositoryException occurred while trying to find the product for Sku code :[ " + skuCode + " ]",
					repositoryException);
		}
	}

	private Product findProduct(final String basePath, final String productId) throws CommerceException, RepositoryException {
		Session session = resolver.adaptTo(Session.class);
		StringBuilder queryString = new StringBuilder("SELECT * FROM [nt:base] WHERE ISDESCENDANTNODE('");
		queryString.append(basePath);
		queryString.append("') AND [");
		queryString.append(PRODUCT_IDENTIFIER);
		queryString.append("] = '");
		queryString.append(productId);
		queryString.append('\'');
		Query query = session.getWorkspace().getQueryManager().createQuery(queryString.toString(), Query.JCR_SQL2);
		NodeIterator iter = query.execute().getNodes();

		if (iter.hasNext()) {
			Node node = iter.nextNode();
			if (ElasticPathCommerceUtil.isProduct(resolver.getResource(node.getPath()))) {
				return getProduct(node.getPath());
			}
		}
		return null;
	}

	/**
	 * Returns the Search provider name as ElasticPathSearch.
	 *
	 * @return string.
	 */
	@Override
	protected String getSearchProviderName() {
		return ElasticPathSearchProviderImpl.PROVIDER_NAME;
	}

	/**
	 * Return Order predicates as null.
	 *
	 * @return null
	 * @throws CommerceException CommerceException
	 */
	public List<String> getOrderPredicates() throws CommerceException {
		return null;
	}
}