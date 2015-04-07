/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.jcr.RepositoryException;

import com.adobe.cq.commerce.api.CommerceConstants;
import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.PlacedOrder;
import com.adobe.cq.commerce.api.PlacedOrderResult;
import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.api.ShippingMethod;
import com.adobe.cq.commerce.api.promotion.PromotionInfo;
import com.adobe.cq.commerce.api.promotion.VoucherInfo;
import com.adobe.cq.commerce.common.AbstractJcrCommerceSession;
import com.adobe.cq.commerce.common.PriceFilter;
import com.adobe.granite.security.user.UserProperties;
import com.day.cq.personalization.UserPropertiesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.aem.commerce.AbstractElasticPathCommerceService;
import com.elasticpath.aem.commerce.ElasticPathCommerceService;
import com.elasticpath.aem.commerce.ElasticPathCommerceSession;
import com.elasticpath.aem.commerce.util.ElasticPathHelper;
import com.elasticpath.rest.sdk.Cart;
import com.elasticpath.rest.sdk.CortexSdkServiceFactory;
import com.elasticpath.rest.sdk.service.CartService;
import com.elasticpath.rest.sdk.service.GeographyService;
import com.elasticpath.rest.sdk.service.OrderConfigurationService;
import com.elasticpath.rest.sdk.service.OrderService;
import com.elasticpath.rest.sdk.service.ProductService;
import com.elasticpath.rest.sdk.service.PromotionService;
import com.elasticpath.rest.sdk.service.PurchaseService;

/**
 * ElasticPathCommerceSessionImpl provides functionalities related to resources like item, carts, order, address etc and also performs actions on the
 * basis of action type.
 */
@SuppressWarnings("PMD.GodClass")
public class ElasticPathCommerceSessionImpl extends AbstractJcrCommerceSession implements ElasticPathCommerceSession {
	private static final String SEPARATOR = ".";

	/**
	 * The Constant LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ElasticPathCommerceSessionImpl.class);

	private final CortexSdkServiceFactory cortexSdkServiceFactory;

	/**
	 * Instantiates ElasticPathCommerceSessionImpl.
	 *
	 * @param commerceService         the commerce service
	 * @param request                 the request.
	 * @param response                the response
	 * @param resource                the resource
	 * @param cortexSdkServiceFactory the cortex sdk services factory
	 * @throws CommerceException the commerce exception
	 */
	public ElasticPathCommerceSessionImpl(final AbstractElasticPathCommerceService commerceService,
			final SlingHttpServletRequest request,
			final SlingHttpServletResponse response,
			final Resource resource,
			final CortexSdkServiceFactory cortexSdkServiceFactory)
			throws CommerceException {
		super(commerceService, request, response, resource);

		PN_UNIT_PRICE = ElasticPathProductImpl.PN_PRICE;
		this.commerceService = commerceService;
		this.resolver = resource.getResourceResolver();
		setUserLocale(ElasticPathHelper.getLocaleFromPage(resource));
		this.cortexSdkServiceFactory = cortexSdkServiceFactory;

		// This is to remove randomly generated orderId from new session
		orderDetails.put(PN_ORDER_ID, "");
		//clear preset values
		prices = new ArrayList<>();
	}

	@Override
	protected void loadCart() throws CommerceException {
		// Disable this behaviour as EP handles cart persistence.
	}

	@Override
	protected void calcCart() {
		// Disable this behaviour as EP handles cart persistence.
	}

	@Override
	protected void saveCart() throws CommerceException {
		// Disable this behaviour as EP handles cart persistence.
	}

	@Override
	protected void calcOrder() throws CommerceException {
		// Disable this behaviour as EP handles order persistence.
	}

	/**
	 * Returns total price in the cart.
	 *
	 * @return String cart total price
	 * @throws CommerceException commerceException
	 */
	@Override
	public String getCartTotalPrice() throws CommerceException {
		Cart cart = getCortexCart();
		return cart.getSubTotal(getUserLocale()).getAmount().toString();
	}

	/**
	 * Returns cart pre tax price.
	 *
	 * @return String Cart pre tax price
	 * @throws CommerceException commerceException
	 * @deprecated Use {@link #getCartPrice(Predicate)} instead
	 */
	@Deprecated
	public String getCartPreTaxPrice() throws CommerceException {
		//super passes a price filter with null argument
		return getCartPrice(new PriceFilter("PRE_TAX"));
	}

	// We override the getCartPriceInfo method to prevent use of the cached
	// prices field.
	@Override
	public List<PriceInfo> getCartPriceInfo(final Predicate filter) {
		// NOTE: We explicitly load the cart here until we have completed
		// decoupling the loading of price information from the loading of the cart.
		getCortexCart();

		if (filter != null) {
			final ArrayList<PriceInfo> filteredPrices = new ArrayList<>();
			CollectionUtils.select(prices, filter, filteredPrices);
			return filteredPrices;
		}
		return prices;
	}

	/**
	 * Returns cart price.
	 *
	 * @param filter the filter Predicate
	 * @return String cart price
	 * @throws CommerceException commerceException
	 */
	public String getCartPrice(final Predicate filter) throws CommerceException {
		final List<PriceInfo> prices = getCartPriceInfo(filter);
		if (prices.isEmpty()) {
			return StringUtils.EMPTY;
		}
		return prices.get(0)
				.getFormattedString();
	}

	/**
	 * Returns the total cart line item count.
	 *
	 * @return cart entry count
	 */
	public int getCartEntryCount() {
		Cart cortexCart = getCortexCart();
		return cortexCart.getTotalQuantity();
	}

	/**
	 * Return list of cart entries after refreshing the cart cache.
	 *
	 * @return List<CartEntry> list of cart entry.
	 */
	public List<CartEntry> getCartEntries() {
		Cart cortexCart = getCortexCart();
		return Lists.newArrayList(cortexCart.getLineItems());
	}

	/**
	 * Encapsulate access to the cart so that we can keep the prices data structure here consistent.
	 *
	 * @return Cart - The cart data.
	 */
	// TODO: Determine if we can break the dependency on super.prices / setPrice here.
	private Cart getCortexCart() {
		CartService cartService = cortexSdkServiceFactory.getCartService();
		Cart cart = cartService.getCortexCart(getUserLocale());

		for (PriceInfo priceInfo : cart.getAllCartPriceInfo(getUserLocale())) {
			prices.add(priceInfo);
		}

		return cart;
	}

	/**
	 * Returns shipping cost.
	 *
	 * @param method String
	 * @return BigDecimal
	 */
	@Override
	protected BigDecimal getShipping(final String method) {
		return BigDecimal.ZERO;
	}

	/**
	 * Return tokenize payment info as faux-payment-token.
	 *
	 * @param paymentDetails Map<String, String>
	 * @return tokenize payment info
	 * @throws CommerceException commerceException
	 */
	@Override
	protected String tokenizePaymentInfo(final Map<String, String> paymentDetails) throws CommerceException {

		// This is only a stub implementation for the Geometrixx-Outdoors demo
		// site, for which there is no real payment processing (or payment info
		// tokenization).
		return "faux-payment-token";
	}

	/**
	 * Logout action to log out the user from Cortex and also invoke the OOTB logout functionality.
	 *
	 * @throws CommerceException CommerceException
	 */
	@Override
	public void logout() throws CommerceException {
		cortexSdkServiceFactory.getLogoutService().logout();
	}

	/**
	 * Add cart entry by calling add to cart action.
	 *
	 * @param product  Product
	 * @param quantity the number of products to add in cart
	 * @throws CommerceException commerceException
	 */
	public void addCartEntry(final Product product, final int quantity) throws CommerceException {
		CartService cartService = cortexSdkServiceFactory.getCartService();
		cartService.addCartEntry(product, quantity);
		cortexSdkServiceFactory.getOrderService().invalidateOrder();
	}

	/**
	 * Modifies the cart by calling update cart action.
	 *
	 * @param entryNumber the index of product in cart to be modified
	 * @param quantity    the number of products to modify in cart
	 * @throws CommerceException commerceException
	 */
	public void modifyCartEntry(final int entryNumber, final int quantity) throws CommerceException {
		cortexSdkServiceFactory.getCartService().modifyCartEntry(entryNumber, quantity, userLocale);
		cortexSdkServiceFactory.getOrderService().invalidateOrder();
	}

	/**
	 * Delete cart by calling remove cart action.
	 *
	 * @param entryNumber the index of product in cart to be deleted
	 * @throws CommerceException commerceException
	 */
	public void deleteCartEntry(final int entryNumber) throws CommerceException {
		cortexSdkServiceFactory.getCartService().deleteCartEntry(entryNumber, userLocale);
		cortexSdkServiceFactory.getOrderService().invalidateOrder();
	}

	/**
	 * Returns price for a given sku & filters.
	 *
	 * @param product Product
	 * @param filter  the filter Predicate
	 * @return price of a product
	 * @throws CommerceException the commerce exception
	 */
	public List<PriceInfo> getProductPriceInfo(final Product product, final Predicate filter) throws CommerceException {
		ProductService productService = cortexSdkServiceFactory.getProductService();
		return productService.getProductPrice(product, locale);
	}


	/**
	 * Returns order after refreshing cart cache.
	 *
	 * @return Map&lt;String, Object>
	 * @throws CommerceException commerceException
	 */
	public Map<String, Object> getOrder() throws CommerceException {
		OrderService orderService = cortexSdkServiceFactory.getOrderService();

		Map<String, Object> orderDetails = Maps.newHashMap();
		orderDetails.putAll(orderService.getCortexOrderProperties(getUserLocale()));

		populateUserEmail(request, orderDetails);

		prices.addAll(orderService.getOrderPrices(getUserLocale()));

		return orderDetails;
	}

	private void populateUserEmail(final SlingHttpServletRequest request, final Map<String, Object> orderDetails) {
		String emailAddress = null;
		final UserProperties userProperties = request.adaptTo(UserProperties.class);
		if (!UserPropertiesUtil.isAnonymous(userProperties)) {
			try {
				emailAddress = userProperties.getProperty(UserProperties.EMAIL);
			} catch (final RepositoryException repositoryException) {
				LOG.error("Exception occured while fetching the user email id", repositoryException);
			}
		}

		if (StringUtils.isNotBlank(emailAddress)) {
			orderDetails.put(CommerceConstants.BILLING_ADDRESS_PREDICATE + SEPARATOR + "email", emailAddress);
			orderDetails.put(CommerceConstants.SHIPPING_ADDRESS_PREDICATE + SEPARATOR + "email", emailAddress);
		}
	}

	/**
	 * Updates order details.
	 *
	 * @param delta Map&lt;String, String&gt; of order related information
	 * @throws CommerceException commerceException
	 */
	public void updateOrderDetails(final Map<String, String> delta) throws CommerceException {
		// The cart must be loaded before updating the order.
		getCortexCart();
		updateOrder(new HashMap<String, Object>(delta));
	}

	/**
	 * Calls Update order action.
	 *
	 * @param delta Map<String, Object> of order related information
	 * @throws CommerceException commerceException
	 */
	public void updateOrder(final Map<String, Object> delta) throws CommerceException {

		// Update order is called from multiple pages, making it a bit of a beast to manage.
		// From the address page, we want to update address information, but not shipping information.
		// From the shipping page, we want to update shipping information, but not address information.
		// It's all very fragile, so please be careful out there.

		OrderConfigurationService orderConfigurationService = cortexSdkServiceFactory.getOrderConfigurationService();
		orderConfigurationService.updateOrderEmailIfGuest(request, delta);

		if (!delta.containsKey(CommerceConstants.SHIPPING_OPTION)) {
			orderConfigurationService.updateAddresses(delta);
		}

		if (delta.containsKey(CommerceConstants.SHIPPING_OPTION)) {
			orderConfigurationService.updateShippingOption(delta);
		}
		cortexSdkServiceFactory.getCartService().invalidateCart();
		cortexSdkServiceFactory.getOrderService().invalidateOrder();
	}

	/**
	 * Calling place order action, use {@link #placeOrder} instead.
	 *
	 * @param delta Map&lt;String, String&gt;
	 * @throws CommerceException commerceException
	 */
	public void submitOrder(final Map<String, String> delta) throws CommerceException {
		placeOrder(new HashMap<String, Object>(delta));
	}

	/**
	 * Place order action to place the order in cortex.
	 *
	 * @param delta Map<String, Object> of order related information
	 * @throws CommerceException commerceException
	 */
	public void placeOrder(final Map<String, Object> delta) throws CommerceException {
		OrderService orderService = cortexSdkServiceFactory.getOrderService();
		String purchaseNumber = orderService.placeCortexOrder(delta);
		this.orderDetails.put(this.PN_ORDER_ID, purchaseNumber);
	}

	/**
	 * Returns order details.
	 *
	 * @return Map&lt;String, String&gt;
	 * @throws CommerceException commerceException
	 */
	public Map<String, String> getOrderDetails() throws CommerceException {
		return new HashMap<>();
	}

	/**
	 * Return list of PromotionInfo for applied promotions.
	 *
	 * @return List<PromotionInfo>
	 * @throws CommerceException commerceException
	 */
	public List<PromotionInfo> getPromotions() throws CommerceException {
		final List<PromotionInfo> promotionInfos = new ArrayList<>();
		Cart cart = getCortexCart();

		promotionInfos.addAll(cart.getLineItemPromotions());
		promotionInfos.addAll(cart.getAppliedPromotions());

		if (cart.getTotalQuantity() > 0) {
			// NOTE: This call fails if the cart is empty.
			OrderService orderService = cortexSdkServiceFactory.getOrderService();
			promotionInfos.addAll(orderService.getCortexOrderShippingPromotions(getUserLocale()));
		}

		return promotionInfos;
	}

	/**
	 * Returns whether client side promotion resolution is supported.
	 *
	 * @return boolean
	 */
	public boolean supportsClientsidePromotionResolution() {
		return false;
	}

	/**
	 * Returns list of voucher info.
	 *
	 * @return List<VoucherInfo>
	 * @throws CommerceException commerceException
	 */
	public List<VoucherInfo> getVoucherInfos() throws CommerceException {
		return cortexSdkServiceFactory.getPromotionService().getVoucherInfos(getUserLocale());
	}

	/**
	 * Removes the voucher for given voucher code.
	 *
	 * @param voucherCode code of voucher
	 * @throws CommerceException commerceException
	 */
	public void removeVoucher(final String voucherCode) throws CommerceException {
		if (StringUtils.isBlank(voucherCode)) {
			throw new CommerceException("Invalid voucher code: " + voucherCode);
		}

		PromotionService promotionService = cortexSdkServiceFactory.getPromotionService();
		boolean successfullyDeletedCoupon = promotionService.deleteCoupon(voucherCode, getUserLocale());

		if (successfullyDeletedCoupon) {
			cortexSdkServiceFactory.getCartService().invalidateCart();
			cortexSdkServiceFactory.getOrderService().invalidateOrder();
		} else {
			throw new CommerceException("Failed to remove voucher: " + voucherCode);
		}
	}

	/**
	 * Returns PlacedOrderResult.
	 *
	 * @param predicate  the filter
	 * @param pageNumber the page number
	 * @param pageSize   the size of page
	 * @param sortId     the sorting criteria
	 * @return PlacedOrderResult
	 * @throws CommerceException commerceException
	 */
	@Override
	public PlacedOrderResult getPlacedOrders(final String predicate, final int pageNumber, final int pageSize, final String sortId)
			throws CommerceException {

		PurchaseService purchaseService = cortexSdkServiceFactory.getPurchaseService();
		List<PlacedOrder> placedOrderList = purchaseService.getPlacedOrders(this, predicate);
		return new PlacedOrderResult(placedOrderList, null, null);
	}

	/**
	 * Return Order on the basis of order id.
	 *
	 * @param orderId the order id
	 * @return PlacedOrder
	 * @throws CommerceException commerceException
	 */
	public PlacedOrder getPlacedOrder(final String orderId) throws CommerceException {
		PurchaseService purchaseService = cortexSdkServiceFactory.getPurchaseService();
		return purchaseService.getPlacedOrder(orderId, this);
	}

	/**
	 * Find product on the basis of product name.
	 *
	 * @param res  Resource
	 * @param name String the product name
	 * @return Product
	 * @throws CommerceException CommerceException
	 */
	public Product getProductByName(final Resource res, final String name) throws CommerceException {
		ElasticPathProductCapability productCapability = new ElasticPathProductCapability
				.Builder().setResource(resource).build();
		return productCapability.getProductByName(res, name);
	}

	/**
	 * Add voucher to the order.
	 *
	 * @param voucherCode - Voucher code
	 * @throws CommerceException commerceException
	 */
	public void addVoucher(final String voucherCode) throws CommerceException {
		if (StringUtils.isBlank(voucherCode)) {
			throw new CommerceException("Invalid voucher code: " + voucherCode);
		}

		PromotionService promotionService = cortexSdkServiceFactory.getPromotionService();
		boolean successfullyAddedCoupon = promotionService.addCoupon(voucherCode, getUserLocale());

		if (successfullyAddedCoupon) {
			cortexSdkServiceFactory.getCartService().invalidateCart();
			cortexSdkServiceFactory.getOrderService().invalidateOrder();
		} else {
			throw new CommerceException("Coupon code is not valid");
		}
	}

	/**
	 * Returns list of shipping methods from cortex.
	 *
	 * @return list of shipping methods
	 * @throws CommerceException CommerceException
	 */
	@Override
	public List<ShippingMethod> getAvailableShippingMethods() throws CommerceException {
		OrderService orderService = cortexSdkServiceFactory.getOrderService();
		return orderService.getCortexOrderAvailableShippingMethods(getUserLocale());
	}

	/**
	 * Returns list of supported available countries from geographies resource.
	 *
	 * @return List of available countries
	 * @throws CommerceException CommerceException
	 */
	public List<String> getAvailableCountries() throws CommerceException {
		GeographyService geographyService = cortexSdkServiceFactory.getGeographyService();
		return geographyService.getAvailableCountries();
	}

	/**
	 * Return available regions supported by Cortex. Available region map will be created once available list of countries will be populated. If
	 * getAvailableCounties() is already being called then no need to call geographies resource again. If geographies is null then it will again call
	 * geographies resource to load regions.
	 *
	 * @return List of available regions
	 * @throws CommerceException CommerceException
	 */
	public Map<String, List<String>> getAvailableRegions() throws CommerceException {
		GeographyService geographyService = cortexSdkServiceFactory.getGeographyService();
		return geographyService.getRegions();
	}

	/**
	 * Returns resource.
	 *
	 * @return Resource
	 */
	public Resource getResource() {
		return this.resource;
	}

	/**
	 * Returns commerce service.
	 *
	 * @return CommerceService
	 */
	public ElasticPathCommerceService getService() {
		return (ElasticPathCommerceService) this.commerceService;
	}

	/**
	 * Set User locale.
	 *
	 * @param locale Locale
	 */
	@Override
	public void setUserLocale(final Locale locale) {
		this.userLocale = locale;
	}
}