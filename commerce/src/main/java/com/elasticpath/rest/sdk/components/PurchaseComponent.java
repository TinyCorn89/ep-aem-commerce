package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Purchase component.
 */
public class PurchaseComponent extends Linkable {

	@JsonProperty("monetary-total")
	private Iterable<Price> total;

	@JsonProperty("tax-total")
	private Price tax;

	@JsonProperty("purchase-number")
	private String purchaseNumber;

	@JsonProperty("status")
	private String status;

	@JsonProperty("_billingaddress")
	private Iterable<AddressEntry> billingAddresses;

	@JsonProperty("_appliedpromotions")
	private Iterable<AppliedPromotions> appliedPromotions;

	@JsonProperty("_shipments")
	private Iterable<ShipmentList> shipmentsList;

	@JsonProperty("purchase-date")
	private DateComponent purchaseDate;

	@JsonProperty("_lineitems")
	private Iterable<PurchaseLineItemList> lineItems;

	@JsonProperty("_coupons")
	private Iterable<CouponList> coupons;


	public Price getFirstTotal() {
		return total.iterator().next();
	}

	public Iterable<Price> getTotal() {
		return total;
	}

	public Price getTax() {
		return tax;
	}

	public String getPurchaseNumber() {
		return purchaseNumber;
	}

	public String getStatus() {
		return status;
	}

	public AddressEntry getFirstBillingAddress() {
		return billingAddresses.iterator().next();
	}

	public AppliedPromotions getFirstAppliedPromotions() {
		return appliedPromotions.iterator().next();
	}

	public Iterable<AppliedPromotions> getAppliedPromotions() {
		return appliedPromotions;
	}

	public ShipmentList getFirstShipmentList() {
		return shipmentsList.iterator().next();
	}

	public String getPurchaseDate() {
		return purchaseDate.getDisplayValue();
	}

	public PurchaseLineItemList getPurchaseLineItemList() {
		return lineItems.iterator().next();
	}

	public CouponList getCoupons() {
		return coupons.iterator().next();
	}
}
