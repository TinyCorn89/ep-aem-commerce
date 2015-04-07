/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.address.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.aem.address.constants.ElasticPathAddressConstants;

/**
 * This helper class provides address details.
 */
public class AddressDetailBuilder {

	/** The profile. */
	private Map<String, Object> profile;

	/** The address. */
	private Map<String, Object> address;

	/**
	 * Create a new {@code AddressDetailBuilder} instance.
	 *
	 * @return AddressDetailBuilder
	 */
	public static AddressDetailBuilder newBuilder() {
		return new AddressDetailBuilder();
	}
	
	/**
	 * This initializes the profile map.
	 * 
	 * @param properties the Map<String, Object>
	 * @return Builder
	 */
	public AddressDetailBuilder withProfile(final Map<String, Object> properties) {
		this.profile = new HashMap<>();
		profile.put(ElasticPathAddressConstants.FAMILY_NAME, properties.get(ElasticPathAddressConstants.LAST_NAME));
		profile.put(ElasticPathAddressConstants.GIVEN_NAME, properties.get(ElasticPathAddressConstants.FIRST_NAME));
		return this;
	}

	/**
	 * This initializes the address map.
	 * 
	 * @param properties the Map<String, Object>
	 * @return Builder
	 */
	public AddressDetailBuilder withAddress(final Map<String, Object> properties) {
		this.address = new HashMap<>();
		address.put(ElasticPathAddressConstants.COUNTRY_NAME, properties.get(ElasticPathAddressConstants.COUNTRY));
		address.put(ElasticPathAddressConstants.LOCALITY, properties.get(ElasticPathAddressConstants.CITY));
		address.put(ElasticPathAddressConstants.EXT_ADDRESS, properties.get(ElasticPathAddressConstants.STREET2));
		address.put(ElasticPathAddressConstants.POSTALCODE, properties.get(ElasticPathAddressConstants.ZIP));

		if (properties.containsKey(ElasticPathAddressConstants.STATE)) {
			address.put(ElasticPathAddressConstants.REGION, properties.get(ElasticPathAddressConstants.STATE));
		} else {
			address.put(ElasticPathAddressConstants.REGION, "");
		}

		address.put(ElasticPathAddressConstants.STREET_ADDRESS, properties.get(ElasticPathAddressConstants.STREET1));
		return this;
	}
	
	/**
	 * Build a new address details Map instance using all the configuration previously specified
     * in this ep address details.
	 *
	 * @return Map<String, Object>
	 */
	public Map<String, Object> build() {
		final Map<String, Object> addressDetailsMap = new HashMap<>();
		addressDetailsMap.put(ElasticPathAddressConstants.ADDRESS, address);
		addressDetailsMap.put(ElasticPathAddressConstants.NAME, profile);
		return addressDetailsMap;
	}
}
