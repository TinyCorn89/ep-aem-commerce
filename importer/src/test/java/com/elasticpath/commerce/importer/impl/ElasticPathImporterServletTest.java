package com.elasticpath.commerce.importer.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants;

/**
 * Tests {@link ElasticPathImporterServlet}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ElasticPathImporterServletTest {

	private static final String TICKER_TOKEN = "tickTockTokenTicker";
	private static final String COMMERCE_PROVIDER = "sovietProvider";
	private static final String CATALOG = "meowStick";
	private static final int FIVE = 5;
	private static final int SEVEN = 7;
	private static final int ELEVEN = 11;
	private static final Long TWO = 2L;
	@Mock
	private ImportInitiator importInitiator;
	@InjectMocks
	ElasticPathImporterServlet elasticPathImporterServlet;
	@Mock
	private SlingHttpServletRequest request;
	@Mock
	private SlingHttpServletResponse response;
	@Mock
	private ResourceResolver resourceResolver;
	@Mock
	private PrintWriter printWriter;
	@Mock
	private ImporterResult importerResult;


	@Test
	public void shouldNotRunImportWhenRequestInvalid() throws ServletException, IOException {

		elasticPathImporterServlet.importProducts(request, response);

		verify(importInitiator, never()).runImport(any(ResourceResolver.class), anyString(), anyString(), anyBoolean(), anyString());
	}

	@Test
	public void shouldRunImportWhenRequestValid() throws IOException {
		arrangeValidRequest();
		when(importInitiator.runImport(resourceResolver, COMMERCE_PROVIDER, CATALOG, false, TICKER_TOKEN)).thenReturn(importerResult);
		when(response.getWriter()).thenReturn(printWriter);

		elasticPathImporterServlet.importProducts(request, response);

		verify(importInitiator).runImport(resourceResolver, COMMERCE_PROVIDER, CATALOG, false, TICKER_TOKEN);
	}

	@Test
	public void shouldPrintSuccessSummaryWhenImportCompletes() throws IOException {
		arrangeValidRequest();
		when(importerResult.getCategoryCount()).thenReturn(FIVE);
		when(importerResult.getProductCount()).thenReturn(SEVEN);
		when(importerResult.getVariationCount()).thenReturn(ELEVEN);
		when(importInitiator.runImport(resourceResolver, COMMERCE_PROVIDER, CATALOG, false, TICKER_TOKEN)).thenReturn(importerResult);
		when(response.getWriter()).thenReturn(printWriter);

		elasticPathImporterServlet.importProducts(request, response);

		verify(printWriter).println(
				FIVE + " categories, " + SEVEN + " products and " + ELEVEN + " variants created/updated."
		);
	}

	@Test
	public void shouldPrintErrorSummaryWhenImportCompletesWithErrors() throws IOException {
		arrangeValidRequest();
		when(importerResult.hasErrors()).thenReturn(true);
		when(importerResult.getErrorCount()).thenReturn(TWO);
		when(importInitiator.runImport(resourceResolver, COMMERCE_PROVIDER, CATALOG, false, TICKER_TOKEN)).thenReturn(importerResult);
		when(response.getWriter()).thenReturn(printWriter);

		elasticPathImporterServlet.importProducts(request, response);

		verify(printWriter).println(
				TWO + " errors encountered, " + 0 + " categories, " + 0 + " products and " + 0 + " variants created/updated."
		);
	}

	@Test(expected = IOException.class)
	public void shouldThrowIOExceptionWhenResponseWriterNotFound() throws IOException {
		arrangeValidRequest();
		doThrow(IOException.class).when(response).getWriter();
		when(importInitiator.runImport(resourceResolver, COMMERCE_PROVIDER, CATALOG, false, TICKER_TOKEN)).thenReturn(importerResult);

		elasticPathImporterServlet.importProducts(request, response);
	}

	private void arrangeValidRequest() {
		when(request.getParameter("tickertoken")).thenReturn(TICKER_TOKEN);
		when(request.getParameter(ElasticPathImporterConstants.COMMERCE_PROVIDER)).thenReturn(COMMERCE_PROVIDER);
		when(request.getParameter(ElasticPathImporterConstants.CATALOG)).thenReturn(CATALOG);
		when(request.getParameter("incrementalImport")).thenReturn("false");
		when(request.getResourceResolver()).thenReturn(resourceResolver);
	}
}
