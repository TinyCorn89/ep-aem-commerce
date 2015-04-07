package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mappings.
 */
public class Mappings {
	@JsonProperty("code")
	private String skuCode;

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}
}
