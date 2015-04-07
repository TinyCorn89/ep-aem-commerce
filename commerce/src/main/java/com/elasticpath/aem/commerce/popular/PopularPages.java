/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.popular;

import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * PopularPages describes a function for querying the most popular pages.
 */
public interface PopularPages {

	/**
	 * Returns a list of <code>PageRankingResults</code> ranked from most popular to least popular.
	 * 
	 * @param topLevelPagePath Root node at which to start searching for popular pages
	 * @param resourceResolver ResourceResolver
	 * @return List of popular pages
	 * @throws RepositoryException RepositoryException
	 */
	List<PageRankingResult> getPopularPages(String topLevelPagePath, ResourceResolver resourceResolver) throws RepositoryException;
}