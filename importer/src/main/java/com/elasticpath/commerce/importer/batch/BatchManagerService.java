package com.elasticpath.commerce.importer.batch;

import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.day.cq.commons.jcr.JcrObservationThrottle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.commerce.importer.jcr.JcrUtilService;
import com.elasticpath.commerce.importer.exception.ImporterException;
import com.elasticpath.commerce.importer.impl.ImporterResult;

/**
 * Manages some of the data access, saving, throttling on the jcr repository.
 */
public class BatchManagerService {

	private static final Logger LOG = LoggerFactory.getLogger(BatchManagerService.class);

	@Inject
	private JcrUtilService jcrUtilService;

	/**
	 * Opens a throttle.
	 *
	 * @param storeRoot Node on which to throttle
	 * @return the throttle
	 * @throws RepositoryException if there is an error throttling
	 */
	public JcrObservationThrottle openThrottle(final Node storeRoot) throws RepositoryException {
		JcrObservationThrottle throttle = new JcrObservationThrottle(
				jcrUtilService.createUniqueNode(
						storeRoot,
						"temp",
						NT_UNSTRUCTURED,
						storeRoot.getSession()
				)
		);
		throttle.open();

		return throttle;
	}

	/**
	 * Closes the throttle.
	 *
	 * @param throttle throttle to close
	 */
	public void closeThrottle(final JcrObservationThrottle throttle) {
		if (throttle != null) {
			throttle.close();
		}
	}

	/**
	 * Saves and data to jcr and flushes if batch size exceeded.
	 *
	 * @param session        jcr Session to save
	 * @param flush          override automatic batch size detection
	 * @param batchStatus    state for this batch run
	 * @param importerResult state for results of the importer run
	 */
	public void checkpoint(final Session session,
						   final boolean flush,
						   final BatchStatus batchStatus,
						   final ImporterResult importerResult) {
		batchStatus.itemBatched();

		if (batchStatus.isTickerSet()) {
			try {
				Node tickerNode = session.getNode(
						getTickerPath(batchStatus.getTickerToken())
				);
				tickerNode.setProperty("message", batchStatus.getTickerMessage());
				tickerNode.setProperty("errorCount", importerResult.getErrorCount());
				tickerNode.setProperty("complete", batchStatus.isTickerComplete());
			} catch (Exception e) {
				LOG.error("ERROR updating ticker", e);
			}
		}

		if (batchStatus.isBatchLimitReached() && !flush) {
			return;
		}

		try {
			session.save();
			batchStatus.resetSaveBatchCount();
		} catch (Exception e) {
			importerResult.logMessage("ERROR saving session", true);
			LOG.error("ERROR saving session", e);
		}

		if (batchStatus.isThrottleBatchLimitExceeded()) {
			try {
				batchStatus.getThrottle()
						.waitForEvents();
				batchStatus.resetThrottleBatchLimit();
			} catch (RepositoryException ignored) {
			}
		}
	}

	/**
	 * Initialises the ticker token.
	 *
	 * @param session     Session
	 * @param tickerToken String
	 */
	public void initialiseTicker(final Session session,
								 final String tickerToken) {
		try {
			Node node = jcrUtilService.createPath(
					getTickerPath(tickerToken),
					NT_UNSTRUCTURED,
					session
			);
			node.setProperty("message", "Initialising import");
		} catch (RepositoryException e) {
			throw new ImporterException(e.getMessage(), e);
		}
	}

	private String getTickerPath(final String tickerToken) {
		return "/tmp/commerce/tickers/import_" + tickerToken;
	}
}
