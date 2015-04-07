package com.elasticpath.commerce.importer.jcr.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PropertyMapperTest {

	private final PropertyMapper propertyMapper = new PropertyMapper();

	@Mock
	private Node node;

	@Test
	public void ensureNullKeyIsNotSet() throws RepositoryException {
		propertyMapper.writeProperty(node, null, null);

		verifyZeroInteractions(node);
	}

	@Test
	public void ensureTagKeyIsNotSet() throws RepositoryException {
		String key = "tag";

		propertyMapper.writeProperty(node, key, null);

		verifyZeroInteractions(node);
	}

	@Test
	public void ensureImagesKeyIsNotSet() throws RepositoryException {
		String key = "images";

		propertyMapper.writeProperty(node, key, null);

		verifyZeroInteractions(node);
	}

	@Test
	public void ensureBaseProductCodeKeyIsNotSet() throws RepositoryException {
		String key = "baseProductCode";

		propertyMapper.writeProperty(node, key, null);

		verifyZeroInteractions(node);
	}

	@Test
	public void ensureParentCodeKeyIsNotSet() throws RepositoryException {
		String key = "parentcode";

		propertyMapper.writeProperty(node, key, null);

		verifyZeroInteractions(node);
	}

	@Test
	public void ensureCodeKeyIsNotSet() throws RepositoryException {
		String key = "code";

		propertyMapper.writeProperty(node, key, null);

		verifyZeroInteractions(node);
	}

	@Test
	public void ensureStringListValueIsSet() throws RepositoryException {
		String key = anyKey();
		List<String> value = Arrays.asList("");

		propertyMapper.writeProperty(node, key, value);

		verify(node).setProperty(key, value.toArray(new String[value.size()]));
	}

	@Test
	public void ensureStringValueIsSet() throws RepositoryException {
		String key = anyKey();
		String value = "";

		propertyMapper.writeProperty(node, key, value);

		verify(node).setProperty(key, value);
	}

	@SuppressWarnings("ConstantConditions")
	@Test
	public void ensureBooleanValueIsSet() throws RepositoryException {
		String key = anyKey();
		boolean value = false;

		propertyMapper.writeProperty(node, key, value);

		verify(node).setProperty(key, value);
	}

	@Test
	public void ensureDoubleValueIsSet() throws RepositoryException {
		String key = anyKey();
		double value = 0;

		propertyMapper.writeProperty(node, key, value);

		verify(node).setProperty(key, value);
	}

	@Test
	public void ensureLongValueIsSet() throws RepositoryException {
		String key = anyKey();
		long value = 0;

		propertyMapper.writeProperty(node, key, value);

		verify(node).setProperty(key, value);
	}

	@Test
	public void ensureBigDecimalValueIsSet() throws RepositoryException {
		String key = anyKey();
		BigDecimal value = BigDecimal.ZERO;

		propertyMapper.writeProperty(node, key, value);

		verify(node).setProperty(key, value);
	}

	@Test
	public void ensureDateValueIsSet() throws RepositoryException {
		String key = anyKey();
		Date value = new Date();
		Calendar calendarValue = Calendar.getInstance();
		calendarValue.setTime(value);

		propertyMapper.writeProperty(node, key, value);

		verify(node).setProperty(key, calendarValue);
	}

	private String anyKey() {
		return "any-key";
	}

}