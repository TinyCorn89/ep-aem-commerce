/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.adobe.granite.security.user.UserProperties;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.aem.commerce.cortex.CortexContext;
import com.elasticpath.aem.commerce.cortex.CortexServiceContext;
import com.elasticpath.aem.commerce.cortex.HttpCortexContext;
import com.elasticpath.aem.commerce.exception.SaveConflictException;
import com.elasticpath.aem.commerce.service.UserPropertiesService;
import com.elasticpath.aem.cookies.AemCortexContextManager;
import com.elasticpath.commerce.config.DemoPasswordConfiguration;
import com.elasticpath.rest.client.CortexClientFactory;
import com.elasticpath.rest.sdk.service.AuthService;
import com.elasticpath.rest.sdk.views.AuthenticationResponseContext;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {
	private static final String TEST_SCOPE = "testscope";
	private static final String TEST_URL = "http://test.com/cortex";
	private static final String ANONYMOUS_USER_ID = "anonymous";
	private static final String REGISTERED_USER_ID = "ted@testy.com";
	private static final String TEST_ENCRYPTED_PASSWORD = "encryptedPassword";
	private static final String TEST_UNPROTECTED_PASSWORD = "unprotectedPassword";

	@Mock
	private CortexServiceContext mockConfiguration;

	@Mock
	private DemoPasswordConfiguration handler;

	@Mock
	private AemCortexContextManager mockCookieManager;

	@Mock
	private CryptoSupport mockCryptoSupport;

	@Mock
	private SlingHttpServletRequest mockRequest;

	@Mock
	private SlingHttpServletResponse mockResponse;

	@Mock
	private UserProperties mockUserProperties;

	@Mock
	private Client mockClient;

	@Mock
	private CortexClientFactory mockCortexClientFactory;

	@Mock
	private UserPropertiesService userPropertiesService;

	private AuthService service;

	@Before
	public void setup() {
		service = new AuthServiceImpl(mockConfiguration, TEST_SCOPE, mockClient, mockCortexClientFactory, userPropertiesService);

		when(mockConfiguration.getCookieManager()).thenReturn(mockCookieManager);
		when(mockConfiguration.getHandler()).thenReturn(handler);
		when(mockConfiguration.getCryptoSupport()).thenReturn(mockCryptoSupport);
	}

	@Test
	public void testGetPublicContextWithExistingContext() throws CommerceException {
		CortexContext expectedContext = createPublicContext();

		when(mockCookieManager.retrieveContext(mockRequest)).thenReturn(expectedContext);
		when(mockRequest.adaptTo(UserProperties.class)).thenReturn(mockUserProperties);
		when(mockUserProperties.getAuthorizableID()).thenReturn(ANONYMOUS_USER_ID);

		CortexContext result = service.getCortexContext(mockRequest, mockResponse);

		assertEquals(expectedContext, result);
	}

	@Test
	public void testGetPublicContextNoContext() throws CommerceException {
		AuthenticationResponseContext expectedContext = new AuthenticationResponseContext();

		mockContext(null);

		when(mockUserProperties.getAuthorizableID()).thenReturn(ANONYMOUS_USER_ID);

		Response mockClientResponse = mockLoginRequest();

		Response.StatusType mockStatusInfo = mock(Response.StatusType.class);
		when(mockClientResponse.getStatusInfo()).thenReturn(mockStatusInfo);
		when(mockStatusInfo.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);
		when(mockClientResponse.readEntity(AuthenticationResponseContext.class)).thenReturn(expectedContext);

		CortexContext result = service.getCortexContext(mockRequest, mockResponse);

		assertEquals(expectedContext, result);

		verify(mockCookieManager).persistContext(mockRequest, mockResponse, expectedContext);
	}

	@Test
	public void testGetRegisteredNoContext() throws CommerceException, RepositoryException, CryptoException {
		AuthenticationResponseContext expectedContext = new AuthenticationResponseContext();

		mockContext(null);

		when(mockUserProperties.getAuthorizableID()).thenReturn(REGISTERED_USER_ID);
		when(mockUserProperties.getProperty("cq:password")).thenReturn(TEST_ENCRYPTED_PASSWORD);
		when(mockCryptoSupport.unprotect(TEST_ENCRYPTED_PASSWORD)).thenReturn(TEST_UNPROTECTED_PASSWORD);

		Response mockClientResponse = mockLoginRequest();

		Response.StatusType mockStatusInfo = mock(Response.StatusType.class);
		when(mockClientResponse.getStatusInfo()).thenReturn(mockStatusInfo);
		when(mockStatusInfo.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);
		when(mockClientResponse.readEntity(AuthenticationResponseContext.class)).thenReturn(expectedContext);

		CortexContext result = service.getCortexContext(mockRequest, mockResponse);

		assertEquals(expectedContext, result);
		verify(mockCookieManager).persistContext(mockRequest, mockResponse, expectedContext);
	}

	@Test
	public void testGetRegisteredExistingContext() throws CommerceException, RepositoryException, CryptoException {
		AuthenticationResponseContext expectedContext = new AuthenticationResponseContext();
		CortexContext existingContext = createPublicContext();

		mockContext(existingContext);

		when(mockUserProperties.getAuthorizableID()).thenReturn(REGISTERED_USER_ID);
		when(mockUserProperties.getProperty("cq:password")).thenReturn(TEST_ENCRYPTED_PASSWORD);
		when(mockCryptoSupport.unprotect(TEST_ENCRYPTED_PASSWORD)).thenReturn(TEST_UNPROTECTED_PASSWORD);

		Response mockClientResponse = mockLoginRequest();

		Response.StatusType mockStatusInfo = mock(Response.StatusType.class);
		when(mockClientResponse.getStatusInfo()).thenReturn(mockStatusInfo);
		when(mockStatusInfo.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);
		when(mockClientResponse.readEntity(AuthenticationResponseContext.class)).thenReturn(expectedContext);

		CortexContext result = service.getCortexContext(mockRequest, mockResponse);

		assertEquals(expectedContext, result);
		verify(mockCookieManager).persistContext(mockRequest, mockResponse, expectedContext);
	}

	@Test
	public void testRegisterCortexUser() throws CommerceException, RepositoryException, CryptoException {
		CortexContext expectedContext = createRegisteredContext();
		CortexContext existingContext = createPublicContext();

		mockContext(existingContext);

		when(mockUserProperties.getAuthorizableID()).thenReturn(REGISTERED_USER_ID);
		when(mockUserProperties.getProperty("familyName")).thenReturn("testy");
		when(mockUserProperties.getProperty("givenName")).thenReturn("ted");
		Node mockNode = mock(Node.class);
		when(mockUserProperties.getNode()).thenReturn(mockNode);
		Session mockSession = mock(Session.class);
		when(mockNode.getSession()).thenReturn(mockSession);

		when(mockCryptoSupport.unprotect(TEST_ENCRYPTED_PASSWORD)).thenReturn(TEST_UNPROTECTED_PASSWORD);
		when(handler.isDemoPasswordEnabledForCortexRegistration()).thenReturn(false);

		Response mockClientResponse = mockRegisterUser(existingContext);
		when(mockClientResponse.getStatusInfo()).thenReturn(Response.Status.CREATED);

		CortexContext result = service.getCortexContext(mockRequest, mockResponse);

		assertContextEquals(expectedContext, result);
		verify(mockCryptoSupport).protect(isA(String.class));
	}

	@Test
	public void testRegisterCortexUserWithUserPropertiesException() throws 	SaveConflictException,
																			RepositoryException,
																			CryptoException,
																			CommerceException {
		CortexContext expectedContext = createRegisteredContext();
		CortexContext existingContext = createPublicContext();

		mockContext(existingContext);

		when(mockUserProperties.getAuthorizableID()).thenReturn(REGISTERED_USER_ID);
		when(mockUserProperties.getProperty("familyName")).thenReturn("testy");
		when(mockUserProperties.getProperty("givenName")).thenReturn("ted");
		Node mockNode = mock(Node.class);
		when(mockUserProperties.getNode()).thenReturn(mockNode);
		Session mockSession = mock(Session.class);
		when(mockNode.getSession()).thenReturn(mockSession);

		when(mockCryptoSupport.unprotect(TEST_ENCRYPTED_PASSWORD)).thenReturn(TEST_UNPROTECTED_PASSWORD);
		when(handler.isDemoPasswordEnabledForCortexRegistration()).thenReturn(false);

		Response mockClientResponse = mockRegisterUser(existingContext);
		when(mockClientResponse.getStatusInfo()).thenReturn(Response.Status.CREATED);

		doThrow(new SaveConflictException("OakState0001: Unresolved conflicts in..."))
				.when(userPropertiesService).updateUserProperty(any(UserProperties.class),
				anyString(),
				anyString());

		CortexContext result = service.getCortexContext(mockRequest, mockResponse);

		assertContextEquals(expectedContext, result);

	}

	@Test(expected = CommerceException.class)
	public void testRegisterCortexUserFailure() throws CommerceException, RepositoryException, CryptoException {
		CortexContext existingContext = createPublicContext();

		mockContext(existingContext);

		when(mockUserProperties.getAuthorizableID()).thenReturn(REGISTERED_USER_ID);
		when(mockUserProperties.getProperty("familyName")).thenReturn("testy");
		when(mockUserProperties.getProperty("givenName")).thenReturn("ted");
		Node mockNode = mock(Node.class);
		when(mockUserProperties.getNode()).thenReturn(mockNode);
		Session mockSession = mock(Session.class);
		when(mockNode.getSession()).thenReturn(mockSession);

		when(mockCryptoSupport.unprotect(TEST_ENCRYPTED_PASSWORD)).thenReturn(TEST_UNPROTECTED_PASSWORD);
		when(handler.isDemoPasswordEnabledForCortexRegistration()).thenReturn(false);

		Response mockClientResponse = mockRegisterUser(existingContext);
		when(mockClientResponse.getStatusInfo()).thenReturn(Response.Status.PRECONDITION_FAILED);

		service.getCortexContext(mockRequest, mockResponse);
	}

	private void assertContextEquals(final CortexContext expectedContext, final CortexContext result) {
		assertEquals(expectedContext.getAuthenticationToken(), result.getAuthenticationToken());
		assertEquals(expectedContext.getScope(), result.getScope());
		assertEquals(expectedContext.getRole(), result.getRole());
	}

	@Test(expected = CommerceException.class)
	public void testGetPublicContextNoContextBadRequest() throws CommerceException {
		AuthenticationResponseContext expectedContext = new AuthenticationResponseContext();

		mockContext(null);
		when(mockUserProperties.getAuthorizableID()).thenReturn(ANONYMOUS_USER_ID);

		Response mockClientResponse = mockLoginRequest();

		Response.StatusType mockStatusInfo = mock(Response.StatusType.class);
		when(mockClientResponse.getStatusInfo()).thenReturn(mockStatusInfo);
		when(mockStatusInfo.getFamily()).thenReturn(Response.Status.Family.CLIENT_ERROR);
		when(mockClientResponse.readEntity(AuthenticationResponseContext.class)).thenReturn(expectedContext);

		service.getCortexContext(mockRequest, mockResponse);
	}

	@Test
	public void testNewCortexRegistrationPasswordDemoEnabled() {
		String testDemoPassword = "testDemoPassword";

		when(handler.isDemoPasswordEnabledForCortexRegistration()).thenReturn(true);
		when(handler.getDemoPasswordForCortexRegistration()).thenReturn(testDemoPassword);

		String actualResult = service.newCortexUserPassword();
		assertEquals("Demo passwords did not match", testDemoPassword, actualResult);
	}

	@Test
	public void testNewCortexRegistrationPasswordDemoDisabled() {
		String testDemoPassword = "testDemoPassword";

		when(handler.isDemoPasswordEnabledForCortexRegistration()).thenReturn(false);

		String result = service.newCortexUserPassword();
		assertFalse("Password should not have matched demo password", testDemoPassword.equals(result));

		String secondResult = service.newCortexUserPassword();
		assertFalse("Passwords should not match they should be generated randomly", result.equals(secondResult));

		verify(handler, times(0)).getDemoPasswordForCortexRegistration();
	}

	private CortexContext createPublicContext() {
		return new HttpCortexContext.ContextBuilder()
				.withAuthenticationToken("testToken")
				.withExpiryDate(new Date())
				.withRole("PUBLIC")
				.withScope(TEST_SCOPE)
				.build();
	}

	private CortexContext createRegisteredContext() {
		return new HttpCortexContext.ContextBuilder()
				.withAuthenticationToken("testToken")
				.withExpiryDate(new Date())
				.withRole("REGISTERED")
				.withScope(TEST_SCOPE)
				.build();
	}

	private Response mockLoginRequest() {
		Invocation.Builder mockBuilder = mock(Invocation.Builder.class);
		Response mockClientResponse = mock(Response.class);
		WebTarget mockWebTarget = mock(WebTarget.class);
		when(mockClient.target(TEST_URL + "/oauth2/tokens")).thenReturn(mockWebTarget);
		when(mockWebTarget.request()).thenReturn(mockBuilder);
		when(mockBuilder.post(isA(Entity.class))).thenReturn(mockClientResponse);
		return mockClientResponse;
	}

	private Response mockRegisterUser(final CortexContext existingContext) {
		Invocation.Builder mockBuilder = mock(Invocation.Builder.class);
		Response mockClientResponse = mock(Response.class);
		WebTarget mockWebTarget = mock(WebTarget.class);
		when(mockClient.target(any(UriBuilder.class))).thenReturn(mockWebTarget);
		when(mockWebTarget.request()).thenReturn(mockBuilder);
		when(mockBuilder.header(HttpHeaders.AUTHORIZATION,
				existingContext.getAuthenticationToken())).thenReturn(mockBuilder);
		when(mockBuilder.post(isA(Entity.class))).thenReturn(mockClientResponse);
		return mockClientResponse;
	}

	private void mockContext(final CortexContext context) {
		when(mockCortexClientFactory.getCortexURL()).thenReturn(TEST_URL);
		when(mockCookieManager.retrieveContext(mockRequest)).thenReturn(context);
		when(mockRequest.adaptTo(UserProperties.class)).thenReturn(mockUserProperties);
	}
}
