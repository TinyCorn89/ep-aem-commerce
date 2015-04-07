/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.RangeIterator;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * Chooses pages based on a collection of tags.
 */

public final class ElasticPathPageRecommender {

	// The number of recommendations to make

	private static final int NUMBER_OF_RECOMMENDATIONS = 3;

	private static final Logger LOG = LoggerFactory.getLogger(ElasticPathPageRecommender.class);

	private ElasticPathPageRecommender() {
		// empty constructor
	}

	/**
	 * This method will return a collection of pages related to the list of tag IDs.
	 * 
	 * @param topTags List of tag IDs in order of their prevalence in the current user's browsing.
	 * @param topLevelPagePath The top level page to search below for recommendations.
	 * @param resourceResolver ResourceResolver
	 * @return A list of pages to recommend to the user.
	 */

	public static List<Page> getPageRecommendations(final List<String> topTags, final String topLevelPagePath,
			final ResourceResolver resourceResolver) {
		if (topTags == null || topLevelPagePath == null || resourceResolver == null) {
			return null;
		}

		List<Page> recommendedPages = null;
		try {
			TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			Page topLevelPage = pageManager.getPage(topLevelPagePath);
			List<Tag> tagList = buildTagList(topTags, tagManager);

			recommendedPages = buildListOfRecommendedPages(tagList, topLevelPage, NUMBER_OF_RECOMMENDATIONS, tagManager, pageManager);
		} catch (RepositoryException repoException) {
			LOG.error("Error building page recommendations.", repoException);
		}

		return recommendedPages;
	}

	/**
	 * This method is used to create tag list.
	 * 
	 * @param topTags List<String>
	 * @param tagManager TagManager
	 * @return tagList
	 */
	private static List<Tag> buildTagList(final List<String> topTags, final TagManager tagManager) {
		List<Tag> tagList = new ArrayList<Tag>();

		for (String tagId : topTags) {
			tagList.add(tagManager.resolve(tagId));
		}

		return tagList;
	}

	/**
	 * This method uses a points system to calculate how relevant a page will be to a user based on the most prevelant tags in their Client Context
	 * tag cloud.
	 * 
	 * @param tagList List<Tag>
	 * @param topLevelPageNode Page
	 * @param numberOfRecommendationsToMake
	 * @param tagManager int
	 * @param pageManager TagManager
	 * @return sortAndPruneTheRecommendations
	 * @throws RepositoryException
	 */
	private static List<Page> buildListOfRecommendedPages(final List<Tag> tagList, final Page topLevelPageNode,
			final int numberOfRecommendationsToMake, final TagManager tagManager, final PageManager pageManager) throws RepositoryException {
		// Maximum number of points for matching the top tag is the # of tags
		// Least amount of points for a match is always 1
		int pointsForAMatch = tagList.size();

		// List to track the number of points accumulated for each matching page
		List<PagePointsTracker> matchingPagesTrackerList = new ArrayList<PagePointsTracker>();
		String[] tagIdArray = new String[1];

		for (Tag tag : tagList) {
			tagIdArray[0] = tag.getTagID();
			// Find all resources which have been tagged with the given tag ID
			RangeIterator<Resource> rangeIterator = tagManager.find(topLevelPageNode.getPath(), tagIdArray);
			while (rangeIterator.hasNext()) {
				Resource currentResource = rangeIterator.next();
				Page currentPage = pageManager.getContainingPage(currentResource);
				// In this case we're only concerned with the tagged cq:Pages
				if (currentPage != null) {
					// Add the page to our list with the current number of
					// points
					PagePointsTracker newTracker = new PagePointsTracker(currentPage, pointsForAMatch);
					matchingPagesTrackerList.contains(newTracker);
					int trackerIndex = matchingPagesTrackerList.indexOf(newTracker);
					if (trackerIndex == -1) {
						// A new match. Add it to the tracker list.
						matchingPagesTrackerList.add(newTracker);
					} else {
						// This page has already been matched once; increment
						// its total number of points
						matchingPagesTrackerList.get(trackerIndex).incrementPoints(pointsForAMatch);
					}
				}
			}
			// Decrement the number of points available on each iteration
			pointsForAMatch--;
		}

		return sortAndPruneTheRecommendations(numberOfRecommendationsToMake, matchingPagesTrackerList);
	}

	/**
	 * This method is used to sort and prune the recommendations.
	 * 
	 * @param numberOfRecommendationsToMake int
	 * @param matchingPagesTrackerList List<Page>
	 * @return recommendedPages
	 */

	private static List<Page> sortAndPruneTheRecommendations(final int numberOfRecommendationsToMake,
			final List<PagePointsTracker> matchingPagesTrackerList) {
		List<Page> recommendedPages = new ArrayList<Page>();
		Collections.sort(matchingPagesTrackerList);
		for (int i = 0; i < numberOfRecommendationsToMake && i < matchingPagesTrackerList.size(); i++) {
			recommendedPages.add(matchingPagesTrackerList.get(i).getPage());
		}
		return recommendedPages;
	}

	/**
	 * Private inner class to track the number of points accumulated by a matching page.
	 */

	private static class PagePointsTracker implements Comparable<PagePointsTracker> {
		private final Page page;

		private int points;

		public PagePointsTracker(final Page page, final int points) {
			this.page = page;
			this.points += points;
			// setPage(page);
			// incrementPoints(points);
		}

		public Page getPage() {
			return page;
		}

		public int getPoints() {
			return points;
		}

		public void incrementPoints(final int points) {
			this.points += points;
		}

		public int compareTo(final PagePointsTracker otherPagePointsTracker) {
			return getPoints() - otherPagePointsTracker.getPoints();
		}

		/**
		 * Overriding the equals method.
		 */
		@Override
		public boolean equals(final Object object) {
			if (this == object) {
				return true;
			}
			if (object == null || getClass() != object.getClass()) {
				return false;
			}

			PagePointsTracker that = (PagePointsTracker) object;

			if (getPage() != null && getPage().getPath() != null && that.getPage() != null) {
				return getPage().getPath().equals(that.getPage().getPath());
			}

			return getPage() == that.getPage();
		}

		@Override
		public int hashCode() {
			if (page == null) {
				return 0;
			}
			return page.hashCode();
		}
	}
}
