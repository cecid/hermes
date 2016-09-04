/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Apache License Version 2.0 [1]
 * 
 * [1] http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package hk.hku.cecid.edi.as2.service;

import hk.hku.cecid.edi.as2.AS2PlusProcessor;
import hk.hku.cecid.edi.as2.dao.MessageDAO;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.soap.SOAPRequestException;
import hk.hku.cecid.piazza.commons.soap.WebServicesAdaptor;
import hk.hku.cecid.piazza.commons.soap.WebServicesRequest;
import hk.hku.cecid.piazza.commons.soap.WebServicesResponse;

import java.util.Iterator;

import javax.xml.soap.SOAPElement;

import org.w3c.dom.Element;

/**
 * AS2MessageStatusQueryService
 * 
 * @author Donahue Sze
 *  
 */
public class AS2MessageStatusQueryService extends WebServicesAdaptor {

    public void serviceRequested(WebServicesRequest request,
            WebServicesResponse response) throws SOAPRequestException,
            DAOException {

        Element[] bodies = request.getBodies();
        String messageID = getText(bodies, "messageId");

        if (messageID == null) {
            throw new SOAPRequestException("Missing Parameters - Message ID");
        }

        AS2PlusProcessor.getInstance().getLogger()
                .info("Message status query request - Message ID: " + messageID);

        MessageDAO messageDAO = (MessageDAO) AS2PlusProcessor.getInstance().getDAOFactory()
                .createDAO(MessageDAO.class);
        MessageDVO messageDVO = (MessageDVO) messageDAO.createDVO();
        messageDVO.setMessageId(messageID);
        messageDVO.setMessageBox(MessageDVO.MSGBOX_OUT);

        if (messageDAO.retrieve(messageDVO)) {
            String status = messageDVO.getStatus();
            String statusDescription = messageDVO.getStatusDescription();
            String mdnMessageId = new String();
            String mdnStatus = new String();
            String mdnStatusDescription = new String();
            if (messageDVO.isReceiptRequested()) {
                if (messageDVO.isAcknowledged()) {
                    Iterator i = messageDAO.findMessageByOriginalMessageID(
                            messageID, MessageDVO.MSGBOX_OUT).iterator();
                    if (i.hasNext()) {
                        MessageDVO mdnMessage = (MessageDVO) i.next();
                        mdnMessageId = mdnMessage.getMessageId();
                        mdnStatus = mdnMessage.getStatus();
                        mdnStatusDescription = mdnMessage
                                .getStatusDescription();
                    }
                }
            }

            generateReply(response, status, statusDescription, mdnMessageId,
                    mdnStatus, mdnStatusDescription);
        } else {
            generateReply(response, "N/A",
                    "Message ID not found from repository", "", "", "");
        }
    }

    private void generateReply(WebServicesResponse response, String status,
            String statusDescription, String mdnMessageId, String mdnStatus,
            String mdnStatusDescription) throws SOAPRequestException {
        try {
            SOAPElement rootElement = createElement("messageInfo", "",
                    "http://service.as2.edi.cecid.hku.hk/", "MessageInfo");

            rootElement.addChildElement(createText("status", status,
                    "http://service.as2.edi.cecid.hku.hk/"));
            rootElement.addChildElement(createText("statusDescription",
                    replaceNullToEmpty(statusDescription),
                    "http://service.as2.edi.cecid.hku.hk/"));
            rootElement.addChildElement(createText("mdnMessageId",
                    replaceNullToEmpty(mdnMessageId),
                    "http://service.as2.edi.cecid.hku.hk/"));
            rootElement.addChildElement(createText("mdnStatus",
                    replaceNullToEmpty(mdnStatus),
                    "http://service.as2.edi.cecid.hku.hk/"));
            rootElement.addChildElement(createText("mdnStatusDescription",
                    replaceNullToEmpty(mdnStatusDescription),
                    "http://service.as2.edi.cecid.hku.hk/"));
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