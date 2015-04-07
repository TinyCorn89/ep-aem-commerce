package com.elasticpath.commerce.importer.jcr.impl;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.day.cq.commons.jcr.JcrUtil;

import com.elasticpath.commerce.importer.jcr.JcrUtilService;

/**
 * Wraps a static utility for more comfortable usage.
 */
public class JcrUtilServiceImpl implements JcrUtilService {

	@Override
	public Node createPath(final String absolutePath,
						   final String nodeType,
						   final Session session) throws RepositoryException {
		return JcrUtil.createPath(absolutePath, nodeType, session);
	}

	@Override
	public Node createPath(final String absolutePath,
						   final String intermediateNodeType,
						   final String nodeType,
						   final Session session,
						   final boolean autoSave) throws RepositoryException {
		return JcrUtil.createPath(absolutePath, intermediateNodeType, nodeType, session, autoSave);
	}

	@Override
	public Node createPath(final String absolutePath,
						   final boolean createUniqueLeaf,
						   final String intermediateNodeType,
						   final String nodeType,
						   final Session session,
						   final boolean autoSave) throws RepositoryException {
		return JcrUtil.createPath(absolutePath, createUniqueLeaf, intermediateNodeType, nodeType, session, autoSave);
	}

	@Override
	public Node createUniqueNode(final Node parentProduct,
								 final String nodeNameHint,
								 final String nodeType,
								 final Session session) throws RepositoryException {
		return JcrUtil.createUniqueNode(parentProduct, nodeNameHint, nodeType, session);
	}

	@Override
	public Node copy(final Node source,
					 final Node destination,
					 final String destinationName) throws RepositoryException {
		return JcrUtil.copy(source, destination, destinationName);
	}

	@Override
	public String createValidName(final String name) {
		return JcrUtil.createValidName(name);
	}
}
