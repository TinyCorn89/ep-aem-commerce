/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.service;

import com.adobe.cq.commerce.api.CommerceException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import com.elasticpath.aem.commerce.cortex.CortexContext;
import com.elasticpath.rest.sdk.views.AuthForm;

/**
 * Service for authenticating with cortex.
 */
public interface AuthService {

	/**
	 * Tries to get a CortexContext for a user. If there is a cookie with a valid one in we use that otherwise we connect to cortex and attempt to.
	 * get a new one. If the context in the cookie is a different role to the aem's user status we will attempt to register/login to cortex as the
	 * registered user.
	 *
	 * @param request users request
	 * @param response users request
	 * @return valid CortexContext
	 * @throws CommerceException if there is a problem logging in or registering.
	 */
	CortexContext getCortexContext(SlingHttpServletRequest request, SlingHttpServletResponse response) throws CommerceException;

	/**
	 * Creates and returns a new password to register AEM user in Cortex.
	 * @return newly created password.
	 */
	String newCortexUserPassword();

	/**
	 * Sends a login request to cortex for the given authentication form.
	 * @param authForm login form.
	 * @return CortexContext, null if an error occurs.
	 * @throws CommerceException if there is a problem logging in or registering.
	 */
	CortexContext loginToCortex(AuthForm authForm) throws CommerceException;
}
