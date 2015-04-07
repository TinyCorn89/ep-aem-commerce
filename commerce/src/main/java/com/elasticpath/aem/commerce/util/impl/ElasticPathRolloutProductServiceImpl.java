/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.util.impl;

import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;
import static com.elasticpath.commerce.config.ElasticPathGlobalConstants.PRODUCT_IDENTIFIER;

import java.util.Calendar;
import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.adobe.cq.commerce.api.CommerceConstants;
import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.common.CommerceHelper;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;

import org.apache.commons.collections.Predicate;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;

import com.elasticpath.aem.commerce.util.ElasticPathRolloutProductService;

/**
 * Encapsulates functionality used by the Elastic Path product rollout hook.
 *
 */
public class ElasticPathRolloutProductServiceImpl implements
		ElasticPathRolloutProductService {

	@Override
	public void copyTagsWithNamespace(final Product productData, final Page productPage,
			final String namespace) throws RepositoryException, WCMException {
		boolean tagsCopiedToPage = CommerceHelper.copyTags(productData,
				productPage.getContentResource(),
				createProductDataTagNamespaceMatcherPredicate(namespace));
		if (tagsCopiedToPage) {
			// set last modified date
			productPage.getPageManager().touch(productPage.adaptTo(Node.class),
					true, Calendar.getInstance(), false);
		}
	}

	private Predicate createProductDataTagNamespaceMatcherPredicate(
			final String tagNamespaceGeometrixxOutdoors) {
		Predicate predicate = new Predicate() {
			public boolean evaluate(final Object tag) {
				Tag productDataTag = (Tag) tag;
				boolean productDataTagNameSpaceIsGeometrixxOutdoors = productDataTag
						.getNamespace().getName()
						.equals(tagNamespaceGeometrixxOutdoors);
				return productDataTagNameSpaceIsGeometrixxOutdoors;
			}
		};
		return predicate;
	}

	@Override
	public void setThumbnailOnPage(final Product productData, final Page productPage)
			throws RepositoryException {
		if (isPageProxy(productPage)) {
			return;
		}
		if (productContainsImage(productData)) {
			String productImageRef = ResourceUtil.getValueMap(
					productData.getImage()).get("fileReference", "");
			createImageNode(productPage, productImageRef);
		}
	}
	

	@Override
	public Node createImageNode(final Page page, final String imagePath)
			throws RepositoryException {			
		Node contentNode = page.getContentResource().adaptTo(Node.class);
		Node pageImageNode = JcrUtils.getOrAddNode(contentNode, "image", NT_UNSTRUCTURED);
		pageImageNode.setProperty("fileReference", imagePath);
		return pageImageNode;
	}

	private boolean productContainsImage(final Product productData) {
		Resource productImage = productData.getImage();
		return productImage != null;
	}

	private boolean isPageProxy(final Page productPage) {
		return ResourceUtil.isA(productPage.getContentResource(),
				CommerceConstants.RT_PRODUCT_PAGE_PROXY);
	}
	
	@Override
	public void setSkuCode(final Product productData, final Product productReference)
			throws RepositoryException {
		Node productReferenceNode = productReference.adaptTo(Node.class);
		productReferenceNode.setProperty(PRODUCT_IDENTIFIER, productData.getSKU());
	}
	
	@Override
	public void setSkuCodeOnProductVariants(final Product productData, final Product productReference)
			throws RepositoryException, CommerceException {
		// copy variant IDs or something to that effect
		Iterator<Product> variants = productReference.getVariants();
		while (variants.hasNext()) {
			Product productVariant = variants.next();
			setSkuCode(productVariant, productVariant);
		}
	}

}
