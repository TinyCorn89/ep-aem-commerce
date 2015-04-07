package com.elasticpath.rest.sdk.components;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Choice representation. 
 */
public class Choice extends Linkable {
	@JsonProperty("_description")
	private List<Linkable> descriptions;

	public List<Linkable> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(final List<Linkable> descriptions) {
		this.descriptions = descriptions;
	}
}
