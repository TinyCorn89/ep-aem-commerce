/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.jms.api;

import javax.jms.ConnectionFactory;

/**
 * Provides a jms connection factory.
 */
public interface JmsConnectionFactoryProvider {
	/**
	 * Create a jms connection factory. This is not guaranteed to return the same ConnectionFactory on subsequent invocations.
	 * @return ConnectionFactory
	 */
	ConnectionFactory create();
}
