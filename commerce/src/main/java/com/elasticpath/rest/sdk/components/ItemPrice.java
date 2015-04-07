package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Item price.
 */
public class ItemPrice {
	@JsonProperty("list-price")
	private Iterable<Price> listPrice;

	@JsonProperty("purchase-price")
	private Iterable<Price> purchasePrice;

	public Iterable<Price> getListPrice() {
		return listPrice;
	}

	public void setListPrice(final Iterable<Price> listPrice) {
		this.listPrice = listPrice;
	}

	public Iterable<Price> getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(final Iterable<Price> purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
}
