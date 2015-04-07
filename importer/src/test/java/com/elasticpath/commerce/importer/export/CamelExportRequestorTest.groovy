package com.elasticpath.commerce.importer.export

import static com.elasticpath.commerce.importer.export.CamelExportRequestorImpl.TRIGGER_FULL_CATALOG_EXPORT_ENDPOINT
import static com.elasticpath.commerce.importer.export.CamelExportRequestorImpl.TRIGGER_INCREMENTAL_CATALOG_EXPORT_ENDPOINT
import static com.google.common.base.Optional.absent
import static com.google.common.base.Optional.of
import static groovy.test.GroovyAssert.shouldFail
import static org.mockito.BDDMockito.given
import static org.mockito.Matchers.any
import static org.mockito.Matchers.anyString
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.verify

import javax.jcr.Session

import org.junit.Test
import org.junit.runner.RunWith

import org.apache.camel.ProducerTemplate
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.commerce.importer.exception.ImporterException
import com.elasticpath.commerce.importer.model.PimExportResponse
import com.elasticpath.commerce.importer.model.PimFullExportRequest
import com.elasticpath.commerce.importer.model.PimIncrementalExportRequest
import com.elasticpath.commerce.importer.jcr.CatalogService

@RunWith(MockitoJUnitRunner)
class CamelExportRequestorTest {

	@Mock
	CatalogService catalogJcrService

	@Mock
	ProducerTemplate producerTemplate

	@InjectMocks
	CamelExportRequestorImpl requestor

	@Captor
	ArgumentCaptor<PimIncrementalExportRequest> incrementalRequest

	@Captor
	ArgumentCaptor<PimFullExportRequest> fullRequest

	@Test
	void 'Given correct request, when full importing, then produce a full export request with correct catalogId'() {
		def catalogId = 'exampleCatalogId'
		requestor.request(null, null, catalogId, false)

		verify(producerTemplate).requestBody(
				eq(TRIGGER_FULL_CATALOG_EXPORT_ENDPOINT) as String,
				fullRequest.capture(),
				eq(PimExportResponse)
		)
		assert catalogId == fullRequest.value.catalogId
	}

	@Test
	void 'Given correct request, when incremental importing, then produce an incremental export request with correct exportId'() {
		def exportId = 'someId'
		given(catalogJcrService.getLastExportIdForCatalog(any(Session), anyString(), anyString()))
				.willReturn(of(exportId))

		requestor.request(null, null, '', true)

		verify(producerTemplate).requestBody(
				eq(TRIGGER_INCREMENTAL_CATALOG_EXPORT_ENDPOINT) as String,
				incrementalRequest.capture(),
				eq(PimExportResponse)
		)
		assert exportId == incrementalRequest.value.exportId
	}

	@Test
	void 'Given no previous import, when incremental import requested, then produce error message'() {
		given(catalogJcrService.getLastExportIdForCatalog(any(Session), anyString(), anyString()))
				.willReturn(absent())

		shouldFail(ImporterException) {
			requestor.request(null, null, '', true)
		}
	}
}
