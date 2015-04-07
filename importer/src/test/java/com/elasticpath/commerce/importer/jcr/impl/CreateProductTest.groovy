package com.elasticpath.commerce.importer.jcr.impl

import static com.adobe.cq.commerce.api.CommerceConstants.PN_COMMERCE_TYPE
import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED
import static org.apache.sling.jcr.resource.JcrResourceConstants.NT_SLING_FOLDER
import static org.apache.sling.jcr.resource.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY
import static org.junit.Assert.assertEquals
import static org.mockito.BDDMockito.given
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify

import javax.jcr.Node
import javax.jcr.NodeIterator
import javax.jcr.Property
import javax.jcr.Session

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import com.day.cq.commons.jcr.JcrObservationThrottle

import org.apache.jackrabbit.util.Text
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.commerce.importer.jcr.JcrUtilService
import com.elasticpath.commerce.importer.batch.BatchManagerService
import com.elasticpath.commerce.importer.batch.BatchStatus
import com.elasticpath.commerce.importer.config.ImporterConfig
import com.elasticpath.commerce.importer.impl.ImporterResult

@RunWith(MockitoJUnitRunner)
class CreateProductTest {
    public static final String PARENT_PATH = 'my/product/parent'

    @Mock
    Node parentNode
    @Mock
    Node mockProduct

    @Mock
    JcrUtilService jcrUtilWrapper

    @Mock
    BatchManagerService batchManagerService

    @Mock
    ImporterConfig importerConfig

    @Mock
    Session session

    @Mock
    JcrObservationThrottle throttle

    @InjectMocks
    ProductServiceImpl productService

    BatchStatus batchStatus
    ImporterResult importerResult

    @Before
    void setup() {
        given(jcrUtilWrapper.createPath(Text.getRelativeParent(PARENT_PATH, 1), false, NT_SLING_FOLDER, NT_SLING_FOLDER, session, false)).willReturn(parentNode)
        batchStatus = new BatchStatus(100, 100, 'token', throttle)
        importerResult = new ImporterResult(100)
    }

    @Test
    void 'create product node'() {
        //mock bucketing logic
        given(parentNode.hasProperty('cq:importBucket')).willReturn(false)
        given(parentNode.hasProperty('cq:importCount')).willReturn(false)
        given(importerConfig.getBucketMax()).willReturn(500L)

        //mock node creation
        given(jcrUtilWrapper.createUniqueNode(parentNode, Text.getName(PARENT_PATH), NT_UNSTRUCTURED, session)).willReturn(mockProduct)

        Node result = productService.createProduct(session, PARENT_PATH, this.batchStatus, importerResult)

        assertProductNode(result)
    }

    @Test
    void 'when product nodes exceed bucket max but no preexisting buckets should create new bucket and copy products'() {
        def Node newBucket = mock(Node.class)

        //mock bucketing logic
        given(parentNode.hasProperty('cq:importBucket')).willReturn(false)
        given(parentNode.hasProperty('cq:importCount')).willReturn(false)
        given(importerConfig.getBucketMax()).willReturn(0L)

        //mock new bucket creation and moving products
        given(jcrUtilWrapper.createUniqueNode(parentNode, 'bucket', NT_SLING_FOLDER, session)).willReturn(newBucket)
        def NodeIterator mockChildren = mock(NodeIterator.class)
        given(parentNode.getNodes()).willReturn(mockChildren)
        given(mockChildren.hasNext()).willReturn(false)

        //mock node creation
        given(jcrUtilWrapper.createUniqueNode(newBucket, Text.getName(PARENT_PATH), NT_UNSTRUCTURED, session)).willReturn(mockProduct)

        Node result = productService.createProduct(session, PARENT_PATH, this.batchStatus, importerResult)

        assertProductNode(result)
    }

    @Test
    void 'should create product node in existing bucket'() {
        def Node bucket = mock(Node.class)

        //mock bucketing logic
        given(parentNode.hasProperty('cq:importBucket')).willReturn(true)
        def Property mockProperty = mock(Property.class)
        given(parentNode.getProperty('cq:importBucket')).willReturn(mockProperty)
        given(mockProperty.getString()).willReturn('importBucket')
        given(parentNode.getNode('importBucket')).willReturn(bucket)
        given(bucket.hasProperty('cq:importCount')).willReturn(false)
        given(importerConfig.getBucketMax()).willReturn(500L)

        //mock product node creation
        given(jcrUtilWrapper.createUniqueNode(bucket, Text.getName(PARENT_PATH), NT_UNSTRUCTURED, session)).willReturn(this.mockProduct)

        Node result = productService.createProduct(session, PARENT_PATH, batchStatus, this.importerResult)

        assertProductNode(result)
    }

    @Test
    void 'should create new bucket when existing bucket exceeds bucket max'() {
        def Node bucket = mock(Node.class)
        def Node newBucket = mock(Node.class)

        //mock the bucketing logic
        given(parentNode.hasProperty('cq:importBucket')).willReturn(true)
        def Property mockProperty = mock(Property.class)
        given(parentNode.getProperty('cq:importBucket')).willReturn(mockProperty)
        given(mockProperty.getString()).willReturn('importBucket')
        given(parentNode.getNode('importBucket')).willReturn(bucket)
        given(bucket.hasProperty('cq:importCount')).willReturn(false)
        given(importerConfig.getBucketMax()).willReturn(0L)

        //mock new bucket creation
        given(bucket.getParent()).willReturn(parentNode)
        given(jcrUtilWrapper.createUniqueNode(parentNode, 'bucket', NT_SLING_FOLDER, session)).willReturn(newBucket)

        //mock product node creation
        given(jcrUtilWrapper.createUniqueNode(newBucket, Text.getName(PARENT_PATH), NT_UNSTRUCTURED, session)).willReturn(this.mockProduct)

        Node result = productService.createProduct(session, PARENT_PATH, batchStatus, this.importerResult)

        verify(jcrUtilWrapper).createUniqueNode(parentNode, 'bucket', NT_SLING_FOLDER, session)
        assertProductNode(result)
    }

    private void assertProductNode(Node result) {
        assertEquals(this.mockProduct, result)
        verify(batchManagerService).checkpoint(session, false, batchStatus, this.importerResult)
        verify(this.mockProduct).setProperty(PN_COMMERCE_TYPE, "product")
        verify(this.mockProduct) setProperty(SLING_RESOURCE_TYPE_PROPERTY, "commerce/components/product")
    }
}
