package com.elasticpath.commerce.importer.bootstrap;

import static java.lang.String.format;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import com.elasticpath.commerce.importer.impl.ImporterResult;
import com.elasticpath.commerce.importer.model.AemCategory;
import com.elasticpath.commerce.importer.model.AemProduct;
import com.elasticpath.commerce.importer.model.PimExportResponse;

/**
 * Defines routes for Camel.
 */
public class ImporterRouteBuilder extends RouteBuilder {

	private static final String EXPORTED_CATEGORIES_ENDPOINT = "jms:exported-categories-endpoint";
	private static final String EXPORTED_PRODUCTS_ENDPOINT = "jms:exported-products-endpoint";
	private static final String TRIGGER_FULL_CATALOG_EXPORT_ENDPOINT = "direct:unmarshalled-full-export-catalog-endpoint";
	private static final String TRIGGER_INCREMENTAL_CATALOG_EXPORT_ENDPOINT = "direct:unmarshalled-incremental-export-catalog-endpoint";
	private static final String TRIGGER_CATALOG_EXPORT_ENDPOINT = "jms:export-catalog-endpoint";

	private final ImporterResult importerResult;

	/**
	 * Constructor.
	 *
	 * @param importerResult ImporterResult
	 */
	public ImporterRouteBuilder(final ImporterResult importerResult) {
		this.importerResult = importerResult;
	}

	@Override
	public void configure() throws Exception {

		onException(Exception.class).handled(true)
				.process(new Processor() {
					@Override
					public void process(final Exchange exchange) throws Exception {
						Throwable caused = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);

						if (caused == null) {
							importerResult.logMessage(format("Unknown Error occurred handling exchangeId: [%s]", exchange.getExchangeId()), true);
						} else {
							importerResult.logMessage(getRootCauseMessage(caused), true);
						}
					}
				});

		from(TRIGGER_FULL_CATALOG_EXPORT_ENDPOINT)
				.routeId("export-catalog-message-marshaller")
				.marshal()
				.json(JsonLibrary.Jackson)
				.to(TRIGGER_CATALOG_EXPORT_ENDPOINT)
				.unmarshal()
				.json(JsonLibrary.Jackson, PimExportResponse.class);

		from(TRIGGER_INCREMENTAL_CATALOG_EXPORT_ENDPOINT)
				.routeId("incremental-catalog-export-message-marshaller")
				.marshal()
				.json(JsonLibrary.Jackson)
				.to("jms:incremental-catalog-export-event-endpoint")
				.unmarshal()
				.json(JsonLibrary.Jackson, PimExportResponse.class);

		from(EXPORTED_PRODUCTS_ENDPOINT)
				.routeId("jcr-product-unmarshaller")
				.unmarshal()
				.json(JsonLibrary.Jackson, AemProduct.class)
				.to("direct:jcr-product-import");

		from(EXPORTED_CATEGORIES_ENDPOINT)
				.routeId("jcr-category-unmarshaller")
				.unmarshal()
				.json(JsonLibrary.Jackson, AemCategory.class)
				.to("direct:jcr-category-import");
	}
}
