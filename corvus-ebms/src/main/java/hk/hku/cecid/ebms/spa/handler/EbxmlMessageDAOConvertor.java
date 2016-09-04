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
import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.pkg.MessageHeader;
import hk.hku.cecid.ebms.pkg.MessageHeader.PartyId;
import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.InboxDAO;
import hk.hku.cecid.ebms.spa.dao.InboxDVO;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.dao.OutboxDAO;
import hk.hku.cecid.ebms.spa.dao.OutboxDVO;
import hk.hku.cecid.ebms.spa.dao.RepositoryDAO;
import hk.hku.cecid.ebms.spa.dao.RepositoryDVO;
import hk.hku.cecid.ebms.spa.task.MessageValidationException;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.util.DataFormatter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Date;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/**
 * 
 * The EbxmlMessageDAOConvertor construct the necessary DAO Data from
 * EbxmlMessage.
 * 
 * @author Donahue Sze
 *  
 */
public class EbxmlMessageDAOConvertor {

    private String messageId;

    private String messageBox;

    private String messageType;

    private String fromPartyId;

    private String fromPartyRole;

    private String toPartyId;

    private String toPartyRole;

    private String cpaId;

    private String service;

    private String action;

    private String convId;

    private String refToMessageId;

    private String syncReply;

    private String dupElimination;

    private String ackRequested;

    private String ackSignRequested;

    private int sequenceNo;

    private int sequenceStatus;

    private Timestamp timeToLive;

    // principal id was initially designed for partnership user group authorization
    // it is not fully implemented
    
    // private String principalId;

    private Timestamp currentTime;

    private ByteArrayInputStream contentStream;

    private String contentType;

    /**
     * 
     * Create a new instance of Ebxml Message to DAO Data Convertor
     * 
     * @param ebxmlMessage
     *            Ebxml Message to be converted
     * @param messageBox
     *            Message box the message will be stored
     * @param messageType
     *            The type of the message
     */
    public EbxmlMessageDAOConvertor(EbxmlMessage ebxmlMessage,
            String messageBox, String messageType) {
    	try {
            setEbxmlMessageByteStream(ebxmlMessage);
            setMessage(ebxmlMessage, messageBox, messageType);
        } catch (Exception e) {
            EbmsProcessor.core.log.error(
                    "Error in converting ebxml message to dvo", e);
        }

    }

    private void setMessage(EbxmlMessage ebxmlMessage, String messageBox,
            String messageType) {
    
        // message id
        messageId = ebxmlMessage.getMessageId();

        // message box
        this.messageBox = messageBox;

        // message type (to be modified)
        this.messageType = messageType;

        // from party id
        // from party role
        Iterator fromPartyIds = ebxmlMessage.getMessageHeader()
                .getFromPartyIds();
        if (fromPartyIds.hasNext()) {
            PartyId partyId = ((MessageHeader.PartyId) fromPartyIds.next());
            fromPartyId = partyId.getId();
            fromPartyRole = partyId.getType();
        }

        // to party id
        // to party role
        Iterator toPartyIds = ebxmlMessage.getMessageHeader().getToPartyIds();
        if (toPartyIds.hasNext()) {
            PartyId partyId = ((MessageHeader.PartyId) toPartyIds.next());
            toPartyId = partyId.getId();
            toPartyRole = partyId.getType();
        }

        // cpa id
        cpaId = ebxmlMessage.getCpaId();

        // service
        service = ebxmlMessage.getService();

        // action
        action = ebxmlMessage.getAction();

        // conv id
        convId = ebxmlMessage.getConversationId();

        // ref to message id
        if (ebxmlMessage.getMessageHeader().getRefToMessageId() != null) {
            refToMessageId = ebxmlMessage.getMessageHeader()
                    .getRefToMessageId(); 
        }

        // sync reply
        syncReply = new Boolean(ebxmlMessage.getSyncReply()).toString();

        // dup elimination
        dupElimination = new Boolean(ebxmlMessage.getDuplicateElimination())
                .toString();

        // ack requested
        if (ebxmlMessage.getAckRequested() != null) {
            ackRequested = new Boolean("true").toString();
            if (ebxmlMessage.getAckRequested().getSigned()) {
                ackSignRequested = new Boolean("true").toString();
            } else {
                ackSignRequested = new Boolean("false").toString();
            }
        } else {
            ackRequested = new Boolean("false").toString();
            ackSignRequested = new Boolean("false").toString();
        }

        // sequence no & status
        if (ebxmlMessage.getMessageOrder() != null) {
            sequenceNo = ebxmlMessage.getMessageOrder().getSequenceNumber();
            sequenceStatus = ebxmlMessage.getMessageOrder().getStatus();
        } else {
            sequenceNo = -1;
            sequenceStatus = -1;
        }

        // time to live
        try{
        	if (ebxmlMessage.getTimeToLive() != null) {        	
            	timeToLive = EbmsUtility.UTC2Timestamp(ebxmlMessage.getTimeToLive());
            } else {
                timeToLive = new java.sql.Timestamp(System.currentTimeMillis());
            }	
        } catch (Exception e){
        	timeToLive = new java.sql.Timestamp(System.currentTimeMillis());
        }        

        // current time
        try{
        	// TO GMT Format
        	Date ts = DataFormatter.getInstance().parseDate(
        			ebxmlMessage.getTimestamp(),
        			"EEE MMM dd HH:mm:ss zz yyyy",
        			java.util.Locale.US);        				        	
            currentTime = new java.sql.Timestamp(ts.getTime());            
        } catch (Exception e){
        	// TO UTC Format
        	try{
        		currentTime = EbmsUtility.UTC2Timestamp(ebxmlMessage.getTimestamp());
        	}catch(Exception e1){
        		currentTime = new java.sql.Timestamp(System.currentTimeMillis());
        	}
        }        	 

        // content
        byte[] contentBytes = ebxmlMessage.getBytes();
        contentStream = new ByteArrayInputStream(contentBytes);

        contentType = ebxmlMessage.getSOAPMessage().getMimeHeaders().getHeader(
                "Content-Type")[0];

    }

    /**
     * Get the message dao data
     * 
     * @return The message dao data
     * @throws DAOException
     */
    public MessageDVO getMessageDVO() throws DAOException {
        MessageDAO dao = (MessageDAO) EbmsProcessor.core.dao
                .createDAO(MessageDAO.class);

        MessageDVO messageDVO = (MessageDVO) dao.createDVO();
        messageDVO.setAckRequested(ackRequested);
        messageDVO.setAckSignRequested(ackSignRequested);
        messageDVO.setAction(action);
        messageDVO.setConvId(convId);
        messageDVO.setCpaId(cpaId);
        messageDVO.setDupElimination(dupElimination);
        messageDVO.setFromPartyId(fromPartyId);
        messageDVO.setFromPartyRole(fromPartyRole);
        messageDVO.setMessageId(messageId);
        messageDVO.setMessageBox(messageBox);
        messageDVO.setMessageType(messageType);
        messageDVO.setRefToMessageId(refToMessageId);
        messageDVO.setSequenceNo(sequenceNo);
        messageDVO.setSequenceStatus(sequenceStatus);
        // default is -1. if sequence no exist, update later
        messageDVO.setSequenceGroup(-1);
        messageDVO.setService(service);
        messageDVO.setSyncReply(syncReply);
        messageDVO.setTimeStamp(currentTime);
        messageDVO.setTimeToLive(timeToLive);
        messageDVO.setToPartyId(toPartyId);
        messageDVO.setToPartyRole(toPartyRole);

        return messageDVO;
    }

    /**
     * Get the repository DAO data
     * 
     * @return The repository DAO data
     * @throws DAOException
     */
    public RepositoryDVO getRepositoryDVO() throws DAOException {
        RepositoryDAO dao = (RepositoryDAO) EbmsProcessor.core.dao
                .createDAO(RepositoryDAO.class);

        RepositoryDVO repositoryDVO = (RepositoryDVO) dao.createDVO();
        repositoryDVO.setMessageId(messageId);
        repositoryDVO.setContent(contentStream);
        repositoryDVO.setTimeStamp(currentTime);
        repositoryDVO.setMessageBox(messageBox);
        repositoryDVO.setContentType(contentType);

        return repositoryDVO;
    }

    /**
     * Get the inbox DAO data
     * 
     * @return The inbox DAO data
     * @throws DAOException
     */
    public InboxDVO getInboxDVO() throws DAOException {
        InboxDAO dao = (InboxDAO) EbmsProcessor.core.dao
                .createDAO(InboxDAO.class);

        InboxDVO inboxDVO = (InboxDVO) dao.createDVO();
        inboxDVO.setMessageId(messageId);

        return inboxDVO;
    }

    /**
     * Get the outbox DAO data
     * 
     * @return The outbox DAO data
     * @throws DAOException
     */
    public OutboxDVO getOutboxDVO() throws DAOException {
        OutboxDAO dao = (OutboxDAO) EbmsProcessor.core.dao.createDAO(OutboxDAO.class);
        OutboxDVO outboxDVO = (OutboxDVO) dao.createDVO();
        outboxDVO.setMessageId(messageId);
        outboxDVO.setRetried(0);
        return outboxDVO;
    }

    /**
     * @param ebxmlMessage
     * @throws MessageServiceHandlerException
     */
    private void setEbxmlMessageByteStream(EbxmlMessage ebxmlMessage)
            throws MessageServiceHandlerException {
        try {
            ByteArrayOutputStream ebxmlMessageByteStream = new ByteArrayOutputStream();
            ebxmlMessage.getSOAPMessage().writeTo(ebxmlMessageByteStream);
            ebxmlMessage.setBytes(ebxmlMessageByteStream.toByteArray());
            ebxmlMessageByteStream.close();
            ebxmlMessageByteStream = null;
        } catch (SOAPException e1) {
            throw new MessageServiceHandlerException(
                    "Error in setting ebxml message byte stream", e1);
        } catch (IOException e1) {
            throw new MessageServiceHandlerException(
                    "Error in setting ebxml message byte stream", e1);
        }
    }

    public static EbxmlMessage getEbxmlMessage(String messageId,
            String messageBox) throws MessageValidationException {
        try {
            // get the byte array of the ebxml message
            RepositoryDAO repositoryDAO = (RepositoryDAO) EbmsProcessor.core.dao
                    .createDAO(RepositoryDAO.class);
            RepositoryDVO repositoryDVO = (RepositoryDVO) repositoryDAO
                    .createDVO();
            repositoryDVO.setMessageId(messageId);
            repositoryDVO.setMessageBox(messageBox);
            repositoryDAO.findRepository(repositoryDVO);
            byte[] content = repositoryDVO.getContent();
            String contentType = repositoryDVO.getContentType();

            // reconstruct the ebxml message
            MimeHeaders mimeHeaders = new MimeHeaders();
            mimeHeaders.setHeader("Content-Type", contentType);
            SOAPMessage soapMessage = MessageFactory.newInstance()
                    .createMessage(mimeHeaders,
                            new ByteArrayInputStream(content));

            return new EbxmlMessage(soapMessage);
        } catch (Exception e) {
            throw new MessageValidationException(
                    "Cannot reconstruct the message " + messageId
                            + " from repository", e);
        }
    }

}
