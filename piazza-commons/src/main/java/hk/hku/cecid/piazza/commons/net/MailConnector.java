/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.net;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

/**
 * MailConnector is an abstract connector for making connection to mail hosts.
 * 
 * The configuration of mail connector is mainly through the standard java properties injection. You 
 * can always use either {@link #addProperties(Properties)} or {@link #getProperties()} to set or get 
 * the current configuration of this mail connector.
 * 
 * For the detail of the properties key, read <a href="http://java.sun.com/products/javamail/javadocs/index.html">here</a>
 * 
 * 1.0.1 - Add help method for enabling black box debug mode through {@link #setDebug(boolean)}
 * 
 * @author Hugo Y. K. Lam, 
 * @author Twinsen Tsang (modifers)
 * @version 1.0.1  
 */
abstract public class MailConnector 
{
    private String     protocol, host, username, password;

    private Properties properties = new Properties();

    /**
     * Creates a new instance of MailConnector.
     * 
     * @param protocol the mail protocol.
     * @param host the mail host.
     */
    public MailConnector(String protocol, String host) 
    {
        this(protocol, host, null, null);
    }

    /**
     * Creates a new instance of MailConnector.
     * 
     * @param protocol the mail protocol.
     * @param host the mail host.
     * @param username the user name for authentication.
     * @param password the password for authentication.
     */
    public MailConnector(String protocol, String host, String username, String password) 
    {
        this.protocol = protocol;
        this.host = host;
        this.username = username;
        this.password = password;
        init();
    }

    /**
     * Initializes this mail connector.
     */
    private void init() 
    {
        if (protocol != null) 
        {
            String pname = protocol.toLowerCase();
            if (host != null) 
            {
                properties.setProperty("mail." + pname + ".host", host);
            }
            if (username != null) 
            {
                properties.setProperty("mail." + pname + ".auth", "true");
            }
        }
    }

    /**
     * Gets the host to which this mail connector connects.
     * 
     * @return the mail host.
     */
    public String getHost() 
    {
        return host;
    }

    /**
     * Gets the protocol that this mail connector uses.
     * 
     * @return the mail protocol.
     */
    public String getProtocol() 
    {
        return protocol;
    }

    /**
     * Creates a mail session from the underlying mail properties.
     * 
     * @return the mail session.
     */
    public Session createSession() 
    {
        Session session;
        if (username == null) 
        {
            session = Session.getInstance(properties);
        }
        else 
        {
            Authenticator auth = new Authenticator() 
            {
                public PasswordAuthentication getPasswordAuthentication() 
                {
                    return new PasswordAuthentication(username,
                            password == null ? "" : password);
                }
            };
            session = Session.getInstance(properties, auth);
        }
        return session;
    }
    
    /**
     * Gets the underlying mail properties used by this connector.
     * 
     * @return the mail properties.
     */
    public Properties getProperties() 
    {
        return properties;
    }    

    /**
     * Adds a property to the underlying mail properties used by this connector.
     * 
     * @param propKey the property key.
     * @param propValue the property value.
     */
    public void addProperty(String propKey, String propValue) 
    {
        properties.setProperty(propKey, propValue);
    }

    /**
     * Adds the given mail properties to the underlying mail properties used by
     * this connector.
     * 
     * @param properties the properties to be added.
     */
    public void addProperties(Properties properties) 
    {
        this.properties.putAll(properties);
    }
    
    /**
     * Set whether the mail connector is under debug mode.  
     * 
     * @param on The flag whether debug mode is switched on for this mail connector.
     */
    public void setDebug(boolean on)
    {
    	this.properties.setProperty("mail.debug", String.valueOf(on));
    }
    
    /**
     * Get whether the mail connector is under debug mode.  
     */
    public boolean getIsDebug()
    {
    	return Boolean.valueOf(this.properties.getProperty("mail.debug")).booleanValue();
    }
}