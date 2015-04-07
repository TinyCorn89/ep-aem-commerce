/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.aem.cookies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Dictionary;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.adobe.granite.crypto.CryptoSupport;
import com.adobe.granite.security.user.UserProperties;
import com.adobe.granite.security.user.UserPropertiesManager;
import com.day.cq.personalization.UserPropertiesUtil;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.osgi.service.component.ComponentContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.aem.commerce.constants.ElasticPathConstants;
import com.elasticpath.aem.cookies.AemCortexContext;
import com.elasticpath.aem.commerce.cortex.CortexContext;
import com.elasticpath.aem.commerce.cortex.HttpCortexContext;

@PrepareForTest({ResourceUtil.class, UserPropertiesUtil.class})
@RunWith(PowerMockRunner.class)
public class AemCortexContextManagerImplTest {
	private static final String PUBLIC_ROLE = "PUBLIC";
	private static final String TEST_TOKEN = "bearer token";
	private static final String TEST_ENCRYPTED_COOKIE_VALUE = "encryptedCookie";
	private static final int TEST_EXPIRES_PAST = 1234;
	private static final long TEST_EXPIRES_FUTURE = 9999999999999L;
	private static final String TESTSCOPE = "testscope";
	private static final String USER_ID = "testUserId";
	private static final String CORTEX_CONTEXT = "cortexContext";

	@Mock
	private CryptoSupport cryptoSupportMock;

	@Mock
	private ComponentContext mockComponentContext;

	@Mock
	private Dictionary mockProperties;

	@Mock
	private SlingHttpServletRequest mockRequest;

	@Mock
	private SlingHttpServletResponse mockResponse;

	@Mock
	private ResourceResolver mockResourceResolver;

	@InjectMocks
	private AemCortexContextManagerImpl cookieManager;

	@Before
	public void setUp() throws Exception {
		when(mockComponentContext.getProperties()).thenReturn(mockProperties);

	}

	@Test
	public void testGetContextFromRequestAttribute() throws Exception {
		CortexContext testContext = new HttpCortexContext.ContextBuilder()
					.withAuthenticationToken(TEST_TOKEN)
					.withScope(TESTSCOPE)
					.withRole(PUBLIC_ROLE)
					.withExpiryDate(new Date(TEST_EXPIRES_PAST))
					.build();
		when(mockRequest.getAttribute(CORTEX_CONTEXT)).thenReturn(testContext);
		cookieManager.activate(mockComponentContext);

		CortexContext result = cookieManager.retrieveContext(mockRequest);

		assertSame("Context should not have been different", testContext, result);
	}

	@Test
	public void testGetContextFromRequestCookie() throws Exception {
		Cookie testCookie = new Cookie(CookieConfig.COOKIE_NAME_DEFAULT_VALUE, TEST_ENCRYPTED_COOKIE_VALUE);
		String unprotectedvalue = AemCortexCookieTransformerImpl.CURRENT_VERSION + ":bearer token:testscope:PUBLIC:9999999999999:test-hash";

		when(mockRequest.getCookies()).thenReturn(new Cookie[]{testCookie});
		when(cryptoSupportMock.unprotect(isA(String.class))).thenReturn(unprotectedvalue);
		UserProperties mockUserProperties = mock(UserProperties.class);
		when(mockRequest.adaptTo(UserProperties.class)).thenReturn(mockUserProperties);
		PowerMockito.mockStatic(UserPropertiesUtil.class);
		when(UserPropertiesUtil.isAnonymous(mockUserProperties)).thenReturn(true);
		cookieManager.activate(mockComponentContext);

		CortexContext result = cookieManager.retrieveContext(mockRequest);

		assertNotNull("Context should not have been null", result);
		assertEquals("Authentication token does not match", TEST_TOKEN, result.getAuthenticationToken());
		assertEquals("Scope does not match", TESTSCOPE, result.getScope());
		assertEquals("Role does not match", PUBLIC_ROLE, result.getRole());
		assertEquals("Expires does not match", result.getExpiryDate().getTime(), TEST_EXPIRES_FUTURE);
	}

	@Test
	public void testGetContextFromResourceResolver() throws Exception {
		String unprotectedvalue = AemCortexCookieTransformerImpl.CURRENT_VERSION + ":bearer token:testscope:PUBLIC:9999999999999:test-hash";

		when(mockResourceResolver.getAttribute(ElasticPathConstants.CORTEX_TOKEN)).thenReturn(TEST_ENCRYPTED_COOKIE_VALUE);
		when(cryptoSupportMock.unprotect(isA(String.class))).thenReturn(unprotectedvalue);
		UserPropertiesManager mockUserPropertiesManager = mock(UserPropertiesManager.class);
		when(mockResourceResolver.adaptTo(UserPropertiesManager.class)).thenReturn(mockUserPropertiesManager);
		UserProperties mockUserProperties = mock(UserProperties.class);
		when(mockUserPropertiesManager.getUserProperties(anyString(), anyString())).thenReturn(mockUserProperties);
		PowerMockito.mockStatic(UserPropertiesUtil.class);
		when(UserPropertiesUtil.isAnonymous(mockUserProperties)).thenReturn(true);
		cookieManager.activate(mockComponentContext);

		CortexContext result = cookieManager.retrieveContext(mockResourceResolver, USER_ID);

		assertNotNull("Context should not have been null", result);
		assertEquals("Authentication token does not match", TEST_TOKEN, result.getAuthenticationToken());
		assertEquals("Scope does not match", TESTSCOPE, result.getScope());
		assertEquals("Role does not match", PUBLIC_ROLE, result.getRole());
		assertEquals("Expires does not match", result.getExpiryDate().getTime(), TEST_EXPIRES_FUTURE);
	}

	@Test
	public void testGetContextFromRequestTokenExpired() throws Exception {
		Cookie testCookie = new Cookie(CookieConfig.COOKIE_NAME_DEFAULT_VALUE, TEST_ENCRYPTED_COOKIE_VALUE);
		String unprotectedvalue = AemCortexCookieTransformerImpl.CURRENT_VERSION + ":bearer token:testscope:PUBLIC:1111:test-hash";

		when(mockRequest.getCookies()).thenReturn(new Cookie[]{testCookie});
		when(cryptoSupportMock.unprotect(isA(String.class))).thenReturn(unprotectedvalue);
		cookieManager.activate(mockComponentContext);

		CortexContext result = cookieManager.retrieveContext(mockRequest);

		assertNull("Context should have been null", result);
	}

	@Test
	public void testGetContextFromRequestCookieNonMatchingRoles() throws Exception {
		Cookie testCookie = new Cookie(CookieConfig.COOKIE_NAME_DEFAULT_VALUE, TEST_ENCRYPTED_COOKIE_VALUE);
		String unprotectedvalue = AemCortexCookieTransformerImpl.CURRENT_VERSION
				+ ":bearer token:testscope:REGISTERED:9999999999999:test-hash";

		when(mockRequest.getCookies()).thenReturn(new Cookie[]{testCookie});
		when(cryptoSupportMock.unprotect(isA(String.class))).thenReturn(unprotectedvalue);
		cookieManager.activate(mockComponentContext);

		CortexContext result = cookieManager.retrieveContext(mockRequest);

		assertNull("Context should have been null", result);
	}

	@Test
	public void testGetContextIgnoreRoleForAnon() throws Exception {
		Cookie testCookie = new Cookie(CookieConfig.COOKIE_NAME_DEFAULT_VALUE, TEST_ENCRYPTED_COOKIE_VALUE);
		String unprotectedvalue = AemCortexCookieTransformerImpl.CURRENT_VERSION
				+ ":bearer token:testscope:REGISTERED:9999999999999:test-hash";

		Resource mockResource = mock(Resource.class);
		ValueMap mockValueMap = mock(ValueMap.class);
		PowerMockito.mockStatic(ResourceUtil.class);

		when(mockRequest.getCookies()).thenReturn(new Cookie[]{testCookie});
		when(mockRequest.getResource()).thenReturn(mockResource);
		when(ResourceUtil.getValueMap(mockResource)).thenReturn(mockValueMap);
		when(mockValueMap.containsKey("cq:ignoreAnonUsers")).thenReturn(true);
		when(mockValueMap.get("cq:ignoreAnonUsers")).thenReturn("true");
		when(cryptoSupportMock.unprotect(isA(String.class))).thenReturn(unprotectedvalue);
		UserProperties mockUserProperties = mock(UserProperties.class);
		when(mockRequest.adaptTo(UserProperties.class)).thenReturn(mockUserProperties);
		PowerMockito.mockStatic(UserPropertiesUtil.class);
		when(UserPropertiesUtil.isAnonymous(mockUserProperties)).thenReturn(false);
		when(UserPropertiesUtil.getValue(mockUserProperties, "ep-identifier")).thenReturn("test-hash");
		cookieManager.activate(mockComponentContext);

		CortexContext result = cookieManager.retrieveContext(mockRequest);

		assertNotNull("Context should have been null", result);
		assertEquals("Authentication token does not match", TEST_TOKEN, result.getAuthenticationToken());
		assertEquals("Scope does not match", TESTSCOPE, result.getScope());
		assertEquals("Role does not match", "REGISTERED" , result.getRole());
		assertEquals("Expires does not match", result.getExpiryDate().getTime(), TEST_EXPIRES_FUTURE);
	}

	@Test
	public void testSetContextAsCookie() throws Exception {
		AemCortexContext context = new AemCortexContext.ContextBuilder()
				.withAuthenticationToken(TEST_TOKEN)
				.withScope("test-scope")
				.withRole(PUBLIC_ROLE)
				.withExpiryDate(new Date(TEST_EXPIRES_PAST))
				.withIdentifier("test-hash")
				.withVersion(AemCortexCookieTransformerImpl.CURRENT_VERSION)
				.build();

		when(cryptoSupportMock.protect(isA(String.class))).thenReturn(TEST_ENCRYPTED_COOKIE_VALUE);
		cookieManager.activate(mockComponentContext);

		cookieManager.persistContext(mockRequest, mockResponse, context);

		verify(mockResponse).addCookie(isA(Cookie.class));
	}

	@Test
	public void testSetContextAsHeader() throws Exception {
		AemCortexContext context = new AemCortexContext.ContextBuilder()
				.withAuthenticationToken(TEST_TOKEN)
				.withScope("test-scope")
				.withRole(PUBLIC_ROLE)
				.withExpiryDate(new Date(TEST_EXPIRES_PAST))
				.withIdentifier("test-hash")
				.withVersion(AemCortexCookieTransformerImpl.CURRENT_VERSION)
				.build();

		when(cryptoSupportMock.protect(isA(String.class))).thenReturn(TEST_ENCRYPTED_COOKIE_VALUE);
		when(mockProperties.get(CookieConfig.COOKIE_HTTPONLY_FLAG_PROPERTY_NAME)).thenReturn("true");
		cookieManager.activate(mockComponentContext);

		cookieManager.persistContext(mockRequest, mockResponse, context);

		verify(mockResponse).setHeader(isA(String.class), isA(String.class));
	}
}