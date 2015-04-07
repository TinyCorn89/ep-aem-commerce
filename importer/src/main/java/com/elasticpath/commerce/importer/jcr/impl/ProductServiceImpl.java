package com.elasticpath.commerce.importer.jcr.impl;

import static com.adobe.cq.commerce.api.CommerceConstants.PN_COMMERCE_TYPE;
import static com.adobe.cq.commerce.api.CommerceConstants.PN_PRODUCT_VARIANT_AXES;
import static com.day.cq.commons.jcr.JcrConstants.JCR_LASTMODIFIED;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;
import static com.elasticpath.commerce.config.ElasticPathGlobalConstants.PRODUCT_IDENTIFIER;
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.IDENTIFIER;
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.PRODUCT_RESOURCE_TYPE;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.sling.jcr.resource.JcrResourceConstants.NT_SLING_FOLDER;
import static org.apache.sling.jcr.resource.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;

import java.security.AccessControlException;
import java.util.Calendar;

import javax.inject.Inject;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.google.common.base.Function;

import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.commerce.importer.jcr.JcrUtilService;
import com.elasticpath.commerce.importer.batch.BatchManagerService;
import com.elasticpath.commerce.importer.batch.BatchStatus;
import com.elasticpath.commerce.importer.config.ImporterConfig;
import com.elasticpath.commerce.importer.exception.ImporterException;
import com.elasticpath.commerce.importer.impl.ImporterResult;
import com.elasticpath.commerce.importer.model.AemProduct;
import com.elasticpath.commerce.importer.jcr.ProductService;

/**
 * Default ProductService implementation.
 */
public class ProductServiceImpl implements ProductService {

	private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

	private static final String NN_BUCKET = "bucket";
	private static final String CQ_IMPORT_BUCKET = "cq:importBucket";
	private static final String CQ_IMPORT_COUNT = "cq:importCount";

	@Inject
	private ImporterConfig importerConfig;

	@Inject
	private BatchManagerService batchManagerService;

	@Inject
	private PropertyMapper propertyMapper;

	@Inject
	private JcrUtilService jcrUtilService;

	@Override
	public Node createProduct(final Session session,
							  final String path,
							  final BatchStatus batchStatus,
							  final ImporterResult importerResult) throws RepositoryException {
		Node parent = getParentNode(session, path);

		Node product = jcrUtilService.createUniqueNode(parent, Text.getName(path), NT_UNSTRUCTURED, session);
		product.setProperty(PN_COMMERCE_TYPE, "product");
		product.setProperty(SLING_RESOURCE_TYPE_PROPERTY, "commerce/components/product");
		product.setProperty(JCR_LASTMODIFIED, Calendar.getInstance());

		importerResult.incrementProduct();
		importerResult.logMessage("Created product   " + product.getPath(), false);
		batchStatus.updateTicker(makeTickerMessage(importerResult));
		batchManagerService.checkpoint(session, false, batchStatus, importerResult);

		return product;
	}

	private Node getParentNode(final Session session, final String path) throws RepositoryException {
		String parentPath = Text.getRelativeParent(path, 1);
		Node parent = jcrUtilService.createPath(parentPath, false, NT_SLING_FOLDER, NT_SLING_FOLDER, session, false);
		boolean bucketing = false;
		//Is there a pointer to a bucket if so change this to parent node and change flag
		if (parent.hasProperty(CQ_IMPORT_BUCKET)) {
			parent = parent.getNode(parent.getProperty(CQ_IMPORT_BUCKET)
					.getString());
			bucketing = true;
		}

		long count = parent.hasProperty(CQ_IMPORT_COUNT) ? parent.getProperty(CQ_IMPORT_COUNT)
				.getLong() + 1L : 1L;

		//are we over bucketMax nodes? (default: 500)
		if (count > importerConfig.getBucketMax()) {
			if (bucketing) {
				//change to bucket parent
				parent = parent.getParent();
			} else {
				//create bucket and move existing nodes into bucket
				demoteProductChildrenToBucket(parent, session);
			}

			//create a new bucket and set as parent
			Node bucket = jcrUtilService.createUniqueNode(parent, NN_BUCKET, NT_SLING_FOLDER, session);
			parent.setProperty(CQ_IMPORT_BUCKET, bucket.getName());
			parent = bucket;
			count = 1L;
		}

		parent.setProperty(CQ_IMPORT_COUNT, count);
		return parent;
	}

	@Override
	public void deleteProduct(final Session session,
							  final String productPath) {
		try {
			if (!session.itemExists(productPath)) {
				return;
			}

			Item item = session.getItem(productPath);
			item.remove();
			session.save();
		} catch (RepositoryException e) {
			throw new ImporterException(e.getMessage(), e);
		}
	}

	@Override
	public void updateProduct(final ResourceResolver resourceResolver,
							  final TagManager tagManager,
							  final Node productNode,
							  final AemProduct product,
							  final String productPath) throws RepositoryException {

		String code = isSingleSku(product) ? product.getSkuCode() : product.getProductCode();

		productNode.setProperty(IDENTIFIER, code);

		productNode.setProperty(PRODUCT_IDENTIFIER, code);
		productNode.setProperty(SLING_RESOURCE_TYPE_PROPERTY, PRODUCT_RESOURCE_TYPE);
		productNode.setProperty(PN_COMMERCE_TYPE, "product");
		productNode.setProperty(JCR_TITLE, product.getDisplayName());
		productNode.addMixin(TagConstants.NT_TAGGABLE);

		registerVariantAxes(productNode, product.getVariantAxes());

		propertyMapper.writeProperties(productNode, product.getProperties());

		updateProductTags(
				tagManager,
				resourceResolver.getResource(productPath),
				product.getTags(),
				product.getProductCode()
		);
	}

	private boolean isSingleSku(final AemProduct product) {
		return product.getVariantAxes() != null && isEmpty(product.getVariantAxes());
	}

	private void registerVariantAxes(final Node productNode,
									 final Iterable<String> axes) throws RepositoryException {
		if (axes != null && !isEmpty(axes)) {
			productNode.setProperty(PN_PRODUCT_VARIANT_AXES, toArray(axes, String.class));
		}
	}

	private void updateProductTags(final TagManager tagManager,
								   final Resource productResource,
								   final Iterable<String> tagNames,
								   final String productCode) {
		if (tagNames == null) {
			return;
		}

		Tag[] tags = toArray(transform(tagNames, new Function<String, Tag>() {

			public Tag apply(final String input) {
				try {
					return tagManager.createTag(input, EMPTY, EMPTY);
				} catch (AccessControlException | InvalidTagFormatException e) {
					LOG.error("Exception while creating tags for product {}", productCode, e);
					throw new ImporterException(e.getMessage(), e);
				}
			}
		}), Tag.class);

		tagManager.setTags(productResource, tags, false);
	}

	private void demoteProductChildrenToBucket(final Node parent,
											   final Session session) throws RepositoryException {
		Node bucket = jcrUtilService.createUniqueNode(parent, NN_BUCKET, NT_SLING_FOLDER, session);
		NodeIterator children = parent.getNodes();
		long productCount = 0L;

		while (children.hasNext()) {
			Node child = (Node) children.next();
			if (child.hasProperty(PN_COMMERCE_TYPE) && child.getProperty(PN_COMMERCE_TYPE)
					.getString()
					.equals("product")) {
				jcrUtilService.copy(child, bucket, child.getName())
						.getPath();
				child.remove();

				++productCount;
			}
		}

		bucket.setProperty(CQ_IMPORT_COUNT, productCount);
		parent.setProperty(CQ_IMPORT_COUNT, (Value) null);
	}

	private String makeTickerMessage(final ImporterResult importerResult) {
		return importerResult.getProductCount() + " products imported/updated";
	}

}
