/**
 * Provides database and message handler and some utility generators at
 * the high level architecture.  
 */
package hk.hku.cecid.edi.sfrm.handler;

import java.util.List;
import java.util.Vector;
import java.sql.Timestamp;

import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.edi.sfrm.spa.SFRMLog;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDVO;

import hk.hku.cecid.piazza.commons.dao.DAO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DVO;

/**
 * The SFRM Message Segment handler is a proxy object of DAO layers. 
 * It wraps with some useful query like retrieve message segment
 * and create message by the SFRM Message Header.<br><br>
 *  
 * Creation Date: 9/10/2006
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	1.0.0
 */
public class SFRMMessageSegmentHandler extends DSHandler{
		
	/**
	 * Create / Get the instance of DAO.
	 */
	protected DAO getInstance() throws DAOException{
		if (this.dao == null){
			this.dao = (SFRMMessageSegmentDAO) 
				SFRMProcessor.getInstance().getDAOFactory().createDAO(SFRMMessageSegmentDAO.class);
			return this.dao;
		}
		return this.dao;
	}
	
	/**
	 * Create an Message Segment according to the SFRM message. 
	 * 
	 * @param message
	 * 			The message segmented used for generating the message segment record. 
	 * @param messageBox 
	 * 			The message box for the mesasge. It should either be "inbox" or "outbox".
	 * @param status 
	 * 			The status for the new message segment record. 
	 * 			if this field is null, we conside the status is PENDING.
	 * @return a message segment dvo if the operation runs succesfully.
	 * @throws DAOException 
	 * 			Error in Database connectivity.
	 */
	public SFRMMessageSegmentDVO 
	createMessageSegmentBySFRMMessage(
			SFRMMessage message, 
			String 		messageBox, 
			String 		status) 
			throws DAOException
	{
		// Error Handling
		if (message == null)
			throw new NullPointerException("Message is empty");		
		if (status == null)
			status = SFRMConstant.MSGS_PENDING;		
		
		SFRMMessageSegmentDAO segDAO = 
			(SFRMMessageSegmentDAO) this.getInstance();
		SFRMMessageSegmentDVO segDVO = 
			(SFRMMessageSegmentDVO) segDAO.createDVO();		
		// Fill in general information / key infomation.
		segDVO.setMessageId	 (message.getMessageID());
		segDVO.setMessageBox (messageBox);
		segDVO.setSegmentNo	 (message.getSegmentNo());
		segDVO.setSegmentType(message.getSegmentType());
		segDVO.setStatus	 (status);
		// Fill in segment type-specified information.
		String type = message.getSegmentType();
		if (type.equals(SFRMConstant.MSGT_PAYLOAD) || 
			type.equals(SFRMConstant.MSGT_META)){
			segDVO.setMD5Value(message.getMicValue());
			segDVO.setSegmentStart(message.getSegmentOffset());
			segDVO.setSegmentEnd(message.getSegmentOffset()
					+ message.getSegmentLength());			
		}				
		// Log dubug information.
		getLogger().info(
			  SFRMLog.MSHDAO_CALLER
		   +  SFRMLog.CREATE_SGT 
		   +" msg id: "   + segDVO.getMessageId() 
		   +" msg box: "  + segDVO.getMessageBox() 
		   +" sgt type: " + segDVO.getSegmentType() 
		   +" sgt no: "   + segDVO.getSegmentNo()); 																		
		// Save it to database.
		segDAO.create(segDVO);
		return segDVO;
	}

	/**
	 * Select a message segment with the specified parameter.
	 * 
	 * @param messageId
	 * 			The message id of the message segment belong to.
	 * @param messageBox
	 * 			The message box of the message segment. It should either "inbox" or "outbox".
	 * @param segmentNo
	 * 			The segment number of the segment
	 * @param type
	 * 			The segment type (META, PAYLOAD, RECEIPT, RECOVERY) 
	 * @return
	 * 			A message segment dvo if the operation runs succesfully.
	 * @throws DAOException 			
	 * 			Error in Database connectivity. 
	 */
	public SFRMMessageSegmentDVO retrieveMessageSegment(
			String 	messageId,
			String 	messageBox, 
			int 	segmentNo, 
			String 	type) 
			throws DAOException 
	{
		SFRMMessageSegmentDVO ret = null;
		ret = ((SFRMMessageSegmentDAO) this.getInstance())
				.findMessageSegmentByMessageIdAndBoxAndType(messageId,
				messageBox, segmentNo, type);
		if (ret != null)
			return ret;					
		return null;
	}
	
	/**
	 * Select a message segment with the specified parameter.<br><br>
	 *  
	 * The segment type is omitted here is this query.<br><br> 
	 *  
	 * @param message
	 * 			The message segmented used for get the message segment record. 
	 * @param messageBox 
	 * 			The message box for the mesasge. It should either be "inbox" or "outbox".
	 * @return 
	 * 			A message segment dvo if the operation runs succesfully. 
	 * @throws DAOException
	 */
	public SFRMMessageSegmentDVO 
	retrieveMessageSegment(
			SFRMMessage message,
			String 		messageBox) 
			throws DAOException 
	{
		if (message == null)
			return null;
		return this.retrieveMessageSegment(
				message.getMessageID(), 
				messageBox,
				message.getSegmentNo(), 
				message.getSegmentType());
	}
	
	/**
	 * Select the last updated message segment with the specified parameter.<br><br>
	 * 
	 * The last updated message segment is defined as the message 
	 * segment which has the latest <code>proceedTimestamp<code> value. 
	 * 
	 * @param messageId
	 * 			The message id of the message segment belong to.
	 * @param messageBox
	 * 			The message box of the message segment. It should either "inbox" or "outbox".
	 * @param type
	 * 			The segment type (META, PAYLOAD, RECEIPT, RECOVERY) 
	 * @return
	 * 			A message segment dvo if the operation runs succesfully.
	 * @throws DAOException
	 */
	public SFRMMessageSegmentDVO
	retrieveLastUpdatedMessageSegment(
			String messageId,
			String messageBox,
			String type) throws 
			DAOException
	{
		return ((SFRMMessageSegmentDAO) this.getInstance())
				.findLastUpdatedMessageSegmentByMessageIdAndBoxAndType(messageId, messageBox, type);
	}
	
	/**
	 * Select the last updated timestamp with the specified parameter.<br><br>
	 * 
	 * The last updated message segment is defined as the message 
	 * segment which has the latest <code>proceedTimestamp<code> value. 
	 * 
	 * @param messageId
	 * 			The message id of the message segment belong to.
	 * @param messageBox
	 * 			The message box of the message segment. It should either "inbox" or "outbox".
	 * @param type
	 * 			The segment type (META, PAYLOAD, RECEIPT, RECOVERY) 
	 * @return
	 * 			A message segment dvo if the operation runs succesfully.
	 * @throws DAOException
	 */
	public Timestamp
	retrieveLastUpdatedTimestamp(
			String messageId,
			String messageBox,
			String type) throws 
			DAOException
	{
		SFRMMessageSegmentDVO msDVO = ((SFRMMessageSegmentDAO) this.getInstance())
			.findLastUpdatedMessageSegmentByMessageIdAndBoxAndType(messageId, messageBox, type);
		if (msDVO != null)
			return msDVO.getProceedTimestamp();	
		return null;
	}			
	
	public List
	retrieveIncompleteSegments(
			String messageBox, 
			String status, 
			String type,
			int    limit) throws 
			DAOException
	{
		return ((SFRMMessageSegmentDAO) this.getInstance())
				.findIncompleteSegments(messageBox, status, type, limit);
	}
	
	/**
	 * Retrieve a set of messages from the specified parameters.
	 * 
	 * @param messageBox
	 * 			The message box of the message segment.
	 * 			Either inbox or outbox can be allowed. 
	 * @param status
	 * 			The status of the message segment.
	 * @param limit
	 * 			The maximum message that can be retrieved.
	 * @return  
	 * 			Return a list of message that fit the criteria.
	 * @throws DAOException
	 */
	public List 
	retrieveMessages(
			String messageBox, 
			String status, 
			int limit) throws 
			DAOException 
	{
		return ((SFRMMessageSegmentDAO) this.getInstance())
				.findMessageSegmentsByMessageBoxAndStatus(messageBox, status,
						limit);
	}
	
	public List 
	retrieveMessages(
			String messageBox, 
			String status,
			String messageStatus, 
			int limit) throws 
			DAOException 
	{
		return ((SFRMMessageSegmentDAO) this.getInstance())
				.findMessageSegmentsByMessageBoxAndStatusAndMessageStatusNotEqualTo(
						messageBox, status, messageStatus, limit);
	}
	
	public List
	retrieveMessages(
			String messageBox,
			String status,
			String type,
			String messageStatus,
			int limit) throws 
			DAOException
	{
		return ((SFRMMessageSegmentDAO) this.getInstance())
				.findMessageSegmentByMessageBoxAndStatusAndTypeAndMessageStatusNotEqualTo(
				messageBox, status, type, messageStatus, limit);
	}
			
	/**
	 * Find how many message for the partiucalur message id and 
	 * message box are available in the database. 
	 * 
	 * @param messageId
	 * 			The message id of the message segment belong to.
	 * @param messageBox
	 * 			The message box of the message segment. 
	 * 			Either inbox or outbox can be allowed. 
	 * @param type 
	 * 			The segment type of the message segment.
	 * @param status 
	 *			The status of the message segment
	 * @return
	 * @throws DAOException
	 */
	public int 
	retrieveMessageSegmentCount(
			String messageId,
			String messageBox, 
			String type, 
			String status) throws 
			DAOException 
	{
		return ((SFRMMessageSegmentDAO) this.getInstance())
				.findNumOfSegmentByMessageIdAndBoxAndTypeAndStatus(messageId, messageBox, type, status);
	}
	
	/**
	 * Find the maximum segment number it has in the DB 
	 * for the specified message.
	 * 
	 * @param messageId
	 * 			The message id of the message segment belong to.
	 * @param messageBox
	 * 			The message box of the message segment. 
	 * @param type
	 * 			The segment type (META, PAYLOAD, RECEIPT, RECOVERY) 
	 * @return
	 * 			
	 * @throws DAOException
	 */
	public int retrieveMaxMessageSegmentNumber(
			String messageId,
			String messageBox,
			String type) throws 
			DAOException
	{
		return ((SFRMMessageSegmentDAO) this.getInstance())
				.findMaxSegmentNoByMessageIdAndBoxAndType(messageId, messageBox, type);
	}
	
	public List retrieveDeliveredSegmentForMessage(String messageId) throws DAOException{
		List results = ((SFRMMessageSegmentDAO)this.getInstance())
			.findSegmentsByMessageIdAndBoxAndTypeAndStatus(messageId, SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGT_PAYLOAD, SFRMConstant.MSGS_DELIVERED);
		return results;
	}
	
	/**
	 * Find message segment by providing the message ID, message box, message type and a list of status 
	 * @param messageId The message id of the mesage segment belong to
	 * @param messageBox The message box of the message segment
	 * @param messageType The segment type (META, PAYLOAD, ACK)
	 * @param status The status of message status
	 * @return List of SFRMMessageSegmentDVO
	 * @throws DAOException
	 */
	public List retrieveMessages(String messageId, String messageBox, String messageType, String[] status) throws DAOException{
		SFRMMessageSegmentDAO msDAO = (SFRMMessageSegmentDAO) this.getInstance();
		Vector resultList = new Vector();
		for(int i=0; status.length > i ; i++){
			List list = msDAO.findSegmentsByMessageIdAndBoxAndTypeAndStatus(messageId, messageBox, messageType, status[i]);
			resultList.addAll(list);
		}
		return resultList;
	}
	
	public List retrieveMessages(String messageId, String messageBox, String type, List<Integer> segmentNums) throws DAOException{
		SFRMMessageSegmentDAO msDAO = (SFRMMessageSegmentDAO) this.getInstance();
		return msDAO.findSegmentByMessageIdAndBoxAndTypeAndNos(messageId, messageBox, type, segmentNums);
	}
	/**
	 * DOES NOT SUPPORT CACHING. 
	 */
	public void clearCache(DVO dvo){	
		// DOES NOT SUPPORT CACHING.
	}
}
