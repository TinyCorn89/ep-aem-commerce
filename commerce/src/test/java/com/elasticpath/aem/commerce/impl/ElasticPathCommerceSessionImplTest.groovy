package com.elasticpath.aem.commerce.impl

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import com.adobe.cq.commerce.api.PlacedOrder
import com.adobe.cq.commerce.common.ServiceContext
import com.day.cq.commons.Language
import com.day.cq.wcm.api.LanguageManager
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import com.google.common.collect.Lists

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.Resource
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import org.powermock.api.support.membermodification.MemberModifier

import com.elasticpath.aem.commerce.AbstractElasticPathCommerceService
import com.elasticpath.rest.sdk.CortexSdkServiceFactory
import com.elasticpath.rest.sdk.components.Address
import com.elasticpath.rest.sdk.components.AddressEntry
import com.elasticpath.rest.sdk.components.Coupon
import com.elasticpath.rest.sdk.components.CouponList
import com.elasticpath.rest.sdk.components.DateComponent
import com.elasticpath.rest.sdk.components.PurchaseComponent
import com.elasticpath.rest.sdk.components.PurchaseLineItemList
import com.elasticpath.rest.sdk.components.Shipment
import com.elasticpath.rest.sdk.components.ShipmentList
import com.elasticpath.rest.sdk.components.ShippingStatus

@RunWith(MockitoJUnitRunner)
class ElasticPathCommerceSessionImplTest {

	private ElasticPathCommerceSessionImpl elasticPathCommerceSessionImpl

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	CortexSdkServiceFactory cortexSdkServiceFactory

	@Before
	public void setUp() throws Exception {
		Resource resource = mock(Resource, Mockito.RETURNS_DEEP_STUBS)

		AbstractElasticPathCommerceService elasticPathCommerceServiceImpl = mock(AbstractElasticPathCommerceService)
		when(elasticPathCommerceServiceImpl.getCortexScope()).thenReturn("geometrixx")

		ServiceContext serviceContext = mockLocaleOnServiceContext(resource)
		when(elasticPathCommerceServiceImpl.serviceContext()).thenReturn(serviceContext)

		mockLocaleOnResource(resource)

		elasticPathCommerceSessionImpl = new ElasticPathCommerceSessionImpl(
				elasticPathCommerceServiceImpl,
				mock(SlingHttpServletRequest),
				mock(SlingHttpServletResponse),
				resource,
				cortexSdkServiceFactory
		)
		elasticPathCommerceSessionImpl.setUserLocale(Locale.US)


	}

	/**
	 * used in constructor of AbstractJcrCommerceSession
	 *
	 * @param resource
	 * @return
	 */
	private ServiceContext mockLocaleOnServiceContext(Resource resource) {
		ServiceContext serviceContext = mock(ServiceContext)
		LanguageManager languageManager = mock(LanguageManager)
		MemberModifier.field(ServiceContext, "languageManager").set(serviceContext, languageManager)
		when(languageManager.getCqLanguage(resource)).thenReturn(new Language(Locale.US))
		return serviceContext
	}

	/**
	 * used in ElasticPathHelper.getLocaleFromPage.
	 *
	 * @param resource
	 */
	private void mockLocaleOnResource(Resource resource) {
		PageManager pageManager = mock(PageManager)
		when(resource.resourceResolver.adaptTo(PageManager)).thenReturn(pageManager)
		when(pageManager.getContainingPage(resource)).thenReturn(mock(Page))
	}

	@Test
	void 'should return placed orders with details'() {

		def purchaseComponent = new PurchaseComponent(
				status: "test",
				billingAddresses: [new AddressEntry(
						address: new Address(
								region: "BC"
						)
				)],
				shipmentsList: [
						new ShipmentList(
								shipments: [
										new Shipment(
												shippingAddressEntry: [
														new AddressEntry()
												],
												shippingStatus: new ShippingStatus(
														code: "shipped!"
												)
										)
								]
						)
				],
				coupons: [
						new CouponList(
								coupons: [
										new Coupon()
								]
						)
				],
				lineItems: [
						new PurchaseLineItemList()
				],
				purchaseDate: new DateComponent(
						displayValue: "today"
				)

		)
		List<PlacedOrder> orders = Lists.newArrayList(mock(PlacedOrder.class))
		when(cortexSdkServiceFactory.purchaseService.getPlacedOrders(elasticPathCommerceSessionImpl, "test")).thenReturn(orders)

		def cartEntries = elasticPathCommerceSessionImpl.getPlacedOrders("test", 3, 10, "10")

		assertThat(cartEntries.orders)
				.isNotEmpty()
	}
}
