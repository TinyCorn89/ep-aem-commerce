package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Name.
 */
public class Name {
	@JsonProperty("family-name")
	private String familyName;
	@JsonProperty("given-name")
	private String givenName;

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(final String familyName) {
		this.familyName = familyName;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(final String givenName) {
		this.givenName = givenName;
	}
}
