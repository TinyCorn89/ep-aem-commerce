package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Cart Total.
 */
public class ShipmentList {

	@JsonProperty("_element")
	private Iterable<Shipment> shipments;

	public Shipment getFirstShipment() {
		return shipments.iterator().next();
	}
}
