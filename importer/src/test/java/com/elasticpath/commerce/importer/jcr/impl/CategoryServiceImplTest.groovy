package com.elasticpath.commerce.importer.jcr.impl

import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.CODE
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.COMMERCE_ROOT_PATH
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.PATH_JOINER
import static org.apache.sling.jcr.resource.JcrResourceConstants.NT_SLING_FOLDER
import static org.mockito.BDDMockito.given
import static org.mockito.Mockito.never
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify

import javax.jcr.Node
import javax.jcr.RepositoryException
import javax.jcr.Session

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import com.google.common.collect.ImmutableList

import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.commerce.importer.impl.ImporterResult
import com.elasticpath.commerce.importer.jcr.JcrUtilService
import com.elasticpath.commerce.importer.model.AemCategory

@RunWith(MockitoJUnitRunner)
class CategoryServiceImplTest {

	String categoryCode = 'testCategoryCode'
	String catalogId = 'storeName'
	String parentCategoryCode = 'parentCategoryCode'

	@Mock
	ImporterResult importerResult

	@Mock
	JcrUtilService jcrUtil

	@Mock
	Node node

	@Mock
	Session session

	@InjectMocks
	CategoryServiceImpl categoryJcrService

	def category = new AemCategory(
			categoryCode: categoryCode,
			catalogId: catalogId,
			categoryHierarchy: [parentCategoryCode],
			properties: [:]
	)

	def expectedCategoryPath = PATH_JOINER
			.join(
			ImmutableList.builder()
					.add(COMMERCE_ROOT_PATH)
					.add(category.getCatalogId())
					.addAll(category.getCategoryHierarchy())
					.add(category.getCategoryCode())
					.build()
	)

	@Before
	void setUp() {
		given(session.getNode(expectedCategoryPath))
				.willReturn(node)
	}

	@Test
	void 'Given new category, when creating category, then ensure new category created'() {
		given(session.nodeExists(expectedCategoryPath))
				.willReturn(false)

		categoryJcrService.createCategory(session, category, importerResult)

		verify(jcrUtil, times(1)).createPath(expectedCategoryPath, NT_SLING_FOLDER, session)
	}

	@Test
	void 'Given existing category, when creating category, then ensure no new category created'() {
		given(session.nodeExists(expectedCategoryPath))
				.willReturn(true)

		categoryJcrService.createCategory(session, category, importerResult)

		verify(jcrUtil, never()).createPath(expectedCategoryPath, NT_SLING_FOLDER, session)
	}

	@Test
	void 'Given existing category, when creating category, then update the existing category'() {
		given(session.nodeExists(expectedCategoryPath))
				.willReturn(true)

		categoryJcrService.createCategory(session, category, importerResult)

		verify(node, times(1)).setProperty(CODE, categoryCode)
	}

	@Test(expected = RepositoryException)
	void 'Given a failure, when creating category, then pass the exception up'() {
		given(session.nodeExists(expectedCategoryPath))
				.willReturn(false)
		given(jcrUtil.createPath(expectedCategoryPath, NT_SLING_FOLDER, session))
				.willThrow(new RepositoryException())

		categoryJcrService.createCategory(session, category, importerResult)
	}

	@Test
	void 'Given normal path, when creating category, then log category creation message'() {
		categoryJcrService.createCategory(session, category, importerResult)

		verify(importerResult).logMessage("Created category $expectedCategoryPath", false)
	}

	@Test
	void 'Given normal path, when creating category, then increment count of categories importer'() {
		categoryJcrService.createCategory(session, category, importerResult)

		verify(importerResult).incrementCategory()
	}

}
