package hk.hku.cecid.edi.sfrm.spa;

import java.util.HashMap;
import java.util.List;

import hk.hku.cecid.piazza.commons.module.ModuleException;
import hk.hku.cecid.piazza.commons.module.ModuleGroup;
import hk.hku.cecid.piazza.commons.module.PluginProcessor;
import hk.hku.cecid.piazza.commons.os.OSManager;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;
import hk.hku.cecid.piazza.commons.spa.Plugin;
import hk.hku.cecid.piazza.commons.spa.PluginException;

import hk.hku.cecid.edi.sfrm.handler.AcknowledgementHandler;
import hk.hku.cecid.edi.sfrm.handler.MessageStatusQueryHandler;
import hk.hku.cecid.edi.sfrm.handler.SFRMExternalRequestHandler;
import hk.hku.cecid.edi.sfrm.handler.SFRMMessageHandler;
import hk.hku.cecid.edi.sfrm.handler.SFRMMessageSegmentHandler;
import hk.hku.cecid.edi.sfrm.handler.SFRMPartnershipHandler;
import hk.hku.cecid.edi.sfrm.handler.IncomingMessageHandler;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.com.PackagedPayloadsRepository;
import hk.hku.cecid.edi.sfrm.com.PayloadsRepository;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;

/**
 * The SFRM SPA plugin processor for initiating and containing the references to all SFRM components.<br><br>
 * 
 * Creation Date: 27/9/2006
 * 
 * @author Twinsen, Philip
 * @version 2.0.0
 * @since	1.5
 */
public class SFRMProcessor extends PluginProcessor {
	
	protected static ModuleGroup moduleGroup;
	private static SFRMProcessor processor;

	public static SFRMProcessor getInstance() throws ModuleException{
		if (processor == null)
			throw new ModuleException("SFRMProcessor didn't initialized");			
		return processor;
	}
	
	protected ModuleGroup getModuleGroupImpl(){
		return moduleGroup;
	}
	
	protected void setModuleGroupImpl(ModuleGroup mg){
		moduleGroup = mg;
	}
	
	public void processActivation(Plugin plugin) throws PluginException {
		processor = this;
		super.processActivation(plugin);
//		suspendProcessingMessage();
//		adjust();
	}
	
	/**
	 * Suspend the message which is in processing and segmenting status in the previous SFRM running session
	 * @throws PluginException
	 */
	private void suspendProcessingMessage() throws PluginException{
		getLogger().debug("enter the suspend message");
		//Retrieve the processing message
		SFRMMessageHandler msgHandler = getMessageHandler();
		try{
			List psMsgs = msgHandler.retrieveMessages(SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGS_PROCESSING);
			List msgs = msgHandler.retrieveMessages(SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGS_SEGMENTING);
			msgs.addAll(psMsgs);
			
			SFRMExternalRequestHandler reqHandler = getExternalRequestHandler();
			for(int i=0; msgs.size() > i ;i++){
				SFRMMessageDVO msgDVO = (SFRMMessageDVO) msgs.get(i);
				SFRMProcessor.getInstance().getLogger().debug("msg id: " + msgDVO.getMessageId() + " need to be suspend");
				reqHandler.suspendMessage(msgDVO.getMessageId());
			}
		}catch(Exception e){
			throw new PluginException("Error when suspending the processing and segmenting message from previous SFRM session", e);
		}
	}

	public SFRMMessageHandler getMessageHandler() {
		return (SFRMMessageHandler)getSystemComponent("message-handler");
	}
	
	public SFRMMessageSegmentHandler getMessageSegmentHandler(){
		return (SFRMMessageSegmentHandler)getSystemComponent("message-segment-handler");
	}
	
	public IncomingMessageHandler getIncomingMessageHandler(){
		return (IncomingMessageHandler)getSystemComponent("incoming-message-handler");
	}
	
	public AcknowledgementHandler getAcknowledgementHandler(){
		return (AcknowledgementHandler)getSystemComponent("acknowledgement-handler");
	}
	
	public SFRMExternalRequestHandler getExternalRequestHandler(){
		return (SFRMExternalRequestHandler)getSystemComponent("external-request-handler");
	}

	public MessageStatusQueryHandler getMessageSpeedQueryHandler(){
		return (MessageStatusQueryHandler)getSystemComponent("message-status-query-handler");
	}
	
	public PayloadsRepository getIncomingRepository() {
		return (PayloadsRepository)getSystemComponent("incoming-payload-repository");
	}
	
	public PackagedPayloadsRepository getOutgoingRepository() {
		return (PackagedPayloadsRepository)getSystemComponent("outgoing-payload-repository");
	}
	
	public KeyStoreManager getKeyStoreManager() {
		return (KeyStoreManager)getSystemComponent("keystore-manager");
	}
	
	public SFRMPartnershipHandler getPartnershipHandler() {
		return (SFRMPartnershipHandler)getSystemComponent("partnership-handler");
	}
	
	public OSManager getOSManager(){
		return (OSManager)getSystemComponent("os-manager");
	}
	
	//TODO: will review the need of locking mechanism 
	
	/**
	 * The internal guard lock for each message. 
	 */
	private static transient HashMap guardLock = new HashMap(); 
			
	/**
	 * [@SYNCRHONIZED] Create a Global lock for a particular key.<br/><br/>
	 * 
	 * @param key
	 */
	public static synchronized Object createLock(String key){
		Object obj = new Object();
		guardLock.put(key, obj);
		return obj;
	}

	/**
	 * [@SYNCRHONIZED] Get a global lock for a particular key.  
	 */
	public static synchronized Object getLock(String key){
		return guardLock.get(key);
	}
	
	/**
	 * [@SYNCRHONIZED] Remove a global lock for a particular key.
	 * 
	 * @param key
	 */
	public static synchronized void	removeLock(String key){
		guardLock.remove(key);
	}
	
//	private void adjust(){
//		ActiveTaskModule am = (ActiveTaskModule) this.getModuleGroup().getModule("sfrm.outgoing.segment.collector");
//		long segmentSize = SFRMProperties.getPayloadSegmentSize();
//		long maxSpeed = SFRMProperties.getSpeedMaximum();
//		int threadCount = StringUtilities.parseInt(am.getComponent("task-list").getParameters().getProperty("max-thread-count"), 5);		
//		long expectedExecInterval = ((segmentSize/1024)*threadCount)/maxSpeed;
//		am.setExecutionInterval(expectedExecInterval);
//	}
}
