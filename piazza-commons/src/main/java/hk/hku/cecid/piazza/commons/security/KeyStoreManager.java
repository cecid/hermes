/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.security;

import hk.hku.cecid.piazza.commons.util.ArrayUtilities;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;


/**
 * KeyStoreManager manages a key store and provides convenient methods such as 
 * method that retrieves an X509Certificate or retrieves a private key. 
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class KeyStoreManager extends KeyStoreComponent {

    /**
     * Creates a new instance of KeyStoreManager.
     */
    public KeyStoreManager() {
    }
    
    /**
     * Creates a new instance of KeyStoreManager.
     * 
     * @param keyStore the initialized keystore to be managed.
     * @throws KeyStoreManagementException if the specified key store is null. 
     */
    public KeyStoreManager(KeyStore keyStore) throws KeyStoreManagementException {
        this(keyStore, null, null); 
    }
    
    /**
     * Creates a new instance of KeyStoreManager.
     *
     * @param keyStore the initialized keystore to be managed.
     * @param alias the alias name associating with the managed key.
     * @param keyPass the key password.
     * @throws KeyStoreManagementException if the specified key store is null. 
     */
    public KeyStoreManager(KeyStore keyStore, String alias, String keyPass) throws KeyStoreManagementException {
        super.init(keyStore, alias, keyPass); 
    }
    
    /**
     * Creates a new instance of KeyStoreManager.
     * 
     * @param location the key store location.
     * @param storePass the key store password.
     * @throws KeyStoreManagementException if unable to initialize the key store with the given paramemeters.
     */
    public KeyStoreManager(String location, String storePass) throws KeyStoreManagementException {
        this(location, storePass, null, null);
    }

    /**
     * Creates a new instance of KeyStoreManager.
     * 
     * @param location the key store location.
     * @param storePass the key store password.
     * @param alias the alias name.
     * @param keyPass the key password.
     * @throws KeyStoreManagementException if unable to initialize the key store with the given paramemeters.
     */
    public KeyStoreManager(String location, String storePass, String alias, String keyPass) throws KeyStoreManagementException {
        this(location, storePass, alias, keyPass, null, null);
    }

    /**
     * Creates a new instance of KeyStoreManager.
     * 
     * @param location the key store location.
     * @param storePass the key store password.
     * @param storeType the key store type.
     * @param provider the key store provider.
     * @throws KeyStoreManagementException if unable to initialize the key store with the given paramemeters.
     */
    public KeyStoreManager(String location, String storePass, String storeType, Object provider) throws KeyStoreManagementException {
        this(location, storePass, null, null, storeType, provider);
    }

    /**
     * Creates a new instance of KeyStoreManager.
     * 
     * @param location the key store location.
     * @param storePass the key store password.
     * @param alias the alias name.
     * @param keyPass the key password.
     * @param storeType the key store type.
     * @param provider the key store provider.
     * @throws KeyStoreManagementException if unable to initialize the key store with the given paramemeters.
     */
    public KeyStoreManager(String location, String storePass, String alias, String keyPass, String storeType, Object provider) throws KeyStoreManagementException {
        init(location, storePass, alias, keyPass, storeType, provider);
    }

    /**
     * Checks if the managed certificate is trusted.
     * 
     * @return true if the managed certificate is trusted.
     */
    public boolean isCertificateTrusted() {
        return isCertificateTrusted(alias);
    }
    
    /**
     * Checks if the certificate asscoiated with the given alias name is trusted.
     * 
     * @return true the certificate is trusted.
     */
    public boolean isCertificateTrusted(String alias) {
        try {
            return keyStore.isCertificateEntry(alias);
        }
        catch (KeyStoreException kse) {
            // the keystore should have been initialized
            return false;
        }
    }
    
    /**
     * Gets the managed certificate chain.
     * 
     * @return the managed certificate chain.
     */
    public Certificate[] getCertificateChain() {
        return getCertificateChain(alias);
    }

    /**
     * Gets the certificate chain asscoiated with the given alias name.
     *
     * @param alias the alias name. 
     * @return the certificate chain.
     */
    public Certificate[] getCertificateChain(String alias) {
        try {
            return keyStore.getCertificateChain(alias);
        }
        catch (KeyStoreException kse) {
            // the keystore should have been initialized
            return null;
        }
    }

    /**
     * Gets the managed X509 certificate chain.
     * 
     * @return the managed certificate chain.
     */
    public X509Certificate[] getX509CertificateChain() {
        return getX509CertificateChain(alias);
    }

    /**
     * Gets the X509 certificate chain asscoiated with the given alias name.
     *
     * @param alias the alias name. 
     * @return the certificate chain.
     */
    public X509Certificate[] getX509CertificateChain(String alias) {
        try {
            Certificate[] certs = keyStore.getCertificateChain(alias);
            ArrayList xcerts = new ArrayList();
            for (int i=0; certs!=null && i<certs.length; i++) {
                xcerts.add(certs[i]);
            }
            return (X509Certificate[])xcerts.toArray(new X509Certificate[]{});
        }
        catch (KeyStoreException kse) {
            // the keystore should have been initialized
            return null;
        }
    }

    /**
     * Gets the managed alias.
     * 
     * @return the managed alias.
     */
    public String getAlias() {
        return alias;
    }
    
    /**
     * Gets all the aliases in the managed key store.
     * 
     * @return all the aliases in the managed key store.
     */
    public String[] getAliases() {
        try {
            return (String[])ArrayUtilities.toArray(keyStore.aliases());
        }
        catch (KeyStoreException e) {
            // the keystore should have been initialized
            return null;
        }
    }
    
    /**
     * Gets the managed certificate.
     * 
     * @return the managed certificate.
     */
    public Certificate getCertificate() {
        return getCertificate(alias);
    }
    
    /**
     * Gets the certificate asscoiated with the given alias name.
     * 
     * @param alias the alias name.
     * @return the certificate.
     */
    public Certificate getCertificate(String alias) {
        try {
            return keyStore.getCertificate(alias);
        }
        catch (KeyStoreException kse) {
            // the keystore should have been initialized
            return null;
        }
    }
    
    /**
     * Gets the managed X509 certificate.
     * 
     * @return the managed X509 certificate.
     * @throws ClassCastException if the certificate is not of the X509 type.
     */
    public X509Certificate getX509Certificate() {
        return getX509Certificate(alias);
    }

    /**
     * Gets the managed X509 certificate.
     * 
     * @param alias the alias name.
     * @return the managed X509 certificate.
     * @throws ClassCastException if the certificate is not of the X509 type.
     */
    public X509Certificate getX509Certificate(String alias) {
        return (X509Certificate)getCertificate(alias);
    }

    /**
     * Gets the managed public key.
     * 
     * @return the public key.
     */
    public PublicKey getPublicKey() {
        return getCertificate().getPublicKey();
    }
    
    /**
     * Gets the managed private key.
     * 
     * @return the private key.
     * @throws NoSuchAlgorithmException if the algorithm for recovering the key cannot be found.
     * @throws UnrecoverableKeyException if the key cannot be recovered (e.g., the given password is wrong).
     */
    public PrivateKey getPrivateKey() throws NoSuchAlgorithmException, UnrecoverableKeyException {
        try {
            return (PrivateKey) keyStore.getKey(alias, keyPass);
        }
        catch (KeyStoreException kse) {
            // the keystore should have been initialized
            return null;
        }
    }
    
    /**
     * Gets the managed key store.
     * 
     * @return the key store.
     */
    public KeyStore getKeyStore() {
        return keyStore;
    }
}