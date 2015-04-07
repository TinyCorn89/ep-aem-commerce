/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.commerce.importer.importers.impl;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.day.cq.dam.api.AssetManager;
import com.day.cq.tagging.TagManager;

import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.commerce.importer.batch.BatchStatus;
import com.elasticpath.commerce.importer.export.ExportRequestor;
import com.elasticpath.commerce.importer.impl.ImporterResult;
import com.elasticpath.commerce.importer.jcr.CatalogService;
import com.elasticpath.commerce.importer.model.PimExportResponse;

/**
 * Handles import of a catalog in either full or incremental modes.
 */
public class CatalogImporter {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogImporter.class);

	@Inject
	private CatalogService catalogService;

	@Inject
	private ExportRequestor exportRequestor;

	@Inject
	private CategoryListener categoryListener;

	@Inject
	private ProductListener productListener;

	/**
	 * Imports an entire catalog in a synchronous fashion.
	 * <p/>
	 * Full imports will trigger a deletion of the existing catalog prior to import.
	 * <p/>
	 * No operation will be performed if the exporter module fails to provide a response.
	 *
	 * @param baseResourceResolver resourceResolve at the base of the tree
	 * @param productsRootNode     the node at the base of the tree, usually /etc/commerce/products
	 * @param commerceProvider     the commerceProvider
	 * @param catalogId            the id of the catalog to import
	 * @param incrementalImport    true if the import is intended to be incremental
	 * @param batchStatus          maintains state for various batch related operations
	 * @param importerResult       tracks the results of an importer run
	 * @throws RepositoryException from jcr
	 */
	public void importCatalog(final ResourceResolver baseResourceResolver,
							  final Node productsRootNode,
							  final String commerceProvider,
							  final String catalogId,
							  final boolean incrementalImport,
							  final BatchStatus batchStatus,
							  final ImporterResult importerResult)
			throws RepositoryException {

		LOG.debug("Importing ElasticPath products");
		LOG.info("incrementalImport is {}", incrementalImport);

		String productsRoot = productsRootNode.getPath();
		Session session = baseResourceResolver.adaptTo(Session.class);

		PimExportResponse pimExportResponse = exportRequestor.request(session, productsRoot, catalogId, incrementalImport);
		if (pimExportResponse == null) {
			return;
		}

		if (!incrementalImport) {
			catalogService.deleteCatalogNode(session, productsRoot, catalogId);
		}

		catalogService.createCatalogNode(session, productsRoot, catalogId, pimExportResponse.getExportId(), commerceProvider);

		// imports are presently synchronous and sequential
		categoryListener.listen(
				session,
				importerResult
		);
		productListener.listen(
				baseResourceResolver,
				baseResourceResolver.adaptTo(AssetManager.class),
				session,
				baseResourceResolver.adaptTo(TagManager.class),
				batchStatus,
				importerResult
		);
	}
}
