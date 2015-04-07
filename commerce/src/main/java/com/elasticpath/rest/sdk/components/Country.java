/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.components;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Details for country resource like display name, country code and associated regions.
 */
public class Country {

	@JsonProperty("display-name")
	private String countryDisplayName;

	@JsonProperty("name")
	private String countryCode;

	@JsonProperty("_regions")
	private Collection<RegionList> regionList;

	/**
	 * Returns country display name.
	 * 
	 * @return the countryDisplayName
	 */
	public String getCountryDisplayName() {
		return countryDisplayName;
	}

	/**
	 * Set country display name.
	 * 
	 * @param countryDisplayName the countryDisplayName to set
	 */
	public void setCountryDisplayName(final String countryDisplayName) {
		this.countryDisplayName = countryDisplayName;
	}

	/**
	 * Returns country code.
	 * 
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * Sets country code.
	 * 
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(final String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * Returns Region list.
	 * 
	 * @return the regionList
	 */
	public Collection<RegionList> getRegionList() {
		return regionList;
	}

	/**
	 * Sets regionsList.
	 * 
	 * @param regionList the regionList to set
	 */
	public void setRegionList(final Collection<RegionList> regionList) {
		this.regionList = regionList;
	}
}
