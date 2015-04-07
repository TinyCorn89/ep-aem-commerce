package com.elasticpath.rest.sdk.components;


import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Applied promotions on line items.
 */
public class AppliedPromotions {
	@JsonProperty("_element")
	private Iterable<Promotion> promotions = new ArrayList<Promotion>();

	public Iterable<Promotion> getPromotions() {
		return promotions;
	}

	public void setPromotions(final Iterable<Promotion> promotions) {
		this.promotions = promotions;
	}
}
