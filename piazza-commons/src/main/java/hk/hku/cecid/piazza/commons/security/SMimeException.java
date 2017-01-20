package hk.hku.cecid.piazza.commons.security;

/**
 * SMimeException represents all kinds of exception related to SMIME.
 * 
 * @author Hugo Y. K. Lam
 */
public class SMimeException extends hk.hku.cecid.piazza.commons.GenericException {

    /**
     * Creates a new instance of SMimeException.
     */
    public SMimeException() {
        super();
    }

    /**
     * Creates a new instance of SMimeException.
     * 
     * @param message the error message.
     */
    public SMimeException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of SMimeException.
     * 
     * @param cause the cause of this exception.
     */
    public SMimeException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of SMimeException.
     * 
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public SMimeException(String message, Throwable cause) {
        super(message, cause);
    }
}