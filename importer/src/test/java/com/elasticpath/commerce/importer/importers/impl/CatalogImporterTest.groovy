package com.elasticpath.commerce.importer.importers.impl

import com.elasticpath.commerce.importer.config.ImporterConfig

import static org.mockito.BDDMockito.given
import static org.mockito.Matchers.any
import static org.mockito.Matchers.anyBoolean
import static org.mockito.Matchers.anyInt
import static org.mockito.Matchers.anyString
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.never
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyZeroInteractions

import javax.jcr.Node
import javax.jcr.RepositoryException
import javax.jcr.Session

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith

import com.day.cq.dam.api.AssetManager
import com.day.cq.tagging.TagManager

import org.apache.sling.api.resource.ResourceResolver
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.commerce.importer.batch.BatchStatus
import com.elasticpath.commerce.importer.export.ExportRequestor
import com.elasticpath.commerce.importer.impl.ImporterResult
import com.elasticpath.commerce.importer.jcr.CatalogService
import com.elasticpath.commerce.importer.model.PimExportResponse

@RunWith(MockitoJUnitRunner.class)
class CatalogImporterTest {

	def PRODUCTS_ROOT = 'products-root'
	def COMMERCE_PROVIDER = 'commerce-provider'
	def CATALOG_ID = 'catalog-id'

	@Rule
	public ExpectedException exception = ExpectedException.none()

	//services
	@Mock
	CatalogService catalogJcrService

	@Mock
	ExportRequestor exportRequestor

	//Camel
	@Mock
	CategoryListener categoryListener

	@Mock
	ProductListener productListener

	//models
	@Mock
	Node productsRootNode

	@Mock
	PimExportResponse pimExportResponse

	@Mock
	BatchStatus batchStatus

	@Mock
	ResourceResolver resourceResolver

	@Mock
	Session session

	@Mock
	ImporterResult importerResult

	@Mock
	ImporterConfig importerConfig

	//class under test
	@InjectMocks
	CatalogImporter importer

	@Before
	void 'init'() throws RepositoryException {
		given(exportRequestor.request(eq(session), eq(PRODUCTS_ROOT), eq(CATALOG_ID), any(boolean)))
				.willReturn(pimExportResponse)

		given(productsRootNode.getPath())
				.willReturn(PRODUCTS_ROOT)

		given(resourceResolver.adaptTo(Session))
				.willReturn(session)
	}

	@Test
	void 'Given full import request, when importing, then delete existing catalog'() {
		boolean incrementalImport = false

		importer.importCatalog(resourceResolver, productsRootNode, COMMERCE_PROVIDER, CATALOG_ID, incrementalImport, batchStatus, importerResult)

		verify(catalogJcrService).deleteCatalogNode(session, PRODUCTS_ROOT, CATALOG_ID)
	}

	@Test
	void 'Given incremental import request, when importing, then never delete existing catalog'() {
		boolean incrementalImport = true

		importer.importCatalog(resourceResolver, productsRootNode, COMMERCE_PROVIDER, CATALOG_ID, incrementalImport, batchStatus, importerResult)

		verify(catalogJcrService, never()).deleteCatalogNode(session, PRODUCTS_ROOT, CATALOG_ID)
	}

	@Test
	void 'Given no response, when requesting export, should make no changes to catalog'() {
		boolean isIncrementalImport = false
		given(exportRequestor.request(any(Session), anyString(), anyString(), anyBoolean()))
				.willReturn(null)

		importer.importCatalog(resourceResolver, productsRootNode, COMMERCE_PROVIDER, CATALOG_ID, isIncrementalImport, batchStatus, importerResult)

		verifyZeroInteractions(catalogJcrService)
	}

	@Test
	void 'Given valid data, when importing, then listen for categories'() {
		boolean isIncrementalImport = false

		importer.importCatalog(resourceResolver, productsRootNode, COMMERCE_PROVIDER, CATALOG_ID, isIncrementalImport, batchStatus, importerResult)

		verify(categoryListener).listen(eq(session), eq(importerResult))
	}

	@Test
	void 'Given valid data, when importing, then listen for products'() {
		boolean isIncrementalImport = false

		importer.importCatalog(resourceResolver, productsRootNode, COMMERCE_PROVIDER, CATALOG_ID, isIncrementalImport, batchStatus, importerResult)

		verify(productListener).listen(eq(resourceResolver),
				any(AssetManager),
				eq(session),
				any(TagManager),
				eq(batchStatus),
				eq(importerResult))
	}


}
