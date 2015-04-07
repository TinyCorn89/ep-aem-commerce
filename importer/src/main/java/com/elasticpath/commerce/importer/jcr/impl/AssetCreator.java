package com.elasticpath.commerce.importer.jcr.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;

/**
 * Creates assets from sources with a given asset manager.
 */
public class AssetCreator {

	/**
	 * Given the path to a file, creates an {@link com.day.cq.dam.api.Asset} using the provided {@link com.day.cq.dam.api.AssetManager}.
	 *
	 * @param assetManager    the {@link com.day.cq.dam.api.AssetManager} which will be used to create the {@link com.day.cq.dam.api.Asset}
	 * @param destinationPath the path under which the asset should be created
	 * @param image           image
	 * @return the newly created {@link com.day.cq.dam.api.Asset}
	 */
	public Asset createAssetFromBytes(final AssetManager assetManager,
									  final String destinationPath,
									  final byte[] image) {
		InputStream inputstream = new ByteArrayInputStream(image);
		return assetManager.createAsset(destinationPath, inputstream, null, false);
	}
}
