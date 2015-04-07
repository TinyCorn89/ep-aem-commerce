package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A shipment.
 */
public class Shipment extends Linkable {

	@JsonProperty("_destination")
	private Iterable<AddressEntry> shippingAddressEntry;

	@JsonProperty("_shippingoption")
	private Iterable<ShippingOption> shippingOptions;

	@JsonProperty("status")
	private ShippingStatus shippingStatus;


	public AddressEntry getFirstShippingAddressEntry() {
		return shippingAddressEntry.iterator().next();
	}

	public ShippingOption getFirstShippingOption() {
		return shippingOptions.iterator().next();
	}
	
	public Iterable<ShippingOption> getShippingOptions() {
		return shippingOptions;
	}

	public String getShippingStatusCode() {
		return shippingStatus.getCode();
	}
}
