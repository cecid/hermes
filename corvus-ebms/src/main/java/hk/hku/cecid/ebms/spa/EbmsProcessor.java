package hk.hku.cecid.ebms.spa;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.module.ModuleException;
import hk.hku.cecid.piazza.commons.module.ModuleGroup;
import hk.hku.cecid.piazza.commons.module.SystemModule;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;
import hk.hku.cecid.piazza.commons.spa.Plugin;
import hk.hku.cecid.piazza.commons.spa.PluginException;
import hk.hku.cecid.piazza.commons.spa.PluginHandler;

/**
 * @author Donahue Sze
 *
 */
public class EbmsProcessor implements PluginHandler {
    
    /**
     * Outbox collector active task module ID name
     */
    public static String ACTIVE_MODULE_OUTBOX_COLLECTOR = "ebms.outbox-collector";
    
    /**
     * Inbox collector active task module ID name
     */
    public static String ACTIVE_MODULE_INBOX_COLLECTOR = "ebms.inbox-collector";
    
    /**
     * POP mail collector active task module ID name
     */
    public static String ACTIVE_MODULE_MAIL_COLLECTOR = "ebms.mail-collector";
    
    private static final String COMPONENT_KEYSTORE_MANAGER_FOR_SIGNATURE = "keystore-manager-for-signature";
    
    private static final String COMPONENT_KEYSTORE_MANAGER_FOR_DECRYPTION = "keystore-manager-for-decryption";
    
    private static ModuleGroup moduleGroup;
    
    public static SystemModule core;

    public void processActivation(Plugin plugin) throws PluginException {
    	
        String mgDescriptor = plugin.getParameters().getProperty("module-group-descriptor");
        moduleGroup = new ModuleGroup(mgDescriptor, plugin.getClassLoader());
        Sys.getModuleGroup().addChild(moduleGroup);

        core = moduleGroup.getSystemModule();
        moduleGroup.startActiveModules();
        
        if (core == null) {
            throw new PluginException("Ebms core system module not found");
        }
    }
    
    /**
     * @return the Ebms module group
     */
    public static ModuleGroup getModuleGroup() {
        if (moduleGroup == null) {
            throw new RuntimeException("Ebms module group not initialized");
        }
        else {
            return moduleGroup;
        }
    }
    
    public static KeyStoreManager getKeyStoreManagerForSignature() {
        KeyStoreManager m = (KeyStoreManager) core.getComponent(COMPONENT_KEYSTORE_MANAGER_FOR_SIGNATURE);
        if (m==null) {
            throw new ModuleException("Key store manager for signature not found");
        }
        else {
            return m;
        }
    }
    
    public static KeyStoreManager getKeyStoreManagerForDecryption() {
        KeyStoreManager m = (KeyStoreManager) core.getComponent(COMPONENT_KEYSTORE_MANAGER_FOR_DECRYPTION);
        if (m==null) {
            throw new ModuleException("Key store manager for decryption not found");
        }
        else {
            return m;
        }
    }

    /* (non-Javadoc)
     * @see hk.hku.cecid.piazza.commons.spa.PluginHandler#processDeactivation(hk.hku.cecid.piazza.commons.spa.Plugin)
     */
    public void processDeactivation(Plugin arg0) throws PluginException {
        moduleGroup.stopActiveModules();
    }
}
