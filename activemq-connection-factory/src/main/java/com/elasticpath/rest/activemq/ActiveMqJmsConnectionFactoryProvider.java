/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.activemq;

import java.util.Dictionary;
import java.util.Locale;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.jms.api.JmsConnectionFactoryProvider;

/**
 * ActiveMQ implementation of JmsConnectionFactoryProvider.
 */
@Component(
		immediate = true,
		metatype = true,
		label = "Elastic Path ActiveMQ Connection Configuration",
		description = "ActiveMQ Connection Configuration")
@Service
public class ActiveMqJmsConnectionFactoryProvider implements JmsConnectionFactoryProvider {
	private static final Logger LOG = LoggerFactory.getLogger(ActiveMqJmsConnectionFactoryProvider.class);
	/** Default JMS Broker URL. */
	static final String DEFAULT_JMS_BROKER_URL = "tcp://localhost:61616";

	/** JMS Broker URL key. */
	@Property(value = ActiveMqJmsConnectionFactoryProvider.DEFAULT_JMS_BROKER_URL,
			label = "JMS Broker URL",
			description = "URL for connecting to JMS Broker.")
	static final String JMS_BROKER_URL = "jms.broker.url";

	private String jmsBrokerUrl;

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
		jmsBrokerUrl = normalizeJmsUrl(PropertiesUtil.toString(props.get(JMS_BROKER_URL), DEFAULT_JMS_BROKER_URL));
	}

	private String normalizeJmsUrl(final String cortexURL) {
		String trimmed = cortexURL.trim().toLowerCase(Locale.getDefault());
		return trimmed.replaceAll("/+$", "");
	}


	@Override
	public ConnectionFactory create() {
		return new ActiveMQConnectionFactory(jmsBrokerUrl);
	}
}
