/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.corvus.core.main.listener;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.piazza.commons.servlet.http.HttpRequestAdaptor;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpProxyListener is an HTTP request listener which serves as a simple
 * HTTP proxy handler. It does not support HTTPS nor caching. 
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class HttpProxyListener extends HttpRequestAdaptor {

    /**
     * Perform a simple HTTP proxy operation. No caching will be provided.
     * 
     * @see hk.hku.cecid.piazza.commons.servlet.http.HttpRequestListener#processRequest(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public String processRequest(HttpServletRequest request,
            HttpServletResponse response) throws RequestListenerException {

        try {
            /*
             * Check whether the request server is the local server. If true, an
             * error will be sent.
             */
            InetAddress[] targetServerAddresses = InetAddress
                    .getAllByName(request.getServerName());
            InetAddress localServerAddress = InetAddress.getLocalHost();
            Sys.main.log.debug("Local server address: "+localServerAddress);

            for (int i = 0; i < targetServerAddresses.length; i++) {
                if (targetServerAddresses[i].isLoopbackAddress()
                        || targetServerAddresses[i].equals(localServerAddress)) {
                    response.sendError(HttpURLConnection.HTTP_FORBIDDEN,
                            "Address not allowed");
                    return null;
                }
            }

            /*
             * Reconstruct the request url
             */
            String requrl = request.getRequestURL().toString();
            Sys.main.log.debug("REQ URL: " + requrl);

            requrl = getURL(requrl);
            Sys.main.log.debug("REQ URL Modified: " + requrl);

            String query = request.getQueryString();
            requrl = requrl + (query == null ? "" : "?" + query);
            Sys.main.log.debug("REQ URL FINAL: " + requrl);

            /*
             * Prepare connection for the request url
             */
            URL url = new URL(requrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod(request.getMethod());

            /*
             * Set all the request headers
             */
            Enumeration requestHeaders = request.getHeaderNames();
            while (requestHeaders.hasMoreElements()) {
                String requestHeaderKey = requestHeaders.nextElement()
                        .toString();
                Enumeration enum2 = request.getHeaders(requestHeaderKey);
                while (enum2.hasMoreElements()) {
                    String requestHeaderValue = getHeaderProperty(enum2
                            .nextElement().toString());
                    conn.addRequestProperty(requestHeaderKey,
                            requestHeaderValue);
                    Sys.main.log.debug("REQ Header: " + requestHeaderKey + "="
                            + requestHeaderValue);
                }
            }

            /*
             * Set all the request parameters
             */
            ServletInputStream servletInputStream = request.getInputStream();
            if (request.getContentLength() > 0) {
                conn.setDoOutput(true);
                OutputStream targetOutputstream = conn.getOutputStream();

                int b;
                while ((b = servletInputStream.read()) != -1) {
                    targetOutputstream.write(b);
                }
            }

            /*
             * Establish the connection
             */
            Sys.main.log.debug("Connecting to " + requrl);
            conn.connect();

            /*
             * Set the response code, return if remote server error
             */
            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();
            if (responseCode >= HttpURLConnection.HTTP_INTERNAL_ERROR) {
                response.sendError(responseCode, responseMessage);
                return null;
            }
            response.setStatus(responseCode);
            Sys.main.log.debug("RES CODE: " + responseCode);
            Sys.main.log.debug("RES Message: " + responseMessage);

            /*
             * Set the response headers
             */
            Map reponseHeaders = conn.getHeaderFields();
            Iterator reponseHeaderKeys = reponseHeaders.keySet().iterator();
            while (reponseHeaderKeys.hasNext()) {
                Object reponseHeaderKey = reponseHeaderKeys.next();
                if (reponseHeaderKey instanceof String) {
                    String reponseHeaderValue = getHeaderProperty(reponseHeaders
                            .get(reponseHeaderKey).toString());
                    response.addHeader(reponseHeaderKey.toString(),
                            reponseHeaderValue);
                    Sys.main.log.debug("RES Header: " + reponseHeaderKey + "="
                            + reponseHeaderValue);
                }
            }

            /*
             * Write out the response content
             */
            InputStream targetInputstream = conn.getInputStream();
            ServletOutputStream outs = response.getOutputStream();

            int b = targetInputstream.read();
            while (b != -1) {
                outs.write(b);
                b = targetInputstream.read();
            }
            outs.flush();
        }
        catch (Exception e) {
            Sys.main.log.error("HttpProxy error: " + e);
        }
        return null;
    }

    /*
     * Remove the brackets, if any, in the given header property
     */
    private String getHeaderProperty(String prop) {
        if (prop.startsWith("[") && prop.endsWith("]")) {
            return prop.substring(1, prop.length() - 1);
        }
        else {
            return prop;
        }
    }

    /*
     * Extract the forwarding url, if any, from the given url
     */
    private String getURL(String url) {
        int i = url.indexOf("/[");
        if (i < 0) {
            return url;
        }

        String host = url.substring(0, i);

        url = url.substring(i + 2);
        i = url.indexOf("]");
        if (i < 0) {
            return url;
        }

        url = url.substring(0, i);

        if (url.indexOf("//") > -1) {
            return url;
        }
        else {
            return host + url;
        }
    }
}