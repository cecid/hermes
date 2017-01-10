/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/SignException.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * kcyee [2002-05-16]
 *
 * Code reviewed by:
 *
 * username [YYYY-MM-DD]
 *
 * Remarks:
 *
 * =====
 */

package hk.hku.cecid.ebms.pkg.pki;

/**
 * Exception class representing the error during signing the digital
 * signature.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */

public class SignException extends Exception {

    /**
     * Construtor with message.
     *
     * @param message the message of the exception
     */
    public SignException(String message) {
        super(message);
    }
}
