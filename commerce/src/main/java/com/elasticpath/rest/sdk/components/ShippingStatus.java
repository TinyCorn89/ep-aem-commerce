package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Shipping Status.
 */
public class ShippingStatus {

	@JsonProperty("code")
	private String code;

	public String getCode() {
		return code;
	}
}
