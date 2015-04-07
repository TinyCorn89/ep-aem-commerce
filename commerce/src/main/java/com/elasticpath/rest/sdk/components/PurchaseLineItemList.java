package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * PurchaseLineItems.
 */
public class PurchaseLineItemList {

	@JsonProperty("_element")
	private Iterable<PurchaseLineItem> purchaseLineItems;

	public PurchaseLineItem getPurchaseLineItem() {
		return purchaseLineItems.iterator().next();
	}
	
	public Iterable<PurchaseLineItem> getPurchaseLineItems() {
		return purchaseLineItems;
	}
}
