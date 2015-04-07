package com.elasticpath.commerce.importer.impl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains result data for an importer run.
 */
public class ImporterResult {

	private final int messageCap;

	private final List<String> messages = new ArrayList<>();
	private long errorCount;
	private int categoryCount;
	private int productCount;
	private int variationCount;

	/**
	 * Constructor.
	 *
	 * @param messageCap max amount of messages to track
	 */
	public ImporterResult(final int messageCap) {
		this.messageCap = messageCap;
	}

	/**
	 * Get count of categories importer.
	 *
	 * @return int
	 */
	public int getCategoryCount() {
		return categoryCount;
	}

	/**
	 * Error count.
	 *
	 * @return long
	 */
	public long getErrorCount() {
		return errorCount;
	}

	/**
	 * Get count of products imported.
	 *
	 * @return int
	 */
	public int getProductCount() {
		return productCount;
	}

	/**
	 * Get count of variants imported.
	 *
	 * @return int
	 */
	public int getVariationCount() {
		return variationCount;
	}

	/**
	 * Increment categories imported.
	 */
	public void incrementCategory() {
		++categoryCount;
	}

	/**
	 * Increment products imported.
	 */
	public void incrementProduct() {
		++productCount;
	}

	/**
	 * Returns true if errors are reported.
	 *
	 * @return boolean
	 */
	public boolean hasErrors() {
		return getErrorCount() > 0;
	}

	/**
	 * Increment variants imported.
	 */
	public void incrementVariant() {
		++variationCount;
	}

	private void incrementErrors(final int saveBatchCount) {
		errorCount += saveBatchCount;
	}

	/**
	 * Log a message.
	 *
	 * @param message Message contents
	 * @param isError true if an error message
	 */
	public void logMessage(final String message,
						   final boolean isError) {
		if (this.messages.size() < messageCap) {
			this.messages.add(message);
		}

		if (isError) {
			incrementErrors(1);
		}
	}

	/**
	 * Print these results to the given writer.
	 *
	 * @param printWriter PrintWriter
	 */
	public void printLog(final PrintWriter printWriter) {
		if (messageCap > 0) {
			printWriter.println("");

			for (String msg : this.messages) {
				printWriter.println(msg);
			}

			if (this.messages.size() == messageCap) {
				printWriter.println("...");
			}
		}
	}
}
