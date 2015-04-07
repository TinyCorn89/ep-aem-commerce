/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.aem.commerce;

import com.adobe.cq.commerce.api.CommerceServiceFactory;
import com.adobe.granite.xss.XSSAPI;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.io.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.elasticpath.commerce.config.ElasticPathGlobalConstants.EP_VENDOR;

/**
 * EpCommerceProvidersServlet provides the path for EP commerce provider and is configurable from OSGI also.
 */
@Component
@Service
@Properties({ @org.apache.felix.scr.annotations.Property(name = "service.description", value = { "Enumerates the registered commerce providers" }),
		@org.apache.felix.scr.annotations.Property(name = "sling.servlet.paths", value = { "/libs/commerce/epproviders" }),
		@org.apache.felix.scr.annotations.Property(name = "sling.servlet.methods", value = { "GET" }) })
public class EpCommerceProvidersServlet extends SlingAllMethodsServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6922250507034975021L;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(EpCommerceProvidersServlet.class);
	
	/** The Constant ERROR_CODE. */
	private static final int ERROR_CODE = 500;

	/** The xss api. */
	@Reference
	private XSSAPI xssAPI;

	/** The factories. */
	@Reference(referenceInterface = CommerceServiceFactory.class, bind = "bindFactory",
			unbind = "unbindFactory", cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	private final  Map<String, Map> factories = new HashMap();

	/**
	 * Used to Bind the factory.
	 * 
	 * @param factory the factory
	 * @param properties the properties
	 */
	protected void bindFactory(final CommerceServiceFactory factory, final Map<?, ?> properties) {
		String provider = (String) properties.get("commerceProvider");
		this.factories.put(provider, properties);
	}

	/**
	 * Used to Unbind factory.
	 * 
	 * @param factory the factory
	 * @param properties the properties
	 */
	protected void unbindFactory(final CommerceServiceFactory factory, final Map<?, ?> properties) {
		String provider = (String) properties.get("commerceProvider");
		this.factories.remove(provider);
	}

	/**
	 * doGet method for EP commerceproviders servlet.
	 * 
	 * @param request SlingHttpServletRequest
	 * @param response SlingHttpServletResponse
	 * @throws javax.servlet.ServletException ServletException
	 * @throws java.io.IOException IOException
	 */
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
		try {
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");
			response.setHeader("Cache-Control", "no-cache");

			// Creating new JSON writer.
			JSONWriter writer = new JSONWriter(response.getWriter());
			writer.array();
			for (Map propertyMap : this.factories.values()) {
				String value = (String) propertyMap.get("commerceProvider");
				String label = (String) propertyMap.get("commerceProviderLabel");
				String vendor = (String) propertyMap.get("service.vendor");
				if (label == null) {
					label = StringUtils.capitalize(value);
				}
				LOG.debug("provider value {} provider label {} ", value, label);
				if (EP_VENDOR.equals(vendor)) {
					writer.object().key("value").value(this.xssAPI.encodeForHTMLAttr(value)).key("text").value(this.xssAPI.encodeForHTML(label))
							.endObject();
				}
			}

			writer.endArray();
		} catch (final Exception exception) {
			LOG.error("Error while generating JSON list" + exception);
			response.sendError(ERROR_CODE, exception.toString());
		}
	}

	/**
	 * Bind xss api.
	 * 
	 * @param paramXSSAPI the param xssapi
	 */
	protected void bindXssAPI(final XSSAPI paramXSSAPI) {
		this.xssAPI = paramXSSAPI;
	}

	/**
	 * Unbind xss api.
	 * 
	 * @param paramXSSAPI the param xssapi
	 */
	protected void unbindXssAPI(final XSSAPI paramXSSAPI) {
		if (this.xssAPI == paramXSSAPI) {
			this.xssAPI = null;
		}
	}
}