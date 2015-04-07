/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.aem.commerce.cortex;

import java.util.Date;

import java.util.Collections;
import java.util.List;

/**
 * Abstract helper class for constructing CortexContext objects.
 */
public abstract class AbstractCortexContext implements CortexContext {

	private static final long serialVersionUID = 20120621L;

	private final String authToken;
	private final String scope;
	private final String role;
	private final Date expires;

	/**
	 * Default Constructor.
	 * @param authToken auth token.
	 * @param scope scope of token.
	 * @param role users role.
	 * @param expiresIn expires.
	 */
	protected AbstractCortexContext(final String authToken, final String scope, final String role, final Date expiresIn) {
		this.authToken = authToken;
		this.scope = scope;
		this.role = role;
		this.expires = expiresIn;
	}

	@Override
	public String getAuthenticationToken() {
		return authToken;
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
		return new Date(expires.getTime());
	}

	@Override
	public List<String> getRequestHeaderNames() {
		return Collections.emptyList();
	}

	@Override
	public List<String> getRequestHeaderValues(final String headerName) {
		return Collections.emptyList();
	}
}

