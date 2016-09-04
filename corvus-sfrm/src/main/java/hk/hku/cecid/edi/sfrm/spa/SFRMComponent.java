/**
 * 
 */
package hk.hku.cecid.edi.sfrm.spa;

import hk.hku.cecid.edi.sfrm.com.PackagedPayloadsRepository;
import hk.hku.cecid.edi.sfrm.handler.AcknowledgementHandler;
import hk.hku.cecid.edi.sfrm.handler.IncomingMessageHandler;
import hk.hku.cecid.edi.sfrm.handler.OutgoingMessageHandler;
import hk.hku.cecid.edi.sfrm.handler.SFRMExternalRequestHandler;
import hk.hku.cecid.edi.sfrm.handler.SFRMMessageHandler;
import hk.hku.cecid.edi.sfrm.handler.SFRMMessageSegmentHandler;
import hk.hku.cecid.edi.sfrm.handler.SFRMPartnershipHandler;

import hk.hku.cecid.edi.sfrm.handler.MessageStatusQueryHandler;

import hk.hku.cecid.piazza.commons.module.SystemComponent;
import hk.hku.cecid.piazza.commons.os.OSManager;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;

/**
 * @author Patrick Yip
 *
 */
public abstract class SFRMComponent extends SystemComponent {
	
	public OSManager getOSManager() throws Exception{
		return (OSManager) getComponent("os-manager");
	}
	
	public SFRMMessageHandler getMessageHandler(){
		return (SFRMMessageHandler) getComponent("message-handler");
	}
	
	public SFRMMessageSegmentHandler getMessageSegmentHandler(){
		return (SFRMMessageSegmentHandler) getComponent("message-segment-handler");
	}
	
	public SFRMPartnershipHandler getPartnershipHandler(){
		return (SFRMPartnershipHandler) getComponent("partnership-handler");
	}
	
	public IncomingMessageHandler getIncomingMessageHandler(){
		return (IncomingMessageHandler) getComponent("incoming-message-handler");
	}
	
	public OutgoingMessageHandler getOutgoingMessageHandler(){
		return (OutgoingMessageHandler) getComponent("outgoing-message-handler");
	}
	
	public PackagedPayloadsRepository getOutgoingRepository(){
		return (PackagedPayloadsRepository) getComponent("outgoing-payload-repository");
	}
	
	public PackagedPayloadsRepository getIncomingRepository(){
		return (PackagedPayloadsRepository) getComponent("incoming-payload-repository");
	}
	
	public AcknowledgementHandler getAcknowledgementHandler(){
		return (AcknowledgementHandler) getComponent("acknowledgement-handler");
	}
	
	public KeyStoreManager getKeyStoreManager(){
		return (KeyStoreManager) getComponent("keystore-manager");
	}	
	
	public SFRMExternalRequestHandler getExternalRequestHandler(){
		return (SFRMExternalRequestHandler) getComponent("external-request-handler");
	}
	
	public MessageStatusQueryHandler getMessageSpeedQueryHandler(){
		return (MessageStatusQueryHandler) getComponent("message-speed-query-handler");
	}
}
