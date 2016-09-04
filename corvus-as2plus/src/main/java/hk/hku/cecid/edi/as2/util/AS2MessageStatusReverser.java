/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.util;

import hk.hku.cecid.edi.as2.AS2Exception;
import hk.hku.cecid.edi.as2.AS2PlusProcessor;
import hk.hku.cecid.edi.as2.dao.MessageDAO;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DAOFactory;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceProcess;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceTransaction;

import java.util.List;

/**
 * Reverse message status for message redownload and resend
 * 
 * @author franz
 *
 */
public class AS2MessageStatusReverser {

	private DAOFactory daoFactory = AS2PlusProcessor.getInstance()
			.getDAOFactory();
	
	/**
	 * Reverse <b>OUTBOX</b> message status back to <b>PENDING</b>.
	 * Hence, the message can be resent again. 
	 * 
	 * @param messageId - Message Id of the message to be resent
	 * @return MessageDVO of the message to be resent
	 * @throws AS2Exception
	 */
	public MessageDVO updateToSend(final String messageId) throws AS2Exception {
		try {
			final MessageDAO messageDao = (MessageDAO) daoFactory.createDAO(MessageDAO.class);
			
			// Prepare Search Criteria
			final MessageDVO criteriaDVO = (MessageDVO) messageDao.createDVO();
			criteriaDVO.setMessageId(messageId);
			criteriaDVO.setMessageBox(MessageDVO.MSGBOX_OUT);
			criteriaDVO.setIsReceipt(false);
			
			// Retrieve DAO
			boolean isFound = messageDao.retrieve(criteriaDVO);
			if(!isFound){
				throw new AS2Exception("Message ["+messageId+"] is not found in database");
			}
			
			String status = criteriaDVO.getStatus();
			if(!MessageDVO.STATUS_DELIVERY_FAILURE.equalsIgnoreCase(status)){
				throw new AS2Exception(
						"Message ["+messageId+"] is not available for re-send");
			}
			
			DataSourceProcess process = new DataSourceProcess((DataSourceDAO) messageDao) {
				protected void doTransaction(DataSourceTransaction tx) throws DAOException {
					messageDao.setTransaction(tx);
					
					criteriaDVO.setStatus(MessageDVO.STATUS_PENDING);
					criteriaDVO.setStatusDescription(null);
					messageDao.persist(criteriaDVO);
					
					// Delete all receipts
					List receiptList = messageDao.findMessageByOriginalMessageID(
							messageId, MessageDVO.MSGBOX_OUT);
					for (int i=0; i<receiptList.size(); ++i) {
						MessageDVO receipt = (MessageDVO) receiptList;
						messageDao.remove(receipt);
					}
				}
			};
			
			process.start();
			
			AS2PlusProcessor.getInstance().getLogger().info("Message ["+messageId+"] is prepared for resend");
			return criteriaDVO;			
		} catch (DAOException daoExp) {
			throw new AS2Exception("Message is fail to reverse", daoExp);
		}
	}

	/**
	 * Reverse <b>INBOX</b> message status back to <b>PROCESSED</b>.
	 * Hence, the message can be downloaded again. 
	 * 
	 * @param messageId - MessageId to query inbox message
	 * @throws AS2Exception
	 */
	public void updateToDownload(String messageId) throws AS2Exception, DAOException{

		  if(messageId == null || 
				  messageId.trim().equalsIgnoreCase("")){
			  throw new AS2Exception("MessageId is empty, No message can be query.");
		  }
		
		MessageDAO dao = (MessageDAO) daoFactory.createDAO(MessageDAO.class);
		// Prepare Search Criteria
		MessageDVO criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setMessageId(messageId);
		criteriaDVO.setMessageBox(MessageDVO.MSGBOX_IN);

		// Retrieve DAO
		boolean isFound = dao.retrieve(criteriaDVO);
		if (!isFound) {
			throw new AS2Exception("Message [" + messageId
					+ "] is not found in database");
		}
		String status = criteriaDVO.getStatus();
		if (!MessageDVO.STATUS_DELIVERED.equalsIgnoreCase(status)) {
			throw new AS2Exception("Message [" + messageId
					+ "] is not available for re-download");
		}

		criteriaDVO.setStatus(MessageDVO.STATUS_PROCESSED);
		dao.persist(criteriaDVO);
	}

}
