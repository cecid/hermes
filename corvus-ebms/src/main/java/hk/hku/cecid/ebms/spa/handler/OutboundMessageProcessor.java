/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.handler;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.pkg.MessageHeader;
import hk.hku.cecid.ebms.pkg.MessageOrder;
import hk.hku.cecid.ebms.pkg.PayloadContainer;
import hk.hku.cecid.ebms.pkg.MessageHeader.PartyId;
import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.EbmsUtility;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.dao.MessageServerDAO;
import hk.hku.cecid.ebms.spa.dao.OutboxDVO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDAO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
import hk.hku.cecid.ebms.spa.dao.RepositoryDVO;
import hk.hku.cecid.ebms.spa.listener.EbmsRequest;
import hk.hku.cecid.ebms.spa.listener.EbmsResponse;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.soap.SOAPRequest;
import hk.hku.cecid.piazza.commons.soap.WebServicesRequest;
import hk.hku.cecid.piazza.commons.util.Generator;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPException;

/**
 * @author Donahue Sze
 * 
 */
public class OutboundMessageProcessor {

	static OutboundMessageProcessor outboundMessageProcessor;

	static boolean outboundMessageProcessor_initFlag = false;

	public synchronized static OutboundMessageProcessor getInstance() {
		if (!outboundMessageProcessor_initFlag) {
			outboundMessageProcessor = new OutboundMessageProcessor();
			outboundMessageProcessor_initFlag = true;
		}
		return outboundMessageProcessor;
	}

	private OutboundMessageProcessor() {
	}

	public void processOutgoingMessage(EbmsRequest request,
			EbmsResponse response) throws MessageServiceHandlerException {

		EbxmlMessage ebxmlMsg;

		try {
			ebxmlMsg = request.getMessage();

			// generate message id if it does not exist
			if (ebxmlMsg.getMessageHeader().getMessageId() == null) {
				String messageId = Generator.generateMessageID();
				ebxmlMsg.getMessageHeader().setMessageId(messageId);
				EbmsProcessor.core.log.info("Genereating message id: " + messageId);
			}

			// classify where the message come from
			Object requestSource = request.getSource();
			if (requestSource != null) {
				boolean validMessage;
				
				if (requestSource instanceof SOAPRequest) {
					validMessage = handleSOAPRequest((SOAPRequest) requestSource, ebxmlMsg);
				} else if (requestSource instanceof WebServicesRequest) {
					validMessage = handleWebServiceRequest((WebServicesRequest) requestSource, ebxmlMsg);
				} else {
					validMessage = false;
				}
				
				if (!validMessage) {
					throw new RuntimeException(
							"Outbound Message Processor - invalid messsage: "
									+ ebxmlMsg.getMessageId());
				}
			} else {
				// msh generate reply msg (without source, no content type)
				// such as acknowledgement, error message
				storeOutgoingMessage(ebxmlMsg, null);
			}
		} catch (Exception e) {
			throw new MessageServiceHandlerException("Error in processing outgoing message", e);
		}
	}

	/**
	 * Handle message came from EbmsOutboundListener
	 * 
	 * @param request
	 * @return true if valid message, otherwise false
	 * @throws DAOException
	 * @throws MessageServiceHandlerException
	 * @throws SOAPException
	 */
	private boolean handleSOAPRequest(SOAPRequest soapRequest, EbxmlMessage ebxmlMsg) 
			throws DAOException,	MessageServiceHandlerException, SOAPException {
		if (!(soapRequest.getSource() instanceof HttpServletRequest)) {
			return false;
		}
		
		HttpServletRequest httpServletRequest = (HttpServletRequest) soapRequest.getSource();
		if (httpServletRequest.getUserPrincipal() == null) {
			return false;
		}
			
		String contentType = httpServletRequest.getHeader("content-type");
		MessageClassifier messageClassifier = new MessageClassifier(ebxmlMsg);
		if (messageClassifier.isMessageOrder()) {
			storeOutgoingOrderedMessage(ebxmlMsg, contentType);
		} else {
			storeOutgoingMessage(ebxmlMsg, contentType);
		}
		
		return true;
	}

	/**
	 * Handle message came from EbmsMessageSenderService
	 * 
	 * @param request
	 * @return true if valid message, otherwise false
	 * @throws DAOException
	 * @throws MessageServiceHandlerException
	 * @throws SOAPException
	 */
	private boolean handleWebServiceRequest(WebServicesRequest wsRequest, EbxmlMessage ebxmlMsg)
			throws DAOException, MessageServiceHandlerException, SOAPException {
		if (wsRequest.getSource() instanceof SOAPRequest) {
			SOAPRequest soapRequest = (SOAPRequest) wsRequest.getSource();
			if (soapRequest.getSource() instanceof HttpServletRequest) {
				generateAndStoreEbxmlMessage(ebxmlMsg);
				return true;
			}
		}
		return false;
	}

	/**
	 * @param ebxmlRequestMessage
	 * @param principalId
	 * @param contentType
	 * @throws DAOException
	 * @throws SOAPException
	 * @throws MessageServiceHandlerException
	 */
	private synchronized void storeOutgoingOrderedMessage(
			EbxmlMessage ebxmlRequestMessage, String contentType)
			throws DAOException, SOAPException, MessageServiceHandlerException {

		MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
				.createDAO(MessageDAO.class);

		// add the sequence no if necessary
		// for auto generating ebxmlmessage by server
		if (ebxmlRequestMessage.getMessageOrder() == null) {
			MessageDVO messageCPADVO = (MessageDVO) messageDAO.createDVO();
			messageCPADVO.setMessageBox(MessageClassifier.MESSAGE_BOX_OUTBOX);
			messageCPADVO.setCpaId(ebxmlRequestMessage.getCpaId());
			messageCPADVO.setService(ebxmlRequestMessage.getService());
			messageCPADVO.setAction(ebxmlRequestMessage.getAction());
			messageCPADVO.setConvId(ebxmlRequestMessage.getConversationId());

			int previousMaxSequenceNo = messageDAO
					.findMaxSequenceNoByMessageBoxAndCpa(messageCPADVO);
			int status = (previousMaxSequenceNo == -1) ? MessageOrder.STATUS_RESET
					: MessageOrder.STATUS_CONTINUE;
			int currentSequenceNo = previousMaxSequenceNo + 1;

			if (previousMaxSequenceNo >= 9000000) {
				EbmsProcessor.core.log.debug("Try to reset the sequence");
				if (isResetAllowed(messageCPADVO)) {
					status = MessageOrder.STATUS_RESET;
					currentSequenceNo = 0;
					EbmsProcessor.core.log.debug("Reset the sequence allowed");
				} else {
					EbmsProcessor.core.log.debug("Reset the sequence not allowed");
				}
			}

			EbmsProcessor.core.log.debug("Ordered message ("
					+ ebxmlRequestMessage.getMessageId()
					+ ") with sequence no: " + currentSequenceNo);

			ebxmlRequestMessage.addMessageOrder(status, currentSequenceNo);
		}

		// message type classification
		MessageClassifier messageClassifier = new MessageClassifier(ebxmlRequestMessage);
		String messageType = messageClassifier.getMessageType();
		EbxmlMessageDAOConvertor message = new EbxmlMessageDAOConvertor(
				ebxmlRequestMessage, 
				MessageClassifier.MESSAGE_BOX_OUTBOX,
				messageType);

		MessageDVO messageDVO = message.getMessageDVO();
		messageDVO.setStatus(MessageClassifier.INTERNAL_STATUS_PENDING);
		// update the sequence group
		int currentMaxSequenceGroup = messageDAO
				.findMaxSequenceGroupByMessageBoxAndCpa(messageDVO);
		if (messageClassifier.isSeqeunceStatusReset()) {
			currentMaxSequenceGroup++;
			EbmsProcessor.core.log
					.debug("Ordered RESET message with new sequence group "
							+ currentMaxSequenceGroup + " for message: "
							+ ebxmlRequestMessage.getMessageId());
		} else {
			EbmsProcessor.core.log.debug("Ordered message with sequence group "
					+ currentMaxSequenceGroup + " for message: "
					+ ebxmlRequestMessage.getMessageId());
		}
		messageDVO.setSequenceGroup(currentMaxSequenceGroup);

		RepositoryDVO repositoryDVO = message.getRepositoryDVO();
		if (contentType != null) {
			repositoryDVO.setContentType(contentType);
		}
		OutboxDVO outboxDVO = message.getOutboxDVO();

		MessageServerDAO messageServerDAO = (MessageServerDAO) EbmsProcessor.core.dao
				.createDAO(MessageServerDAO.class);
		messageServerDAO.storeOutboxMessage(messageDVO, repositoryDVO,
				outboxDVO, null);
		EbmsProcessor.core.log.info("Store outgoing ordered message: "
				+ ebxmlRequestMessage.getMessageId());

	}

	/**
	 * @param messageCPADVO
	 * @return
	 * @throws DAOException
	 */
	private boolean isResetAllowed(MessageDVO messageCPADVO)
			throws DAOException {
		MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
				.createDAO(MessageDAO.class);
		messageCPADVO
				.setStatus(MessageClassifier.INTERNAL_STATUS_DELIVERY_FAILURE);
		// find all the failed outbox sequence messageDVO using cpa
		List<?> failedList = messageDAO
				.findOrderedMessagesByMessageBoxAndCpaAndStatus(messageCPADVO);
		if (failedList.size() != 0) {
			return false;
		}
		messageCPADVO.setStatus(MessageClassifier.INTERNAL_STATUS_PROCESSING);
		// find all the processing outbox sequence messageDVO using cpa
		List<?> processingList = messageDAO
				.findOrderedMessagesByMessageBoxAndCpaAndStatus(messageCPADVO);
		// the number of processing sequence msg more than 1
		if (processingList.size() > 1) {
			return false;
		}
		messageCPADVO
				.setStatus(MessageClassifier.INTERNAL_STATUS_PROCESSED_ERROR);
		// find all the processing outbox sequence messageDVO using cpa
		List<?> processedErrorList = messageDAO
				.findOrderedMessagesByMessageBoxAndCpaAndStatus(messageCPADVO);
		// the number of processing sequence msg more than 1
		if (processedErrorList.size() > 1) {
			return false;
		}
		return true;
	}
	
	private void storeOutgoingMessage(EbxmlMessage ebxmlRequestMessage,
			String contentType) throws DAOException {
		storeOutgoingMessage(ebxmlRequestMessage, contentType, null);
	}

	private void storeOutgoingMessage(EbxmlMessage ebxmlRequestMessage,
			String contentType, MessageDVO primalMsgDVO) throws DAOException {
		// message type classification
		MessageClassifier messageClassifier = new MessageClassifier(
				ebxmlRequestMessage);
		String messageType = messageClassifier.getMessageType();

		MessageServerDAO dao = (MessageServerDAO) EbmsProcessor.core.dao
				.createDAO(MessageServerDAO.class);
		EbxmlMessageDAOConvertor message = new EbxmlMessageDAOConvertor(
				ebxmlRequestMessage, MessageClassifier.MESSAGE_BOX_OUTBOX,
				messageType);

		MessageDVO messageDVO = message.getMessageDVO();
		messageDVO.setStatus(MessageClassifier.INTERNAL_STATUS_PENDING);
		
		if (null != primalMsgDVO) {
			messageDVO.setPrimalMessageId(primalMsgDVO.getMessageId());
		}

		RepositoryDVO repositoryDVO = message.getRepositoryDVO();
		if (contentType != null) {
			repositoryDVO.setContentType(contentType);
		}

		dao.storeOutboxMessage(messageDVO, repositoryDVO, message.getOutboxDVO(), primalMsgDVO);

		EbmsProcessor.core.log.info("Store outgoing message: "
				+ ebxmlRequestMessage.getMessageId());
	}

	private void generateAndStoreEbxmlMessage(EbxmlMessage ebxmlRequestMessage) 
			throws DAOException, SOAPException, MessageServiceHandlerException {
		generateAndStoreEbxmlMessage(ebxmlRequestMessage, null);
	}

	private void generateAndStoreEbxmlMessage(
			EbxmlMessage ebxmlRequestMessage, MessageDVO primalMsgDVO)
			throws DAOException, SOAPException, MessageServiceHandlerException {

		// find the cpa and set the related element in the ebxmlMessage
		PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao
				.createDAO(PartnershipDAO.class);
		PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO
				.createDVO();
		partnershipDVO.setCpaId(ebxmlRequestMessage.getCpaId());
		partnershipDVO.setService(ebxmlRequestMessage.getService());
		partnershipDVO.setAction(ebxmlRequestMessage.getAction());

		if (!partnershipDAO.findPartnershipByCPA(partnershipDVO)) {
			EbmsProcessor.core.log.error("Partnership not found");
			throw new MessageServiceHandlerException("Partnership not found");
		}

		// Set sync reply
		String syncReplyMode = partnershipDVO.getSyncReplyMode();
		if (null != syncReplyMode && "mshSignalsOnly".equalsIgnoreCase(syncReplyMode)) {
			ebxmlRequestMessage.addSyncReply();
		}

		// Set ack requested
		String ackRequested = partnershipDVO.getAckRequested();
		if (null != ackRequested && "always".equalsIgnoreCase(ackRequested)) {
			String ackSignRequested = partnershipDVO.getAckSignRequested();
			if (null != ackSignRequested && "always".equalsIgnoreCase(ackSignRequested)) {
				ebxmlRequestMessage.addAckRequested(true);
			} else {
				ebxmlRequestMessage.addAckRequested(false);
			}			
		}
		
		// Set message order and duplicate elimination
		String messageOrder = partnershipDVO.getMessageOrder();
		if (null != messageOrder && "Guaranteed".equalsIgnoreCase(messageOrder)) {
			// if msg order is on, find the next sequence no
			storeOutgoingOrderedMessage(ebxmlRequestMessage, null);
		} else {
			if ("always".equalsIgnoreCase(partnershipDVO.getDupElimination())) {
				ebxmlRequestMessage.getMessageHeader().setDuplicateElimination();
			}
			storeOutgoingMessage(ebxmlRequestMessage, null, primalMsgDVO);
		}
	}
	
	public EbxmlMessage resendAsNew(String primalMessageId) throws Exception {
		MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao.createDAO(MessageDAO.class);
		MessageDVO primalMsgDVO = (MessageDVO) messageDAO.createDVO();
		primalMsgDVO.setMessageId(primalMessageId);
		primalMsgDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_OUTBOX);
		if (!messageDAO.retrieve(primalMsgDVO)) {
	    	throw new Exception("No message found - Message Id : " + primalMessageId + ", Message Box : " + MessageClassifier.MESSAGE_BOX_OUTBOX);
		}
		
		String primalStatus = primalMsgDVO.getStatus();
		if (MessageClassifier.INTERNAL_STATUS_PENDING.equals(primalStatus) || MessageClassifier.INTERNAL_STATUS_PROCESSING.equals(primalStatus)) {
			throw new Exception("Message can only be resent as new when its status is not Pending or Processing");
		}
		
		// Check message type
		EbxmlMessage oldEbxmlMessage = EbxmlMessageDAOConvertor.getEbxmlMessage(primalMessageId, MessageClassifier.MESSAGE_BOX_OUTBOX);
		MessageClassifier msgClassifier = new MessageClassifier(oldEbxmlMessage);
		String messageType = msgClassifier.getMessageType();
		if (MessageClassifier.MESSAGE_TYPE_ACKNOWLEDGEMENT.equals(messageType)) {
			throw new Exception("Acknowledgement cannot be resent as new");
		} else if (MessageClassifier.MESSAGE_TYPE_ERROR.equals(messageType)) {
			throw new Exception("Error message cannot be resent as new");
		}
		
		// Retrieve partnership
		String partnershipId = primalMsgDVO.getPartnershipId();
		if (null == partnershipId) {
			throw new Exception("Undefined partnership");
		}
		
		PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao.createDAO(PartnershipDAO.class);
		PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
		partnershipDVO.setPartnershipId(partnershipId);
		if (!partnershipDAO.retrieve(partnershipDVO)) {
			throw new Exception("No partnership [" + partnershipId + "] is found");
		}
		
		// Check partnership setting
		if ("Guaranteed".equalsIgnoreCase(partnershipDVO.getMessageOrder())) {
			throw new Exception("Cannot resend as new when Message Order is set to \"Guaranteed\"");
		}
		
		primalMsgDVO.setHasResendAsNew("true");
		
		EbxmlMessage newEbxmlMessage = createNewEbxmlMessage(oldEbxmlMessage, partnershipDVO);
		
        generateAndStoreEbxmlMessage(newEbxmlMessage, primalMsgDVO);
        
        return newEbxmlMessage;
	}
	
	private EbxmlMessage createNewEbxmlMessage(
			EbxmlMessage originalMessage,
			PartnershipDVO partnershipDVO) throws Exception {
		try {
			EbxmlMessage newMessage = new EbxmlMessage();

			MessageHeader msgHeader = newMessage.addMessageHeader();

			msgHeader.setCpaId(partnershipDVO.getCpaId());
			msgHeader.setConversationId(originalMessage.getConversationId());
			msgHeader.setService(partnershipDVO.getService());
			msgHeader.setAction(partnershipDVO.getAction());

			String serviceType = originalMessage.getServiceType();
			if (serviceType != null && !serviceType.equals("")) {
				msgHeader.setServiceType(serviceType);
			}

			String messageId = Generator.generateMessageID();
			msgHeader.setMessageId(messageId);
			EbmsProcessor.core.log.info("Genereating message id: " + messageId);

			msgHeader.setTimestamp(EbmsUtility.getCurrentUTCDateTime());

			Iterator<?> iter;

			iter = originalMessage.getFromPartyIds();
			while (iter.hasNext()) {
				PartyId partyId = (PartyId) iter.next();
				msgHeader.addFromPartyId(partyId.getId(), partyId.getType());
			}

			iter = originalMessage.getToPartyIds();
			while (iter.hasNext()) {
				PartyId partyId = (PartyId) iter.next();
				msgHeader.addToPartyId(partyId.getId(), partyId.getType());
			}

			iter = originalMessage.getPayloadContainers();
			while (iter.hasNext()) {
				PayloadContainer payloadContainer = (PayloadContainer) iter
						.next();
				newMessage.addPayloadContainer(payloadContainer
						.getDataHandler(), payloadContainer.getContentId(),
						null);
			}
			
			return newMessage;
		} catch (Exception e) {
			EbmsProcessor.core.log.error("Error in constructing ebxml message",
					e);
			throw new Exception("Error in constructing ebxml message", e);
		}
	}
}