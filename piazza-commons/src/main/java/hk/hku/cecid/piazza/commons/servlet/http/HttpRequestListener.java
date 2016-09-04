/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.servlet.http;

import hk.hku.cecid.piazza.commons.servlet.RequestListener;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpRequestListener is a listener for handling requests 
 * for both HTTP <code>GET</code> and <code>POST</code> methods. 
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public interface HttpRequestListener extends RequestListener {

    /**
     * Invoked before invoking processRequest().
     * 
     * @param request the servlet request.
     * @param response the servlet response.
     * @return true if processRequest() should be invoked.
     * @throws RequestListenerException if errors occurred during processing the request.
     */
    public boolean doStartRequest(HttpServletRequest request,
            HttpServletResponse response) throws RequestListenerException;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     * 
     * @param request the servlet request.
     * @param response the servlet response.
     * @return the forwarding path. 
     * 
     * @throws RequestListenerException if errors occurred during processing the request.
     */
    public String processRequest(HttpServletRequest request,
            HttpServletResponse response) throws RequestListenerException;

    /**
     * Invoked after invoking processRequest().
     * 
     * @param request the servlet request.
     * @param response the servlet response.
     * 
     * @throws RequestListenerException if errors occurred during processing the request.
     */
    public void doEndRequest(HttpServletRequest request,
            HttpServletResponse response) throws RequestListenerException;
}