package com.elasticpath.commerce.importer.importers.impl;

import static java.lang.String.format;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.commerce.importer.impl.ImporterResult;
import com.elasticpath.commerce.importer.importers.CategoryImporter;
import com.elasticpath.commerce.importer.jcr.CategoryService;
import com.elasticpath.commerce.importer.model.AemCategory;

/**
 * Handles the importing of categories from an input model AemCategory model object to calling services that store the data.
 */
public class DefaultCategoryImporterImpl implements CategoryImporter {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultCategoryImporterImpl.class);

	@Inject
	private CategoryService categoryService;

	@Override
	public void importCategory(final Session session,
							   final AemCategory category,
							   final ImporterResult importerResult) throws RepositoryException {
		categoryService.createCategory(session, category, importerResult);
	}

	@Override
	public void error(final String callerId,
					  final AemCategory category,
					  final ImporterResult importerResult,
					  final Exception exception) {
		String message = format(
				"Error importing category [%s] from exchange [%s]",
				category.getCategoryCode(),
				callerId
		);
		LOG.error(message, exception);
		importerResult.logMessage(message, true);
	}
}
