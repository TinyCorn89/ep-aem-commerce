/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.sdk.service;

import java.util.List;
import java.util.Map;

import com.adobe.cq.address.api.Address;
import com.adobe.cq.address.api.AddressException;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * Service for performing address operations.
 */
public interface AddressService {

	/**
	 * Load an address with the given path.
	 * @param resourceResolver jcr resourceResolver
	 * @param addressPath location of address.
	 * @return the retrieved address.
	 * @throws AddressException if no address can be found.
	 */
	Address getAddress(ResourceResolver resourceResolver, String addressPath) throws AddressException;

	/**
	 * Load all available addresses.
	 * @param resourceResolver jcr resourceResolver
	 * @return List of addresses.
	 * @throws AddressException if there are no addresses.
	 */
	List<Address> getAddressList(ResourceResolver resourceResolver) throws AddressException;

	/**
	 * Create a new address.
	 * @param resourceResolver jcr resourceResolver
	 * @param properties address properties.
	 * @return the new Address.
	 * @throws AddressException if an address cannot be created.
	 */
	Address createAddress(ResourceResolver resourceResolver, Map<String, Object> properties) throws AddressException;

	/**
	 * Update an existing address.
	 * @param resourceResolver jcr resourceResolver.
	 * @param address the existing address.
	 * @param properties properties to update.
	 * @return the updated address.
	 * @throws AddressException if the address cannot be updated.
	 */
	Address updateAddress(ResourceResolver resourceResolver, Address address, Map<String, Object> properties) throws AddressException;

	/**
	 * Removed the address at the specified location.
	 * @param path address location.
	 */
	void delete(String path);

	/**
	 * Load the default billing address if one exists.
	 * @param resourceResolver jcr resourceResolver.
	 * @return default address or null if no address can be found.
	 * @throws AddressException if an error occurs.
	 */
	Address getDefaultBillingAddress(ResourceResolver resourceResolver) throws AddressException;

	/**
	 * Load the default shipping address if one exists.
	 * @param resourceResolver jcr resourceResolver.
	 * @return default address or null if no address can be found.
	 * @throws AddressException if an error occurs.
	 */
	Address getDefaultShippingAddress(ResourceResolver resourceResolver) throws AddressException;
}
