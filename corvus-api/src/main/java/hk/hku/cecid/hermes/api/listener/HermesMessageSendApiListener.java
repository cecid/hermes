package hk.hku.cecid.hermes.api.listener;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPException;

import org.apache.commons.codec.binary.Base64;
import hk.hku.cecid.ebms.spa.EbmsUtility;
import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.pkg.MessageHeader;
import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.handler.MessageClassifier;
import hk.hku.cecid.ebms.spa.handler.MessageServiceHandler;
import hk.hku.cecid.ebms.spa.handler.MessageServiceHandlerException;
import hk.hku.cecid.ebms.spa.listener.EbmsRequest;
import hk.hku.cecid.edi.as2.AS2Processor;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.edi.as2.module.PayloadCache;
import hk.hku.cecid.edi.as2.module.PayloadRepository;
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

    public static int MAX_NUMBER = 2147483647;  

    protected Map<String, Object> processGetRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = parseFromPathInfo(httpRequest.getPathInfo(), 2).get(1);
        ApiPlugin.core.log.info("Get message sending status API invoked, protocol = " + protocol);

        String messageId = httpRequest.getParameter("id");
        if (messageId == null) {
            String errorMessage = "Missing required field: id";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
        }

        if (protocol.equalsIgnoreCase(Constants.EBMS_PROTOCOL)) {
            return getEbmsMessageStatus(messageId);
        }
        else if (protocol.equalsIgnoreCase(Constants.AS2_PROTOCOL)) {
            return getAs2MessageStatus(messageId);
        }
        else {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }
    }

    protected Map<String, Object> getEbmsMessageStatus(String messageId) {
        ApiPlugin.core.log.debug("Parameters: id=" + messageId);

        try {
            hk.hku.cecid.ebms.spa.dao.MessageDAO msgDAO = (hk.hku.cecid.ebms.spa.dao.MessageDAO) EbmsProcessor.core.dao.createDAO(hk.hku.cecid.ebms.spa.dao.MessageDAO.class);
            hk.hku.cecid.ebms.spa.dao.MessageDVO message = (hk.hku.cecid.ebms.spa.dao.MessageDVO) msgDAO.createDVO();
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

    protected Map<String, Object> getAs2MessageStatus(String messageId) {
        ApiPlugin.core.log.debug("Parameters: id=" + messageId);

        try {
            hk.hku.cecid.edi.as2.dao.MessageDAO msgDAO = (hk.hku.cecid.edi.as2.dao.MessageDAO) AS2Processor.core.dao.createDAO(hk.hku.cecid.edi.as2.dao.MessageDAO.class);
            hk.hku.cecid.edi.as2.dao.MessageDVO message = (hk.hku.cecid.edi.as2.dao.MessageDVO) msgDAO.createDVO();
            message.setMessageId(messageId);
            message.setMessageBox(hk.hku.cecid.edi.as2.dao.MessageDVO.MSGBOX_OUT);
            message.setAs2From("%");
            message.setAs2To("%");
            message.setStatus("%");
            message.setPrincipalId("%");

            List messages = msgDAO.findMessagesByHistory(message, MAX_NUMBER, 0);
            if (messages.size() > 0) {
                String status = ((hk.hku.cecid.edi.as2.dao.MessageDVO) messages.get(0)).getStatus();
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
        String protocol = parseFromPathInfo(httpRequest.getPathInfo(), 2).get(1);
        ApiPlugin.core.log.info("Send message API invoked, protocol = " + protocol);

        if (protocol.equalsIgnoreCase(Constants.EBMS_PROTOCOL)) {
            return sendEbmsMessage(httpRequest);
        }
        else if (protocol.equalsIgnoreCase(Constants.AS2_PROTOCOL)) {
            return sendAs2Message(httpRequest);
        }
        else {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }
    }

    protected Map<String, Object> sendEbmsMessage(HttpServletRequest httpRequest) {
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

        ArrayList<byte[]> payloads = new ArrayList<byte[]>();
        if (inputDict.containsKey("payload")) {
            String payloadString = (String) inputDict.get("payload");
            try {
                payloads.add(Base64.decodeBase64(payloadString.getBytes()));
            } catch (Exception e) {
                String errorMessage = "Error parsing parameter: payload";
                ApiPlugin.core.log.error(errorMessage, e);
                return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
            }
        }
        else if (inputDict.containsKey("payloads")) {
            try {
                List<Object> payloadStrings = (List<Object>) inputDict.get("payloads");
                for (Object payloadObj : payloadStrings) {
                    Map<String,Object> payloadMap = (Map<String,Object>) payloadObj;
                    if (payloadMap.containsKey("payload")) {
                    	String payloadString = (String) payloadMap.get("payload");
                    	payloads.add(Base64.decodeBase64(payloadString.getBytes()));
                    }
                }
            } catch (Exception e) {
                String errorMessage = "Error parsing parameter: payloads";
                ApiPlugin.core.log.error(errorMessage, e);
                return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
            }
        }

        ApiPlugin.core.log.debug("Parameters: partnership_id=" + partnershipId + ", from_party_id=" + fromPartyId +
                                 ", to_party_id=" + toPartyId + ", conversation_id=" + conversationId +
                                 ", number of payloads=" + payloads.size());

        EbmsRequest ebmsRequest;
        String messageId = Generator.generateMessageID();
        try {
            hk.hku.cecid.ebms.spa.dao.PartnershipDAO partnershipDAO = (hk.hku.cecid.ebms.spa.dao.PartnershipDAO) EbmsProcessor.core.dao.createDAO(hk.hku.cecid.ebms.spa.dao.PartnershipDAO.class);
            hk.hku.cecid.ebms.spa.dao.PartnershipDVO partnershipDVO = (hk.hku.cecid.ebms.spa.dao.PartnershipDVO) partnershipDAO.createDVO();
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

            if (payloads.size() > 0) {
                int i = 1;
                for (byte[] payload : payloads) {
                    ByteArrayDataSource bads = new ByteArrayDataSource(payload, "application/octet");
                    DataHandler dh = new DataHandler(bads);
                    ebxmlMessage.addPayloadContainer(dh, "payload-" + i, null);
                    i++;
                }
            }

            ebmsRequest = new EbmsRequest(httpRequest);
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

    protected Map<String, Object> sendAs2Message(HttpServletRequest httpRequest) {
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

        String as2From = null;
        try {
            as2From = (String) inputDict.get("as2_from");
            if (as2From == null) {
                String errorMessage = "Missing required partinership field: as2_from";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: as2_from";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }

        String as2To = null;
        try {
            as2To = (String) inputDict.get("as2_to");
            if (as2To == null) {
                String errorMessage = "Missing required partinership field: as2_to";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: as2_to";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }

        String type = null;
        try {
            type = (String) inputDict.get("type");
            if (type == null) {
                String errorMessage = "Missing required partinership field: type";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: type";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }

        ArrayList<byte[]> payloads = new ArrayList<byte[]>();
        if (inputDict.containsKey("payload")) {
            String payloadString = (String) inputDict.get("payload");
            try {
                payloads.add(Base64.decodeBase64(payloadString.getBytes()));
            } catch (Exception e) {
                String errorMessage = "Error parsing parameter: payload";
                ApiPlugin.core.log.error(errorMessage, e);
                return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
            }
        }
        else if (inputDict.containsKey("payloads")) {
            try {
                List<Object> payloadStrings = (List<Object>) inputDict.get("payloads");
                for (Object payloadObj : payloadStrings) {
                    Map<String,Object> payloadMap = (Map<String,Object>) payloadObj;
                    if (payloadMap.containsKey("payload")) {
                        String payloadString = (String) payloadMap.get("payload");
                        payloads.add(Base64.decodeBase64(payloadString.getBytes()));
                    }
                }
            } catch (Exception e) {
                String errorMessage = "Error parsing parameter: payloads";
                ApiPlugin.core.log.error(errorMessage, e);
                return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
            }
        }

        ApiPlugin.core.log.debug("Parameters: as2_from=" + as2From + ", as2_to=" + as2To +
                                 ", type=" + type + ", number of payloads=" + payloads.size());

        EbmsRequest ebmsRequest;
        ArrayList<String> messageIds = new ArrayList<String>();
        try {
            hk.hku.cecid.edi.as2.dao.PartnershipDAO partnershipDAO = (hk.hku.cecid.edi.as2.dao.PartnershipDAO) AS2Processor.core.dao.createDAO(hk.hku.cecid.edi.as2.dao.PartnershipDAO.class);
            if (partnershipDAO.findByParty(as2From, as2To) == null) {
                throw new DAOException("No partnership [" + as2From + ", " + as2To + "] is registered");
            }

            PayloadRepository repository = AS2Processor.getOutgoingPayloadRepository();
            if (payloads.size() > 0) {
                for (byte[] payload : payloads) {
                    ByteArrayInputStream in = new ByteArrayInputStream(payload);
                    String messageId = AS2Message.generateID();
                    messageIds.add(messageId);
                    PayloadCache cache = repository.createPayloadCache(messageId, as2From, as2To, type);
                    cache.save(in);
                    if (!cache.checkIn()) {
                        String errorMessage = "Error persisting payloads";
                        ApiPlugin.core.log.error(errorMessage);
                        return createError(ErrorCode.ERROR_WRITING_MESSAGE, errorMessage);
                    }
                }
            }
        }
        catch (DAOException e) {
            String errorMessage = "Error loading partnership";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_READING_DATABASE, errorMessage);
        }
        catch (IOException e) {
            String errorMessage = "Error reading input";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_DATA_NOT_FOUND, errorMessage);
        }

        Map<String, Object> returnObj = new HashMap<String, Object>();
        ArrayList<Object> messageIdObjs = new ArrayList<Object>();
        for (String messageId : messageIds) {
            Map<String, Object> dict = new HashMap<String, Object>();
            dict.put("id", messageId);
            messageIdObjs.add(dict);
        }

        returnObj.put("ids", messageIdObjs);
        return returnObj;
    }
}
