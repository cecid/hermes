/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Apache License Version 2.0 [1]
 * 
 * [1] http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package hk.hku.cecid.piazza.corvus.core.main.admin.listener;

import hk.hku.cecid.piazza.commons.module.PersistentComponent;
import hk.hku.cecid.piazza.commons.util.ArrayUtilities;
import hk.hku.cecid.piazza.commons.util.PropertyMap;
import hk.hku.cecid.piazza.commons.util.PropertySheet;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor;

import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;

/**
 * PropertiesPageletAdaptor is an admin pagelet adaptor which provides an admin
 * function of the JVM properties.
 * 
 * Subclasses can override the getProperties() method and return a compatible
 * properties object for sharing the same admin function.
 * 
 * @author Hugo Y. K. Lam
 * 
 */
public class PropertiesPageletAdaptor extends AdminPageletAdaptor {

	private static final String REQ_PARAM_PROPERTY = "property:";

	/**
	 * Generates the transformation source of the properties.
	 * 
	 * @see hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor#getCenterSource(javax.servlet.http.HttpServletRequest)
	 */
	protected Source getCenterSource(HttpServletRequest request) {
		PropertyTree dom = new PropertyTree();
		dom.setProperty("/properties", "");
		return getPropertiesForDisplayAndUpdate(request, dom).getSource();

	}

	protected PropertyTree getPropertiesForDisplayAndUpdate(HttpServletRequest request,
			PropertyTree dom) {

		PropertySheet props = getProperties();

		String action = request.getParameter(REQ_PARAM_ACTION);
		if (request.getMethod().equalsIgnoreCase("post")) {
			if ("update".equalsIgnoreCase(action)) {
				Enumeration names = request.getParameterNames();
				while (names.hasMoreElements()) {
					String name = names.nextElement().toString();
					if (name.startsWith(REQ_PARAM_PROPERTY)) {
						props.setProperty(name.substring(REQ_PARAM_PROPERTY
								.length()), request.getParameter(name));
					}
				}
				try {
					if (props instanceof PersistentComponent) {
						if (((PersistentComponent) props).getURL() != null) {
							props.store();
						}
					}
					request.setAttribute(ATTR_MESSAGE,
							"Properties updated successfully");
				} catch (Exception e) {
					request.setAttribute(ATTR_MESSAGE,
							"Unable to update properties: " + e);
				}
			}
		}

		String[] keys = (String[]) ArrayUtilities.toArray(
				props.propertyNames(), new String[] {});
		Arrays.sort(keys);

		for (int i = 0, pi = 0; i < keys.length; i++) {
			String value = props.getProperty(keys[i], "");
			if (value.indexOf('\n') == -1 && value.indexOf('\r') == -1) {
				pi++;
				dom.setProperty("property[" + pi + "]/name", keys[i]);
				dom.setProperty("property[" + pi + "]/value", props
						.getProperty(keys[i]));
			}
		}

		return dom;

	}

	/**
	 * Gets the properties that this adaptor administrates.
	 * 
	 * @return the JVM properties.
	 */
	protected PropertySheet getProperties() {
		return new PropertyMap(System.getProperties());
	}
}
