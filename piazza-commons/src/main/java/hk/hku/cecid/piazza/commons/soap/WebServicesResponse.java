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
 * The WebServicesResponse class represents a Web Services response.
 * It is independent of which access protocol it is using
 * and contains the bodies of the response message.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class WebServicesResponse {

    private Element[]      bodies;

    private Object      target;

    /**
     * Creates a new instance of WebServicesResponse.
     */
    WebServicesResponse() {
    }

    /**
     * Creates a new instance of WebServicesResponse.
     * 
     * @param target the target that this response should be committed to.
     */
    WebServicesResponse(Object target) {
        this.target = target;
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
     * Sets the target that this response should be committed to.
     * 
     * @param target the target that this response should be committed to.
     */
    void setTarget(Object target) {
        this.target = target;
    }

    /**
     * Sets the body elements of the Web Services response message. 
     * 
     * @param bs the bodies of the Web Services response message.
     */
    public void setBodies(Element[] bs) {
        bodies = bs;
    }

    /**
     * Gets the body elements of the Web Services response message.
     * 
     * @return the bodies of the Web Services response message.
     */
    public Element[] getBodies() {
        return bodies;
    }
}