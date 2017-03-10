package hk.hku.cecid.hermes.api.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPException;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.pkg.MessageHeader;
import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.EbmsUtility;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDAO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
import hk.hku.cecid.ebms.spa.handler.MessageClassifier;
import hk.hku.cecid.ebms.spa.handler.MessageServiceHandler;
import hk.hku.cecid.ebms.spa.handler.MessageServiceHandlerException;
import hk.hku.cecid.ebms.spa.listener.EbmsRequest;
import hk.hku.cecid.hermes.api.ErrorCode;
import hk.hku.cecid.hermes.api.listener.HermesAbstractApiListener;
import hk.hku.cecid.hermes.api.spa.ApiPlugin;
import hk.hku.cecid.piazza.commons.activation.ByteArrayDataSource;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.rest.RestRequest;
import hk.hku.cecid.piazza.commons.util.Generator;

import org.apache.commons.codec.binary.Base64;


public class EbmsSendMessageHandler extends MessageHandler implements SendMessageHandler {

    public EbmsSendMessageHandler(HermesAbstractApiListener listener) {
        super(listener);
    }

    public Map<String, Object> getMessageStatus(String messageId) {
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
                return listener.createError(ErrorCode.ERROR_DATA_NOT_FOUND, errorMessage);
            }
        }
        catch (DAOException e) {
            String errorMessage = "DAO exception";
            ApiPlugin.core.log.error(errorMessage, e);
            return listener.createError(ErrorCode.ERROR_READING_DATABASE, errorMessage);
        }
    }

    public Map<String, Object> sendMessage(Map<String, Object> inputDict, RestRequest sourceRequest) {
        HttpServletRequest httpRequest = (HttpServletRequest) sourceRequest.getSource();
        Map<String, Object> errorObject = new HashMap<String, Object>();

        String partnershipId = listener.getStringFromInput(inputDict, "partnership_id", errorObject);
        if (partnershipId == null) {
            return errorObject;
        }
        String fromPartyId = listener.getStringFromInput(inputDict, "from_party_id", errorObject);
        if (fromPartyId == null) {
            return errorObject;
        }
        String toPartyId = listener.getStringFromInput(inputDict, "to_party_id", errorObject);
        if (toPartyId == null) {
            return errorObject;
        }
        String conversationId = listener.getStringFromInput(inputDict, "conversation_id", errorObject);
        if (conversationId == null) {
            return errorObject;
        }

        List<byte[]> payloads = new ArrayList<byte[]>();
        if (inputDict.containsKey("payload")) {
            String payloadString = (String) inputDict.get("payload");
            try {
                payloads.add(Base64.decodeBase64(payloadString.getBytes()));
            } catch (Exception e) {
                String errorMessage = "Error parsing parameter: payload";
                ApiPlugin.core.log.error(errorMessage, e);
                return listener.createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
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
                return listener.createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
            }
        }

        ApiPlugin.core.log.debug("Parameters: partnership_id=" + partnershipId + ", from_party_id=" + fromPartyId +
                                 ", to_party_id=" + toPartyId + ", conversation_id=" + conversationId +
                                 ", number of payloads=" + payloads.size());

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

            if (payloads.size() > 0) {
                int i = 1;
                for (byte[] payload : payloads) {
                    ByteArrayDataSource bads = new ByteArrayDataSource(payload, "application/octet");
                    DataHandler dh = new DataHandler(bads);
                    ebxmlMessage.addPayloadContainer(dh, "payload-" + i, null);
                    i++;
                }
            }

            ebmsRequest = new EbmsRequest(sourceRequest);
            ebmsRequest.setMessage(ebxmlMessage);
        }
        catch (DAOException e) {
            String errorMessage = "Error loading partnership";
            ApiPlugin.core.log.error(errorMessage, e);
            return listener.createError(ErrorCode.ERROR_READING_DATABASE, errorMessage);
        }
        catch (SOAPException e) {
            String errorMessage = "Error constructing ebXML message";
            ApiPlugin.core.log.error(errorMessage, e);
            return listener.createError(ErrorCode.ERROR_WRITING_MESSAGE, errorMessage);
        }

        MessageServiceHandler msh = MessageServiceHandler.getInstance();
        try {
            msh.processOutboundMessage(ebmsRequest, null);
            ApiPlugin.core.log.info("Message sent, ID: " + messageId);
        } catch (MessageServiceHandlerException e) {
            String errorMessage = "Error in passing ebms Request to msh outbound";
            ApiPlugin.core.log.error(errorMessage, e);
            return listener.createError(ErrorCode.ERROR_SENDING_MESSAGE, errorMessage);
        }

        Map<String, Object> returnObj = new HashMap<String, Object>();
        returnObj.put("id", messageId);
        return returnObj;
    }
}
