package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Item.
 */
public class Item {
	@JsonProperty("_price")
	private Iterable<ItemPrice> prices;
	@JsonProperty("_code")
	private Iterable<Mappings> mappings;

	public Iterable<ItemPrice> getPrices() {
		return prices;
	}

	public void setPrices(final Iterable<ItemPrice> prices) {
		this.prices = prices;
	}

	public Iterable<Mappings> getMappings() {
		return mappings;
	}

	public void setMappings(final Iterable<Mappings> mappings) {
		this.mappings = mappings;
	}
}
