package com.elasticpath.commerce.importer.impl;

import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.COMMERCE_ROOT_PATH;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.day.cq.commons.jcr.JcrObservationThrottle;

import org.apache.camel.CamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.commerce.importer.batch.BatchManagerService;
import com.elasticpath.commerce.importer.batch.BatchStatus;
import com.elasticpath.commerce.importer.bootstrap.ImporterRouteBuilder;
import com.elasticpath.commerce.importer.bootstrap.ImporterRouteBuilderFactory;
import com.elasticpath.commerce.importer.config.ImporterConfig;
import com.elasticpath.commerce.importer.exception.ImporterException;
import com.elasticpath.commerce.importer.importers.impl.CatalogImporter;

/**
 * Tests {@link com.elasticpath.commerce.importer.impl.ImportInitiator}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ImportInitiatorTest {

	private static final String TICKER_TOKEN = "tickTockTokenTicker";
	private static final String COMMERCE_PROVIDER = "sovietProvider";
	private static final String CATALOG = "meowStick";
	private static final String ROUTE_ID = "routeId";

	@Mock
	private CamelContext camelContext;

	@Mock
	@SuppressWarnings("PMD.UnusedPrivateField")
	private ImporterConfig importerConfig;

	@Mock
	private BatchManagerService batchManagerService;

	@Mock
	private CatalogImporter importer;

	@Mock
	private ImporterRouteBuilderFactory importerRouteBuilderFactory;

	@InjectMocks
	private ImportInitiator importInitiator;

	@Mock
	private ResourceResolver resourceResolver;

	@Mock
	private Resource baseResource;

	@Mock(answer = RETURNS_DEEP_STUBS)
	private ImporterRouteBuilder mockImporterRouteBuilder;

	@Mock
	private RouteDefinition routeDefinition;

	@Mock
	private ResourceResolver productsResourceResolver;

	@Before
	public void setUp() {
		given((baseResource.getResourceResolver()))
				.willReturn(productsResourceResolver);

		given(resourceResolver.adaptTo(Session.class))
				.willReturn(mock(Session.class));
	}

	@Test
	public void shouldReturnImporterResultObjectWhenSuccessful() throws Exception {
		when(importerRouteBuilderFactory.create(any(ImporterResult.class))).thenReturn(mockImporterRouteBuilder);
		when(resourceResolver.getResource(COMMERCE_ROOT_PATH)).thenReturn(baseResource);

		ImporterResult importerResult = importInitiator.runImport(resourceResolver, COMMERCE_PROVIDER, CATALOG, false, TICKER_TOKEN);

		assertNotNull(importerResult);
	}

	@Test
	public void shouldExecuteDoImportWithExpectedParametersWhenSuccessful() throws Exception {
		arrangeMocksForDoImportExecutution();

		importInitiator.runImport(resourceResolver, COMMERCE_PROVIDER, CATALOG, false, TICKER_TOKEN);

		verify(importer)
				.importCatalog(eq(productsResourceResolver),
						any(Node.class),
						eq(COMMERCE_PROVIDER),
						eq(CATALOG),
						eq(false),
						any(BatchStatus.class),
						any(ImporterResult.class));
	}

	@Test
	public void camelRoutesShouldBeManagedResponsiblyWhenSuccessful() throws Exception {
		arrangeMocksForDoImportExecutution();

		importInitiator.runImport(resourceResolver, COMMERCE_PROVIDER, CATALOG, false, TICKER_TOKEN);

		verify(camelContext).addRoutes(mockImporterRouteBuilder);
		verify(camelContext).stopRoute(ROUTE_ID);
		verify(camelContext).removeRoute(ROUTE_ID);
	}

	@Test
	public void batchManagerShouldBeManagedResponsiblyWhenSuccessful() throws Exception {
		arrangeMocksForDoImportExecutution();

		importInitiator.runImport(resourceResolver, COMMERCE_PROVIDER, CATALOG, false, TICKER_TOKEN);

		verify(batchManagerService).openThrottle(any(Node.class));
		verify(batchManagerService).checkpoint(any(Session.class), eq(true), any(BatchStatus.class), any(ImporterResult.class));
		verify(batchManagerService).closeThrottle(any(JcrObservationThrottle.class));
	}

	@Test
	public void camelRoutesShouldBeManagedResponsiblyWhenDoImportThrowsException() throws Exception {
		arrangeMocksForDoImportExecutution();
		arrangeDoImportToThrowException(ImporterException.class);

		importInitiator.runImport(resourceResolver, COMMERCE_PROVIDER, CATALOG, false, TICKER_TOKEN);

		verify(camelContext).addRoutes(mockImporterRouteBuilder);
		verify(camelContext).stopRoute(ROUTE_ID);
		verify(camelContext).removeRoute(ROUTE_ID);
	}

	@Test
	public void resultShouldListErrorWhenDoImportThrowsRepositoryException() throws Exception {
		arrangeMocksForDoImportExecutution();
		arrangeDoImportToThrowException(RepositoryException.class);

		ImporterResult importerResult = importInitiator.runImport(resourceResolver, COMMERCE_PROVIDER, CATALOG, false, TICKER_TOKEN);

		assertTrue(importerResult.hasErrors());
	}

	@Test
	public void resultShouldListErrorWhenDoImportThrowsImporterException() throws Exception {
		arrangeMocksForDoImportExecutution();
		arrangeDoImportToThrowException(ImporterException.class);

		ImporterResult importerResult = importInitiator.runImport(resourceResolver, COMMERCE_PROVIDER, CATALOG, false, TICKER_TOKEN);

		assertTrue(importerResult.hasErrors());
	}

	@Test
	public void batchManagerShouldBeManagedResponsiblyWhenDoImportThrowsException() throws Exception {
		arrangeMocksForDoImportExecutution();
		arrangeDoImportToThrowException(ImporterException.class);

		importInitiator.runImport(resourceResolver, COMMERCE_PROVIDER, CATALOG, false, TICKER_TOKEN);

		verify(batchManagerService).openThrottle(any(Node.class));
		verify(batchManagerService).checkpoint(any(Session.class), eq(true), any(BatchStatus.class), any(ImporterResult.class));
		verify(batchManagerService).closeThrottle(any(JcrObservationThrottle.class));
	}


	private void arrangeDoImportToThrowException(final Class<? extends Exception> exception) throws RepositoryException {
		doThrow(exception).when(importer)
				.importCatalog(any(ResourceResolver.class),
						any(Node.class),
						anyString(),
						anyString(),
						anyBoolean(),
						any(BatchStatus.class),
						any(ImporterResult.class));
	}

	private void arrangeMocksForDoImportExecutution() {
		when(importerRouteBuilderFactory.create(any(ImporterResult.class))).thenReturn(mockImporterRouteBuilder);
		when(mockImporterRouteBuilder
				.getRouteCollection()
				.getRoutes())
				.thenReturn(Collections.singletonList(routeDefinition));
		when(routeDefinition.getId()).thenReturn(ROUTE_ID);
		when(resourceResolver.getResource(COMMERCE_ROOT_PATH)).thenReturn(baseResource);
	}

}
