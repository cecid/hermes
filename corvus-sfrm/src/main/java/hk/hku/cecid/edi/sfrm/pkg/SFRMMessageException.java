/**
 * Provide the core class for packing the sfrm message 
 * for messaging. 
 */
package hk.hku.cecid.edi.sfrm.pkg;

import hk.hku.cecid.piazza.commons.GenericException;

/**
 * SFRMMessageException represents an exception related to SFRM message.
 * 
 * @author Twinsen
 */
public class SFRMMessageException extends GenericException {

    /**
	 * Compiler Generated Serial Version ID.
	 */
	private static final long serialVersionUID = -1637480552988682018L;

	/**
     * Creates a new instance of AS2MessageException.
     */
    public SFRMMessageException() {
        super();
    }

    /**
     * Creates a new instance of AS2MessageException.
     * 
     * @param message the error message.
     */
    public SFRMMessageException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of AS2MessageException.
     * 
     * @param cause the cause of this exception.
     */
    public SFRMMessageException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of AS2MessageException.
     * 
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public SFRMMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}