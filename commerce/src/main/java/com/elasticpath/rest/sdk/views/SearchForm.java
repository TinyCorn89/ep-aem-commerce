/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.views;

import java.util.Map;

import javax.ws.rs.core.Form;

/**
 * Cortex keyword Search Form.
 */
public class SearchForm {
	private final Form form;

	/**
	 * Create Default search form.
	 */
	public SearchForm() {
		form = new Form();
	}

	/**
	 * Set the keywords to search for.
	 * @param queryString keyword search string.
	 */
	public void setKeywords(final String queryString) {
		form.param("keywords", queryString);
	}

	/**
	 * Custom page size.
	 * @param pageSize page size, values less than 1 will be ignored.
	 */
	public void setPageSize(final int pageSize) {
		if (pageSize > 0) {
			form.param("page-size", Integer.toString(pageSize));
		}
	}

	/**
	 * Return the Search form as a map form.
	 * @return this form.
	 */
	public Map<String, ?> asMap() {
		return form.asMap();
	}
}
