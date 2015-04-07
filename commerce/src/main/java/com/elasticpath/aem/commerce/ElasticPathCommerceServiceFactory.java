/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.commerce.api.CommerceServiceFactory;

import com.elasticpath.aem.commerce.cortex.CortexServiceContext;

/**
 * An extension of the core {@link CommerceServiceFactory} interface to add specific ElasticPath-related APIs.
 */
public interface ElasticPathCommerceServiceFactory extends CommerceServiceFactory {

	/**
	 * Returns a new <code>ElasticPathCommerceService</code>.
	 * 
	 * @param resource Resource
	 * @return ElasticPathCommerceService- Commerce service specific to Elastic path.
	 */
	ElasticPathCommerceService getCommerceService(Resource resource);

	/**
	 * Gets the ElasticPath service context.
	 * 
	 * @return CortexServiceContext
	 */
	CortexServiceContext getServiceContext();
}
