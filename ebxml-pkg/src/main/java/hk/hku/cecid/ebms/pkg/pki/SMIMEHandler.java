/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/SMIMEHandler.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * achong [2002-08-01]
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

import java.security.Security;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
/**
It is the base class of SMIMEEncrypter and SMIMEDecrypter. The BouncyCastle 's
SMIME engine only works properly with BouncyCastle 's JCE provider. The subclass
should call the method initiate(), which add the BouncyCastle 's Provider and
setup which DataHandler for what types of content type.
*/
public class SMIMEHandler {

    private static boolean initiated;
        
    /**
    Add the BouncyCastle 's Provider and setup which DataHandler for what types 
    of content type.
    */
    static void initiate() {
        if (!initiated) {
            synchronized(SMIMEHandler.class) {
                if (!initiated) {
                    Security.addProvider(new BouncyCastleProvider());   
                    setupMailCommandMap();
                    initiated = true;
                }
            }
        }
    }

    /**
    Setup the MailCommandMap programmatically. It sets which ContentType 
    should be handled by which type of DataHandler.
    */
    private static void setupMailCommandMap() {
        MailcapCommandMap mailcap = (MailcapCommandMap)CommandMap.
        getDefaultCommandMap();
        mailcap.addMailcap("application/pkcs7-mime;; x-java-content-handler=" + 
        "org.bouncycastle.mail.smime.handlers.pkcs7_mime");
        mailcap.addMailcap("application/x-pkcs7-mime;; x-java-content-handler" +
        "=org.bouncycastle.mail.smime.handlers.x_pkcs7_mime");
        mailcap.addMailcap("application/pkcs7-signature;; x-java-content-" + 
        "handler=org.bouncycastle.mail.smime.handlers.pkcs7_signature");
        mailcap.addMailcap("application/x-pkcs7-signature;; x-java-content-" + 
        "handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_signature");
        mailcap.addMailcap("multipart/signed;; x-java-content-handler=" + 
        "org.bouncycastle.mail.smime.handlers.multipart_signed");
        CommandMap.setDefaultCommandMap(mailcap);
    }
}
