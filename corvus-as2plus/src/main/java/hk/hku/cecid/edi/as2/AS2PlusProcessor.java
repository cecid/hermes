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
import hk.hku.cecid.edi.as2.module.OutgoingMessageProcessor;
import hk.hku.cecid.edi.as2.module.PayloadRepository;
import hk.hku.cecid.piazza.commons.module.ModuleException;
import hk.hku.cecid.piazza.commons.module.ModuleGroup;
import hk.hku.cecid.piazza.commons.module.PluginProcessor;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;
import hk.hku.cecid.piazza.commons.spa.Plugin;
import hk.hku.cecid.piazza.commons.spa.PluginException;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;


/**
 * AS2Processor
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class AS2PlusProcessor extends PluginProcessor {

    private static AS2PlusProcessor processor;
    
    private static ModuleGroup moduleGroup;
    
    private static final String COMPONENT_KEYSTORE_MANAGER = "keystore-manager";
    
    private static final String COMPONENT_INCOMING_MSG_PROCESSOR = "incoming-message-processor";
    
    private static final String COMPONENT_OUTGOING_MSG_PROCESSOR = "outgoing-message-processor";

    // TODO: will be removed after removing outgoing payload repository
    private static final String COMPONENT_OUTGOING_PAYLOAD_REPOSITORY = "outgoing-payload-repository";

    private static final String PROPERTY_MAILCAPS = "/as2/mailcaps/cap";
    
    public static AS2PlusProcessor getInstance() {
    	if (processor == null)
    		throw new ModuleException("ASProcessor not initialized");
    	
    	return processor;
    }
    
	@Override
	protected ModuleGroup getModuleGroupImpl() {
		return moduleGroup;
	}

	@Override
	protected void setModuleGroupImpl(ModuleGroup moduleGroup) {
		this.moduleGroup = moduleGroup;
	}
	
    /**
     * @see hk.hku.cecid.piazza.commons.spa.PluginHandler#processActivation(hk.hku.cecid.piazza.commons.spa.Plugin)
     */
    public void processActivation(Plugin plugin) throws PluginException {
    	processor = this;
    	
    	super.processActivation(plugin);

    	// TODO: will be removed later, it has to be initiated after processActivation()
    	//core = getSystemModule();
    	
        initActivationFramework();

        recover();
        
        this.getLogger().info("AS2 plugin started up successfully");
    }
    
    
    private void initActivationFramework() {
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        String[] caps = this.getProperties(PROPERTY_MAILCAPS);
        for (int i=0; i<caps.length; i++) {
            mc.addMailcap(caps[i]);
        }
        CommandMap.setDefaultCommandMap(mc);
    }
    
    private void recover() {
        try {
            AS2DAOHandler daoHandler = new AS2DAOHandler(this.getDAOFactory());
            int rs = daoHandler.createMessageDAO().recoverProcessingMessages();
            getLogger().info("Total number of messages recovered: "+rs);
        }
        catch (Exception e) {
        	getLogger().error("Unable to recover server status", e);
        }
    }
    
    public IncomingMessageProcessor getIncomingMessageProcessor() {
    	return (IncomingMessageProcessor) getSystemModule().getComponent(COMPONENT_INCOMING_MSG_PROCESSOR);
    }
    
    public OutgoingMessageProcessor getOutgoingMessageProcessor() {
    	return (OutgoingMessageProcessor) getSystemModule().getComponent(COMPONENT_OUTGOING_MSG_PROCESSOR);
    }
    
    public KeyStoreManager getKeyStoreManager() {
    	return (KeyStoreManager) getSystemModule().getComponent(COMPONENT_KEYSTORE_MANAGER);
    }
    
    // TODO: will be removed after moving getContentType() to other class
    public PayloadRepository getOutgoingPayloadRepository() {
        return (PayloadRepository) getSystemModule().getComponent(COMPONENT_OUTGOING_PAYLOAD_REPOSITORY);
    }
    
}
