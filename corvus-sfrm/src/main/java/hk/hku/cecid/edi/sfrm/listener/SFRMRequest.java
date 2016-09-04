/**
 * It provides the classes for the listening HTTP Request for 
 * incoming and outgoing, and the request and response model 
 * for SFRM.
 */
package hk.hku.cecid.edi.sfrm.listener;

import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;

/**
 * The SFRMRequest class represents a wrapper of HTTP request
 * wrapping the SFRMMessage inside.
 * 
 * @author Twinsen Tsang
 * @version	1.0.0  
 * @since  	1.0.0
 */
public class SFRMRequest {

	/**
	 * The sfrm message for the request.
	 */
    private SFRMMessage message;

    private Object      source;

    /**
     * Creates a new instance of SFRM Request.
     */
    SFRMRequest() {
    }

    /**
     * Creates a new instance of SFRM Request.
     *
     * @param source the source which initiated this request.
     */
    SFRMRequest(Object source) {
        this.source = source;
    }
    
    /**
     * Sets the SOAP message of this request.
     * 
     * @param message the SOAP message of this request.
     */
    void setMessage(SFRMMessage message) {
        this.message = message;
    }
    
    /**
     * Gets the SOAP message of this request.
     * 
     * @return the SOAP message of this request.
     */
    public SFRMMessage getMessage() {
        return message;
    }

    /**
     * Sets the source which initiated this request.
     * 
     * @param source the source which initiated this request.
     */
    void setSource(Object source) {
        this.source = source;
    }

    /**
     * Gets the source which initiated this request.
     * 
     * @return the source which initiated this request.
     */
    public Object getSource() {
        return source;
    }   
}