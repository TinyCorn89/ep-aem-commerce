package com.elasticpath.commerce.importer.importers;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.elasticpath.commerce.importer.impl.ImporterResult;
import com.elasticpath.commerce.importer.model.AemCategory;

/**
 * Handles the importing of categories from an input AemCategory model object to calling services that store the data.
 */
public interface CategoryImporter {

	/**
	 * Processes an individual category.
	 *
	 * @param session        Session
	 * @param category       AemCategory
	 * @param importerResult ImporterResult
	 * @throws RepositoryException if error
	 */
	void importCategory(Session session,
						AemCategory category,
						ImporterResult importerResult) throws RepositoryException;

	/**
	 * Handles an error response for an individual category.
	 *
	 * @param callerId       an id relating to the initiator of the import
	 * @param category       AemCategory in error
	 * @param importerResult ImporterResult
	 * @param exception      Exception
	 */
	void error(String callerId,
			   AemCategory category,
			   ImporterResult importerResult,
			   Exception exception);
}
