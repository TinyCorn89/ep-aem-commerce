/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce;

import static com.elasticpath.commerce.config.ElasticPathGlobalConstants.COMMERCE_PROVIDER;

import javax.ws.rs.client.Client;

import com.adobe.cq.commerce.common.AbstractJcrCommerceServiceFactory;
import com.adobe.granite.crypto.CryptoSupport;

import com.elasticpath.aem.commerce.service.UserPropertiesService;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.elasticpath.aem.commerce.util.ElasticPathRolloutProductService;
import com.elasticpath.commerce.config.DemoPasswordConfiguration;
import com.elasticpath.aem.commerce.cortex.CortexServiceContext;
import com.elasticpath.aem.commerce.util.impl.ElasticPathRolloutProductServiceImpl;
import com.elasticpath.aem.cookies.AemCortexContextManager;
import com.elasticpath.rest.client.CortexClientFactory;

/**
 * Elastic Path-specific implementation for the {@link com.adobe.cq.commerce.api.CommerceServiceFactory} interface.
 */
@Component
@Service
@Properties(value = {
		@Property(name = "service.description", value = "Factory for Elastic Path implementation of CommerceServiceFactory"),
		@Property(name = "commerceProvider", value = COMMERCE_PROVIDER),
		@Property(name = "commerceProviderLabel", value = "ElasticPath Commerce Provider")})
public abstract class AbstractElasticPathCommerceServiceFactory
		extends AbstractJcrCommerceServiceFactory implements ElasticPathCommerceServiceFactory {

	@Reference
	private CryptoSupport cryptoSupport;

	@Reference
	private DemoPasswordConfiguration handler;

	@Reference
	private AemCortexContextManager cookieManager;

	@Reference
	private CortexClientFactory cortexClientFactory;

	@Reference
	private Client jaxRsClient;

	@Reference
	private UserPropertiesService userPropertiesService;
	/**
	 * Returns EPConfigurationHandler.
	 *
	 * @return the handler
	 */
	public DemoPasswordConfiguration getHandler() {
		return handler;
	}

	/**
	 * Sets EPConfigurationHandler.
	 *
	 * @param handler - Configurations specific to ElasticPath
	 */
	public void setHandler(final DemoPasswordConfiguration handler) {
		this.handler = handler;
	}

	/**
	 * Gets the service context.
	 *
	 * @return CortexServiceContext
	 */
	@Override
	public CortexServiceContext getServiceContext() {
		ElasticPathRolloutProductService elasticPathRolloutProductService = new ElasticPathRolloutProductServiceImpl();
		return new CortexServiceContext.Builder(this)
				.setCryptoSupport(cryptoSupport)
				.setCookieManager(cookieManager)
				.setHandler(handler)
				.setElasticPathRolloutProductService(elasticPathRolloutProductService)
				.build();
	}

	/**
	 * Gets the CortexClientFactory.
	 *
	 * @return The client factory.
	 */
	protected CortexClientFactory getCortexClientFactory() {
		return cortexClientFactory;
	}

	/**
	 * Gets the JAX-RS Client.
	 *
	 * @return The client.
	 */
	protected Client getJaxRsClient() {
		return jaxRsClient;
	}

	/**
	 * Gets the UserProperties service, for updating the user info.
	 * @return the service.
	 */
	protected UserPropertiesService getUserPropertiesService() {
		return userPropertiesService;
	}
}