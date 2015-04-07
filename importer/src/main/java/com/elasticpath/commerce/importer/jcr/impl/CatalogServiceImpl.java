package com.elasticpath.commerce.importer.jcr.impl;

import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.CATALOG_ID;
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.LAST_IMPORTED;
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.PATH_JOINER;
import static java.lang.String.format;
import static org.apache.sling.jcr.resource.JcrResourceConstants.NT_SLING_FOLDER;

import javax.inject.Inject;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.adobe.cq.commerce.api.CommerceConstants;
import com.google.common.base.Optional;

import com.elasticpath.commerce.importer.exception.ImporterException;
import com.elasticpath.commerce.importer.jcr.CatalogService;
import com.elasticpath.commerce.importer.jcr.JcrUtilService;

/**
 * Implementation of {@link com.elasticpath.commerce.importer.jcr.CatalogService}.
 */
public class CatalogServiceImpl implements CatalogService {

	@Inject
	private JcrUtilService jcrUtil;

	@Override
	public void createCatalogNode(final Session session,
								  final String productsRoot,
								  final String catalogId,
								  final String exportId,
								  final String commerceProviderName) {
		try {
			String catalogPath = PATH_JOINER.join(productsRoot, catalogId);
			Node catalogNode = jcrUtil.createPath(catalogPath, NT_SLING_FOLDER, session);

			catalogNode.setProperty(CATALOG_ID, catalogId);
			catalogNode.setProperty(CommerceConstants.PN_COMMERCE_PROVIDER, commerceProviderName);
			catalogNode.setProperty(LAST_IMPORTED, exportId);

			session.save();
		} catch (RepositoryException e) {
			throw new ImporterException(format("Unable to create new catalog node: [%s]", productsRoot), e);
		}
	}

	@Override
	public void deleteCatalogNode(final Session session,
								  final String productsRoot,
								  final String catalogId) {

		try {
			String catalogPath = PATH_JOINER.join(productsRoot, catalogId);
			if (!session.itemExists(catalogPath)) {
				return;
			}

			Item item = session.getItem(catalogPath);
			item.remove();
			session.save();
		} catch (RepositoryException e) {
			throw new ImporterException(format("Unable to delete existing catalog node: [%s]", productsRoot), e);
		}
	}

	@Override
	public Optional<String> getLastExportIdForCatalog(
			final Session session,
			final String productsRoot,
			final String catalogId) throws RepositoryException {
		String catalogPath = PATH_JOINER.join(productsRoot, catalogId);
		if (!session.itemExists(catalogPath)) {
			return Optional.absent();
		}
		Node node = session.getNode(catalogPath);
		Property lastImportedProperty = node.getProperty(LAST_IMPORTED);
		if (lastImportedProperty == null) {
			return Optional.absent();
		}
		return Optional.of(lastImportedProperty.getString());
	}
}
