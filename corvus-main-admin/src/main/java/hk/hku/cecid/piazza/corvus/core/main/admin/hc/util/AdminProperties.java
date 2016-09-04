package hk.hku.cecid.piazza.corvus.core.main.admin.hc.util;

import java.net.URL;

import hk.hku.cecid.piazza.commons.module.ComponentException;
import hk.hku.cecid.piazza.commons.util.PropertyTree;

/**
 * A class to wrap the admin.main properties file for easier handling.
 * 
 * @author Joel Matsumoto
 * 
 */
public class AdminProperties {

	/**
	 * The base element for the admin properties file.
	 */
	public static final String ADMIN = "admin";

	/**
	 * Properties within the admin element
	 */
	public static final String HOUSECLEANING = "housecleaning";

	/**
	 * Properties within the housecleaning element.
	 */
	public static final String HCPATH = ADMIN + "/" + HOUSECLEANING + "/";

	public static final String ON = "on";

	public static final String DAY = "day";

	public static final String TIME = "time";

	public static final String CUTOFF_BEFORE = "cutoff";

	public static final String EMAIL = "email";

	public static final String SMTP = "smtp";

	public static final String PORT = "port";

	public static final String USERNAME = "username";

	public static final String PASSWORD = "password";

	public static final String STATUS = "status";

	public static final String LAST_RUN = "lastrun";

	public static final String NEXT_RUN = "nextrun";

	public static final String REASON_FAILED = "reason";

	private PropertyTree props;

	public AdminProperties(PropertyTree tree) {
		props = tree;
	}

	/**
	 * Return a boolean depending on if the value is set to ON/OFF or return
	 * false if none set/null/invalid.
	 * 
	 * @return boolean
	 */
	public boolean isOn() {
		return Boolean.valueOf(props.getProperty(HCPATH + ON)).booleanValue();
	}

	/**
	 * Set the property 'on'.
	 * 
	 * @param b
	 */
	public void setOn(boolean b) {
		props.setProperty(HCPATH + ON, Boolean.toString(b));
	}

	/**
	 * Return the 'day' property or throw an exception if an invalid integer
	 * 
	 * @return int
	 * @throws AdminPropertiesException
	 */
	public int getDay() throws AdminPropertiesException {
		try {
			return Integer.parseInt(props.getProperty(HCPATH + DAY));
		} catch (NumberFormatException e) {
			throw new AdminPropertiesException("Invalid property for 'day'.", e);
		}
	}

	/**
	 * Set the property 'day'. 1-7 Sun-Sat
	 * 
	 * @param day
	 */
	public void setDay(int day) {
		props.setProperty(HCPATH + DAY, Integer.toString(day));
	}

	/**
	 * Return the 'time' property.
	 * 
	 * @return String
	 * @throws AdminPropertiesException
	 */
	public String getTime() throws AdminPropertiesException {
		if (props.getProperty(HCPATH + TIME) == null) {
			throw new AdminPropertiesException("Null value for 'time'.");
		}
		return props.getProperty(HCPATH + TIME);
	}

	/**
	 * Set the property 'time'.
	 * 
	 * @param time
	 */
	public void setTime(String time) {
		if (time == null) {
			time = "";
		}
		props.setProperty(HCPATH + TIME, time);
	}

	/**
	 * Return the 'cutoff' property if set, else if null or invalid, throw an
	 * exception.
	 * 
	 * @return int
	 * @throws AdminPropertiesException
	 */
	public int getCutoff() throws AdminPropertiesException {
		try {
			return Integer.parseInt(props.getProperty(HCPATH + CUTOFF_BEFORE));
		} catch (NumberFormatException e) {
			throw new AdminPropertiesException(
					"Invalid property for 'cutoff'.", e);
		}
	}

	/**
	 * Set the property 'cutoff'.
	 * 
	 * @param cutoff
	 */
	public void setCutoff(int cutoff) {
		props.setProperty(HCPATH + CUTOFF_BEFORE, Integer.toString(cutoff));
	}

	/**
	 * Return the 'email' property if set or return an empty String. If null
	 * throw an exception.
	 * 
	 * @return String
	 * @throws AdminPropertiesException
	 */
	public String getEmail() throws AdminPropertiesException {
		if (props.getProperty(HCPATH + EMAIL) == null) {
			throw new AdminPropertiesException("Null value for 'email'.");
		}
		return props.getProperty(HCPATH + EMAIL);
	}

	/**
	 * Set the property 'email'.
	 * 
	 * @param email
	 */
	public void setEmail(String email) {
		if (email == null) {
			email = "";
		}
		props.setProperty(HCPATH + EMAIL, email);
	}

	/**
	 * Return the 'smtp' property if set or return an empty String. If null,
	 * throw an exception.
	 * 
	 * @return String
	 * @throws AdminPropertiesException
	 */
	public String getSmtp() throws AdminPropertiesException {
		if (props.getProperty(HCPATH + SMTP) == null) {
			throw new AdminPropertiesException("Null value for 'smtp'.");
		}
		return props.getProperty(HCPATH + SMTP);
	}

	/**
	 * Set the property 'smtp'.
	 * 
	 * @param smtp
	 */
	public void setSmtp(String smtp) {
		if (smtp == null) {
			smtp = "";
		}
		props.setProperty(HCPATH + SMTP, smtp);
	}

	/**
	 * Return the 'port' property if set or throw an exception if null/invalid.
	 * If the value is not set, will return the deafult, 25;
	 * 
	 * @return int
	 * @throws AdminPropertiesException
	 */
	public int getPort() throws AdminPropertiesException {
		try {
			if (props.getProperties(HCPATH + PORT).equals("")) {
				return 25;
			}
			return Integer.parseInt(props.getProperty(HCPATH + PORT));
		} catch (NumberFormatException e) {
			throw new AdminPropertiesException("Invalid value for 'port'.");
		}
	}

	/**
	 * Set the property 'port'.
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		props.setProperty(HCPATH + PORT, Integer.toString(port));
	}

	/**
	 * Return the 'username' property if set, else return an empty String. If
	 * null, throw an exception.
	 * 
	 * @return String
	 * @throws AdminPropertiesException
	 */
	public String getUsername() throws AdminPropertiesException {
		if (props.getProperty(HCPATH + USERNAME) == null) {
			throw new AdminPropertiesException("Null value for 'username'.");
		}
		return props.getProperty(HCPATH + USERNAME);
	}

	/**
	 * Set the property 'username'.
	 * 
	 * @param username
	 */
	public void setUsername(String username) {
		props.setProperty(HCPATH + USERNAME, username);
	}

	// TODO: password encryption and dycryption.
	public String getPassword() {
		return props.getProperty(HCPATH + PASSWORD);
	}

	public void setPassword(String password) {
		props.setProperty(HCPATH + PASSWORD, password);
	}

	/**
	 * Return the 'status' property if set, else return an empty String. If
	 * null, throw an exception.
	 * 
	 * @return String
	 * @throws AdminPropertiesException
	 */
	public String getStatus() throws AdminPropertiesException {
		if (props.getProperty(HCPATH + STATUS) == null) {
			throw new AdminPropertiesException("Null value for 'status'.");
		}
		return props.getProperty(HCPATH + STATUS);
	}

	/**
	 * Set the property 'status'.
	 * 
	 * @param status
	 */
	public void setStatus(String status) {
		if (status == null) {
			status = "";
		}
		props.setProperty(HCPATH + STATUS, status);
	}

	/**
	 * Return the 'lastrun' property if set, else return an empty String. If
	 * null, throw an exception.
	 * 
	 * @return String
	 * @throws AdminPropertiesException
	 */
	public String getLastRun() throws AdminPropertiesException {
		if (props.getProperty(HCPATH + LAST_RUN) == null) {
			throw new AdminPropertiesException("Null value for 'lastrun'.");
		}
		return props.getProperty(HCPATH + LAST_RUN);
	}

	/**
	 * Set the property 'lastrun'.
	 * 
	 * @param lastrun
	 */
	public void setLastRun(String lastrun) {
		if (lastrun == null) {
			lastrun = "";
		}
		props.setProperty(HCPATH + LAST_RUN, lastrun);
	}

	/**
	 * Return the 'nextrun' property if set, else return an empty String. If
	 * null, throw an exception.
	 * 
	 * @return String
	 * @throws AdminPropertiesException
	 */
	public String getNextRun() throws AdminPropertiesException {
		if (props.getProperty(HCPATH + NEXT_RUN) == null) {
			throw new AdminPropertiesException("Null value for 'nextrun'.");
		}
		return props.getProperty(HCPATH + NEXT_RUN);
	}

	/**
	 * Set the property 'nextrun'.
	 * 
	 * @param nextrun
	 */
	public void setNextRun(String nextrun) {
		if (nextrun == null) {
			nextrun = "";
		}
		props.setProperty(HCPATH + NEXT_RUN, nextrun);
	}

	/**
	 * Return the 'reason' property if set, else return an empty String. If
	 * null, throw an exception.
	 * 
	 * @return String
	 * @throws AdminPropertiesException
	 */
	public String getReason() throws AdminPropertiesException {
		if (props.getProperty(HCPATH + REASON_FAILED) == null) {
			throw new AdminPropertiesException("Null value for 'reason'.");
		}
		return props.getProperty(HCPATH + REASON_FAILED);
	}

	/**
	 * Set the property 'reason'.
	 * 
	 * @param reason
	 */
	public void setReason(String reason) {
		if (reason == null) {
			reason = "";
		}
		props.setProperty(HCPATH + REASON_FAILED, reason);
	}
	
	public void setElementHC(String name, String value){
		props.setProperty(HCPATH + "/" + name, value);
	}

	public String getElementHC(String name) throws AdminPropertiesException{
		if(props.getProperty(HCPATH + "/" + name) == null){
			throw new AdminPropertiesException("Null value for '" + name + "'.");
		}
		return props.getProperty(HCPATH + "/" + name);
	}
	
	/**
	 * Writes the property tree to url asscoaited with the component
	 * 
	 * @throws AdminPropertiesException
	 */
	public void write() throws AdminPropertiesException {
		try {
			props.store();
		} catch (ComponentException e) {
			throw new AdminPropertiesException(
					"Error occured while writing to " + props.getURL(), e);
		}
	}

	/**
	 * Writes to the specified url.
	 * 
	 * @param u
	 * @throws AdminPropertiesException
	 */
	public void write(URL u) throws AdminPropertiesException {
		try {
			props.store(u);
		} catch (ComponentException e) {
			throw new AdminPropertiesException(
					"Error occured while writing to " + u, e);
		}
	}

}
