/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.impl;

import java.util.List;
import java.util.Map;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.common.DefaultJcrCartEntry;

/**
 * ElasticPathCartEntryImpl provides functionalities related to cart like provides cart total price, product price etc.
 */
public class ElasticPathCartEntryImpl extends DefaultJcrCartEntry {

	private static final int LINE_PRICE_INDEX = 0;

	private static final String LINE_PRICE_TYPE = "LINE";

	private static final int UNIT_PRICE_INDEX = 1;

	private static final String UNIT_PRICE_TYPE = "UNIT";

	private final String lineItemUri;

	/**
	 * Instantiates a new Cart Entry and populates all the required attributes for the line item like price, line item URI etc.
	 * 
	 * @param product Product
	 * @param cartEntryindex Index of Cart Entry
	 * @param quantity Quantity
	 * @param cortexUrl Line Item URL
	 * @param priceList Line and Unit Price
	 * @param entryValues Map of cart values
	 * @throws CommerceException commerceException
	 */
	public ElasticPathCartEntryImpl(final Product product, final int cartEntryindex, final int quantity, final String cortexUrl,
			final List<PriceInfo> priceList, final Map<String, Object> entryValues) throws CommerceException {
		super(cartEntryindex, product, quantity);
		updateProperties(entryValues);
		setPrice(priceList.get(LINE_PRICE_INDEX), LINE_PRICE_TYPE);
		setPrice(priceList.get(UNIT_PRICE_INDEX), UNIT_PRICE_TYPE);
		this.lineItemUri = cortexUrl;
	}

	/**
	 * Gets the line item uri.
	 * 
	 * @return the line item uri
	 */
	public String getLineItemUri() {
		return this.lineItemUri;
	}
}
