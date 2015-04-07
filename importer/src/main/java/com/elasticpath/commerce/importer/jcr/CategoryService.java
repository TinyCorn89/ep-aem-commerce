package com.elasticpath.commerce.importer.jcr;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.elasticpath.commerce.importer.impl.ImporterResult;
import com.elasticpath.commerce.importer.model.AemCategory;

/**
 * Category JCR Service.
 */
public interface CategoryService {

	/**
	 * Create category node.
	 *
	 * @param session          the {@link javax.jcr.Session}
	 * @param category         the category
	 * @param importerResult   ImporterResult
	 * @throws RepositoryException if creation of JCR node fails
	 */
	void createCategory(Session session,
						AemCategory category,
						ImporterResult importerResult) throws RepositoryException;

	/**
	 * Creates a category node.
	 *
	 * @param session        JcrSession
	 * @param categoryPath   Path to the category to create
	 * @param importerResult ImporterResult
	 * @throws RepositoryException if creation of JCR node fails
	 */
	void createCategoryNode(Session session,
							String categoryPath,
							ImporterResult importerResult) throws RepositoryException;
}
