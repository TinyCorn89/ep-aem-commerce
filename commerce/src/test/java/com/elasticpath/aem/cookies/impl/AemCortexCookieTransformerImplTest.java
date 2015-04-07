/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.aem.cookies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.adobe.granite.crypto.CryptoSupport;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elasticpath.aem.cookies.AemCortexContext;

public class AemCortexCookieTransformerImplTest {
	private static final String URL_ENCODING_AGNOSTIC_VALUE = "1234abc";
	private static final String PUBLIC_ROLE = "PUBLIC";
	private static final String TEST_TOKEN = "bearer token";
	private static final long TEST_EXPIRES_PAST = 1111L;

	@Mock
	private CryptoSupport cryptoSupportMock;

	private AemCortexCookieTransformerImpl processor;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		processor = new AemCortexCookieTransformerImpl(cryptoSupportMock);
	}

	@Test
	public void testParseValidCookieValue() throws Exception {
		String unprotectedvalue = AemCortexCookieTransformerImpl.CURRENT_VERSION + ":bearer token:testscope:PUBLIC:1111:test-hash";

		when(cryptoSupportMock.unprotect(URL_ENCODING_AGNOSTIC_VALUE)).thenReturn(unprotectedvalue);

		AemCortexContext result = processor.deserializeValue(URL_ENCODING_AGNOSTIC_VALUE);

		assertEquals("Cookie version did not match", AemCortexCookieTransformerImpl.CURRENT_VERSION, result.getVersion());
		assertEquals("Cookie token did not match", TEST_TOKEN, result.getAuthenticationToken());
		assertEquals("Cookie scope did not match", "testscope", result.getScope());
		assertEquals("Cookie role did not match", PUBLIC_ROLE, result.getRole());
		assertEquals("Cookie expires did not match", TEST_EXPIRES_PAST, result.getExpiryDate().getTime());
		assertEquals("Cookie hash did not match", "test-hash", result.getIdentifier());
	}

	@Test
	public void testParseCookieIncorrectVersion() throws Exception {
		String unprotectedvalue = "1.1.1.1:bearer token:testscope:PUBLIC:1111:test-hash";

		when(cryptoSupportMock.unprotect(URL_ENCODING_AGNOSTIC_VALUE)).thenReturn(unprotectedvalue);

		AemCortexContext result = processor.deserializeValue(URL_ENCODING_AGNOSTIC_VALUE);

		assertNull("Context should not have been created", result);
	}

	@Test
	public void testParseCookieMissingData() throws Exception {
		String unprotectedvalue = AemCortexCookieTransformerImpl.CURRENT_VERSION + ":bearer token:1111:test-hash";

		when(cryptoSupportMock.unprotect(URL_ENCODING_AGNOSTIC_VALUE)).thenReturn(unprotectedvalue);

		AemCortexContext result = processor.deserializeValue(URL_ENCODING_AGNOSTIC_VALUE);

		assertNull("Context should not have been created", result);
	}

	@Test
	public void testSerializeValue() throws Exception {
		AemCortexContext context = new AemCortexContext.ContextBuilder()
				.withAuthenticationToken(TEST_TOKEN)
				.withScope("test-scope")
				.withRole(PUBLIC_ROLE)
				.withExpiryDate(new Date(TEST_EXPIRES_PAST))
				.withIdentifier("test-hash")
				.withVersion(AemCortexCookieTransformerImpl.CURRENT_VERSION)
				.build();

		when(cryptoSupportMock.protect(isA(String.class))).thenReturn(URL_ENCODING_AGNOSTIC_VALUE);

		String result = processor.serializeValue(context);

		assertEquals(result, URL_ENCODING_AGNOSTIC_VALUE);
	}
}