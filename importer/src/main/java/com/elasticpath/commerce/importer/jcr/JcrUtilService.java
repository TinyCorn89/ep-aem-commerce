package com.elasticpath.commerce.importer.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Wraps a static utility for more comfortable usage.
 */
public interface JcrUtilService {

	/**
	 * Creates a node at the provided path on jcr.
	 *
	 * @param absolutePath The absolutePath of the node to create
	 * @param nodeType     The type of node to create, often from {@link com.day.cq.commons.jcr.JcrConstants}
	 * @param session      The session within which to create the node
	 * @return The created Node
	 * @throws RepositoryException if there is an error on the repository
	 * @see com.day.cq.commons.jcr.JcrUtil#createPath(String, String, Session)
	 */
	Node createPath(String absolutePath,
					String nodeType,
					Session session) throws RepositoryException;

	/**
	 * Creates a node at the provided path on jcr.
	 *
	 * @param absolutePath         The absolutePath of the node to create
	 * @param intermediateNodeType The intermediate node types to create when building missing path elements
	 *                             often from {@link com.day.cq.commons.jcr.JcrConstants}
	 * @param nodeType             The type of node to create, often from {@link com.day.cq.commons.jcr.JcrConstants}
	 * @param session              The session within which to create the node
	 * @param autoSave             Whether to save the node immediately or wait for a flush
	 * @return The created Node
	 * @throws RepositoryException if there is an error on the repository
	 * @see com.day.cq.commons.jcr.JcrUtil#createPath(String, String, String, Session, boolean)
	 */
	Node createPath(String absolutePath,
					String intermediateNodeType,
					String nodeType,
					Session session,
					boolean autoSave) throws RepositoryException;

	/**
	 * Creates a node at the provided path on jcr.
	 *
	 * @param absolutePath         The absolutePath of the node to create
	 * @param createUniqueLeaf     If true will return the Node if it already exists
	 * @param intermediateNodeType The intermediate node types to create when building missing path elements often from
	 *                             {@link com.day.cq.commons.jcr.JcrConstants}
	 * @param nodeType             The type of node to create, often from {@link com.day.cq.commons.jcr.JcrConstants}
	 * @param session              The session within which to create the node
	 * @param autoSave             Whether to save the node immediately or wait for a flush
	 * @return The created Node
	 * @throws RepositoryException if there is an error on the repository
	 * @see com.day.cq.commons.jcr.JcrUtil#createPath(String, boolean, String, String, Session, boolean)
	 */
	Node createPath(String absolutePath,
					boolean createUniqueLeaf,
					String intermediateNodeType,
					String nodeType,
					Session session,
					boolean autoSave) throws RepositoryException;

	/**
	 * Creates a node on jcr, using the provided name as a hint and appending a counter if the node already exists.
	 *
	 * @param parentProduct The parent node
	 * @param nodeNameHint  A hint as to what the desired node name is
	 * @param nodeType      The type of node to create, often from {@link com.day.cq.commons.jcr.JcrConstants}
	 * @param session       The session within which to create the node
	 * @return The created Node
	 * @throws RepositoryException if there is an error on the repository
	 * @see com.day.cq.commons.jcr.JcrUtil#createUniqueNode(Node, String, String, Session)
	 */
	Node createUniqueNode(Node parentProduct,
						  String nodeNameHint,
						  String nodeType,
						  Session session) throws RepositoryException;

	/**
	 * Copies a node.
	 *
	 * @param source          Source {@link javax.jcr.Node}
	 * @param destination     Node
	 * @param destinationName Destination node name
	 * @return The created Node
	 * @throws RepositoryException if there is an error on the repository
	 * @see com.day.cq.commons.jcr.JcrUtil#copy(Node, Node, String)
	 */
	Node copy(Node source,
			  Node destination,
			  String destinationName) throws RepositoryException;

	/**
	 * Converts the given string to a name that is valid on a jcr node.
	 *
	 * @param name Desired node name String
	 * @return The valid node name String
	 * @see com.day.cq.commons.jcr.JcrUtil#createValidName(String)
	 */
	String createValidName(String name);
}
