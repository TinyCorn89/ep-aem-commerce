package com.elasticpath.commerce.importer.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ImporterResultTest {


	private static final String TEST_MESSAGE = "Test message";
	private ImporterResult fixture;

	@Before
	public void init() {
		fixture = new ImporterResult(1);
	}

	@Test
	public void shouldPrintLogWhenMessageCapIsGreaterThanZeroAndMessageSizeEqualsToMessageCap() {

		final StringWriter stringWriter = new StringWriter();
		final PrintWriter writer = new PrintWriter(stringWriter);
		fixture.logMessage(TEST_MESSAGE, false);
		fixture.printLog(writer);

		Assert.assertEquals(TEST_MESSAGE + System.getProperty("line.separator") + "...", stringWriter.toString().trim());

	}

	@Test
	public void shouldPrintLogWhenMessageCapIsGreaterThanZeroAndMessageSizeLessThanMessageCap() {
		fixture = new ImporterResult(2);

		final StringWriter stringWriter = new StringWriter();
		final PrintWriter writer = new PrintWriter(stringWriter);
		fixture.logMessage(TEST_MESSAGE, false);
		fixture.printLog(writer);

		Assert.assertEquals(TEST_MESSAGE, stringWriter.toString().trim());
	}

	@Test
	public void shouldNotPrintLogWhenMessageCapIsEqualToZero() {
		fixture = new ImporterResult(0);

		final StringWriter stringWriter = new StringWriter();
		final PrintWriter writer = new PrintWriter(stringWriter);
		fixture.logMessage(TEST_MESSAGE, false);
		fixture.printLog(writer);

		Assert.assertEquals("", stringWriter.toString().trim());
	}

	@Test
	public void shouldNotPrintLogWhenMessageCapIsGreaterThanZeroAndThereAreNoMessages() {

		final StringWriter stringWriter = new StringWriter();
		final PrintWriter writer = new PrintWriter(stringWriter);
		fixture.printLog(writer);

		Assert.assertEquals("", stringWriter.toString().trim());
	}
}
