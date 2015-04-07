package com.elasticpath.rest.sdk;

/**
 * Cortex Entry Point Uris.
 */
public class EntryPointUris {

	/**
	 * The URI of the default cart for the current user.
	 */
	public static final String CART = "/carts/{scope}/default";

	/**
	 * The URI of the profile for the current user.
	 */
	public static final String PROFILE = "/profiles/{scope}/default";

	/**
	 * The URI for looking up an item by sku code.
	 */
	public static final String ITEM_LOOKUP = "/lookups/{scope}/items";

	/**
	 * The URI for searching for an item by keywords.
	 */
	public static final String ITEM_SEARCH = "/searches/{scope}/keywords/items";

	/**
	 * The URI for POSTing new email addresses.
	 */
	public static final String EMAILS = "/emails/{scope}";

	/**
	 * The URI for accessing geographic information about countries.
	 */
	public static final String COUNTRIES = "/geographies/{scope}/countries";
}
