/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * It contains region details like region display name and region code.
 */
public class Region {

	@JsonProperty("display-name")
	private String regionDisplayName;

	@JsonProperty("name")
	private String regionCode;

	/**
	 * Return region code.
	 * 
	 * @return the regionCode
	 */
	public String getRegionCode() {
		return regionCode;
	}

	/**
	 * Sets region code.
	 *  
	 * @param regionCode the regionCode to set
	 */
	public void setRegionCode(final String regionCode) {
		this.regionCode = regionCode;
	}

	/**
	 * Returns region display name.
	 * 
	 * @return the regionDisplayName
	 */
	public String getRegionDisplayName() {
		return regionDisplayName;
	}

	/**
	 * Sets region display name.
	 * 
	 * @param regionDisplayName the regionDisplayName to set
	 */
	public void setRegionDisplayName(final String regionDisplayName) {
		this.regionDisplayName = regionDisplayName;
	}
}