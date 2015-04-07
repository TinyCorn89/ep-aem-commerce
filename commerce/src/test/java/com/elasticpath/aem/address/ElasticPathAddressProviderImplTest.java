/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.address;

import static com.elasticpath.aem.commerce.constants.ElasticPathConstants.REQUEST_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.ClientBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.adobe.cq.address.api.Address;
import com.adobe.cq.address.api.AddressException;
import com.adobe.cq.commerce.api.CommerceConstants;
import com.adobe.cq.commerce.api.CommerceService;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.mockito.Mock;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.aem.address.impl.ElasticPathAddressProviderImpl;
import com.elasticpath.aem.commerce.AbstractElasticPathCommerceService;
import com.elasticpath.aem.commerce.ElasticPathCommerceService;
import com.elasticpath.aem.commerce.cortex.CortexContext;
import com.elasticpath.aem.cookies.AemCortexContextManager;
import com.elasticpath.rest.client.CortexClient;
import com.elasticpath.rest.client.CortexClientFactory;
import com.elasticpath.rest.sdk.CortexSdkServiceFactory;
import com.elasticpath.rest.sdk.service.AddressService;

/**
 * Tests retrieving address details from {@link com.elasticpath.aem.address.impl.ElasticPathAddressProviderImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ClientBuilder.class)
public class ElasticPathAddressProviderImplTest {

	private static final String USER_ID = "userId";
	private static final String AUTH_TOKEN = "token";
	private static final String AEM_CORTEX_CONTEXT_MANAGER = "contextManager";
	private static final String CORTEX_CLIENT_FACTORY = "cortexClientFactory";
	private static final String RESOURCE_PATH = "/addressBook";
	private static final String SCOPE = "scope";

	@Mock
	private ResourceResolver resourceResolver;
	@Mock
	private CortexContext cortexContext;
	@Mock
	private CortexClient cortexClient;

	private AddressService mockAddressService;

	private ElasticPathAddressProviderImpl elasticPathAddressProviderImpl;
	private Address mockAddress;
	private Resource mockResource;
	private Map<String, Object> properties;

	@Before
	public void setUp() throws IllegalArgumentException, IllegalAccessException, AddressException {
		elasticPathAddressProviderImpl = new ElasticPathAddressProviderImpl();
		initMocks(this);
		mockCortexScope();
		mockCortexClient();
		mockAddressService = createMockAddressService();
		mockResource = mock(Resource.class);
		mockAddress = new Address(mockResource);
		properties = new HashMap<>();
	}

	@Test
	public void shouldAddAddress() throws Exception {
		when(mockAddressService.createAddress(resourceResolver, properties)).thenReturn(mockAddress);

		final Address address = elasticPathAddressProviderImpl.addAddress(resourceResolver, USER_ID, properties, true);

		assertThat(address).isEqualTo(mockAddress);
	}

	@Test
	public void shouldReturnDefaultBillingAddressAssociatedToUserId() throws Exception {
		when(mockAddressService.getDefaultBillingAddress(resourceResolver)).thenReturn(mockAddress);

		final Address defaultAddress = elasticPathAddressProviderImpl.getDefaultAddress(
				resourceResolver, USER_ID, CommerceConstants.BILLING_ADDRESS_PREDICATE);

		assertThat(defaultAddress).isEqualTo(mockAddress);
	}

	@Test
	public void shouldReturnDefaultShippingAddressAssociatedToUserId() throws Exception {
		when(mockAddressService.getDefaultShippingAddress(resourceResolver)).thenReturn(mockAddress);

		final Address defaultAddress = elasticPathAddressProviderImpl.getDefaultAddress(
				resourceResolver, USER_ID, CommerceConstants.SHIPPING_ADDRESS_PREDICATE);

		assertThat(defaultAddress).isEqualTo(mockAddress);
	}

	@Test
	public void shouldReturnDefaultShippingAddressWhenTypeNotSpecified() throws Exception {
		when(mockAddressService.getDefaultShippingAddress(resourceResolver)).thenReturn(mockAddress);
		String addressType = "Take one for the team you're a cog in the machine";

		final Address defaultAddress = elasticPathAddressProviderImpl.getDefaultAddress(resourceResolver, USER_ID, addressType);

		assertThat(defaultAddress).isEqualTo(mockAddress);
	}

	@Test
	public void shouldReturnAllAddressesAssociatedToAUserId() throws Exception {
		final List<Address> expectedAddressList = Arrays.asList(new Address(mock(Resource.class)));
		when(mockAddressService.getAddressList(resourceResolver)).thenReturn(expectedAddressList);

		final Collection<Address> addressList = elasticPathAddressProviderImpl.getAddresses(resourceResolver, USER_ID);

		assertThat(addressList).containsAll(expectedAddressList);
	}

	@Test
	public void shouldReturnAddressCorrespondingToAddressPathAssociatedToUserId() throws Exception {
		when(mockAddressService.getAddress(resourceResolver, RESOURCE_PATH)).thenReturn(mockAddress);

		final Address address = elasticPathAddressProviderImpl.getAddress(resourceResolver, USER_ID, RESOURCE_PATH);

		assertThat(address).isNotNull();
	}

	@Test
	public void shouldRemoveAddress() throws Exception {
		when(mockResource.getPath()).thenReturn("testpath");

		elasticPathAddressProviderImpl.removeAddress(resourceResolver, USER_ID, mockAddress, true);

		verify(mockAddressService).delete("testpath");
	}

	@Test
	public void shouldUpdateAddressAssociatedToUserId() throws Exception {
		when(mockAddressService.updateAddress(resourceResolver, mockAddress, properties)).thenReturn(mockAddress);

		final Address address = elasticPathAddressProviderImpl.updateAddress(resourceResolver, USER_ID, mockAddress, properties, true);

		assertThat(address).isEqualTo(mockAddress);
	}

	private AddressService createMockAddressService() {
		when(resourceResolver.getAttribute(REQUEST_URI)).thenReturn("");
		final Resource mockResource = mock(Resource.class);
		when(resourceResolver.resolve("")).thenReturn(mockResource);
		final ElasticPathCommerceService mockElasticPathCommerceService = mock(ElasticPathCommerceService.class);
		when(mockResource.adaptTo(CommerceService.class)).thenReturn(mockElasticPathCommerceService);
		final AddressService mockAddressService = mock(AddressService.class);
		final CortexSdkServiceFactory mockCortexSdkServiceFactory = mock(CortexSdkServiceFactory.class);
		when(mockCortexSdkServiceFactory.getAddressService()).thenReturn(mockAddressService);
		when(mockElasticPathCommerceService.getServiceFactory(any(CortexClient.class))).thenReturn(mockCortexSdkServiceFactory);
		return mockAddressService;
	}

	private void mockCortexClient() throws IllegalArgumentException, IllegalAccessException {
		final AemCortexContextManager aemCortexContextManager = mock(AemCortexContextManager.class);
		MemberModifier.field(ElasticPathAddressProviderImpl.class, AEM_CORTEX_CONTEXT_MANAGER).set(elasticPathAddressProviderImpl,
				aemCortexContextManager);
		when(aemCortexContextManager.retrieveContext(resourceResolver, USER_ID)).thenReturn(cortexContext);
		when(cortexContext.getAuthenticationToken()).thenReturn(AUTH_TOKEN);

		final CortexClientFactory cortexClientFactory = mock(CortexClientFactory.class);
		MemberModifier.field(ElasticPathAddressProviderImpl.class, CORTEX_CLIENT_FACTORY).set(elasticPathAddressProviderImpl, cortexClientFactory);

		when(cortexClientFactory.create(AUTH_TOKEN, SCOPE))
				.thenReturn(cortexClient);
	}

	private void mockCortexScope() {
		final AbstractElasticPathCommerceService epCommerceService = mock(AbstractElasticPathCommerceService.class);
		final Resource resource = mock(Resource.class);
		when(resourceResolver.getAttribute(REQUEST_URI)).thenReturn(RESOURCE_PATH);
		when(resourceResolver.resolve(RESOURCE_PATH)).thenReturn(resource);
		when(resource.adaptTo(CommerceService.class)).thenReturn(epCommerceService);
		when(epCommerceService.getCortexScope()).thenReturn(SCOPE);
	}
}
