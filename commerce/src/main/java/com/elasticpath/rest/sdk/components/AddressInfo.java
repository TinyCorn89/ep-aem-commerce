package com.elasticpath.rest.sdk.components;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AddressInfo representation.
 */
public class AddressInfo extends Linkable {
	@JsonProperty("_selector")
	private List<Selector> selectors;

	public List<Selector> getSelectors() {
		return selectors;
	}
}
