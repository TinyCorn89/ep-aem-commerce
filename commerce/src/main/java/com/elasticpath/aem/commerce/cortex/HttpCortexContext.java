/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.aem.commerce.cortex;

import java.util.Date;

/**
 * Http-specific cortex context object.
 */
public class HttpCortexContext extends AbstractCortexContext {
	private static final long serialVersionUID = 20120621L;

	/**
	 * Default Constructor to match parent.
	 * @param authToken auth token.
	 * @param scope scope of token.
	 * @param role users role.
	 * @param expires expires.
	 */
	protected HttpCortexContext(final String authToken, final String scope, final String role, final Date expires) {
		super(authToken, scope, role, expires);
	}

	/**
	 * Builder for the HttpCortexContext.
	 */
	public static class ContextBuilder {

		private String authenticationToken;
		private String scope;
		private String role;
		private Date expires;

		/**
		 * add an authentication token.
		 *
		 * @param authenticationToken token
		 * @return this Builder
		 */
		public ContextBuilder withAuthenticationToken(final String authenticationToken) {
			this.authenticationToken = authenticationToken;
			return this;
		}

		/**
		 * add scope.
		 * @param scope of context
		 * @return this Builder
		 */
		public ContextBuilder withScope(final String scope) {
			this.scope = scope;
			return this;
		}

		/**
		 * role associtated with token.
		 * @param role of user.
		 * @return this Builder
		 */
		public ContextBuilder withRole(final String role) {
			this.role = role;
			return this;
		}

		/**
		 * Timestaps that represents when the token expires in the future (not how long its valid for).
		 * @param expires expiry timestamp
		 * @return this Builder
		 */
		public ContextBuilder withExpiryDate(final Date expires) {
			this.expires = new Date(expires.getTime());
			return this;
		}

		/**
		 * Create a new CortexContext.
		 * @return new Instance.
		 */
		public CortexContext build() {
			return new HttpCortexContext(authenticationToken, scope, role, expires);
		}
	}
}

