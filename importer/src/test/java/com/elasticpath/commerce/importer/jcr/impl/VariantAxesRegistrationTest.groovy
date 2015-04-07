package com.elasticpath.commerce.importer.jcr.impl

import static com.adobe.cq.commerce.api.CommerceConstants.PN_PRODUCT_VARIANT_AXES
import static org.mockito.BDDMockito.given
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.verify

import javax.jcr.Node
import javax.jcr.Property

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.commons.testing.jcr.MockProperty
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.commerce.importer.model.AemProduct

@RunWith(MockitoJUnitRunner)
class VariantAxesRegistrationTest {

	Property property

	@Mock
	PropertyMapper propertyMapper

	@Mock
	ResourceResolver resourceResolver

	@Mock
	Node node

	@InjectMocks
	ProductServiceImpl productService

	@Captor
	ArgumentCaptor<String[]> axesCaptor

	@Before
	void setUp() {
		property = new MockProperty(PN_PRODUCT_VARIANT_AXES)
	}

	@Test
	void 'Given new variant axes, when registering, should overwrite all data'() {
		property.setValue(['axis1'] as String[])
		def newAxes = ['colour', 'size']
		given(node.hasProperty(PN_PRODUCT_VARIANT_AXES))
				.willReturn(true)
		given(node.getProperty(PN_PRODUCT_VARIANT_AXES))
				.willReturn(property)

		productService.updateProduct(resourceResolver, null, node, new AemProduct(variantAxes: newAxes), null)

		verify(node).setProperty(eq(PN_PRODUCT_VARIANT_AXES), axesCaptor.capture() as String[])
		assert newAxes == axesCaptor.value
	}

	@Test
	void 'Given subset axes, when registering, should overwrite all data'() {
		property.setValue(['colour', 'size'] as String[])
		def newAxes = ['colour']
		given(node.hasProperty(PN_PRODUCT_VARIANT_AXES))
				.willReturn(true)
		given(node.getProperty(PN_PRODUCT_VARIANT_AXES))
				.willReturn(property)

		productService.updateProduct(resourceResolver, null, node, new AemProduct(variantAxes: newAxes), null)

		verify(node).setProperty(eq(PN_PRODUCT_VARIANT_AXES), axesCaptor.capture() as String[])
		assert newAxes == axesCaptor.value
	}
}
