package com.elasticpath.commerce.importer.export;

import static java.lang.String.format;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.base.Optional;

import org.apache.camel.ProducerTemplate;

import com.elasticpath.commerce.importer.exception.ImporterException;
import com.elasticpath.commerce.importer.model.PimExportResponse;
import com.elasticpath.commerce.importer.model.PimFullExportRequest;
import com.elasticpath.commerce.importer.model.PimIncrementalExportRequest;
import com.elasticpath.commerce.importer.jcr.CatalogService;

/**
 * Requests an external service to begin exporting.
 */
public class CamelExportRequestorImpl implements ExportRequestor {

	/**
	 * Full export endpoint.
	 */
	public static final String TRIGGER_FULL_CATALOG_EXPORT_ENDPOINT = "direct:unmarshalled-full-export-catalog-endpoint";

	/**
	 * Incremental export endpoint.
	 */
	public static final String TRIGGER_INCREMENTAL_CATALOG_EXPORT_ENDPOINT = "direct:unmarshalled-incremental-export-catalog-endpoint";

	@Inject
	private ProducerTemplate producerTemplate;

	@Inject
	private CatalogService catalogService;

	@Override
	public PimExportResponse request(final Session session,
									 final String productsRoot,
									 final String catalogId,
									 final boolean incrementalImport) throws RepositoryException, ImporterException {
		if (!incrementalImport) {
			return startFullExport(catalogId);
		}

		Optional<String> lastExport = catalogService.getLastExportIdForCatalog(session, productsRoot, catalogId);
		if (lastExport.isPresent()) {
			return startIncrementalExport(catalogId, lastExport);
		} else {
			throw new ImporterException(
					format("No previous import has been recorded for catalog [%s]. Unable to perform incremental import.", catalogId)
			);
		}
	}

	private PimExportResponse startFullExport(final String catalogId) {
		PimFullExportRequest request = new PimFullExportRequest();
		request.setCatalogId(catalogId);

		return producerTemplate.requestBody(
				TRIGGER_FULL_CATALOG_EXPORT_ENDPOINT,
				request,
				PimExportResponse.class
		);
	}

	private PimExportResponse startIncrementalExport(final String catalogId,
													 final Optional<String> lastExport) {
		PimIncrementalExportRequest request = new PimIncrementalExportRequest();
		request.setCatalogId(catalogId);
		request.setExportId(lastExport.get());

		return producerTemplate.requestBody(
				TRIGGER_INCREMENTAL_CATALOG_EXPORT_ENDPOINT,
				request,
				PimExportResponse.class
		);
	}
}
