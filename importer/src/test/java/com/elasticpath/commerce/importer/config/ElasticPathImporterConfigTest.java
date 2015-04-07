package com.elasticpath.commerce.importer.config;

import static com.elasticpath.commerce.importer.config.ElasticPathImporterConfig.BUCKET_SIZE_PROP_NAME;
import static com.elasticpath.commerce.importer.config.ElasticPathImporterConfig.SAVE_BATCH_SIZE_PROP_NAME;
import static com.elasticpath.commerce.importer.config.ElasticPathImporterConfig.THROTTLE_BATCH_SIZE_PROP_NAME;
import static com.elasticpath.commerce.importer.config.ElasticPathImporterConfig.MESSAGE_CAP_PROP_NAME;
import static com.elasticpath.commerce.importer.config.ElasticPathImporterConfig.IMPORT_POLLING_TIMEOUT_PROP_NAME;
import static com.elasticpath.commerce.importer.config.ElasticPathImporterConfig.IMPORT_INITIAL_TIMEOUT_PROP_NAME;
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.DEFAULT_INITIAL_MESSAGE_TIMEOUT;
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.DEFAULT_POLLING_MESSAGE_TIMEOUT;
import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.service.cm.ConfigurationException;

@SuppressWarnings("checkstyle:magicnumber")
@RunWith(MockitoJUnitRunner.class)
public class ElasticPathImporterConfigTest {

	private ElasticPathImporterConfig fixture;
	@Rule
	public ExpectedException exception = ExpectedException.none();
	@Before
	public void init() {
		fixture = new ElasticPathImporterConfig();
	}

	@Test
	public void shouldSetDefaultValuesWhenPropertiesAreNull() throws ConfigurationException {
		fixture.updated(null);

		assertEquals(1000, fixture.getSaveBatchSize());
		assertEquals(50000, fixture.getThrottleBatchSize());
		assertEquals(1000, fixture.getMessageCap());
		assertEquals(500, fixture.getBucketMax());
		assertEquals(DEFAULT_POLLING_MESSAGE_TIMEOUT, fixture.getImportPollingTimeout());
		assertEquals(DEFAULT_INITIAL_MESSAGE_TIMEOUT, fixture.getImportInitialTimeout());
	}

	@Test
	public void shouldSetDefaultValuesFromProperties() throws ConfigurationException {
		final Properties props = new Properties();
		props.put(SAVE_BATCH_SIZE_PROP_NAME, 1);
		props.put(THROTTLE_BATCH_SIZE_PROP_NAME, 2);
		props.put(MESSAGE_CAP_PROP_NAME, 3);
		props.put(BUCKET_SIZE_PROP_NAME, 4);
		props.put(IMPORT_POLLING_TIMEOUT_PROP_NAME, 5);

		fixture.updated(props);

		assertEquals(1, fixture.getSaveBatchSize());
		assertEquals(2, fixture.getThrottleBatchSize());
		assertEquals(3, fixture.getMessageCap());
		assertEquals(4, fixture.getBucketMax());
		assertEquals(5, fixture.getImportPollingTimeout());
	}

	@Test
	public void shouldErrorIfImportPollingTimeoutLessThanZero() throws ConfigurationException {
		final Properties props = new Properties();
		props.put(SAVE_BATCH_SIZE_PROP_NAME, 1);
		props.put(THROTTLE_BATCH_SIZE_PROP_NAME, 2);
		props.put(MESSAGE_CAP_PROP_NAME, 3);
		props.put(BUCKET_SIZE_PROP_NAME, 4);
		props.put(IMPORT_POLLING_TIMEOUT_PROP_NAME, -1);

		exception.expect(ConfigurationException.class);
		fixture.updated(props);
	}

	@Test
	public void shouldErrorIfImportPollingTimeoutEqualsZero() throws ConfigurationException {
		final Properties props = new Properties();
		props.put(SAVE_BATCH_SIZE_PROP_NAME, 1);
		props.put(THROTTLE_BATCH_SIZE_PROP_NAME, 2);
		props.put(MESSAGE_CAP_PROP_NAME, 3);
		props.put(BUCKET_SIZE_PROP_NAME, 4);
		props.put(IMPORT_POLLING_TIMEOUT_PROP_NAME, 0);

		exception.expect(ConfigurationException.class);
		fixture.updated(props);
	}

	@Test
	public void shouldErrorIfImportPollingTimeoutGreaterThanInt() throws ConfigurationException {
		final Properties props = new Properties();
		props.put(SAVE_BATCH_SIZE_PROP_NAME, 1);
		props.put(THROTTLE_BATCH_SIZE_PROP_NAME, 2);
		props.put(MESSAGE_CAP_PROP_NAME, 3);
		props.put(BUCKET_SIZE_PROP_NAME, 4);
		props.put(IMPORT_POLLING_TIMEOUT_PROP_NAME, Integer.MAX_VALUE + 1);

		exception.expect(ConfigurationException.class);
		fixture.updated(props);
	}
	@Test
	public void shouldErrorIfImportInitialTimeoutLessThanZero() throws ConfigurationException {
		final Properties props = new Properties();
		props.put(SAVE_BATCH_SIZE_PROP_NAME, 1);
		props.put(THROTTLE_BATCH_SIZE_PROP_NAME, 2);
		props.put(MESSAGE_CAP_PROP_NAME, 3);
		props.put(BUCKET_SIZE_PROP_NAME, 4);
		props.put(IMPORT_INITIAL_TIMEOUT_PROP_NAME, -1);

		exception.expect(ConfigurationException.class);
		fixture.updated(props);
	}

	@Test
	public void shouldErrorIfImportInitialTimeoutEqualsZero() throws ConfigurationException {
		final Properties props = new Properties();
		props.put(SAVE_BATCH_SIZE_PROP_NAME, 1);
		props.put(THROTTLE_BATCH_SIZE_PROP_NAME, 2);
		props.put(MESSAGE_CAP_PROP_NAME, 3);
		props.put(BUCKET_SIZE_PROP_NAME, 4);
		props.put(IMPORT_INITIAL_TIMEOUT_PROP_NAME, 0);

		exception.expect(ConfigurationException.class);
		fixture.updated(props);
	}

	@Test
	public void shouldErrorIfImportInitialTimeoutGreaterThanInt() throws ConfigurationException {
		final Properties props = new Properties();
		props.put(SAVE_BATCH_SIZE_PROP_NAME, 1);
		props.put(THROTTLE_BATCH_SIZE_PROP_NAME, 2);
		props.put(MESSAGE_CAP_PROP_NAME, 3);
		props.put(BUCKET_SIZE_PROP_NAME, 4);
		props.put(IMPORT_INITIAL_TIMEOUT_PROP_NAME, Integer.MAX_VALUE + 1);

		exception.expect(ConfigurationException.class);
		fixture.updated(props);
	}
}
