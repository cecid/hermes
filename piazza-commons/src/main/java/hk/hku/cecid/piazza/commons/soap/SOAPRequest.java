/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.soap;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;

/**
 * The SOAPRequest class represents a SOAP request.
 * It is independent of which transport protocol it is using
 * and contains the SOAP message of the original request.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class SOAPRequest {

    private MimeHeaders headers;

    private SOAPMessage message;

    private byte[]      bytes;

    private Object      source;

    /**
     * Creates a new instance of SOAPRequest.
     */
    SOAPRequest() {
    }

    /**
     * Creates a new instance of SOAPRequest.
     *
     * @param source the source which initiated this request.
     */
    SOAPRequest(Object source) {
        this.source = source;
    }

    /**
     * Gets the SOAP message as bytes.
     * 
     * @return the byte array of the SOAP message.
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Gets the mime headers of the request.
     * 
     * @return the mime headers of the request.
     */
    public MimeHeaders getHeaders() {
        return headers;
    }

    /**
     * Gets the SOAP message of this request.
     * 
     * @return the SOAP message of this request.
     */
    public SOAPMessage getMessage() {
        return message;
    }

    /**
     * Gets the source which initiated this request.
     * 
     * @return the source which initiated this request.
     */
    public Object getSource() {
        return source;
    }

    /**
     * Sets the bytes of the SOAP message. 
     * 
     * @param bs the byte array of the SOAP message.
     */
    void setBytes(byte[] bs) {
        bytes = bs;
    }

    /**
     * Sets the mime headers of the request.
     * 
     * @param headers the mime headers of the request.
     */
    void setHeaders(MimeHeaders headers) {
        this.headers = headers;
    }

    /**
     * Sets the SOAP message of this request.
     * 
     * @param message the SOAP message of this request.
     */
    void setMessage(SOAPMessage message) {
        this.message = message;
    }

    /**
     * Sets the source which initiated this request.
     * 
     * @param source the source which initiated this request.
     */
    void setSource(Object source) {
        this.source = source;
    }

}