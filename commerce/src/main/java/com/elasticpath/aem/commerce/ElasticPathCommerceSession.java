package com.elasticpath.aem.commerce;

import java.util.List;
import java.util.Map;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.Product;

import org.apache.sling.api.resource.Resource;

/**
 * An extension of the core {@link CommerceSession} interface to add specific ElasticPath-related functions.
 */
public interface ElasticPathCommerceSession extends CommerceSession {

	/**
	 * Returns resource.
	 * 
	 * @return Resource
	 */
	Resource getResource();

	/**
	 * Returns commerce service.
	 * 
	 * @return CommerceService
	 */
	ElasticPathCommerceService getService();

	/**
	 * Find product on the basis of product name.
	 * 
	 * @param resource Resource
	 * @param productName -Product name
	 * @return Product
	 * @throws CommerceException CommerceException
	 */
	Product getProductByName(Resource resource, String productName) throws CommerceException;
	
	/**
	 * Returns map of available regions with key as country code and value as list of regions for the country.
	 * 
	 * @return regionMap having key as country code and value as list of regions.
	 * @throws CommerceException commerceException
	 */
	Map<String, List<String>> getAvailableRegions() throws CommerceException;
}
