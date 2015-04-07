/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.promotion.VoucherInfo;

import org.mockito.runners.MockitoJUnitRunner;

/**
 * Mock Unit test class for {@link ElasticPathCommerceSessionImpl} for voucher related implementations.
 */
@RunWith(MockitoJUnitRunner.class)
public class ElasticPathCommerceSessionVoucherTest extends BaseElasticPathCommerceSessionTest {

	/** TEST_COUPON_CODE. */
	private static final String TEST_COUPON_CODE = "LAYER";

	/** TEST_COUPON_CODE. */
	private static final String TEST_PROMOTION_NAME = "10% Off";

	/**
	 * Test case to retrieve coupons in commerce for {@link ElasticPathCommerceSessionImpl#getVoucherInfos()}.
	 *
	 * @throws CommerceException commerceException
	 */
	@Test
	public void testGetVoucherInfos() throws CommerceException {
		givenOrderHasCouponApplied();
		final List<VoucherInfo> voucherInfoList = getElasticPathCommerceSessionImpl().getVoucherInfos();
		assertEquals(TEST_COUPON_CODE, voucherInfoList.get(0).getCode());
		assertEquals(TEST_PROMOTION_NAME, voucherInfoList.get(0).getMessage());
	}

	/**
	 * Test case to retrieve coupons in commerce for {@link ElasticPathCommerceSessionImpl#getVoucherInfos()} when no coupons available. It is
	 * verifying the negative scenario for view coupons like user added any invalid coupon, In that case there will be no coupons in the list. So it
	 * will not show any coupon code on the page.
	 * 
	 * @throws CommerceException commerceException
	 */
	@Test
	public void testGetVoucherInfosWithNoCoupons() throws CommerceException {
		List<VoucherInfo> expectedVoucherInfos = new ArrayList<>();
		when(promotionService.getVoucherInfos(any(Locale.class))).thenReturn(expectedVoucherInfos);
		final List<VoucherInfo> voucherInfoList = getElasticPathCommerceSessionImpl().getVoucherInfos();
		assertTrue(voucherInfoList.isEmpty());
	}

	@Test
	public void testAddVoucher() throws CommerceException {
		when(promotionService.addCoupon(isA(String.class), any(Locale.class))).thenReturn(true);

		getElasticPathCommerceSessionImpl().addVoucher(TEST_COUPON_CODE);
	}

	@Test(expected = CommerceException.class)
	public void testAddVoucherFail() throws CommerceException {
		when(promotionService.addCoupon(isA(String.class), any(Locale.class))).thenReturn(false);

		getElasticPathCommerceSessionImpl().addVoucher(TEST_COUPON_CODE);
	}

	@Test(expected = CommerceException.class)
	public void testAddVoucherEmpty() throws CommerceException {
		when(promotionService.addCoupon(isA(String.class), any(Locale.class))).thenReturn(false);

		getElasticPathCommerceSessionImpl().addVoucher(null);
	}

	@Test
	public void testRemoveVoucher() throws CommerceException {
		givenOrderHasCouponApplied();

		when(promotionService.deleteCoupon(isA(String.class), any(Locale.class))).thenReturn(true);

		getElasticPathCommerceSessionImpl().removeVoucher(TEST_COUPON_CODE);
	}

	@Test(expected = CommerceException.class)
	public void testRemoveVoucherFail() throws CommerceException {
		givenOrderHasCouponApplied();

		when(promotionService.deleteCoupon(isA(String.class), any(Locale.class))).thenReturn(false);

		getElasticPathCommerceSessionImpl().removeVoucher(TEST_COUPON_CODE);
	}

	@Test(expected = CommerceException.class)
	public void testRemoveVoucherEmpty() throws CommerceException {
		givenOrderHasCouponApplied();

		getElasticPathCommerceSessionImpl().removeVoucher(null);
	}

	private void givenOrderHasCouponApplied() {
		List<VoucherInfo> expectedVoucherInfos = new ArrayList<>();
		expectedVoucherInfos.add(
				new VoucherInfo(TEST_COUPON_CODE,
						"test path",
						"test title",
						"test description",
						true,
						TEST_PROMOTION_NAME));
		when(promotionService.getVoucherInfos(any(Locale.class))).thenReturn(expectedVoucherInfos);
	}
}