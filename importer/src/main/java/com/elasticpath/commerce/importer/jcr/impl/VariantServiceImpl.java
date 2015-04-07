package com.elasticpath.commerce.importer.jcr.impl;

import static com.adobe.cq.commerce.api.CommerceConstants.PN_COMMERCE_TYPE;
import static com.day.cq.commons.jcr.JcrConstants.JCR_LASTMODIFIED;
import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;
import static com.elasticpath.commerce.config.ElasticPathGlobalConstants.PRODUCT_IDENTIFIER;
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.IDENTIFIER;
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.PATH_JOINER;
import static com.google.common.collect.Iterables.isEmpty;
import static org.apache.sling.jcr.resource.JcrResourceConstants.NT_SLING_FOLDER;
import static org.apache.sling.jcr.resource.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;

import java.util.Calendar;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.day.cq.dam.api.AssetManager;

import com.elasticpath.commerce.importer.batch.BatchManagerService;
import com.elasticpath.commerce.importer.batch.BatchStatus;
import com.elasticpath.commerce.importer.impl.ImporterResult;
import com.elasticpath.commerce.importer.jcr.AssetService;
import com.elasticpath.commerce.importer.jcr.JcrUtilService;
import com.elasticpath.commerce.importer.jcr.VariantService;
import com.elasticpath.commerce.importer.model.AemAxis;
import com.elasticpath.commerce.importer.model.AemVariant;

/**
 * Default ProductService implementation.
 */
public class VariantServiceImpl implements VariantService {

	@Inject
	private BatchManagerService batchManagerService;

	@Inject
	private AssetService assetService;

	@Inject
	private JcrUtilService jcrUtil;

	@Override
	public Node createVariant(final Node parentProduct,
							  final String name,
							  final BatchStatus batchStatus,
							  final ImporterResult importerResult) throws RepositoryException {
		String validName = jcrUtil.createValidName(name);

		Node variant = jcrUtil.createUniqueNode(parentProduct, validName, NT_UNSTRUCTURED, parentProduct.getSession());
		variant.setProperty(PN_COMMERCE_TYPE, "variant");
		variant.setProperty(SLING_RESOURCE_TYPE_PROPERTY, "commerce/components/product");
		variant.setProperty(JCR_LASTMODIFIED, Calendar.getInstance());

		importerResult.incrementVariant();
		importerResult.logMessage("Created variation " + variant.getPath(), false);
		batchManagerService.checkpoint(parentProduct.getSession(), false, batchStatus, importerResult);
		return variant;
	}

	@Override
	public void createVariants(final Session session,
							   final AssetManager assetManager,
							   final String rootProductNodePath,
							   final Iterable<AemVariant> variants,
							   final String catalogId,
							   final BatchStatus batchStatus,
							   final ImporterResult importerResult) throws RepositoryException {
		if (isEmpty(variants)) {
			return;
		}

		/*
		 * Code in the inner loop acts upon every single axis in the tree.
		 * Code in the outer loop, after the inner loop acts upon only the leaf nodes.
		 * e.g. /blue/15
		 * inside inner loop operates on nodes 'blue' and '15
		 * post inner loop operates on '15' only
		 */
		Node currentVariantNode = null;
		for (AemVariant variant : variants) {

			String productNodePath = rootProductNodePath;
			for (AemAxis axis : variant.getAxes()) {
				String formattedAxis = jcrUtil.createValidName(axis.getValue());
				Node variantParentNode = jcrUtil.createPath(productNodePath, NT_SLING_FOLDER, session);
				productNodePath = PATH_JOINER.join(productNodePath, formattedAxis);

				if (session.nodeExists(productNodePath)) {
					continue;
				}

				currentVariantNode = createVariant(
						variantParentNode,
						axis.getValue(),
						batchStatus,
						importerResult
				);

				updateVariant(session, assetManager, catalogId, currentVariantNode, variant, axis);
			}

			if (currentVariantNode == null) {
				continue;
			}

			updateLeafVariants(currentVariantNode, variant);
		}
	}

	private void updateVariant(final Session session,
							   final AssetManager assetManager,
							   final String catalogId,
							   final Node currentVariantNode,
							   final AemVariant variant,
							   final AemAxis axis) throws RepositoryException {
		currentVariantNode.setProperty(axis.getName(), axis.getValue());

		// TODO 2014-10-14 dom: Only leaf nodes and products requires images. CMS-1723 / CMS-1671. Activate on last axis only?
		assetService.createAsset(session,
				assetManager,
				catalogId,
				currentVariantNode.getPath(),
				variant.getImagesUrl(),
				variant.getImage()
		);
	}

	private void updateLeafVariants(final Node currentVariantNode,
									final AemVariant variant) throws RepositoryException {
		currentVariantNode.setProperty(IDENTIFIER, variant.getSkuCode());
		currentVariantNode.setProperty(PRODUCT_IDENTIFIER, variant.getSkuCode());
	}
}
