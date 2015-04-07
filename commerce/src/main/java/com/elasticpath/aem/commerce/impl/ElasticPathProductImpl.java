/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.impl;

import static com.elasticpath.commerce.config.ElasticPathGlobalConstants.PRODUCT_IDENTIFIER;

import com.adobe.cq.commerce.common.AbstractJcrProduct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.elasticpath.aem.commerce.constants.ElasticPathConstants;

/**
 * It is the implementation of product, specific to ElasticPath.
 */
public class ElasticPathProductImpl extends AbstractJcrProduct {

	/** The Constant PN_PRICE. */
	public static final String PN_PRICE = "price";

	/**
	 * Instantiates ElasticPathProductImpl.
	 * 
	 * @param resource Resource
	 */
	public ElasticPathProductImpl(final Resource resource) {
		super(resource);
	}

	/**
	 * Gets the sku for Product.
	 * 
	 * @return string
	 */
	public String getSKU() {
		return getProperty(PRODUCT_IDENTIFIER, String.class);
	}


	/**
	 * Method to return the description.
	 * 
	 * @return description
	 */
	@Override
	public String getDescription() {
		String description = getProperty(ElasticPathConstants.DESCRIPTION, String.class);
		if (StringUtils.isEmpty(description)) {
			return StringUtils.EMPTY;
		}
		return description;
	}
}
