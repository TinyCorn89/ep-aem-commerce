/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.impl;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.PriceInfo;

/**
 * Tests {@link com.elasticpath.aem.commerce.impl.ElasticPathCartEntryImpl}.
 */
public class ElasticPathCartEntryImplTest {
	static final Currency RESOURCE_CURRENCY = Currency.getInstance("USD");

	private static final List<PriceInfo> PRICE_LIST = Arrays.asList(
			new PriceInfo(BigDecimal.TEN, Locale.US, RESOURCE_CURRENCY),
			new PriceInfo(BigDecimal.TEN, Locale.US, RESOURCE_CURRENCY));

	/**
	 * Tests {@link com.elasticpath.aem.commerce.impl.ElasticPathCartEntryImpl#getLineItemUri()}.
	 * 
	 * @throws CommerceException commerceException
	 */
	@Test
	public void testGetLineItemUri() throws CommerceException {
		final String cortexUrl = "cortexUrl";
		ElasticPathCartEntryImpl elasticPathCartEntryImpl = givenACartEntryWithUrl(cortexUrl);
		assertEquals(cortexUrl, elasticPathCartEntryImpl.getLineItemUri());
	}

	/**
	 * Tests {@link ElasticPathCartEntryImpl#getLineItemUri()} with null url.
	 * 
	 * @throws CommerceException commerceException
	 */
	@Test
	public void testGetLineItemUriWithNullUrl() throws CommerceException {
		ElasticPathCartEntryImpl elasticPathCartEntryImpl = givenACartEntryWithUrl(null);
		assertNull(elasticPathCartEntryImpl.getLineItemUri());
	}

	private ElasticPathCartEntryImpl givenACartEntryWithUrl(final String cortexUrl) throws CommerceException {
		return new ElasticPathCartEntryImpl(null, 0, 1, cortexUrl, PRICE_LIST, Collections.<String, Object> emptyMap());
	}
}