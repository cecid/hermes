package hk.hku.cecid.piazza.commons.module;

import hk.hku.cecid.piazza.commons.GenericException;

/**
 * ComponentException represents an exception related to a component.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class ComponentException extends GenericException {

    /**
     * Creates a new instance of ComponentException.
     */
    public ComponentException() {
        super();
    }

    /**
     * Creates a new instance of ComponentException.
     * 
     * @param message the error message.
     */
    public ComponentException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of ComponentException.
     * 
     * @param cause the cause of this exception.
     */
    public ComponentException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of ComponentException.
     * 
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public ComponentException(String message, Throwable cause) {
        super(message, cause);
    }
}