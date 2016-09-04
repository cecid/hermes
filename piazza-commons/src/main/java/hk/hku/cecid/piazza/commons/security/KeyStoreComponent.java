/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.security;

import hk.hku.cecid.piazza.commons.module.Component;
import hk.hku.cecid.piazza.commons.util.Instance;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.util.Properties;


/**
 * KeyStoreComponent is a module component which embeds a key store. 
 * 
 * @author Hugo Y. K. Lam
 *
 */
abstract class KeyStoreComponent extends Component {

    /**
     * The embeded key store.
     */
    KeyStore keyStore;
    
    /**
     * The key store provider.
     */
    Object provider;

    /**
     * The key store location. 
     */
    String location;
    
    /**
     * The key store type.
     */
    String storeType;
    
    /**
     * The alias name.
     */
    String alias;
    

    /**
     * The key password
     */
    char[] keyPass;
    
    /**
     * The key store password
     */
    char[] storePass;

    /**
     * Creates a new instance of KeyStoreComponent.
     */
    public KeyStoreComponent() {
    }
    
    /**
     * Initializes this key store component.
     * <p>
     * Component parameters:
     * </p>
     * <ul>
     *   <li>keystore-location: the key store location
     *   <li>keystore-password: the key store password
     *   <li>key-alias: the alias name
     *   <li>key-password: the key password
     *   <li>keystore-type: the key store type.
     *   <li>keystore-provider: the key store provider
     * </ul>
     * 
     * @throws KeyStoreManagementException if unable to initialize the key store component.
     * @see #init(String, String, String, String, String, Object) 
     * @see hk.hku.cecid.piazza.commons.module.Component#init()
     */
    protected void init() throws KeyStoreManagementException {
        Properties params = getParameters();
        init(   params.getProperty("keystore-location"),
                params.getProperty("keystore-password"),
                params.getProperty("key-alias"),
                params.getProperty("key-password"),
                params.getProperty("keystore-type"),
                params.getProperty("keystore-provider")
                );
    }
    
    /**
     * Initializes this key store component.
     * 
     * @param keyStore
     * @param alias the alias name.
     * @param keyPass the key password.
     * @throws KeyStoreManagementException if unable to initialize the key store component. 
     */
    protected void init(KeyStore keyStore, String alias, String keyPass) 
            throws KeyStoreManagementException {
        if (keyStore == null) {
            throw new KeyStoreManagementException("No key store specified for initialization");
        }
        this.keyStore = keyStore;
        init(null, null, alias, keyPass, null, null);
    }
    
    /**
     * Initializes this key store component.
     * 
     * @param location the key store location.
     * @param storePass the key store password.
     * @param alias the alias name.
     * @param keyPass the key password.
     * @param storeType the key store type.
     * @param provider the key store provider.
     * @throws KeyStoreManagementException if unable to initialize the key store component. 
     */
    protected void init(String location, String storePass, String alias, 
                        String keyPass, String storeType, Object provider) 
            throws KeyStoreManagementException {
        this.location = location;
        this.alias = alias==null? "mykey":alias;
        this.storePass = StringUtilities.toCharArray(storePass);
        this.keyPass = StringUtilities.toCharArray(keyPass);
        this.storeType = storeType;
        this.provider = provider;
        load();
    }
    
    /**
     * Loads the key store.
     * 
     * @throws KeyStoreManagementException if unable to loads the key store.
     */
    private void load() throws KeyStoreManagementException {
        if (keyStore == null) {
            try {
                if (storeType==null) {
                    storeType = KeyStore.getDefaultType();
                }
                if (provider == null) {
                    keyStore = KeyStore.getInstance(storeType);
                }
                else {
                    Instance secProviderInstance = new Instance(provider); 
                    Provider secProvider = (Provider)secProviderInstance.getObject();
                    Security.addProvider(secProvider);
                    keyStore = KeyStore.getInstance(storeType, secProvider);
                }
                
                InputStream ins = getModule()==null? new FileInputStream(location):getModule().getResourceAsStream(location);
                if (ins == null) {
                    throw new KeyStoreManagementException("No key store found: " + location);
                }
                else {
                    keyStore.load(ins, storePass);
                    ins.close();
                    ins = null;
                }
            }
            catch (Exception e) {
                throw new KeyStoreManagementException("Unable to initialize the key store", e);
            }
        }
    }
}