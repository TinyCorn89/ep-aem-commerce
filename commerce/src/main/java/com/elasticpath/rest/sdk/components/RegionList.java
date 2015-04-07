/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.components;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Region list having regions with region display name and code.
 */
public class RegionList {

	@JsonProperty("_element")
	private Iterable<Region> regions = new ArrayList<Region>();

	/**
	 * Returns Regions.  
	 * 
	 * @return the regionList
	 */
	public Iterable<Region> getRegions() {
		return regions;
	}

	/**
	 * Sets Regions.
	 * 
	 * @param regions the regionList to set
	 */
	public void setRegion(final Iterable<Region> regions) {
		this.regions = regions;
	}
}