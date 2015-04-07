package com.elasticpath.commerce.importer.config;

/**
 * Configuration variables for handling specific behaviour in how the importer processes messages and buckets.
 */
public interface ImporterConfig {

	/**
	 * Gets the max size for a folder before bucketing begins.
	 *
	 * @return long
	 */
	long getBucketMax();

	/**
	 * Max amount of messages to gather in one importer run.
	 *
	 * @return int
	 */
	int getMessageCap();

	/**
	 * Save batch size.
	 *
	 * @return int
	 */
	int getSaveBatchSize();

	/**
	 * Throttle batch size.
	 *
	 * @return int
	 */
	int getThrottleBatchSize();

	/**
	 * Import timeout (in milliseconds). Used to configure the polling timeouts on
	 * {@link com.elasticpath.commerce.importer.importers.impl.CategoryListener} and
	 * {@link com.elasticpath.commerce.importer.importers.impl.ProductListener}
	 * @return int
	 */
	int getImportPollingTimeout();

	/**
	 * Initial import timeout (in milliseconds). Used to initialize the Category and Product
	 * listeners.
	 * @return int
	 */
	int getImportInitialTimeout();
}
