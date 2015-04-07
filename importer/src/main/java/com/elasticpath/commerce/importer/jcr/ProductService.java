package com.elasticpath.commerce.importer.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.day.cq.tagging.TagManager;

import org.apache.sling.api.resource.ResourceResolver;

import com.elasticpath.commerce.importer.batch.BatchStatus;
import com.elasticpath.commerce.importer.impl.ImporterResult;
import com.elasticpath.commerce.importer.model.AemProduct;

/**
 * A service that performs operations on products in JCR.
 */
public interface ProductService {

	/**
	 * Creates a product at the given path.
	 *
	 * @param session        Session
	 * @param path           path at which to create a product
	 * @param batchStatus    maintains state for various batch related operations
	 * @param importerResult tracks the results of the importer
	 * @return the created node
	 * @throws RepositoryException if there is an error modifying the repository
	 */
	Node createProduct(Session session,
					   String path,
					   BatchStatus batchStatus,
					   ImporterResult importerResult) throws RepositoryException;

	/**
	 * Deletes the product at the given path.
	 *
	 * @param session     Session
	 * @param productPath path of the product to delete
	 * @throws RepositoryException if there is an error modifying the repository
	 */
	void deleteProduct(Session session,
					   String productPath)
			throws RepositoryException;

	/**
	 * Updates a product in jcr.
	 *
	 * @param resourceResolver resourceResolver
	 * @param tagManager       tagManager
	 * @param productNode      the product node to initialize
	 * @param product          product
	 * @param productPath      String
	 * @throws RepositoryException if a problem occurs working with the repository
	 */
	void updateProduct(ResourceResolver resourceResolver,
					   TagManager tagManager,
					   Node productNode,
					   AemProduct product,
					   String productPath) throws RepositoryException;
}
