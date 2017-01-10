package hk.hku.cecid.piazza.commons.util;

/**
 * UtilitiesException represents all kinds of exception occuring in the utility functions.
 * 
 * @author Hugo Y. K. Lam
 */
public class UtilitiesException extends hk.hku.cecid.piazza.commons.GenericException {

    /**
     * Creates a new instance of UtilitiesException.
     */
    public UtilitiesException() {
        super();
    }

    /**
     * Creates a new instance of UtilitiesException.
     * 
     * @param message the error message.
     */
    public UtilitiesException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of UtilitiesException.
     * 
     * @param cause the cause of this exception.
     */
    public UtilitiesException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of UtilitiesException.
     * 
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public UtilitiesException(String message, Throwable cause) {
        super(message, cause);
    }
}