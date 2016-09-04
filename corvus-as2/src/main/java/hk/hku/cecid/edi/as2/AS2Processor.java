/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2;

import hk.hku.cecid.edi.as2.dao.AS2DAOHandler;
import hk.hku.cecid.edi.as2.module.IncomingMessageProcessor;
import hk.hku.cecid.edi.as2.module.MessageRepository;
import hk.hku.cecid.edi.as2.module.PayloadRepository;
import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.module.ModuleException;
import hk.hku.cecid.piazza.commons.module.ModuleGroup;
import hk.hku.cecid.piazza.commons.module.SystemModule;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;
import hk.hku.cecid.piazza.commons.spa.Plugin;
import hk.hku.cecid.piazza.commons.spa.PluginException;
import hk.hku.cecid.piazza.commons.spa.PluginHandler;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;


/**
 * AS2Processor
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class AS2Processor implements PluginHandler {

    private static ModuleGroup moduleGroup;
    
    public static SystemModule core;
    
    private static final String COMPONENT_KEYSTORE_MANAGER = "keystore-manager";
    
    private static final String COMPONENT_INCOMING_MSG_PROCESSOR = "incoming-message-processor";

    private static final String COMPONENT_INCOMING_PAYLOAD_REPOSITORY = "incoming-payload-repository";

    private static final String COMPONENT_OUTGOING_PAYLOAD_REPOSITORY = "outgoing-payload-repository";

    private static final String COMPONENT_ORIGINAL_MESSAGE_REPOSITORY = "original-message-repository";

    private static final String PROPERTY_MAILCAPS = "/as2/mailcaps/cap";
    
    /**
     * @see hk.hku.cecid.piazza.commons.spa.PluginHandler#processActivation(hk.hku.cecid.piazza.commons.spa.Plugin)
     */
    public void processActivation(Plugin plugin) throws PluginException {
        try {
            init(plugin.getParameters().getProperty("module-group-descriptor"),
                    plugin.getClassLoader());
        }
        catch (Exception e) {
            throw new PluginException("Unable to initialize AS2 processor", e);
        }
    }
    
    /**
     * @see hk.hku.cecid.piazza.commons.spa.PluginHandler#processDeactivation(hk.hku.cecid.piazza.commons.spa.Plugin)
     */
    public void processDeactivation(Plugin plugin) throws PluginException {
        try {
            destroy();
        }
        catch (Exception e) {
            throw new PluginException("Unable to shutdown AS2 processor", e);
        }
    } 
    
    private static void destroy() {
        if (moduleGroup != null) {
            moduleGroup.stopActiveModules();
            Sys.getModuleGroup().removeChild(moduleGroup);
        }
    }
    
    private static void init(String moduleGroupDescriptor, ClassLoader loader) {
        if (moduleGroupDescriptor == null) {
            moduleGroupDescriptor = AS2Processor.class.getPackage().getName()
                .replace('.', '/') + "/conf/as2.module-group.xml";
        }
        if (loader == null) {
            loader = AS2Processor.class.getClassLoader();
        }
        
        moduleGroup = new ModuleGroup(moduleGroupDescriptor, loader);
        Sys.getModuleGroup().addChild(moduleGroup);
        core = getSystemModule();

        initActivationFramework();

        recover();
        
        moduleGroup.startActiveModules();
        
        core.log.info("AS2 server started up successfully");
    }
    
    private static void initActivationFramework() {
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        String[] caps = core.properties.getProperties(PROPERTY_MAILCAPS);
        for (int i=0; i<caps.length; i++) {
            mc.addMailcap(caps[i]);
        }
        CommandMap.setDefaultCommandMap(mc);
    }
    
    private static void recover() {
        try {
            AS2DAOHandler daoHandler = new AS2DAOHandler(core.dao);
            int rs = daoHandler.createMessageDAO().recoverProcessingMessages();
            core.log.info("Total number of messages recovered: "+rs);
        }
        catch (Exception e) {
            core.log.error("Unable to recover server status", e);
        }
    }
    
    public static SystemModule getSystemModule() {
        SystemModule module = getModuleGroup().getSystemModule();
        if (module == null) {
            throw new ModuleException("AS2 core system module not found");
        }
        else {
            return module;
        }
    }
    
    public static ModuleGroup getModuleGroup() {
        if (moduleGroup == null) {
            throw new ModuleException("AS2 module group not initialized");
        }
        else {
            return moduleGroup;
        }
    }
    
    public static IncomingMessageProcessor getIncomingMessageProcessor() {
        IncomingMessageProcessor p = (IncomingMessageProcessor) getSystemModule().getComponent(COMPONENT_INCOMING_MSG_PROCESSOR);
        if (p==null) {
            throw new ModuleException("AS2 incoming message processor not found");
        }
        else {
            return p;
        }
    }
    
    public static KeyStoreManager getKeyStoreManager() {
        KeyStoreManager m = (KeyStoreManager) getSystemModule().getComponent(COMPONENT_KEYSTORE_MANAGER);
        if (m==null) {
            throw new ModuleException("AS2 key store manager not found");
        }
        else {
            return m;
        }
    }
    
    public static PayloadRepository getIncomingPayloadRepository() {
        PayloadRepository p = (PayloadRepository) getSystemModule().getComponent(COMPONENT_INCOMING_PAYLOAD_REPOSITORY);
        if (p==null) {
            throw new ModuleException("AS2 incoming payload repository not found");
        }
        else {
            return p;
        }
    }
    
    public static PayloadRepository getOutgoingPayloadRepository() {
        PayloadRepository p = (PayloadRepository) getSystemModule().getComponent(COMPONENT_OUTGOING_PAYLOAD_REPOSITORY);
        if (p==null) {
            throw new ModuleException("AS2 outgoing payload repository not found");
        }
        else {
            return p;
        }
    }
    
    public static MessageRepository getMessageRepository() {
        MessageRepository p = (MessageRepository) getSystemModule().getComponent(COMPONENT_ORIGINAL_MESSAGE_REPOSITORY);
        if (p==null) {
            throw new ModuleException("AS2 message repository not found");
        }
        else {
            return p;
        }
    }
}
