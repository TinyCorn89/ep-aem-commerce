package com.elasticpath.aem.commerce.service;

import javax.jcr.RepositoryException;

import com.adobe.granite.security.user.UserProperties;
import com.elasticpath.aem.commerce.exception.SaveConflictException;

/**
 * Service for updating UserProperties.
 */
public interface UserPropertiesService {
	/**
	 * Update a user node property.
	 * @param userProperties the UserProperties instance that represents the user.
	 * @param propertyName the name of the property
	 * @param propertyValue the value of the property
	 * @throws SaveConflictException a conflict occurred during a save operation.
	 * @throws RepositoryException some other persistence error.
	 */
	void updateUserProperty(UserProperties userProperties,
							   String propertyName,
							   String propertyValue) throws SaveConflictException, RepositoryException;
}
