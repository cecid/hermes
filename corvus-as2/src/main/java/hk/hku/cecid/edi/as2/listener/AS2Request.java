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
 * The AS2Request class represents a SOAP request.
 * It is independent of which transport protocol it is using
 * and contains the SOAP message of the original request.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class AS2Request {

    private AS2Message message;

    private Object      source;

    /**
     * Creates a new instance of AS2Request.
     */
    AS2Request() {
    }

    /**
     * Creates a new instance of AS2Request.
     *
     * @param source the source which initiated this request.
     */
    AS2Request(Object source) {
        this.source = source;
    }

    /**
     * Gets the SOAP message of this request.
     * 
     * @return the SOAP message of this request.
     */
    public AS2Message getMessage() {
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
     * Sets the SOAP message of this request.
     * 
     * @param message the SOAP message of this request.
     */
    void setMessage(AS2Message message) {
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