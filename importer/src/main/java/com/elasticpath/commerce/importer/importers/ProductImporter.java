package com.elasticpath.commerce.importer.importers;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.day.cq.dam.api.AssetManager;
import com.day.cq.tagging.TagManager;

import org.apache.sling.api.resource.ResourceResolver;

import com.elasticpath.commerce.importer.batch.BatchStatus;
import com.elasticpath.commerce.importer.impl.ImporterResult;
import com.elasticpath.commerce.importer.model.AemProduct;

/**
 * Handles the importing of products from an input AemProduct model object to calling services that store the data.
 */
public interface ProductImporter {

	/**
	 * Processes an individual product.
	 *
	 * @param resourceResolver ResourceResolver
	 * @param assetManager     AssetManager
	 * @param session          Session
	 * @param tagManager       TagManager
	 * @param product          AemProduct
	 * @param batchStatus      BatchStatus
	 * @param importerResult   ImporterResult
	 * @throws RepositoryException if an error occurs
	 */
	void importProduct(ResourceResolver resourceResolver,
					   AssetManager assetManager,
					   Session session,
					   TagManager tagManager,
					   AemProduct product,
					   BatchStatus batchStatus,
					   ImporterResult importerResult) throws RepositoryException;

	/**
	 * Handles an error response for an individual product.
	 *
	 * @param callerId       an id relating to the initiator of the import
	 * @param product        AemProduct in error
	 * @param importerResult ImporterResult
	 * @param exception      Exception
	 */
	void error(String callerId,
			   AemProduct product,
			   ImporterResult importerResult,
			   Exception exception);
}
