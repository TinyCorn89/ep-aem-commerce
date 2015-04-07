/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.cortex;

import com.adobe.cq.commerce.common.AbstractJcrCommerceServiceFactory;
import com.adobe.cq.commerce.common.ServiceContext;
import com.adobe.granite.crypto.CryptoSupport;

import com.elasticpath.aem.commerce.util.ElasticPathRolloutProductService;
import com.elasticpath.commerce.config.DemoPasswordConfiguration;
import com.elasticpath.aem.cookies.AemCortexContextManager;

/**
 * It contains all the Elastic path specific OSGI services required by Elastic path Commerce provider.
 */
public class CortexServiceContext extends ServiceContext {

	private final AemCortexContextManager cookieManager;

	/** The crypto support. */
	private final CryptoSupport cryptoSupport;

	/** The EPConfigurationHandler. */
	private final DemoPasswordConfiguration handler;

	/** The elasticPathRolloutProductService. */
	private final ElasticPathRolloutProductService elasticPathRolloutProductService;

	/**
	 * The static inner builder class.
	 */
	public static class Builder {

		/** The crypto support. */
		private CryptoSupport cryptoSupport;

		/** The EPConfigurationHandler. */
		private DemoPasswordConfiguration handler;

		private AemCortexContextManager cookieManager;

		/** The serviceFactory. */
		private final AbstractJcrCommerceServiceFactory serviceFactory;

		/** The elasticPathRolloutProductService. */
		private ElasticPathRolloutProductService elasticPathRolloutProductService;

		/**
		 * Builder constructor.
		 * 
		 * @param commerceServiceFactory the AbstractJcrCommerceServiceFactory
		 */
		public Builder(final AbstractJcrCommerceServiceFactory commerceServiceFactory) {
			serviceFactory = commerceServiceFactory;
		}

		/**
		 * Sets the EP Configuration Handler.
		 * 
		 * @param confHandler the handler to set
		 * @return Builder
		 */
		public Builder setHandler(final DemoPasswordConfiguration confHandler) {
			handler = confHandler;
			return this;
		}
		
		/**
		 * Sets the crypto support.
		 * 
		 * @param crypto the new crypto support
		 * @return Builder
		 */
		public Builder setCryptoSupport(final CryptoSupport crypto) {
			cryptoSupport = crypto;
			return this;
		}

		/**
		 * Set the cookie manager instance.
		 *
		 * @param cookieManager instance
		 * @return this Builder
		 */
		public Builder setCookieManager(final AemCortexContextManager cookieManager) {
			this.cookieManager = cookieManager;
			return this;
		}
		/**
		 * Set the ElasticPathRolloutProductService instance.
		 *
		 * @param elasticPathRolloutProductService instance
		 * @return this Builder
		 */
		public Builder setElasticPathRolloutProductService(
				final ElasticPathRolloutProductService elasticPathRolloutProductService) {
			this.elasticPathRolloutProductService = elasticPathRolloutProductService;
			return this;
		}

		/**
		 * Method to build the ElasticPathCartCapability class.
		 *
		 * @return ElasticPathCheckoutCapability
		 */
		public CortexServiceContext build() {
			return new CortexServiceContext(this);
		}
	}
	
	/**
	 * Instantiates a new Cortex configuration.
	 * 
	 * @param builder the Builder
	 */
	CortexServiceContext(final Builder builder) {

		super(builder.serviceFactory);
		this.cryptoSupport = builder.cryptoSupport;
		this.handler = builder.handler;
		this.cookieManager = builder.cookieManager;
		this.elasticPathRolloutProductService = builder.elasticPathRolloutProductService;
	}

	/**
	 * Gets the EP Configuration Handler.
	 * 
	 * @return the handler
	 */
	public DemoPasswordConfiguration getHandler() {
		return handler;
	}

	/**
	 * Gets the crypto support.
	 * 
	 * @return the crypto support
	 */
	public CryptoSupport getCryptoSupport() {
		return cryptoSupport;
	}

	public AemCortexContextManager getCookieManager() {
		return cookieManager;
	}
	
	public ElasticPathRolloutProductService getElasticPathRolloutProductService() {
		return elasticPathRolloutProductService;
	}

	@Override
	public String toString() {
		return "CortexServiceContext [cryptoSupport=" + cryptoSupport + "]";
	}
}
