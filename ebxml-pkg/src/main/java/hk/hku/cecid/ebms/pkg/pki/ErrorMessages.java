/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/ErrorMessages.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * kcyee [2003-03-24]
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

import java.util.Iterator;
import java.util.TreeMap;
/**
 * A class holding error codes and the corresponding error messages.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */
public class ErrorMessages {

    public static final int ERR_PKI_UNKNOWN_ERROR = 10200;
    public static final int ERR_PKI_INVALID_KEYSTORE = 10201;
    public static final int ERR_PKI_CANNOT_ENCRYPT = 10202;
    public static final int ERR_PKI_CANNOT_DECRYPT = 10203;
    public static final int ERR_PKI_CANNOT_SIGN = 10204;
    public static final int ERR_PKI_VERIFY_SIGNATURE_FAILED = 10205;

    protected static TreeMap errorMsg;
    protected static boolean isConfigured = false;

    protected static synchronized void configure() {

        if (isConfigured) return;

        errorMsg = new TreeMap();

        load(ERR_PKI_INVALID_KEYSTORE, "Invalid keystore");
        load(ERR_PKI_CANNOT_ENCRYPT, "Cannot encrypt message");
        load(ERR_PKI_CANNOT_DECRYPT, "Cannot decrypt message");
        load(ERR_PKI_CANNOT_SIGN, "Cannot sign message");
        load(ERR_PKI_VERIFY_SIGNATURE_FAILED, 
            "Verification of signature failed");


    }

    protected static void load(int code, String msg) {
        errorMsg.put(new Integer(code), msg);
    }

    public static String getMessage(int code) {
        return getMessage(code, null, "");
    }

    public static String getMessage(int code, String extraMsg) {
        return getMessage(code, null, extraMsg);
    }

    public static String getMessage(int code, Throwable e) {
        return getMessage(code, e, "");
    }

    public static String getMessage(int code, Throwable e, String extraMsg) {
        configure();
    
        StringBuffer sb = new StringBuffer();
        sb.append("[").append(code).append("] ");
        String err = (String) errorMsg.get(new Integer(code));
        if (err == null) {
            sb.append("Unknown error");
        }
        else {
            sb.append(err);
        }
        if (!extraMsg.equals("")) {
            sb.append(" - ").append(extraMsg);
        }
        if (e != null) {
            sb.append("\nException: ").append(e.getClass().getName());
            sb.append("\nMessage: ").append(e.getMessage());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        configure();
        Iterator keys = errorMsg.keySet().iterator();
        while (keys.hasNext()) {
            Integer err = (Integer) keys.next();
            String msg = (String) errorMsg.get(err);
            System.out.println(err.intValue() + "\t" + msg);
        }

    }
}

