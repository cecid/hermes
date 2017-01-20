package hk.hku.cecid.piazza.commons.module;

import hk.hku.cecid.piazza.commons.util.StringUtilities;

/**
 * ModuleException represents a module's runtime exception.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class ModuleException extends RuntimeException {

    /**
     * Creates a new instance of ModuleException.
     */
    public ModuleException() {
        super();
    }

    /**
     * Creates a new instance of ModuleException.
     * 
     * @param message the error message.
     */
    public ModuleException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of ModuleException.
     * 
     * @param cause the cause of this exception.
     */
    public ModuleException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of ModuleException.
     * 
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public ModuleException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Returns a string representation of this exception.
     * 
     * @return a string representation of this exception.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return StringUtilities.toString(this);
    }
}