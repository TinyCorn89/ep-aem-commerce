package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a cart discount.
 */
public class CartDiscount {
	@JsonProperty("discount")
	private Iterable<Discount> discounts;

	public Iterable<Discount> getDiscounts() {
		return discounts;
	}

	public void setDiscounts(final Iterable<Discount> discounts) {
		this.discounts = discounts;
	}
}
