/**
 * It provides the classes for the listening HTTP Request for 
 * incoming and outgoing, and the request and response model 
 * for SFRM.
 */
package hk.hku.cecid.edi.sfrm.listener;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.KeyStoreException;

import javax.mail.internet.InternetHeaders;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessageException;

import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;

import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.piazza.commons.servlet.http.HttpRequestAdaptor;
import hk.hku.cecid.piazza.commons.security.SMimeException;
import hk.hku.cecid.piazza.commons.util.Headers;

/**
 * This is the sfrm customizied HTTP Request Adaptor.
 * 
 * @author Twinsen
 * @version 1.0.0
 * @since 	1.0.0
 * 
 * @see hk.hku.cecid.piazza.commons.servlet.http.HttpRequestAdaptor
 */
public abstract class SFRMRequestAdaptor extends HttpRequestAdaptor {

    /**
     * It process a HTTP request from the piazza corvus framework.
     * 
     * @param request		
     * 			The HTTP Request
     * @param response
     * 			The HTTP Response
     * @return
     * @throws RequestListenerException
     * @see hk.hku.cecid.piazza.commons.servlet.http.HttpRequestListener#processRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public String processRequest(HttpServletRequest request,
            HttpServletResponse response) throws RequestListenerException {
        try {
            Headers headers = new Headers(request, response);
            
            // Create the HTTP headers.
            InternetHeaders requestHeaders = headers.getInternetHeaders();
            InputStream requestStream = request.getInputStream();
            SFRMMessage requestMessage = new SFRMMessage(requestHeaders, requestStream);
                        
            if (requestMessage.getPartnershipId() == null){
            	// TODO: Do we need to do more validation to prevent expensive rollback?
                response.sendError(HttpURLConnection.HTTP_BAD_REQUEST, "Invalid SFRM Message");
                return null;
            }
            
            // Redirect request 
            SFRMRequest sfrmrequest = new SFRMRequest(request);
            sfrmrequest.setMessage(requestMessage);
            SFRMResponse sfrmresponse = new SFRMResponse(response);
            
            this.processRequest(sfrmrequest, sfrmresponse);
            
            SFRMMessage responseMessage = sfrmresponse.getMessage();
            
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
        	try{
        		return this.processFail(request, response, e);
        	}
        	catch(Exception ex){
        		throw new RequestListenerException(
						"Error in processing SFRM request", ex);
        	}
        }
    }
    
    /**
     * It process a HTTP request from the piazza corvus framework
     * when there is exception thrown from underlying handler.
     * 
     * @param request		
     * 			The HTTP Request
     * @param response
     * 			The HTTP Response
     * @param cause 
     * 			The exception cause to fail.
     * @return
     */
    public String processFail(HttpServletRequest request,
			HttpServletResponse response, Exception cause) throws IOException {
    	
    	if ( cause == null )      			    	
    		return null; // No Exception or unknown exception type.    	
    	// Get the actual exception.
    	Throwable actualCause = cause.getCause();
    	SFRMProcessor.getInstance().getLogger().error("Fail to receive: ", cause); 
    	
    	// -----------------------------------------------------------
    	// Error handling
    	//
    	// HTTP 403 (Forbidden): 
    	//	when the partnership setup is missing or invalid message 
    	//  security setup
    	// HTTP 401 (Unauthorized):
    	//  when the certificate or the private key in the keystore can
    	//  not de-crypt and unsign the message
    	// HTTP 412 
    	// HTTP 500
    	// -----------------------------------------------------------    	    	
    	if (actualCause instanceof MalformedURLException ||
    		actualCause instanceof GeneralSecurityException)
    		response.sendError(HttpURLConnection.HTTP_FORBIDDEN, 
    			actualCause.getMessage());
    	else 
    	if (actualCause instanceof CertificateException ||
    		actualCause instanceof KeyStoreException 	||
    		actualCause instanceof SMimeException)
    		response.sendError(HttpURLConnection.HTTP_UNAUTHORIZED,
    			actualCause.getMessage());    		
    	else
    	if (actualCause instanceof SFRMMessageException)
    		response.sendError(HttpURLConnection.HTTP_PRECON_FAILED,
        		actualCause.getMessage());
    	else 
    	if (actualCause instanceof IOException)
    		response.sendError(HttpURLConnection.HTTP_INTERNAL_ERROR,
            	actualCause.getMessage());
    	else
    		response.sendError(HttpURLConnection.HTTP_BAD_REQUEST,
            	actualCause.getMessage());
    	// Log error.    	
    	return null;
    }

    /**
     * 
     * @param request
     * @param response
     * @throws RequestListenerException
     */
    abstract public void processRequest(SFRMRequest request, SFRMResponse response)
            throws RequestListenerException;
}
