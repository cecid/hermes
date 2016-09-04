/**
 * Contains the SPA plugin hook point for the piazza corvus
 * and the constant table.
 */
package hk.hku.cecid.edi.sfrm.spa;

import hk.hku.cecid.piazza.commons.GenericException;

/**
 * SFRMException represents all kinds of exception related to
 * SFRM processing.
 *
 * Creation Date: 27/9/2006
 *
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	1.0.0
 */
public class SFRMException extends GenericException {

    /**
	 * Compiled-Generated Serial ID
	 */
	private static final long serialVersionUID = -5607381126197282308L;

	/**
     * Creates a new instance of SFRMException.
     */
    public SFRMException() {
        super();
    }

    /**
     * Creates a new instance of SFRMException.
     * 
     * @param message the error message.
     */
    public SFRMException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of SFRMException.
     * 
     * @param cause the cause of this exception.
     */
    public SFRMException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of SFRMException.
     * 
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public SFRMException(String message, Throwable cause) {
        super(message, cause);
    }
}