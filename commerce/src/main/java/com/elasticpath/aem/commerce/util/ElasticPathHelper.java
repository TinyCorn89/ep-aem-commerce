/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.Product;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class providing various helper functions for Geometrixx Outdoors.
 */
public final class ElasticPathHelper {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticPathHelper.class);

	/** The Constant IMAGE_SIZE_MAP for mapping image size strings to the corresponding width and height dimensions. */
	private static final Map<String, String> IMAGE_SIZE_MAP;

	static {
		IMAGE_SIZE_MAP = new HashMap<>();
		IMAGE_SIZE_MAP.put("small-image", "220.150");
		IMAGE_SIZE_MAP.put("medium-image", "480.320");
		IMAGE_SIZE_MAP.put("large-image", "770.360");
		IMAGE_SIZE_MAP.put("landscape-image", "370.150");
	}

	/** The Constructor ElasticPathHelper. */
	private ElasticPathHelper() {
		// empty constructor
	}

	/**
	 * Returns the title of the given resource. If the title is empty it will fallback to the page title, title, or name of the given page.
	 * 
	 * @param resource The resource.
	 * @param page The page to fallback to.
	 * @return The best suited title found (or <code>null</code> if resource is <code>null</code>).
	 */
	public static String getTitle(final Resource resource, final Page page) {
		if (resource == null) {
			LOGGER.error("Provided resource argument is null");
			return null;
		}
		final ValueMap properties = resource.adaptTo(ValueMap.class);
		if (properties != null) {
			final String title = properties.get(NameConstants.PN_TITLE, String.class);
			if (StringUtils.isNotEmpty(title)) {
				return title;
			} else {
				return getPageTitle(page);
			}
		}
		return null;
	}

	/**
	 * Returns the page title of the given page. If the page title is empty it will fallback to the title and to the name of the page.
	 * 
	 * @param page The page.
	 * @return The best suited title found (or <code>null</code> if page is <code>null</code>).
	 */
	public static String getPageTitle(final Page page) {
		if (page == null) {
			return null;
		}
		final String title = page.getPageTitle();
		if (StringUtils.isEmpty(title)) {
			return ElasticPathHelper.getTitle(page);
		}
		return title;
	}

	/**
	 * Returns the title of the given page. If the title is empty it will fallback to the name of the page.
	 * 
	 * @param page The page.
	 * @return The best suited title found (or <code>null</code> if page is <code>null</code>).
	 */
	public static String getTitle(final Page page) {
		if (page == null) {
			return null;
		}
		final String title = page.getTitle();
		if (StringUtils.isEmpty(title)) {
			return page.getName();
		}
		return title;
	}

	/**
	 * Returns Locale on the basis of Current page.
	 * 
	 * @param resource the Resource
	 * @return Locale
	 */
	public static Locale getLocaleFromPage(final Resource resource) {
		final PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
		final Page currentPage = pageManager.getContainingPage(resource);
		return currentPage.getLanguage(false);
	}

	/**
	 * Gets the product sku.
	 * 
	 * @param product Product
	 * @return String
	 */
	public static String getProductSku(final Product product) {
		String sku = product.getSKU();

		try {
			if (isBaseProduct(product)) {
				// If BaseProduct(displayed on nav_product), get Price from first Variant.
				Iterator<Product> unorderedVarients = product.getVariants();
				if (unorderedVarients != null && unorderedVarients.hasNext()) {
					sku = unorderedVarients.next().getSKU();
				}
			}
		} catch (CommerceException exception) {
			LOGGER.error("Error occured while fetching price. " + exception);
		}
		// Hack as size 9'2" is imported as 9_2 in DB.
		// Because it doesn't consume/import (')
		return modifySku(sku);

	}

	/**
	 * Modify the sku to correct format.
	 * 
	 * @param sku product sku code
	 * @return string
	 */
	private static String modifySku(final String sku) {
		if (StringUtils.contains(sku, "'")) {
			return sku.replace("'", "_").replace("\"", "");
		}
		return sku;
	}

	/**
	 * Checks if a Product is Base one or not.
	 * 
	 * @param product Product
	 * @return true, if BaseProduct
	 * @throws CommerceException Exception
	 */
	private static boolean isBaseProduct(final Product product) throws CommerceException {

		if (product == null) {
			return false;
		}
		final String baseSku = product.getBaseProduct().getSKU();
		if (StringUtils.isNotEmpty(baseSku) && baseSku.equalsIgnoreCase(product.getSKU())) {
			return true;
		}
		return false;
	}

}