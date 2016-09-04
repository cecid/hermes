package hk.hku.cecid.piazza.commons.mail;
import java.util.Properties;

/**
 * A class wrapping the SMTP Properties.
 * 
 * @author Joel Matsumoto
 * 
 */
public class SmtpMailProperties {

	/**
	 * Properties data structure
	 */
	private Properties props;

	/**
	 * Property constants.
	 */
	private static final String HOST = "mail.smtp.host";

	private static final String USERNAME = "mail.smtp.user";

	private static final String PASSWORD = "mail.smtp.password";

	private static final String PORT = "mail.smtp.port";

	private static final String FROM = "mail.smtp.from";

	public SmtpMailProperties() {
		props = new Properties();
	}

	/**
	 * Set the default host to connect to.
	 * 
	 * @param host
	 */
	public void setHost(String host) {
		props.put(HOST, host);
	}

	/**
	 * Return the host.
	 * 
	 * @return host
	 */
	public String getHost() {
		return props.getProperty(HOST);
	}

	/**
	 * Set the default username to be used
	 * 
	 * @param user
	 */
	public void setUsername(String user) {
		props.put(USERNAME, user);
	}

	/**
	 * Get the default username
	 * 
	 * @return username
	 */
	public String getUsername() {
		return props.getProperty(USERNAME);
	}

	/**
	 * Set the password for servers needing authentication
	 * 
	 * @param pw
	 */
	public void setPassword(String pw) {
		props.put(PASSWORD, pw);
	}

	/**
	 * Get the password
	 * 
	 * @return String
	 */
	public String getPassword() {
		return props.getProperty(PASSWORD);
	}

	/**
	 * Set the port number. Defaults to 25.
	 * 
	 * @param port
	 */
	public void setPort(int port){
		props.put(PORT, Integer.toString(port));
	}

	/**
	 * Get the port number.
	 * 
	 * @return int
	 */
	public int getPort() throws NumberFormatException{
		return Integer.parseInt(props.getProperty(PORT));
	}

	/**
	 * Set the from property. If no from is set, uses msg.getFrom() or
	 * InternetAddress.getLocalAddress().
	 * 
	 * @param from
	 */
	public void setFrom(String from) {
		props.put(FROM, from);
	}

	/**
	 * Get the from property.
	 * 
	 * @return String
	 */
	public String getFrom() {
		return props.getProperty(FROM);
	}

	/**
	 * Add the key/value pair to trhe property structure.
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value) {
		props.put(key, value);
	}

	/**
	 * Get the value of the key.
	 * 
	 * @param key
	 * @return String
	 */
	public String getProperty(String key) {
		return props.getProperty(key);
	}

	/**
	 * Get the properties data structure.
	 * 
	 * @return Properties
	 */
	public Properties getProperties() {
		return props;
	}
}
