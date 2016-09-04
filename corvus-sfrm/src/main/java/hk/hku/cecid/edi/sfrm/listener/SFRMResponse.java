/**
 * It provides the classes for the listening HTTP Request for 
 * incoming and outgoing, and the request and response model 
 * for SFRM.
 */
package hk.hku.cecid.edi.sfrm.listener;

/**
 * It provides the classes for the listening HTTP Request for 
 * incoming and outgoing, and the request and response model 
 * for SFRM.
 */
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;

/**
 * The SFRMResponse class represents a wrapper of HTTP response
 * wrapping the SFRMMessage inside.
 * 
 * @author 	Twinsen Tsang
 * @version	1.0.3
 */
public class SFRMResponse {

    private SFRMMessage message;

    private Object      target;

    /**
     * Creates a new instance of AS2Response.
     */
    SFRMResponse() {
    }

    /**
     * Creates a new instance of AS2Response.
     * 
     * @param target the target that this response should be committed to.
     */
    SFRMResponse(Object target) {
        this.target = target;
    }

    /**
     * Gets the SOAP message of this response.
     * 
     * @return the SOAP message of this response.
     */
    public SFRMMessage getMessage() {
        return message;
    }

    /**
     * Gets the target that this response should be committed to.
     * 
     * @return the target that this response should be committed to.
     */
    public Object getTarget() {
        return target;
    }

    /**
     * Sets the SOAP message of this response.
     * 
     * @param message the SOAP message of this response.
     */
    public void setMessage(SFRMMessage message) {
        this.message = message;
    }

    /**
     * Sets the target that this response should be committed to.
     * 
     * @param target the target that this response should be committed to.
     */
    void setTarget(Object target) {
        this.target = target;
    }
}