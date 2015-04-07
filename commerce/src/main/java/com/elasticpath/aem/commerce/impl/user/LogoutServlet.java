/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.impl.user;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceSession;

import com.elasticpath.aem.cookies.AemCortexContextManager;
import com.elasticpath.aem.commerce.constants.ElasticPathConstants;

/**
 * Class to perform logout by clearing Login token cookie.
 */
@Component
@Service(Servlet.class)
@Properties({ @Property(name = "sling.servlet.paths", value = "/system/sling/logout"),
		@Property(name = "sling.servlet.selectors", value = "logout"), @Property(name = "sling.servlet.extensions", value = "html"),
		@Property(name = "sling.servlet.methods", value = "GET") })
public class LogoutServlet extends SlingSafeMethodsServlet {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(LogoutServlet.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 821710006677290178L;

	@Reference
	private AemCortexContextManager contextManager;

	/**
	 * doGet implementation for Logout servlet.
	 * 
	 * @param request SlingHttpServletRequest
	 * @param response SlingHttpServletResponse
	 * @throws ServletException ServletException
	 * @throws IOException IOException
	 */
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
		try {
			CommerceService commerceService = request.getResource().adaptTo(CommerceService.class);
			clearLoginToken(request, response);
			String currentPagePath = "/";
			if (request.getParameter(ElasticPathConstants.RESOURCE) != null) {
				currentPagePath = request.getParameter(ElasticPathConstants.RESOURCE);
			}
			CommerceSession commerceSession = commerceService.login(request, response);
			commerceSession.logout();
			removeCortexToken(request, response);
			response.sendRedirect(currentPagePath);
		} catch (final CommerceException commerceException) {
			LOG.error("Unable to log out from Cortex ", commerceException);
		}
	}

	private void removeCortexToken(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
		String cookieName = contextManager.getCookieName();
		Cookie cookie = request.getCookie(cookieName);
		if (cookie != null) {
			cookie = new Cookie(cookieName, null);
			cookie.setMaxAge(0);
			cookie.setPath("/");
			response.addCookie(cookie);
		}
	}

	/**
	 * Clears token for Login.
	 * 
	 * @param request SlingHttpServletRequest
	 * @param response SlingHttpServletResponse
	 */
	private void clearLoginToken(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {

		// Getting login token from cookies.
		Cookie loginToken = request.getCookie("login-token");
		if (loginToken != null) {
			// If cookie exists then remove the cookie.
			loginToken = new Cookie("login-token", null);
			loginToken.setMaxAge(0);
			loginToken.setPath(getContextPath(request));
			response.addCookie(loginToken);
		}
	}

	private String getContextPath(final SlingHttpServletRequest request) {
		String contextPath = request.getContextPath();
		if (!contextPath.endsWith("/")) {
			contextPath += "/";
		}
		return contextPath;
	}
}