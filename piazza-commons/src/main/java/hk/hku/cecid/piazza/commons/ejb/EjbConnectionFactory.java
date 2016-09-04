/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.ejb;

import hk.hku.cecid.piazza.commons.util.PropertyTree;

import java.net.URL;

/**
 * EjbConnectionFactory is a concrete factory class for creating EjbConnection
 * by using a pre-defined set of properties.
 * 
 * @author Hugo Y. K. Lam
 */
public final class EjbConnectionFactory {

    private EjbConnectionFactory() {
    }

    private static PropertyTree props = new PropertyTree();

    /**
     * Configures the EjbConnectionFactory from the configuration located by the
     * specifed URL. Expected to be called before any other methods are called.
     * 
     * @param url The URL of the configuration file.
     * @throws EjbConnectionException if any errors in configuring the
     *             EjbConnectionFactory.
     */
    public static void configure(URL url) throws EjbConnectionException {
        try {
            props = new PropertyTree(url);
        }
        catch (Exception e) {
            throw new EjbConnectionException(
                    "Unable to configure EJB connection factory", e);
        }
    }

    /**
     * Creates a new EjbConnection. The url, username and password will be
     * retrieved from the default properties.
     * 
     * @return a new EjbConnection.
     */
    public static EjbConnection createConnection()
            throws EjbConnectionException {
        String url = props.getProperty("url", null);

        if (url == null) {
            throw new EjbConnectionException("No default connection URL.");
        }
        else {
            return createConnection(url);
        }
    }

    /**
     * Creates a new EjbConnection. The username and password will be retrieved
     * from the default properties.
     * 
     * @param url the URL for this connection.
     * @return a new EjbConnection.
     */
    public static EjbConnection createConnection(String url) {
        return createConnection(url, props.getProperty("username", null), props
                .getProperty("password", null));
    }

    /**
     * Creates a new EjbConnection.
     * 
     * @param url the URL for this connection.
     * @param username the username for authentication.
     * @param password the password for authentication.
     * @return a new EjbConnection.
     */
    public static EjbConnection createConnection(String url, String username,
            String password) {
        return new EjbConnection(url, username, password);
    }
}