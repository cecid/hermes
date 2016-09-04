/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.security;

import java.net.Socket;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;

/**
 * KeyStoreKeyManager implements javax.net.ssl.X509KeyManager, which manages a 
 * given key store of X509 certificate-based key pairs and authenticates the 
 * local side of a secure socket. 
 *
 * @author Hugo Y. K. Lam
 */
public class KeyStoreKeyManager extends KeyStoreComponent implements X509KeyManager {
    
    /**
     * Creates a new instance of KeyStoreKeyManager.
     */
    public KeyStoreKeyManager() {
    }
    
    /**
     * Creates a new instance of KeyStoreKeyManager.
     * 
     * @param keyman the key store manager used for authentication.
     * @throws KeyStoreManagementException if the specified key store manager is null.
     */
    public KeyStoreKeyManager(KeyStoreManager keyman)
            throws KeyStoreManagementException {
        if (keyman==null) {
            throw new KeyStoreManagementException("KeyStoreManager is null");
        }
        super.init(keyman.keyStore, keyman.alias, String.valueOf(keyman.keyPass));
    }

    /**
     * Creates a new instance of KeyStoreKeyManager.
     * 
     * @param keyStore the initialized key store used for authentication.
     * @param alias the alias name associated with the key.
     * @param password the key password.
     * @throws KeyStoreManagementException if the specified key store is null.
     */
    public KeyStoreKeyManager(KeyStore keyStore, String alias, String password)
            throws KeyStoreManagementException {
        super.init(keyStore, alias, password);
    }

    /**
     * Chooses an alias to authenticate the client side of a secure socket. 
     * This method always returns the predefined alias. 
     * 
     * @param keyType the key algorithm type name(s), 
     *        ordered with the most-preferred key type first.
     * @param issuers the list of acceptable CA issuer subject names 
     *        or null if it does not matter which issuers are used.
     * @param socket the socket to be used for this connection or null.
     * @return the alias name.
     * @see javax.net.ssl.X509KeyManager#chooseClientAlias(java.lang.String[], java.security.Principal[], java.net.Socket)
     */
    public String chooseClientAlias(String[] keyType, Principal[] issuers,
            Socket socket) {
        return alias;
    }

    /**
     * Chooses an alias to authenticate the server side of a secure socket. 
     * This method always returns the predefined alias. 
     * 
     * @param keyType the key algorithm type name.
     * @param issuers the list of acceptable CA issuer subject names 
     *        or null if it does not matter which issuers are used.
     * @param socket the socket to be used for this connection or null.
     * @return the alias name.
     * @see javax.net.ssl.X509KeyManager#chooseServerAlias(java.lang.String, java.security.Principal[], java.net.Socket)
     */
    public String chooseServerAlias(String keyType, Principal[] issuers,
            Socket socket) {
        return alias;
    }

    /**
     * Gets the aliases for authenticating the client side of a secure socket. 
     * This method always returns the predefined alias. 
     * 
     * @param keyType the key algorithm type name.
     * @param issuers the list of acceptable CA issuer subject names 
     *        or null if it does not matter which issuers are used.
     * @return the aliases for authenticating the client side of a secure socket.
     * @see javax.net.ssl.X509KeyManager#getClientAliases(java.lang.String, java.security.Principal[])
     */
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return new String[]{alias};
    }
     
    /**
     * Gets the aliases for authenticating the server side of a secure socket. 
     * This method always returns the predefined alias. 
     * 
     * @param keyType the key algorithm type name.
     * @param issuers the list of acceptable CA issuer subject names 
     *        or null if it does not matter which issuers are used.
     * @return the aliases for authenticating the server side of a secure socket.
     * @see javax.net.ssl.X509KeyManager#getServerAliases(java.lang.String, java.security.Principal[])
     */
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return new String[]{alias};
    }

    /**
     * Gets the certificate chain associated with the given alias.
     * 
     * @param alias the alias name.
     * @return the certificate chain.
     * @see javax.net.ssl.X509KeyManager#getCertificateChain(java.lang.String)
     */
    public X509Certificate[] getCertificateChain(String alias) {
        try {
            Certificate[] certs = keyStore.getCertificateChain(alias);
            X509Certificate[] xcerts = new X509Certificate[certs.length];
            System.arraycopy(certs, 0, xcerts, 0, certs.length);
            return xcerts;
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to retrieve certificate chain", e);
        }
    }

    /**
     * Gets the key associated with the given alias.
     * 
     * @param alias the alias name.
     * @return the private key.
     * @throws RuntimeException if unable to retrieve the private key. 
     * @see javax.net.ssl.X509KeyManager#getPrivateKey(java.lang.String)
     */
    public PrivateKey getPrivateKey(String alias) {
        try {
            return (PrivateKey)keyStore.getKey(alias, keyPass);
        } catch (Exception e) {
            throw new RuntimeException("Unable to retrieve private key", e);
        }
    }
}