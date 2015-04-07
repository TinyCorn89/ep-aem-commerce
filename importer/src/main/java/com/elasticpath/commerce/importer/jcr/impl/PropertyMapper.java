package com.elasticpath.commerce.importer.jcr.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Provides methods for mapping keys and values onto JCR {@link Node}s.
 */
public class PropertyMapper {

	/**
	 * Copy the given key and value onto a {@link Node}.
	 *
	 * @param node  the destination {@link Node}
	 * @param key   the key
	 * @param value the value
	 * @throws RepositoryException if a problem occurs working with the node
	 */
	public void writeProperty(final Node node,
							  final String key,
							  final Object value) throws RepositoryException {

		if (value instanceof List<?> && ((List<?>) value).size() > 0) {
			List<?> list = (List<?>) value;
			if (list.get(0) instanceof String) {
				node.setProperty(key, list.toArray(new String[list.size()]));
			}
		} else if (value instanceof String) {
			node.setProperty(key, (String) value);
		} else if (value instanceof Boolean) {
			node.setProperty(key, (Boolean) value);
		} else if (value instanceof Double) {
			node.setProperty(key, (Double) value);
		} else if (value instanceof Long) {
			node.setProperty(key, (Long) value);
		} else if (value instanceof BigDecimal) {
			node.setProperty(key, (BigDecimal) value);
		} else if (value instanceof Date) {
			Calendar cal = Calendar.getInstance();
			cal.setTime((Date) value);
			node.setProperty(key, cal);
		}
	}

	/**
	 * Writes properties for given {@link javax.jcr.Node}.
	 *
	 * @param node       the {@link javax.jcr.Node}
	 * @param properties the properties
	 * @throws RepositoryException if a problem occurs working with the node
	 */
	public void writeProperties(final Node node,
								final Map<String, Object> properties) throws RepositoryException {
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			writeProperty(
					node,
					entry.getKey(),
					entry.getValue()
			);
		}
	}
}