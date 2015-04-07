/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.aem.cookies;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

/**
 * Builder for creating cookies to add to the http response.
 */
public class CookieBuilder {
	private String domain;
	private String path;
	private int maxage;
	private boolean secure;
	private boolean httpOnly;
	private String name;
	private String value;

	/**
	 * Set the withDomain attribute on the cookie.
	 * @param domain attribute value
	 * @return this CookieBuilder
	 */
	public CookieBuilder withDomain(final String domain) {
		this.domain = domain;
		return this;
	}

	/**
	 * Set the path attribute on the cookie.
	 * @param path attribute value
	 * @return this CookieBuilder
	 */
	public CookieBuilder withPath(final String path) {
		this.path = path;
		return this;
	}

	/**
	 * Set the maxage attribute on the cookie.
	 * @param maxAge attribute value
	 * @return this CookieBuilder
	 */
	public CookieBuilder withMaxAge(final int maxAge) {
		this.maxage = maxAge;
		return this;
	}

	/**
	 * Set the secure flag on the cookie.
	 * @param secure flag value
	 * @return this CookieBuilder
	 */
	public CookieBuilder withSecureFlag(final boolean secure) {
		this.secure = secure;
		return this;
	}

	/**
	 * Set the http only flag on the cookie.
	 * @param httpOnly flag value
	 * @return this CookieBuilder
	 */
	public CookieBuilder withHttpOnlyFlag(final boolean httpOnly) {
		this.httpOnly = httpOnly;
		return this;
	}

	/**
	 * Set the name of the cookie.
	 * @param name of cookie
	 * @return this CookieBuilder
	 */
	public CookieBuilder withCookieName(final String name) {
		this.name = name;
		return this;
	}

	/**
	 * Set the value of the cookie.
	 * @param value of the cookie
	 * @return this CookieBuilder
	 */
	public CookieBuilder withCookieValue(final String value) {
		this.value = value;
		return this;
	}

	/**
	 * Create the cookie. This method does not support rfc-6265, to use flags supported by this rfc call buildHeaderValue() and add
	 * a header directly to the response.
	 *
	 * @return cookie created with provided settings.
	 */
	public Cookie build() {
		Cookie cookie;
		if (StringUtils.isBlank(name) || StringUtils.isBlank(value)) {
			throw new IllegalStateException("Cookie must have a name and a value");
		} else {
			cookie = new Cookie(name, value);
		}

		if (StringUtils.isNotBlank(domain)) {
			cookie.setDomain(domain);
		}

		if (StringUtils.isNotBlank(path)) {
			cookie.setPath(path);
		}

		if (maxage > 0) {
			cookie.setMaxAge(maxage);
		}

		cookie.setSecure(secure);

		return cookie;
	}

	/**
	 * Create cookie directly as a header where cookie attributes are not supported by the servlet cookie classes.
	 *
	 * @return header string for new cookie.
	 */
	public String buildHeaderValue() {
		Cookie cookie = this.build();
		StringBuilder header = new StringBuilder()
				.append(cookie.getName())
				.append('=')
				.append(cookie.getValue());

		if (StringUtils.isNotBlank(cookie.getPath())) {
			header.append("; Path=")
					.append(cookie.getPath());
		}

		if (StringUtils.isNotBlank(cookie.getDomain())) {
			header.append("; Domain=")
					.append(cookie.getDomain());
		}

		if (maxage > 0) {
			header.append("; Max-Age=")
				.append(cookie.getMaxAge());
		}

		if (this.secure) {
			header.append("; Secure");
		}

		if (this.httpOnly) {
			header.append("; HttpOnly");
		}

		return header.toString();
	}
}
