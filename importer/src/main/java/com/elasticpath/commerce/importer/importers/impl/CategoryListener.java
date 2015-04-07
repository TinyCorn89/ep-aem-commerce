package com.elasticpath.commerce.importer.importers.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Session;

import com.elasticpath.commerce.importer.config.ImporterConfig;
import org.apache.camel.Exchange;
import org.apache.camel.PollingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.commerce.importer.impl.ImporterResult;
import com.elasticpath.commerce.importer.importers.CategoryImporter;
import com.elasticpath.commerce.importer.model.AemCategory;

/**
 * Listens on an external queue for category messages.
 */
public class CategoryListener {

	private static final Logger LOG = LoggerFactory.getLogger(CategoryListener.class);

	@Inject
	@Named("categoryConsumer")
	private PollingConsumer categoryConsumer;

	@Inject
	private CategoryImporter categoryImporter;

	@Inject
	private ImporterConfig importerConfig;
	/**
	 * Listens on an external queue for category messages.
	 *
	 * @param session        Session
	 * @param importerResult ImporterResult
	 */
	void listen(final Session session,
				final ImporterResult importerResult) {
		Exchange categoryExchange = categoryConsumer.receive(importerConfig.getImportInitialTimeout());
		while (categoryExchange != null) {
			AemCategory category = categoryExchange.getIn()
					.getBody(AemCategory.class);

			try {
				categoryImporter.importCategory(session, category, importerResult);
			} catch (Exception e) {
				categoryImporter.error(categoryExchange.getExchangeId(), category, importerResult, e);
			}

			categoryExchange = categoryConsumer.receive(importerConfig.getImportPollingTimeout());
		}

		logCategoriesImported(importerResult);
	}

	private void logCategoriesImported(final ImporterResult importerResult) {
		LOG.debug("Categories imported successfully");
		importerResult.logMessage(importerResult.getCategoryCount() + " categories created/updated.", false);
		importerResult.logMessage("", false);
	}
}
