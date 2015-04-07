package com.elasticpath.commerce.importer.jcr.impl;

import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.CODE;
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.COMMERCE_ROOT_PATH;
import static com.elasticpath.commerce.importer.constants.ElasticPathImporterConstants.PATH_JOINER;
import static java.lang.String.format;
import static org.apache.sling.jcr.resource.JcrResourceConstants.NT_SLING_FOLDER;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.ImmutableList;

import com.elasticpath.commerce.importer.impl.ImporterResult;
import com.elasticpath.commerce.importer.model.AemCategory;
import com.elasticpath.commerce.importer.jcr.CategoryService;
import com.elasticpath.commerce.importer.jcr.JcrUtilService;

/**
 * Implementation of {@link com.elasticpath.commerce.importer.jcr.CategoryService}.
 */
public class CategoryServiceImpl implements CategoryService {

	private final PropertyMapper propertyMapper = new PropertyMapper();

	@Inject
	private JcrUtilService jcrUtil;

	@Override
	public void createCategory(final Session session,
							   final AemCategory category,
							   final ImporterResult importerResult) throws RepositoryException {
		String categoryPath = createCategoryPath(category);

		createCategoryNode(session, categoryPath, importerResult);
		updateCategoryNode(session, category, categoryPath);
	}

	private String createCategoryPath(final AemCategory category) {
		return PATH_JOINER
				.join(
						ImmutableList.builder()
								.add(COMMERCE_ROOT_PATH)
								.add(category.getCatalogId())
								.addAll(category.getCategoryHierarchy())
								.add(category.getCategoryCode())
								.build()
				);
	}

	@Override
	public void createCategoryNode(final Session session,
								   final String categoryPath,
								   final ImporterResult importerResult)
			throws RepositoryException {

		if (!session.nodeExists(categoryPath)) {
			// create category node
			jcrUtil.createPath(categoryPath, NT_SLING_FOLDER, session);

			importerResult.logMessage(format("Created category %s", categoryPath), false);
			importerResult.incrementCategory();
		}
	}

	private void updateCategoryNode(final Session session,
									final AemCategory category,
									final String path) throws RepositoryException {
		Node node = session.getNode(path);
		node.setProperty(CODE, category.getCategoryCode());
		propertyMapper.writeProperties(node, category.getProperties());
	}
}
