/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.commerce.config;

/**
 * Interface for configuration handler.
 */
public interface DemoPasswordConfiguration {

	/**
	 * Is the demo password enabled for Cortex registration.
	 * @return true if demo password is enabled
	 */
	boolean isDemoPasswordEnabledForCortexRegistration();

	/**
	 * Get the demo password.
	 * @return the password
	 */
	String getDemoPasswordForCortexRegistration();
}
