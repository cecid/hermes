/*
 * Created on Oct 15, 2004
 *
 */
package hk.hku.cecid.ebms.pkg;

import hk.hku.cecid.ebms.pkg.pki.CertResolver;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Iterator;

import javax.net.ssl.X509TrustManager;
import javax.xml.soap.SOAPException;

/**
 * @author Donahue Sze
 * 
 */
public class SignatureHandler {

    private static Object signLock = new Object();

    private static Object verifyLock = new Object();

    private EbxmlMessage message;

    private String username;

    private char[] password;

    private String keyStoreLocation;

    private PublicKey publicKey;

    private CertResolver certResolver;

    public SignatureHandler(EbxmlMessage message, String username, char[] password, String keyStoreLocation, X509TrustManager trustman) throws SignatureException {
        this.message = message;
        this.username = username;
        this.password = password;
        this.keyStoreLocation = keyStoreLocation;

        try {
            certResolver = new KeyStoreCertResolver(trustman);
        } catch (Exception e) {
            throw new SignatureException("Unable to create signature handler",
                    e);
        }
    }

    public SignatureHandler(EbxmlMessage message, final Certificate cert) throws SignatureException {
        this.message = message;
        this.certResolver = new CertResolver() {
            public Certificate[] resolve(Object obj) {
                return new Certificate[] { cert };
            }
        };
    }

    /**
     * Sign this <code>EbxmlMessage</code> with XML signature
     * 
     * @throws SOAPException
     * @throws SignatureException
     */
    public void sign() throws SOAPException, SignatureException {
        sign(null);
    }

    /**
     * Sign this <code>EbxmlMessage</code> with XML signature
     * 
     * @param algorithm
     *            Specifies the algorithm used to generate the digital
     *            signature. Refer to <a href=
     *            "http://www.w3.org/TR/2002/REC-xmldsig-core-20020212/#sec-AlgID">
     *            XML-Signature Syntax and Processing: Algorithm Identifiers and
     *            Implementation Requirements </a> for details.
     * @throws SOAPException
     * @throws SignatureException
     */
    public void sign(String algorithm) throws SOAPException, SignatureException {
        sign(null, null, false);
    }

    /**
     * Sign this <code>EbxmlMessage</code> with XML signature
     * 
     * @param algorithm
     *            Specifies the algorithm used to generate the digital
     *            signature. Refer to <a href=
     *            "http://www.w3.org/TR/2002/REC-xmldsig-core-20020212/#sec-AlgID">
     *            XML-Signature Syntax and Processing: Algorithm Identifiers and
     *            Implementation Requirements </a> for details.
     * @param signEnvelopeOnly
     *            whether it should sign the envelope only, without signing the
     *            payload.
     * @param digestAlgorithm
     *            Description of the Parameter
     * @throws SOAPException
     * @throws SignatureException
     */
    public void sign(String algorithm, String digestAlgorithm,
            boolean signEnvelopeOnly) throws SOAPException, SignatureException {
        synchronized (signLock) {
            Signature signature = Signature.newInstance(message);
            signature.sign(username, password, keyStoreLocation, algorithm,
                    digestAlgorithm, signEnvelopeOnly);
            message.getHeaderContainer().addExtensionElement(signature);
        }
    }

    /**
     * Verify the message using trusted keystore.
     * 
     * @return true if the digital signature is valid; false otherwise.
     * @throws SOAPException
     * @throws SignatureException
     */
    public boolean verify() throws SOAPException, SignatureException {
        synchronized (verifyLock) {
            boolean result = true;
            Iterator i = message.getHeaderContainer().getSignatures();
            if (i.hasNext()) {
                while (i.hasNext()) {
                    Signature sig = (Signature) i.next();
                    Signature signature = Signature.newInstance(message,
                            sig.soapEnvelope, sig.getSOAPElement());
                    result = result
                            && signature.verify(password, keyStoreLocation,
                                    certResolver, message.getDatasource());
                }
            } else {
                throw new SignatureException("No <"
                        + Signature.NAMESPACE_PREFIX_DS + ":"
                        + Signature.ELEMENT_SIGNATURE
                        + "> element is found to be verified!");
            }
            return result;
        }
    }

    public boolean verifyByPublicKey() throws SOAPException, SignatureException {
        synchronized (verifyLock) {
            boolean result = true;
            Iterator i = message.getHeaderContainer().getSignatures();
            if (i.hasNext()) {
                while (i.hasNext()) {
                    Signature sig = (Signature) i.next();
                    Signature signature = Signature.newInstance(message,
                            sig.soapEnvelope, sig.getSOAPElement());
                    //result = result && signature.verify(publicKey);
                    result = result
                            && signature.verify(null, null, certResolver,
                                    message.getDatasource());
                }
            } else {
                throw new SignatureException("No <"
                        + Signature.NAMESPACE_PREFIX_DS + ":"
                        + Signature.ELEMENT_SIGNATURE
                        + "> element is found to be verified!");
            }
            return result;
        }
    }

    private class KeyStoreCertResolver implements CertResolver {

        X509TrustManager manager;

        /**
         * @throws IOException
         * @throws CertificateException
         * @throws NoSuchAlgorithmException
         * @throws KeyStoreException
         * @throws NoSuchProviderException
         *  
         */
        public KeyStoreCertResolver(X509TrustManager trustman) throws NoSuchAlgorithmException,
                CertificateException, IOException, KeyStoreException,
                NoSuchProviderException {
            manager = trustman;
        }

        /*
         * (non-Javadoc)
         * 
         * @see hk.hku.cecid.ebms.pkg.pki.CertResolver#resolve(java.lang.Object)
         */
        public Certificate[] resolve(Object obj) {
            return manager.getAcceptedIssuers();
        }

    }
}