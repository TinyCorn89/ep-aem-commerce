package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Cart Total.
 */
public class Total {
	@JsonProperty("cost")
	private Iterable<Cost> costs;

	public Iterable<Cost> getCosts() {
		return costs;
	}

	public void setCosts(final Iterable<Cost> costs) {
		this.costs = costs;
	}
}
