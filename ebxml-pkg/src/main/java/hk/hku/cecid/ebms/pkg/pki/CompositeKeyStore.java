/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/CompositeKeyStore.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * kcyee [2002-05-02]
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
/**
 * Composite keystore which manages keystores of different types. A typical
 * Java keystore supports only one keystore type per file. That will be
 * inconvenient for applications to manage several types of keystore. Also,
 * this composite keystore supports managing multiple keystore files. This 
 * can be viewed as a keystore registry, that is, this object manages a pool
 * of keystore files.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */
public class CompositeKeyStore {

    /**
     * Internal storage of the keystore file information
     */
    protected Hashtable storage;

    /**
     * Internal storage of the aliases inside the keystore file
     */
    protected Hashtable cache;

    /**
     * Internal storage of the keystore object
     */
    protected Vector keystores;

    /**
     * Default constructor. The internal variables are being initialized.
     */
    public CompositeKeyStore() {
        storage = new Hashtable();
        cache = null;
        keystores = new Vector();
    }

    /**
     * Adds a keystore file to the keystore management pool.
     *
     * @param keyFile the name of the keystore file
     * @param type the type of the keystore
     * @param password the password for accessing the keystore
     */
    public void addKeyStoreFile(String keyFile, String type, char[] password) {
        if (keyFile != null) {
            File f = new File(keyFile);
            if (f != null && f.exists()) {
                addKeyStoreFile(f, type, password);
            }
        }
    }

    /**
     * Adds a keystore file to the keystore management pool.
     *
     * @param keyFile the keystore file
     * @param type the type of the keystore
     * @param password the password for accessing the keystore
     */
    protected void addKeyStoreFile(File keyFile, String type, char[] password) {
        KeyStoreFileProp ksp = new KeyStoreFileProp(type, password);
        try {
            storage.put(keyFile.getCanonicalPath(), ksp);
        }
        catch (IOException e) {}
    }

    /**
     * Gets the first KeyStore object from the keystore management pool.
     *
     * @return the first KeyStore object from the keystore management pool
     */
    public KeyStore getKeyStore() {
        if (cache == null) {
            loadCache();
        }
        if (keystores.size() > 0) {
            return (KeyStore) keystores.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * Removes a keystore file from the keystore management pool.
     *
     * @param keyFile the name of the keystore file
     */
    public void removeKeyStoreFile(String keyFile) {
        removeKeyStoreFile(new File(keyFile));
    }

    /**
     * Removes a keystore file from the keystore management pool.
     *
     * @param keyFile the keystore file
     */
    protected void removeKeyStoreFile(File keyFile) {
        try {
            storage.remove(keyFile.getCanonicalPath());
        }
        catch (IOException e) {}
    }

    /**
     * Gets an instance of the keystore of correct type. This function
     * will consider the Java version and determine whether to use
     * JSSE or not. For Java version 1.4 or above, JSSE is built in.
     * So, no need to call an external provider to create an instance
     * of PKCS#12 formatted keystore. Otherwise, JSSE should be used, and
     * we make use of dynamic binding to load the JSSE library.
     *
     * @param fileName the keystore file name to load
     * @param ksp other keystore parameters for loading
     * @return keystore instance of the correct type
     */
    protected KeyStore loadKeyStore(String fileName, KeyStoreFileProp ksp) {
        KeyStore ks = null;
        if (ksp.getType() == null) {
            KeyStoreFileProp ksp_new = 
                new KeyStoreFileProp("JKS", ksp.getPassword());
            ks = loadKeyStore(fileName, ksp_new);
            if (ks == null) {
                ksp_new = new KeyStoreFileProp("PKCS12", ksp.getPassword());
                ks = loadKeyStore(fileName, ksp_new);
            }
            return ks;
        }
        else if (ksp.getType().toUpperCase().equals("JKS")) {
            try {
                ks = KeyStore.getInstance("JKS");
            }
            catch (KeyStoreException e) {}
        }
        else if (ksp.getType().toUpperCase().equals("PKCS12")) {
            /*
            if (isUsingJSSE()) {
                try {
                    Class clsProv = Class.forName(
                        "com.sun.net.ssl.internal.ssl.Provider");
                    Constructor c = clsProv.getConstructor(null);
                    Provider provider = (Provider) c.newInstance(null);
                    if (Security.getProvider(provider.getName()) == null) {
                        Security.addProvider(provider);
                    }
                }
                catch (ClassNotFoundException e) {}
                catch (NoSuchMethodException e) {}
                catch (InstantiationException e) {}
                catch (IllegalAccessException e) {}
                catch (InvocationTargetException e) {}

                try {
                    ks = KeyStore.getInstance("PKCS12", "SunJSSE");
                }
                catch (NoSuchProviderException e) {}
                catch (KeyStoreException e) {}
            }
            else {
                try {
                    ks = KeyStore.getInstance("PKCS12");
                }
                catch (KeyStoreException e) {}
            }
            */

            try {
                Class clsProv = Class.forName(
                    "org.bouncycastle.jce.provider.BouncyCastleProvider");
                Constructor c = clsProv.getConstructor();
                Provider provider = (Provider) c.newInstance();
                if (Security.getProvider(provider.getName()) == null) {
                    Security.addProvider(provider);
                }
            }
            catch (ClassNotFoundException e) {}
            catch (NoSuchMethodException e) {}
            catch (InstantiationException e) {}
            catch (IllegalAccessException e) {}
            catch (InvocationTargetException e) {}

            try {
                ks = KeyStore.getInstance("PKCS12", "BC");
            }
            catch (NoSuchProviderException e) {}
            catch (KeyStoreException e) {}

        }
        if (ks != null) {
            try {
                ks.load(new FileInputStream(fileName), ksp.getPassword());
                return ks;
            }
            catch (IOException e) {}
            catch (NoSuchAlgorithmException e) {}
            catch (CertificateException e) {}
        }
        return null;
    }

    /**
     * Loads the keystores pointed by this composite keystore into memory
     * and create a caching of aliases.
     */
    protected void loadCache() {
        cache = new Hashtable();

        Enumeration fileNames = storage.keys();
        while (fileNames.hasMoreElements()) {
            String fileName = (String) fileNames.nextElement();
            KeyStoreFileProp ksp = (KeyStoreFileProp) storage.get(fileName);

            KeyStore ks = loadKeyStore(fileName, ksp);
            keystores.add(ks);
            if (ks != null) {
                try {
                    Enumeration ks_alias = ks.aliases();
                    while (ks_alias.hasMoreElements()) {
                        cache.put(ks_alias.nextElement(), ks);
                    }
                }
                catch (KeyStoreException e) {}
            }
        }
    }

    /**
     * Gets all the aliases of the keystores pointed by this composite
     * keystore.
     *
     * @return an enumeration of string, holding the aliases of the keys
     */
    public Enumeration aliases() {
        if (cache == null) {
            loadCache();
        }
        return cache.keys();
    }

    /**
     * Determines whether a given alias exists in one of the keystores
     * pointed by this composite keystore or not.
     *
     * @param alias the alias of the key/certificate
     * @return true if the alias exists in one of the keystores, false
     &         otherwise
     */
    public boolean containsAlias(String alias) {
        if (cache == null) {
            loadCache();
        }
        return (cache.get(alias) != null);
    }

    /**
     * Gets the certificate named by the given alias, from the collection
     * of keystores pointed by this composite keystore.
     *
     * @param alias the alias of the key/certificate
     * @return the certificate named by the given alias, null if not found
     * @throws KeyStoreException the keystore is corrupted
     */
    public Certificate getCertificate(String alias) throws KeyStoreException {
        if (cache == null) {
            loadCache();
        }
        KeyStore ks = (KeyStore) cache.get(alias);
        if (ks != null) {
            return ks.getCertificate(alias);
        }
        return null;
    }

    /**
     * Gets the alias of the specified certificate.
     *
     * @param cert the certificate
     * @return the alias of the certificate, if the certificate can be found
     *         in the collection of keystores pointed by this composite
     *         keystore. Otherwise, null will be returned
     */
    public String getCertificateAlias(Certificate cert) {
        if (cache == null) {
            loadCache();
        }
        Enumeration keyStores = cache.elements();
        while (keyStores.hasMoreElements()) {
            KeyStore ks = (KeyStore) keyStores.nextElement();
            try {
                String alias = ks.getCertificateAlias(cert);
                if (alias != null) {
                    return alias;
                }
            }
            catch (KeyStoreException e) {}
        }
        return null;
    }

    /**
     * Gets the certificate chain by the specified alias.
     *
     * @param alias the alias of the key/certificate
     * @return the certificate chain by the specified alias, null if not found
     * @throws KeyStoreException the keystore is corrupted
     */
    public Certificate[] getCertificateChain(String alias) 
        throws KeyStoreException {
        if (cache == null) {
            loadCache();
        }
        KeyStore ks = (KeyStore) cache.get(alias);
        if (ks != null) {
            return ks.getCertificateChain(alias);
        }
        return null;
    }

    /**
     * Gets the creation date of the key/certificate by the specified alias.
     *
     * @param alias the alias of the key/certificate
     * @return the creation date of the key/certificate by the specified alias, 
     *         null if not found
     * @throws KeyStoreException the keystore is corrupted
     */
    public Date getCreationDate(String alias) throws KeyStoreException {
        if (cache == null) {
            loadCache();
        }
        KeyStore ks = (KeyStore) cache.get(alias);
        if (ks != null) {
            return ks.getCreationDate(alias);
        }
        return null;
    }

    /**
     * Gets the key by the specified alias. A password should be given also
     * to retrieve the key.
     *
     * @param alias the alias of the key/certificate
     * @param password the password to retrieve the key
     * @return the key specified by the alias, null if not found
     * @throws KeyStoreException the keystore is corrupted
     * @throws NoSuchAlgorithmException the keystore cannot be read
     * @throws UnrecoverableKeyException the keystore cannot be read
     */
    public Key getKey(String alias, char[] password) throws 
        KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        if (cache == null) {
            loadCache();
        }
        KeyStore ks = (KeyStore) cache.get(alias);
        if (ks != null) {
            return ks.getKey(alias, password);
        }
        return null;
    }

    /**
     * Determines whether the specified alias is specifying a certificate 
     * or not.
     *
     * @param alias the alias of the key/certificate
     * @throws KeyStoreException the keystore is corrupted
     */
    public boolean isCertificateEntry(String alias) throws KeyStoreException {
        if (cache == null) {
            loadCache();
        }
        KeyStore ks = (KeyStore) cache.get(alias);
        if (ks != null) {
            return ks.isCertificateEntry(alias);
        }
        return false;
    }

    /**
     * Determines whether the specified alias is specifying a key 
     * or not.
     *
     * @param alias the alias of the key/certificate
     * @throws KeyStoreException the keystore is corrupted
     */
    public boolean isKeyEntry(String alias) throws KeyStoreException {
        if (cache == null) {
            loadCache();
        }
        KeyStore ks = (KeyStore) cache.get(alias);
        if (ks != null) {
            return ks.isKeyEntry(alias);
        }
        return false;
    }

    /**
     * Gets the total number of keys/certificates in all the keystores
     * pointed by this composite keystore.
     *
     * @return the total number of keys/certificates
     */
    public int size() {
        if (cache == null) {
            loadCache();
        }
        return cache.size();
    }

    /**
     * Loads the composite keystore from a persistent file in the file
     * system.
     *
     * @param storeFileName the name of the composite keystore persistent file
     * @throws InitializationException the persistent file is corrupted
     */
    public void load(String storeFileName) throws InitializationException {
        load(new File(storeFileName));
    }

    /**
     * Loads the composite keystore from a persistent file in the file
     * system.
     *
     * @param storeFile the composite keystore persistent file
     * @throws InitializationException the persistent file is corrupted
     */
    public void load(File storeFile) throws InitializationException {
        try {
            ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(storeFile));
            storage = (Hashtable) ois.readObject();
            ois.close();
        }
        catch (FileNotFoundException e) {
            storage = null;
            throw new InitializationException("FileNotFound Exception\n" 
                + e.getMessage());
        }
        catch (IOException e) {
            storage = null;
            throw new InitializationException("IO Exception\n" 
                + e.getMessage());
        }
        catch (ClassNotFoundException e) {
            storage = null;
            throw new InitializationException("ClassNotFound Exception\n" 
                + e.getMessage());
        }
    }

    /**
     * Stores the composite keystore to a persistent file in the file
     * system.
     *
     * @param storeFileName the name of the composite keystore persistent file
     * @throws StoreException the composite keystore is not successfully stored
     */
    public void store(String storeFileName) throws StoreException {
        store(new File(storeFileName));
    }

    /**
     * Stores the composite keystore to a persistent file in the file
     * system.
     *
     * @param storeFile the composite keystore persistent file
     * @throws StoreException the composite keystore is not successfully stored
     */
    public void store(File storeFile) throws StoreException {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(storeFile));
            oos.writeObject(storage);
            oos.close();
        }
        catch (FileNotFoundException e) {
            throw new StoreException("FileNotFound Exception\n" 
                + e.getMessage());
        }
        catch (IOException e) {
            throw new StoreException("IO Exception\n" 
                + e.getMessage());
        }
    }

}
