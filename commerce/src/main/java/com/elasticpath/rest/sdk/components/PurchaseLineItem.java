package com.elasticpath.rest.sdk.components;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Purchase Line item representation.
 */
public class PurchaseLineItem extends Linkable   {

	private String name;
	private int quantity;
	@JsonProperty("line-extension-amount")
	private Iterable<Price> total;


	public void setName(final String name) {
		this.name = name;
	}

	public void setQuantity(final int quantity) {
		this.quantity = quantity;
	}


	public void setTotal(final Iterable<Price> total) {
		this.total = total;
	}

	public String getName() {
		return name;
	}

	public int getQuantity() {
		return quantity;
	}

	public String getDisplayPrice() {
		return total.iterator().next().getDisplay();
	}

	public BigDecimal getPriceNumeric() {
		return total.iterator().next().getAmount();
	}

	public String getCurrencyCode() {
		return total.iterator().next().getCurrency();
	}
}
