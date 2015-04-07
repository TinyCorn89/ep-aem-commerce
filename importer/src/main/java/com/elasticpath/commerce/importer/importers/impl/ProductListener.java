package com.elasticpath.commerce.importer.importers.impl;


import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Session;

import com.day.cq.dam.api.AssetManager;
import com.day.cq.tagging.TagManager;

import com.elasticpath.commerce.importer.config.ImporterConfig;
import org.apache.camel.Exchange;
import org.apache.camel.PollingConsumer;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.commerce.importer.batch.BatchStatus;
import com.elasticpath.commerce.importer.impl.ImporterResult;
import com.elasticpath.commerce.importer.importers.ProductImporter;
import com.elasticpath.commerce.importer.model.AemProduct;

/**
 * Listens on an external queue for product messages.
 */
public class ProductListener {

	private static final Logger LOG = LoggerFactory.getLogger(ProductListener.class);

	@Inject
	@Named("productConsumer")
	private PollingConsumer productConsumer;

	@Inject
	private ProductImporter productImporter;

	@Inject
	private ImporterConfig importerConfig;
	/**
	 * Listens on an external queue for product messages.
	 *
	 * @param resourceResolver ResourceResolver
	 * @param assetManager     AssetManager
	 * @param session          Session
	 * @param tagManager       TagManager
	 * @param batchStatus      BatchStatus
	 * @param importerResult   ImporterResult
	 */
	void listen(final ResourceResolver resourceResolver,
				final AssetManager assetManager,
				final Session session,
				final TagManager tagManager,
				final BatchStatus batchStatus,
				final ImporterResult importerResult) {
		Exchange productExchange = productConsumer.receive(importerConfig.getImportInitialTimeout());
		while (productExchange != null) {
			AemProduct product = productExchange.getIn()
					.getBody(AemProduct.class);

			try {
				productImporter.importProduct(
						resourceResolver,
						assetManager,
						session,
						tagManager,
						product,
						batchStatus,
						importerResult
				);
			} catch (Exception e) {
				productImporter.error(productExchange.getExchangeId(), product, importerResult, e);
			}

			productExchange = productConsumer.receive(importerConfig.getImportPollingTimeout());
		}

		logProductsImported(importerResult);
	}

	private void logProductsImported(final ImporterResult importerResult) {
		LOG.debug("Products imported successfully");
		importerResult.logMessage(importerResult.getProductCount() + " products created/updated.", false);

		LOG.debug("Variants imported successfully");
		importerResult.logMessage(importerResult.getVariationCount() + " variants created/updated.", false);
		importerResult.logMessage("", false);
	}
}
