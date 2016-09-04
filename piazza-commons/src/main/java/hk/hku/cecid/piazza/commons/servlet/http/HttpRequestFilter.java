/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.servlet.http;


/**
 * HttpRequestFilter
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public interface HttpRequestFilter {

    /**
     * Invoked when the an HTTP request is accepted.
     * 
     * @param event the HTTP request event.
     */
    public boolean requestAccepted(HttpRequestEvent event);

    /**
     * Invoked when the an HTTP request has been processed.
     * 
     * @param event the HTTP request event.
     */
    public void requestProcessed(HttpRequestEvent event);
}