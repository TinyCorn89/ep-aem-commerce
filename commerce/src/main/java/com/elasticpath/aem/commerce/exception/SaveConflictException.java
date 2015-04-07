package com.elasticpath.aem.commerce.exception;

/**
 * Exception thrown when a save conflict occurs.
 */
public class SaveConflictException extends Exception {
	/**
	 * Construct a SaveConflictException with a message.
	 * @param message the message
	 */
	public SaveConflictException(final String message) {
		super(message);
	}

	/**
	 * Construct a SaveConflictException with a message and a root cause.
	 * @param message the message.
	 * @param cause the root cause.
	 */
	public SaveConflictException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
