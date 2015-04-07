package com.elasticpath.rest.sdk.service;

import java.util.Locale;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.Product;

import com.elasticpath.rest.sdk.Cart;

/**
 * Cart service.
 */
public interface CartService {

	/**
	 * Get the cart data.
	 * @param userLocale The locale.
	 * @return Cart - The cart data.
	 */
	Cart getCortexCart(Locale userLocale);

	/**
	 * Invalidate the cart.
	 */
	void invalidateCart();

	/**
	 * Adds a product to cart.
	 * @param product The product.
	 * @param quantity How many.
	 * @throws CommerceException When the add fails.
	 */
	void addCartEntry(Product product, int quantity) throws CommerceException;

	/**
	 * Modify the quantity of items in the shopping cart.
	 * @param entryNumber the entry number of the cart item.
	 * @param quantity the new quantity to adjust to.
	 * @param userLocale the user locale.
	 * @throws CommerceException when the entry modification fails.
	 */
	void modifyCartEntry(int entryNumber, int quantity, Locale userLocale) throws CommerceException;

	/**
	 * Delete and entry from the shopping cart.
	 * @param entryNumber the entry number of the cart item to delete.
	 * @param userLocale the user locale.
	 * @throws CommerceException when the delete fails.
	 */
	void deleteCartEntry(int entryNumber, Locale userLocale) throws CommerceException;
}
