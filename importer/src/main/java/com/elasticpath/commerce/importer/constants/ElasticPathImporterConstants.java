/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.commerce.importer.constants;

import com.google.common.base.Joiner;

/**
 * Constants for EP Commerce Importer.
 */
public final class ElasticPathImporterConstants {

	/**
	 * The Constant PATH.
	 */
	public static final String PATH = "path";

	/**
	 * The commerce provider request parameter.
	 */
	public static final String COMMERCE_PROVIDER = "commerce-provider";

	/**
	 * The Constant CODE.
	 */
	public static final String CODE = "code";

	/**
	 * The Constant CATALOG.
	 */
	public static final String CATALOG = "catalog";

	/**
	 * The Constant IDENTIFIER.
	 */
	public static final String IDENTIFIER = "identifier";

	/**
	 * The Constant PRODUCT_RESOURCE_TYPE.
	 */
	public static final String PRODUCT_RESOURCE_TYPE = "commerce/components/product";

	/**
	 * The Constant IMAGE_RESOURCE_TYPE.
	 */
	public static final String IMAGE_RESOURCE_TYPE = "commerce/components/product/image";

	/**
	 * The Constant CATALOG_ID.
	 */
	public static final String CATALOG_ID = "cq:epCatalogId";

	/**
	 * The Constant LAST_IMPORTED.
	 */
	public static final String LAST_IMPORTED = "cq:epLastImported";

	/**
	 * The base root where products should be imported to.
	 */
	public static final String COMMERCE_ROOT_PATH = "/etc/commerce/products";
	/**
	 * Joins paths.
	 */
	public static final Joiner PATH_JOINER = Joiner.on("/");
	/**
	 * JMS related - default initial message timeout during message reception.
	 */
	public static final int DEFAULT_INITIAL_MESSAGE_TIMEOUT = 10000;
	/**
	 * JMS related - default polling message timeout during message reception.
	 */
	public static final int DEFAULT_POLLING_MESSAGE_TIMEOUT = 10000;
}
