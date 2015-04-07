package com.elasticpath.rest.sdk.components;

import java.math.BigDecimal;

/**
 * Discount.
 */
public class Discount {
	private BigDecimal amount;
	private String currency;
	private String display;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(final BigDecimal amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(final String currency) {
		this.currency = currency;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(final String display) {
		this.display = display;
	}
}
