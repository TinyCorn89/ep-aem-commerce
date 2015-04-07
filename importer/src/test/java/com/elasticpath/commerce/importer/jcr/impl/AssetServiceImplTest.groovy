package com.elasticpath.commerce.importer.jcr.impl

import static com.day.cq.commons.DownloadResource.PN_REFERENCE
import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.IMAGE_RESOURCE_TYPE
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.PATH_JOINER
import static groovy.test.GroovyAssert.shouldFail
import static org.apache.sling.jcr.resource.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY
import static org.mockito.BDDMockito.given
import static org.mockito.Matchers.anyString
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyZeroInteractions
import static org.mockito.Mockito.when

import javax.jcr.Node
import javax.jcr.RepositoryException
import javax.jcr.Session

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import com.day.cq.dam.api.AssetManager

import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.commerce.importer.jcr.JcrUtilService
import com.elasticpath.commerce.importer.exception.ImporterException

@RunWith(MockitoJUnitRunner)
class AssetServiceImplTest {

	@Mock
	AssetCreator assetCreator

	@Mock
	AssetManager assetManager

	@Mock
	JcrUtilService jcrUtil

	@Mock
	Node imageNode

	@Mock
	Session session

	@Spy
	PropertyMapper propertyMapper = new PropertyMapper()

	@InjectMocks
	AssetServiceImpl assetService

	String catalogId = "geometrixx"
	String imagesUrl = "/products/footwear/Kamloops.jpg"

	def parentPath = 'parentPath'
	def imagePath = PATH_JOINER.join(parentPath, 'image')

	@Before
	void setUp() {
		given(session.getNode(imagePath))
				.willReturn(imageNode)
	}

	@Test
	void 'Given a repository error, when creating new asset, wrap with ImporterException'() {
		given(jcrUtil.createPath(imagePath, NT_UNSTRUCTURED, NT_UNSTRUCTURED, session, false))
				.willThrow(RepositoryException)

		shouldFail(ImporterException) {
			assetService.createAsset(session, assetManager, 'catalogId', parentPath, 'imagesUrl', [] as byte[])
		}
	}

	@Test
	void 'Given asset already exists, when creating new asset, no-op'() {
		given(session.nodeExists(imagePath))
				.willReturn(true)

		assetService.createAsset(session, assetManager, 'catalogId', parentPath, 'imagesUrl', [] as byte[])

		verifyZeroInteractions(jcrUtil)
	}

	@Test
	void 'Given new asset, when creating new asset, create on jcr'() {
		assetService.createAsset(session, assetManager, 'catalogId', parentPath, 'imagesUrl', [] as byte[])

		verify(jcrUtil).createPath(imagePath, NT_UNSTRUCTURED, NT_UNSTRUCTURED, session, false)
	}

	@Test
	void testUpdateCatalogAsset() throws Exception {
		when(session.nodeExists(anyString()))
				.thenReturn(true)

		assetService.updateCatalogAsset(session, assetManager, catalogId, imageNode, imagesUrl, null)

		verify(imageNode, times(1)).setProperty(SLING_RESOURCE_TYPE_PROPERTY, IMAGE_RESOURCE_TYPE)
		verify(imageNode, times(1)).setProperty(PN_REFERENCE, "/content/dam/geometrixx/products/footwear/Kamloops.jpg")
	}
}
