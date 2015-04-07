/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.service;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.api.ShippingMethod;
import com.adobe.cq.commerce.api.promotion.PromotionInfo;

/**
 * Order Service is responsible for the order and ensuring it is consistent between aem and cortex.
 */
public interface OrderService {
	/**
	 * Load the order from cortex for this request.
	 * @param userLocale The locale.
	 * @return Order pojo.
	 */
	Map<String, String> getCortexOrderProperties(Locale userLocale);

	/**
	 * Provides a list of prices (with price filters) that this implementation understands.
	 *
	 * @param userLocale The locale.
	 * @return list of prices available for this order.
	 */
	List<PriceInfo> getOrderPrices(Locale userLocale);

	/**
	 * Place the current order with Cortex given the supplied payment data.
	 *
	 * @param paymentData the payment data captured from the AEM storefront.
	 * @return the Cortex purchase number for the completed order.
	 */
	String placeCortexOrder(Map<String, Object> paymentData);

	/**
	 * Invalidates the order.
	 */
	void invalidateOrder();

	/**
	 * Gets the applied shipping promotions information.
	 * @param userLocale The locale.
	 * @return The applied shipping promotions.
	 */
	Collection<PromotionInfo> getCortexOrderShippingPromotions(Locale userLocale);

	/**
	 * Gets the cortex shipping options.
	 * @param userLocale The locale.
	 * @return The shipping options.
	 */
	List<ShippingMethod> getCortexOrderAvailableShippingMethods(Locale userLocale);
}
