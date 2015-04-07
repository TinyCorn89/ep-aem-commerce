/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.service;

import java.util.List;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.PlacedOrder;

import com.elasticpath.aem.commerce.ElasticPathCommerceSession;

/**
 * Purchase Service is responsible for retrieving completed purchase information from Cortex, or PlacedOrders in AEM speak.
 */
public interface PurchaseService {

	/**
	 * Load the order from cortex for this request.
	 * @param commerceSession the session the Placed order if for.
	 * @param predicate the filter
	 * @return List of PlacedOrders for this session using the provided filter.
	 */
	List<PlacedOrder> getPlacedOrders(ElasticPathCommerceSession commerceSession, String predicate);

	/**
	 * Load the order from cortex for this request.
	 * @param orderId the order Id.
	 * @param commerceSession the session the Placed order if for.
	 * @return PurchaseView pojo.
	 * @throws CommerceException if the purchase was not found.
	 */
	PlacedOrder getPlacedOrder(String orderId, ElasticPathCommerceSession commerceSession) throws CommerceException;
}
