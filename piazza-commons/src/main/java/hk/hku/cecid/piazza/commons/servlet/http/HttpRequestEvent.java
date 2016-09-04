/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * HttpRequestEvent represents an event of an HTTP request.  
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class HttpRequestEvent {

    private String pathInfo;
    private HttpServletRequest request;
    private HttpServletResponse response;
        
    /**
     * Creates a new instance of HttpRequestEvent.
     */
    HttpRequestEvent() {
        super();
    }

    /**
     * Creates a new instance of HttpRequestEvent.
     * 
     * @param pathInfo the path information.
     * @param request the request object.
     * @param response the response object.
     */
    HttpRequestEvent(String pathInfo, HttpServletRequest request, HttpServletResponse response) {
        this.pathInfo = pathInfo;
        this.request = request;
        this.response = response;
    }

    /**
     * Gets the path information.
     * 
     * @return the path information.
     */
    public String getPathInfo() {
        return pathInfo;
    }
    
    /**
     * Sets the path information.
     * 
     * @param pathInfo the path information.
     */
    void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }
    
    /**
     * Gets the request object.
     * 
     * @return the request object.
     */
    public HttpServletRequest getRequest() {
        return request;
    }
    
    /**
     * Sets the request object.
     * 
     * @param request the request object.
     */
    void setRequest(HttpServletRequest request) {
        this.request = request;
    }
    
    /**
     * Gets the response object.
     * 
     * @return the response object.
     */
    public HttpServletResponse getResponse() {
        return response;
    }
    
    /**
     * Sets the response object.
     * 
     * @param response the response object.
     */
    void setResponse(HttpServletResponse response) {
        this.response = response;
    }
    
    
    /**
     * Returns a string representation of this event.
     * 
     * @return a string representation of this event.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Request#" + request.hashCode() + ":" + pathInfo + "@" + request.getRemoteHost();
    }
}