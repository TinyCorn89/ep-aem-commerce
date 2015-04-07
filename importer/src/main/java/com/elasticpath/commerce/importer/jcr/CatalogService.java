package com.elasticpath.commerce.importer.jcr;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.base.Optional;

/**
 * Catalog JCR Service.
 */
public interface CatalogService {
	/**
	 * Create JCR catalog node.
	 *
	 * @param session the {@link Session} to use
	 * @param productsRoot the base resource path
	 * @param catalogId the catalogId
	 * @param exportId the id of the export
	 * @param commerceProvider the name of the commerce provider for the catalog node
	 * @throws RepositoryException if catalog node creation fails
	 */
	void createCatalogNode(Session session,
			String productsRoot,
			String catalogId, String exportId, String commerceProvider) throws RepositoryException;

	/**
	 * Deletes the specified JCR catalog node.
	 *
	 * @param session the {@link Session} to use
	 * @param productsRoot the base resource path
	 * @param catalogId the catalogId
	 */
	void deleteCatalogNode(Session session, String productsRoot, String catalogId);

	/**
	 * Returns the last export id for the given product root and catalog.
	 * @param session the {@link Session} to use
	 * @param productsRoot the base resource path
	 * @param catalogId the catalogId
	 * @return the last export id
	 * @throws RepositoryException if checking for the last import fails
	 */
	Optional<String> getLastExportIdForCatalog(Session session, String productsRoot, String catalogId) throws RepositoryException;
}
