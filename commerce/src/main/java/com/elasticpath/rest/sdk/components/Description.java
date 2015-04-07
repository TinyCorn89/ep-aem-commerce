/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Selector Description.
 */
public class Description {
	@JsonProperty("display-name")
	private String displayName;
	private String carrier;
	@JsonProperty("cost")
	private Iterable<Cost> costs;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(final String carrier) {
		this.carrier = carrier;
	}

	public Iterable<Cost> getCosts() {
		return costs;
	}

	public void setCosts(final Iterable<Cost> costs) {
		this.costs = costs;
	}
}
