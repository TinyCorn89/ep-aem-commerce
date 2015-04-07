/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.popular;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * Finds the most popular pages below a given node based on the default configurations.
 */
public class DefaultPopularPages implements PopularPages {

	private final Resource resource;

	/**
	 * Constructor for DefaultPopularPages.
	 * 
	 * @param resource Resource
	 */
	public DefaultPopularPages(final Resource resource) {
		this.resource = resource;
	}

	/**
	 * Returns a list of pre-configured pages to supplement the popular-pages component in the absence of comments.
	 * 
	 * @param topLevelPagePath Root node at which to start searching for popular pages.
	 * @param resourceResolver Sling Resource resolver
	 * @return List of <code>PageRankingResult</code> objects representing the most popular pages.
	 * @throws RepositoryException RepositoryException
	 */

	public List<PageRankingResult> getPopularPages(final String topLevelPagePath, final ResourceResolver resourceResolver)
			throws RepositoryException {
		List<PageRankingResult> results = new ArrayList<PageRankingResult>();
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

		ValueMap properties = resource.adaptTo(ValueMap.class);
		String[] propNames = new String[] { "firstDefault", "secondDefault", "thirdDefault" };
		for (String propName : propNames) {
			String path = properties.get(propName, String.class);
			if (path != null) {
				Page page = pageManager.getPage(path);
				if (page != null) {
					results.add(new PageRankingResult(page, 0));
				}
			}
		}

		return results;
	}

}
