/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.net;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.module.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * HttpConnector is a connector for making HTTP/S connections to an URL.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class HttpConnector {

    private URL url;
    
    private Vector keyManagers = new Vector(); 
    private Vector trustManagers = new Vector(); 
    
    private HostnameVerifier hostnameVerifier;
    
    private Map headers;
    
    /**
     * Creates a new instance of HttpConnector.
     * 
     * @param destUrl the destination URL, either in String or URL format.
     * @throws MalformedURLException if the URL is malformed.
     */
    public HttpConnector(Object destUrl) throws MalformedURLException {
        if (destUrl instanceof URL) {
            url = (URL)destUrl;
        }
        else {
            url = new URL(destUrl.toString());
        }
        setDefaultManagers();
    }

    /**
     * Sets the default key manager and trust manager. This method will look up 
     * module components of key manager (id: ssl-key-manager) and trust manager 
     * (id: ssl-trust-manager) in the system main module.
     */
    private void setDefaultManagers() {
        Component keyman = Sys.main.getComponent("ssl-key-manager");
        Component trustman = Sys.main.getComponent("ssl-trust-manager");
        if (keyman != null && keyman instanceof KeyManager) {
            addKeyManager((KeyManager)keyman);
        }
        if (trustman != null && trustman instanceof TrustManager) {
            addTrustManager((TrustManager)trustman);
        }
    }
    
    /**
     * Sets a host name verifier for SSL connection.
     * 
     * @param hostnameVerifier the host name verifier.
     */
    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }
    
    /**
     * Gets the host verifier for SSL connection.
     * 
     * @return the host name verifier.
     */
    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier == null? HttpsURLConnection.getDefaultHostnameVerifier() : hostnameVerifier;
    }
    
    /**
     * Adds a key manager for SSL connection.
     * 
     * @param km the key manager.
     */
    public void addKeyManager(KeyManager km) {
        if (km != null) {
            keyManagers.addElement(km);
        }
    }

    /**
     * Adds a trust manager for SSL connection.
     * 
     * @param tm the trust manager.
     */
    public void addTrustManager(TrustManager tm) {
        if (tm != null) {
            trustManagers.addElement(tm);
        }
    }
    
    /**
     * Gets the SSL socket factory which is used in SSL connection.
     * 
     * @return the SSL socket factory.
     * @throws ConnectionException if unable to create SSL socket factory.
     */
    public SSLSocketFactory getSSLSocketFactory() throws ConnectionException {
        try {
            if (keyManagers.size() > 0 || trustManagers.size() > 0) {
                SSLContext context = SSLContext.getInstance("SSL");
                context.init((KeyManager[]) keyManagers.toArray(new KeyManager[] {}),
                        (TrustManager[]) trustManagers.toArray(new TrustManager[] {}),
                        null);
                return context.getSocketFactory();
            }
            else {
                return HttpsURLConnection.getDefaultSSLSocketFactory();
            }
        } catch (Exception e) {
            throw new ConnectionException("Unable to create SSL socket factory", e);
        }
    }    
    
    /**
     * Sets the HTTP request headers.
     * 
     * @param headers the HTTP headers.
     */
    public void setRequestHeaders(Map headers) {
        this.headers = headers; 
    }
    
    /**
     * Gets the HTTP request headers.
     * 
     * @return the HTTP headers, or null.
     */
    public Map getRequestHeaders() {
        return headers;
    }
    
    /**
     * Creates a new HTTP connection based on this connector's properties.
     * 
     * @return a new HTTP connection.
     * @throws ConnectionException if unable to create a new HTTP connection.
     */
    public HttpURLConnection createConnection() throws ConnectionException {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
    
            if (connection instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                httpsConnection.setHostnameVerifier(getHostnameVerifier());
                httpsConnection.setSSLSocketFactory(getSSLSocketFactory());
            }
    
            if (headers != null) {
                Iterator headerKeys = headers.keySet().iterator();
                while (headerKeys.hasNext()) {
                    String headerKey = headerKeys.next().toString();
                    Object headerValue = headers.get(headerKey);
                    if (headerValue != null) {
                        connection.addRequestProperty(headerKey, headerValue.toString());
                    }
                }
            }
            return connection;
        } catch (Exception e) {
            throw new ConnectionException("Unable to create HTTP connection", e);
        }
    }
    
    /**
     * Sends an HTTP/S request using the given HTTP connection.
     * 
     * @param request the HTTP request content or null for a simple get request.
     * @return an input stream for reading the reply from the host. 
     * @throws ConnectionException if failed in sending the HTTP request or 
     *          creating a new connection.
     */
    public InputStream send(InputStream request) throws ConnectionException {
        return send(request, createConnection());
    }
    
    /**
     * Sends an HTTP/S request using the given HTTP connection.
     * 
     * @param request the HTTP request content or null for a simple get request.
     * @param connection the HTTP connection for sending the request.
     * @return an input stream for reading the reply from the host. 
     * @throws ConnectionException if failed in sending the HTTP request.
     */
    public InputStream send(InputStream request, HttpURLConnection connection) throws ConnectionException {
        OutputStream outstream = null;
        try {
            if (request != null) {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                outstream = connection.getOutputStream();
                IOHandler.pipe(request, outstream);
            }
            else {
                connection.setRequestMethod("GET");
            }
            
            connection.connect();

            return connection.getInputStream();
        }
        catch (Exception e) {
            throw new ConnectionException("Unable to send HTTP request", e);
        }
        finally {
            try {
                if (outstream != null) {
                    outstream.close();
                }
            } catch (Exception e) {
            }
        }        
    }
}