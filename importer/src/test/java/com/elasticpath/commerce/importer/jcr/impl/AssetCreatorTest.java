package com.elasticpath.commerce.importer.jcr.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.day.cq.dam.api.AssetManager;

import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test.
 */
@RunWith(MockitoJUnitRunner.class)
public class AssetCreatorTest {

	@InjectMocks
	private AssetCreator assetCreator;

	/**
	 * Test.
	 *
	 * @throws IOException if there's a problem reading the file
	 */
	@Test
	public void ensureAssetIsAdded() throws IOException {
		AssetManager assetManager = mock(AssetManager.class);
		String anyDestination = "any-destination";

		assetCreator.createAssetFromBytes(assetManager, anyDestination, new byte[]{0});

		verify(assetManager, times(1)).createAsset(eq(anyDestination), any(FileInputStream.class), isNull(String.class), eq(false));
	}
}
