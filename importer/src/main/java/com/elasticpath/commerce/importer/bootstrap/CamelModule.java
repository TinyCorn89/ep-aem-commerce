package com.elasticpath.commerce.importer.bootstrap;

import static org.apache.camel.component.jms.JmsComponent.jmsComponent;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.Provides;

import org.apache.camel.CamelContext;
import org.apache.camel.PollingConsumer;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.guice.CamelModuleWithRouteTypes;
import org.apache.camel.guice.GuiceCamelContext;

import com.elasticpath.jms.api.JmsConnectionFactoryProvider;

/**
 * EP Guice bindings for Camel.
 */
@SuppressWarnings("PMD.UnusedPrivateMethod") // Used by Camel which ignores visibility
public class CamelModule extends CamelModuleWithRouteTypes {

	@Override
	protected void configureCamelContext() {
		bind(CamelContext.class).to(GuiceCamelContext.class)
				.in(Singleton.class);
	}

	@Override
	protected void configure() {
		super.configure();

		bind(CamelDeactivator.class).in(Singleton.class);
	}

	@Provides
	private JmsComponent jms(final JmsConnectionFactoryProvider connectionFactory) {
		return jmsComponent(connectionFactory.create());
	}

	@Provides
	@Singleton
	@Named("categoryConsumer")
	private PollingConsumer categoryConsumer(final CamelContext camelContext) throws Exception {
		PollingConsumer categoryConsumer = camelContext.getEndpoint("direct:jcr-category-import")
				.createPollingConsumer();

		categoryConsumer.start();

		return categoryConsumer;
	}

	@Provides
	@Singleton
	@Named("productConsumer")
	private PollingConsumer productConsumer(final CamelContext camelContext) throws Exception {
		PollingConsumer productConsumer = camelContext.getEndpoint("direct:jcr-product-import")
				.createPollingConsumer();

		productConsumer.start();

		return productConsumer;
	}

	@Provides
	@Singleton
	private ProducerTemplate producerTemplate(final CamelContext camelContext) {

		return camelContext.createProducerTemplate();
	}
}
