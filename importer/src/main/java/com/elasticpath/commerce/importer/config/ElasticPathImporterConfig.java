package com.elasticpath.commerce.importer.config;

import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.DEFAULT_INITIAL_MESSAGE_TIMEOUT;
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.DEFAULT_POLLING_MESSAGE_TIMEOUT;

import java.util.Dictionary;

import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

/**
 * Configures the importer.
 */
public class ElasticPathImporterConfig implements ImporterConfig, ManagedService {

	/**
	 * Save batch size.
	 */
	protected static final String SAVE_BATCH_SIZE_PROP_NAME = "cq.commerce.importer.savebatchsize";
	/**
	 * Throttle batch size.
	 */
	protected static final String THROTTLE_BATCH_SIZE_PROP_NAME = "cq.commerce.importer.throttlebatchsize";
	/**
	 * Message cap.
	 */
	protected static final String MESSAGE_CAP_PROP_NAME = "cq.commerce.importer.messagecap";
	/**
	 * Bucket size.
	 */
	protected static final String BUCKET_SIZE_PROP_NAME = "cq.commerce.productimporter.bucketsize";
	/**
	 * Import polling timeout.
	 */
	protected static final String IMPORT_POLLING_TIMEOUT_PROP_NAME = "cq.commerce.importer.pollingtimeout";

	/**
	 * Import initial timeout.
	 */
	protected static final String IMPORT_INITIAL_TIMEOUT_PROP_NAME = "cq.commerce.importer.initialtimeout";
	private long bucketMax;
	private int messageCap;
	private int saveBatchSize;
	private int throttleBatchSize;
	private int importPollingTimeout;
	private int importInitialTimeout;

	private static int toInteger(final Dictionary properties,
								 final String key,
								 final int defaultValue) {
		if (properties == null) {
			return defaultValue;
		}

		return PropertiesUtil.toInteger(properties.get(key), defaultValue);
	}

	private static long toLong(final Dictionary properties,
							   final String key,
							   final int defaultValue) {
		if (properties == null) {
			return defaultValue;
		}

		return PropertiesUtil.toLong(properties.get(key), defaultValue);
	}

	@SuppressWarnings("checkstyle:magicnumber")
	@Override
	public void updated(final Dictionary properties) throws ConfigurationException {
		saveBatchSize = toInteger(properties, SAVE_BATCH_SIZE_PROP_NAME, 1000);
		throttleBatchSize = toInteger(properties, THROTTLE_BATCH_SIZE_PROP_NAME, 50000);
		messageCap = toInteger(properties, MESSAGE_CAP_PROP_NAME, 1000);
		bucketMax = toLong(properties, BUCKET_SIZE_PROP_NAME, 500);
		importPollingTimeout = validateTimeout(properties, IMPORT_POLLING_TIMEOUT_PROP_NAME, DEFAULT_POLLING_MESSAGE_TIMEOUT);
		importInitialTimeout = validateTimeout(properties, IMPORT_INITIAL_TIMEOUT_PROP_NAME, DEFAULT_INITIAL_MESSAGE_TIMEOUT);
	}

	private int validateTimeout(final Dictionary properties, final String key, final int defaultTimeout) throws ConfigurationException {
		int timeout = toInteger(properties, key, defaultTimeout);

		if (timeout <= 0) {
			throw new ConfigurationException(key,
					String.format("Timeout value (%d) invalid. Must be between 1 and %d",
							timeout,
							Integer.MAX_VALUE));
		} else {
			return timeout;
		}
	}

	@Override
	public long getBucketMax() {
		return bucketMax;
	}

	@Override
	public int getMessageCap() {
		return messageCap;
	}

	@Override
	public int getSaveBatchSize() {
		return saveBatchSize;
	}

	@Override
	public int getThrottleBatchSize() {
		return throttleBatchSize;
	}

	@Override
	public int getImportPollingTimeout() {
		return importPollingTimeout;
	}

	@Override
	public int getImportInitialTimeout() {
		return importInitialTimeout;
	}
}
