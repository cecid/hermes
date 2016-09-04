/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.service;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.handler.MessageClassifier;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.soap.SOAPRequestException;
import hk.hku.cecid.piazza.commons.soap.WebServicesAdaptor;
import hk.hku.cecid.piazza.commons.soap.WebServicesRequest;
import hk.hku.cecid.piazza.commons.soap.WebServicesResponse;

import javax.xml.soap.SOAPElement;

import org.w3c.dom.Element;

/**
 * EbmsMessageStatusQueryService
 * 
 * @author Donahue Sze
 *  
 */
public class EbmsMessageStatusQueryService extends WebServicesAdaptor {

    public void serviceRequested(WebServicesRequest request,
            WebServicesResponse response) throws SOAPRequestException,
            DAOException {

        Element[] bodies = request.getBodies();
        String messageID = getText(bodies, "messageId");

        if (messageID == null) {
            throw new SOAPRequestException("Missing Parameters - Message ID");
        }

        EbmsProcessor.core.log
                .info("Message status query request - Message ID: " + messageID);

        MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                .createDAO(MessageDAO.class);
        MessageDVO messageDVO = (MessageDVO) messageDAO.createDVO();
        messageDVO.setMessageId(messageID);
        messageDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_OUTBOX);

        if (messageDAO.findMessage(messageDVO)) {
            String status = messageDVO.getStatus();
            String statusDescription = messageDVO.getStatusDescription();
            String ackMessageId = new String();
            String ackStatus = new String();
            String ackStatusDescription = new String();
            if (messageDVO.getAckRequested().equalsIgnoreCase("true")) {
                if (messageDVO.getStatus().equalsIgnoreCase(
                        MessageClassifier.INTERNAL_STATUS_PROCESSED)) {
                    MessageDVO ackMessageDVO = (MessageDVO) messageDAO
                            .createDVO();
                    ackMessageDVO.setRefToMessageId(messageDVO.getMessageId());
                    ackMessageDVO
                            .setMessageBox(MessageClassifier.MESSAGE_BOX_INBOX);
                    ackMessageDVO
                            .setMessageType(MessageClassifier.MESSAGE_TYPE_ACKNOWLEDGEMENT);
                    if (messageDAO.findRefToMessage(ackMessageDVO)) {
                        ackMessageId = ackMessageDVO.getMessageId();
                        ackStatus = ackMessageDVO.getStatus();
                        ackStatusDescription = ackMessageDVO
                                .getStatusDescription();
                    }
                }
            }
            generateReply(response, status, statusDescription, ackMessageId,
                    ackStatus, ackStatusDescription);
        } else {
            generateReply(response, "N/A",
                    "Message ID not found from repository", "", "", "");
        }
    }

    private void generateReply(WebServicesResponse response, String status,
            String statusDescription, String ackMessageId, String ackStatus,
            String ackStatusDescription) throws SOAPRequestException {
        try {
            SOAPElement rootElement = createElement("messageInfo", "",
                    "http://service.ebms.edi.cecid.hku.hk/", "MessageInfo");

            rootElement.addChildElement(createText("status", status,
                    "http://service.ebms.edi.cecid.hku.hk/"));
            rootElement.addChildElement(createText("statusDescription",
                    replaceNullToEmpty(statusDescription),
                    "http://service.ebms.edi.cecid.hku.hk/"));
            rootElement.addChildElement(createText("ackMessageId",
                    replaceNullToEmpty(ackMessageId),
                    "http://service.ebms.edi.cecid.hku.hk/"));
            rootElement.addChildElement(createText("ackStatus",
                    replaceNullToEmpty(ackStatus),
                    "http://service.ebms.edi.cecid.hku.hk/"));
            rootElement.addChildElement(createText("ackStatusDescription",
                    replaceNullToEmpty(ackStatusDescription),
                    "http://service.ebms.edi.cecid.hku.hk/"));
            response.setBodies(new SOAPElement[] { rootElement });
        } catch (Exception e) {
            throw new SOAPRequestException("Unable to generate reply message",
                    e);
        }
    }

    public String replaceNullToEmpty(String value) {
        if (value == null) {
            return new String("");
        } else {
            return value;
        }
    }

    protected boolean isCacheEnabled() {
        return false;
    }
}