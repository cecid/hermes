/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/SignatureException.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * cyng [2002-03-21]
 *
 * Code reviewed by:
 *
 * username [YYYY-MM-DD]
 *
 * Remarks:
 *
 * =====
 */

package hk.hku.cecid.ebms.pkg;

/**
 * A class used to encapsulate all kinds of exception thrown during the
 * XML signature signing and verification process.
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */

public class SignatureException extends Exception {

    /** Construct a <code>SignatureException</code> with the specified error
        message
    */
    public SignatureException(String message) {
        super(message);
    }

    public SignatureException(String message, Throwable e) {
        super(message, e);
    }
}
