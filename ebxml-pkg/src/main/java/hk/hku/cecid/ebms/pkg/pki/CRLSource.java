/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/CRLSource.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.util.Date;
/**
 * This is an abstract class for holding a X509 CRL instance. The functionality
 * of CRL is given by this class. The sub-classes will provide different 
 * initialization method for loading the CRL, for example, through a file
 * or through LDAP.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */
public abstract class CRLSource {

    /**
     * Internal storage of X509 CRL
     */
    protected X509CRL crl;

    /**
     * Indicates the CRL has been loaded correctly or not
     */
    protected boolean ready;

    /**
     * Default constructor. It initializes the object. But the object 
     * is still unusable until init() is called.
     */
    public CRLSource() {
        crl = null;
        ready = false;
    }

    /**
     * Initializes the object. The initialization procedure depends on 
     * the source of the CRL. So, we declare this method as abstract here,
     * leaving the sub-classes to concern about the initialization.
     *
     * @throws CRLException Initialization error occurs
     */
    public abstract void init() throws CRLException;

    /**
     * Gets the readiness of the object. The object will be ready for use
     * after init() is called. And the internal X509 CRL storage is populated.
     *
     * @return true if the object is ready for use; false if otherwise
     */
    public boolean isReady() {
        return ready && (crl != null);
    }

    /**
     * Verifies the CRL to check whether is is signed by the private key 
     * corresponding to the specified public key or not.
     *
     * @param pubKey the public key used to verify
     * @return true if the CRL is signed by the private key corresponding to
     *         <code>pubKey</code>; false if otherwise
     * @throws InitializationException the object is not yet initialized
     */
    public boolean verifySignature(PublicKey pubKey) 
        throws InitializationException {
        if (!isReady()) {
            throw new InitializationException("Not yet initialized.");
        }
        boolean ret = false;
        try {
            crl.verify(pubKey);
            ret = true;
        }
        catch (CRLException e) {}
        catch (NoSuchAlgorithmException e) {}
        catch (InvalidKeyException e) {}
        catch (NoSuchProviderException e) {}
        catch (SignatureException e) {}
        return ret;
    }

    /**
     * Verifies the CRL to check whether is is signed by the private key 
     * corresponding to the public key in the specified certificate or not.
     *
     * @param cert the certificate storing the public key to be used for
     *             verification
     * @return true if the CRL is signed by the private key corresponding to
     *         the public key stored in <code>cert</code>; false if otherwise
     * @throws InitializationException the object is not yet initialized
     */
    public boolean verifySignature(Certificate cert)
        throws InitializationException {
        if (!isReady()) {
            throw new InitializationException("Not yet initialized.");
        }
        return this.verifySignature(cert.getPublicKey());
    }

    /**
     * Verifies the CRL to check whether is is signed by the private key 
     * corresponding to the public key in the specified certificate or not.
     *
     * @param cert the certificate storing the public key to be used for
     *             verification
     * @return true if the CRL is signed by the private key corresponding to
     *         the public key stored in <code>cert</code>; false if otherwise
     * @throws InitializationException the object is not yet initialized
     */
    public boolean verifySignature(CertSource cert)
        throws InitializationException {
        if (!isReady()) {
            throw new InitializationException("Not yet initialized.");
        }
        return this.verifySignature(cert.getPublicKey());
    }

    /**
     * Checks the specified certificate against the CRL to see whether the 
     * certificate has been revoked or not.
     *
     * @param cert the certificate to be tested against the CRL
     * @return true if the specified certificate is revoked according to the
     *         CRL; false if otherwise
     * @throws InitializationException the object is not yet initialized
     */
    public boolean isRevoked(Certificate cert)
        throws InitializationException {
        if (!isReady()) {
            throw new InitializationException("Not yet initialized.");
        }
        return crl.isRevoked(cert);
    }

    /**
     * Checks the specified certificate against the CRL to see whether the 
     * certificate has been revoked or not.
     *
     * @param cert the certificate to be tested against the CRL
     * @return true if the specified certificate is revoked according to the
     *         CRL; false if otherwise
     * @throws InitializationException the object is not yet initialized
     */
    public boolean isRevoked(CertSource cert)
        throws InitializationException {
        if (!isReady()) {
            throw new InitializationException("Not yet initialized.");
        }
        return this.isRevoked(cert.getInternalCert());
    }

    /**
     * Gets the last update date of the CRL.
     *
     * @return the last update date
     * @throws InitializationException the object is not yet initialized
     */
    public Date getThisUpdate() throws InitializationException {
        if (!isReady()) {
            throw new InitializationException("Not yet initialized.");
        }
        return crl.getThisUpdate();
    }

    /**
     * Gets the next update date of the CRL.
     *
     * @return the next update date
     * @throws InitializationException the object is not yet initialized
     */
    public Date getNextUpdate() throws InitializationException {
        if (!isReady()) {
            throw new InitializationException("Not yet initialized.");
        }
        return crl.getNextUpdate();
    }

    /**
     * Gets the distinguished name (DN) of the issuer of the CRL.
     *
     * @return the DN of the issuer
     * @throws InitializationException the object is not yet initialized
     */
    public String getIssuer() throws InitializationException {
        if (!isReady()) {
            throw new InitializationException("Not yet initialized.");
        }
        return crl.getIssuerDN().getName();
    }
}
