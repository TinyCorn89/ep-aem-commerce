/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import com.adobe.cq.commerce.api.ShippingMethod;
import com.google.common.collect.Maps;

import com.elasticpath.aem.commerce.constants.ElasticPathConstants;

/**
 * It is the implementation of commerce ShippingMethod, specific to ElasticPath.
 */
public class ElasticPathShippingMethodImpl implements ShippingMethod {

	/** The values. */
	private final Map<String, Object> values;
	private final String path;
	private final String title;
	private final String description;
	private final BigDecimal amount;

	/**
	 * Instantiates ElasticPathShippingMethodImpl.
	 * @param path shipping method path.
	 * @param title shipping method title.
	 * @param description shipping method description.
	 * @param amount shipping method amount
	 */
	public ElasticPathShippingMethodImpl(final String path, final String title, final String description, final BigDecimal amount) {
		this.values = Maps.newHashMap();
		this.path = path;
		this.title = title;
		this.description = description;
		this.amount = amount.setScale(2, RoundingMode.CEILING);
		this.values.put(ElasticPathConstants.AMOUNT, this.amount); //add non api data to value map
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getPredicate() {
		return null;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getImageUrl(final String paramString) {
		return null;
	}

	@Override
	public String getUIPath() {
		return null;
	}

	@Override
	public String getRedirectUrl() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(final String name, final Class<T> type) {
		Object value = values.get(name);

		if (value.getClass().equals(type)) {
			return (T) value;
		} else {
			return null;
		}
	}

	public BigDecimal getAmount() {
		return amount;
	}
}
