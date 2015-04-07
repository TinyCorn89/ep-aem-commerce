package com.elasticpath.rest.sdk.service;

import java.util.List;
import java.util.Locale;

import com.adobe.cq.commerce.api.promotion.VoucherInfo;

/**
 * Promotion service.
 */
public interface PromotionService {

	/**
	 * Gets the vouchers that are applied to the current order.
	 * @param userLocale The locale.
	 * @return The vouchers.
	 */
	List<VoucherInfo> getVoucherInfos(Locale userLocale);

	/**
	 * Add a new coupon to your order, It is possible that a coupon is valid but not active in your cart.
	 * @param couponCode The coupon code.
	 * @param userLocale The locale.
	 * @return success.
	 */
	boolean addCoupon(String couponCode, Locale userLocale);

	/**
	 * Remove a coupon from your order.
	 * @param couponCode The coupon code.
	 * @param userLocale The locale.
	 * @return success.
	 */
	boolean deleteCoupon(String couponCode, Locale userLocale);
}
