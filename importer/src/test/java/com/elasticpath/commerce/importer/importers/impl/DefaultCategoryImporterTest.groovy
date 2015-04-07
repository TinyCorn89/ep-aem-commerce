package com.elasticpath.commerce.importer.importers.impl

import static org.mockito.Answers.RETURNS_SMART_NULLS
import static org.mockito.Mockito.verify

import javax.jcr.Session

import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.commerce.importer.exception.ImporterException
import com.elasticpath.commerce.importer.impl.ImporterResult
import com.elasticpath.commerce.importer.jcr.CategoryService
import com.elasticpath.commerce.importer.model.AemCategory

@RunWith(MockitoJUnitRunner)
class DefaultCategoryImporterTest {

	@Mock(answer = RETURNS_SMART_NULLS)
	CategoryService categoryJcrService

	@Mock(answer = RETURNS_SMART_NULLS)
	ImporterResult importerResult

	@Mock(answer = RETURNS_SMART_NULLS)
	Session session

	@InjectMocks
	DefaultCategoryImporterImpl importer

	def category = new AemCategory(
			categoryCode: 'trivet'
	)

	@Test
	void 'Given valid category, when processing category, then handle the category'() {
		importer.importCategory(session, category, importerResult)

		verify(categoryJcrService).createCategory(session, category, importerResult)
	}

	@Test
	void 'Given valid error report, when recording error, then record the message in the result context'() {
		def callerId = 'exchange:blah'

		importer.error(callerId, category, importerResult, new ImporterException('testException'))

		verify(importerResult).logMessage("Error importing category [$category.categoryCode] from exchange [exchange:blah]", true)
	}
}
