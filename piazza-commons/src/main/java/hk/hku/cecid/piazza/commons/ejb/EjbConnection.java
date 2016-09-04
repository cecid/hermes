/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.ejb;

import java.net.InetAddress;
import java.util.Hashtable;

import javax.naming.InitialContext;

/**
 * An EjbConnection represents a connection to the initial context by which the
 * specified EJBHome can be looked up.
 * 
 * The URL connection string for an EJBConnection should comply the following
 * format: PROVIDER_URL&#064;INITIAL_CONTEXT_FACTORY {&#064;SECURITY_PROTOCOL}
 * 
 * @author Hugo Y. K. Lam
 */
public class EjbConnection {

    private boolean        connected, closed;

    private String         url;

    private String         username;

    private String         password;

    private InitialContext initialContext;

    private String         host;

    /**
     * Creates a new instance of EjbConnection.
     */
    public EjbConnection(String url, String username, String password) {
        super();

        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Gets the URL of this connection.
     * 
     * @return the URL of this connection.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the username used for this connection.
     * 
     * @return the username used in this connection.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns a string representation of this object, which is the URL.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getUrl(); //+", username: "+username+", password: "+password;
    }

    /**
     * Connects to the destination URL using the username and password stored in
     * this connection.
     * 
     * @throws EjbConnectionException if errors occurred during the
     *             establishment of connection.
     */
    public synchronized void connect() throws EjbConnectionException {
        connect(username, password);
    }

    /**
     * Connects to the destination URL using the specified username and
     * password.
     * 
     * @param username the username used to connect. null if authentication is
     *            not required.
     * @param password the password used to connect. null if authentication is
     *            not required.
     * @throws EjbConnectionException if errors occurred during the
     *             establishment of connection or, the connection has been
     *             connected or closed already.
     */
    public synchronized void connect(String username, String password)
            throws EjbConnectionException {
        if (!connected && !closed) {
            try {
                String[] connStrings = getConnectionStrings();

                Hashtable props = new Hashtable();
                props.put(InitialContext.PROVIDER_URL, connStrings[0]);
                props.put(InitialContext.INITIAL_CONTEXT_FACTORY,
                        connStrings[1]);

                // This establishes the security for
                // authorization/authentication
                if (username != null && !"".equals(username)) {
                    props.put(InitialContext.SECURITY_PRINCIPAL, username);
                    props.put(InitialContext.SECURITY_CREDENTIALS,
                            password == null ? "" : password);
                    if (connStrings.length > 2) {
                        props.put(InitialContext.SECURITY_PROTOCOL,
                                connStrings[2]);
                    }
                }

                InetAddress localhost = InetAddress.getLocalHost();
                host = localhost.getHostName() + "@"
                        + localhost.getHostAddress();

                InitialContext context = new InitialContext(props);
                context.rebind(host, "connected");

                initialContext = context;

                connected = true;
                closed = false;
            }
            catch (Exception e) {
                throw new EjbConnectionException(
                        "Unable to establish connection.", e);
            }
        }
        else {
            throw new EjbConnectionException(
                    "Connection has been connected or closed already.");
        }
    }

    /**
     * Closes this connection and releases any resources associated.
     * 
     * @throws EjbConnectionException if errors occurred during closing the
     *             connection or, the connection has not yet been connected or
     *             has been closed already.
     */
    public synchronized void close() throws EjbConnectionException {
        if (connected && !closed) {
            try {
                initialContext.unbind(host);
                initialContext.close();

                connected = false;
                closed = true;
            }
            catch (Exception e) {
                throw new EjbConnectionException("Unable to close connection.",
                        e);

            }
        }
        else {
            throw new EjbConnectionException(
                    "Connection has not yet been connected or has been closed already.");
        }
    }

    /**
     * Closes this connection in finalization.
     * 
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        super.finalize();
        if (!closed) {
            close();
        }
    }

    /**
     * Parses the URL connection string.
     * 
     * @return an array of String which contains the PROVIDER_URL,
     *         INITIAL_CONTEXT_FACTORY, and, if any, the SECURITY_PROTOCOL.
     * @throws EjbConnectionException if the URL has less than two connection
     *             tokens.
     */
    private String[] getConnectionStrings() throws EjbConnectionException {
        String[] connStrings = url == null ? null : url.split("@");
        if (connStrings == null || connStrings.length < 2) {
            throw new EjbConnectionException("Invalid URL: " + url);
        }
        else {
            return connStrings;
        }
    }

    /**
     * Retrieves the EJBHome by looking it up using the specified JNDI name. The
     * EJBHome will be narrowed to the specified class if it is a remote
     * interface.
     * 
     * @param jndiName the JNDI name for the EJBHome to be looked up.
     * @param narrowTo the class to which the EJBHome should be narrowed.
     * @return the EJBHome bound to the specified JNDI name.
     * @throws EjbConnectionException if there is a naming or narrowing error.
     * @throws NullPointerException if <code>narrowTo<code> is null.
     */
    public Object lookupHome(String jndiName, Class narrowTo)
            throws EjbConnectionException {
        try {
            Object objRef = initialContext.lookup(jndiName);
            // only narrow if necessary
            if (narrowTo.isInstance(java.rmi.Remote.class)) {
                return javax.rmi.PortableRemoteObject.narrow(objRef, narrowTo);
            }
            else {
                return objRef;
            }
        }
        catch (Exception e) {
            throw new EjbConnectionException(
                    "Unable to lookup the home interface for jndi name: "
                            + jndiName, e);
        }
    }
}