package com.elasticpath.commerce.importer.bootstrap;

import javax.inject.Inject;

import com.adobe.cq.commerce.pim.api.ProductImporter;
import com.google.inject.AbstractModule;

import org.apache.camel.CamelContext;
import org.ops4j.peaberry.Export;

import com.elasticpath.commerce.importer.config.ImporterConfig;
import com.elasticpath.commerce.importer.batch.BatchManagerService;

/**
 * Used by Peaberry to bootstrap the DI in this module.
 */
@SuppressWarnings("PMD.UnusedPrivateField") // This is part of peaberry's syntax for creating singletons
public class RootModule extends AbstractModule {

	@Inject
	private Export<ImporterConfig> importerConfigExport;

	@Inject
	private Export<ProductImporter> productImporterExport;

	@Inject
	private CamelDeactivator camelDeactivator;

	@Inject
	private CamelContext camelContext;

	@Inject
	private BatchManagerService batchManagerService;

	@Override
	protected void configure() {
		install(new ImporterModule());
		install(new CamelModule());
	}
}
