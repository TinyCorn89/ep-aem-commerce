/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.aem.cookies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.aem.cookies.impl.CookieConfig;

public class CookieBuilderTest {

	private static final String TEST_COOKIE_NAME = "test-cookie-name";
	private static final String TEST_COOKIE_VALUE = "test-cookie-value";
	private CookieBuilder builder;

	@Before
	public void init() {
		builder = new CookieBuilder();
	}

 	@Test
	public void testBuildMinimumCookie() {
		builder.withCookieName(TEST_COOKIE_NAME);
		builder.withCookieValue(TEST_COOKIE_VALUE);

		Cookie result = builder.build();

		assertEquals("Cookie Name does not match", TEST_COOKIE_NAME, result.getName());
		assertEquals("Cookie Value does not match", TEST_COOKIE_VALUE, result.getValue());
	}

	@Test
	public void testBuildMinimumCookieHeader() {
		String cookieHeader = TEST_COOKIE_NAME + "=" + TEST_COOKIE_VALUE; //Only required parts RFC 2109
		builder.withCookieName(TEST_COOKIE_NAME);
		builder.withCookieValue(TEST_COOKIE_VALUE);

		String result = builder.buildHeaderValue();

		assertEquals("Cookie header does not match", cookieHeader, result);
	}

	@Test(expected = IllegalStateException.class)
	public void testExceptionWhenNoNameSpecified() {
		builder.withCookieValue(TEST_COOKIE_VALUE);

		builder.build();
	}

	@Test(expected = IllegalStateException.class)
	public void testExceptionWhenNoValueSpecified() {
		builder.withCookieName(TEST_COOKIE_NAME);

		builder.build();
	}

	@Test
	public void testBuildDefaultsCookie() {
		builder.withCookieName(TEST_COOKIE_NAME);
		builder.withCookieValue(TEST_COOKIE_VALUE);
		builder.withDomain(CookieConfig.COOKIE_DOMAIN_DEFAULT_VALUE);
		builder.withMaxAge(CookieConfig.COOKIE_MAXAGE_DEFAULT_VALUE);
		builder.withPath(CookieConfig.COOKIE_PATH_DEFAULT_VALUE);
		builder.withSecureFlag(CookieConfig.COOKIE_SECURE_FLAG_DEFAULT_VALUE);

		Cookie result = builder.build();

		assertEquals("Cookie Name does not match", TEST_COOKIE_NAME, result.getName());
		assertEquals("Cookie Value does not match", TEST_COOKIE_VALUE, result.getValue());
		assertCookieDefaults(result);
	}

	@Test
	public void testBuildDefaultsCookieHeader() {
		String cookieHeader = TEST_COOKIE_NAME + "=" + TEST_COOKIE_VALUE
				+ "; Path=" + CookieConfig.COOKIE_PATH_DEFAULT_VALUE
				+ "; Max-Age=" + CookieConfig.COOKIE_MAXAGE_DEFAULT_VALUE;
		builder.withCookieName(TEST_COOKIE_NAME);
		builder.withCookieValue(TEST_COOKIE_VALUE);
		builder.withDomain(CookieConfig.COOKIE_DOMAIN_DEFAULT_VALUE);
		builder.withMaxAge(CookieConfig.COOKIE_MAXAGE_DEFAULT_VALUE);
		builder.withPath(CookieConfig.COOKIE_PATH_DEFAULT_VALUE);
		builder.withSecureFlag(CookieConfig.COOKIE_SECURE_FLAG_DEFAULT_VALUE);

		String result = builder.buildHeaderValue();

		assertEquals("Cookie header does not match", cookieHeader, result);
	}

	private void assertCookieDefaults(final Cookie result) {
		assertEquals("Cookie max age did not match default", CookieConfig.COOKIE_MAXAGE_DEFAULT_VALUE, result.getMaxAge());
		assertEquals("Cookie path did not match default", CookieConfig.COOKIE_PATH_DEFAULT_VALUE, result.getPath());
		assertNull("Cookie domain did not match default", result.getDomain());
		assertEquals("Cookie secure flag did not match default",
				CookieConfig.COOKIE_SECURE_FLAG_DEFAULT_VALUE, result.getSecure());
	}
}