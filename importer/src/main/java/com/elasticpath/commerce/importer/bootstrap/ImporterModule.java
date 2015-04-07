package com.elasticpath.commerce.importer.bootstrap;

import static com.elasticpath.commerce.config.ElasticPathGlobalConstants.COMMERCE_PROVIDER;
import static com.google.inject.name.Names.named;
import static org.ops4j.peaberry.Peaberry.service;
import static org.ops4j.peaberry.util.TypeLiterals.export;
import static org.osgi.framework.Constants.SERVICE_DESCRIPTION;
import static org.osgi.framework.Constants.SERVICE_PID;

import java.util.Map;

import javax.inject.Singleton;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;

import org.osgi.service.cm.ManagedService;

import com.elasticpath.commerce.importer.batch.BatchManagerService;
import com.elasticpath.commerce.importer.config.ElasticPathImporterConfig;
import com.elasticpath.commerce.importer.config.ImporterConfig;
import com.elasticpath.commerce.importer.export.CamelExportRequestorImpl;
import com.elasticpath.commerce.importer.export.ExportRequestor;
import com.elasticpath.commerce.importer.impl.ElasticPathImporterServlet;
import com.elasticpath.commerce.importer.importers.CategoryImporter;
import com.elasticpath.commerce.importer.importers.ProductImporter;
import com.elasticpath.commerce.importer.importers.impl.CatalogImporter;
import com.elasticpath.commerce.importer.importers.impl.DefaultCategoryImporterImpl;
import com.elasticpath.commerce.importer.importers.impl.DefaultProductImporterImpl;
import com.elasticpath.commerce.importer.jcr.AssetService;
import com.elasticpath.commerce.importer.jcr.CatalogService;
import com.elasticpath.commerce.importer.jcr.CategoryService;
import com.elasticpath.commerce.importer.jcr.JcrUtilService;
import com.elasticpath.commerce.importer.jcr.ProductService;
import com.elasticpath.commerce.importer.jcr.VariantService;
import com.elasticpath.commerce.importer.jcr.impl.AssetServiceImpl;
import com.elasticpath.commerce.importer.jcr.impl.CatalogServiceImpl;
import com.elasticpath.commerce.importer.jcr.impl.CategoryServiceImpl;
import com.elasticpath.commerce.importer.jcr.impl.JcrUtilServiceImpl;
import com.elasticpath.commerce.importer.jcr.impl.ProductServiceImpl;
import com.elasticpath.commerce.importer.jcr.impl.VariantServiceImpl;
import com.elasticpath.jms.api.JmsConnectionFactoryProvider;

/**
 * EP Guice bindings for Importer.
 */
public class ImporterModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ExportRequestor.class).to(CamelExportRequestorImpl.class);
		bind(JcrUtilService.class).to(JcrUtilServiceImpl.class);

		bind(ProductImporter.class).to(DefaultProductImporterImpl.class);
		bind(CategoryImporter.class).to(DefaultCategoryImporterImpl.class);

		bind(CatalogService.class).to(CatalogServiceImpl.class);
		bind(CategoryService.class).to(CategoryServiceImpl.class);
		bind(AssetService.class).to(AssetServiceImpl.class);
		bind(ProductService.class).to(ProductServiceImpl.class);
		bind(VariantService.class).to(VariantServiceImpl.class);

		bind(BatchManagerService.class).in(Singleton.class);

		importJmsConnectionFactory();

		exportImporterConfig();
		exportImporterService();
	}

	private void importJmsConnectionFactory() {
		bind(JmsConnectionFactoryProvider.class)
				.toProvider(service(JmsConnectionFactoryProvider.class)
						.single());
	}

	private void exportImporterService() {
		Map<String, Object> attributes = ImmutableMap.<String, Object>builder()
				.put(SERVICE_DESCRIPTION, "Elastic Path Product Importer")
				.put(SERVICE_PID, CatalogImporter.class.getName())
				.put("commerceProvider", COMMERCE_PROVIDER)
				.build();

		bind(export(com.adobe.cq.commerce.pim.api.ProductImporter.class))
				.toProvider(
						service(ElasticPathImporterServlet.class)
								.attributes(attributes)
								.export()
				);
	}

	private void exportImporterConfig() {
		Map<String, Object> attributes = ImmutableMap.<String, Object>builder()
				.put(SERVICE_DESCRIPTION, "Elastic Path Importer Config")
				.put(SERVICE_PID, ElasticPathImporterConfig.class.getName())
				.build();

		ElasticPathImporterConfig elasticPathImporterConfig = new ElasticPathImporterConfig();

		bind(export(ManagedService.class))
				.annotatedWith(named("importerConfig"))
				.toProvider(
						service(elasticPathImporterConfig)
								.attributes(attributes)
								.export()
				);

		bind(ImporterConfig.class)
				.toInstance(elasticPathImporterConfig);
	}
}
