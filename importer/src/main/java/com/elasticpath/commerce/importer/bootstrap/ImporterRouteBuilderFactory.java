package com.elasticpath.commerce.importer.bootstrap;

import com.elasticpath.commerce.importer.impl.ImporterResult;

/**
 * Factory for creating {@link ImporterRouteBuilder}s.
 */
public class ImporterRouteBuilderFactory {

	/**
	 * Create a ImporterRouteBuilder with the provided importerResult data object.
	 * @param importerResult the result data object.
	 * @return the ImporterRouteBuilder.
	 */
	public ImporterRouteBuilder create(final ImporterResult importerResult)  {
		return new ImporterRouteBuilder(importerResult);
	}
}
