package com.elasticpath.commerce.importer.importers.impl

import com.elasticpath.commerce.importer.impl.ImporterResult
import com.elasticpath.commerce.importer.model.AemCategory
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner

import javax.jcr.Session

import static org.mockito.BDDMockito.given
import static org.mockito.Matchers.anyInt
import static org.mockito.Mockito.atLeastOnce
import static org.mockito.Mockito.atMost
import static org.mockito.Mockito.verify
import com.elasticpath.commerce.importer.config.ImporterConfig
import com.elasticpath.commerce.importer.importers.CategoryImporter
import org.apache.camel.PollingConsumer
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock


/**
 * Created by hyleung on 2015-02-17.
 */
@RunWith(MockitoJUnitRunner.class)
class CategoryListenerTest {
    @Mock
    PollingConsumer pollingConsumer
    @Mock
    CategoryImporter categoryImporter
    @Mock
    ImporterConfig importerConfig
    @Mock
    Session session
    @Mock
    ImporterResult importerResult
    @Mock
    Exchange exchange
    @Mock
    Message message
    //class under test
    @InjectMocks
    CategoryListener categoryListener

    @Before
    void 'init'() {
        // HY: Bit of a test smell here, test code has too much knowledge of the implementation
        // details of the code under test.
        given(message.getBody()).willReturn(new AemCategory())
        given(exchange.getIn()).willReturn(message)
        given(pollingConsumer.receive(anyInt()))
                .willReturn(exchange)
                .willReturn(null)

    }
    @Test
	void 'Given an import timeout, when importing should initiate category listener with timeout'() {
		int expectedPollingTimeout = 9000
		int expectedInitialTimeout = 10000
		given(importerConfig.getImportPollingTimeout()).willReturn(expectedPollingTimeout)
		given(importerConfig.getImportInitialTimeout()).willReturn(expectedInitialTimeout)

        categoryListener.listen(session, importerResult)

        verify(pollingConsumer, atMost(1)).receive(expectedInitialTimeout)
        verify(pollingConsumer, atLeastOnce()).receive(expectedPollingTimeout)
	}

}
