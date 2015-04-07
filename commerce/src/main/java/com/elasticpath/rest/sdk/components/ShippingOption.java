package com.elasticpath.rest.sdk.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Locale;

import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.api.promotion.PromotionInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ShippingOption.
 */
public class ShippingOption extends Linkable {
	private String carrier;
	@JsonProperty("display-name")
	private String displayName;
	@JsonProperty("name")
	private String name;
	@JsonProperty("cost")
	private Iterable<Cost> costs;
	@JsonProperty("_appliedpromotions")
	private Collection<AppliedPromotions> shippingOptionPromotions;

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(final String carrier) {
		this.carrier = carrier;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Iterable<Cost> getCosts() {
		return costs;
	}

	public void setCosts(final Iterable<Cost> costs) {
		this.costs = costs;
	}

	/**
	 * Get order total.
	 * 
	 * @param locale the locale
	 * @return the order total
	 */
	public PriceInfo getShippingPrice(final Locale locale) {
		Cost shippingCost = costs.iterator().next();
		return new PriceInfo(shippingCost.getAmount(), locale, Currency.getInstance(shippingCost.getCurrency()));
	}

	/**
	 * Sets the applied shipping option promotions.
	 * 
	 * @param shippingOptionPromotions the applied shipping option promotions
	 */
	public void setAppliedPromotions(final Collection<AppliedPromotions> shippingOptionPromotions) {
		this.shippingOptionPromotions = shippingOptionPromotions;
	}

	/**
	 * Gets the applied shipping option promotions.
	 * 
	 * @return the applied shipping option promotions
	 */
	public Collection<PromotionInfo> getAppliedPromotions() {
		Collection<PromotionInfo> appliedShippingOptionPromotions = new ArrayList<>();

		if (shippingOptionPromotions != null && !shippingOptionPromotions.isEmpty()) {
			AppliedPromotions appliedPromotions = shippingOptionPromotions.iterator().next();

			for (Promotion promotion : appliedPromotions.getPromotions()) {
				appliedShippingOptionPromotions.add(new PromotionInfo(promotion.getName(), promotion.getDisplayName(),
																	  PromotionInfo.PromotionStatus.FIRED, promotion.getDisplayDescription(),
																	  promotion.getDisplayDescription(), null));
			}
		}

		return appliedShippingOptionPromotions;
	}
}
