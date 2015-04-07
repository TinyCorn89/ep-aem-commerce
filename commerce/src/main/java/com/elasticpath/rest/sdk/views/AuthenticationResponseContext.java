/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.views;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.elasticpath.aem.commerce.cortex.CortexContext;

/**
 * ResponseContext for auth.
 */
public class AuthenticationResponseContext implements CortexContext {
	private static final int SECOND_IN_MILLI = 1000;
	@JsonProperty("token_type")
	private String tokenType;
	@JsonProperty("access_token")
	private String accessToken;
	@JsonProperty("expires_in")
	private Long expires;
	@JsonProperty("role")
	private String role;
	@JsonProperty("scope")
	private String scope;

	@Override
	public String getAuthenticationToken() {
		return tokenType + " " + accessToken;
	}

	@Override
	public String getScope() {
		return scope;
	}

	@Override
	public String getRole() {
		return role;
	}

	@Override
	public Date getExpiryDate() {
		return new Date((expires * SECOND_IN_MILLI) + new Date().getTime());
	}

	@Override
	public List<String> getRequestHeaderNames() {
		return null;
	}

	@Override
	public List<String> getRequestHeaderValues(final String headerName) {
		return null;
	}
}
