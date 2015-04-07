package com.elasticpath.aem.commerce.service.impl;

import javax.jcr.InvalidItemStateException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.adobe.granite.security.user.UserProperties;
import com.elasticpath.aem.commerce.exception.SaveConflictException;
import com.elasticpath.aem.commerce.service.UserPropertiesService;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

/**
 * Service for updating UserProperties - Jcr node impl.
 */
@Component(
		label = "Elastic Path User Properties Service",
		description = "Provides persistence of the user properties in the JCR tree."
)
@Service(UserPropertiesService.class)
public class UserPropertiesServiceImpl implements UserPropertiesService {
	/**
	 * Update a user node property.
	 *
	 * @param userProperties the UserProperties instance that represents the user.
	 * @param propertyName   the name of the property
	 * @param propertyValue  the value of the property
	 * @throws SaveConflictException a conflict occurred during a save operation.
	 * @throws RepositoryException some other persistence error.
	 */
	@Override
	public void updateUserProperty(final UserProperties userProperties,
									  final String propertyName,
									  final String propertyValue) throws SaveConflictException, RepositoryException {
		Node node = userProperties.getNode();
		node.getSession().refresh(false);
		node.setProperty(propertyName, propertyValue);
		try {
			node.getSession().save();
		} catch (InvalidItemStateException invalidItemState) {
			throw new SaveConflictException(String.format("Conflict occurred while saving update (%s) to node (%s)",
					propertyName,
					node.getPath()),
					invalidItemState);
		}
	}
}
