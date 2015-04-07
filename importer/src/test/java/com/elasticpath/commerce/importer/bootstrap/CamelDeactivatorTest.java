package com.elasticpath.commerce.importer.bootstrap;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.camel.CamelContext;
import org.apache.camel.PollingConsumer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CamelDeactivatorTest {

	@Mock
	private PollingConsumer productConsumer;
	@Mock
	private PollingConsumer categoryConsumer;
	@Mock
	private CamelContext camelContext;

	@InjectMocks
	private CamelDeactivator fixture;

	@Test
	public void shouldStopConsumersAndRemoveEndpoints() throws Exception {
		fixture.stop();

		verify(productConsumer).stop();
		verify(categoryConsumer).stop();

		verify(camelContext).removeEndpoints("direct:jcr-product-import");
		verify(camelContext).removeEndpoints("direct:jcr-category-import");
		verify(camelContext).removeComponent("jms");
		verify(camelContext).stop();
	}
}
