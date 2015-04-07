/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.cookies.impl;

/**
 * Attributes of osgi properties for cookie configuration.
 */
@SuppressWarnings("checkstyle:javadocvariable")
public final class CookieConfig {
	public static final String COOKIE_NAME_PROPERTY_NAME = "name";
	public static final String COOKIE_NAME_DEFAULT_VALUE = "cortexSession";
	public static final String COOKIE_NAME_LABEL = "Cookie Name";
	public static final String COOKIE_NAME_DESCRIPTION = "Name of the cookie to store the cortex context.";

	public static final String COOKIE_PATH_PROPERTY_NAME = "path";
	public static final String COOKIE_PATH_DEFAULT_VALUE = "/";
	public static final String COOKIE_PATH_LABEL = "Cookie Path";
	public static final String COOKIE_PATH_DESCRIPTION = "Path for the cookies.";

	public static final String COOKIE_DOMAIN_PROPERTY_NAME = "withDomain";
	public static final String COOKIE_DOMAIN_DEFAULT_VALUE = "";
	public static final String COOKIE_DOMAIN_LABEL = "Cookie Domain";
	public static final String COOKIE_DOMAIN_DESCRIPTION = "Domain name for the cookies.";

	public static final String COOKIE_MAXAGE_PROPERTY_NAME = "maxage";
	public static final int COOKIE_MAXAGE_DEFAULT_VALUE = 2592000;
	public static final String COOKIE_MAXAGE_LABEL = "Max Age";
	public static final String COOKIE_MAXAGE_DESCRIPTION = "Cookie max age in seconds.";

	public static final String COOKIE_SECURE_FLAG_PROPERTY_NAME = "secureFlag";
	public static final boolean COOKIE_SECURE_FLAG_DEFAULT_VALUE = false;
	public static final String COOKIE_SECURE_FLAG_LABEL = "Secure Cookie";
	public static final String COOKIE_SECURE_FLAG_DESCRIPTION = "Set the Secure cookie flag, this means the cookie is only available via https";

	public static final String COOKIE_HTTPONLY_FLAG_PROPERTY_NAME = "httpOnly";
	public static final boolean COOKIE_HTTPONLY_FLAG_DEFAULT_VALUE = false;
	public static final String COOKIE_HTTPONLY_FLAG_LABEL = "Http Only Cookie";
	public static final String COOKIE_HTTPONLY_FLAG_DESCRIPTION = "Set the Http Only flag, which restricts cookies visibility to http requests.";
}
