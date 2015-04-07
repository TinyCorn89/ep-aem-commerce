package com.elasticpath.commerce.importer.jcr.impl;

import static com.day.cq.commons.DownloadResource.PN_REFERENCE;
import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.IMAGE_RESOURCE_TYPE;
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.PATH_JOINER;
import static org.apache.sling.jcr.resource.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.day.cq.dam.api.AssetManager;
import com.google.common.base.CharMatcher;

import com.elasticpath.commerce.importer.jcr.JcrUtilService;
import com.elasticpath.commerce.importer.exception.ImporterException;
import com.elasticpath.commerce.importer.jcr.AssetService;

/**
 * Manages assets on jcr.
 */
public class AssetServiceImpl implements AssetService {

	private static final String ASSET_DEFAULT_PATH = "/content/dam";
	private static final CharMatcher SLASH_MATCHER = CharMatcher.anyOf("/\\");

	@Inject
	private AssetCreator assetCreator;

	@Inject
	private JcrUtilService jcrUtil;

	@Override
	public void createAsset(final Session session,
							final AssetManager assetManager,
							final String catalogId,
							final String path,
							final String imagesUrl,
							final byte[] image) {
		try {
			String imagePath = createImagePath(path);

			if (session.nodeExists(imagePath)) {
				return;
			}

			if (imagesUrl == null) {
				return;
			}

			jcrUtil.createPath(imagePath, NT_UNSTRUCTURED, NT_UNSTRUCTURED, session, false);

			updateCatalogAsset(
					session,
					assetManager,
					catalogId,
					session.getNode(imagePath),
					imagesUrl,
					image
			);
		} catch (RepositoryException e) {
			throw new ImporterException(e.getMessage(), e);
		}
	}

	/**
	 * Updates an image asset node for the a given path.
	 *
	 * @param session      Session
	 * @param assetManager AssetManager
	 * @param catalogId    catalogId
	 * @param imageNode    Node
	 * @param imagesUrl    imagesUrl
	 * @param image        image
	 */
	@Override
	public void updateCatalogAsset(final Session session,
								   final AssetManager assetManager,
								   final String catalogId,
								   final Node imageNode,
								   final String imagesUrl,
								   final byte[] image) {

		try {
			String assetPath = PATH_JOINER.join(ASSET_DEFAULT_PATH, catalogId, SLASH_MATCHER.trimLeadingFrom(imagesUrl));

			if (!session.nodeExists(assetPath)) {
				assetCreator.createAssetFromBytes(assetManager, assetPath, image);
			}

			imageNode.setProperty(SLING_RESOURCE_TYPE_PROPERTY, IMAGE_RESOURCE_TYPE);
			imageNode.setProperty(PN_REFERENCE, assetPath);
		} catch (RepositoryException e) {
			throw new ImporterException(e.getMessage(), e);
		}
	}

	private String createImagePath(final String path) {
		return PATH_JOINER.join(path, "image");
	}
}
