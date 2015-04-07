/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.sdk.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Address element representation.
 */
public class AddressEntry extends Linkable {

	@JsonProperty("address")
	private Address address;

	@JsonProperty("name")
	private Name name;

	public Name getName() {
		return name;
	}

	public Address getAddress() {
		return address;
	}

	/**
	 * Sets the adress.
	 * 
	 * @param address the address
	 */
	public void setAddress(final Address address) {
		this.address = address;
	}

	/**
	 * Sets the address name.
	 * 
	 * @param name the user's name
	 */
	public void setName(final Name name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "AddressEntry [name=" + name + ", [address=" + address + "]";
	}
}
