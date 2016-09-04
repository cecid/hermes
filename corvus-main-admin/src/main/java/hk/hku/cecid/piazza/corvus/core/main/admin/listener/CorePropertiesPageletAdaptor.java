/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Apache License Version 2.0 [1]
 * 
 * [1] http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package hk.hku.cecid.piazza.corvus.core.main.admin.listener;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.util.PropertySheet;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.commons.util.StringUtilities;
import hk.hku.cecid.piazza.corvus.core.main.admin.AdminMainProcessor;
import hk.hku.cecid.piazza.corvus.core.main.admin.hc.module.SchedulerTask;
import hk.hku.cecid.piazza.corvus.core.main.admin.hc.util.AdminProperties;
import hk.hku.cecid.piazza.corvus.core.main.admin.hc.util.AdminPropertiesException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;

/**
 * CorePropertiesPageletAdaptor is a properties pagelet adaptor which provides
 * an admin function of the core system properties.
 * 
 * @author Hugo Y. K. Lam
 * 
 */
public class CorePropertiesPageletAdaptor extends PropertiesPageletAdaptor {

	private static final String REQ_PARAM_PROPERTY_HC = "hc:";

	protected Source getCenterSource(HttpServletRequest request) {
		PropertyTree dom = new PropertyTree();
		dom.setProperty("/properties", "");
		dom = getPropertiesForDisplayAndUpdate(request, dom);
		dom.setProperty("page_type", "coreProps");
		dom = addHouseCleaning(request, dom);
		return dom.getSource();
	}

	/**
	 * Add the housecleaning data to the XSL tranformation.
	 * 
	 * @param request
	 * @param dom
	 * @return PropertyTree
	 */
	protected PropertyTree addHouseCleaning(HttpServletRequest request,
			PropertyTree dom) {

		AdminProperties props = new AdminProperties(
				(PropertyTree) getHCProperties());

		boolean required = false;
		dom.setProperty("required", "true");

		String action = request.getParameter(REQ_PARAM_ACTION);
		if (request.getMethod().equalsIgnoreCase("post")) {
			if ("update_hc".equalsIgnoreCase(action)) {
				/**
				 * check if the email is set
				 */
				if (!(StringUtilities.isEmptyString(request
						.getParameter(REQ_PARAM_PROPERTY_HC + "email")))) {
					props.setEmail(StringUtilities.trim(request
							.getParameter(REQ_PARAM_PROPERTY_HC + "email")));
					/**
					 * check if it is a valid email.
					 */
					if (!validEmail(request.getParameter(REQ_PARAM_PROPERTY_HC
							+ "email"))) {
						required = false;
						request.setAttribute(ATTR_MESSAGE,
								"Invalid email address.");
					} else {
						/**
						 * check if the smtp is set.
						 */
						if (StringUtilities.isEmptyString(request
								.getParameter(REQ_PARAM_PROPERTY_HC + "smtp"))) {
							request.setAttribute(ATTR_MESSAGE,
									"Please specify an SMTP server");
							required = false;
							dom.setProperty("required", "false");
						} else {
							required = true;
							dom.setProperty("required", "true");
						}
					}
				} else {
					required = true;
					dom.setProperty("required", "true");
				}
				if (!StringUtilities.isEmptyString(request
						.getParameter(REQ_PARAM_PROPERTY_HC + "smtp"))
						&& !isValidDomain(request
								.getParameter(REQ_PARAM_PROPERTY_HC + "smtp"))) {
					if (required == true) {
						required = false;
						request.setAttribute(ATTR_MESSAGE,
								"Invalid SMTP server.");
					}
				}
				if (request.getParameter(REQ_PARAM_PROPERTY_HC + "on").equals(
						"true")) {
					Calendar c = GregorianCalendar.getInstance();
					try {
						c.setTime(new SimpleDateFormat(
								SchedulerTask.TIME_FORMAT).parse(props
								.getTime()));
						props.setNextRun(new SimpleDateFormat(
								SchedulerTask.DATE_FORMAT).format(SchedulerTask
								.getNextRunDateFromNow(props.getDay(), c
										.get(Calendar.HOUR_OF_DAY), c
										.get(Calendar.MINUTE), c
										.get(Calendar.SECOND))));
					} catch (AdminPropertiesException e) {
						request.setAttribute(ATTR_MESSAGE,
								"Error retrieving property.");
					} catch (Exception e) {
						request.setAttribute(ATTR_MESSAGE,
								"Unable to set next run time: " + e);
					}
				}
				Enumeration names = request.getParameterNames();
				while (names.hasMoreElements()) {
					String name = names.nextElement().toString();
					if (name.startsWith(REQ_PARAM_PROPERTY_HC)) {
						props.setElementHC(name.substring(REQ_PARAM_PROPERTY_HC
								.length()), request.getParameter(name));
					}
				}
				if (required) {
					try {
						props.write();
						request.setAttribute(ATTR_MESSAGE,
								"House Cleaning settings updated successfully");
					} catch (Exception e) {
						request.setAttribute(ATTR_MESSAGE,
								"Unable to update properties: " + e);
					}
				}
			}
		}

		String[] propArray = { AdminProperties.ON, AdminProperties.EMAIL,
				AdminProperties.SMTP, AdminProperties.USERNAME,
				AdminProperties.PASSWORD };
		try {
			for (int index = 0; index < propArray.length; index++) {
				dom.setProperty("hc/" + propArray[index], props
						.getElementHC(propArray[index]));
			}
			String[] datesArray = { AdminProperties.LAST_RUN,
					AdminProperties.NEXT_RUN };
			for (int jndex = 0; jndex < datesArray.length; jndex++) {
				if (!props.getElementHC(datesArray[jndex]).equals("")
						&& props.getElementHC(datesArray[jndex]) != null) {
					dom
							.setProperty(
									"hc/" + datesArray[jndex],
									(new SimpleDateFormat(
											SchedulerTask.DATE_FORMAT))
											.parse(
													props
															.getElementHC(datesArray[jndex]))
											.toString());
				}
			}
			return dom;
		} catch (Exception e) {
			request.setAttribute(ATTR_MESSAGE,
					"Unable to retrieve properties: " + e);
			return dom;
		}
	}

	private PropertySheet getHCProperties() {
		try {
			AdminMainProcessor.core.properties.load();
		} catch (Exception e) {
			AdminMainProcessor.core.log.error("Unable to load the properties.");
		}
		return AdminMainProcessor.core.properties;
	}

	/**
	 * Checks if the String s is in a valid email format using regex.
	 * 
	 * @param s
	 * @return
	 */
	private boolean validEmail(String s) {
		int pos_at = posOfAt(s);
		String username;
		String domain;

		if (pos_at == 0) {
			return false;
		} else {
			username = s.substring(0, pos_at - 1);
			if (!isValidFormat(username, "[\\w\\.\\-]")) {
				return false;
			} else {
				domain = s.substring(pos_at + 1);
				if (!isValidFormat(domain, "[a-zA-Z0-9\\.\\-]")) {
					return false;
				} else {
					if (!isValidDomain(domain)) {
						return false;
					} else {
						return true;
					}
				}
			}
		}
	}

	/**
	 * Checks if the characters in the String s conform to the regex specified.
	 * 
	 * @param s
	 * @param regex
	 * @return
	 */
	private boolean isValidFormat(String s, String regex) {
		char[] array = s.toCharArray();
		boolean matches = true;
		for (int i = 0; i < array.length; i++) {
			if (!Pattern.matches(regex, String.valueOf(array[i]))) {
				matches = false;
				break;
			}
		}
		return matches;
	}

	/**
	 * Checks to see if the String s is a valid domain. A valid domain is
	 * considered to be where there is at least 1 decimal and the sections
	 * devided by decimals are alphanumeric or dashes but the dashes can only be
	 * in the middle. The last segment should be all alphabets.
	 * 
	 * @param s
	 * @return
	 */
	private boolean isValidDomain(String s) {
		int dot_count = 0;
		char[] array = s.toCharArray();

		for (int i = 0; i < array.length; i++) {
			if (array[i] == '.') {
				dot_count++;
			}
		}

		boolean validString = true;

		String[] blocks = s.split("\\.");
		if (dot_count != blocks.length - 1) {
			return false;
		}
		for (int j = 0; j < blocks.length; j++) {
			char[] temp = blocks[j].toCharArray();

			for (int k = 0; k < temp.length; k++) {
				if (j == blocks.length - 1) {
					if (!String.valueOf(temp[k]).matches("[a-zA-Z]")) {
						validString = false;
					}
				} else {
					if (k == 0 || k == temp.length - 1) {
						if (!String.valueOf(temp[k]).matches("[a-zA-Z0-9]")) {
							validString = false;
						} else {
							if (!String.valueOf(temp[k]).matches(
									"[a-zA-Z0-9\\-]")) {
								validString = false;
							}
						}
					}
				}
			}
		}

		if (dot_count != 0 && validString) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return position of the @ symbol or 0 if none or more than 1
	 * @param address
	 * @return
	 */
	private int posOfAt(String s) {
		char[] address = s.toCharArray();
		int at_count = 0;
		int pos = 0;
		for (int i = 0; i < address.length; i++) {
			if (address[i] == '@') {
				at_count++;
				pos = i;
			}
		}
		if (at_count == 1) {
			return pos;
		} else {
			return 0;
		}
	}

	/**
	 * Gets the core system properties.
	 * 
	 * @return the core system properties.
	 * @see hk.hku.cecid.piazza.corvus.core.main.admin.listener.PropertiesPageletAdaptor#getProperties()
	 */
	protected PropertySheet getProperties() {
		return Sys.main.properties;
	}
}