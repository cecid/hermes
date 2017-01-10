/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/CertPathVerifier.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * kcyee [2002-06-25]
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

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import org.apache.log4j.Logger;
/**
 * This class wraps the certificate path verification routine into a 
 * separate static method. This is useful when JDK1.3 is used, the cert
 * path verification is skipped. And the JDK1.4 specific classes will not
 * be loaded, as they are all called in this class.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */
public class CertPathVerifier {

    /**
     * Logger
     */
    protected static Logger logger = Logger.getLogger(CertPathVerifier.class);

    /**
     * Verifies the specified certificate chain against the trusted anchors.
     * The trusted anchors contains all public certificate that is trusted.
     * This method will make use of JDK1.4's utilities to verify the 
     * certificate chain.
     *
     * @param certs the certificate chain being verified
     * @param trusted the keystore storing the trusted anchors.
     * @return true if verification is succeeded; false otherwise
     */
    public static boolean verify(java.security.cert.Certificate[] certs,
            CompositeKeyStore trusted) {

        try {
            CertPathBuilder certPathBuilder = 
                CertPathBuilder.getInstance("PKIX");
    
            X509CertSelector targetConstraints = new X509CertSelector();
    
            for (int i=0; i < certs.length; i++) {
                targetConstraints.setSubject(
                    ((X509Certificate) certs[i])
                    .getSubjectX500Principal().getEncoded());
            } 

            KeyStore trustAnchorsKS = trusted.getKeyStore();
            if (trustAnchorsKS == null) {
                logger.debug("trustAnchorsKS is null");
                return false;
            }

            PKIXBuilderParameters params = new PKIXBuilderParameters(
                trustAnchorsKS, targetConstraints);
        
            ArrayList certsList = new ArrayList();
            for (int i=0; i < certs.length; i++) {
                certsList.add(certs[i]);
            }
            CollectionCertStoreParameters ccsp = 
                new CollectionCertStoreParameters();
            CertStore store = CertStore.getInstance("Collection", ccsp);
            params.addCertStore(store);

            CertPath certPath = 
                certPathBuilder.build(params).getCertPath();
        }
        catch (NoSuchAlgorithmException e) {
            String err = ErrorMessages.getMessage(
                ErrorMessages.ERR_PKI_VERIFY_SIGNATURE_FAILED, e);
            logger.debug(err);
            return false;
        }
        catch (IOException e) {
            String err = ErrorMessages.getMessage(
                ErrorMessages.ERR_PKI_VERIFY_SIGNATURE_FAILED, e);
            logger.debug(err);
            return false;
        }
        catch (KeyStoreException e) {
            String err = ErrorMessages.getMessage(
                ErrorMessages.ERR_PKI_VERIFY_SIGNATURE_FAILED, e);
            logger.debug(err);
            return false;
        }
        catch (CertPathBuilderException e) {
            String err = ErrorMessages.getMessage(
                ErrorMessages.ERR_PKI_VERIFY_SIGNATURE_FAILED, e);
            logger.debug(err);
            return false;
        }
        catch (InvalidAlgorithmParameterException e) {
            String err = ErrorMessages.getMessage(
                ErrorMessages.ERR_PKI_VERIFY_SIGNATURE_FAILED, e);
            logger.debug(err);
            return false;
        }

        return true;
    }
}
