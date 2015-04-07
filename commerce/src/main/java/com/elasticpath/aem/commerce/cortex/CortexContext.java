/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.aem.commerce.cortex;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Contains context info for a cortex "session". Builders used where a new instance is required.
 */
public interface CortexContext extends Serializable {
	/**
	 * Returns the cortex authentication token for this session.
	 *
	 * @return the token
	 */
	String getAuthenticationToken();

	/**
	 * Returns the cortex scope for this authentication token e.g. mobee
	 *
	 * @return String scope of store
	 */
	String getScope();

	/**
	 * The role associated with this token eg. PUBLIC or REGISTERED
	 *
	 * @return String role
	 */
	String getRole();

	/**
	 * Timestamp when token expires.
	 *
	 * @return String expires
	 */
	Date getExpiryDate();

	/**
	 * Returns a list of header names to add to the request.
	 *
	 * @return list of headerNames, never <code>null</code>.
	 */
	List<String> getRequestHeaderNames();

	/**
	 * Get all header values for a given header name.
	 * This method should only be called with a header present in {@link #getRequestHeaderNames()}.
	 * If called with a name not present in that list, then this may return either <code>null</code> or an empty list.
	 * If present in the list, this method must never return null.
	 *
	 * @param headerName name of header, must exist in {@link #getRequestHeaderNames()}.
	 * @return list of values for the given header name.
	 */
	List<String> getRequestHeaderValues(String headerName);
}
