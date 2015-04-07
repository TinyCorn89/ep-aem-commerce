/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.adobe.cq.commerce.api.CommerceConstants;
import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.ShippingMethod;
import com.google.common.collect.Lists;

import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.aem.commerce.constants.ElasticPathConstants;
import com.elasticpath.rest.sdk.service.OrderConfigurationService;

/**
 * Mock Unit test class for {@link ElasticPathCommerceSessionImpl} for shipping methods related implementations.
 */
@RunWith(MockitoJUnitRunner.class)
public class ElasticPathCommerceSessionShippingMethodTest extends BaseElasticPathCommerceSessionTest {

	/** TEST_SHIPPING_METHOD. */
	private static final String TEST_SHIPPING_METHOD_CHOSEN = "/shipping/FedEx1day";

	/** TEST_SHIPPING_METHOD. */
	private static final String TEST_SHIPPING_METHOD_CHOICE = "/shipping/SuperSpeedy";

	private static final BigDecimal AMOUNT = BigDecimal.ONE;

	/**
	 * Test case to test {@link ElasticPathCommerceSessionImpl#getAvailableShippingMethods()} API of commerce when shipping methods are available.
	 * 
	 * @throws CommerceException commerceException
	 */
	@Test
	public void testGetAvailableShippingMethods() throws CommerceException {
		List<ShippingMethod> methods = Lists.newArrayList();
		ShippingMethod methodOne = new ElasticPathShippingMethodImpl(TEST_SHIPPING_METHOD_CHOSEN, "test-title", "test-desc", AMOUNT);
		ShippingMethod methodTwo = new ElasticPathShippingMethodImpl(TEST_SHIPPING_METHOD_CHOICE, "test-title", "test-desc", AMOUNT);
		methods.add(methodOne);
		methods.add(methodTwo);

		when(orderService.getCortexOrderAvailableShippingMethods(any(Locale.class))).thenReturn(methods);

		List<ShippingMethod> shippingMethods = getElasticPathCommerceSessionImpl().getAvailableShippingMethods();
		assertNotNull(shippingMethods);
		assertEquals(2, shippingMethods.size());
		assertValidShippingMethod(methodOne, shippingMethods.get(0));
		assertValidShippingMethod(methodTwo, shippingMethods.get(1));
	}

	private void assertValidShippingMethod(final ShippingMethod expected, final ShippingMethod actual) {
		assertEquals(expected.getPath(), actual.getPath());
		assertEquals(expected.getTitle(), actual.getTitle());
		assertEquals(expected.getDescription(), actual.getDescription());
		assertEquals(expected.getProperty(ElasticPathConstants.AMOUNT, BigDecimal.class),
				actual.getProperty(ElasticPathConstants.AMOUNT, BigDecimal.class));
	}


	/**
	 * Test case to test {@link ElasticPathCommerceSessionImpl#getAvailableShippingMethods()} API of commerce for non availability of shipping
	 * methods.
	 * 
	 * @throws CommerceException commerceException
	 */
	@Test
	public void testNoAvailableShippingMethods() throws CommerceException {
		List<ShippingMethod> methods = Lists.newArrayList();
		when(orderService.getCortexOrderAvailableShippingMethods(any(Locale.class))).thenReturn(methods);

		List<ShippingMethod> shippingMethods = getElasticPathCommerceSessionImpl().getAvailableShippingMethods();
		assertNotNull(shippingMethods);
		assertEquals(0, shippingMethods.size());
	}

	/**
	 * Test case to test {@link ElasticPathCommerceSessionImpl#updateOrder(Map<String, Object>)} API of commerce.
	 * 
	 * @throws IllegalArgumentException illegalArgumentException
	 * @throws SecurityException securityException
	 * @throws CommerceException commerceException
	 */
	@Test
	public void testUpdateOrder() throws CommerceException {
		Map<String, Object> orderDetails = new HashMap<>();
		orderDetails.put(CommerceConstants.SHIPPING_OPTION, TEST_SHIPPING_METHOD_CHOICE);
		OrderConfigurationService orderConfigurationService = mock(OrderConfigurationService.class);
		when(cortexSdkServiceFactory.getOrderConfigurationService()).thenReturn(orderConfigurationService);
		doNothing().when(orderConfigurationService).updateAddresses(orderDetails);

		getElasticPathCommerceSessionImpl().updateOrder(orderDetails);
	}
}
