/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.address.impl;

import static com.elasticpath.aem.commerce.constants.ElasticPathConstants.REQUEST_URI;

import java.util.Collection;
import java.util.Map;

import com.adobe.cq.address.api.Address;
import com.adobe.cq.address.api.AddressException;
import com.adobe.cq.address.api.AddressProvider;
import com.adobe.cq.commerce.api.CommerceConstants;
import com.adobe.cq.commerce.api.CommerceService;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;

import com.elasticpath.aem.commerce.ElasticPathCommerceService;
import com.elasticpath.aem.commerce.cortex.CortexContext;
import com.elasticpath.aem.cookies.AemCortexContextManager;
import com.elasticpath.rest.client.CortexClient;
import com.elasticpath.rest.client.CortexClientFactory;
import com.elasticpath.rest.sdk.CortexSdkServiceFactory;
import com.elasticpath.rest.sdk.service.AddressService;

/**
 * ElasticPathAddressProviderImpl for Address APIs.
 */
@Component
@Service({ AddressProvider.class })
@Properties({
	@Property(name = "service.description", value = { "ElasticPath Address Provider Service" }),
	@Property(name = "service.ranking", intValue = Integer.MAX_VALUE) })
public class ElasticPathAddressProviderImpl implements AddressProvider {

	@Reference
	private AemCortexContextManager contextManager;

	@Reference
	private CortexClientFactory cortexClientFactory;

	/**
	 * Adds the Address in user's profile in cortex.
	 *
	 * @param resourceResolver ResourceResolver
	 * @param userID String
	 * @param properties Map<String, Object>
	 * @param autoSave boolean
	 * @return Address
	 * @throws AddressException AddressException
	 */
	public Address addAddress(final ResourceResolver resourceResolver, final String userID, final Map<String, Object> properties,
			final boolean autoSave) throws AddressException {
		final AddressService addressService = getAddressService(resourceResolver, userID);

		return addressService.createAddress(resourceResolver, properties);
	}

	/**
	 * Returns the Address corresponding to addressPath from cortex.
	 *
	 * @param resourceResolver ResourceResolver
	 * @param userID String
	 * @param addressPath String
	 * @return Address
	 */
	public Address getAddress(final ResourceResolver resourceResolver, final String userID, final String addressPath) {
		final AddressService addressService = getAddressService(resourceResolver, userID);

		try {
			return addressService.getAddress(resourceResolver, addressPath);
		} catch (AddressException e) {
			throw new IllegalStateException("Wrapping Correct Exception as method does not handle it correctly", e);
		}
	}

	/**
	 * Returns the list of Addresses from user's profile in cortex.
	 *
	 * @param resourceResolver ResourceResolver
	 * @param userID String
	 * @return Collection<Address>
	 */
	public Collection<Address> getAddresses(final ResourceResolver resourceResolver, final String userID) {
		final AddressService addressService = getAddressService(resourceResolver, userID);

		try {
			return addressService.getAddressList(resourceResolver);
		} catch (AddressException e) {
			throw new IllegalStateException("Wrapping Correct Exception as method does not handle it correctly", e);
		}
	}

	/**
	 * Returns the default address from user's profile. We are not implementing this API as cortex doesn't support setting default address.
	 *
	 * @param resourceResolver ResourceResolver
	 * @param userID String
	 * @return Address
	 */
	public Address getDefaultAddress(final ResourceResolver resourceResolver, final String userID) {
		// TODO : Implement Default address API once default address is available in profile. Currently default address is only available in billing
		// or shipping address.
		return null;
	}

	/**
	 * Returns the default address corresponding to address type from user's profile. We are not implementing this API as cortex doesn't support
	 * setting default address.
	 *
	 * @param resourceResolver ResourceResolver
	 * @param userID String
	 * @param type String
	 * @return Address
	 */
	public Address getDefaultAddress(final ResourceResolver resourceResolver, final String userID, final String type) {
		final AddressService addressService = getAddressService(resourceResolver, userID);

		try {
			if (CommerceConstants.BILLING_ADDRESS_PREDICATE.equalsIgnoreCase(type)) {
				return addressService.getDefaultBillingAddress(resourceResolver);
			} else { // If a default address is requested, but the type is not specified, return the default shipping address.
				return addressService.getDefaultShippingAddress(resourceResolver);
			}
		} catch (AddressException ae) {
			/* When no default address can be found return null, this was existing behaviour, I doubt it is handled uniformly
			* by api callers but it could at least be valid not to have a default address. */
			return null;
		}
	}

	/**
	 * Removes the address from user's profile in cortex.
	 *
	 * @param resourceResolver ResourceResolver
	 * @param userID String
	 * @param address Address
	 * @param autoSave boolean
	 * @throws AddressException AddressException
	 */
	public void removeAddress(final ResourceResolver resourceResolver, final String userID, final Address address, final boolean autoSave)
			throws AddressException {

		final AddressService addressService = getAddressService(resourceResolver, userID);
		addressService.delete(address.getPath());
	}

	/**
	 * Sets the default address corresponding in user's profile. We are not implementing this API as cortex doesn't support setting default address.
	 *
	 * @param resourceResolver ResourceResolver
	 * @param userID String
	 * @param address Address
	 * @param autoSave boolean
	 * @throws AddressException AddressException
	 */
	public void setDefaultAddress(final ResourceResolver resourceResolver, final String userID, final Address address, final boolean autoSave)
			throws AddressException {
		//TODO : Implement Default address API once set default address is available in cortex.
		return;
	}

	/**
	 * Sets the default address corresponding to address type in user's profile. We are not implementing this API as cortex doesn't support setting
	 * default address.
	 *
	 * @param resourceResolver ResourceResolver
	 * @param userID String
	 * @param address Address
	 * @param type String
	 * @param autoSave boolean
	 * @throws AddressException AddressException
	 */
	public void setDefaultAddress(final ResourceResolver resourceResolver, final String userID, final Address address, final String type,
			final boolean autoSave) throws AddressException {
		//TODO : Implement Default address API once set default address is available in cortex.
		return;
	}

	/**
	 * Update the Address in user's profile in cortex.
	 *
	 * @param resourceResolver ResourceResolver
	 * @param userID String
	 * @param address Address
	 * @param properties Map<String, Object>
	 * @param autoSave boolean
	 * @return Address address
	 * @throws AddressException AddressException
	 */
	public Address updateAddress(final ResourceResolver resourceResolver, final String userID, final Address address,
			final Map<String, Object> properties, final boolean autoSave) throws AddressException {
		final AddressService addressService = getAddressService(resourceResolver, userID);

		return addressService.updateAddress(resourceResolver, address, properties);
	}

	private AddressService getAddressService(final ResourceResolver resourceResolver, final String userID) {
		final String resourcePath = (String) resourceResolver.getAttribute(REQUEST_URI);
		final CommerceService commerceService = resourceResolver.resolve(resourcePath).adaptTo(CommerceService.class);

		if (commerceService instanceof ElasticPathCommerceService) {
			ElasticPathCommerceService epCommerceService = (ElasticPathCommerceService) commerceService;
			CortexSdkServiceFactory factory = epCommerceService.getServiceFactory(
					getCortexClient(
							resourceResolver,
							epCommerceService.getCortexScope(),
							userID)
			);

			return factory.getAddressService();
		} else {
			throw new IllegalStateException("This address provider only supports ElasticPallCommerceService commerce-provider types");
		}
	}

	private CortexClient getCortexClient(final ResourceResolver resourceResolver, final String cortexScope, final String userID) {
		CortexContext cortexContext = contextManager.retrieveContext(resourceResolver, userID);
		return cortexClientFactory.create(cortexContext.getAuthenticationToken(), cortexScope);
	}
}