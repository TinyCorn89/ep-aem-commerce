/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.service;

import java.util.Map;

import com.adobe.cq.commerce.api.CommerceException;

import org.apache.sling.api.SlingHttpServletRequest;

/**
 * Order Service is responsible for the order and ensuring it is consistent between aem and cortex.
 */
public interface OrderConfigurationService {


	/**
	 * update the billing address and the shipping address on the order.
	 * @param delta update order delta.
	 * @throws com.adobe.cq.commerce.api.CommerceException if there is an error updating the address.
	 */
	void updateAddresses(Map<String, Object> delta) throws CommerceException;

	/**
	 * update the order with the users email address for guest checkout.
	 * @param request request to update the order for.
	 * @param delta update order delta.
	 * @throws CommerceException if there is an error updating the email.
	 */
	void updateOrderEmailIfGuest(SlingHttpServletRequest request, Map<String, Object> delta) throws CommerceException;

	/**
	 * update the shipping option used on the order.
	 * @param delta update order delta.
	 * @throws CommerceException if there is an error updating the shipping method.
	 */
	void updateShippingOption(Map<String, Object> delta) throws CommerceException;

}
