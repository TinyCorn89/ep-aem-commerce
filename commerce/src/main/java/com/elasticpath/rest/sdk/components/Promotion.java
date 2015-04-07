package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Promotion.
 */
public class Promotion {
	@JsonProperty("display-description")
	private String displayDescription;
	@JsonProperty("display-name")
	private String displayName;
	@JsonProperty("name")
	private String name;

	public String getDisplayDescription() {
		return displayDescription;
	}

	public void setDisplayDescription(final String displayDescription) {
		this.displayDescription = displayDescription;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
