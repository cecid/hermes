/* =====
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/validation/ValidationException.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * frankielam [2002-11-14]
 *
 * Code reviewed by:
 *
 * username [YYYY-MM-DD]
 *
 * Remarks:
 *
 * =====
 */
package hk.hku.cecid.ebms.pkg.validation;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/** 
 * Base class for message validation exceptions. All classes extending this
 * class must be able to generate a SOAP message to indicate the error, but it
 * is not necessary a SOAP Fault message (e.g. an ebXML message containing
 * ErrorList element).
 * 
 * @author  Frankie Lam
 * @version $Revision 1.0$
 */
public abstract class ValidationException extends SOAPException {

    /** The error represented by this exception is unknown. */
    public static final int ERROR_UNKNOWN = 0;

    /** The error represented by this exception is caused by an incorrectly
     *  packaged ebXML message. */
    public static final int ERROR_EBXML = 1;

    /** The error represented by this exception is a SOAP Fault */
    public static final int ERROR_SOAP = 2;


    // Basic information about the error occurred. Derived classes may interpret
    // these fields in their own way.

    /** The type of error represented by this exception object. */
    protected final int errorType;

    /** Error code. */
    protected final String errorCode;

    /** A string describing the error occurred. */
    protected final String errorString;


    /** 
     * Constructs a <code>ValidationException</code> object. 
     * 
     * @param errorType     The type of error represented by this exception 
     *                      object.
     * @param errorCode     An error code in string to be processed by the
     *                      applications.
     * @param errorString   A human-readable description string.
     */
    public ValidationException(int errorType, String errorCode, 
                               String errorString) {
        super(errorCode + ": " + errorString);
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.errorString = errorString;
    }

    /** 
     * Get the SOAP message containing the error information.
     * 
     * @return <code>SOAPMessage</code> object containing the error information.
     */
    public abstract SOAPMessage getSOAPMessage();
}
