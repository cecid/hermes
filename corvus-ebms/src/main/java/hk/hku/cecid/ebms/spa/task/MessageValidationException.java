/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.task;

/**
 * DeliveryException represents all kinds of exception occuring in delivery.
 * 
 * @author Hugo Y. K. Lam
 */
public class MessageValidationException extends
        hk.hku.cecid.piazza.commons.GenericException {

    /**
     * Creates a new instance of DeliveryException.
     */
    public MessageValidationException() {
        super();
    }

    /**
     * Creates a new instance of DeliveryException.
     * 
     * @param message
     *            the error message.
     */
    public MessageValidationException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of DeliveryException.
     * 
     * @param cause
     *            the cause of this exception.
     */
    public MessageValidationException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of DeliveryException.
     * 
     * @param message
     *            the error message.
     * @param cause
     *            the cause of this exception.
     */
    public MessageValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}