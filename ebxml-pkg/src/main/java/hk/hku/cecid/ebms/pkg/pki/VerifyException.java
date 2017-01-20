/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/VerifyException.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * kcyee [2002-05-17]
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
 * Exception class representing the error during verification of digital
 * signature.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */

public class VerifyException extends Exception {

    /**
     * Construtor with message.
     *
     * @param message the message of the exception
     */
    public VerifyException(String message) {
        super(message);
    }
}
