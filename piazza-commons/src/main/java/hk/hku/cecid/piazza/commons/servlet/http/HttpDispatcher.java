/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.servlet.http;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpDispatcher is an HttpServlet which dispatches Http request to the Http
 * request listeners registered in its Http dispatcher context.
 * 
 * @see HttpDispatcherContext
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class HttpDispatcher extends HttpServlet {

    private ServletConfig         servletConfig;

    private HttpDispatcherContext dispatcherContext;

    /**
     * The request attribute key of the servlet config.
     */
    public static final String    CONFIG_KEY = "SERVLET_CONFIG";

    /**
     * Initializes the servlet.
     * 
     * @param config the ServletConfig.
     * @throws ServletException if error occurred in initialization.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        servletConfig = config;
        String contextId = servletConfig.getInitParameter("context-id");
        dispatcherContext = HttpDispatcherContext.getContext(contextId);
        if (dispatcherContext == null) {
            dispatcherContext = HttpDispatcherContext.getDefaultContext();
        }
        Sys.main.log.info(servletConfig.getServletName()
                + " initialized successfully");
    }

    /**
     * Destroys the servlet.
     */
    public void destroy() {
        super.destroy();
        dispatcherContext.unregisterAll();
        Sys.main.log.info(servletConfig.getServletName()
                + " destroyed successfully");
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     * 
     * @param request servlet request.
     * @param response servlet response.
     */
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        dispatcherContext.acquire();

        try {
            /*
             * Generate an HTTP request event and look for its listener
             */
            String pathInfo = dispatcherContext.getPathInfo(request);
            HttpRequestEvent event = new HttpRequestEvent(pathInfo, request, response);
            HttpRequestListener listener = dispatcherContext.getListener(pathInfo);

            /*
             * Send an HTTP NOT FOUND error if no listener found for the event.
             */
            if (listener == null) {
                Sys.main.log.debug(event + " has no listener.");
                response.sendError(HttpURLConnection.HTTP_NOT_FOUND, pathInfo);
                return;
            }
            /*
             * Process the event if there is a corresponding listener.
             */
            else {
                String path = null;
                try {
                    setDefaults(request, response);

                    /*
                     * Fire a request accepted event and invokes the listener to
                     * process it if it should be accepted.
                     */
                    if (fireRequestAcceptedEvent(event)) {
                        Sys.main.log.debug(event + " is being processed by "
                                + listener);

                        /*
                         * Process the request and time it.
                         */
                        long start = new Date().getTime();
                        if (listener.doStartRequest(request, response)) {
                            path = listener.processRequest(request, response);
                            listener.doEndRequest(request, response);
                        }
                        long end = new Date().getTime();

                        Sys.main.log.debug(event
                                + " had been processed successfully for "
                                + (end - start) + " ms");

                        fireRequestProcessedEvent(event);
                    }
                    /*
                     * Send an HTTP BAD REQUEST error if the request has been
                     * rejected.
                     */
                    else {
                        Sys.main.log.debug(event + " is rejected.");
                        if (!response.isCommitted()) {
                            response.sendError(
                                    HttpURLConnection.HTTP_BAD_REQUEST,
                                    pathInfo);
                        }
                        return;
                    }
                }
                /*
                 * Send an HTTP INTERNAL ERROR error if there is any unhandled
                 * or unexpected server error.
                 */
                catch (Throwable e) {
                    Sys.main.log.error("Error in processing HTTP request", e);
                    String errMsg = (e instanceof RequestListenerException ? e
                            .getMessage() : e.getClass().getName() + ": "
                            + e.getMessage());
                    response.sendError(HttpURLConnection.HTTP_INTERNAL_ERROR,
                            errMsg);
                    return;
                }

                /*
                 * Forward the request to the path, if any, returned by the
                 * listener after processing the request.
                 */
                if (path != null) {
                    if (path.startsWith("&")) {
                        String returnCode = path.substring(1);
                        Properties params = listener.getParameters();
                        path = params == null? null : params.getProperty(
                                "return-code:" + returnCode);
                        if (path == null) {
                            return;
                        }
                    }

                    Sys.main.log.debug(event + " is being forwarded to '" + path + "'");

                    RequestDispatcher dispatcher = getServletContext()
                            .getRequestDispatcher(path);
                    dispatcher.forward(request, response);
                }
            }
        }
        finally {
            dispatcherContext.release();
        }
    }

    /**
     * Sets the defaults for this HttpDispatcher. The servlet config will be
     * stored as a request attribute and the encodings of the request and
     * response will be set to what defined in the dispatcher context.
     * 
     * @param request the servlet request.
     * @param response the servlet response.
     */
    private void setDefaults(HttpServletRequest request,
            HttpServletResponse response) {
        try {
            request.setAttribute(CONFIG_KEY, servletConfig);

            if (dispatcherContext.getResponseEncoding() != null) {
                String enc = dispatcherContext.getResponseEncoding();
                if (enc.indexOf(';')!=-1) {
                    response.setContentType(enc);
                }
                else {
                    response.setCharacterEncoding(enc);
                }
            }
            if (dispatcherContext.getRequestEncoding() != null) {
                request.setCharacterEncoding(dispatcherContext.getRequestEncoding());
            }
        }
        catch (Exception e) {
            Sys.main.log.error("Unable to set servlet default encoding", e);
        }
    }

    /**
     * Invoked before a request is processed by the listener registered at the
     * request context path which is relative to this servlet's context path.
     * 
     * @param event the HTTP request event.
     * @return true if the request should be accepted and processed by the
     *         registered listener.
     */
    protected boolean fireRequestAcceptedEvent(HttpRequestEvent event) {
        Iterator filters = dispatcherContext.getRequestFilters().iterator();
        while (filters.hasNext()) {
            HttpRequestFilter filter = (HttpRequestFilter) filters.next();
            if (!filter.requestAccepted(event)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Invoked when a request has been processed by the listener registered at
     * the request context path which is relative to this servlet's context
     * path.
     * 
     * @param event the HTTP request event.
     */
    protected void fireRequestProcessedEvent(HttpRequestEvent event) {
        Iterator filters = dispatcherContext.getRequestFilters().iterator();
        while (filters.hasNext()) {
            HttpRequestFilter filter = (HttpRequestFilter) filters.next();
            filter.requestProcessed(event);
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * 
     * @param request the servlet request.
     * @param response the servlet response.
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * 
     * @param request the servlet request.
     * @param response the servlet response.
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Gets a short description of this servlet.
     * 
     * @return a short description of this servlet.
     */
    public String getServletInfo() {
        return "An HTTP Request Dispatcher";
    }
}