package hk.hku.cecid.edi.sfrm.com;

import hk.hku.cecid.piazza.commons.GenericException;
/**
 * PayloadException represents all kinds of exception related to
 * SFRM payload.
 *
 * Creation Date: 6/3/2009
 *
 * @author Patrick Yip
 * @version 2.0.0
 */
public class PayloadException extends GenericException{
	/**
	 * Compiled-Generated Serial ID
	 */
	private static final long serialVersionUID = -5607622126197282308L;

	/**
     * Creates a new instance of SFRMException.
     */
    public PayloadException() {
        super();
    }

    /**
     * Creates a new instance of SFRMException.
     * 
     * @param message the error message.
     */
    public PayloadException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of SFRMException.
     * 
     * @param cause the cause of this exception.
     */
    public PayloadException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of SFRMException.
     * 
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public PayloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
