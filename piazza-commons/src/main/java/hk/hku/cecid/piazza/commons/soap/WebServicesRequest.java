/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.soap;

import org.w3c.dom.Element;

/**
 * The WebServicesRequest class represents a Web Services request.
 * It is independent of which access protocol it is using
 * and contains the bodies of the request message.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class WebServicesRequest {

    private Element[]      bodies;

    private Object      source;

    /**
     * Creates a new instance of WebServicesRequest.
     */
    WebServicesRequest() {
    }

    /**
     * Creates a new instance of WebServicesRequest.
     *
     * @param source the source which initiated this request.
     */
    WebServicesRequest(Object source) {
        this.source = source;
    }

    /**
     * Sets the body elements of the Web Services request message. 
     * 
     * @param bs the bodies of the Web Services request message.
     */
    void setBodies(Element[] bs) {
        bodies = bs;
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
     * Gets the body elements of the Web Services request message.
     * 
     * @return the bodies of the Web Services request message.
     */
    public Element[] getBodies() {
        return bodies;
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