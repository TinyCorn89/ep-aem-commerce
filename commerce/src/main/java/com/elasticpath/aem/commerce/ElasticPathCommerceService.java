/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.aem.commerce;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.Product;
import com.elasticpath.aem.commerce.cortex.CortexServiceContext;
import com.elasticpath.rest.client.CortexClient;
import com.elasticpath.rest.sdk.CortexSdkServiceFactory;

/**
 * An extension of the core {@link CommerceService} interface to add specific ElasticPath-related functions.
 */
public interface ElasticPathCommerceService extends CommerceService {
	/**
	 * Get the Service Factory, the implementation will depend on the commerce provider being used.
	 *
	 * @param cortexClient cortex client.
	 * @return service factory.
	 */
	CortexSdkServiceFactory getServiceFactory(CortexClient cortexClient);

	/**
	 * Gets the product using sku code.
	 * 
	 * @param resource Resource
	 * @param skuCode String- SkuCode for which product needs to be loaded.
	 * @return Product
	 * @throws CommerceException CommerceException
	 */
	Product getProductBySkuCode(Resource resource, String skuCode) throws CommerceException;

	/**
	 * Returns the resource.
	 * 
	 * @return resource
	 */
	Resource getResource();

	/**
	 * Gets the Cortex Config.
	 * 
	 * @return CortexServiceContext: Cortex configuration which contains all the services required for Cortex access.
	 */
	CortexServiceContext getCortexConfig();

	/**
	 * Get the current store scope.
	 * 
	 * @return Scope of the current Store
	 */
	String getCortexScope();
}