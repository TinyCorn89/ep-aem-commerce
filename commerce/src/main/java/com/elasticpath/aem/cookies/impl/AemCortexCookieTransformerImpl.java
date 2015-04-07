/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.aem.cookies.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.aem.cookies.AemCortexContext;
import com.elasticpath.aem.cookies.AemCortexCookieTransformer;

/**
 * Implementation of the cookie value processor for use with the cortex auth cookie.
 * V1.0 format <version>:<token>:<scope>:<role>:<expires>:<user-identifier>
 */
public class AemCortexCookieTransformerImpl implements AemCortexCookieTransformer {
	/** Current version of cookie. */
	public static final String CURRENT_VERSION = "1.0";

	private static final Logger LOG = LoggerFactory.getLogger(AemCortexCookieTransformerImpl.class);
	private static final String SEPERATOR = ":";

	private static final int VERSION_INDEX = 0;
	private static final int AUTH_TOKEN_INDEX = 1;
	private static final int SCOPE_INDEX = 2;
	private static final int ROLE_INDEX = 3;
	private static final int EXPIRES_INDEX = 4;
	private static final int EP_IDENTIFIER_INDEX = 5;
	private static final int CURRENT_SIZE = 6;

	private final CryptoSupport cryptoSupport;

	/**
	 * Cookie value processor.
	 *
	 * @param cryptoSupport reference to aem crypto support service
	 */
	public AemCortexCookieTransformerImpl(final CryptoSupport cryptoSupport) {
		this.cryptoSupport = cryptoSupport;
	}

	@Override
	public AemCortexContext deserializeValue(final String value) {
		String cookieValue;

		try {
			cookieValue = cryptoSupport.unprotect(URLDecoder.decode(value, "UTF-8"));
		} catch (CryptoException e) {
			LOG.warn("Could not decrypt cookie value, ignoring cookie");
			LOG.trace("Decrypt Exception", e);
			return null;
		} catch (UnsupportedEncodingException e) {
			LOG.warn("Could not decode cookie value, ignoring cookie");
			LOG.trace("Encoding Exception", e);
			return null;
		}

		LOG.debug("Cookie input value: {}", cookieValue);
		String [] parts = cookieValue.split(SEPERATOR);

		if (isValid(parts)) {
			long expires = Long.parseLong(parts[EXPIRES_INDEX]);

			return new AemCortexContext.ContextBuilder()
					.withAuthenticationToken(parts[AUTH_TOKEN_INDEX])
					.withScope(parts[SCOPE_INDEX])
					.withRole(parts[ROLE_INDEX])
					.withExpiryDate(new Date(expires))
					.withIdentifier(parts[EP_IDENTIFIER_INDEX])
					.withVersion(parts[VERSION_INDEX])
					.build();
		} else {
			LOG.info("Cookie not valid, ignoring. Current version is {}, cookie version is {}", CURRENT_VERSION, parts[VERSION_INDEX]);
		}

		return null;
	}

	private boolean isValid(final String[] parts) {
		return parts[VERSION_INDEX].equals(CURRENT_VERSION) && parts.length == CURRENT_SIZE;
	}

	@Override
	public String serializeValue(final AemCortexContext context) throws CryptoException {
		StringBuilder cookieValue = new StringBuilder();
		cookieValue.append(CURRENT_VERSION)
				.append(SEPERATOR)
				.append(context.getAuthenticationToken())
		        .append(SEPERATOR)
				.append(context.getScope())
				.append(SEPERATOR)
				.append(context.getRole())
				.append(SEPERATOR)
				.append(context.getExpiryDate().getTime())
				.append(SEPERATOR)
				.append(context.getIdentifier());

		LOG.debug("Unprotected cookie value: {}", cookieValue);
		try {
			return URLEncoder.encode(cryptoSupport.protect(cookieValue.toString()), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new CryptoException("Character encoding not supported", e);
		}
	}
}
