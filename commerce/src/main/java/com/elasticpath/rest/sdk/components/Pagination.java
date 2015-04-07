/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Pagination.
 */
public class Pagination {
	private int current;
	private int pages;
	private int results;
	@JsonProperty("page-size")
	private int pageSize;
	@JsonProperty("results-on-page")
	private int resultsOnPage;

	public int getCurrent() {
		return current;
	}

	public int getPages() {
		return pages;
	}

	public int getResults() {
		return results;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getResultsOnPage() {
		return resultsOnPage;
	}

	public void setCurrent(final int current) {
		this.current = current;
	}

	public void setPages(final int pages) {
		this.pages = pages;
	}

	public void setResults(final int results) {
		this.results = results;
	}

	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
	}

	public void setResultsOnPage(final int resultsOnPage) {
		this.resultsOnPage = resultsOnPage;
	}
}
