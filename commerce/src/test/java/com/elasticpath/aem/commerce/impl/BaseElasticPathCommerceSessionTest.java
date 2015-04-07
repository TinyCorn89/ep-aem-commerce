/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

import org.junit.Before;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.common.ServiceContext;
import com.day.cq.commons.Language;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.support.membermodification.MemberModifier;

import com.elasticpath.aem.commerce.AbstractElasticPathCommerceService;
import com.elasticpath.rest.sdk.Cart;
import com.elasticpath.rest.sdk.CortexSdkServiceFactory;
import com.elasticpath.rest.sdk.components.ShippingOption;
import com.elasticpath.rest.sdk.service.CartService;
import com.elasticpath.rest.sdk.service.OrderService;
import com.elasticpath.rest.sdk.service.PromotionService;
import com.elasticpath.rest.sdk.service.PurchaseService;

/**
 * Base test class for {@link ElasticPathCommerceSessionImpl}. This includes common setups for all ElasticPathCommerceSessionImpl related test
 * classes.
 */
public class BaseElasticPathCommerceSessionTest {

	/** SERVICE_CONTEXT_LANGUAGE_MANAGER. */
	static final String SERVICE_CONTEXT_LANGUAGE_MANAGER = "languageManager";

	/** RESOURCE_LOCALE. */
	static final Locale RESOURCE_LOCALE = Locale.US;

	static final Currency RESOURCE_CURRENCY = Currency.getInstance("USD");

	/** TEST_CORTEX_SCOPE. */
	static final String TEST_CORTEX_SCOPE = "geometrixx";

	static final BigDecimal CART_SUBTOTAL = new BigDecimal("10.00");
	static final PriceInfo CART_SUBTOTAL_PRICE_INFO = new PriceInfo(CART_SUBTOTAL, RESOURCE_LOCALE, RESOURCE_CURRENCY);
	static final PriceInfo CART_DISCOUNT_TOTAL_PRICE_INFO = CART_SUBTOTAL_PRICE_INFO;

	static final PriceInfo SHIPPING_PRICE_INFO = CART_SUBTOTAL_PRICE_INFO;

	/**
	 * Instance of ElasticPathCommerceSessionImpl.
	 */
	private ElasticPathCommerceSessionImpl elasticPathCommerceSessionImpl;

	@Mock
	AbstractElasticPathCommerceService epCommerceService;

	@Mock
	CortexSdkServiceFactory cortexSdkServiceFactory;
//
//	@Mock
//	Order order;

	@Mock
	Cart cart;

	@Mock
	ShippingOption shippingOption;
//
//	@Mock
//	AvailableShippingMethods shippingOptions;
//
//	@Mock
//	AppliedShippingPromotions orderShipping;

	@Mock
	CartService cartService;

	@Mock
	OrderService orderService;

	@Mock
	PromotionService promotionService;

	@Mock
	PurchaseService purchaseService;

	/**
	 * Mock Setup method for {@link ElasticPathCommerceSessionImpl}.
	 *
	 * @throws IllegalAccessException IllegalAccessException
	 * @throws IllegalArgumentException IllegalArgumentException
	 * @throws CommerceException CommerceException
	 */
	@Before
	public void setUp() throws IllegalArgumentException, IllegalAccessException, CommerceException {
		initMocks(this);
		Resource resource = mock(Resource.class, Mockito.RETURNS_DEEP_STUBS);

		ServiceContext serviceContext = mock(ServiceContext.class);
		LanguageManager languageManager = mock(LanguageManager.class);
		MemberModifier.field(ServiceContext.class, SERVICE_CONTEXT_LANGUAGE_MANAGER).set(serviceContext, languageManager);
		when(epCommerceService.serviceContext()).thenReturn(serviceContext);
		when(epCommerceService.getCortexScope()).thenReturn(TEST_CORTEX_SCOPE);
		Language resourceLanguage = mock(Language.class);
		when(languageManager.getCqLanguage(resource)).thenReturn(resourceLanguage);
		when(resourceLanguage.getCountryCode()).thenReturn(RESOURCE_LOCALE.getCountry());
		when(resourceLanguage.getLanguageCode()).thenReturn(RESOURCE_LOCALE.getLanguage());
		when(resourceLanguage.getLocale()).thenReturn(RESOURCE_LOCALE);

		PageManager pageManager = mock(PageManager.class);
		ResourceResolver resourceResolver = mock(ResourceResolver.class);
		when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
		when(resource.getResourceResolver()).thenReturn(resourceResolver);
		when(pageManager.getContainingPage(resource)).thenReturn(mock(Page.class));
		when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);

		when(cortexSdkServiceFactory.getCartService()).thenReturn(cartService);
		when(cartService.getCortexCart(any(Locale.class)))
				.thenReturn(cart);
		when(cortexSdkServiceFactory.getOrderService()).thenReturn(orderService);
		when(orderService.getCortexOrderProperties(any(Locale.class))).thenReturn(new HashMap<String, String>());

		when(cortexSdkServiceFactory.getPromotionService()).thenReturn(promotionService);
		when(cortexSdkServiceFactory.getPurchaseService()).thenReturn(purchaseService);

		elasticPathCommerceSessionImpl = new ElasticPathCommerceSessionImpl(epCommerceService, mock(SlingHttpServletRequest.class),
				mock(SlingHttpServletResponse.class), resource,	cortexSdkServiceFactory);
		elasticPathCommerceSessionImpl.setUserLocale(Locale.US);


	}

	/**
	 * Gets elasticPathCommerceSessionImpl.
	 *
	 * @return ElasticPathCommerceSessionImpl.
	 */
	public ElasticPathCommerceSessionImpl getElasticPathCommerceSessionImpl() {
		return elasticPathCommerceSessionImpl;
	}


	void mockCartWithTotals() {
		when(cart.getSubTotal(RESOURCE_LOCALE)).thenReturn(CART_SUBTOTAL_PRICE_INFO);
		when(cart.getDiscountedTotal(RESOURCE_LOCALE)).thenReturn(CART_DISCOUNT_TOTAL_PRICE_INFO);
		when(shippingOption.getShippingPrice(RESOURCE_LOCALE)).thenReturn(SHIPPING_PRICE_INFO);
	}
}
