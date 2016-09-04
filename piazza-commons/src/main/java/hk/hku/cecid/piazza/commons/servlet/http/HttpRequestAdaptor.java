/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.servlet.http;

import java.util.Properties;

import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpRequestAdaptor is an abstract adapter class for handling HTTP requests. 
 * The methods in this class are empty. This class exists as convenience for 
 * creating listener objects.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public abstract class HttpRequestAdaptor implements HttpRequestListener {

    /**
     * The parameters of this listener.
     */
    protected final Properties parameters = new Properties();

    /**
     * @see hk.hku.cecid.piazza.commons.servlet.RequestListener#listenerCreated()
     */
    public void listenerCreated() throws RequestListenerException {
    }
    
    /**
     * @see hk.hku.cecid.piazza.commons.servlet.RequestListener#listenerDestroyed()
     */
    public void listenerDestroyed() throws RequestListenerException {
    }
    
    /**
     * @see hk.hku.cecid.piazza.commons.servlet.http.HttpRequestListener#doStartRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public boolean doStartRequest(HttpServletRequest request,
            HttpServletResponse response) throws RequestListenerException {
        return true;
    }

    /**
     * @see hk.hku.cecid.piazza.commons.servlet.http.HttpRequestListener#doEndRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doEndRequest(HttpServletRequest request,
            HttpServletResponse response) throws RequestListenerException {
    }
    
    /**
     * @see hk.hku.cecid.piazza.commons.servlet.RequestListener#getParameters()
     */
    public Properties getParameters() {
        return parameters;
    }
}