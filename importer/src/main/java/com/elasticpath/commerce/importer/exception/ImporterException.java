package com.elasticpath.commerce.importer.exception;

/**
 * Records importer specific exceptions.
 */
public class ImporterException extends RuntimeException {

	/**
	 * Constructor.
	 *
	 * @param message String
	 */
	public ImporterException(final String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param message   String
	 * @param exception Exception
	 */
	public ImporterException(final String message,
							 final Exception exception) {
		super(message, exception);
	}
}
