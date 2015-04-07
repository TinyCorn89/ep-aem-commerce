/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.service.impl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.jcr.RepositoryException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.security.user.UserProperties;
import com.day.cq.personalization.UserPropertiesUtil;
import com.elasticpath.aem.commerce.cortex.CortexContext;
import com.elasticpath.aem.commerce.cortex.CortexServiceContext;
import com.elasticpath.aem.commerce.cortex.HttpCortexContext;
import com.elasticpath.aem.commerce.exception.SaveConflictException;
import com.elasticpath.aem.commerce.service.UserPropertiesService;
import com.elasticpath.aem.commerce.util.ElasticPathCommerceUtil;
import com.elasticpath.rest.sdk.views.AuthForm;
import com.elasticpath.rest.sdk.views.AuthenticationResponseContext;
import com.elasticpath.rest.sdk.views.RegistrationForm;
import com.elasticpath.rest.client.CortexClientFactory;
import com.elasticpath.rest.client.jaxrs.JaxRsUtil;
import com.elasticpath.rest.sdk.service.AuthService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for authenticating with cortex.
 */
public class AuthServiceImpl implements AuthService {
	private static final String AUTH_URL = "/oauth2/tokens";
	private static final String REGISTERED_ROLE = "REGISTERED";
	private static final String PUBLIC_ROLE = "PUBLIC";
	private static final String GRANT_TYPE = "password";
	private static final String CQ_PASSWORD_PROPERTY = "cq:password";

	private static final Logger LOG = LoggerFactory.getLogger(AuthServiceImpl.class);
	private static final LoadingCache<String, Lock> REGISTRATION_LOCKS = CacheBuilder
			.newBuilder()
			.weakKeys()
			.build(new CacheLoader<String, Lock>() {
					   public Lock load(final String key) throws Exception {
						   return new ReentrantLock();
					   }
				   }
			);

	private final CortexServiceContext cortexServiceContext;
	private final String urlScope;
	private final Client authClient;
	private final CortexClientFactory cortexClientFactory;
	private final UserPropertiesService userPropertiesService;

	/**
	 * Create default AuthService.
	 *
	 * @param cortexServiceContext  config.
	 * @param urlScope              urlScope.
	 * @param authClient            client without token filter.
	 * @param cortexClientFactory   the cortex client factory
	 * @param userPropertiesService service for updating the user in JCR
	 */
	public AuthServiceImpl(final CortexServiceContext cortexServiceContext,
						   final String urlScope,
						   final Client authClient,
						   final CortexClientFactory cortexClientFactory,
						   final UserPropertiesService userPropertiesService) {
		this.cortexServiceContext = cortexServiceContext;
		this.urlScope = urlScope;
		this.authClient = authClient;
		this.cortexClientFactory = cortexClientFactory;
		this.userPropertiesService = userPropertiesService;
	}

	@Override
	public CortexContext getCortexContext(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws CommerceException {
		CortexContext cookieContext = cortexServiceContext.getCookieManager().retrieveContext(request);
		UserProperties userProperties = request.adaptTo(UserProperties.class);

		if (cookieContext == null) {
			if (UserPropertiesUtil.isAnonymous(userProperties)) {
				cookieContext = loginPublic(request, response);
			} else {
				//logged into aem but no token
				cookieContext = loginOrRegisterAEMUser(request, response, null, userProperties);
			}
		} else {
			//logged into aem but have public token
			if (!UserPropertiesUtil.isAnonymous(userProperties) && cookieContext.getRole().equals(PUBLIC_ROLE)) {
				cookieContext = loginOrRegisterAEMUser(request, response, cookieContext, userProperties);
			}
		}

		if (cookieContext == null) {
			throw new CommerceException("Unable to retrieve a valid context");
		} else {
			return cookieContext;
		}
	}

	private CortexContext loginPublic(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws CommerceException {
		AuthForm authForm = new AuthForm();
		authForm.setGrantType(GRANT_TYPE);
		authForm.setRole(PUBLIC_ROLE);
		authForm.setScope(urlScope);

		return loginToCortex(request, response, authForm);
	}

	/**
	 * It creates the user in cortex if the user is logging into AEM first time and also authenticates it. If the user already exists then it just
	 * authenticate and returns the cortex context.
	 *
	 * @param response        SlingHttpServletResponse
	 * @param request         SlingHttpServletRequest
	 * @param existingContext CortexContext
	 * @param userproperties  UserProperties
	 * @return CortexContext CortexContext
	 * @throws CommerceException commerceException
	 */
	private CortexContext loginOrRegisterAEMUser(final SlingHttpServletRequest request,
												 final SlingHttpServletResponse response,
												 final CortexContext existingContext,
												 final UserProperties userproperties) throws CommerceException {
		CortexContext newContext = null;

		try {
			String userId = userproperties.getAuthorizableID();
			//Optimistically get the node.
			String cortexPassword = userproperties.getProperty(CQ_PASSWORD_PROPERTY);
			if (StringUtils.isNotBlank(cortexPassword)) {
				newContext = loginRegisteredUser(request, response, existingContext, userId, cortexPassword);
			} else {
				CortexContext publicContext;

				//we need a token to register so we should grab a public token if there isn't already one.
				if (existingContext == null) {
					publicContext = loginPublic(request, response);
				} else {
					publicContext = existingContext;
				}

				Lock lock = REGISTRATION_LOCKS.getUnchecked(userId);
				lock.lock();
				try {
					//Now that we have the lock lets see if someone has registered while we were waiting
					userproperties.getNode().getSession().refresh(false);
					cortexPassword = userproperties.getProperty(CQ_PASSWORD_PROPERTY);

					if (cortexPassword == null) {
						if (registerUser(userproperties, publicContext, userId)) {
							//When we register a new user the existing token remains valid but becomes registered.
							newContext = new HttpCortexContext.ContextBuilder()
									.withAuthenticationToken(publicContext.getAuthenticationToken())
									.withScope(publicContext.getScope())
									.withRole(REGISTERED_ROLE)
									.withExpiryDate(publicContext.getExpiryDate())
									.build();
							cortexServiceContext.getCookieManager().persistContext(request, response, newContext);
						} else {
							throw new CommerceException("An error occurred while registering a new user with cortex");
						}
					} else {
						newContext = loginRegisteredUser(request, response, existingContext, userId, cortexPassword);
					}
				} finally {
					lock.unlock();
				}
			}
		} catch (RepositoryException | CryptoException ex) {
			throw new CommerceException("RepositoryException occurred during login or signup ", ex);
		}
		return newContext;
	}

	private CortexContext loginRegisteredUser(final SlingHttpServletRequest request,
											  final SlingHttpServletResponse response,
											  final CortexContext context,
											  final String userId,
											  final String encryptedPassword) throws CryptoException, CommerceException {
		AuthForm authForm = new AuthForm();
		authForm.setGrantType(GRANT_TYPE);
		authForm.setRole(REGISTERED_ROLE);
		authForm.setScope(urlScope);
		authForm.setUsername(userId);
		authForm.setPassword(cortexServiceContext.getCryptoSupport().unprotect(encryptedPassword));

		if (context != null) {
			authForm.setAuthenticationToken(context.getAuthenticationToken());
		}

		return loginToCortex(request, response, authForm);
	}

	private boolean registerUser(final UserProperties userproperties,
								 final CortexContext existingContext,
								 final String userId) throws RepositoryException, CryptoException {
		boolean isUserCreated = false;
		String familyName;
		String givenName;
		String userPassword;
		String encryptedPassword;
		familyName = userproperties.getProperty(UserProperties.FAMILY_NAME);
		givenName = userproperties.getProperty(UserProperties.GIVEN_NAME);

		RegistrationForm newUser = new RegistrationForm();
		newUser.setUsername(userId);
		newUser.setGivenName(givenName);
		newUser.setFamilyName(familyName);

		if (validateUserDetails(newUser)) {
			userPassword = newCortexUserPassword();
			newUser.setPassword(userPassword);

			UriBuilder registrationUri = UriBuilder.fromUri(cortexClientFactory.getCortexURL())
					.path("/registrations")
					.path(urlScope)
					.path("/newaccount");

			Response response = null;

			try {
				response = authClient
						.target(registrationUri)
						.request()
						.header(HttpHeaders.AUTHORIZATION, existingContext.getAuthenticationToken())
						.post(Entity.entity(newUser, MediaType.APPLICATION_JSON_TYPE));
			} catch (ProcessingException processingException) {
				LOG.debug("ProcessingException while authenticating with cortex", processingException);
				return isUserCreated;
			} finally {
				JaxRsUtil.closeQuietly(response);
			}

			//If we get expected response store the users password in aem
			if (JaxRsUtil.isSuccessful(response.getStatusInfo())) {
				encryptedPassword = cortexServiceContext.getCryptoSupport().protect(userPassword);
				try {
					userPropertiesService.updateUserProperty(userproperties, CQ_PASSWORD_PROPERTY, encryptedPassword);
				} catch (SaveConflictException conflict) {
					/**
					 * A conflict occurred during the update operation, which we'll ignore in the case.
					 */
					LOG.warn("Conflict occurred during user update.", conflict);
				}
				isUserCreated = true;
			} else {
				LOG.info("Failed to register user:  {}", JaxRsUtil.getStatusString(response.getStatusInfo()));
			}
		} else {
			LOG.info("New user was not valid {}", newUser);
		}

		return isUserCreated;
	}

	/**
	 * Validate the user input before sending to cortex.
	 *
	 * @param newUser to create.
	 * @return isValid.
	 */
	protected boolean validateUserDetails(final RegistrationForm newUser) {
		EmailValidator emailValidator = EmailValidator.getInstance();
		return emailValidator.isValid(newUser.getUsername())
				&& StringUtils.isNotBlank(newUser.getFamilyName())
				&& StringUtils.isNotBlank(newUser.getGivenName());
	}

	@Override
	public String newCortexUserPassword() {
		if (cortexServiceContext.getHandler().isDemoPasswordEnabledForCortexRegistration()) {
			return cortexServiceContext.getHandler().getDemoPasswordForCortexRegistration();
		}
		return ElasticPathCommerceUtil.generateRandomPassword();
	}

	@Override
	public CortexContext loginToCortex(final AuthForm authForm) throws CommerceException {
		String cortexUrl = cortexClientFactory.getCortexURL() + AUTH_URL;
		Invocation.Builder reqBuilder = authClient
				.target(cortexUrl)
				.request();

		mergeExistingContext(authForm, reqBuilder);

		Response authResponse = null;
		try {
			authResponse = reqBuilder.post(Entity.form(authForm.asForm()));
		} catch (ProcessingException processingException) {
			LOG.debug("ProcessingException while authenticating with cortex", processingException);
			JaxRsUtil.closeQuietly(authResponse);
			throw new CommerceException("ProcessingException while authenticating with cortex", processingException);
		}

		CortexContext context = null;
		if (JaxRsUtil.isSuccessful(authResponse.getStatusInfo())) {
			context = authResponse.readEntity(AuthenticationResponseContext.class);
			JaxRsUtil.closeQuietly(authResponse);
		} else {
			LOG.debug("User Authentication Failure with cortex: {} {}",
					cortexUrl, JaxRsUtil.getStatusString(authResponse.getStatusInfo()));
		}
		return context;
	}

	private CortexContext loginToCortex(final SlingHttpServletRequest request,
										final SlingHttpServletResponse response,
										final AuthForm authForm) throws CommerceException {
		CortexContext context = loginToCortex(authForm);

		if (context != null) {
			cortexServiceContext.getCookieManager().persistContext(request, response, context);
		}

		return context;
	}

	/**
	 * If we already have an existing token add it as a header so its context will be merged into the new one.
	 * e.g. cart merge.
	 *
	 * @param authForm   login form.
	 * @param reqBuilder builder.
	 */
	private void mergeExistingContext(final AuthForm authForm, final Invocation.Builder reqBuilder) {
		if (authForm.hasAuthHeader()) {
			reqBuilder.header(HttpHeaders.AUTHORIZATION, authForm.getAuthenticationToken());
		}
	}
}
