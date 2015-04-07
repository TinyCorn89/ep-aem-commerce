/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.aem.commerce.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.apache.commons.lang.StringUtils;

public class ElasticPathCommerceUtilTest {

	@Test
	public void testGenerateRandomPassword() throws Exception {
		String randomPassword = ElasticPathCommerceUtil.generateRandomPassword();
		assertTrue("Password was not set correctly", StringUtils.isNotBlank(randomPassword));
		assertFalse("Password should not be 'password'", "password".equals(randomPassword));
	}
}