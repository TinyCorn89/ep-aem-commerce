/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.authentication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.apache.sling.auth.core.spi.AuthenticationInfoPostProcessor;

import com.elasticpath.aem.commerce.constants.ElasticPathConstants;
import com.elasticpath.aem.cookies.AemCortexContextManager;

/**
 * ElasticPathAuthenticationInfoPostProcessor having post processor logic for storing Cortex token and request URI in AuthenticationInfo object.
 */
@Service(AuthenticationInfoPostProcessor.class)
@Component
public class ElasticPathAuthenticationInfoPostProcessor implements AuthenticationInfoPostProcessor {

	@Reference
	private AemCortexContextManager cookieManager;
	
	/**
	 * ElasticPath Post processor after sling authentication to populate cortex token and request URI in AuthenticationInfo.
	 * 
	 * @param authenticationInfo AuthenticationInfo
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws LoginException LoginException
	 */
	public void postProcess(final AuthenticationInfo authenticationInfo, final HttpServletRequest request, final HttpServletResponse response)
			throws LoginException {

		final Cookie cookie = findCookie(request, cookieManager.getCookieName());
		if (cookie == null) {
			return;
		}
		authenticationInfo.put(ElasticPathConstants.CORTEX_TOKEN, cookie.getValue());
		authenticationInfo.put(ElasticPathConstants.REQUEST_URI, request.getRequestURI());
	}

	private Cookie findCookie(final HttpServletRequest request, final String cookieName) {
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookieName.equals(cookie.getName())) {
					return cookie;
				}
			}
		}
		return null;
	}
}