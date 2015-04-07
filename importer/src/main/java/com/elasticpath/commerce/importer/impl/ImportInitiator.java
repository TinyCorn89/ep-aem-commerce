package com.elasticpath.commerce.importer.impl;

import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.COMMERCE_ROOT_PATH;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.commerce.importer.batch.BatchManagerService;
import com.elasticpath.commerce.importer.batch.BatchStatus;
import com.elasticpath.commerce.importer.bootstrap.ImporterRouteBuilder;
import com.elasticpath.commerce.importer.bootstrap.ImporterRouteBuilderFactory;
import com.elasticpath.commerce.importer.config.ImporterConfig;
import com.elasticpath.commerce.importer.exception.ImporterException;
import com.elasticpath.commerce.importer.importers.impl.CatalogImporter;

/**
 * Sets up and initiates the Elastic Path Importer and safely dismantles it once complete.
 */
public class ImportInitiator {

	private static final Logger LOG = LoggerFactory.getLogger(ImportInitiator.class);

	@Inject
	private CamelContext camelContext;

	@Inject
	private CatalogImporter productImporter;

	@Inject
	private ImporterConfig importerConfig;

	@Inject
	private BatchManagerService batchManagerService;

	@Inject
	private ImporterRouteBuilderFactory importerRouteBuilderFactory;


	/**
	 * Sets up the camel importer routes, executes the import, and then safely tears down the routes. Import results are collected in a
	 * importerResult object and returned.
	 *
	 * @param resourceResolver    the sling resource resolver.
	 * @param commerceProvider    the commerce provider to use in this import.
	 * @param catalog             the catalog the import is to be run against.
	 * @param isIncrementalImport a boolean stating whether this is an incremental import or not.
	 * @param tickerToken         the ticker token for the web progress bar.
	 * @return importerResult the results of the import.
	 */
	ImporterResult runImport(final ResourceResolver resourceResolver,
							 final String commerceProvider,
							 final String catalog,
							 final boolean isIncrementalImport,
							 final String tickerToken) {

		batchManagerService.initialiseTicker(resourceResolver.adaptTo(Session.class), tickerToken);

		ImporterResult importerResult = new ImporterResult(importerConfig.getMessageCap());
		ImporterRouteBuilder routeBuilder = importerRouteBuilderFactory.create(importerResult);
		setUpRoutes(routeBuilder);

		executeImport(resourceResolver, commerceProvider, catalog, isIncrementalImport, tickerToken, importerResult);

		tearDownRoutes(routeBuilder);
		return importerResult;
	}

	private void executeImport(final ResourceResolver resourceResolver,
							   final String commerceProvider,
							   final String catalog,
							   final boolean isIncrementalImport,
							   final String tickerToken,
							   final ImporterResult importerResult) {

		Resource baseResource = resourceResolver
				.getResource(COMMERCE_ROOT_PATH);
		Node baseNode = baseResource.adaptTo(Node.class);

		BatchStatus batchStatus = null;

		try {
			batchStatus = new BatchStatus(importerConfig.getSaveBatchSize(),
										  importerConfig.getThrottleBatchSize(),
										  tickerToken,
										  batchManagerService.openThrottle(baseNode)
			);

			productImporter.importCatalog(
					baseResource.getResourceResolver(),
					baseNode,
					commerceProvider,
					catalog,
					isIncrementalImport,
					batchStatus,
					importerResult
			);
		} catch (RepositoryException e) {
			importerResult.logMessage(e.getMessage(), true);
			LOG.error("Error while running import", e);
		} catch (ImporterException e) {
			importerResult.logMessage(e.getMessage(), true);
			LOG.error(e.getMessage(), e);
		} finally {
			if (batchStatus != null) {
				batchStatus.completeTicker();

				batchManagerService.checkpoint(resourceResolver.adaptTo(Session.class), true, batchStatus, importerResult);
				batchManagerService.closeThrottle(batchStatus.getThrottle());
			}
		}
	}

	private void setUpRoutes(final ImporterRouteBuilder routeBuilder) {
		try {
			camelContext.addRoutes(routeBuilder);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private void tearDownRoutes(final RouteBuilder routeBuilder) {
		Iterable<RouteDefinition> routes = routeBuilder.getRouteCollection()
												   .getRoutes();
		for (RouteDefinition route : routes) {
			String routeId = route.getId();
			try {
				camelContext.stopRoute(routeId);
				camelContext.removeRoute(routeId);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}
}
