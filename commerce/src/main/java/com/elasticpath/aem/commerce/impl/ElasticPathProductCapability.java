/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.impl;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.Product;

import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.aem.commerce.util.ElasticPathCommerceUtil;

/**
 * This helper class provides implementations related to product.
 */
class ElasticPathProductCapability {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ElasticPathProductCapability.class);

	/** The resource. */
	private final Resource resource;

	/**
	 * The static inner builder class.
	 */
	static class Builder {

		/** The resource. */
		private Resource resource;


		/**
		 * This initializes the resource variable.
		 * 
		 * @param res the resource
		 * @return Builder
		 */
		Builder setResource(final Resource res) {
			resource = res;
			return this;
		}


		/**
		 * Method to initialize the ElasticPathProductCapability class.
		 * 
		 * @return ElasticPathProductCapability
		 */
		ElasticPathProductCapability build() {
			return new ElasticPathProductCapability(resource);
		}
	}

	/**
	 * ElasticPathProductCapability constructor.
	 * 
	 * @param resource the resource
	 */
	ElasticPathProductCapability(final Resource resource) {
		this.resource = resource;
	}

	/**
	 * Find product on the basis of product name.
	 * 
	 * @param res Resource
	 * @param name String the product name
	 * @return Product
	 * @throws CommerceException CommerceException
	 */
	Product getProductByName(final Resource res, final String name) throws CommerceException {
		try {
			Resource baseStore = ElasticPathCommerceUtil.findBaseStore(res);
			Product product = findProduct(baseStore.getPath(), name);

			LOG.debug("Successfully gets the product {}", name);
			return product;
		} catch (final RepositoryException repositoryException) {
			throw new CommerceException("Error occurred while fetching product for [" + name + "]", repositoryException);
		}
	}

	/**
	 * Find product on the basis of product id.
	 * 
	 * @param basePath String the product base path
	 * @param productId String the product id
	 * @return Product
	 * @throws CommerceException commerceException
	 * @throws RepositoryException repositoryException
	 */
	private Product findProduct(final String basePath, final String productId) throws CommerceException, RepositoryException {
		Session session = resource.getResourceResolver().adaptTo(Session.class);
		StringBuilder queryString = new StringBuilder("SELECT * FROM [nt:base] WHERE ISDESCENDANTNODE('");
		queryString.append(basePath);
		queryString.append("') AND [jcr:title] = '");
		queryString.append(productId);
		queryString.append('\'');
		Query query = session.getWorkspace().getQueryManager().createQuery(queryString.toString(), Query.JCR_SQL2);
		NodeIterator iter = query.execute().getNodes();

		if (iter.hasNext()) {
			Node node = iter.nextNode();
			if (node.hasProperty("cq:productMaster")) {
				return ElasticPathCommerceUtil.getProduct(node.getProperty("cq:productMaster").getString(), resource.getResourceResolver());
			}
		}
		return null;
	}
}