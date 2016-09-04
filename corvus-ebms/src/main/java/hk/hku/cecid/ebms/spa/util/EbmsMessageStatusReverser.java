/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package hk.hku.cecid.ebms.spa.util;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.dao.OutboxDAO;
import hk.hku.cecid.ebms.spa.dao.OutboxDVO;
import hk.hku.cecid.ebms.spa.handler.MessageClassifier;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DAOFactory;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceProcess;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceTransaction;

/**
 * Reverse message status for message redownload and resend
 * 
 * @author franz
 * 
 */
public class EbmsMessageStatusReverser {
	private DAOFactory daoFactory = EbmsProcessor.core.dao;

	/**
	 * Reverse <b>OUTBOX</b> message status back to <b>PENDING</b>. Hence, the
	 * message can be resent again.
	 * 
	 * @param messageId
	 *            - Message Id of the message to be resent
	 * @return MessageDVO of the message to be resent
	 * @throws Exception
	 */
	public MessageDVO updateToSend(final String messageId) throws Exception {
		final MessageDAO messageDao = (MessageDAO) daoFactory
				.createDAO(MessageDAO.class);

		// Prepare Search Criteria
		final MessageDVO criteriaDVO = (MessageDVO) messageDao.createDVO();
		criteriaDVO.setMessageId(messageId);
		criteriaDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_OUTBOX);
		criteriaDVO.setMessageType(MessageClassifier.MESSAGE_TYPE_ORDER);

		// Retrieve DAO
		boolean isFound = messageDao.retrieve(criteriaDVO);
		if (!isFound) {
			throw new Exception("Message [" + messageId
					+ "] is not found in database");
		}

		String status = criteriaDVO.getStatus();
		if (!MessageClassifier.INTERNAL_STATUS_DELIVERY_FAILURE
				.equalsIgnoreCase(status)) {
			throw new Exception("Message [" + messageId
					+ "] is not available for re-send");
		}

		DataSourceProcess process = new DataSourceProcess(
				(DataSourceDAO) messageDao) {
			protected void doTransaction(DataSourceTransaction tx)
					throws DAOException {
				messageDao.setTransaction(tx);

				criteriaDVO.setStatus(MessageClassifier.INTERNAL_STATUS_PENDING);
				criteriaDVO.setStatusDescription(null);
				messageDao.persist(criteriaDVO);

				// Delete acknowledgement
				MessageDVO response = (MessageDVO) messageDao.createDVO();
				response.setRefToMessageId(messageId);
				response.setMessageBox(MessageClassifier.MESSAGE_BOX_INBOX);
				response
						.setMessageType(MessageClassifier.MESSAGE_TYPE_ACKNOWLEDGEMENT);
				if (messageDao.findRefToMessage(response)) {
					messageDao.remove(response);
				}

				// Delete error
				response.setRefToMessageId(messageId);
				response.setMessageBox(MessageClassifier.MESSAGE_BOX_INBOX);
				response.setMessageType(MessageClassifier.MESSAGE_TYPE_ERROR);
				if (messageDao.findRefToMessage(response)) {
					messageDao.remove(response);
				}

				// Add outbox record
				OutboxDAO outboxDAO = (OutboxDAO) daoFactory
						.createDAO(OutboxDAO.class);
				outboxDAO.setTransaction(tx);
				OutboxDVO outboxDVO = (OutboxDVO) outboxDAO.createDVO();
				outboxDVO.setMessageId(messageId);
				outboxDVO.setRetried(0);
				outboxDAO.addOutbox(outboxDVO);
			}
		};

		process.start();

		EbmsProcessor.core.log.info("Message [" + messageId
				+ "] is prepared for resend");
		return criteriaDVO;
	}

	public void updateToDownload(String messageId) throws Exception {
		
		if(messageId == null ||
				messageId.trim().equalsIgnoreCase("")){
				throw new Exception(
						"MessageId is empty, No message can be query.");
		}
		
		MessageDAO dao = (MessageDAO) daoFactory.createDAO(MessageDAO.class);

		// Prepare Search Criteria
		MessageDVO criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setMessageId(messageId);
		criteriaDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_INBOX);

		// Retrieve DAO
		boolean isFound = dao.retrieve(criteriaDVO);
		if (!isFound) {
			throw new Exception("Message [" + messageId
					+ "] is not found in database");
		}
		String status = criteriaDVO.getStatus();
		if (!MessageClassifier.INTERNAL_STATUS_DELIVERED
				.equalsIgnoreCase(status)) {
			throw new Exception("Message [" + messageId
					+ "] is not available for re-download");
		}

		criteriaDVO.setStatus(MessageClassifier.INTERNAL_STATUS_PROCESSED);
		dao.persist(criteriaDVO);

		EbmsProcessor.core.log.info("Message [" + messageId
				+ "] is prepared for redownload");
	}
}
