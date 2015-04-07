package com.elasticpath.commerce.importer.impl;

import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.CATALOG;
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.COMMERCE_PROVIDER;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;

import com.adobe.cq.commerce.pim.api.ProductImporter;
import com.google.common.base.Stopwatch;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for importer flow.
 * Extracts the necessary request parameters and times how long the importInitiator takes to run the import. Then writes the appropriate
 * response.
 */
public class ElasticPathImporterServlet implements ProductImporter {

	private static final Logger LOG = LoggerFactory.getLogger(ElasticPathImporterServlet.class);

	private static final long ONE_MINUTE = 60L;
	private static final long TWO_MINUTES = 120L;

	@Inject
	private ImportInitiator importInitiator;

	@Override
	public void importProducts(final SlingHttpServletRequest request,
							   final SlingHttpServletResponse response) throws IOException {

		if (!validateInput(request)) {
			return;
		}

		Stopwatch stopwatch = Stopwatch.createStarted();

		ImporterResult importerResult = importInitiator.runImport(
				request.getResourceResolver(),
				request.getParameter(COMMERCE_PROVIDER),
				request.getParameter(CATALOG),
				BooleanUtils.toBoolean(request.getParameter("incrementalImport")),
				request.getParameter("tickertoken")
		);

		logElapsedTime(stopwatch, importerResult);

		respondWithMessages(response, summary(importerResult), importerResult);
	}

	private void logElapsedTime(final Stopwatch stopwatch,
								final ImporterResult importerResult) {
		long elapsed = stopwatch.elapsed(SECONDS);
		if (elapsed > TWO_MINUTES) {
			LOG.info("Imported " + importerResult.getProductCount() + " products in " + elapsed / ONE_MINUTE + " minutes.");
		} else {
			LOG.info("Imported " + importerResult.getProductCount() + " products in " + elapsed + " seconds.");
		}
	}

	private String summary(final ImporterResult importerResult) {

		String summary = format(
				"%d categories, %d products and %d variants created/updated.",
				importerResult.getCategoryCount(),
				importerResult.getProductCount(),
				importerResult.getVariationCount()
		);

		if (importerResult.hasErrors()) {
			summary = format(
					"%d errors encountered, %s",
					importerResult.getErrorCount(),
					summary
			);
		}

		return summary;
	}

	private void respondWithMessages(final SlingHttpServletResponse response,
									 final String summary,
									 final ImporterResult importerResult) throws IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		PrintWriter printWriter = response.getWriter();
		printWriter.println("<html><body>");
		printWriter.println("<pre>");
		printWriter.println(summary);
		importerResult.printLog(printWriter);

		printWriter.println("</pre>");
		printWriter.println("</body></html>");
		printWriter.flush();
	}

	private boolean validateInput(final SlingHttpServletRequest request) {

		return isNotBlank(request.getParameter(CATALOG))
				&& isNotBlank(request.getParameter(COMMERCE_PROVIDER));
	}
}
