/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.service;

import com.adobe.cq.commerce.api.CommerceException;

/**
 * Logout Service.
 */
public interface LogoutService {

	/**
	 * Revoke access for the current access token in cortex.
	 * @throws CommerceException if an error occurs while revoking token.
	 */
	void logout() throws CommerceException;
}
