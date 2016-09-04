/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.handler;

import hk.hku.cecid.ebms.spa.EbmsUtility;
import hk.hku.cecid.ebms.pkg.Description;
import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.pkg.ErrorList;
import hk.hku.cecid.ebms.pkg.Signature;
import hk.hku.cecid.ebms.pkg.SignatureException;
import hk.hku.cecid.ebms.pkg.SignatureHandler;
import hk.hku.cecid.ebms.pkg.validation.EbxmlMessageValidator;
import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.dao.MessageServerDAO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDAO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
import hk.hku.cecid.ebms.spa.dao.RepositoryDVO;
import hk.hku.cecid.ebms.spa.listener.EbmsRequest;
import hk.hku.cecid.ebms.spa.listener.EbmsResponse;
import hk.hku.cecid.ebms.spa.task.AgreementHandler;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.soap.SOAPRequest;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;

/**
 * @author Donahue Sze
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class InboundMessageProcessor {

    static InboundMessageProcessor inboundMessageProcessor;

    static boolean inboundMessageProcessor_initFlag = false;

    public static InboundMessageProcessor getInstance() {
        if (!inboundMessageProcessor_initFlag) {
            inboundMessageProcessor = new InboundMessageProcessor();
            inboundMessageProcessor_initFlag = true;
        }
        return inboundMessageProcessor;
    } 

    private InboundMessageProcessor() {
    }

    public void processIncomingMessage(EbmsRequest request,
            EbmsResponse response) throws MessageServiceHandlerException {

        EbxmlMessage ebxmlRequestMessage = request.getMessage();

        // Modified by Steve Chan
        // To enforce the async empty message response
        EbxmlMessage ebxmlResponseMessage = null;

        // validation of ebxml message
        try {
            EbxmlMessageValidator validator = new EbxmlMessageValidator();
            validator.validate(ebxmlRequestMessage);
        } catch (Exception e) {
            EbmsProcessor.core.log.error("Incoming message is not a valid ebxml message", e);
            ebxmlResponseMessage = processInvalidMessage(ebxmlRequestMessage,
                    false);
            response.setMessage(ebxmlResponseMessage);
            return;
        }

        // message type classification
        MessageClassifier messageClassifier = new MessageClassifier(
                ebxmlRequestMessage);
        String messageType = messageClassifier.getMessageType();
        boolean isSync = messageClassifier.isSync();

        // validation of partnership
        String partnershipId = findPartnershipId(ebxmlRequestMessage);
        
        if (partnershipId == null) {
            // unauthorized user
            // generate error msg (sync reply)
            EbmsProcessor.core.log
                    .error("Unauthorized message, no partnership is found");
            ebxmlResponseMessage = processUnauthorizedMessage(
                    ebxmlRequestMessage, false);
            response.setMessage(ebxmlResponseMessage);
            return;
        }

        EbmsProcessor.core.log.info("Incoming ebxml message received: "
                + ebxmlRequestMessage.getMessageId());

        // get content type
        String contentType = null;
        if (request.getSource() != null) {
            if (request.getSource() instanceof SOAPRequest) {
                SOAPRequest soapRequest = (SOAPRequest) request.getSource();
                if (soapRequest.getSource() instanceof HttpServletRequest) {
                    HttpServletRequest httpServletRequest = (HttpServletRequest) soapRequest
                            .getSource();
                    contentType = httpServletRequest.getHeader("Content-Type");
                }
            }
        }

        // check signature & verify it
        boolean isVerifySuccess = checkSignature(ebxmlRequestMessage);
        if (!isVerifySuccess) {
            // store process error message
            storeIncomingMessage(ebxmlRequestMessage,
                    MessageClassifier.MESSAGE_TYPE_PROCESSED_ERROR, contentType);

        	ebxmlResponseMessage = processVerificationFail(ebxmlRequestMessage,
                    isSync);
            response.setMessage(ebxmlResponseMessage);
            return;
        }

        // Check Agreement
        if (MessageServiceHandler.getInstance().isInboundAgreementCheck()) {
            EbmsProcessor.core.log
                    .info("Inbound agreement checking for interop");
            if (messageType
                    .equalsIgnoreCase(MessageClassifier.MESSAGE_TYPE_ORDER)) {
                try {
                	new AgreementHandler(
                            ebxmlRequestMessage,
                            MessageClassifier.MESSAGE_BOX_INBOX, messageType,
                            true);
                } catch (Exception e) {
                    // store process error message
                	storeIncomingMessage(ebxmlRequestMessage,
                            MessageClassifier.MESSAGE_TYPE_PROCESSED_ERROR,
                            contentType);
                	EbmsProcessor.core.log.error(
                            "Incoming message is violate the agreement", e);
                    ebxmlResponseMessage = processAgreementViolationMessage(
                            ebxmlRequestMessage, isSync);
                    response.setMessage(ebxmlResponseMessage);
                    return;
                }
            }
        }

        // check dup msg
        boolean hasReceived = checkDuplicate(ebxmlRequestMessage);

        // process dup elimination
        if (hasReceived) {
            EbmsProcessor.core.log.info("The message has received before: "
                    + ebxmlRequestMessage.getMessageId());
            if (messageClassifier.isAckRequested()) {
                // resend the acknowledgement
                ebxmlResponseMessage = processDuplicateAckRequestedMessage(
                        ebxmlRequestMessage, isSync);
                response.setMessage(ebxmlResponseMessage);
            } else {
                if (messageClassifier.isDupElimination()) {
                    // generate error message
                    EbmsProcessor.core.log
                            .info("Duplicate message received, and ignored: "
                                    + ebxmlRequestMessage.getMessageId());
                } else {
                    // simply ignore it
                    EbmsProcessor.core.log
                            .info("Duplicate message received, and ignored: "
                                    + ebxmlRequestMessage.getMessageId());
                }
            }
            return;
        }

        // check time to live
        boolean isExpired = checkExpiredMessage(ebxmlRequestMessage);
        if (isExpired) {
            // store process error message
            storeIncomingMessage(ebxmlRequestMessage,
                    MessageClassifier.MESSAGE_TYPE_PROCESSED_ERROR, contentType);
        	ebxmlResponseMessage = processExpiredMessage(ebxmlRequestMessage, isSync);
            response.setMessage(ebxmlResponseMessage);
            return;
        }

        // receive error msg
        if (messageType.equalsIgnoreCase(MessageClassifier.MESSAGE_TYPE_ERROR)) {
            EbmsProcessor.core.log.info("It is an error message: "
                    + ebxmlRequestMessage.getMessageId());
            ebxmlResponseMessage = processErrorMessage(ebxmlRequestMessage,
                    isSync, messageType, contentType);
            response.setMessage(ebxmlResponseMessage);
            return;
        }

        // receive acknowledgement
        if (messageType
                .equalsIgnoreCase(MessageClassifier.MESSAGE_TYPE_ACKNOWLEDGEMENT)) {
            EbmsProcessor.core.log.info("It is an acknowledgement message: "
                    + ebxmlRequestMessage.getMessageId());
            ebxmlResponseMessage = processAcknowledgement(ebxmlRequestMessage,
                    isSync, messageType, contentType);
            response.setMessage(ebxmlResponseMessage);
            return;
        }

        // receive ping message
        if (messageType.equalsIgnoreCase(MessageClassifier.MESSAGE_TYPE_PING)) {
            EbmsProcessor.core.log.info("It is a ping message: "
                    + ebxmlRequestMessage.getMessageId());
            ebxmlResponseMessage = processPingMessage(ebxmlRequestMessage,
                    isSync, messageType, contentType);
            response.setMessage(ebxmlResponseMessage);
            return;
        }

        // receive pong message
        if (messageType.equalsIgnoreCase(MessageClassifier.MESSAGE_TYPE_PONG)) {
            EbmsProcessor.core.log.info("It is a pong message: "
                    + ebxmlRequestMessage.getMessageId());
            ebxmlResponseMessage = processPongMessage(ebxmlRequestMessage,
                    isSync, messageType, contentType);
            response.setMessage(ebxmlResponseMessage);
            return;
        }

        // receive status request message
        if (messageType
                .equalsIgnoreCase(MessageClassifier.MESSAGE_TYPE_STATUS_REQUEST)) {
            EbmsProcessor.core.log.info("It is a status request message: "
                    + ebxmlRequestMessage.getMessageId());
            ebxmlResponseMessage = processStatusRequestMessage(
                    ebxmlRequestMessage, isSync, messageType, contentType);
            response.setMessage(ebxmlResponseMessage);
            return;
        }

        // receive status response message
        if (messageType
                .equalsIgnoreCase(MessageClassifier.MESSAGE_TYPE_STATUS_RESPONSE)) {
            EbmsProcessor.core.log.info("It is a status response message: "
                    + ebxmlRequestMessage.getMessageId());
            ebxmlResponseMessage = processStatusResponseMessage(
                    ebxmlRequestMessage, isSync, messageType, contentType);
            response.setMessage(ebxmlResponseMessage);
            return;
        }

        // receive order message
        if (messageType.equalsIgnoreCase(MessageClassifier.MESSAGE_TYPE_ORDER)) {
            EbmsProcessor.core.log.info("It is an order message: "
                    + ebxmlRequestMessage.getMessageId());
            ebxmlResponseMessage = processOrderMessage(ebxmlRequestMessage,
                    isSync, messageClassifier, messageType, contentType);
            response.setMessage(ebxmlResponseMessage);
            return;
        }

    }

    /**
     * @param ebxmlRequestMessage
     * @param isSync
     * @return
     * @throws MessageServiceHandlerException
     */
    private EbxmlMessage processAgreementViolationMessage(
            EbxmlMessage ebxmlRequestMessage, boolean isSync)
            throws MessageServiceHandlerException {
        EbxmlMessage ebxmlResponseMessage = null;
        try {
            // generate error msg
            EbxmlMessage responseMessage = SignalMessageGenerator
                    .generateErrorMessage(ebxmlRequestMessage,
                            ErrorList.CODE_INCONSISTENT,
                            ErrorList.SEVERITY_ERROR, "Agreement violation",
                            null);
            if (isSync) {
                ebxmlResponseMessage = responseMessage;
                storeOutgoingMessage(ebxmlResponseMessage);
            } else {
                sendAsyncMessage(responseMessage);
            }
        } catch (SOAPException e) {
            EbmsProcessor.core.log
                    .error("Cannot generate error message in processing agreement violation message");
            throw new MessageServiceHandlerException(
                    "Cannot generate error message in processing agreement violation message",
                    e);
        }
        return ebxmlResponseMessage;
    }

    /**
     * @throws MessageServiceHandlerException
     *  
     */
    private EbxmlMessage processInvalidMessage(
            EbxmlMessage ebxmlRequestMessage, boolean isSync)
            throws MessageServiceHandlerException {
        EbxmlMessage ebxmlResponseMessage = null;
        // generate error msg (sync reply)
        try {
            // generate error msg
            EbxmlMessage responseMessage = SignalMessageGenerator
                    .generateErrorMessage(ebxmlRequestMessage,
                            ErrorList.CODE_INCONSISTENT,
                            ErrorList.SEVERITY_ERROR,
                            "It is not a valid ebxml message", null);

            ebxmlResponseMessage = responseMessage;
            storeOutgoingMessage(ebxmlResponseMessage);

        } catch (SOAPException e) {
            EbmsProcessor.core.log
                    .error("Cannot generate error message in processing invalid message");
            throw new MessageServiceHandlerException(
                    "Cannot generate error message in processing invalid message",
                    e);
        }
        return ebxmlResponseMessage;
    }

    /**
     * @param ebxmlRequestMessage
     * @param ebxmlResponseMessage
     * @return
     * @throws MessageServiceHandlerException
     */
    private EbxmlMessage processUnauthorizedMessage(
            EbxmlMessage ebxmlRequestMessage, boolean isSync)
            throws MessageServiceHandlerException {
        EbxmlMessage ebxmlResponseMessage = null;
        // generate error msg (sync reply)
        try {
            // generate error msg
            EbxmlMessage responseMessage = SignalMessageGenerator
                    .generateErrorMessage(ebxmlRequestMessage,
                            ErrorList.CODE_SECURITY_FAILURE,
                            ErrorList.SEVERITY_ERROR, "Unauthorized Msg", null);
            ebxmlResponseMessage = responseMessage;
            storeOutgoingMessage(ebxmlResponseMessage);

        } catch (SOAPException e) {
            EbmsProcessor.core.log
                    .error("Cannot generate error message in processing unauthorized message");
            throw new MessageServiceHandlerException(
                    "Cannot generate error message in processing unauthorized message",
                    e);
        }
        return ebxmlResponseMessage;
    }

    /**
     * @param ebxmlRequestMessage
     * @param ebxmlResponseMessage
     * @return
     * @throws MessageServiceHandlerException
     */
    private EbxmlMessage processVerificationFail(
            EbxmlMessage ebxmlRequestMessage, boolean isSync)
            throws MessageServiceHandlerException {
        EbxmlMessage ebxmlResponseMessage = null;
        try {
            // generate error msg
            EbxmlMessage responseMessage = SignalMessageGenerator
                    .generateErrorMessage(ebxmlRequestMessage,
                            ErrorList.CODE_SECURITY_FAILURE,
                            ErrorList.SEVERITY_ERROR, "Security Checks Failed",
                            null);
            if (isSync) {
                ebxmlResponseMessage = responseMessage;
                storeOutgoingMessage(ebxmlResponseMessage);
            } else {
                sendAsyncMessage(responseMessage);
            }
        } catch (SOAPException e) {
            EbmsProcessor.core.log
                    .error("Cannot generate error message in processing unverified message");
            throw new MessageServiceHandlerException(
                    "Cannot generate error message in processing unverified message",
                    e);
        }
        return ebxmlResponseMessage;
    }

    /**
     * @param ebxmlRequestMessage
     * @param ebxmlResponseMessage
     * @throws MessageServiceHandlerException
     */
    private EbxmlMessage processDuplicateAckRequestedMessage(
            EbxmlMessage ebxmlRequestMessage, boolean isSync)
            throws MessageServiceHandlerException {
        EbxmlMessage ebxmlResponseMessage = null;
        // find the original acknowledgement and resend
        try {
            MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                    .createDAO(MessageDAO.class);
            MessageDVO messageDVO = (MessageDVO) messageDAO.createDVO();
            messageDVO.setRefToMessageId(ebxmlRequestMessage.getMessageId());
            messageDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_OUTBOX);
            messageDVO
                    .setMessageType(MessageClassifier.MESSAGE_TYPE_ACKNOWLEDGEMENT);
            if (messageDAO.findRefToMessage(messageDVO)) {
                if (isSync) {
                    // find the ack in repository and send directly
                    EbmsProcessor.core.log
                            .info("Previous acknowledgement found ("
                                    + messageDVO.getMessageId()
                                    + ") and reply synchronously for message: "
                                    + ebxmlRequestMessage.getMessageId());
                    try {
                        ebxmlResponseMessage = EbxmlMessageDAOConvertor
                                .getEbxmlMessage(messageDVO.getMessageId(),
                                        MessageClassifier.MESSAGE_BOX_OUTBOX);
                    } catch (Exception e) {
                        EbmsProcessor.core.log
                                .error("Cannot reconstruct the ebxml message from repository: "
                                        + messageDVO.getMessageId());
                        throw new MessageServiceHandlerException(
                                "Cannot reconstruct the ebxml message from repository: "
                                        + messageDVO.getMessageId(), e);
                    }
                } else {
                    // set status to pending, it will resend again
                    EbmsProcessor.core.log
                            .info("Previous acknowledgement found ("
                                    + messageDVO.getMessageId()
                                    + ") and reply asynchronously for message: "
                                    + ebxmlRequestMessage.getMessageId());
                    messageDVO
                            .setStatus(MessageClassifier.INTERNAL_STATUS_PENDING);
                    messageDAO.updateMessage(messageDVO);
                }
            } else {
                // acknowledgement missing (internal error) or
                // the acknowledgement is generating
                EbmsProcessor.core.log
                        .error("Acknowldegement missed. Internal server error or the acknowledgement is generating for message: "
                                + ebxmlRequestMessage.getMessageId());
                throw new MessageServiceHandlerException(
                        "Acknowldegement missed. Internal server error or the acknowledgement is generating for message: "
                                + ebxmlRequestMessage.getMessageId());
            }
        } catch (DAOException e) {
            EbmsProcessor.core.log.error(
                    "Error in processing duplicate acknowledgement requested message for message: "
                            + ebxmlRequestMessage.getMessageId(), e);
            throw new MessageServiceHandlerException(
                    "Error in processing duplicate acknowledgement requested message for message: "
                            + ebxmlRequestMessage.getMessageId(), e);
        }
        return ebxmlResponseMessage;
    }

    /**
     * @param ebxmlRequestMessage
     * @return
     * @throws MessageServiceHandlerException
     */
    private boolean checkExpiredMessage(EbxmlMessage ebxmlRequestMessage)
    		throws MessageServiceHandlerException {
    	String ttlString = ebxmlRequestMessage.getTimeToLive();
    	if (ttlString != null) {
    		try{
    			Calendar ebxmlCal;
				try{
		    		ebxmlCal = EbmsUtility.UTC2Calendar(ttlString);
				} catch (Exception ex){
					ebxmlCal = EbmsUtility.GMT2Calender(ttlString);
				}        			       	
				// Transformation System Clock calendar to sender GMT.	        		
		    	Calendar sysTzCal = Calendar.getInstance(TimeZone.getDefault());
		
		    	return ebxmlCal.getTime().before(sysTzCal.getTime());        		
    		}
    		catch(Exception e){	
				EbmsProcessor.core.log.info(
	                    "Cannot convert time to live for message: "
                        + ebxmlRequestMessage.getMessageId() + " with format: " + ttlString, e);
	            throw new MessageServiceHandlerException(
	                    "Cannot convert time to live for message: "
                        + ebxmlRequestMessage.getMessageId() + " with format: " + ttlString, e);
    		}         
    	}
    	return false;
    }

    /**
     * @param ebxmlRequestMessage
     * @return
     * @throws MessageServiceHandlerException
     */
    private EbxmlMessage processExpiredMessage(
            EbxmlMessage ebxmlRequestMessage, boolean isSync)
            throws MessageServiceHandlerException {
        EbxmlMessage ebxmlResponseMessage = null;
        try {
            EbxmlMessage errorMessage = SignalMessageGenerator
                    .generateErrorMessage(ebxmlRequestMessage,
                            ErrorList.CODE_TIME_TO_LIVE_EXPIRED,
                            ErrorList.SEVERITY_ERROR,
                            "TimeToLive value expired", null);
            if (isSync) {
                ebxmlResponseMessage = errorMessage;
                storeOutgoingMessage(ebxmlResponseMessage);
            } else {
                sendAsyncMessage(errorMessage);
            }
        } catch (SOAPException e) {
            EbmsProcessor.core.log
                    .error("Cannot generate error msg in processing expired message: "
                            + ebxmlRequestMessage.getMessageId());
            throw new MessageServiceHandlerException(
                    "Cannot generate error msg in processing expired message: "
                            + ebxmlRequestMessage.getMessageId(), e);
        }
        return ebxmlResponseMessage;
    }

    /**
     * @param ebxmlRequestMessage
     * @return
     * @throws MessageServiceHandlerException
     */
    private EbxmlMessage processStatusResponseMessage(
            EbxmlMessage ebxmlRequestMessage, boolean isSync,
            String messageType, String contentType)
            throws MessageServiceHandlerException {

    	EbxmlMessage ebxmlResponseMessage = null;
        boolean hasRefMessageId = checkRefToMessage(ebxmlRequestMessage);
        if (hasRefMessageId) {
            // store the response msg in repository
            storeIncomingMessage(ebxmlRequestMessage, messageType, contentType);

        } else {
            EbmsProcessor.core.log.error("Cannot find the ref to message: "
                    + ebxmlRequestMessage.getMessageId());

            // store process error message
            storeIncomingMessage(ebxmlRequestMessage,
                    MessageClassifier.MESSAGE_TYPE_PROCESSED_ERROR, contentType);
            
            // generate error msg
            EbxmlMessage responseMessage;
            try {
                responseMessage = SignalMessageGenerator.generateErrorMessage(
                        ebxmlRequestMessage,
                        ErrorList.CODE_VALUE_NOT_RECOGNIZED,
                        ErrorList.SEVERITY_ERROR, "Unknown Ref To Message Id",
                        null);
            } catch (SOAPException e) {
                EbmsProcessor.core.log
                        .error("Cannot generate error msg in processing unknown status response message: "
                                + ebxmlRequestMessage.getMessageId());
                throw new MessageServiceHandlerException(
                        "Cannot generate error msg in processing unknown status response message: "
                                + ebxmlRequestMessage.getMessageId(), e);
            }
            if (isSync) {
                ebxmlResponseMessage = responseMessage;
                storeOutgoingMessage(ebxmlResponseMessage);
            } else {
                sendAsyncMessage(responseMessage);
            }
        }
        return ebxmlResponseMessage;
    }

    /**
     * @param ebxmlRequestMessage
     * @return
     * @throws MessageServiceHandlerException
     */
    private EbxmlMessage processStatusRequestMessage(
            EbxmlMessage ebxmlRequestMessage, boolean isSync,
            String messageType, String contentType)
            throws MessageServiceHandlerException {

    	// store the request message in repository
        storeIncomingMessage(ebxmlRequestMessage, messageType, contentType);

    	EbxmlMessage ebxmlResponseMessage = null;
        try {
            MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                    .createDAO(MessageDAO.class);
            MessageDVO messageDVO = (MessageDVO) messageDAO.createDVO();
            messageDVO.setMessageId(ebxmlRequestMessage.getMessageHeader()
                    .getRefToMessageId());
            messageDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_INBOX);
            EbxmlMessage responseMessage = null;
            Date timestamp = new Date();
            String status = new String();
            // map the message status obey specification
            if (messageDAO.findMessage(messageDVO)) {
                if (messageDVO.getStatus().equals(
                        MessageClassifier.INTERNAL_STATUS_PENDING)
                        || messageDVO.getStatus().equals(
                                MessageClassifier.INTERNAL_STATUS_PROCESSED)) {
                    status = MessageClassifier.STATUS_RECEIVED;
                } else if (messageDVO.getStatus().equals(
                        MessageClassifier.INTERNAL_STATUS_DELIVERED)
                        || messageDVO
                                .getStatus()
                                .equals(
                                        MessageClassifier.INTERNAL_STATUS_PROCESSED_ERROR)) {
                    status = MessageClassifier.STATUS_PROCESSED;
                }
                timestamp = messageDVO.getTimeStamp();
            } else {
                status = MessageClassifier.STATUS_NOT_RECOGNIZED;
            }
            responseMessage = SignalMessageGenerator
                    .generateStatusResponseMessage(ebxmlRequestMessage, status,
                            timestamp);
            if (isSync) {
                ebxmlResponseMessage = responseMessage;
                storeOutgoingMessage(ebxmlResponseMessage);
            } else {
                sendAsyncMessage(responseMessage);
            }

        } catch (Exception e) {
            EbmsProcessor.core.log
                    .error("Cannot generate status response message: "
                            + ebxmlRequestMessage.getMessageId());
            throw new MessageServiceHandlerException(
                    "Cannot generate status response message: "
                            + ebxmlRequestMessage.getMessageId(), e);
        }
        return ebxmlResponseMessage;
    }

    /**
     * @param ebxmlRequestMessage
     * @return
     * @throws MessageServiceHandlerException
     */
    private EbxmlMessage processPongMessage(
    		EbxmlMessage ebxmlRequestMessage,
            boolean isSync, String messageType, 
            String contentType) 
    	throws MessageServiceHandlerException {

    	EbxmlMessage ebxmlResponseMessage = null;
        boolean hasRefMessageId = checkRefToMessage(ebxmlRequestMessage);
        if (hasRefMessageId) {
            // store the pong msg in repository
            storeIncomingMessage(ebxmlRequestMessage, messageType, contentType);
        	
        } else {
            EbmsProcessor.core.log.error("Cannot find the ref to message: : "
                    + ebxmlRequestMessage.getMessageId());

            // store process error message
            storeIncomingMessage(ebxmlRequestMessage,
                    MessageClassifier.MESSAGE_TYPE_PROCESSED_ERROR, contentType);
            
            // generate error msg
            EbxmlMessage responseMessage;
            try {
                responseMessage = SignalMessageGenerator.generateErrorMessage(
                        ebxmlRequestMessage,
                        ErrorList.CODE_VALUE_NOT_RECOGNIZED,
                        ErrorList.SEVERITY_ERROR, "Unknown Ref To Message Id",
                        null);
            } catch (SOAPException e) {
                EbmsProcessor.core.log
                        .error("Cannot generate error msg in processing invalid pong message: "
                                + ebxmlRequestMessage.getMessageId());
                throw new MessageServiceHandlerException(
                        "Cannot generate error msg in processing invalid pong message: "
                                + ebxmlRequestMessage.getMessageId(), e);
            }
            if (isSync) {
                ebxmlResponseMessage = responseMessage;
                storeOutgoingMessage(ebxmlResponseMessage);
            } else {
                sendAsyncMessage(responseMessage);
            }
        }
        return ebxmlResponseMessage;
    }

    /**
     * @param ebxmlRequestMessage
     * @return
     * @throws MessageServiceHandlerException
     */
    private EbxmlMessage processPingMessage(
    		EbxmlMessage ebxmlRequestMessage,
            boolean isSync, String messageType, 
            String contentType) 
    	throws MessageServiceHandlerException {

    	// store the ping msg in repository
        storeIncomingMessage(ebxmlRequestMessage, messageType, contentType);
    	
        EbxmlMessage ebxmlResponseMessage = null;
        try {
            EbxmlMessage responseMessage = SignalMessageGenerator
                    .generatePongMessage(ebxmlRequestMessage);
            if (isSync) {
                ebxmlResponseMessage = responseMessage;
                storeOutgoingMessage(ebxmlResponseMessage);
            } else {
                sendAsyncMessage(responseMessage);
            }
        } catch (SOAPException e) {
            EbmsProcessor.core.log.error("Cannot generate pong message: "
                    + ebxmlRequestMessage.getMessageId());
            throw new MessageServiceHandlerException(
                    "Cannot generate pong message: "
                            + ebxmlRequestMessage.getMessageId(), e);
        }
        return ebxmlResponseMessage;
    }

    /**
     * Process the incoming <code>EbXML Acknowledgment Message</code>.
     * 
     * @param ebxmlRequestMessage
     * 			The dispatched incoming <code>EbXML Message</code>. 
     * @param isSync
     * 			Whether the <code>EbXML Response message</code> returns 
     * 			in HTTP/SOAP response or different HTTP/SOAP connection.
     * @param messageType
     * 			the value equal to MessageClassifier.MESSAGE_TYPE_ACKNOWLEDGEMENT.
     * @param contentType 
     *  
     * @return 	return Null if <code>isSync</code> is set to false.
     * 			otherwise return the <code>EbXML Response Message</code>.
     * 
     * @throws MessageServiceHandlerException
     * 			When unable to generate the <code>EbXML error message</code>
     */
    private EbxmlMessage processAcknowledgement(
			EbxmlMessage ebxmlRequestMessage, boolean isSync,
			String messageType, String contentType)
			throws MessageServiceHandlerException {

    	// Since H20 01062007, the checkRefToMessage has been inlined for new handling in acknowledgment.
    	EbxmlMessage ebxmlResponseMessage = null;
    	String ackID = ebxmlRequestMessage.getMessageId();
    	MessageDAO msgDAO = null;
    	MessageDVO refToMsgDVO = null;
    	boolean hasRefMessageId = false;
    	try {
    		// Try to query the ORDER message corresponding to this ACK message.
    		if (ebxmlRequestMessage.getAcknowledgment() != null &&
    				ebxmlRequestMessage.getAcknowledgment().getRefToMessageId() != null) {
    			msgDAO = (MessageDAO) EbmsProcessor.core.dao.createDAO(MessageDAO.class);
    			refToMsgDVO	= (MessageDVO) msgDAO.createDVO();
    			refToMsgDVO.setMessageId(ebxmlRequestMessage.getAcknowledgment().getRefToMessageId());
    			refToMsgDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_OUTBOX);
    			hasRefMessageId = msgDAO.findMessage(refToMsgDVO);
    		}
    		
    		// If the corresponding ORDER message is found, Update the message status if 
    		// it's acknowledgement or error is received.
    		if (hasRefMessageId){    		 
				if (ebxmlRequestMessage.getAcknowledgment() != null){
					if (!refToMsgDVO.getStatus().equalsIgnoreCase(
						 MessageClassifier.INTERNAL_STATUS_DELIVERY_FAILURE)){
						// If it is failed already..don't mark it as deliveried 
						// it is because it is timeout failure
		            	EbmsProcessor.core.log.info(
		              		  "Reliable message (" 
		              		+ refToMsgDVO.getMessageId()
		              		+ ") - acknowledgement received with message id: " + ackID);             	
						
						refToMsgDVO.setStatus(MessageClassifier.INTERNAL_STATUS_PROCESSED);
						refToMsgDVO.setStatusDescription("Acknowledgement is received");
						refToMsgDVO.setTimeoutTimestamp(null);
						msgDAO.updateMessage(refToMsgDVO);
					} else {
						EbmsProcessor.core.log.info(
							  "Reliable message (" 
			              	+ refToMsgDVO.getMessageId()
			           		+ ") - has been timed-out already");						
					}
				} else if (ebxmlRequestMessage.getErrorList() != null){
					
					EbmsProcessor.core.log.info(
		          		  "Reliable message ("
		                   + refToMsgDVO.getMessageId()
		                   + ") - error message received with message id: " + ackID);
					
					refToMsgDVO.setStatus(MessageClassifier.INTERNAL_STATUS_PROCESSED_ERROR);
					refToMsgDVO.setTimeoutTimestamp(null);
					StringBuffer sb = new StringBuffer();
					Iterator i = ebxmlRequestMessage.getErrorList().getErrors();
					while (i.hasNext()) {
						ErrorList.Error error = (ErrorList.Error) i.next();
						Description description = error.getDescription();
						sb.append(error.getErrorCode() + ": " + description.getDescription());
					}
					refToMsgDVO.setStatusDescription(sb.toString());
					
					msgDAO.updateMessage(refToMsgDVO);
				}
				// Store the ACK / ERROR in persistence storage.
				storeIncomingMessage(ebxmlRequestMessage, messageType, contentType);
				
				// Clear the ORDER message in Outbox.
				MessageServerDAO msgServerDAO = (MessageServerDAO) EbmsProcessor.core.dao
	            	.createDAO(MessageServerDAO.class);
	            msgServerDAO.clearMessage(refToMsgDVO);
	        // If the corresponding ORDER message isn't found, Create an error message
	        // and send back to recipient.
    		} else {
    			EbmsProcessor.core.log.error("Cannot find the ref to message: " + ackID);
                // Store processed error message
                storeIncomingMessage(ebxmlRequestMessage,
                    	MessageClassifier.MESSAGE_TYPE_PROCESSED_ERROR, contentType);

    			// Generate error message
                EbxmlMessage responseMessage;
                try {
                    responseMessage = SignalMessageGenerator.generateErrorMessage(
                    	ebxmlRequestMessage,
                    	ErrorList.CODE_VALUE_NOT_RECOGNIZED,
                    	ErrorList.SEVERITY_ERROR, "Unknown Ref To Message Id", null);
                } catch (SOAPException se) {
                	String detail = "Cannont generate error message in processing acknolwedgment message: " + ackID; 
                    EbmsProcessor.core.log.error(detail);
                    throw new MessageServiceHandlerException(detail, se);
                }
                // Send back the error message whether using same connection (sync | async)
                if (isSync) {
                    ebxmlResponseMessage = responseMessage;
                    storeOutgoingMessage(ebxmlResponseMessage);
                } else {
                    sendAsyncMessage(responseMessage);
                }    			
    		}
        } catch (DAOException daoe){
        	String detail = "Error in checking the reference to message: " + ackID;
        	EbmsProcessor.core.log.error(detail, daoe);    
        	throw new MessageServiceHandlerException(detail, daoe);
        }
        return ebxmlResponseMessage;
    }

    /**
     * @param ebxmlRequestMessage
     * @return
     * @throws MessageServiceHandlerException
     */
    private EbxmlMessage processErrorMessage(
    		EbxmlMessage ebxmlRequestMessage,
            boolean isSync, String messageType, 
            String contentType) throws MessageServiceHandlerException {
    	EbxmlMessage ebxmlResponseMessage = null;
        boolean hasRefMessageId = checkRefToMessage(ebxmlRequestMessage);
        if (hasRefMessageId) {
            // store the error message in repository
        	storeIncomingMessage(ebxmlRequestMessage, messageType, contentType);

        } else {
            EbmsProcessor.core.log.error("Cannot find the ref to message: : "
                    + ebxmlRequestMessage.getMessageId());
            // store process error message
            storeIncomingMessage(ebxmlRequestMessage,
                    MessageClassifier.MESSAGE_TYPE_PROCESSED_ERROR, contentType);
        }
        return ebxmlResponseMessage;
    }

    /**
     * @param ebxmlRequestMessage
     * @return
     * @throws MessageServiceHandlerException
     * @throws SignatureException
     */
    private EbxmlMessage processOrderMessage(
    		EbxmlMessage ebxmlRequestMessage,
            boolean isSync, MessageClassifier messageClassifier,
            String messageType, String contentType)
            throws MessageServiceHandlerException {
    	EbxmlMessage ebxmlResponseMessage = null;
        if (messageClassifier.isMessageOrder()) {
        	ebxmlResponseMessage = processMessageOrderMessage(
                    ebxmlRequestMessage, isSync, messageClassifier,
                    messageType, contentType);
        } else if (messageClassifier.isAckRequested()) {
            // store the ack request message in repository
            storeIncomingMessage(ebxmlRequestMessage, messageType, contentType);
        	
            try {
                EbxmlMessage responseMessage = SignalMessageGenerator
                        .generateAcknowledgment(ebxmlRequestMessage);
                if (ebxmlRequestMessage.getAckRequested().getSigned()) {
                    EbmsProcessor.core.log.info("Sign the acknowledgement ("
                            + responseMessage.getMessageId()
                            + ") for message: "
                            + ebxmlRequestMessage.getMessageId());
                    signAcknowledgement(responseMessage);
                }
                if (isSync) {
                    ebxmlResponseMessage = responseMessage;
                    storeOutgoingMessage(ebxmlResponseMessage);
                } else {
                    sendAsyncMessage(responseMessage);
                }
            } catch (Exception e) {
                EbmsProcessor.core.log
                        .error("Cannot generate acknowledgement for message: "
                                + ebxmlRequestMessage.getMessageId());
                throw new MessageServiceHandlerException(
                        "Cannot generate acknowledgement for message: "
                                + ebxmlRequestMessage.getMessageId(), e);
            }
        } else {
            // store the order message in repository
            storeIncomingMessage(ebxmlRequestMessage, messageType, contentType);
        }
        return ebxmlResponseMessage;
    }

    /**
     * @param ebxmlRequestMessage
     * @param ebxmlResponseMessage
     * @return
     * @throws MessageServiceHandlerException
     */
    private synchronized EbxmlMessage processMessageOrderMessage(
            EbxmlMessage ebxmlRequestMessage, boolean isSync,
            MessageClassifier messageClassifier, String messageType,
            String contentType)
            throws MessageServiceHandlerException {

    	EbxmlMessage ebxmlResponseMessage = null;
        if (!isSync) {
            if (messageClassifier.isAckRequested()) {
                if (messageClassifier.isSeqeunceStatusReset()) {

                    if (!isFirstResetMessage(ebxmlRequestMessage)
                            && hasPendingMessage(ebxmlRequestMessage)) {
                        // store process error message
                        storeIncomingMessage(ebxmlRequestMessage,
                                MessageClassifier.MESSAGE_TYPE_PROCESSED_ERROR, contentType);

                        // if pending msg exist -> error
                        String errMessage = "Pending message exist, cannot reset the sequence number for message: "
                                + ebxmlRequestMessage.getMessageId();
                        EbmsProcessor.core.log.error(errMessage);
                        sendAsyncErrorMessage(ebxmlRequestMessage, errMessage);
                    } else {
                        storeIncomingOrderedMessage(ebxmlRequestMessage,
                                messageClassifier, messageType, contentType);

                        // else reset allowed -> generate acknowledgement
                        try {
                            EbxmlMessage responseMessage = SignalMessageGenerator
                                    .generateAcknowledgment(ebxmlRequestMessage);
                            if (ebxmlRequestMessage.getAckRequested()
                                    .getSigned()) {
                                EbmsProcessor.core.log
                                        .info("Sign the acknowledgement ("
                                                + responseMessage
                                                        .getMessageId()
                                                + ") for message: "
                                                + ebxmlRequestMessage
                                                        .getMessageId());
                                signAcknowledgement(responseMessage);
                            }
                            sendAsyncMessage(responseMessage);
                        } catch (Exception e) {
                            EbmsProcessor.core.log
                                    .error("Cannot generate acknowledgement for message: "
                                            + ebxmlRequestMessage
                                                    .getMessageId());
                            throw new MessageServiceHandlerException(
                                    "Cannot generate acknowledgement for message: "
                                            + ebxmlRequestMessage
                                                    .getMessageId(), e);
                        }
                    }
                } else {
                    if (ebxmlRequestMessage.getMessageOrder()
                            .getSequenceNumber() == 0) {
                        // This version of corvus ebms plugin does not support
                        // (seq no: 0, continue)
                        EbmsProcessor.core.log
                                .error("This version of corvus ebms plugin does not support (seq no: 0, continue) for message: "
                                        + ebxmlRequestMessage.getMessageId());
                        throw new MessageServiceHandlerException(
                                "This version of corvus ebms plugin does not support (seq no: 0, continue) for message: "
                                        + ebxmlRequestMessage.getMessageId());
                    }

                    if (hasSequenceNumber(ebxmlRequestMessage)) {
                        // store process error message
                        storeIncomingMessage(ebxmlRequestMessage,
                                MessageClassifier.MESSAGE_TYPE_PROCESSED_ERROR, contentType);
                    	
                        String errMessage = "Message order is duplicate for message: "
                                + ebxmlRequestMessage.getMessageId();
                        EbmsProcessor.core.log.error(errMessage);
                        sendAsyncErrorMessage(ebxmlRequestMessage, errMessage);
                    } else {
                        storeIncomingOrderedMessage(ebxmlRequestMessage,
                                messageClassifier, messageType, contentType);
                    	
                        try {
                            EbxmlMessage responseMessage = SignalMessageGenerator
                                    .generateAcknowledgment(ebxmlRequestMessage);
                            if (ebxmlRequestMessage.getAckRequested()
                                    .getSigned()) {
                                EbmsProcessor.core.log
                                        .info("Sign the acknowledgement ("
                                                + responseMessage
                                                        .getMessageId()
                                                + ") for message: "
                                                + ebxmlRequestMessage
                                                        .getMessageId());
                                signAcknowledgement(responseMessage);
                            }
                            sendAsyncMessage(responseMessage);
                        } catch (Exception e) {
                            EbmsProcessor.core.log
                                    .error("Cannot generate acknowledgement for message: "
                                            + ebxmlRequestMessage
                                                    .getMessageId());
                            throw new MessageServiceHandlerException(
                                    "Cannot generate acknowledgement for message: "
                                            + ebxmlRequestMessage
                                                    .getMessageId(), e);
                        }
                    }
                }
            } else {
                // store process error message
                storeIncomingMessage(ebxmlRequestMessage,
                        MessageClassifier.MESSAGE_TYPE_PROCESSED_ERROR, contentType);
            	
                // error - message order is reliable messaging
                String errMessage = "Message order is reliable messaging for message: "
                        + ebxmlRequestMessage.getMessageId();
                EbmsProcessor.core.log.error(errMessage);
                sendAsyncErrorMessage(ebxmlRequestMessage, errMessage);
            }
        } else {
            // error - message order is async (sync reply)
            try {
                // store process error message
                storeIncomingMessage(ebxmlRequestMessage,
                        MessageClassifier.MESSAGE_TYPE_PROCESSED_ERROR, contentType);

            	String errMessage = "Message order is asynchronize for message: "
                        + ebxmlRequestMessage.getMessageId();
                EbxmlMessage responseMessage = SignalMessageGenerator
                        .generateErrorMessage(ebxmlRequestMessage,
                                ErrorList.CODE_INCONSISTENT,
                                ErrorList.SEVERITY_ERROR, errMessage, null);
                EbmsProcessor.core.log.error(errMessage);
                ebxmlResponseMessage = responseMessage;
                storeOutgoingMessage(ebxmlResponseMessage);
            } catch (SOAPException e) {
                EbmsProcessor.core.log
                        .error("Cannot generate error message in processing invalid ordered message: "
                                + ebxmlRequestMessage.getMessageId());
                throw new MessageServiceHandlerException(
                        "Cannot generate error message in processing invalid ordered message: "
                                + ebxmlRequestMessage.getMessageId(), e);
            }
        }
        return ebxmlResponseMessage;
    }

    /**
     * @param ebxmlRequestMessage
     * @param messageType
     * @param principalId
     * @param contentType
     * @throws MessageServiceHandlerException
     * @throws DAOException
     */
    private void storeIncomingOrderedMessage(EbxmlMessage ebxmlRequestMessage,
            MessageClassifier messageClassifier, String messageType,
            String contentType)
            throws MessageServiceHandlerException {

    	try {
            MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                    .createDAO(MessageDAO.class);
            EbxmlMessageDAOConvertor message = new EbxmlMessageDAOConvertor(
                    ebxmlRequestMessage, MessageClassifier.MESSAGE_BOX_INBOX,
                    messageType);

            MessageDVO messageDVO = message.getMessageDVO();
            messageDVO.setStatus(MessageClassifier.INTERNAL_STATUS_PENDING);
            // update the sequence group
            int currentMaxSequenceGroup = messageDAO
                    .findMaxSequenceGroupByMessageBoxAndCpa(messageDVO);
            if (messageClassifier.isSeqeunceStatusReset()) {
                if (isFirstResetMessage(ebxmlRequestMessage)) {
                    EbmsProcessor.core.log
                            .debug("It is the first reset message: "
                                    + ebxmlRequestMessage.getMessageId());
                    currentMaxSequenceGroup = 0;
                } else {
                    currentMaxSequenceGroup++;
                }
                EbmsProcessor.core.log
                        .debug("Ordered RESET message with new sequence group "
                                + currentMaxSequenceGroup + " for message: "
                                + ebxmlRequestMessage.getMessageId());
            } else {
                if (currentMaxSequenceGroup == -1) {
                    currentMaxSequenceGroup++;
                }
                EbmsProcessor.core.log
                        .debug("Ordered message with sequence group "
                                + currentMaxSequenceGroup + " for message: "
                                + ebxmlRequestMessage.getMessageId());
            }
            messageDVO.setSequenceGroup(currentMaxSequenceGroup);

            RepositoryDVO repositoryDVO = message.getRepositoryDVO();

            MessageServerDAO messageServerDAO = (MessageServerDAO) EbmsProcessor.core.dao
                    .createDAO(MessageServerDAO.class);
            messageServerDAO.storeMessage(messageDVO, repositoryDVO);

            EbmsProcessor.core.log.info("Store incoming ordered message: "
                    + ebxmlRequestMessage.getMessageId());
        } catch (DAOException e) {
            EbmsProcessor.core.log.error(
                    "Error in storing the incoming ordered message", e);
            throw new MessageServiceHandlerException(
                    "Error in storing the incoming ordered message", e);
        }
    }

    /**
     * @param ebxmlRequestMessage
     * @throws DAOException
     * @throws SignatureException
     * @throws SOAPException
     */
    private void signAcknowledgement(EbxmlMessage responseMessage)
            throws DAOException, SOAPException, SignatureException {
        try {
            String dsAlgorithm = null;
            String mdAlgorithm = null;
            PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao
                    .createDAO(PartnershipDAO.class);
            PartnershipDVO parntershipDVO = (PartnershipDVO) partnershipDAO
                    .createDVO();
            parntershipDVO.setCpaId(responseMessage.getCpaId());
            parntershipDVO.setService(responseMessage.getService());
            parntershipDVO.setAction(responseMessage.getAction());
            if (partnershipDAO.findPartnershipByCPA(parntershipDVO)) {
                dsAlgorithm = parntershipDVO.getDsAlgorithm();
                mdAlgorithm = parntershipDVO.getMdAlgorithm();
            }
            SignatureHandler signatureHandler = MessageServiceHandler
                    .createSignatureHandler(responseMessage);
            if (MessageServiceHandler.getInstance().isSignHeaderOnly()) {
                EbmsProcessor.core.log.error("sign header only for interop");
                signatureHandler.sign(dsAlgorithm, mdAlgorithm, true);
            } else {
                signatureHandler.sign(dsAlgorithm, mdAlgorithm, false);
            }
        }
        catch (Throwable t) {
            throw new SignatureException("Unable to sign acknowledgement", t);
        }
    }

    /**
     * @param errMessage
     * @throws MessageServiceHandlerException
     */
    private void sendAsyncErrorMessage(EbxmlMessage ebxmlRequestMessage,
            String errMessage) throws MessageServiceHandlerException {
        try {
            EbxmlMessage responseMessage = SignalMessageGenerator
                    .generateErrorMessage(ebxmlRequestMessage,
                            ErrorList.CODE_INCONSISTENT,
                            ErrorList.SEVERITY_ERROR, errMessage, null);
            sendAsyncMessage(responseMessage);
        } catch (SOAPException e) {
            EbmsProcessor.core.log.error("Cannot generate error message");
            throw new MessageServiceHandlerException(
                    "Cannot generate error message", e);
        }
    }

    /**
     * @param ebxmlRequestMessage
     * @return
     * @throws MessageServiceHandlerException
     */
    private boolean isFirstResetMessage(EbxmlMessage ebxmlRequestMessage)
            throws MessageServiceHandlerException {
        if (ebxmlRequestMessage.getMessageOrder().getSequenceNumber() != 0) {
            throw new MessageServiceHandlerException(
                    "It is not a reset message");
        }

        try {
            MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                    .createDAO(MessageDAO.class);
            MessageDVO messageDVO = (MessageDVO) messageDAO.createDVO();
            messageDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_INBOX);
            messageDVO.setCpaId(ebxmlRequestMessage.getCpaId());
            messageDVO.setService(ebxmlRequestMessage.getService());
            messageDVO.setAction(ebxmlRequestMessage.getAction());
            messageDVO.setConvId(ebxmlRequestMessage.getConversationId());
            messageDVO.setSequenceNo(ebxmlRequestMessage.getMessageOrder()
                    .getSequenceNumber());

            int currentMaxSequenceGroup = messageDAO
                    .findMaxSequenceGroupByMessageBoxAndCpa(messageDVO);
            if (currentMaxSequenceGroup == -1) {
                // no any messages
                return true;
            } else if (currentMaxSequenceGroup == 0) {
                // has message in sequence group zero
                messageDVO.setSequenceGroup(currentMaxSequenceGroup);
                return !messageDAO
                        .findOrderedMessageByMessageBoxAndCpaAndSequenceGroupAndSequenceNo(messageDVO);
            } else {
                // has message and reset already
                return false;
            }
        } catch (DAOException e) {
            EbmsProcessor.core.log.error(
                    "Error in checking is first reset message: "
                            + ebxmlRequestMessage.getMessageId(), e);
            throw new MessageServiceHandlerException(
                    "Error in checking is first reset message: "
                            + ebxmlRequestMessage.getMessageId(), e);
        }
    }

    /**
     * @param ebxmlRequestMessage
     * @return
     * @throws MessageServiceHandlerException
     */
    private boolean hasSequenceNumber(EbxmlMessage ebxmlRequestMessage)
            throws MessageServiceHandlerException {
        try {
            MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                    .createDAO(MessageDAO.class);
            MessageDVO messageDVO = (MessageDVO) messageDAO.createDVO();
            messageDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_INBOX);
            messageDVO.setCpaId(ebxmlRequestMessage.getCpaId());
            messageDVO.setService(ebxmlRequestMessage.getService());
            messageDVO.setAction(ebxmlRequestMessage.getAction());
            messageDVO.setConvId(ebxmlRequestMessage.getConversationId());
            messageDVO.setSequenceNo(ebxmlRequestMessage.getMessageOrder()
                    .getSequenceNumber());

            int currentMaxSequenceGroup = messageDAO
                    .findMaxSequenceGroupByMessageBoxAndCpa(messageDVO);
            if (currentMaxSequenceGroup == -1) {
                return false;
            }
            messageDVO.setSequenceGroup(currentMaxSequenceGroup);

            return messageDAO
                    .findOrderedMessageByMessageBoxAndCpaAndSequenceGroupAndSequenceNo(messageDVO);
        } catch (DAOException e) {
            EbmsProcessor.core.log.error("Error in checking sequence number: "
                    + ebxmlRequestMessage.getMessageId(), e);
            throw new MessageServiceHandlerException(
                    "Error in checking sequence number: "
                            + ebxmlRequestMessage.getMessageId(), e);
        }
    }

    /**
     * @param ebxmlRequestMessage
     * @throws MessageServiceHandlerException
     */
    private boolean hasPendingMessage(EbxmlMessage ebxmlRequestMessage)
            throws MessageServiceHandlerException {
        try {
            MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                    .createDAO(MessageDAO.class);
            MessageDVO messageDVO = (MessageDVO) messageDAO.createDVO();
            messageDVO.setCpaId(ebxmlRequestMessage.getCpaId());
            messageDVO.setService(ebxmlRequestMessage.getService());
            messageDVO.setAction(ebxmlRequestMessage.getAction());
            messageDVO.setConvId(ebxmlRequestMessage.getConversationId());
            messageDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_INBOX);

            int currentMaxSequenceGroup = messageDAO
                    .findMaxSequenceGroupByMessageBoxAndCpa(messageDVO);
            if (currentMaxSequenceGroup == -1) {
                return false;
            }
            messageDVO.setSequenceGroup(currentMaxSequenceGroup);
            int currentNumOfMessagesInMaxSequenceGroup = messageDAO
                    .findNumOfMessagesByMessageBoxAndCpaAndSequenceGroup(messageDVO);
            int currentMaxSequenceNo = messageDAO
            		.findMaxSequenceNoByMessageBoxAndCpa(messageDVO);
            if (currentNumOfMessagesInMaxSequenceGroup == currentMaxSequenceNo + 1) {
                return false;
            } else {
                return true;
            }
        } catch (DAOException e) {
            EbmsProcessor.core.log.error("Error in checking pending message: "
                    + ebxmlRequestMessage.getMessageId(), e);
            throw new MessageServiceHandlerException(
                    "Error in checking pending message: "
                            + ebxmlRequestMessage.getMessageId(), e);
        }
    }
    
    
    private String findPartnershipId(EbxmlMessage ebxmlRequestMessage) 
    	throws MessageServiceHandlerException {
        // first - from ref to message
        // second - from receiver channel

        if (ebxmlRequestMessage.getMessageHeader().getRefToMessageId() != null) {
            // if it contains reference message id
            try {
                MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                        .createDAO(MessageDAO.class);
                MessageDVO messageDVO = (MessageDVO) messageDAO.createDVO();
                
                // if RefToMessageId is not found in eb:Header, try to retrieve from eb:Acknowledgement 
                if (ebxmlRequestMessage.getMessageHeader().getRefToMessageId() != null)
                	messageDVO.setMessageId(ebxmlRequestMessage.getMessageHeader()
                        .getRefToMessageId());
                else if (ebxmlRequestMessage.getAcknowledgment() != null &&
                		ebxmlRequestMessage.getAcknowledgment().getRefToMessageId() != null)
                	messageDVO.setMessageId(ebxmlRequestMessage.getAcknowledgment()
                            .getRefToMessageId());
                
                messageDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_OUTBOX);
                
                if (messageDAO.findMessage(messageDVO)) {
                    PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao
                    	.createDAO(PartnershipDAO.class);
                    PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO
                    	.createDVO();
                    partnershipDVO.setCpaId(messageDVO.getCpaId());
                    partnershipDVO.setService(messageDVO.getService());
                    partnershipDVO.setAction(messageDVO.getAction());
                    if (partnershipDAO.findPartnershipByCPA(partnershipDVO)) {
                        return partnershipDVO.getPartnershipId();
                    }
                }
            } catch (DAOException e) {
                EbmsProcessor.core.log.error(
                        "Error in finding the partnership of reference message id: "
                                + ebxmlRequestMessage.getMessageHeader().getRefToMessageId(), e);
                throw new MessageServiceHandlerException(
                        "Error in finding the partnership of reference message id: "
                                + ebxmlRequestMessage.getMessageHeader().getRefToMessageId(), e);
            }
        }
        
        // search in receiver channel    	
        try {
            PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao
                    .createDAO(PartnershipDAO.class);
            PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO
                    .createDVO();
            partnershipDVO.setCpaId(ebxmlRequestMessage.getCpaId());
            partnershipDVO.setService(ebxmlRequestMessage.getService());
            partnershipDVO.setAction(ebxmlRequestMessage.getAction());
            if (partnershipDAO.findPartnershipByCPA(partnershipDVO)) {
                return partnershipDVO.getPartnershipId();
            }
        } catch (DAOException e) {
            EbmsProcessor.core.log.error("Error in finding the partnership of message id: "
                    + ebxmlRequestMessage.getMessageId(), e);
            throw new MessageServiceHandlerException(
                    "Error in finding the partnership of message id: "
                            + ebxmlRequestMessage.getMessageId(), e);
        }
        return null;
    	
    }

    /**
     * @param ebxmlRequestMessage
     * @return
     * @throws MessageServiceHandlerException
     */
    /*
    private String findPrincipalId(EbxmlMessage ebxmlRequestMessage)
            throws MessageServiceHandlerException {
        // first - from ref to message
        // second - from receiver channel

        if (ebxmlRequestMessage.getMessageHeader().getRefToMessageId() != null) {
            // if it contains reference message id
            try {
                MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                        .createDAO(MessageDAO.class);
                MessageDVO messageDVO = (MessageDVO) messageDAO.createDVO();
                messageDVO.setMessageId(ebxmlRequestMessage.getMessageHeader()
                        .getRefToMessageId());
                messageDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_OUTBOX);
                if (messageDAO.findMessage(messageDVO)) {
                    return messageDVO.getPrincipalId();
                }
            } catch (DAOException e) {
                EbmsProcessor.core.log.error(
                        "Error in finding the principal id: "
                                + ebxmlRequestMessage.getMessageId(), e);
                throw new MessageServiceHandlerException(
                        "Error in finding the principal id: "
                                + ebxmlRequestMessage.getMessageId(), e);
            }
        }
        // search in receiver channel
        try {
            PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao
                    .createDAO(PartnershipDAO.class);
            PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO
                    .createDVO();
            partnershipDVO.setCpaId(ebxmlRequestMessage.getCpaId());
            partnershipDVO.setService(ebxmlRequestMessage.getService());
            partnershipDVO.setAction(ebxmlRequestMessage.getAction());
            if (partnershipDAO.findPartnershipByCPA(partnershipDVO)) {
                return partnershipDVO.getPrincipalId();
            }
        } catch (DAOException e) {
            EbmsProcessor.core.log.error("Error in finding the principal id: "
                    + ebxmlRequestMessage.getMessageId(), e);
            throw new MessageServiceHandlerException(
                    "Error in finding the principal id: "
                            + ebxmlRequestMessage.getMessageId(), e);
        }
        return null;
    }
    */
    
    /**
     * @param ebxmlRequestMessage
     * @return
     * @throws MessageServiceHandlerException
     */
    private X509Certificate findSenderCert(EbxmlMessage ebxmlRequestMessage)
            throws MessageServiceHandlerException {
        X509Certificate senderCert = null;
        // search in receiver channel
        try {
            PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao
                    .createDAO(PartnershipDAO.class);
            PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO
                    .createDVO();
            partnershipDVO.setCpaId(ebxmlRequestMessage.getCpaId());
            if (ebxmlRequestMessage.getAcknowledgment() == null) {
                partnershipDVO.setService(ebxmlRequestMessage.getService());
                partnershipDVO.setAction(ebxmlRequestMessage.getAction());
            } else {
                // it is not an order msg.
                MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                        .createDAO(MessageDAO.class);
                MessageDVO orgMessageDVO = (MessageDVO) messageDAO.createDVO();
                
                // if RefToMessageId is not found in eb:Header, try to retrieve from eb:Acknowledgement 
                if (ebxmlRequestMessage.getMessageHeader().getRefToMessageId() != null)
                	orgMessageDVO.setMessageId(ebxmlRequestMessage.getMessageHeader()
                        .getRefToMessageId());
                else if (ebxmlRequestMessage.getAcknowledgment() != null &&
                		ebxmlRequestMessage.getAcknowledgment().getRefToMessageId() != null)
                	orgMessageDVO.setMessageId(ebxmlRequestMessage.getAcknowledgment()
                            .getRefToMessageId());

                orgMessageDVO
                        .setMessageBox(MessageClassifier.MESSAGE_BOX_OUTBOX);
                if (messageDAO.findMessage(orgMessageDVO)) {
                    partnershipDVO.setService(orgMessageDVO.getService());
                    partnershipDVO.setAction(orgMessageDVO.getAction());
                }
            }

            partnershipDAO.findPartnershipByCPA(partnershipDVO);

            if (partnershipDVO.getSignCert() != null) {
                ByteArrayInputStream bais = new ByteArrayInputStream(
                        partnershipDVO.getSignCert());
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                senderCert = (X509Certificate) cf.generateCertificate(bais);
                bais.close();
                bais = null;
            } else {
                EbmsProcessor.core.log.error("Please upload the certificate");
                throw new RuntimeException("Please upload the certificate");
            }
            return senderCert;
        } catch (Exception e) {
            EbmsProcessor.core.log.error("Error in finding the certificate", e);
            throw new MessageServiceHandlerException(
                    "Error in finding the certificate", e);
        }
    }

    /**
     * @param ebxmlRequestMessage
     * @return
     * @throws MessageServiceHandlerException
     */
    private boolean checkDuplicate(EbxmlMessage ebxmlRequestMessage)
            throws MessageServiceHandlerException {
        try {
            MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                    .createDAO(MessageDAO.class);
            MessageDVO messageDVO = (MessageDVO) messageDAO.createDVO();
            messageDVO.setMessageId(ebxmlRequestMessage.getMessageId());
            messageDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_INBOX);
            return messageDAO.findMessage(messageDVO);
        } catch (DAOException e) {
            EbmsProcessor.core.log.error(
                    "Error in checking duplicate message: "
                            + ebxmlRequestMessage.getMessageId(), e);
            throw new MessageServiceHandlerException(
                    "Error in checking duplicate message", e);
        }
    }

    /**
     * @param ebxmlRequestMessage
     * @return
     */
    private boolean checkSignature(EbxmlMessage ebxmlRequestMessage) {
        try {
            SOAPEnvelope soapEnvelope = ebxmlRequestMessage.getSOAPMessage()
                    .getSOAPPart().getEnvelope();
            Name signatureName = soapEnvelope.createName(
                    Signature.ELEMENT_SIGNATURE, Signature.NAMESPACE_PREFIX_DS,
                    Signature.NAMESPACE_URI_DS);
            boolean hasSignature = soapEnvelope.getHeader().getChildElements(
                    signatureName).hasNext();

            // if it has signature, verify it
            if (!hasSignature) {
                return true;
            } else {
                SignatureHandler signatureHandler = MessageServiceHandler
                        .createSignatureHandler(ebxmlRequestMessage,
                                findSenderCert(ebxmlRequestMessage));
                if (!signatureHandler.verifyByPublicKey()) {
                    EbmsProcessor.core.log
                            .error("Signature verification fail: "
                                    + ebxmlRequestMessage.getMessageId());
                    return false;
                } else {
                    EbmsProcessor.core.log
                            .info("Signature verification success: "
                                    + ebxmlRequestMessage.getMessageId());
                    return true;
                }
            }
        } catch (Throwable e) {
            EbmsProcessor.core.log.error("Error in verifying signature", e);
            return false;
        }
    }

    private boolean checkRefToMessage(EbxmlMessage ebxmlRequestMessage)
            throws MessageServiceHandlerException {
        boolean hasRefMessageId = false;
        try {
            MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                    .createDAO(MessageDAO.class);
            MessageDVO messageDVO = (MessageDVO) messageDAO.createDVO();
            messageDVO.setMessageId(ebxmlRequestMessage.getMessageHeader()
                    .getRefToMessageId());
            messageDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_OUTBOX);
            hasRefMessageId = messageDAO.findMessage(messageDVO);

            // update the message status if it's acknowledgement or error is
            // received
            if (ebxmlRequestMessage.getAcknowledgment() != null) {
                if (!messageDVO.getStatus().equalsIgnoreCase(
                        MessageClassifier.INTERNAL_STATUS_DELIVERY_FAILURE)) {
                    // if it is failed already..don't mark it as deliveried
                    // it is because it is timeout failure
                    messageDVO
                            .setStatus(MessageClassifier.INTERNAL_STATUS_PROCESSED);
                    messageDVO.setStatusDescription("Acknowledgement is received");
                    messageDVO.setTimeoutTimestamp(null);
                    messageDAO.updateMessage(messageDVO);
                }
            } else if (ebxmlRequestMessage.getErrorList() != null) {
                messageDVO
                        .setStatus(MessageClassifier.INTERNAL_STATUS_PROCESSED_ERROR);
                StringBuffer sb = new StringBuffer();
                Iterator i = ebxmlRequestMessage.getErrorList().getErrors();
                while (i.hasNext()) {
                    ErrorList.Error error = (ErrorList.Error) i.next();
                    Description description = error.getDescription();
                    sb.append(error.getErrorCode() + ": "
                            + description.getDescription());
                }

                messageDVO.setStatusDescription(sb.toString());
                messageDVO.setTimeoutTimestamp(null);
                messageDAO.updateMessage(messageDVO);
            }
        } catch (DAOException e) {
            EbmsProcessor.core.log.error(
                    "Error in checking the reference to message: "
                            + ebxmlRequestMessage.getMessageId(), e);
            throw new MessageServiceHandlerException(
                    "Error in checking the reference to message: "
                            + ebxmlRequestMessage.getMessageId(), e);
        }
        return hasRefMessageId;
    }

    private void sendAsyncMessage(EbxmlMessage ebxmlResponseMessage)
            throws MessageServiceHandlerException {
        EbmsProcessor.core.log.info("Sending async reply message: "
                + ebxmlResponseMessage.getMessageId());
        EbmsRequest ebmsRequest = new EbmsRequest();

        ebmsRequest.setMessage(ebxmlResponseMessage);
        MessageServiceHandler.getInstance().processOutboundMessage(ebmsRequest,
                null);
    }

    private void storeIncomingMessage(EbxmlMessage ebxmlRequestMessage,
            String messageType, String contentType)
            throws MessageServiceHandlerException {

    	EbxmlMessageDAOConvertor message = new EbxmlMessageDAOConvertor(
                ebxmlRequestMessage, MessageClassifier.MESSAGE_BOX_INBOX,
                messageType);

    	try {
            MessageDVO messageDVO = message.getMessageDVO();
            messageDVO.setStatus(MessageClassifier.INTERNAL_STATUS_PENDING);
            MessageServerDAO messageServerDAO = (MessageServerDAO) EbmsProcessor.core.dao
                    .createDAO(MessageServerDAO.class);
            RepositoryDVO repositoryDVO = message.getRepositoryDVO();
            messageServerDAO.storeMessage(messageDVO, repositoryDVO);
        } catch (DAOException e) {
            EbmsProcessor.core.log.error(
                    "Error in storing the incoming message: "
                            + ebxmlRequestMessage.getMessageId(), e);
            throw new MessageServiceHandlerException(
                    "Error in storing the incoming message: "
                            + ebxmlRequestMessage.getMessageId(), e);
        }

        EbmsProcessor.core.log.info("Store the incoming message: "
                + ebxmlRequestMessage.getMessageId());
    }

    private void storeOutgoingMessage(EbxmlMessage ebxmlResponseMessage)
            throws MessageServiceHandlerException {
        MessageClassifier messageClassifier = new MessageClassifier(
                ebxmlResponseMessage);
        EbxmlMessageDAOConvertor message = new EbxmlMessageDAOConvertor(
                ebxmlResponseMessage, MessageClassifier.MESSAGE_BOX_OUTBOX,
                messageClassifier.getMessageType());

        try {
            MessageDVO messageDVO = message.getMessageDVO();
            messageDVO.setStatus(MessageClassifier.INTERNAL_STATUS_PROCESSED);
            messageDVO.setStatusDescription("Message was sent synchronously");
            MessageServerDAO messageServerDAO = (MessageServerDAO) EbmsProcessor.core.dao
                    .createDAO(MessageServerDAO.class);
            messageServerDAO.storeMessage(messageDVO, message
                    .getRepositoryDVO());
        } catch (DAOException e) {
            EbmsProcessor.core.log.error(
                    "Error in storing the outgoing message", e);
            throw new MessageServiceHandlerException(
                    "Error in storing the outgoing message", e);
        }

        // it is for sync reply msg to store a copy
        EbmsProcessor.core.log.info("Store outgoing message: "
                + ebxmlResponseMessage.getMessageId());
    }

    /**
     * Convert a string of data type "dateTime" as specified by XML-Schema Part
     * 2: Datatypes section 3.2.7 to local date/time. Only date/time represented
     * as CCYY-MM-DDThh:mm:ssZ is supported.
     * 
     * @param dateTime
     *            Date/time string in UTC.
     * 
     * @return local time representation of the given UTC time string.
     */
    private Date fromUTCString(String dateTime) {
        try {
            ArrayList parts = new ArrayList();
            int i, j;
            for (i = 0, j = 0; i < dateTime.length(); i++) {
                if ("-+:TZ.".indexOf(dateTime.charAt(i)) != -1
                        || i == dateTime.length() - 1) {
                    parts.add(dateTime.substring(j, i));
                    j = i + 1;
                }
            }

            // Check if all date/time components exist or not
            int count = parts.size();
            if (count < 6)
                return null;

            int year = Integer.parseInt((String) parts.get(0));
            int month = Integer.parseInt((String) parts.get(1));
            int day = Integer.parseInt((String) parts.get(2));
            int hour = Integer.parseInt((String) parts.get(3));
            int minute = Integer.parseInt((String) parts.get(4));
            int second = Integer.parseInt((String) parts.get(5));

            if (count == 8) {
                int hourOffset = Integer.parseInt((String) parts.get(6));
                int minOffset = Integer.parseInt((String) parts.get(7));
                if (dateTime.indexOf("+") != -1) {
                    hour -= hourOffset;
                    minute -= minOffset;
                } else {
                    hour += hourOffset;
                    minute += minOffset;
                }
            }

            final Calendar c = Calendar
                    .getInstance(TimeZone.getTimeZone("GMT"));
            c.clear();

            // In Calendar class, January = 0
            c.set(year, month - 1, day, hour, minute, second);
            return c.getTime();

        } catch (NumberFormatException nfe) {
            return null;
        }
    }
}
