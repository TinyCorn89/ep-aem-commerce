package com.elasticpath.commerce.importer.export;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.elasticpath.commerce.importer.exception.ImporterException;
import com.elasticpath.commerce.importer.model.PimExportResponse;

/**
 * Requests exports from external sources.
 */
public interface ExportRequestor {

	/**
	 * Signals a remote system to request an export of the data that this system will import.
	 *
	 * @param session           Session
	 * @param productsRoot      String
	 * @param catalogId         String
	 * @param incrementalImport boolean
	 * @return PimExportResponse
	 * @throws RepositoryException if there is a repository error
	 * @throws ImporterException   if there is an error during import
	 */
	PimExportResponse request(Session session,
							  String productsRoot,
							  String catalogId,
							  boolean incrementalImport) throws RepositoryException, ImporterException;
}
