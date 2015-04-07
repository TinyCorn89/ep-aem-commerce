package com.elasticpath.commerce.config;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.service.component.ComponentContext;

import java.util.Dictionary;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DemoPasswordConfigurationImplTest {

	@Mock
	private ComponentContext mockCtx;
	@Mock
	private Dictionary mockProperties;

	private final DemoPasswordConfigurationImpl configurationHandler = new DemoPasswordConfigurationImpl();


	@Test
	public void testActivateDefaults() throws Exception {
		when(mockCtx.getProperties()).thenReturn(mockProperties);

		configurationHandler.activate(mockCtx);

		assertEquals(DemoPasswordConfigurationImpl.DEFAULT_DEMO_PASSWORD_ENABLED_FOR_CORTEX_REGISTRATION,
			configurationHandler.isDemoPasswordEnabledForCortexRegistration());
	}

	@Test
	public void testActivateHappy() throws Exception {
		when(mockCtx.getProperties()).thenReturn(mockProperties);

		configurationHandler.activate(mockCtx);

		assertEquals(DemoPasswordConfigurationImpl.DEFAULT_DEMO_PASSWORD_ENABLED_FOR_CORTEX_REGISTRATION,
			configurationHandler.isDemoPasswordEnabledForCortexRegistration());
	}

	@Test
	public void testModified() throws Exception {
		when(mockCtx.getProperties()).thenReturn(mockProperties);

		configurationHandler.modified(mockCtx);

		assertEquals(DemoPasswordConfigurationImpl.DEFAULT_DEMO_PASSWORD_ENABLED_FOR_CORTEX_REGISTRATION,
			configurationHandler.isDemoPasswordEnabledForCortexRegistration());
	}
}