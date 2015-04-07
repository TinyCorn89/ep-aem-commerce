/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.views;

import javax.ws.rs.core.Form;

/**
 * Auth post body form.
 */
public class AuthForm extends Form {
	private final Form form;
	private String authenticationToken;
	private boolean hasAuth;

	/**
	 * Create default authForm.
	 */
	public AuthForm() {
		form = new Form();
	}

	/**
	 * set username on form.
	 * @param username username.
	 */
	public void setUsername(final String username) {
		form.param("username", username);
	}

	/**
	 * set password on form.
	 * @param password password.
	 */
	public void setPassword(final String password) {
		form.param("password", password);
	}

	/**
	 * set scope on form.
	 * @param scope scope.
	 */
	public void setScope(final String scope) {
		form.param("scope", scope);
	}

	/**
	 * set role on form.
	 * @param role role.
	 */
	public void setRole(final String role) {
		form.param("role", role);
	}

	/**
	 * set grant_type on form.
	 * @param grantType grant_type.
	 */
	public void setGrantType(final String grantType) {
		form.param("grant_type", grantType);
	}

	/**
	 * setToken.
	 * @param authToken token.
	 */
	public void setAuthenticationToken(final String authToken) {
		this.authenticationToken = authToken;
		this.hasAuth = true;
	}

	/**
	 * Check if we need to set header on the request.
	 * @return boolean.
	 */
	public boolean hasAuthHeader() {
		return hasAuth;
	}

	/**
	 * getToken.
	 * @return token.
	 */
	public String getAuthenticationToken() {
		return authenticationToken;
	}

	/**
	 * return as superclass for jax-rs processing.
	 * @return this form.
	 */
	public Form asForm() {
		return form;
	}
}

