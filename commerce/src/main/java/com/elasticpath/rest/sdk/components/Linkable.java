package com.elasticpath.rest.sdk.components;

/**
 * Representation that contains both links and self.
 */
public class Linkable {

	private Iterable<Link> links;

	private Self self;

	public Self getSelf() {
		return self;
	}

	public void setSelf(final Self self) {
		this.self = self;
	}

	public Iterable<Link> getLinks() {
		return links;
	}

	public void setLinks(final Iterable<Link> links) {
		this.links = links;
	}
}

