package com.elasticpath.commerce.importer.jcr.impl

import static com.adobe.cq.commerce.api.CommerceConstants.PN_COMMERCE_TYPE
import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED
import static com.elasticpath.commerce.config.ElasticPathGlobalConstants.PRODUCT_IDENTIFIER
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.IDENTIFIER
import static org.apache.sling.jcr.resource.JcrResourceConstants.NT_SLING_FOLDER
import static org.apache.sling.jcr.resource.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY
import static org.junit.Assert.assertEquals
import static org.mockito.Answers.RETURNS_SMART_NULLS
import static org.mockito.BDDMockito.given
import static org.mockito.Matchers.anyString
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.never
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyZeroInteractions

import javax.jcr.Node
import javax.jcr.Session

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import com.day.cq.dam.api.AssetManager

import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.commerce.importer.batch.BatchManagerService
import com.elasticpath.commerce.importer.batch.BatchStatus
import com.elasticpath.commerce.importer.impl.ImporterResult
import com.elasticpath.commerce.importer.jcr.AssetService
import com.elasticpath.commerce.importer.jcr.JcrUtilService
import com.elasticpath.commerce.importer.model.AemAxis
import com.elasticpath.commerce.importer.model.AemVariant

@RunWith(MockitoJUnitRunner)
class CreateVariantTest {

	@Mock(answer = RETURNS_SMART_NULLS)
	AssetManager assetManager

	@Mock(answer = RETURNS_SMART_NULLS)
	AssetService assetService

	@Mock(answer = RETURNS_SMART_NULLS)
	BatchManagerService batchManagerService

	@Mock(answer = RETURNS_SMART_NULLS)
	ImporterResult importerResult

	@Mock(answer = RETURNS_SMART_NULLS)
	Node productNode

	@Mock(answer = RETURNS_SMART_NULLS)
	Node variantNode

	@Mock(answer = RETURNS_SMART_NULLS)
	Session session

	@Mock(answer = RETURNS_SMART_NULLS)
	JcrUtilService jcrUtil

	@InjectMocks
	VariantServiceImpl variantService

	def batchStatus = new BatchStatus(100, 100, 'token', null)
	def axisName = 'colour'
	def axisValue = 'blue'
	def variants = [
			new AemVariant(
					axes: [
							new AemAxis(
									name: axisName,
									value: axisValue,
							),
					],
			)
	]
	def productNodePath = '/etc/commerce/products/hiking/nice-boots'

	@Before
	void setUp() {
		given(productNode.getSession())
				.willReturn(session)

		given(jcrUtil.createValidName(axisValue))
				.willReturn(axisValue)

		given(jcrUtil.createUniqueNode(productNode, axisValue, NT_UNSTRUCTURED, session))
				.willReturn(variantNode)

		given(jcrUtil.createPath(anyString(), eq(NT_SLING_FOLDER), eq(session)))
				.willReturn(productNode)
	}

	@Test
	void 'createVariant for product'() {
		ImporterResult importerResult = new ImporterResult(100)

		Node result = variantService.createVariant(productNode, axisValue, batchStatus, importerResult)

		assertEquals(variantNode, result)
		verify(batchManagerService).checkpoint(session, false, batchStatus, importerResult)
		verify(variantNode).setProperty(PN_COMMERCE_TYPE, "variant")
		verify(variantNode) setProperty(SLING_RESOURCE_TYPE_PROPERTY, "commerce/components/product")
	}

	@Test
	void 'Given invalid axisValue, when creating variant, then sanitise the name'() {
		def invalidAxisValue = 'grün'
		def validAxisValue = 'grun'
		given(jcrUtil.createValidName(invalidAxisValue))
				.willReturn('grun')
		given(jcrUtil.createUniqueNode(productNode, validAxisValue, NT_UNSTRUCTURED, session))
				.willReturn(variantNode)

		variantService.createVariant(productNode, invalidAxisValue, batchStatus, importerResult)

		verify(jcrUtil).createUniqueNode(productNode, validAxisValue, NT_UNSTRUCTURED, session)
	}

	@Test
	void 'Given no variants, when creating variants, then no-op'() {
		variantService.createVariants(null, null, 'irrelevant', [], 'irrelevant', null, null)

		verifyZeroInteractions(jcrUtil)
	}

	@Test
	void 'Given valid variant, when creating variants, then create the variant'() {
		variantService.createVariants(session, assetManager, productNodePath, variants, 'catalogId', batchStatus, importerResult)

		verify(jcrUtil).createUniqueNode(productNode, axisValue, NT_UNSTRUCTURED, session)
	}

	@Test
	void 'Given existing variant, when creating variants, then skip this variant'() {
		given(session.nodeExists(anyString()))
				.willReturn(true)

		variantService.createVariants(session, assetManager, productNodePath, variants, 'catalogId', batchStatus, importerResult)

		verify(jcrUtil, never()).createUniqueNode(productNode, axisValue, NT_UNSTRUCTURED, session)
	}

	@Test
	void 'Given valid variant, when creating variants, then update the axis name and value'() {
		variantService.createVariants(session, assetManager, productNodePath, variants, 'catalogId', batchStatus, importerResult)

		verify(variantNode).setProperty(axisName, axisValue)
	}

	@Test
	void 'Given valid variant, when creating variants, then update the identifier for leaf variants only'() {
		def colour = 'red-hot'
		def size = '33 1/3'
		def variantNode = mockNode(colour)
		def variantLeafNode = mockNode(size)
		def skuCode = 'newSkuForYou'
		def variants = [
				new AemVariant(
						skuCode: skuCode,
						axes: [
								new AemAxis(
										name: 'colour',
										value: colour,
								),
								new AemAxis(
										name: 'size',
										value: size,
								),
						],
				)
		]

		variantService.createVariants(session, assetManager, productNodePath, variants, 'catalogId', batchStatus, importerResult)

		verify(variantNode, times(0)).setProperty(IDENTIFIER, skuCode)
		verify(variantNode, times(0)).setProperty(PRODUCT_IDENTIFIER, skuCode)
		verify(variantLeafNode).setProperty(IDENTIFIER, skuCode)
		verify(variantLeafNode).setProperty(PRODUCT_IDENTIFIER, skuCode)
	}

	def mockNode(String axisName) {
		given(jcrUtil.createValidName(axisName))
				.willReturn(axisName)
		def variantNode = mock(Node)
		given(jcrUtil.createUniqueNode(productNode, axisName, NT_UNSTRUCTURED, session))
				.willReturn(variantNode)
		variantNode
	}
}
