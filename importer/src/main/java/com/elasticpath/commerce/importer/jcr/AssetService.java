package com.elasticpath.commerce.importer.jcr;

import javax.jcr.Node;
import javax.jcr.Session;

import com.day.cq.dam.api.AssetManager;

/**
 * Manages assets on jcr.
 */
public interface AssetService {

	/**
	 * Creates an asset.
	 *
	 * @param session      jcr Session
	 * @param assetManager jcr AssetManager
	 * @param catalogId    String
	 * @param path         path to store the image
	 * @param imagesUrl    image url
	 * @param image        image byte data
	 */
	void createAsset(Session session,
					 AssetManager assetManager,
					 String catalogId,
					 String path,
					 String imagesUrl,
					 byte[] image);

	/**
	 * Updates an asset.
	 *
	 * @param session      jcr Session
	 * @param assetManager jcr AssetManager
	 * @param catalogId    String
	 * @param imageNode    Node
	 * @param imagesUrl    image url
	 * @param image        image byte data
	 */
	void updateCatalogAsset(Session session,
							AssetManager assetManager,
							String catalogId,
							Node imageNode,
							String imagesUrl,
							byte[] image);
}
