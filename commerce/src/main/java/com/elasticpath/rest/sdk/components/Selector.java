package com.elasticpath.rest.sdk.components;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Selector Representation.
 */
public class Selector {
	@JsonProperty("_choice")
	private List<Choice> choices;

	@JsonProperty("_chosen")
	private List<Choice> chosens;

	public List<Choice> getChoices() {
		return choices;
	}

	public List<Choice> getChosens() {
		return chosens;
	}
}
