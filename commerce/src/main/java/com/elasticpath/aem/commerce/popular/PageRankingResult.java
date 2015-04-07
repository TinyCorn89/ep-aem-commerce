/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.popular;

import com.day.cq.wcm.api.Page;

/**
 * Provides a means to compare pages based on a count.
 */
public class PageRankingResult implements Comparable<PageRankingResult> {

	/**
	 * Page associated with the count.
	 */
	private final Page page;

	/**
	 * The value of the metric used to determine rank.
	 */
	private final int count;

	/**
	 * Constructor for PageRankingResulr.
	 * 
	 * @param page Page
	 * @param count count
	 */
	public PageRankingResult(final Page page, final int count) {
		this.page = page;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public Page getPage() {
		return page;
	}

	/**
	 * method to compare page comments count.
	 * 
	 * @param otherPageComments PageRankingResult
	 * @return int
	 */
	public int compareTo(final PageRankingResult otherPageComments) {
		if (getCount() < otherPageComments.getCount()) {
			return 1;
		} else if (getCount() > otherPageComments.getCount()) {
			return -1;
		}
		return 0;
	}
}