package hk.hku.cecid.piazza.commons.soap;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.piazza.commons.servlet.http.HttpRequestAdaptor;
import hk.hku.cecid.piazza.commons.util.Headers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

/**
 * SOAPHttpAdaptor is both an HttpRequestListener and SOAPRequestListener. It is
 * an adaptor for handling SOAP on Http requests.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public abstract class SOAPHttpAdaptor extends HttpRequestAdaptor implements
        SOAPRequestListener {

    /**
     * A SOAP message factory.
     */
    protected MessageFactory msgFactory;

    /**
     * A SOAP factory.
     */
    protected SOAPFactory    soapFactory;

    /**
     * Creates a new instance of MessageFactory.
     * 
     * @throws RequestListenerException if unable to create MessageFactory.
     * @see hk.hku.cecid.piazza.commons.servlet.RequestListener#listenerCreated()
     */
    public void listenerCreated() throws RequestListenerException {
        try {
            msgFactory = MessageFactory.newInstance();
            soapFactory = SOAPFactory.newInstance();
        }
        catch (Exception e) {
            throw new RequestListenerException(
                    "Unable to create SOAP factories", e);
        }
    }

    /**
     * Cleans up resources.
     * 
     * @see hk.hku.cecid.piazza.commons.servlet.RequestListener#listenerDestroyed()
     */
    public void listenerDestroyed() throws RequestListenerException {
        msgFactory = null;
    }

    /**
     * Processes the HTTP request and transforms it into a SOAP request.
     * 
     * @see hk.hku.cecid.piazza.commons.servlet.http.HttpRequestListener#processRequest(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public String processRequest(HttpServletRequest request,
            HttpServletResponse response) throws RequestListenerException {
        try {
            Headers headers = new Headers(request, response);
            
            /*
             * Create a SOAP response object.
             */
            SOAPResponse soapResponse = new SOAPResponse(response);
            SOAPMessage responseMessage = msgFactory.createMessage();
            soapResponse.setMessage(responseMessage);

            try {
                /*
                 * Create a SOAP request object.
                 */
                MimeHeaders mimeHeaders = headers.getMimeHeaders();
                
                byte[] requestBytes;
                InputStream requestStream;
                
                if (isCacheEnabled()) {
                    requestBytes = IOHandler.readBytes(request.getInputStream());
                    requestStream = new ByteArrayInputStream(requestBytes);
                }
                else {
                    requestBytes = null;
                    requestStream = request.getInputStream();
                }
                
                SOAPMessage soapMessage;
                try {
                    soapMessage = msgFactory.createMessage(mimeHeaders,
                            requestStream);
                    soapMessage.getSOAPPart().getEnvelope().getBody();
                }
                catch (SOAPException e) {
                    throw new SOAPFaultException(
                            SOAPFaultException.SOAP_FAULT_CLIENT,
                            "Invalid SOAP message: " + e.getMessage(), e);
                }

                SOAPRequest soapRequest = new SOAPRequest(request);
                soapRequest.setBytes(requestBytes);
                soapRequest.setHeaders(mimeHeaders);
                soapRequest.setMessage(soapMessage);

                /*
                 * Prcoess the SOAP request.
                 */
                processRequest(soapRequest, soapResponse);
            }
            catch (Throwable e) {
                /*
                 * If there is any exception, create a SOAP fault if SOAP fault
                 * reporting is enabled.
                 */
                if (isSOAPFaultEnabled()) {
                    Sys.main.log.error("SOAP fault exception trace", e);
                    soapResponse.addFault(e);
                }
                else {
                    throw e;
                }
            }

            /*
             * Process the SOAP response.
             */
            SOAPMessage replyMessage = soapResponse.getMessage();

            if (replyMessage != null) {
                if (replyMessage.saveRequired()) {
                    replyMessage.saveChanges();
                }

                /*
                 * Set the content type if the SOAP message is not multipart.
                 * Otherwise, unset the content type and let the mime headers
                 * define it.
                 */
                if (replyMessage.countAttachments() == 0) {
                    response.setContentType("text/xml;charset=UTF-8");
                }
                else {
                    response.setContentType(null);
                }
                
                MimeHeaders replyHeaders = replyMessage.getMimeHeaders();
                Iterator iter = replyHeaders.getAllHeaders();
                while (iter.hasNext()) {
                	MimeHeader header = (MimeHeader)iter.next();
                	
        			// .Net only supports Multipart/Related MIME type in small letter 
                	String newValue = "";
                	if ("Content-Type".equals(header.getName())) {
                		String value = header.getValue();
                		
                		String[] values = value.split(";");
                		for (String s: values) {
                			s = s.trim();
                			
                			if (s.toLowerCase().startsWith("multipart")) 
                				s = s.toLowerCase();
                			
            				if ("".equals(newValue))
            					newValue = s;
            				else
            					newValue += "; " + s;	
                		}

                		replyMessage.getMimeHeaders().setHeader("Content-Type", newValue);
                	}
                }
                
                headers.putMimeHeaders(replyMessage.getMimeHeaders());

                /*
                 * Write the response message.
                 */
                javax.servlet.ServletOutputStream servletOutputStream = response
                        .getOutputStream();
                replyMessage.writeTo(servletOutputStream);
                servletOutputStream.flush();
            }
            else {
                response.setStatus(HttpURLConnection.HTTP_NO_CONTENT);
            }

            return null;
        }
        catch (Throwable e) {
            throw new RequestListenerException(
                    "Error in transforming the HTTP request into SOAP request",
                    e);
        }
    }

    /**
     * Checks if errors in the request process should be reported as a SOAP
     * fault to the client.
     * 
     * @return true if SOAP fault reporting is enabled.
     */
    protected boolean isSOAPFaultEnabled() {
        return true;
    }
    
    /**
     * Checks if cache in memory should be enabled. If this method returns 
     * false, it indicates that the SOAP request object will not provide a byte
     * array cache of the original SOAP request.
     * 
     * @return true if cache in memory should be enabled.
     */
    protected boolean isCacheEnabled() {
        return true;
    }
}