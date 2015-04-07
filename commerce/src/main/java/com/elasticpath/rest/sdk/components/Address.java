package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Address.
 */
public class Address {
	@JsonProperty("street-address")
	private String streetAddress;
	@JsonProperty("country-name")
	private String countryName;
	@JsonProperty("extended-address")
	private String extendedAddress;
	@JsonProperty("locality")
	private String locality;
	@JsonProperty("region")
	private String region;
	@JsonProperty("postal-code")
	private String postalCode;

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(final String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(final String countryName) {
		this.countryName = countryName;
	}

	public String getExtendedAddress() {
		return extendedAddress;
	}

	public void setExtendedAddress(final String extendedAddress) {
		this.extendedAddress = extendedAddress;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(final String locality) {
		this.locality = locality;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(final String region) {
		this.region = region;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(final String postalCode) {
		this.postalCode = postalCode;
	}
}
