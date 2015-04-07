package com.elasticpath.commerce.importer.batch;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import com.day.cq.commons.jcr.JcrObservationThrottle;

/**
 * Maintains state for various batch related operations.
 */
public class BatchStatus {

	private final int maxSaveBatchSize;
	private final int minThrottleBatchSize;
	private final JcrObservationThrottle throttle;

	private int saveBatchCount;
	private int throttleBatchCount;

	private boolean tickerComplete;
	private String tickerMessage = "";
	private final String tickerToken;

	/**
	 * Constructor.
	 *
	 * @param maxSaveBatchSize     maximum number of items to wait for before saving
	 * @param minThrottleBatchSize once this is exceeded throttling needs to start
	 * @param tickerToken          String
	 * @param throttle             JcrObservationThrottle
	 */
	public BatchStatus(final int maxSaveBatchSize,
					   final int minThrottleBatchSize,
					   final String tickerToken,
					   final JcrObservationThrottle throttle) {

		this.maxSaveBatchSize = maxSaveBatchSize;
		this.minThrottleBatchSize = minThrottleBatchSize;
		this.tickerToken = tickerToken;
		this.throttle = throttle;
	}

	/**
	 * Record an item as batched.
	 */
	public void itemBatched() {

		++saveBatchCount;
		++throttleBatchCount;
	}

	/**
	 * Checks to see if the max batch limit has been reached.
	 *
	 * @return boolean
	 */
	public boolean isBatchLimitReached() {
		return saveBatchCount <= maxSaveBatchSize;
	}

	/**
	 * Resets the save batch count, usually after saving.
	 */
	public void resetSaveBatchCount() {
		saveBatchCount = 0;
	}

	/**
	 * Checks to see if the throttle batch size has been exceeded.
	 *
	 * @return boolean
	 */
	public boolean isThrottleBatchLimitExceeded() {
		return throttleBatchCount > minThrottleBatchSize;
	}

	/**
	 * Resets the save batch count, usually after throttling.
	 */
	public void resetThrottleBatchLimit() {
		throttleBatchCount = 0;
	}

	/**
	 * Checks if the ticker has been set.
	 *
	 * @return boolean
	 */
	public boolean isTickerSet() {
		return isNotEmpty(tickerToken);
	}

	/**
	 * Checks if the ticker is marked as completed.
	 *
	 * @return boolean
	 */
	public boolean isTickerComplete() {
		return tickerComplete;
	}

	/**
	 * Getter.
	 *
	 * @return String
	 */
	public String getTickerMessage() {
		return tickerMessage;
	}

	/**
	 * Getter.
	 *
	 * @return String
	 */
	public String getTickerToken() {
		return tickerToken;
	}

	/**
	 * Updates the ticker.
	 *
	 * @param tickerMessage String
	 */
	public void updateTicker(final String tickerMessage) {
		this.tickerMessage = tickerMessage;
	}

	/**
	 * Marks the ticket as complete.
	 */
	public void completeTicker() {
		this.tickerComplete = true;
	}

	/**
	 * JcrObservationThrottle.
	 *
	 * @return JcrObservationThrottle
	 */
	public JcrObservationThrottle getThrottle() {
		return throttle;
	}
}
