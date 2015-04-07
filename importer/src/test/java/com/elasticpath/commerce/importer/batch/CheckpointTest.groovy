package com.elasticpath.commerce.importer.batch

import static org.mockito.BDDMockito.given
import static org.mockito.Matchers.anyString
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify

import javax.jcr.Node
import javax.jcr.Session

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import com.day.cq.commons.jcr.JcrObservationThrottle

import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.commerce.importer.jcr.JcrUtilService
import com.elasticpath.commerce.importer.impl.ImporterResult

@RunWith(MockitoJUnitRunner)
class CheckpointTest {

	@Mock
	JcrUtilService jcrUtilWrapper

	@Mock
	Session session

	@Mock
	JcrObservationThrottle throttle

	@InjectMocks
	BatchManagerService batchManagerService

	ImporterResult importerResult
	boolean flush

	@Before
	void setup() {
		flush = true
		importerResult = new ImporterResult(100)
	}

	@Test
	void 'Given batch limit reached but no flushing do not save session'() {
		flush = false
		def batchStatus =  new BatchStatus(1, 100, '', throttle)

		batchManagerService.checkpoint(session, flush, batchStatus, importerResult)
	}

	@Test
	void 'Given flushing enabled save session'() {
		def batchStatus = new BatchStatus(100, 100, '', throttle)

		batchManagerService.checkpoint(session, flush, batchStatus, importerResult)

		verify(session).save()
	}

	@Test
	void 'Given ticker has been set, then update ticker node'() {
		def batchStatus =  new BatchStatus(100, 0, 'ticker', throttle)
		def tickerNode = mock(Node.class)

		given(session.getNode('/tmp/commerce/tickers/import_ticker'))
				.willReturn(tickerNode)

		batchManagerService.checkpoint(session, flush, batchStatus, importerResult)

		verify(tickerNode).setProperty(eq('message'), anyString())
	}

	@Test
	void 'Given flushing enabled and throttle limit reached save session and wait for throttle events'() {
		def batchStatus =  new BatchStatus(100, 0, '', throttle)

		batchManagerService.checkpoint(session, flush, batchStatus, importerResult)

		verify(throttle).waitForEvents()
		verify(session).save()
	}
}
