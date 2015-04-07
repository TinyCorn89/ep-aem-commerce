/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.aem.cookies;

import com.adobe.granite.crypto.CryptoException;

/**
 * Processor for processing the cookie value, this is used where processing is needed to process the cookie value
 * e.g multiple values, encryption, etc.
 */
public interface AemCortexCookieTransformer {

	/**
	 * Parse the value from the cookie and convert into a context. If a context cannot be created for the value return null.
	 * @param value the value from the cookie.
	 * @return context where one can be created otherwise null
	 */
	AemCortexContext deserializeValue(String value);

	/**
	 * Serialize the context into a cookie value.
	 * @param context to serialize
	 * @return serialized value.
	 * @throws com.adobe.granite.crypto.CryptoException when there is an error protecting the serialized value.
	 */
	String serializeValue(AemCortexContext context) throws CryptoException;
}
