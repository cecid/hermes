/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.listener;

import hk.hku.cecid.edi.as2.pkg.AS2Message;

/**
 * The AS2Response class represents a SOAP response. It is independent of which
 * transport protocol it is using and contains the SOAP message of the target
 * response.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class AS2Response {

    private AS2Message message;

    private Object      target;

    /**
     * Creates a new instance of AS2Response.
     */
    AS2Response() {
    }

    /**
     * Creates a new instance of AS2Response.
     * 
     * @param target the target that this response should be committed to.
     */
    AS2Response(Object target) {
        this.target = target;
    }

    /**
     * Gets the SOAP message of this response.
     * 
     * @return the SOAP message of this response.
     */
    public AS2Message getMessage() {
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
    public void setMessage(AS2Message message) {
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