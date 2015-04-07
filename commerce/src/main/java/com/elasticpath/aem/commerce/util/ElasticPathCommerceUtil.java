/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.util;

import static com.elasticpath.commerce.config.ElasticPathGlobalConstants.PRODUCT_IDENTIFIER;

import java.util.Arrays;
import java.util.HashSet;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.common.PriceFilter;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import com.elasticpath.aem.commerce.constants.ElasticPathConstants;
import com.elasticpath.aem.commerce.impl.ElasticPathProductImpl;

/**
 * A class providing various utility functions.
 */
// TODO : This class is split from ElaticpathHelper class and needs to be revisited to follow static factory or builder pattern.
public final class ElasticPathCommerceUtil {
	private static final int CORTEX_PASSWORD_LENGTH = 10;

	/** The Constructor ElasticPathCommerceUtil. */
	private ElasticPathCommerceUtil() {
		// empty constructor
	}

	/**
	 * Find the base store in a page's jcr:content resource or one of the parent pages.
	 * 
	 * @param res Resource
	 * @return the base store
	 * @throws IllegalArgumentException IllegalArgumentException
	 */
	public static Resource findBaseStore(final Resource res) {
		if (res == null) {
			return null;
		}

		ValueMap props = null;
		if (res.getChild(JcrConstants.JCR_CONTENT) == null) {
			props = ResourceUtil.getValueMap(res);
		} else {
			props = ResourceUtil.getValueMap(res.getChild(JcrConstants.JCR_CONTENT));
		}
		if (props.containsKey(ElasticPathConstants.BASE_STORE)) {
			if (res.getName().equals(JcrConstants.JCR_CONTENT)) {
				return res.getParent();
			} else {
				return res;
			}

		}
		return findBaseStore(res.getParent());
	}

	/**
	 * Find the base store in a page's jcr:content resource or one of the parent pages and return its id.
	 * 
	 * @param res Resource
	 * @return id for baseStore
	 * @throws IllegalArgumentException IllegalArgumentException
	 */
	public static String findBaseStoreId(final Resource res) {
		if (res == null) {
			return null;
		}

		ValueMap props = null;
		if (res.getChild(JcrConstants.JCR_CONTENT) == null) {
			props = ResourceUtil.getValueMap(res);
		} else {
			props = ResourceUtil.getValueMap(res.getChild(JcrConstants.JCR_CONTENT));
		}
		if (props.containsKey(ElasticPathConstants.BASE_STORE)) {
			return props.get(ElasticPathConstants.BASE_STORE, String.class);
		}
		return findBaseStoreId(res.getParent());
	}

	/**
	 * Returns scope for a given site. Reference property cq:elasticpathScope.
	 * 
	 * @param res Resource
	 * @return scope associated with site
	 */
	public static String findScope(final Resource res) {
		if (res == null) {
			return null;
		}

		ValueMap props = null;
		if (res.getChild(JcrConstants.JCR_CONTENT) == null) {
			props = ResourceUtil.getValueMap(res);
		} else {
			props = ResourceUtil.getValueMap(res.getChild(JcrConstants.JCR_CONTENT));
		}

		if (props.containsKey(ElasticPathConstants.ELASTICPATH_SCOPE)) {
			return props.get(ElasticPathConstants.ELASTICPATH_SCOPE, String.class);
		}

		return findScope(res.getParent());
	}

	/**
	 * Returns true if provided resource contains cq:productSkuCode.
	 * 
	 * @param res Resource
	 * @return boolean
	 */
	public static boolean isProduct(final Resource res) {
		ValueMap props = ResourceUtil.getValueMap(res);
		if (props.containsKey(PRODUCT_IDENTIFIER)) {
			return true;
		}
		return false;
	}

	/**
	 * Generate random passwords.
	 * 
	 * @return password
	 */
	public static String generateRandomPassword() {
		return RandomStringUtils.randomAscii(CORTEX_PASSWORD_LENGTH).replace(' ', '!');
	}

	/**
	 * Gets the jcr name of a product.
	 * 
	 * @param product Product
	 * @return String
	 */
	public static String getJcrName(final Product product) {

		if (JcrUtil.isValidName(product.getSKU())) {
			return product.getSKU();
		}
		return JcrUtil.createValidName(product.getSKU());
	}

	/**
	 * Returns the product.
	 * 
	 * @param path String the product path
	 * @param resolver the Resource Resolver
	 * @return Product
	 * @throws CommerceException commerceException
	 */
	public static Product getProduct(final String path, final ResourceResolver resolver) throws CommerceException {
		Resource resource = resolver.getResource(path);
		if (resource != null && ElasticPathProductImpl.isAProductOrVariant(resource)) {
			return new ElasticPathProductImpl(resource);
		}
		return null;
	}


	/**
	 * Adds the price types.
	 * 
	 * @param priceInfo the PriceInfo
	 * @param types the types
	 * @return the price info
	 */
	public static PriceInfo addPriceTypes(final PriceInfo priceInfo, final String... types) {
		priceInfo.put(PriceFilter.PN_TYPES, new HashSet<>(Arrays.asList(types)));
		return priceInfo;
	}
}
