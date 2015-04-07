/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.views;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Register new User form.
 */
public class RegistrationForm {
	@JsonProperty("family-name")
	private String familyName;
	@JsonProperty("given-name")
	private String givenName;
	private String username;
	private String password;

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

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}
}

