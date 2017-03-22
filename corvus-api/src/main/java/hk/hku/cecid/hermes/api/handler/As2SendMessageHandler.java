package hk.hku.cecid.hermes.api.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.servlet.http.HttpServletRequest;

import hk.hku.cecid.edi.as2.AS2Processor;
import hk.hku.cecid.edi.as2.dao.MessageDAO;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.edi.as2.dao.PartnershipDAO;
import hk.hku.cecid.edi.as2.dao.PartnershipDVO;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.edi.as2.module.PayloadCache;
import hk.hku.cecid.edi.as2.module.PayloadRepository;
import hk.hku.cecid.hermes.api.ErrorCode;
import hk.hku.cecid.hermes.api.listener.HermesAbstractApiListener;
import hk.hku.cecid.hermes.api.spa.ApiPlugin;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.rest.RestRequest;

import org.apache.commons.codec.binary.Base64;


public class As2SendMessageHandler extends MessageHandler implements SendMessageHandler {

    public As2SendMessageHandler(HermesAbstractApiListener listener) {
        super(listener);
    }

    public Map<String, Object> getMessageStatus(String messageId) {
        ApiPlugin.core.log.debug("Parameters: id=" + messageId);

        try {
            MessageDAO msgDAO = (MessageDAO) AS2Processor.core.dao.createDAO(MessageDAO.class);
            MessageDVO message = (MessageDVO) msgDAO.createDVO();
            message.setMessageId(messageId);
            message.setMessageBox(MessageDVO.MSGBOX_OUT);
            message.setAs2From("%");
            message.setAs2To("%");
            message.setStatus("%");
            message.setPrincipalId("%");

            List messages = msgDAO.findMessagesByHistory(message, MAX_NUMBER, 0);
            if (messages.size() > 0) {
                String status = ((MessageDVO) messages.get(0)).getStatus();
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
        Map<String, Object> errorObject = new HashMap<String, Object>();

        String as2From = listener.getStringFromInput(inputDict, "as2_from", errorObject);
        if (as2From == null) {
            return errorObject;
        }
        String as2To = listener.getStringFromInput(inputDict, "as2_to", errorObject);
        if (as2To == null) {
            return errorObject;
        }
        String type = listener.getStringFromInput(inputDict, "type", errorObject);
        if (type == null) {
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

        ApiPlugin.core.log.debug("Parameters: as2_from=" + as2From + ", as2_to=" + as2To +
                                 ", type=" + type + ", number of payloads=" + payloads.size());

        List<String> messageIds = new ArrayList<String>();
        try {
            PartnershipDAO partnershipDAO = (PartnershipDAO) AS2Processor.core.dao.createDAO(PartnershipDAO.class);
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
                        return listener.createError(ErrorCode.ERROR_WRITING_MESSAGE, errorMessage);
                    }
                }
            }
        }
        catch (DAOException e) {
            String errorMessage = "Error loading partnership";
            ApiPlugin.core.log.error(errorMessage, e);
            return listener.createError(ErrorCode.ERROR_READING_DATABASE, errorMessage);
        }
        catch (IOException e) {
            String errorMessage = "Error reading input";
            ApiPlugin.core.log.error(errorMessage, e);
            return listener.createError(ErrorCode.ERROR_DATA_NOT_FOUND, errorMessage);
        }

        Map<String, Object> returnObj = new HashMap<String, Object>();
        List<Object> messageIdObjs = new ArrayList<Object>();
        for (String messageId : messageIds) {
            Map<String, Object> dict = new HashMap<String, Object>();
            dict.put("id", messageId);
            messageIdObjs.add(dict);
        }

        returnObj.put("ids", messageIdObjs);
        return returnObj;
    }
}
