/**
 * Active Task to query the database which message need to fire the acknowledgment request. Then send an acknowledgment to 
 * receiver to enquiry the status of SFRM message in the receiver side. 
 * @auther Patrick Yip
 * @version 2.0.0
 * @since 2.0.0
 */
package hk.hku.cecid.edi.sfrm.task;

import hk.hku.cecid.edi.sfrm.spa.SFRMException;
import hk.hku.cecid.edi.sfrm.spa.SFRMLog;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.module.ActiveTaskAdaptor;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDVO;
import hk.hku.cecid.edi.sfrm.handler.AcknowledgementHandler;
import hk.hku.cecid.edi.sfrm.handler.OutgoingMessageHandler;
import hk.hku.cecid.edi.sfrm.handler.SFRMMessageFactory;
import hk.hku.cecid.edi.sfrm.pkg.SFRMAcknowledgementBuilder;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.List;

import javax.mail.MessagingException;

/**
 * Active task responsible for handling the action of sending the acknowledgement request to receiver 
 * @author Patrick Yip
 * @since 2.0.0
 */
public class AcknowledgementTask extends ActiveTaskAdaptor {
	private final SFRMMessageDVO messageDVO;
	private final SFRMPartnershipDVO partnershipDVO;
	
	public AcknowledgementTask(SFRMMessageDVO messageDVO, SFRMPartnershipDVO partenershipDVO){
		this.messageDVO = messageDVO;
		this.partnershipDVO = partenershipDVO;
	}
	
	public void execute() throws Exception{
		String ackContent = buildAckContent();
		SFRMMessage ackMessage = SFRMMessageFactory
			.getInstance()
			
			.createAcknowledgement(messageDVO, partnershipDVO, SFRMConstant.MSGT_ACK_REQUEST, ackContent);
		
		//Send the acknowledgement
		//TODO: To refactor or add a method that can receive the response message from acknowledgement
		
		SFRMMessage responseMessage = OutgoingMessageHandler.getInstance().sendMessageWithMessageResponse(
				ackMessage, messageDVO.getPartnerEndpoint(), messageDVO.getIsHostnameVerified(), 
				messageDVO.getSignAlgorithm(), messageDVO.getEncryptAlgorithm(), messageDVO.getPartnerX509Certificate());
		
		unpackIncomingMessage(responseMessage, partnershipDVO);
		
		BufferedReader ackReader = new BufferedReader(new InputStreamReader(responseMessage.getBodyPart().getInputStream()));
		String ackResponseContent = IOHandler.readString(ackReader);
		
		ackReader.close();
		
		AcknowledgementHandler ackHandler = SFRMProcessor.getInstance().getAcknowledgementHandler();
		
		ackHandler.processAcknowledgementResponse(ackResponseContent);
	}

	private String buildAckContent() throws DAOException{
		SFRMAcknowledgementBuilder builder = new SFRMAcknowledgementBuilder();
		builder.setMessage(messageDVO.getMessageId(), messageDVO.getStatus());
		if(messageDVO.getStatus().equalsIgnoreCase(SFRMConstant.MSGS_PROCESSING) || messageDVO.getStatus().equalsIgnoreCase(SFRMConstant.MSGS_SEGMENTING)){
			List segDVOs = SFRMProcessor.getInstance()
							.getMessageSegmentHandler()
							.retrieveDeliveredSegmentForMessage(messageDVO.getMessageId());
			for(int i=0; segDVOs.size() > i ; i++){
				SFRMMessageSegmentDVO segDVO = (SFRMMessageSegmentDVO) segDVOs.get(i);
				builder.setSegment(messageDVO.getMessageId(), segDVO.getSegmentNo(), segDVO.getStatus());
			}			
		}
		return builder.toString();
	}
	
	/**
	 * Invoke when failure occur
	 * @param e
	 */
	public void onFaulure(Throwable e){
		SFRMProcessor.getInstance().getLogger().error(SFRMLog.AT_CALLER + "Unknown Error", e);
	}
	
	// Philip 2009-07-31
	// same as IncomingMessageHandler.unpackIncomingMessage
	public void unpackIncomingMessage(SFRMMessage message, SFRMPartnershipDVO partnershipDVO) throws SFRMException {
	
		KeyStoreManager keyman = SFRMProcessor.getInstance().getKeyStoreManager();
		
		// Encryption enforcement check and decrypt
		if (partnershipDVO.getEncryptAlgorithm() != null) {
			if (!message.isEncryptedContentType()) {
				SFRMProcessor.getInstance().getLogger().error(
					SFRMLog.AT_CALLER + "Encryption enforcement check failed: " 
					 + message);
				throw new SFRMException("Insufficient message security");
			} else {
				try {
					message.decrypt(keyman.getX509Certificate(), keyman.getPrivateKey());
				} catch (SFRMException e) {
					SFRMProcessor.getInstance().getLogger().error(
						SFRMLog.AT_CALLER + "Unable to decrypt " 
						   + message, e);
					throw e;
				} catch (NoSuchAlgorithmException e) {
					SFRMProcessor.getInstance().getLogger().error(
							SFRMLog.AT_CALLER + "Unable to decrypt " 
							   + message, e);
					throw new SFRMException(e.getMessage(), e);
				} catch (UnrecoverableKeyException e) {
					SFRMProcessor.getInstance().getLogger().error(
							SFRMLog.AT_CALLER + "Unable to decrypt " 
							   + message, e);
					throw new SFRMException(e.getMessage(), e);
				}
			}
		}
			
		// Signing enforcement check and unpack verified signature
		if (partnershipDVO.getSignAlgorithm() != null) {
			if (!message.isSignedContentType()) {
				SFRMProcessor.getInstance().getLogger().error(
					SFRMLog.AT_CALLER + "Signature enforcement check failed: "
					   + message);
				throw new SFRMException("Insufficient message security");
				
			} else {
				try {
					message.verify(partnershipDVO.getVerifyX509Certificate());
				} catch (SFRMException e) {
					SFRMProcessor.getInstance().getLogger().error(
						SFRMLog.AT_CALLER + "Unable to verify "  
							+ message, e);
					throw e;
				}
			}
		}
		
		// Log information
		try {
			SFRMProcessor.getInstance().getLogger().info(
				 SFRMLog.AT_CALLER + SFRMLog.UNPACK_SGT 
			  + " msg id: " + message.getMessageID() 			    
			  + " with payload size : " 
			  + message.getBodyPart().getSize());
		} catch (MessagingException e) {
			throw new SFRMException("Unable to get body part size");
		}
	}	
}
