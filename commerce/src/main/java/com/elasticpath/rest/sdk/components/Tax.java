package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Order Tax.
 */
public class Tax {
	@JsonProperty("cost")
	private Iterable<Cost> costs;
	@JsonProperty("total")
	private Cost total;

	public Iterable<Cost> getCosts() {
		return costs;
	}

	public void setCosts(final Iterable<Cost> costs) {
		this.costs = costs;
	}
	
	public Cost getTotal() {
		return total;
	}

	public void setTotal(final Cost total) {
		this.total = total;
	}
}
