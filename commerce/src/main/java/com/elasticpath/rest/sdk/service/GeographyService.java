package com.elasticpath.rest.sdk.service;

import java.util.List;
import java.util.Map;

/**
 * Responsible for looking up Geographic information from Cortex.
 */
public interface GeographyService {

	/**
	 * Gets the countries from Cortex.
	 * @return the available countries.
	 */
	List<String> getAvailableCountries();

	/**
	 * Gets the regions for each country from Cortex.
	 * @return the available regions by country.
	 */
	Map<String, List<String>> getRegions();
}
