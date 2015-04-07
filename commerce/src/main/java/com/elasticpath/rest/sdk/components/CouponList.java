package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * CouponList.
 */
public class CouponList {

	@JsonProperty("_element")
	private Iterable<Coupon> coupons;

	public Coupon getCoupon() {
		return coupons.iterator().next();
	}
	
	public Iterable<Coupon> getCoupons() {
		return coupons;
	}
}
