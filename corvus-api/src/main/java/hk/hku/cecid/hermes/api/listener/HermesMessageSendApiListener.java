/* 
 * Copyright(c) 2016 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.hermes.api.listener;

import java.io.IOException;

import java.util.Base64;

import javax.activation.DataHandler;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPException;

import hk.hku.cecid.ebms.spa.EbmsUtility;
import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.pkg.MessageHeader;
import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDAO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
import hk.hku.cecid.ebms.spa.handler.MessageClassifier;
import hk.hku.cecid.ebms.spa.handler.MessageServiceHandler;
import hk.hku.cecid.ebms.spa.handler.MessageServiceHandlerException;
import hk.hku.cecid.ebms.spa.listener.EbmsRequest;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.rest.RestRequest;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.piazza.commons.activation.ByteArrayDataSource;
import hk.hku.cecid.piazza.commons.util.Generator;

/**
 * HermesMessageSendApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesMessageSendApiListener extends HermesProtocolApiListener {

    protected void processApi(RestRequest request, JsonObjectBuilder jsonBuilder) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = this.getProtocolFromPathInfo(httpRequest.getPathInfo());

        if (protocol.equalsIgnoreCase("ebms")) {
            if (httpRequest.getMethod().equalsIgnoreCase("GET")) {
                String messageId = httpRequest.getParameter("id");
                if (messageId == null) {
                    this.fillError(jsonBuilder, -1, "Missing required field: id");
                    return;
                }

                try {
                    MessageDAO msgDAO = (MessageDAO) EbmsProcessor.core.dao.createDAO(MessageDAO.class);
                    MessageDVO message = (MessageDVO) msgDAO.createDVO();
                    message.setMessageId(messageId);
                    message.setMessageBox(MessageClassifier.MESSAGE_BOX_OUTBOX);

                    if (msgDAO.findMessage(message)) {
                        String status = message.getStatus();
                        jsonBuilder.add("message_id", messageId);
                        jsonBuilder.add("status", status);
                    }
                    else {
                        this.fillError(jsonBuilder, -1, "Message with such id not found");
                        return;
                    }
                }
                catch (DAOException e) {
                    this.fillError(jsonBuilder, -1, "Error loading message status");
                    return;
                }
            }
            else if (httpRequest.getMethod().equalsIgnoreCase("POST")) {
                String partnership_id = null;
                String from_party_id = null;
                String to_party_id = null;
                String conversation_id = null;
                byte[] payload = null;

                JsonObject jsonObject = this.getJsonObjectFromRequest(httpRequest);
                if (jsonObject == null) {
                    this.fillError(jsonBuilder, -1, "Error reading request data");
                    return;
                }

                partnership_id = jsonObject.getString("partnership_id");
                from_party_id = jsonObject.getString("from_party_id");
                to_party_id = jsonObject.getString("to_party_id");
                conversation_id = jsonObject.getString("conversation_id");
                String payload_string = jsonObject.getString("payload");

                if (payload_string != null) {
                    Base64.Decoder decoder = Base64.getDecoder();
                    payload = decoder.decode(payload_string.getBytes());
                }

                if (partnership_id == null) {
                    this.fillError(jsonBuilder, -1, "Missing required field: partnership_id");
                    return;
                }
                if (from_party_id == null) {
                    this.fillError(jsonBuilder, -1, "Missing required field: from_party_id");
                    return;
                }
                if (to_party_id == null) {
                    this.fillError(jsonBuilder, -1, "Missing required field: to_party_id");
                    return;
                }
                if (conversation_id == null) {
                    this.fillError(jsonBuilder, -1, "Missing required field: conversation_id");
                    return;
                }

                EbmsRequest ebmsRequest;
                String messageId = Generator.generateMessageID();
                try {
                    PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao.createDAO(PartnershipDAO.class);
                    PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
                    partnershipDVO.setPartnershipId(partnership_id);
                    if (!partnershipDAO.retrieve(partnershipDVO)) {
                        throw new DAOException("No partnership [" + partnership_id + "] is found");
                    }

                    EbxmlMessage ebxmlMessage = new EbxmlMessage();
                    MessageHeader msgHeader = ebxmlMessage.addMessageHeader();

                    msgHeader.setCpaId(partnershipDVO.getCpaId());
                    msgHeader.setService(partnershipDVO.getService());
                    msgHeader.setAction(partnershipDVO.getAction());
                    msgHeader.addFromPartyId(from_party_id);
                    msgHeader.addToPartyId(to_party_id);
                    msgHeader.setConversationId(conversation_id);
                    msgHeader.setMessageId(messageId);
                    msgHeader.setTimestamp(EbmsUtility.getCurrentUTCDateTime());

                    if (payload != null) {
                        ByteArrayDataSource bads = new ByteArrayDataSource(payload, "application/octet");
                        DataHandler dh = new DataHandler(bads);
                        ebxmlMessage.addPayloadContainer(dh, "payload", null);
                    }

                    ebmsRequest = new EbmsRequest(request);
                    ebmsRequest.setMessage(ebxmlMessage);
                }
                catch (DAOException e) {
                    this.fillError(jsonBuilder, -1, "Error loading partinership");
                    return;
                }
                catch (SOAPException e) {
                    this.fillError(jsonBuilder, -1, "Error constructing ebXML message");
                    return;
                }

                MessageServiceHandler msh = MessageServiceHandler.getInstance();
                try {
                    msh.processOutboundMessage(ebmsRequest, null);
                } catch (MessageServiceHandlerException e) {
                    String message = "Error in passing ebms Request to msh outbound";
                    EbmsProcessor.core.log.error(message, e);
                    this.fillError(jsonBuilder, -1, message);
                    return;
                }

                jsonBuilder.add("message_id", messageId);
            }
            else {
                throw new RequestListenerException("Request method not supported");
            }
        }
        else {
            this.fillError(jsonBuilder, -1, "Protocol unknown");
        }
    }
}
