package hk.hku.cecid.piazza.commons.util;

/**
 * InstanceException represents all kinds of exception in creating an instance or invoking a method by reflection.
 * 
 * @author Hugo Y. K. Lam
 */
public class InstanceException extends hk.hku.cecid.piazza.commons.GenericException {

    /**
     * Creates a new instance of InstanceException.
     */
    public InstanceException() {
        super();
    }

    /**
     * Creates a new instance of InstanceException.
     * 
     * @param message the error message.
     */
    public InstanceException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of InstanceException.
     * 
     * @param cause the cause of this exception.
     */
    public InstanceException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of InstanceException.
     * 
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public InstanceException(String message, Throwable cause) {
        super(message, cause);
    }
}