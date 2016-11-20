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


/**
 * HermesMessageSendApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesMessageSendApiListener extends HermesProtocolApiListener {

    protected Map<String, Object> processGetRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = getProtocolFromPathInfo(httpRequest.getPathInfo());

        if (!protocol.equalsIgnoreCase(Constants.EBMS_PROTOCOL)) {
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, "Protocol unknown");
        }

        String messageId = httpRequest.getParameter("id");
        if (messageId == null) {
            return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, "Missing required field: id");
        }

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
                return createError(ErrorCode.ERROR_DATA_NOT_FOUND, "Message with such id not found");
            }
        }
        catch (DAOException e) {
            return createError(ErrorCode.ERROR_READING_DATABASE, "DAO exception");
        }
    }

    protected Map<String, Object> processPostRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = getProtocolFromPathInfo(httpRequest.getPathInfo());

        if (!protocol.equalsIgnoreCase(Constants.EBMS_PROTOCOL)) {
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, "Protocol unknown");
        }

        Map<String, Object> inputDict = null;
        try {
            inputDict = getDictionaryFromRequest(httpRequest);
        } catch (IOException e) {
            return createError(ErrorCode.ERROR_READING_REQUEST, "Exception while reading input stream");
        } catch (JsonParseException e) {
            return createError(ErrorCode.ERROR_PARSING_REQUEST, "Exception while parsing input stream");
        }

        String partnershipId = null;
        try {
            partnershipId = (String) inputDict.get("partnership_id");
            if (partnershipId == null) {
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, "Missing required partinership field: partnership_id");
            }
        } catch (Exception e) {
            return createError(ErrorCode.ERROR_PARSING_REQUEST, "Error parsing parameter: partnership_id");
        }

        String fromPartyId = null;
        try {
            fromPartyId = (String) inputDict.get("from_party_id");
            if (fromPartyId == null) {
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, "Missing required partinership field: from_party_id");
            }
        } catch (Exception e) {
            return createError(ErrorCode.ERROR_PARSING_REQUEST, "Error parsing parameter: from_party_id");
        }

        String toPartyId = null;
        try {
            toPartyId = (String) inputDict.get("to_party_id");
            if (toPartyId == null) {
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, "Missing required partinership field: to_party_id");
            }
        } catch (Exception e) {
            return createError(ErrorCode.ERROR_PARSING_REQUEST, "Error parsing parameter: to_party_id");
        }

        String conversationId = null;
        try {
            conversationId = (String) inputDict.get("conversation_id");
            if (conversationId == null) {
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, "Missing required partinership field: conversation_id");
            }
        } catch (Exception e) {
            return createError(ErrorCode.ERROR_PARSING_REQUEST, "Error parsing parameter: conversation_id");
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
            return createError(ErrorCode.ERROR_PARSING_REQUEST, "Error parsing parameter: payload");
        }

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
            return createError(ErrorCode.ERROR_READING_DATABASE, "Error loading partnership");
        }
        catch (SOAPException e) {
            return createError(ErrorCode.ERROR_WRITING_MESSAGE, "Error constructing ebXML message");
        }

        MessageServiceHandler msh = MessageServiceHandler.getInstance();
        try {
            msh.processOutboundMessage(ebmsRequest, null);
        } catch (MessageServiceHandlerException e) {
            String message = "Error in passing ebms Request to msh outbound";
            EbmsProcessor.core.log.error(message, e);
            return createError(ErrorCode.ERROR_SENDING_MESSAGE, message);
        }

        Map<String, Object> returnObj = new HashMap<String, Object>();
        returnObj.put("id", messageId);
        return returnObj;
    }
}
