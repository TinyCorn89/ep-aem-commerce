package com.elasticpath.commerce.importer.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.day.cq.dam.api.AssetManager;

import com.elasticpath.commerce.importer.batch.BatchStatus;
import com.elasticpath.commerce.importer.impl.ImporterResult;
import com.elasticpath.commerce.importer.model.AemVariant;

/**
 * A service that performs operations on variants in JCR.
 */
public interface VariantService {

	/**
	 * Creates a variant at the given path.
	 *
	 * @param parentProduct  parent product in which to create the variant
	 * @param name           name of the variant to create
	 * @param batchStatus    maintains state for various batch related operations
	 * @param importerResult tracks the results of the importer
	 * @return the created Node
	 * @throws RepositoryException if there is an error in the repository
	 */
	Node createVariant(Node parentProduct,
					   String name,
					   BatchStatus batchStatus,
					   ImporterResult importerResult) throws RepositoryException;

	/**
	 * Creates all variants.
	 *
	 * @param session             Session
	 * @param assetManager        AssetManager
	 * @param rootProductNodePath the path of the parent node
	 * @param variants            AemVariants to import
	 * @param catalogId           catalogId to import under
	 * @param batchStatus         BatchStatus
	 * @param importerResult      ImporterResult
	 * @throws RepositoryException if there is an error in the repository
	 */
	void createVariants(Session session,
						AssetManager assetManager,
						String rootProductNodePath,
						Iterable<AemVariant> variants,
						String catalogId,
						BatchStatus batchStatus,
						ImporterResult importerResult) throws RepositoryException;
}
