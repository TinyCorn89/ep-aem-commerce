/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.api.promotion.PromotionInfo;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.aem.commerce.util.ElasticPathCommerceUtil;
import com.elasticpath.rest.sdk.service.OrderService;

/**
 * Tests {@link com.elasticpath.aem.commerce.impl.ElasticPathCommerceSessionImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ElasticPathCommerceSessionCartAndPromotionsTest extends BaseElasticPathCommerceSessionTest {

	@Mock
	private CommerceSession.CartEntry lineItem;
	
	@Before
	public void setUp() throws CommerceException, IllegalAccessException {
		super.setUp();

		mockCartWithTotals();
	}

	@Test
	public void testCartEntriesAreReturnedCorrectly() {

		when(lineItem.getQuantity()).thenReturn(1);

		List<CommerceSession.CartEntry> expectedCartEntries = Arrays.asList(lineItem);
		Mockito.doReturn(expectedCartEntries).when(cart).getLineItems();

		List<?> cartEntries = getElasticPathCommerceSessionImpl().getCartEntries();

		assertEquals(expectedCartEntries, cartEntries);
	}

	@Test
	public void testNoCartEntriesReturnedForEmptyCart() {
		Mockito.doReturn(Collections.<CommerceSession.CartEntry>emptyList()).when(cart).getLineItems();

		List<CommerceSession.CartEntry> cartEntries = getElasticPathCommerceSessionImpl().getCartEntries();

		assertTrue(cartEntries.isEmpty());
	}

	@Test
	public void testCartPricesCanBeRetrievedCorrectly() throws CommerceException {
		List<PriceInfo> expectedPrices = new ArrayList<>();
		expectedPrices.add(ElasticPathCommerceUtil.addPriceTypes(CART_SUBTOTAL_PRICE_INFO, "PRE_TAX"));
		expectedPrices.add(ElasticPathCommerceUtil.addPriceTypes(CART_DISCOUNT_TOTAL_PRICE_INFO, "ORDER", "SUB_TOTAL"));
		expectedPrices.add(ElasticPathCommerceUtil.addPriceTypes(
				new PriceInfo(BigDecimal.ZERO, RESOURCE_LOCALE, RESOURCE_CURRENCY), "DISCOUNT"));

		when(cart.getAllCartPriceInfo(RESOURCE_LOCALE)).thenReturn(expectedPrices);

		List<PriceInfo> cartPrices = getElasticPathCommerceSessionImpl().getCartPriceInfo(null);

		assertEquals(expectedPrices.toString(), cartPrices.toString());
	}

	@Test
	public void testPromotionCanBeRetrievedCorrectly() throws CommerceException {
		PromotionInfo mockPromotionInfo = mock(PromotionInfo.class);
		List<PromotionInfo> expectedPromotions = Arrays.asList(mockPromotionInfo);
		when(cart.getLineItemPromotions()).thenReturn(expectedPromotions);

		mockNoShippingPromosApplied();

		List<PromotionInfo> promotions = getElasticPathCommerceSessionImpl().getPromotions();

		assertEquals(expectedPromotions.size(), promotions.size());
		assertEquals(expectedPromotions.get(0).getPath(), promotions.get(0).getPath());
	}

	private void mockNoShippingPromosApplied() {
		OrderService orderService = mock(OrderService.class);
		when(cortexSdkServiceFactory.getOrderService()).thenReturn(orderService);
		when(orderService.getCortexOrderShippingPromotions(any(Locale.class))).thenReturn(new ArrayList<PromotionInfo>());
	}

	@Test
	public void testNoPromotionsReturnedWhenThereAreNoLineItemPromotions() throws CommerceException {
		when(cart.getAppliedPromotions()).thenReturn(Collections.<PromotionInfo>emptyList());

		mockNoShippingPromosApplied();

		List<PromotionInfo> promotions = getElasticPathCommerceSessionImpl().getPromotions();

		assertTrue(promotions.isEmpty());
	}

	@Test
	public void testShippingPromotionsAreReturnedCorrectly() throws CommerceException {
		Mockito.doReturn(Arrays.asList(lineItem)).when(cart).getLineItems();
		PromotionInfo mockShippingOptionPromotion = mock(PromotionInfo.class);
		OrderService orderService = mock(OrderService.class);
		when(cortexSdkServiceFactory.getOrderService()).thenReturn(orderService);
		when(cart.getTotalQuantity()).thenReturn(1);
		when(orderService.getCortexOrderShippingPromotions(any(Locale.class))).thenReturn(Arrays.asList(mockShippingOptionPromotion));

		List<PromotionInfo> promotions = getElasticPathCommerceSessionImpl().getPromotions();

		assertEquals(1, promotions.size());
		assertTrue(promotions.containsAll(Arrays.asList(mockShippingOptionPromotion)));
	}

	@Test
	public void testLineItemCanModifiedCorrectly() throws CommerceException {

		getElasticPathCommerceSessionImpl().modifyCartEntry(0, 1);

		verify(cartService, times(1)).modifyCartEntry(0, 1, RESOURCE_LOCALE);
	}

	@Test
	public void testLineItemCanBeDeletedCorrectly() throws CommerceException {

		getElasticPathCommerceSessionImpl().deleteCartEntry(0);

		verify(cartService, times(1)).deleteCartEntry(0, RESOURCE_LOCALE);
	}

	@Test
	public void testAddingLineItemToCart() throws CommerceException {
		Product product = mock(Product.class);

		getElasticPathCommerceSessionImpl().addCartEntry(product, 1);

		verify(cartService, times(1)).addCartEntry(any(Product.class), anyInt());
	}

	@Test(expected = CommerceException.class)
	public void testAddingLineItemFailureThrowsCommerceException() throws CommerceException {
		Product product = mock(Product.class);
		doThrow(new CommerceException("test")).when(cartService).addCartEntry(any(Product.class), anyInt());
		getElasticPathCommerceSessionImpl().addCartEntry(product, 1);
	}
}