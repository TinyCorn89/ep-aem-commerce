/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.components;

import java.util.List;

import com.adobe.cq.commerce.api.ShippingMethod;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.elasticpath.aem.commerce.impl.ElasticPathShippingMethodImpl;

/**
 * Shipping Option from Selector.
 */
public class SelectorShippingOption extends Linkable {
	@JsonProperty("_description")
	private Description description;

	public Description getDescription() {
		return description;
	}

	public void setDescription(final List<Description> description) {
		this.description = description.get(0);
	}

	/**
	 * Adapt ShippingOption to an AEM ShippingMethod.
	 * @return ShippingMethod instance.
	 */
	public ShippingMethod adaptToShippingMethod() {
		return new ElasticPathShippingMethodImpl(
				getSelf().getUri(),
				description.getCarrier(),
				description.getDisplayName(),
				description.getCosts().iterator().next().getAmount());
	}
}
