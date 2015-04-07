/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.commerce.config;

import java.util.Dictionary;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized location to configure WebConsole properties. Ref com.elasticpath.config.web.EPConfigurationHandler.xml
 */
@Component(
		immediate = true,
		metatype = true,
		label = "Elastic Path Demo Password Configuration",
		description = "Enabling demo password will cause all users registered in aem to have a cortex password of 'password'.")
@Service
public class DemoPasswordConfigurationImpl implements DemoPasswordConfiguration {
	private static final Logger LOG = LoggerFactory.getLogger(DemoPasswordConfigurationImpl.class);

	private static final String DEMO_PASSWORD_FOR_CORTEX_REGISTRATION = "password";
	/** Default demo password enabled. */
	static final boolean DEFAULT_DEMO_PASSWORD_ENABLED_FOR_CORTEX_REGISTRATION = false;

	/** Demo password enabled key. */
	@Property(boolValue = false,
			label = "Enable Demo Password",
			description = "Whether or not demo password should be used for Cortex registration.")
	static final String DEMO_PASSWORD_ENABLED_FOR_CORTEX_REGISTRATION = "demo.password.enabled.for.cortex.registration";

	private boolean demoPasswordEnabledForCortexRegistration;

	/**
	 * Activates and loads EP Commerce configuration properties.
	 *
	 * @param ctx Context.
	 */
	@Activate
	protected void activate(final ComponentContext ctx) {
		Dictionary<?, ?> props = ctx.getProperties();
		LOG.info("activate({})", props);
		setProperties(props);
	}

	/**
	 * Updates EP Commerce configuration properties from a Config Admin change.
	 *
	 * @param ctx Context.
	 */
	@Modified
	public void modified(final ComponentContext ctx) {
		Dictionary<?, ?> props = ctx.getProperties();
		LOG.info("modified({})", props);
		setProperties(props);
	}

	private void setProperties(final Dictionary<?, ?> props) {
		demoPasswordEnabledForCortexRegistration = PropertiesUtil.toBoolean(props.get(DEMO_PASSWORD_ENABLED_FOR_CORTEX_REGISTRATION),
				DEFAULT_DEMO_PASSWORD_ENABLED_FOR_CORTEX_REGISTRATION);
	}

	/**
	 * @return Whether or not demo password should be used for Cortex registration.
	 */
	@Override
	public boolean isDemoPasswordEnabledForCortexRegistration() {
		return demoPasswordEnabledForCortexRegistration;
	}

	/**
	 * @return Demo password for Cortex user registration
	 */
	@Override
	public String getDemoPasswordForCortexRegistration() {
		return DEMO_PASSWORD_FOR_CORTEX_REGISTRATION;
	}
}
