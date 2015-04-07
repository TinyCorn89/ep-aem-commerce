package com.elasticpath.commerce.importer.importers.impl

import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.COMMERCE_ROOT_PATH
import static org.mockito.Answers.RETURNS_SMART_NULLS
import static org.mockito.BDDMockito.given
import static org.mockito.Mockito.verify

import javax.jcr.Node
import javax.jcr.Session

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import com.day.cq.dam.api.AssetManager
import com.day.cq.tagging.TagManager

import org.apache.sling.api.resource.ResourceResolver
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.commerce.importer.batch.BatchStatus
import com.elasticpath.commerce.importer.exception.ImporterException
import com.elasticpath.commerce.importer.impl.ImporterResult
import com.elasticpath.commerce.importer.jcr.AssetService
import com.elasticpath.commerce.importer.jcr.ProductService
import com.elasticpath.commerce.importer.jcr.VariantService
import com.elasticpath.commerce.importer.model.AemProduct

@RunWith(MockitoJUnitRunner)
class DefaultProductImporterTest {

	//services
	@Mock(answer = RETURNS_SMART_NULLS)
	AssetService assetService

	@Mock(answer = RETURNS_SMART_NULLS)
	ProductService productService

	@Mock(answer = RETURNS_SMART_NULLS)
	VariantService variantService

	//models, don't really care about these
	@Mock(answer = RETURNS_SMART_NULLS)
	AssetManager assetManager

	@Mock(answer = RETURNS_SMART_NULLS)
	BatchStatus batchStatus

	@Mock(answer = RETURNS_SMART_NULLS)
	ImporterResult importerResult

	@Mock(answer = RETURNS_SMART_NULLS)
	ResourceResolver resourceResolver

	@Mock(answer = RETURNS_SMART_NULLS)
	Session session

	@Mock(answer = RETURNS_SMART_NULLS)
	Node productNode

	@Mock(answer = RETURNS_SMART_NULLS)
	TagManager tagManager

	//class under test
	@InjectMocks
	DefaultProductImporterImpl productImporter

	def catalogId = 'geometrixx'
	def category = 'spurtle'
	def productCode = 'wps1'
	def variants = []
	def product = new AemProduct(
			catalogId: catalogId,
			categoryHierarchy: [category],
			productCode: productCode,
			variants: variants
	)
	def productPath = "$COMMERCE_ROOT_PATH/$catalogId/$category/$productCode" as String

	@Before
	void setUp() {
		given(productService.createProduct(session, productPath, batchStatus, importerResult))
				.willReturn(productNode)
		given(productNode.getPath())
				.willReturn(productPath)
	}

	@Test
	void 'Given valid product, when processing, then delete and create the product'() {
		productImporter.importProduct(resourceResolver, assetManager, session, tagManager, product, batchStatus, importerResult)

		verify(productService).deleteProduct(session, productPath)
		verify(productService).createProduct(session, productPath, batchStatus, importerResult)
	}

	@Test
	void 'Given valid product, when processing, then update the product node'() {
		productImporter.importProduct(resourceResolver, assetManager, session, tagManager, product, batchStatus, importerResult)

		verify(productService).updateProduct(resourceResolver, tagManager, productNode, product, productPath)
	}

	@Test
	void 'Given valid product, when processing, then create its assets'() {
		productImporter.importProduct(resourceResolver, assetManager, session, tagManager, product, batchStatus, importerResult)

		verify(assetService).createAsset(session, assetManager, catalogId, productPath, null, null)
	}

	@Test
	void 'Given valid product, when processing, then create its variants'() {
		productImporter.importProduct(resourceResolver, assetManager, session, tagManager, product, batchStatus, importerResult)

		verify(variantService).createVariants(session, assetManager, productPath, variants, catalogId, batchStatus, importerResult)
	}

	@Test
	void 'Given product node name changed, when process updates product, then use the new name from the productNode'() {
		def productPath = 'newProductPath'
		given(productNode.getPath())
				.willReturn(productPath)

		productImporter.importProduct(resourceResolver, assetManager, session, tagManager, product, batchStatus, importerResult)

		verify(productService).updateProduct(resourceResolver, tagManager, productNode, product, productPath)
	}

	@Test
	void 'Given product node name changed, when process updates asset, then use the new name from the productNode'() {
		def productPath = 'newProductPath'
		given(productNode.getPath())
				.willReturn(productPath)

		productImporter.importProduct(resourceResolver, assetManager, session, tagManager, product, batchStatus, importerResult)

		verify(assetService).createAsset(session, assetManager, catalogId, productPath, null, null)
	}

	@Test
	void 'Given product node name changed, when processing, then create its variants'() {
		def productPath = 'newProductPath'
		given(productNode.getPath())
				.willReturn(productPath)

		productImporter.importProduct(resourceResolver, assetManager, session, tagManager, product, batchStatus, importerResult)

		verify(variantService).createVariants(session, assetManager, productPath, variants, catalogId, batchStatus, importerResult)
	}

	@Test
	void 'Given valid error report, when recording error, then record the message in the result context'() {
		def callerId = 'exchange: blah'
		productImporter.error(callerId, product, importerResult, new ImporterException('testException'))

		verify(importerResult).logMessage("Error importing product [$productCode] from exchange [$callerId]", true)
	}
}
