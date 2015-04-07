/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.views;

import com.adobe.cq.commerce.api.PaginationInfo;
import com.adobe.cq.commerce.common.DefaultPaginationInfo;

import com.fasterxml.jackson.contrib.jsonpath.annotation.JsonPath;
import com.elasticpath.rest.client.urlbuilding.annotations.FollowLocation;
import com.elasticpath.rest.client.urlbuilding.annotations.Path;
import com.elasticpath.rest.client.urlbuilding.annotations.Uri;
import com.elasticpath.rest.client.urlbuilding.annotations.Zoom;
import com.elasticpath.rest.sdk.EntryPointUris;
import com.elasticpath.rest.sdk.components.Item;
import com.elasticpath.rest.sdk.components.Link;
import com.elasticpath.rest.sdk.components.Linkable;
import com.elasticpath.rest.sdk.components.Pagination;

/**
 * Search Results.
 */
@Uri(EntryPointUris.ITEM_SEARCH)
@Zoom({
		@Path("element:code"),
		@Path("element:price")
})
@FollowLocation
public class PaginatedSearchResults extends Linkable {

	@JsonPath("$.pagination")
	private Pagination pagination;

	@JsonPath("$._element")
	private Iterable<Item> results;

	public Pagination getPagination() {
		return pagination;
	}

	public Iterable<Item> getResults() {
		return results;
	}

	public void setPagination(final Pagination pagination) {
		this.pagination = pagination;
	}

	public void setResults(final Iterable<Item> results) {
		this.results = results;
	}

	/**
	 * Get the link to the next page.
	 * @return Link null if no next page exists.
	 */
	public Link getNextPage() {
		for (Link link : getLinks()) {
			if (link.getRel().equals("next")) {
				return link;
			}
		}

		return null;
	}

	/**
	 * Get link to prev page.
	 * @return Link null if no previous page exists.
	 */
	public Link getPreviousPage() {
		for (Link link : getLinks()) {
			if (link.getRel().equals("previous")) {
				return link;
			}
		}

		return null;
	}

	/**
	 * Return paging info with current page, page size, total pages and total results.
	 *
	 * @return PaginationInfo object with page size, total results and total pages.
	 */
	public PaginationInfo getPaginationInfo() {
		final DefaultPaginationInfo paginationInfo = new DefaultPaginationInfo();
		paginationInfo.setCurrentPage(pagination.getCurrent());
		paginationInfo.setPageSize(pagination.getPageSize());
		paginationInfo.setTotalPages(pagination.getPages());
		paginationInfo.setTotalResults(pagination.getResults());
		return paginationInfo;
	}

}
