package com.elasticpath.commerce.importer.jcr.impl

import static com.adobe.cq.commerce.api.CommerceConstants.PN_COMMERCE_TYPE
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE
import static com.elasticpath.commerce.config.ElasticPathGlobalConstants.PRODUCT_IDENTIFIER
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.IDENTIFIER
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.PRODUCT_RESOURCE_TYPE
import static groovy.test.GroovyAssert.shouldFail
import static org.apache.commons.lang3.StringUtils.EMPTY
import static org.apache.sling.jcr.resource.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY
import static org.mockito.BDDMockito.given
import static org.mockito.Matchers.any
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.verify

import java.security.AccessControlException

import javax.jcr.Node

import org.junit.Test
import org.junit.runner.RunWith

import com.day.cq.tagging.InvalidTagFormatException
import com.day.cq.tagging.Tag
import com.day.cq.tagging.TagManager

import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.commerce.importer.exception.ImporterException
import com.elasticpath.commerce.importer.model.AemProduct

@RunWith(MockitoJUnitRunner)
class UpdateProductTest {

	String identifierValue = 'eqwnkm'

	@Mock
	Node productNode

	@Mock
	PropertyMapper propertyMapper

	@Mock
	ResourceResolver resourceResolver

	@Mock
	TagManager tagManager

	@InjectMocks
	ProductServiceImpl productService

	@Test
	void 'Given single sku product, when updating product, then use skuCode for productIdentifier'() {
		def skuCode = 'skuCode'
		AemProduct product = new AemProduct(
				variantAxes: [],
				skuCode: skuCode,
				productCode: 'productCode',
				properties: [:],
		)

		productService.updateProduct(resourceResolver, null, productNode, product, null)

		verify(productNode).setProperty(IDENTIFIER, skuCode)
	}

	@Test
	void 'Given multi sku product, when updating product, then use productCode for productIdentifier'() {
		def productCode = 'productCode'
		AemProduct product = new AemProduct(
				variantAxes: ['variant!'],
				skuCode: 'skuCode',
				productCode: productCode,
				properties: [:],
		)

		productService.updateProduct(resourceResolver, null, productNode, product, null)

		verify(productNode).setProperty(IDENTIFIER, productCode)
	}

	@Test
	void 'Given normal data, when updating, then populate node with the correct properties'() {
		def displayName = 'Nairobi Runners'
		AemProduct product = new AemProduct(
				variantAxes: [],
				skuCode: identifierValue,
				properties: [:],
				displayName: displayName,
		)

		productService.updateProduct(resourceResolver, null, productNode, product, null)

		verify(productNode).setProperty(PRODUCT_IDENTIFIER, identifierValue)
		verify(productNode).setProperty(JCR_TITLE, displayName)
		verify(productNode).setProperty(SLING_RESOURCE_TYPE_PROPERTY, PRODUCT_RESOURCE_TYPE)
		verify(productNode).setProperty(PN_COMMERCE_TYPE, 'product')
	}

	@Test
	void 'Given product has tags, when updating, then set tags on node'() {
		def tag = 'ep/tag'
		AemProduct product = new AemProduct(
				tags: [tag],
		)

		productService.updateProduct(resourceResolver, tagManager, productNode, product, null)

		verify(tagManager).createTag(tag, EMPTY, EMPTY)
		verify(tagManager).setTags(any(Resource), any(Tag[]), eq(false))
	}

	@Test
	void 'Given invalid tag format, when updating, then report with EP exception'() {
		def tag = 'ep/tag'
		given(tagManager.createTag(tag, EMPTY, EMPTY))
				.willThrow(InvalidTagFormatException)

		AemProduct product = new AemProduct(
				tags: [tag],
		)

		shouldFail(ImporterException) {
			productService.updateProduct(resourceResolver, tagManager, productNode, product, null)
		}
	}

	@Test
	void 'Given repository access error, when updating, then report with EP exception'() {
		def tag = 'ep/tag'
		given(tagManager.createTag(tag, EMPTY, EMPTY))
				.willThrow(AccessControlException)

		AemProduct product = new AemProduct(
				tags: [tag],
		)

		shouldFail(ImporterException) {
			productService.updateProduct(resourceResolver, tagManager, productNode, product, null)
		}
	}
}
