/**
 * Provides database and message handler and some utility generators at
 * the high level architecture.  
 */
package hk.hku.cecid.edi.sfrm.handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDVO;
import hk.hku.cecid.edi.sfrm.spa.SFRMException;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;

import hk.hku.cecid.piazza.commons.dao.DAO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DVO;

/** 
 * THe SFRM Message handler is a proxy object of DAO layers. It wraps
 * with some useful query like retrieve message and create message 
 * by the SFRM Meta Header.<br><br>
 * 
 * Creation Date: 3/10/2006<br><br>
 * 
 * V1.0.1 - supports DVO <code>caching</code>.
 * 
 * @author Twinsen Tsang
 * @version 1.0.1
 * @since	1.0.0
 */
public class SFRMMessageHandler extends DSHandler {

	/**
	 * Create / Get the instance of DAO.
	 */
	protected DAO 
	getInstance() throws DAOException
	{
		// TODO: Not thread-safety, may lead to partial constructed issue. 
		if (this.dao == null){
			this.dao = (SFRMMessageDAO) 
				SFRMProcessor.getInstance().getDAOFactory().createDAO(SFRMMessageDAO.class);
			return this.dao;
		}
		return this.dao;
	}	 
	
	/**
	 * Create a new message record by a SFRM Mesasge which is meta type.
	 * 
	 * @param msg
	 * 			The SFRM message.
	 * @param pDVO
	 * 			The partnership associated with this message.
	 * @param messageBox
	 * 			The message box of the new message record.
	 * @param status
	 * 			The status of the new message record.
	 * @param statusDesc
	 * 			The status description of the new message record.
	 * @return
	 * 			return null if database error, otherwise a new message record.
	 * @throws DAOException
	 * 			If the sfrm message is null or with segment no not equal to zero
	 * 			, or other database error.
	 * @throws SFRMException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */	 	 
	public SFRMMessageDVO createMessageBySFRMMetaMessage(
			SFRMMessage msg, SFRMPartnershipDVO pDVO, String messageBox, String status, String statusDesc) 
			throws DAOException, SFRMException, FileNotFoundException, IOException {
		
		String		   msgId  = msg.getMessageID();
		SFRMMessageDAO msgDAO = (SFRMMessageDAO) this.getInstance();
		SFRMMessageDVO msgDVO = (SFRMMessageDVO) msgDAO.createDVO();
		msgDVO.setMessageId 		(msgId);
		msgDVO.setMessageBox		(messageBox);
		msgDVO.setPartnershipId		(msg.getPartnershipId());
		msgDVO.setStatus			(status);
		msgDVO.setTotalSize			(msg.getTotalSize());
		msgDVO.setPartnerEndpoint	(pDVO.getPartnerEndpoint());
		msgDVO.setTotalSegment		(msg.getTotalSegment());		
		msgDVO.setStatusDescription	(statusDesc);
		// Set the features of the message depends on the message box of this message.
		msgDVO.setSignAlgorithm(pDVO.getSignAlgorithm());
		msgDVO.setEncryptAlgorithm(pDVO.getEncryptAlgorithm());
		//Update the single/multiple file related field information
		msgDVO.setFilename			(msg.getFilename());
		msgDVO.setIsHostnameVerified(pDVO.isHostnameVerified());
		if (pDVO.getEncryptAlgorithm() != null) {		
			msgDVO.setPartnerCertContent(pDVO.getEncryptX509CertificateBase64());
		}

		// cache-and-create action.
		if (this.isCacheEnable){
			String key = msgId + "_" + messageBox;
			this.cacher.putOrUpdateDVO(key, msgDVO);		
		}
		msgDAO.create(msgDVO);		
		return msgDVO;
	}
	
	/**
	 * Retrieve a message from the specified parameters. This method 
	 * support caching. If the particular DVO is already in the cache, it 
	 * returned immediately without JDBC calls. 
	 * 
	 * @param messageId		
	 * 			The message id of the message.
	 * @param messageBox
	 * 			The message box of the message. either inbox or outbox.
	 * @return
	 * 			return null if not found by the specified parameter, otherwise a message record.
	 * @throws DAOException 
	 */
	public SFRMMessageDVO 
	retrieveMessage(
			String messageId, 
			String messageBox) throws DAOException
	{
		SFRMMessageDVO ret = null;
		String key = null;
		if (this.isCacheEnable){
			// cache-or-select action.
			key = messageId + "_" + messageBox;	
			ret = (SFRMMessageDVO) this.cacher.getDVO(key);
		}
		// TODO: Broken cache.		
		if (ret == null){			
			ret = (SFRMMessageDVO) ((SFRMMessageDAO) this.getInstance())
				.findMessageByMessageIdAndBox(messageId, messageBox);
			if (this.isCacheEnable)
				this.cacher.putOrUpdateDVO(key, ret);
		}				 
		return ret;
	}
	
	/**
	 * Retrieve a set of messages from the specified parameters.
	 * 
	 * @param messageBox
	 * 			The message box of the message. either inbox or outbox. 
	 * @param status
	 * 			The status of the message.
	 * @return 
	 * 			return a list of message that fit the criteria.
	 * @throws DAOException
	 */
	public List 
	retrieveMessages(
			String messageBox, 
			String status) throws DAOException
	{ 
		return ((SFRMMessageDAO) this.getInstance())
				.findMessageByMessageBoxAndStatus(messageBox, status);
	}			
	
	/**
	 * Retrieve a set of messages that is required to ack the acknowledgement from receiver
	 * @param numOfMessage number of messages for acknowledgement request
	 * @param offset offset in the message records
	 * @return a list of message that is required to ack the acknowledgement
	 * @throws DAOException
	 */
	public List retrieveAcknowledgmentMessages(int numOfMessage, int offset) throws DAOException{
		return ((SFRMMessageDAO) this.getInstance()).findMessageForAcknowledgement(numOfMessage, offset);
	}
	
	/**
	 * Update the <code>msgDVO</code> to the pesistence DB.<br><br>
	 * 
	 * Developer SHOULD use this method instead of 
	 * {@link hk.hku.cecid.piazza.commons.dao.DAO#persist(hk.hku.cecid.piazza.commons.dao.DVO)}
	 * because this invocation also manage the DVO cache.
	 * 
	 * @param	msgDVO
	 * 				The message DVO to be update.
	 * @return	true if operation success, fail vice versa.
	 *  
	 * @since	1.0.1  
	 */
	public boolean
	updateMessage(SFRMMessageDVO msgDVO) throws DAOException
	{		
		if (this.isCacheEnable){
			// cache-and-update action.
			String key = this.extractKey(msgDVO);	
			// Cache it or update it.
			this.cacher.putOrUpdateDVO(key, msgDVO);
		}
		return ((SFRMMessageDAO) this.getInstance()).persist(msgDVO);			
	}
	
	/**
	 * Remove the <code>msgDVO</code> to the pesistence DB.<br><br>
	 * 
	 * Developer SHOULD use this method instead of 
	 * {@link hk.hku.cecid.piazza.commons.dao.DAO#remove(hk.hku.cecid.piazza.commons.dao.DVO)}
	 * because this invocation also manage the DVO cache.
	 * 
	 * @param	msgDVO
	 * 				The message DVO to be update.
	 * @return	true if operation success, fail vice versa.
	 * @throws DAOException
	 * 
	 * @since 	1.0.1	
	 */
	public boolean
	removeMessage(SFRMMessageDVO msgDVO) throws DAOException
	{
		if (this.isCacheEnable){
			// cache-and-remove action.
			String key = this.extractKey(msgDVO);
			this.cacher.removeDVO(key);
		}
		return ((SFRMMessageDAO) this.getInstance()).remove(msgDVO);
	}
	
	/**
	 * Clear the cache ONLY by a particular dvo object; 
	 */
	public void clearCache(DVO dvo){	
		if (dvo != null && dvo instanceof SFRMMessageDVO){
			String key = this.extractKey((SFRMMessageDVO) dvo);
			this.cacher.removeDVO(key);
		}
	}
	
	/**
	 * Extract the key field and form a unique I.D. for the DVO cache.<br><br>
	 * 
	 * The extraction is quite simple. The form of key is just
	 * <strong>messageId_messageBox</strong>.<br>
	 * 
	 * @since	1.0.1
	 */
	private String extractKey(SFRMMessageDVO msgDVO) {
		return (msgDVO != null) ? msgDVO.getMessageId() + "_" + msgDVO.getMessageBox(): "";
	}
	
	
}
