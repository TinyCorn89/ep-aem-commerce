package com.elasticpath.commerce.importer.importers.impl;

import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.COMMERCE_ROOT_PATH;
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.PATH_JOINER;
import static java.lang.String.format;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.day.cq.dam.api.AssetManager;
import com.day.cq.tagging.TagManager;
import com.google.common.collect.ImmutableList;

import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.commerce.importer.batch.BatchStatus;
import com.elasticpath.commerce.importer.impl.ImporterResult;
import com.elasticpath.commerce.importer.importers.ProductImporter;
import com.elasticpath.commerce.importer.jcr.AssetService;
import com.elasticpath.commerce.importer.jcr.ProductService;
import com.elasticpath.commerce.importer.jcr.VariantService;
import com.elasticpath.commerce.importer.model.AemProduct;

/**
 * Handles the importing of products from an input model AemProduct model object to calling services that store the data.
 */
public class DefaultProductImporterImpl implements ProductImporter {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultProductImporterImpl.class);

	@Inject
	private AssetService assetService;

	@Inject
	private ProductService productService;

	@Inject
	private VariantService variantService;

	@Override
	public void importProduct(final ResourceResolver resourceResolver,
							  final AssetManager assetManager,
							  final Session session,
							  final TagManager tagManager,
							  final AemProduct product,
							  final BatchStatus batchStatus,
							  final ImporterResult importerResult) throws RepositoryException {
		String productPath = createProductPath(product);

		productService.deleteProduct(session, productPath);

		Node productNode = productService.createProduct(
				session,
				productPath,
				batchStatus,
				importerResult
		);

		productService.updateProduct(
				resourceResolver,
				tagManager,
				productNode,
				product,
				productNode.getPath()
		);

		assetService.createAsset(
				session,
				assetManager,
				product.getCatalogId(),
				productNode.getPath(),
				product.getImagesUrl(),
				product.getImage()
		);

		variantService.createVariants(
				session,
				assetManager,
				productNode.getPath(),
				product.getVariants(),
				product.getCatalogId(),
				batchStatus,
				importerResult
		);
	}

	@Override
	public void error(final String callerId,
					  final AemProduct product,
					  final ImporterResult importerResult,
					  final Exception exception) {
		String message = format(
				"Error importing product [%s] from exchange [%s]",
				product.getProductCode(),
				callerId
		);
		LOG.error(message, exception);
		importerResult.logMessage(message, true);
	}

	private String createProductPath(final AemProduct product) {
		return PATH_JOINER
				.join(ImmutableList.builder()
						.add(COMMERCE_ROOT_PATH)
						.add(product.getCatalogId())
						.addAll(product.getCategoryHierarchy())
						.add(product.getProductCode())
						.build());
	}
}
