package com.elasticpath.commerce.importer.bootstrap;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.camel.CamelContext;
import org.apache.camel.PollingConsumer;
import org.ops4j.peaberry.activation.Stop;

/**
 * Shuts down Camel cleanly when the module is unloaded.
 */
public class CamelDeactivator {

	@Inject
	@Named("productConsumer")
	private PollingConsumer productConsumer;

	@Inject
	@Named("categoryConsumer")
	private PollingConsumer categoryConsumer;

	@Inject
	private CamelContext camelContext;

	/**
	 * Stops Camel.
	 *
	 * @throws Exception because Camel
	 */
	@Stop
	public void stop() throws Exception {

		productConsumer.stop();
		categoryConsumer.stop();
		camelContext.removeEndpoints("direct:jcr-product-import");
		camelContext.removeEndpoints("direct:jcr-category-import");
		camelContext.removeComponent("jms");

		camelContext.stop();
	}
}
