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
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
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
import hk.hku.cecid.piazza.commons.json.JsonParseException;
import hk.hku.cecid.piazza.commons.rest.RestRequest;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.piazza.commons.activation.ByteArrayDataSource;
import hk.hku.cecid.piazza.commons.util.Generator;
import hk.hku.cecid.hermes.api.Constants;
import hk.hku.cecid.hermes.api.ErrorCode;
import hk.hku.cecid.hermes.api.spa.ApiPlugin;


/**
 * HermesMessageSendApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesMessageSendApiListener extends HermesProtocolApiListener {

    protected Map<String, Object> processGetRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = parseFromPathInfo(httpRequest.getPathInfo()).get(1);
        ApiPlugin.core.log.info("Get message sending status API invoked, protocol = " + protocol);

        if (!protocol.equalsIgnoreCase(Constants.EBMS_PROTOCOL)) {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }

        String messageId = httpRequest.getParameter("id");
        if (messageId == null) {
            String errorMessage = "Missing required field: id";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
        }

        ApiPlugin.core.log.debug("Parameters: id=" + messageId);

        try {
            MessageDAO msgDAO = (MessageDAO) EbmsProcessor.core.dao.createDAO(MessageDAO.class);
            MessageDVO message = (MessageDVO) msgDAO.createDVO();
            message.setMessageId(messageId);
            message.setMessageBox(MessageClassifier.MESSAGE_BOX_OUTBOX);

            if (msgDAO.findMessage(message)) {
                String status = message.getStatus();
                Map<String, Object> returnObj = new HashMap<String, Object>();
                returnObj.put("message_id", messageId);
                returnObj.put("status", status);
                return returnObj;
            }
            else {
                String errorMessage = "Message with such id not found";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_DATA_NOT_FOUND, errorMessage);
            }
        }
        catch (DAOException e) {
            String errorMessage = "DAO exception";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_READING_DATABASE, errorMessage);
        }
    }

    protected Map<String, Object> processPostRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = parseFromPathInfo(httpRequest.getPathInfo()).get(1);
        ApiPlugin.core.log.info("Send message API invoked, protocol = " + protocol);

        if (!protocol.equalsIgnoreCase(Constants.EBMS_PROTOCOL)) {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }

        Map<String, Object> inputDict = null;
        try {
            inputDict = getDictionaryFromRequest(httpRequest);
        } catch (IOException e) {
            String errorMessage = "Exception while reading input stream";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_READING_REQUEST, errorMessage);
        } catch (JsonParseException e) {
            String errorMessage = "Exception while parsing input stream";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }

        String partnershipId = null;
        try {
            partnershipId = (String) inputDict.get("partnership_id");
            if (partnershipId == null) {
                String errorMessage = "Missing required partinership field: partnership_id";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: partnership_id";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }

        String fromPartyId = null;
        try {
            fromPartyId = (String) inputDict.get("from_party_id");
            if (fromPartyId == null) {
                String errorMessage = "Missing required partinership field: from_party_id";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: from_party_id";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }

        String toPartyId = null;
        try {
            toPartyId = (String) inputDict.get("to_party_id");
            if (toPartyId == null) {
                String errorMessage = "Missing required partinership field: to_party_id";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: to_party_id";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }

        String conversationId = null;
        try {
            conversationId = (String) inputDict.get("conversation_id");
            if (conversationId == null) {
                String errorMessage = "Missing required partinership field: conversation_id";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: conversation_id";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }

        String payloadString = null;
        byte[] payload = null;
        try {
            payloadString = (String) inputDict.get("payload");
            if (payloadString != null) {
                Base64.Decoder decoder = Base64.getDecoder();
                payload = decoder.decode(payloadString.getBytes());
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: payload";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }

        ApiPlugin.core.log.debug("Parameters: partnership_id=" + partnershipId + ", from_party_id=" + fromPartyId +
                                 ", to_party_id=" + toPartyId + ", conversation_id=" + conversationId +
                                 ", payload=" + payload);

        EbmsRequest ebmsRequest;
        String messageId = Generator.generateMessageID();
        try {
            PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao.createDAO(PartnershipDAO.class);
            PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
            partnershipDVO.setPartnershipId(partnershipId);
            if (!partnershipDAO.retrieve(partnershipDVO)) {
                throw new DAOException("No partnership [" + partnershipId + "] is found");
            }

            EbxmlMessage ebxmlMessage = new EbxmlMessage();
            MessageHeader msgHeader = ebxmlMessage.addMessageHeader();

            msgHeader.setCpaId(partnershipDVO.getCpaId());
            msgHeader.setService(partnershipDVO.getService());
            msgHeader.setAction(partnershipDVO.getAction());
            msgHeader.addFromPartyId(fromPartyId);
            msgHeader.addToPartyId(toPartyId);
            msgHeader.setConversationId(conversationId);
            msgHeader.setMessageId(messageId);
            msgHeader.setTimestamp(EbmsUtility.getCurrentUTCDateTime());

            if (payload != null) {
                ByteArrayDataSource bads = new ByteArrayDataSource(payload, "application/octet");
                DataHandler dh = new DataHandler(bads);
                ebxmlMessage.addPayloadContainer(dh, "payload-1", null);
            }

            ebmsRequest = new EbmsRequest(request);
            ebmsRequest.setMessage(ebxmlMessage);
        }
        catch (DAOException e) {
            String errorMessage = "Error loading partnership";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_READING_DATABASE, errorMessage);
        }
        catch (SOAPException e) {
            String errorMessage = "Error constructing ebXML message";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_WRITING_MESSAGE, errorMessage);
        }

        MessageServiceHandler msh = MessageServiceHandler.getInstance();
        try {
            msh.processOutboundMessage(ebmsRequest, null);
            ApiPlugin.core.log.info("Message sent, ID: " + messageId);
        } catch (MessageServiceHandlerException e) {
            String errorMessage = "Error in passing ebms Request to msh outbound";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_SENDING_MESSAGE, errorMessage);
        }

        Map<String, Object> returnObj = new HashMap<String, Object>();
        returnObj.put("id", messageId);
        return returnObj;
    }
}
