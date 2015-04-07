/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.views
import org.junit.Test

import com.fasterxml.jackson.contrib.jsonpath.DefaultJsonUnmarshaller

import com.elasticpath.rest.sdk.views.AuthenticationResponseContext

/**
 * ResponseContext for auth.
 */
public class AuthenticationResponseContextTest {

	DefaultJsonUnmarshaller unmarshaller = new DefaultJsonUnmarshaller();

	@Test
	void 'Test authentication unmarshalling'() {
		AuthenticationResponseContext response = unmarshaller.unmarshal(AuthenticationResponseContext.class, json)

		assert response.accessToken == "f1638ca7-2ce0-48a3-b550-284f6f4770ef"
		assert response.expires == 604799
		assert response.tokenType == "bearer"
	}

	private def json = '''{"access_token":"f1638ca7-2ce0-48a3-b550-284f6f4770ef","token_type":"bearer","expires_in":604799,"scope":"geometrixx","role":"PUBLIC"}'''

}
