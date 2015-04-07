package com.elasticpath.rest.sdk.components;

import java.util.Collection;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Cortex Coupon.
 */
public class Coupon extends Linkable {

	@JsonProperty("code")
	private String code;

	@JsonProperty("_appliedpromotions")
	private Collection<AppliedPromotions> couponTriggeredPromotions;

	/**
	 * Gets the code.
	 * @return the code.
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Gets the promotions triggered by this coupon.
	 * @return the promotions triggered by this coupon.
	 */
	public Collection<AppliedPromotions> getCouponTriggeredPromotions() {
		if (couponTriggeredPromotions == null) {
			return Collections.EMPTY_LIST;
		} else {
			return couponTriggeredPromotions;
		}
	}
}
