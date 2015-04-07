package com.elasticpath.rest.sdk.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

import com.elasticpath.rest.sdk.components.Choice;
import com.elasticpath.rest.sdk.components.Linkable;
import com.elasticpath.rest.sdk.components.Self;

/**
 * Test for {@link com.elasticpath.rest.sdk.service.impl.AddressChoiceFilter}.
 */
public class AddressChoiceFilterTest {

	private AddressChoiceFilter addressChoiceFilter;

	/**
	 * Tests {@link AddressChoiceFilter#evaluate(Object)} if addressPath is same.
	 */
	@Test
	public void testMatch() {
		final String addressPath = "addressPath";
		final Choice choice = newChoice(addressPath);
		addressChoiceFilter = new AddressChoiceFilter(addressPath);
		assertTrue(addressChoiceFilter.evaluate(choice));
	}

	/**
	 * Tests {@link AddressChoiceFilter#evaluate(Object)} if addressPath is different.
	 */
	@Test
	public void testNotMatch() {
		final String addressPath = "addressPath";
		final Choice choice = newChoice(addressPath + "1");
		addressChoiceFilter = new AddressChoiceFilter(addressPath);
		assertFalse(addressChoiceFilter.evaluate(choice));
	}

	private Choice newChoice(final String uri) {
		final Choice choice = new Choice();
		choice.setDescriptions(Collections.singletonList(newDescription(uri)));
		return choice;
	}

	private Linkable newDescription(final String uri) {
		final Linkable description = new Linkable();
		description.setSelf(newSelf(uri));
		return description;
	}

	private Self newSelf(final String uri) {
		final Self self = new Self();
		self.setUri(uri);
		return self;
	}

}
