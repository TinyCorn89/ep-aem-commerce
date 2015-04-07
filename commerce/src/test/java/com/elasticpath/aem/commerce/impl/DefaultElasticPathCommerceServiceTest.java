package com.elasticpath.aem.commerce.impl;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import com.elasticpath.aem.commerce.AbstractElasticPathCommerceService;
import com.elasticpath.aem.commerce.service.UserPropertiesService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.adobe.cq.commerce.api.CommerceConstants;
import com.day.cq.commons.jcr.JcrConstants;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.aem.commerce.constants.ElasticPathConstants;
import com.elasticpath.aem.commerce.cortex.CortexServiceContext;
import com.elasticpath.rest.client.CortexClient;
import com.elasticpath.rest.client.CortexClientFactory;
import com.elasticpath.rest.sdk.CortexSdkServiceFactory;

@RunWith(MockitoJUnitRunner.class)
public class DefaultElasticPathCommerceServiceTest {

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private Client jaxRsClient;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private CortexServiceContext cortexConfig;
	@Mock
	private CortexClientFactory cortexClientFactory;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private Resource resource;

	@Mock
	private UserPropertiesService userPropertiesService;

	private AbstractElasticPathCommerceService elasticPathCommerceService;

	@Before
	public void setUp() {
		ValueMap valueMap = mock(ValueMap.class);
		when(valueMap.containsKey(ElasticPathConstants.ELASTICPATH_SCOPE)).thenReturn(true);
		when(resource.getChild(JcrConstants.JCR_CONTENT)
				.getValueMap())
				.thenReturn(valueMap);

		elasticPathCommerceService = new AbstractElasticPathCommerceService(cortexConfig,
				cortexClientFactory,
				jaxRsClient,
				resource,
				userPropertiesService) {
			@Override
			public CortexSdkServiceFactory getServiceFactory(final CortexClient cortexClient) {
				return null;
			}
		};
	}

	@Test
	public void testIsAvailable() {
		Response mockResponse = mock(Response.class);
		when(jaxRsClient.target(anyString())
				.request()
				.get())
				.thenReturn(mockResponse);

		elasticPathCommerceService.isAvailable(CommerceConstants.SERVICE_COMMERCE);

		verify(mockResponse).close();
	}
}
