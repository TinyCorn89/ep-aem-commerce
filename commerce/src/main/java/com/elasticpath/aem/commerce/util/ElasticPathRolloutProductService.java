/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.util;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.Product;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;

/**
 * Encapsulates functionality used by the Elastic Path product rollout hook.
 *
 */
public interface ElasticPathRolloutProductService {
	
	/**
	 * copy tags with defined namespace from Product to Page and set last modified date to Page.
	 * 
	 * 
	 * @param productData The productData for which the product page was created
	 * @param productPage The product page created from the section blueprint's product template
	 * @param namespace namespace of tages to be copied
	 * @throws RepositoryException jcr repository exception
	 * @throws WCMException WCM operation failed
	 */
	void copyTagsWithNamespace(Product productData,
			Page productPage, String namespace)
			throws RepositoryException, WCMException;
	
	/**
	 * Give product pages a product-specific thumbnail so they don't have to fall back to the (generic) page_product template's thumbnail.
	 * This improves the usability of the pages content finder tab.
	 * 
	 * @param productData The productData for which the product page was created
	 * @param productPage The product page created from the section blueprint's product template
	 * @throws RepositoryException jcr repository exception
	 */
	void setThumbnailOnPage(Product productData,
			Page productPage) throws RepositoryException;

	/**
	 * set the product sku-code to productReference.
	 * 
	 * @param productData The productData for which the product page was created
	 * @param productReference The product reference on the created/updated page
	 * @throws RepositoryException jcr repository exception
	 */
	void setSkuCode(Product productData, Product productReference)
			throws RepositoryException;

	/**
	 * set the product sku-code to variants of productRefercence.
	 * 
	 * @param productData The productData for which the product page was created
	 * @param productReference The product reference on the created/updated page
	 * @throws CommerceException CommerceException
	 * @throws RepositoryException jcr repository exception
	 */
	void setSkuCodeOnProductVariants(Product productData, Product productReference) 
			throws CommerceException, RepositoryException;
	
	/**
	 * create a new image tag on the page node. The image tag link will point to the given imagePath. 
	 * 
	 * @param page the page to create the image node on to
	 * @param imagePath the path to the image 
	 * @return the created image tag
	 * @throws RepositoryException jcr repository exception
	 */
	Node createImageNode(Page page, String imagePath)
			throws RepositoryException;
}
