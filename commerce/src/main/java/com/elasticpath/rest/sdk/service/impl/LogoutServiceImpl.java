/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.service.impl;

import javax.ws.rs.core.Response;

import com.adobe.cq.commerce.api.CommerceException;

import com.elasticpath.rest.client.CortexClient;
import com.elasticpath.rest.client.jaxrs.JaxRsUtil;
import com.elasticpath.rest.sdk.service.LogoutService;

/**
 * Logout Service logs the user with this access token out of cortex.
 */
public class LogoutServiceImpl implements LogoutService {
	private static final String OAUTH2_TOKENS_URL = "/oauth2/tokens";

	private final String urlScope;
	private final CortexClient client;

	/**
	 * Logout Service.
	 * @param urlScope store scope.
	 * @param client http client.
	 */
	public LogoutServiceImpl(final String urlScope,
							 final CortexClient client) {
		this.urlScope = urlScope;
		this.client = client;
	}

	@Override
	public void logout() throws CommerceException {
		Response.StatusType status = client.delete(urlScope + OAUTH2_TOKENS_URL);

		if (JaxRsUtil.isNotSuccessful(status)) {
			throw new CommerceException("Logout was unsuccessful: " + JaxRsUtil.getStatusString(status));
		}
	}
}
