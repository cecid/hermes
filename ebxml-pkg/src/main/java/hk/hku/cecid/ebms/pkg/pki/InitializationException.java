/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/InitializationException.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * kcyee [2002-04-30]
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
 * Exception class representing the error during initialization.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */
public class InitializationException extends Exception {
    
    private Exception nestedException;
    
    /**
     * Construtor with message.
     *
     * @param msg the message of the exception
     */
    public InitializationException(String msg) {
        super(msg);
    }

    /**
    Instantiate the exception with a nested exception
    @param msg the message of the exception
    @param e the exception to be wrapped
    */
    public InitializationException(String msg, Exception e) {
        super(msg);
        nestedException = e;
    }

    /**
    @return the nested exception
    */
    public Exception getNestedException() {
        return nestedException;
    }

    
    /**
    It overrides the getMessage() method so it also reports the nested exception
    , if it exists.
    @return the exception message.
    */
    public String getMessage() {
        if (nestedException == null) {
            return super.getMessage();
        }
        else {
            return super.getMessage() + ". The nested Exception is " + 
            nestedException.toString(); 
        }
    }    
}
