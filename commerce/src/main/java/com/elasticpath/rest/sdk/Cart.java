/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.sdk;

import java.util.List;
import java.util.Locale;

import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.api.promotion.PromotionInfo;

/**
 * Cart Representation, this facilitates caching of the cart during a request.
 */
public interface Cart {
	/**
	 * The uri to the order resource.
	 * @return uri string.
	 */
	String getOrderUri();

	/**
	 * Cart line items.
	 * @return List of line items.
	 */
	List<? extends CommerceSession.CartEntry> getLineItems();

	/**
	 * Promotions applied to cart line items.
	 * @return List of line item promotions.
	 */
	List<PromotionInfo> getLineItemPromotions();

	/**
	 * Cart id.
	 * @return id.
	 */
	String getId();

	/**
	 * Cart sub total.
	 * @param locale user locale.
	 * @return PriceInfo with sub total price filter.
	 */
	PriceInfo getSubTotal(Locale locale);

	/**
	 * Cart discounted total.
	 * @param locale user locale.
	 * @return PriceInfo with discounted total price filter.
	 */
	PriceInfo getDiscountedTotal(Locale locale);

	/**
	 * Promotions applied to the cart.
	 * @return Collection of PromotionInfo.
	 */
	List<PromotionInfo> getAppliedPromotions();

	/**
	 * Get all prices available on the cart.
	 * @param userLocale user locale.
	 * @return List of all cart prices.
	 */
	List<PriceInfo> getAllCartPriceInfo(Locale userLocale);

	/**
	 * Number of items in the cart.
	 * @return items in the cart.
	 */
	int getTotalQuantity();
}
