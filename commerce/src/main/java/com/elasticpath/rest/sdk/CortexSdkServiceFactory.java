/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.sdk;

import com.elasticpath.rest.sdk.service.AddressService;
import com.elasticpath.rest.sdk.service.CartService;
import com.elasticpath.rest.sdk.service.GeographyService;
import com.elasticpath.rest.sdk.service.LogoutService;
import com.elasticpath.rest.sdk.service.OrderConfigurationService;
import com.elasticpath.rest.sdk.service.OrderService;
import com.elasticpath.rest.sdk.service.ProductService;
import com.elasticpath.rest.sdk.service.PromotionService;
import com.elasticpath.rest.sdk.service.PurchaseService;

/**
 * A factory for creating objects to perform different Cortex commerce calls using the Cortex-JaxRs-Client.
 */
public interface CortexSdkServiceFactory {
	/**
	 * Gets the CortexCartService.
	 * @return the cart service.
	 */
	CartService getCartService();

	/**
	 * Get the Cortex Order Processor service.
	 * @return the order processor.
	 */
	OrderService getOrderService();

	/**
	 * Get the Cortex Order Address Management service.
	 * @return the address management service.
	 */
	OrderConfigurationService getOrderConfigurationService();

	/**
	 * Gets the Cortex promotion service.
	 * @return The promotion service.
	 */
	PromotionService getPromotionService();

	/**
	 * Gets the Cortex product service.
	 * @return The product service.
	 */
	ProductService getProductService();

	/**
	 * Gets the Cortex geography service.
	 * @return the Cortex geography service.
	 */
	GeographyService getGeographyService();

	/**
	 * Gets the Cortex logout Service.
	 * @return the Cortex logout service.
	 */
	LogoutService getLogoutService();

	/**
	 * Gets the Cortex purchase Service.
	 * @return the Cortex purchase service.
	 */
	PurchaseService getPurchaseService();

	/**
	 * Gets teh Cortex Address Service.
	 * @return the Cortex Address Service.
	 */
	AddressService getAddressService();
}
