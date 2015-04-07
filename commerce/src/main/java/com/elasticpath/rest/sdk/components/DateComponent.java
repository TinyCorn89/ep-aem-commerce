package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Date component.
 */
public class DateComponent {

	@JsonProperty("display-value")
	private String displayValue;

	public String getDisplayValue() {
		return displayValue;
	}
}
