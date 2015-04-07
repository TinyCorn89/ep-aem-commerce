/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.address.impl;

import java.util.HashMap;
import java.util.Map;

import com.adobe.cq.address.api.Address;
import com.adobe.granite.ui.components.ds.ValueMapResource;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

import com.elasticpath.aem.address.constants.ElasticPathAddressConstants;
import com.elasticpath.rest.sdk.components.AddressEntry;
import com.elasticpath.rest.sdk.components.Name;

/**
 * This helper class provides implementations related to address.
 */
class AddressBuilder {

	private ResourceResolver resourceResolver;
	private AddressEntry addressEntry;
	private Map<String, Object> addressDetails;
	private String uri;

	/**
	 * Create a new {@code AddressBuilder} instance.
	 *
	 * @return AddressBuilder
	 */
	static AddressBuilder newBuilder() {
		return new AddressBuilder();
	}
	
	/**
	 * This initializes the resourceResolver variable.
	 * 
	 * @param resourceResolver the ResourceResolver
	 * @return AddressBuilder
	 */
	AddressBuilder withResourceResolver(final ResourceResolver resourceResolver) {
		this.resourceResolver = resourceResolver;
		return this;
	}

	/**
	 * This initializes the zoomedAddress variable.
	 * 
	 * @param addressEntry the ZoomedAddress
	 * @return AddressBuilder
	 */
	AddressBuilder withAddressEntry(final AddressEntry addressEntry) {
		this.addressEntry = addressEntry;
		return this;
	}

	/**
	 * Set the address details to use to construct this address.
	 *
	 * @param properties addressDetails.
	 * @return this builder
	 */
	AddressBuilder withDetails(final Map<String, Object> properties) {
		this.addressDetails = properties;
		return this;
	}

	/**
	 * Set the address path.
	 * @param uri cortex uri of resource to be used as path.
	 * @return this builder.
	 */
	AddressBuilder withURI(final String uri) {
		this.uri = uri;
		return this;
	}
	
	/**
	 * Build a new Address instance using all the configuration previously specified
     * in this ep address.
	 *
	 * @return Map<String, Object>
	 */
	Address build() {
		ValueMapResource addressResource;

		if (addressEntry == null) {
			addressResource = new ValueMapResource(resourceResolver, uri, "", new ValueMapDecorator(addressDetails));
		} else {
			addressResource = new ValueMapResource(resourceResolver, addressEntry.getSelf().getUri(), "", getAddressDetailsMap());
		}

		return new Address(addressResource);
	}

	private ValueMap getAddressDetailsMap() {
		final Map<String, Object> addressdetails = new HashMap<>();
		addressdetails.put(ElasticPathAddressConstants.PATH, addressEntry.getSelf().getUri());
		Name name = addressEntry.getName();
		addressdetails.put(ElasticPathAddressConstants.FIRST_NAME, name.getGivenName());
		addressdetails.put(ElasticPathAddressConstants.LAST_NAME, name.getFamilyName());

		com.elasticpath.rest.sdk.components.Address address = addressEntry.getAddress();
		addressdetails.put(ElasticPathAddressConstants.STREET1, address.getStreetAddress());
		addressdetails.put(ElasticPathAddressConstants.STREET2, address.getExtendedAddress());
		addressdetails.put(ElasticPathAddressConstants.CITY, address.getLocality());
		addressdetails.put(ElasticPathAddressConstants.STATE, address.getRegion());
		addressdetails.put(ElasticPathAddressConstants.COUNTRY, address.getCountryName());
		addressdetails.put(ElasticPathAddressConstants.ZIP, address.getPostalCode());
		return new ValueMapDecorator(addressdetails);
	}
}
