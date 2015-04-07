/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.aem.cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.resource.ResourceResolver;

import com.elasticpath.aem.commerce.cortex.CortexContext;

/**
 * Used for managing cookies, will return a context for a given request and will also encode a cookie onto the
 * request for a given context.
 */
public interface AemCortexContextManager {

	/**
	 * Create a cortex context from the request. If a context cannot be created or the context is invalid this method will return null.
	 * It can be assumed that if this method returns a CortexContext it is valid and usable and should require no further validation
	 *
	 * @param request user request to extract context from.
	 * @return returns a CortexContext if one can be created and it is valid for the current request.
	 */
	CortexContext retrieveContext(HttpServletRequest request);

	/**
	 * Create a cortex context from the resourceResolver. If a context cannot be created from resourceResolver this method will return null.
	 * It can be assumed that if this method returns a CortexContext it is valid and usable and should require no further validation
	 *
	 * @param resourceResolver ResourceResolver to extract context from.
	 * @param userId user id to get userproperties.
	 * @return returns a CortexContext if one can be created for the resourceResolver.
	 */
	CortexContext retrieveContext(ResourceResolver resourceResolver, String userId);
	
	/**
	 * Persists a CortexContext onto the users response.
	 *
	 * @param request user request to extract context from.
	 * @param response users response
	 * @param context users context
	 */
	void persistContext(HttpServletRequest request, HttpServletResponse response, CortexContext context);
	
	/**
	 * Returns the cortex context cookie name configured in console.
	 *
	 * @return cookie name String
	 */
	String getCookieName();
}
