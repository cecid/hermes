package hk.hku.cecid.edi.as2.pkg;

import hk.hku.cecid.piazza.commons.GenericException;

/**
 * AS2MessageException represents an exception related to AS2 message.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class AS2MessageException extends GenericException {

    /**
     * Creates a new instance of AS2MessageException.
     */
    public AS2MessageException() {
        super();
    }

    /**
     * Creates a new instance of AS2MessageException.
     * 
     * @param message the error message.
     */
    public AS2MessageException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of AS2MessageException.
     * 
     * @param cause the cause of this exception.
     */
    public AS2MessageException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of AS2MessageException.
     * 
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public AS2MessageException(String message, Throwable cause) {
        super(message, cause);
    }
}