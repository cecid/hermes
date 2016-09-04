/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.listener;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import javax.mail.internet.InternetHeaders;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.piazza.commons.servlet.http.HttpRequestAdaptor;
import hk.hku.cecid.piazza.commons.util.Headers;


/**
 * AS2RequestAdaptor
 * 
 * @author Hugo Y. K. Lam
 *
 */
public abstract class AS2RequestAdaptor extends HttpRequestAdaptor {

    /**
     * processRequest
     * @param request
     * @param response
     * @return String
     * @throws RequestListenerException
     * @see hk.hku.cecid.piazza.commons.servlet.http.HttpRequestListener#processRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public String processRequest(HttpServletRequest request,
            HttpServletResponse response) throws RequestListenerException {
        try {
            Headers headers = new Headers(request, response);
            
            InternetHeaders requestHeaders = headers.getInternetHeaders();
            InputStream requestStream = request.getInputStream();
            AS2Message requestMessage = new AS2Message(requestHeaders, requestStream);
            
            if (requestMessage.getFromPartyID() == null || requestMessage.getToPartyID() == null) {
                response.sendError(HttpURLConnection.HTTP_BAD_REQUEST, "Invalid AS2 Message");
                return null;
            }
            
            AS2Request as2request = new AS2Request(request);
            as2request.setMessage(requestMessage);
            AS2Response as2response = new AS2Response(response);
            
            processRequest(as2request, as2response);
            
            AS2Message responseMessage = as2response.getMessage();
            if (responseMessage == null) {
                return null;
            }
            
            InternetHeaders responseHeaders = responseMessage.getHeaders();
            headers.putInternetHeaders(responseHeaders);
            
            InputStream contentStream= responseMessage.getContentStream();
            OutputStream responseStream= response.getOutputStream();
            IOHandler.pipe(contentStream, responseStream);
            
            return null;
        }
        catch (Exception e) {
            throw new RequestListenerException("Error in processing AS2 request", e);
        }
    }

    abstract public void processRequest(AS2Request request, AS2Response response)
            throws RequestListenerException;
}
