/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.aem.cookies;

import java.util.Date;

import com.elasticpath.aem.commerce.cortex.AbstractCortexContext;

/**
 * Extension of the basic HttpCortexContext that includes a cookie version and a unique identifier.
 */
public class AemCortexContext extends AbstractCortexContext {
	private final String identifier;
	private final String version;

	/**
	 * Create a new cookie context.
	 *
	 * @param authToken cortex auth token.
	 * @param scope cortex store scope.
	 * @param role token role.
	 * @param expiryDate expiry date.
	 * @param identifier cookie identifier.
	 * @param version cookie verison.
	 */
	AemCortexContext(final String authToken,
					 final String scope,
					 final String role,
					 final Date expiryDate,
					 final String identifier,
					 final String version) {
		super(authToken, scope, role, expiryDate);
		this.identifier = identifier;
		this.version = version;
	}

	/**
	 * Hash associated with this cookie and user.
	 *
	 * @return identifier value.
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Version for this cookie value.
	 *
	 * @return version value.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Builder for the CookieContext.
	 */
	public static class ContextBuilder {

		private String authenticationToken;
		private String scope;
		private String role;
		private Date expiryDate;
		private String identifier;
		private String version;

		/**
		 * Sets the authentication token.
		 *
		 * @param authenticationToken token
		 * @return this Builder
		 */
		public ContextBuilder withAuthenticationToken(final String authenticationToken) {
			this.authenticationToken = authenticationToken;
			return this;
		}

		/**
		 * Sets the scope.
		 *
		 * @param scope of context
		 * @return this Builder
		 */
		public ContextBuilder withScope(final String scope) {
			this.scope = scope;
			return this;
		}

		/**
		 * Sets the role associated with token.
		 *
		 * @param role of user.
		 * @return this Builder
		 */
		public ContextBuilder withRole(final String role) {
			this.role = role;
			return this;
		}

		/**
		 * The date the token expires in the future.
		 *
		 * @param expiryDate expiry date
		 * @return this Builder
		 */
		public ContextBuilder withExpiryDate(final Date expiryDate) {
			this.expiryDate = new Date(expiryDate.getTime());
			return this;
		}

		/**
		 * Sets the identifier associated with this AEM user.
		 *
		 * @param identifier value.
		 * @return identifier value.
		 */
		public ContextBuilder withIdentifier(final String identifier) {
			this.identifier = identifier;
			return this;
		}

		/**
		 * Sets the version of this cookie format.
		 *
		 * @param version value.
		 * @return version value.
		 */
		public ContextBuilder withVersion(final String version) {
			this.version = version;
			return this;
		}

		/**
		 * Create a new CortexContext.
		 * @return new Instance.
		 */
		public AemCortexContext build() {
			return new AemCortexContext(authenticationToken, scope, role, expiryDate, identifier, version);
		}
	}
}
