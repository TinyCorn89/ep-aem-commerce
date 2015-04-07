package com.elasticpath.rest.sdk.service;

import java.util.List;
import java.util.Locale;

import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.api.Product;

/**
 * Product Service is responsible for retrieving the price of a Product from Cortex.
 */
public interface ProductService {


	/**
	 * Retrieve the price for a product from Cortex given the supplied sku code.
	 * @param product the AEM product.
	 * @param locale the locale.
	 * @return the product price.
	 */
	List<PriceInfo> getProductPrice(Product product, Locale locale);
}
