package com.elasticpath.commerce.importer.jcr.impl;

import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.CATALOG_ID;
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.LAST_IMPORTED;
import static org.apache.sling.jcr.resource.JcrResourceConstants.NT_SLING_FOLDER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.adobe.cq.commerce.api.CommerceConstants;
import com.google.common.base.Optional;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.commerce.importer.jcr.JcrUtilService;
import com.elasticpath.commerce.importer.exception.ImporterException;

/**
 * Tests the {@link CatalogServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CatalogServiceImplTest {

	private static final String TEST_PRODUCTS_ROOT_PATH = "testBaseResourcePath";
	private static final String TEST_CATALOG_ID = "testStoreName";
	private static final String ANY_STRING = "any-string";
	private static final String TEST_CATALOG_ROOT = TEST_PRODUCTS_ROOT_PATH + "/" + TEST_CATALOG_ID;
	private static final String TEST_COMMERCE_PROVIDER = "testCommerceProvider";

	@Mock
	private JcrUtilService jcrUtil;
	@Mock
	private Node node;
	@Mock
	private Session session;

	@Mock
	private javax.jcr.Property property;

	@InjectMocks
	private CatalogServiceImpl catalogJCRService;

	@Test
	public void ensureCatalogNodeCanBeCreatedCorrectly() throws RepositoryException {
		given(jcrUtil.createPath(TEST_CATALOG_ROOT, NT_SLING_FOLDER, session)).willReturn(node);

		catalogJCRService.createCatalogNode(session, TEST_PRODUCTS_ROOT_PATH, TEST_CATALOG_ID, ANY_STRING, TEST_COMMERCE_PROVIDER);

		verify(node, times(1)).setProperty(CATALOG_ID, TEST_CATALOG_ID);
		verify(node, times(1)).setProperty(LAST_IMPORTED, ANY_STRING);
	}

	@Test(expected = ImporterException.class)
	public void ensureRepositoryExceptionThrownWhenNodeCreationFails() throws RepositoryException {
		given(jcrUtil.createPath(TEST_CATALOG_ROOT, NT_SLING_FOLDER, session)).willThrow(new RepositoryException());

		catalogJCRService.createCatalogNode(session, TEST_PRODUCTS_ROOT_PATH, TEST_CATALOG_ID, ANY_STRING, TEST_COMMERCE_PROVIDER);
	}

	@Test
	public void ensureCatalogNodeCreatedWithExpectedCommerceProvider() throws RepositoryException {
		given(jcrUtil.createPath(TEST_CATALOG_ROOT, NT_SLING_FOLDER, session)).willReturn(node);
		catalogJCRService.createCatalogNode(session, TEST_PRODUCTS_ROOT_PATH, TEST_CATALOG_ID, ANY_STRING, TEST_COMMERCE_PROVIDER);
		verify(node).setProperty(CommerceConstants.PN_COMMERCE_PROVIDER, TEST_COMMERCE_PROVIDER);
	}

	@Test
	public void ensureNodeIsNotRemovedIfItDoesntExist() throws RepositoryException {
		when(session.itemExists(TEST_CATALOG_ROOT)).thenReturn(false);

		catalogJCRService.deleteCatalogNode(session, TEST_PRODUCTS_ROOT_PATH, TEST_CATALOG_ID);

		verify(session).itemExists(TEST_CATALOG_ROOT);
		verifyZeroInteractions(session);
	}

	@Test
	public void ensureCatalogNodeIsRetrievedOnRemoval() throws RepositoryException {
		when(session.itemExists(TEST_CATALOG_ROOT)).thenReturn(true);
		when(session.getItem(anyString())).thenReturn(node);
		catalogJCRService.deleteCatalogNode(session, TEST_PRODUCTS_ROOT_PATH, TEST_CATALOG_ID);
		verify(session, times(1)).getItem(TEST_CATALOG_ROOT);
	}

	@Test
	public void ensureCatalogNodeIsRemoved() throws RepositoryException {
		when(session.itemExists(TEST_CATALOG_ROOT)).thenReturn(true);
		when(session.getItem(anyString())).thenReturn(node);
		catalogJCRService.deleteCatalogNode(session, TEST_PRODUCTS_ROOT_PATH, TEST_CATALOG_ID);
		verify(node, times(1)).remove();
	}

	@Test
	public void ensureLastImportChecksIfItemExists() throws RepositoryException {
		when(session.itemExists(TEST_CATALOG_ROOT)).thenReturn(false);
		catalogJCRService.getLastExportIdForCatalog(session, TEST_PRODUCTS_ROOT_PATH, TEST_CATALOG_ID);
		verify(session).itemExists(TEST_CATALOG_ROOT);
	}

	@Test
	public void ensureLastImportReturnsAbsentWhenNoCatalogExists() throws RepositoryException {
		when(session.itemExists(TEST_CATALOG_ROOT)).thenReturn(false);
		Optional<String> lastExportId = catalogJCRService.getLastExportIdForCatalog(session, TEST_PRODUCTS_ROOT_PATH, TEST_CATALOG_ID);
		assertFalse(lastExportId.isPresent());
	}

	@Test
	public void ensureLastImportReturnsAbsentWhenLastImportedPropertyDoesntExist() throws RepositoryException {
		when(session.itemExists(TEST_CATALOG_ROOT)).thenReturn(true);
		when(session.getNode(TEST_CATALOG_ROOT)).thenReturn(node);
		when(node.getProperty(LAST_IMPORTED)).thenReturn(null);
		Optional<String> lastExportId = catalogJCRService.getLastExportIdForCatalog(session, TEST_PRODUCTS_ROOT_PATH, TEST_CATALOG_ID);
		assertFalse(lastExportId.isPresent());
	}

	@Test
	public void ensureLastImportReturnsValueFromJcrIfCatalogExists() throws RepositoryException {
		when(session.itemExists(TEST_CATALOG_ROOT)).thenReturn(true);
		when(session.getNode(TEST_CATALOG_ROOT)).thenReturn(node);
		when(node.getProperty(LAST_IMPORTED)).thenReturn(property);
		when(property.getString()).thenReturn(ANY_STRING);
		Optional<String> lastExportId = catalogJCRService.getLastExportIdForCatalog(session, TEST_PRODUCTS_ROOT_PATH, TEST_CATALOG_ID);
		assertEquals(ANY_STRING, lastExportId.get());
	}
}
