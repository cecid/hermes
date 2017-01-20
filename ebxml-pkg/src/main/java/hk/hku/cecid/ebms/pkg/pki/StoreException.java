/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/StoreException.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * kcyee [2002-05-03]
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
 * Exception class representing the error during storing the composite 
 * keystore.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */
public class StoreException extends Exception {

    /**
     * Construtor with message.
     *
     * @param msg the message of the exception
     */
    public StoreException(String msg) {
        super(msg);
    }
}
