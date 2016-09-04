/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.security;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.X509TrustManager;

/**
 * This class implements the javax.net.ssl.X509TrustManager, which trusts a
 * Certificate Chain if any of the certificate in the certificate chain is
 * stored in the KeyStore.
 *
 * @author Bob P. Y. Koon
 */
public class KeyStoreTrustManager extends KeyStoreComponent implements X509TrustManager {

    /**
     * Creates a new instance of KeyStoreTrustManger.
     */
    public KeyStoreTrustManager() {
    }

    /**
     * Creates a new instance of KeyStoreTrustManger.
     * 
     * @param keyman the trusted key store manager.
     * @throws KeyStoreManagementException if the specified key store manager is null.
     */
    public KeyStoreTrustManager(KeyStoreManager keyman) throws KeyStoreManagementException {
        if (keyman==null) {
            throw new KeyStoreManagementException("KeyStoreManager is null");
        }
        super.init(keyman.keyStore, null, null);
    }

    /**
     * Creates a new instance of KeyStoreTrustManger.
     * 
     * @param keyStore the initialized trusted key store.
     * @throws KeyStoreManagementException if the specified key store is null.
     */
    public KeyStoreTrustManager(KeyStore keyStore) throws KeyStoreManagementException {
        if (keyStore==null) {
            throw new KeyStoreManagementException("KeyStore is null");
        }
        super.init(keyStore, null, null);
    }
    
    /**
     * Checks if any certificate in the certificate chain is stored in the key store.
     * 
     * @param chain the certificate chain.
     * @return true if any certificate in the certificate chain is stored in the key store.
     */
    private boolean isChainTrusted(X509Certificate[] chain) {
        try {
            for (int i = chain.length - 1; i >= 0; i-- ) {
                if (keyStore.getCertificateAlias(chain[i]) != null) {
                    return true;
                }
            }
        } catch(KeyStoreException e) {
            return false;
        }
        return false;
    }

    /**
     * Checks if any certificate in the certificate chain is stored in the key store.
     * 
     * @param chain the certificate chain.
     * @return true if any certificate in the certificate chain is stored in the key store.
     * @throws IllegalArgumentException if null or zero-length chain is passed in 
     *          for the chain parameter or if null or zero-length string is passed in 
     *          for the authType parameter. 
     * @throws CertificateException if the certificate chain is not trusted by this TrustManager.
     */
    private void checkTrusted(X509Certificate[] chain)
            throws CertificateException {
        if (chain == null || chain.length == 0) {
            throw new IllegalArgumentException("Null or zero length chain");
        }
        if (!isChainTrusted(chain)) {
            throw new CertificateException("Certificate chain not trusted");
        }
    }

    /**
     * Checks if the client is trusted. It trusts the certificate chain if the embeded 
     * key store contains one of the certificate in the chain.
     * 
     * @param chain the peer certificate chain.
     * @param authType the key exchange algorithm used.
     * @throws IllegalArgumentException if null or zero-length chain is passed in 
     *          for the chain parameter or if null or zero-length string is passed in 
     *          for the authType parameter. 
     * @throws CertificateException if the certificate chain is not trusted by this TrustManager.
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
     */
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        checkTrusted(chain);
    }
    
    /**
     * Checks if the server is trusted. It trusts the certificate chain if the embeded 
     * key store contains one of the certificate in the chain.
     * 
     * @param chain the peer certificate chain.
     * @param authType the key exchange algorithm used.
     * @throws IllegalArgumentException if null or zero-length chain is passed in 
     *          for the chain parameter or if null or zero-length string is passed in 
     *          for the authType parameter. 
     * @throws CertificateException if the certificate chain is not trusted by this TrustManager.
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
     */
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        checkTrusted(chain);
    }

    /**
     * Returns an array of certificate authority certificates 
     * which are stored in the embeded key store.
     * 
     * @return a non-null (possibly empty) array of acceptable CA issuer certificates.
     */
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] certs = null;
        try {
            // See how many certificates are in the keystore.
            int numberOfEntry = keyStore.size();
            // If there are any certificates in the keystore.
            if(numberOfEntry > 0) {
                // Create an array of X509Certificates
                certs = new X509Certificate[numberOfEntry];

                // Get all of the certificate alias out of the keystore.
                Enumeration aliases = keyStore.aliases();

                // Retrieve all of the certificates out of the keystore
                // via the alias name.
                int i = 0;
                while (aliases.hasMoreElements()) {
                    certs[i] = (X509Certificate) keyStore.getCertificate(
                            (String) aliases.nextElement());
                    i++;
                }
            }
        } catch(KeyStoreException e) {
            certs = null;
        }
        return certs;
    }
}
