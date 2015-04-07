package com.elasticpath.aem.commerce.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.common.PriceFilter;
import com.google.common.collect.Maps;

import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.aem.commerce.util.ElasticPathCommerceUtil;
import com.elasticpath.rest.sdk.service.OrderService;

/**
 * Tests retrieving order details from {@link com.elasticpath.aem.commerce.impl.ElasticPathCommerceSessionImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ElasticPathCommerceSessionOrderTest extends BaseElasticPathCommerceSessionTest {

	private static final String COUNTRY_NAME = "countryName";
	private static final String EXTENDED_ADDRESS = "extendedAddress";
	private static final String LOCALITY = "locale";
	private static final String POSTAL_CODE = "postalCode";
	private static final String REGION = "region";
	private static final String STREET_ADDRESS = "streetAddress";
	private static final String FAMILY_NAME = "familyName";
	private static final String GIVEN_NAME = "givenName";
	private static final PriceInfo ORDER_TOTAL_PRICE_INFO =
			new PriceInfo(BigDecimal.TEN, RESOURCE_LOCALE, RESOURCE_CURRENCY);
	private static final PriceInfo ORDER_TAX_PRICE_INFO =
			new PriceInfo(BigDecimal.ONE, RESOURCE_LOCALE, RESOURCE_CURRENCY);

	@Before
	public void setUp() throws CommerceException, IllegalAccessException {
		super.setUp();
	}

	@Test
	public void testOrderDetailsArePopulatedCorrectlyWithBillingInformation() throws CommerceException {
		mockOrderWithBillingInformation();
		Map<String, Object> orderDetails = getElasticPathCommerceSessionImpl().getOrder();

		assertEquals(COUNTRY_NAME, orderDetails.get("billing.country"));
		assertEquals(EXTENDED_ADDRESS, orderDetails.get("billing.street2"));
		assertEquals(LOCALITY, orderDetails.get("billing.city"));
		assertEquals(POSTAL_CODE, orderDetails.get("billing.zip"));
		assertEquals(REGION, orderDetails.get("billing.state"));
		assertEquals(STREET_ADDRESS, orderDetails.get("billing.street1"));
		assertEquals(FAMILY_NAME, orderDetails.get("billing.lastname"));
		assertEquals(GIVEN_NAME, orderDetails.get("billing.firstname"));
	}
	
	@Test
	public void testOrderDetailsArePopulatedCorrectlyWithShippingInformation() throws CommerceException {
		mockOrderWithShippingInformation();

		Map<String, Object> orderDetails = getElasticPathCommerceSessionImpl().getOrder();

		assertEquals(COUNTRY_NAME, orderDetails.get("shipping.country"));
		assertEquals(EXTENDED_ADDRESS, orderDetails.get("shipping.street2"));
		assertEquals(LOCALITY, orderDetails.get("shipping.city"));
		assertEquals(POSTAL_CODE, orderDetails.get("shipping.zip"));
		assertEquals(REGION, orderDetails.get("shipping.state"));
		assertEquals(STREET_ADDRESS, orderDetails.get("shipping.street1"));
		assertEquals(FAMILY_NAME, orderDetails.get("shipping.lastname"));
		assertEquals(GIVEN_NAME, orderDetails.get("shipping.firstname"));
	}

	@Test
	public void testOrderTotalAndTaxAreSetCorrectly() throws CommerceException {
		mockCartWithTotals();
		mockOrderService();

		List<PriceInfo> expectedPrices = new ArrayList<>();
		expectedPrices.add(ElasticPathCommerceUtil.addPriceTypes(ORDER_TOTAL_PRICE_INFO, "ORDER"));
		expectedPrices.add(ElasticPathCommerceUtil.addPriceTypes(ORDER_TAX_PRICE_INFO, "TAX"));
		when(orderService.getOrderPrices(any(Locale.class))).thenReturn(expectedPrices);

		getElasticPathCommerceSessionImpl().getOrder();
		String orderTotalPrice = getElasticPathCommerceSessionImpl().getCartPrice(new PriceFilter("ORDER"));
		String orderTax = getElasticPathCommerceSessionImpl().getCartPrice(new PriceFilter("TAX"));

		assertEquals(ORDER_TAX_PRICE_INFO.getFormattedString(), orderTax);
		assertEquals(ORDER_TOTAL_PRICE_INFO.getFormattedString(), orderTotalPrice);
	}

	private void mockOrderWithBillingInformation() {
		mockOrderService();

		Map<String, String> orderDetails = Maps.newHashMap();

		orderDetails.put("billing.country", COUNTRY_NAME);
		orderDetails.put("billing.street2", EXTENDED_ADDRESS);
		orderDetails.put("billing.city", LOCALITY);
		orderDetails.put("billing.zip", POSTAL_CODE);
		orderDetails.put("billing.state", REGION);
		orderDetails.put("billing.street1", STREET_ADDRESS);
		orderDetails.put("billing.lastname", FAMILY_NAME);
		orderDetails.put("billing.firstname", GIVEN_NAME);

		when(orderService.getCortexOrderProperties(any(Locale.class))).thenReturn(orderDetails);
	}

	private void mockOrderWithShippingInformation() {
		mockOrderService();

		Map<String, String> orderDetails = Maps.newHashMap();

		orderDetails.put("shipping.country", COUNTRY_NAME);
		orderDetails.put("shipping.street2", EXTENDED_ADDRESS);
		orderDetails.put("shipping.city", LOCALITY);
		orderDetails.put("shipping.zip", POSTAL_CODE);
		orderDetails.put("shipping.state", REGION);
		orderDetails.put("shipping.street1", STREET_ADDRESS);
		orderDetails.put("shipping.lastname", FAMILY_NAME);
		orderDetails.put("shipping.firstname", GIVEN_NAME);

		when(orderService.getCortexOrderProperties(any(Locale.class))).thenReturn(orderDetails);
	}

	private void mockOrderService() {
		orderService = mock(OrderService.class);
		when(cortexSdkServiceFactory.getOrderService()).thenReturn(orderService);
	}

}
