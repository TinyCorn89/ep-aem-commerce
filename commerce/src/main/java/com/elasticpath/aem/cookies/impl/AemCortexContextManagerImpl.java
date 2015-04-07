/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.aem.cookies.impl;

import java.util.Date;
import java.util.Dictionary;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.adobe.granite.security.user.UserProperties;
import com.adobe.granite.security.user.UserPropertiesManager;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.personalization.UserPropertiesUtil;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.aem.cookies.AemCortexContext;
import com.elasticpath.aem.cookies.AemCortexContextManager;
import com.elasticpath.aem.cookies.AemCortexCookieTransformer;
import com.elasticpath.aem.cookies.CookieBuilder;
import com.elasticpath.aem.commerce.cortex.CortexContext;

/**
 * Cortex Cookie Manager Implementation.
 */
@Component(metatype = true,
		label = "Elastic Path Cortex Context Manager",
		description = "Provides persistence of the Cortex Context using a browser cookie."
)
@Service(AemCortexContextManager.class)
@Properties({
		@Property(name = CookieConfig.COOKIE_NAME_PROPERTY_NAME,
				value = CookieConfig.COOKIE_NAME_DEFAULT_VALUE,
				label = CookieConfig.COOKIE_NAME_LABEL,
				description = CookieConfig.COOKIE_NAME_DESCRIPTION
		),
		@Property(name = CookieConfig.COOKIE_DOMAIN_PROPERTY_NAME,
				value = CookieConfig.COOKIE_DOMAIN_DEFAULT_VALUE,
				label = CookieConfig.COOKIE_DOMAIN_LABEL,
				description = CookieConfig.COOKIE_DOMAIN_DESCRIPTION
		),
		@Property(name = CookieConfig.COOKIE_PATH_PROPERTY_NAME,
				value = CookieConfig.COOKIE_PATH_DEFAULT_VALUE,
				label = CookieConfig.COOKIE_PATH_LABEL,
				description = CookieConfig.COOKIE_PATH_DESCRIPTION
		),
		@Property(name = CookieConfig.COOKIE_MAXAGE_PROPERTY_NAME,
				intValue = CookieConfig.COOKIE_MAXAGE_DEFAULT_VALUE,
				label = CookieConfig.COOKIE_MAXAGE_LABEL,
				description = CookieConfig.COOKIE_MAXAGE_DESCRIPTION
		),
		@Property(name = CookieConfig.COOKIE_SECURE_FLAG_PROPERTY_NAME,
				boolValue = CookieConfig.COOKIE_SECURE_FLAG_DEFAULT_VALUE,
				label = CookieConfig.COOKIE_SECURE_FLAG_LABEL,
				description = CookieConfig.COOKIE_SECURE_FLAG_DESCRIPTION
		),
		@Property(name = CookieConfig.COOKIE_HTTPONLY_FLAG_PROPERTY_NAME,
				boolValue = CookieConfig.COOKIE_HTTPONLY_FLAG_DEFAULT_VALUE,
				label = CookieConfig.COOKIE_HTTPONLY_FLAG_LABEL,
				description = CookieConfig.COOKIE_HTTPONLY_FLAG_DESCRIPTION
		)
})
public class AemCortexContextManagerImpl implements AemCortexContextManager {
	private static final Logger LOG = LoggerFactory.getLogger(AemCortexContextManagerImpl.class);
	private static final String EP_IDENTIFIER = "ep-identifier";
	private static final String PUBLIC_ROLE = "PUBLIC";
	private static final String REGISTERED_ROLE = "REGISTERED";
	private static final String IGNORE_ANON_USERS_PROPERTY = "cq:ignoreAnonUsers";
	private static final String CORTEX_CONTEXT = "cortexContext";
	private static final String CORTEX_TOKEN = "cortexToken";

	@Reference
	private CryptoSupport cryptoSupport;

	private AemCortexCookieTransformer valueProcessor;
	private String cookieName;
	private String cookieDomain;
	private String cookiePath;
	private int cookieMaxAge;
	private boolean cookieSecureFlag;
	private boolean cookieHttpOnlyFlag;

	/**
	 * OSGI activate method.
	 *
	 * @param componentContext  component properties
	 */
	@Activate
	public void activate(final ComponentContext componentContext) {
		Dictionary<?, ?> properties = componentContext.getProperties();
		LOG.info("activate({})", properties);
		setProperties(properties);
	}

	/**
	 * OSGI Config Admin sends modified message here.
	 *
	 * @param componentContext component properties
	 */
	@Modified
	public void modified(final ComponentContext componentContext) {
		Dictionary<?, ?> properties = componentContext.getProperties();
		LOG.info("modified({})", properties);
		setProperties(properties);
	}

	private void setProperties(final Dictionary<?, ?> properties) {
		cookieName = PropertiesUtil.toString(properties.get(CookieConfig.COOKIE_NAME_PROPERTY_NAME), CookieConfig.COOKIE_NAME_DEFAULT_VALUE);
		cookieDomain = PropertiesUtil.toString(properties.get(CookieConfig.COOKIE_DOMAIN_PROPERTY_NAME), CookieConfig.COOKIE_DOMAIN_DEFAULT_VALUE);
		cookiePath = PropertiesUtil.toString(properties.get(CookieConfig.COOKIE_PATH_PROPERTY_NAME), CookieConfig.COOKIE_PATH_DEFAULT_VALUE);
		cookieMaxAge = PropertiesUtil.toInteger(properties.get(CookieConfig.COOKIE_MAXAGE_PROPERTY_NAME), CookieConfig.COOKIE_MAXAGE_DEFAULT_VALUE);
		cookieSecureFlag = PropertiesUtil.toBoolean(properties.get(CookieConfig.COOKIE_SECURE_FLAG_PROPERTY_NAME),
				CookieConfig.COOKIE_SECURE_FLAG_DEFAULT_VALUE);
		cookieHttpOnlyFlag = PropertiesUtil.toBoolean(properties.get(CookieConfig.COOKIE_HTTPONLY_FLAG_PROPERTY_NAME),
				CookieConfig.COOKIE_HTTPONLY_FLAG_DEFAULT_VALUE);

		valueProcessor = new AemCortexCookieTransformerImpl(cryptoSupport);
	}

	@Override
	public CortexContext retrieveContext(final HttpServletRequest request) {
		if (request.getAttribute(CORTEX_CONTEXT) != null) {
			return (CortexContext) request.getAttribute(CORTEX_CONTEXT);
		}

		final Cookie authCookie = findCookie(request, cookieName);

		if (authCookie != null) {
			AemCortexContext context = valueProcessor.deserializeValue(authCookie.getValue());

			if (context != null && isValidForRequest(request, context)) {
				request.setAttribute(CORTEX_CONTEXT, context);
				return context;
			}
		}

		return null;
	}

	@Override
	public CortexContext retrieveContext(final ResourceResolver resourceResolver, final String userId) {
		final String authCookieValue = (String) resourceResolver.getAttribute(CORTEX_TOKEN);

		if (authCookieValue != null) {
			AemCortexContext context = valueProcessor.deserializeValue(authCookieValue);

			if (context != null && isValidForResource(resourceResolver, context, userId)) {
				return context;
			}
		}
		return null;
	}

	private boolean isValidForRequest(final HttpServletRequest request, final AemCortexContext context) {
		boolean result = false;

		final Date expiryDate = context.getExpiryDate();
		final Date now = new Date();

		if (now.after(expiryDate)) {
			LOG.debug("Token has expired, expired at: {}", expiryDate);
		} else {
			if (request instanceof SlingHttpServletRequest) {
				UserProperties userProperties = ((SlingHttpServletRequest) request).adaptTo(UserProperties.class);
				final boolean ignoreAnonUsers = ignoreAnonUsers((SlingHttpServletRequest) request);

				result = isContextValidForUser(context, userProperties, ignoreAnonUsers);
			}
		}

		return result;
	}

	private boolean isValidForResource(final ResourceResolver resourceResolver, final AemCortexContext context, final String userId) {
		boolean result = false;

		final Date expiryDate = context.getExpiryDate();
		final Date now = new Date();

		if (now.after(expiryDate)) {
			LOG.debug("Token has expired, expired at: {}", expiryDate);
		} else {
			final UserPropertiesManager userPropertiesManager = resourceResolver.adaptTo(UserPropertiesManager.class);
			UserProperties userProperties = null;
			try {
				userProperties = userPropertiesManager.getUserProperties(userId, "profile");
			} catch (final RepositoryException repositoryException) {
				LOG.error("Error while fetching userProperties : {}", repositoryException.getMessage());
			}
			result = isContextValidForUser(context, userProperties, false);
		}

		return result;
	}

	private boolean isContextValidForUser(final AemCortexContext context, final UserProperties userProperties, final boolean ignoreAnonUsers) {
		if (userProperties == null) {
			return false;
		}
		boolean result;
		final boolean isAnonymous = UserPropertiesUtil.isAnonymous(userProperties);
		final String role = context.getRole();

		// If anonymous make sure the Cortex role is PUBLIC unless we've been asked not to enforce this
		if (isAnonymous) {
			if (ignoreAnonUsers) {
				LOG.trace("Ignoring Cortex roles for anonymous users, returning context valid (Cortex role: {}).", role);
				result = true;
			} else {
				result = PUBLIC_ROLE.equals(role);
				LOG.debug("Checking current Cortex role '{}' for anonymous user is public; result: {}.", role, result);
			}
		} else {
			final String aemUserId = userProperties.getAuthorizableID();

			// If registered then make sure the identifier in the cookie (server) matches the identifier
			// on the server in the JCR for the current user logged into AEM
			if (REGISTERED_ROLE.equals(role)) {
				final String currentIdentifierOnServer = UserPropertiesUtil.getValue(userProperties, EP_IDENTIFIER);
				final String currentIdentifierOnClient = context.getIdentifier();

				result = currentIdentifierOnServer.equals(currentIdentifierOnClient);

				LOG.debug("Checking current AEM identifier for user '{}' matches the one in the current Cortex context; result: {}."
								+ " (AEM identifier: '{}', Cortex cortex identifier: '{}')",
						new Object[] { aemUserId, result, currentIdentifierOnServer, currentIdentifierOnClient });
			} else {
				LOG.debug("Current AEM user '{}' currently has a public Cortex role so returning valid as will be logged in later in the"
						+ " request.", aemUserId);
				result = true;
			}
		}
		return result;
	}

	private Cookie findCookie(final HttpServletRequest request, final String cookieName) {
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(cookieName)) {
					return cookie;
				}
			}
		}

		LOG.debug("None of the cookies on the request matched cortex auth cookie named: {}", cookieName);
		return null;
	}

	@Override
	public void persistContext(final HttpServletRequest request, final HttpServletResponse response, final CortexContext context) {
		CookieBuilder cookieBuilder = new CookieBuilder();
		cookieBuilder.withCookieName(cookieName);
		cookieBuilder.withDomain(cookieDomain);
		cookieBuilder.withPath(cookiePath);
		cookieBuilder.withMaxAge(cookieMaxAge);
		cookieBuilder.withHttpOnlyFlag(cookieHttpOnlyFlag);
		cookieBuilder.withSecureFlag(cookieSecureFlag);

		AemCortexContext aemCortexContext = insertUserHash(request, context);

		try {
			cookieBuilder.withCookieValue(valueProcessor.serializeValue(aemCortexContext));
		} catch (CryptoException e) {
			LOG.error("Unable to serialize cookie value, not persisting context.");
			LOG.debug("Error while attempting to encrypt cookie value", e);
			return;
		}

		if (cookieHttpOnlyFlag) {
			String rawOutput = cookieBuilder.buildHeaderValue();
			LOG.debug("Writing the cookie to the response as a raw output: {}", rawOutput);
			response.setHeader("SET-COOKIE", rawOutput);
		} else {
			Cookie cookie = cookieBuilder.build();
			LOG.debug("Writing the cookie to the response: {}", cookie);
			response.addCookie(cookie);
		}

		request.setAttribute(CORTEX_CONTEXT, aemCortexContext);
	}

	private AemCortexContext insertUserHash(final HttpServletRequest request, final CortexContext context) {
		if (request instanceof SlingHttpServletRequest) {
			UserProperties userProperties = ((SlingHttpServletRequest) request).adaptTo(UserProperties.class);

			if (!UserPropertiesUtil.isAnonymous(userProperties)) {
				String identifier = getOrCreateIdentifier(userProperties);

				return new AemCortexContext.ContextBuilder()
						.withAuthenticationToken(context.getAuthenticationToken())
						.withScope(context.getScope())
						.withRole(context.getRole())
						.withExpiryDate(context.getExpiryDate())
						.withIdentifier(identifier)
						.withVersion(AemCortexCookieTransformerImpl.CURRENT_VERSION)
						.build();
			}
		}

		return  new AemCortexContext.ContextBuilder()
				.withAuthenticationToken(context.getAuthenticationToken())
				.withScope(context.getScope())
				.withRole(context.getRole())
				.withExpiryDate(context.getExpiryDate())
				.withIdentifier("none")
				.withVersion(AemCortexCookieTransformerImpl.CURRENT_VERSION)
				.build();
	}

	private String getOrCreateIdentifier(final UserProperties userProperties) {
		String identifier = UserPropertiesUtil.getValue(userProperties, EP_IDENTIFIER);
		if (identifier == null) {
			identifier = UUID.randomUUID().toString();
			Node node = userProperties.getNode();

			try {
				node.setProperty(EP_IDENTIFIER, identifier);
				node.getSession().save();
			} catch (RepositoryException e) {
				LOG.warn("Unable to set ep-identifier on node");
			}
		}
		return identifier;
	}

	private boolean ignoreAnonUsers(final SlingHttpServletRequest request) {
		String enforceAnonProperty = findProperty(request.getResource(), IGNORE_ANON_USERS_PROPERTY);

		return BooleanUtils.toBoolean(enforceAnonProperty);
	}

	//FIXME this should be in the ephelper class, needs to be moved from aem commerce
	/**
	 * Looks for a property on the resource recursively.
	 *
	 * @param res Resource
	 * @param key The property to look for
	 * @return scope associated with site
	 */
	public static String findProperty(final Resource res, final String key) {
		if (res == null) {
			return null;
		}

		ValueMap props = null;
		if (res.getChild(JcrConstants.JCR_CONTENT) == null) {
			props = ResourceUtil.getValueMap(res);
		} else {
			props = ResourceUtil.getValueMap(res.getChild(JcrConstants.JCR_CONTENT));
		}

		if (props.containsKey(key)) {
			return props.get(key, String.class);
		}

		return findProperty(res.getParent(), key);
	}

	@Override
	public String getCookieName() {
		return cookieName;
	}
}
