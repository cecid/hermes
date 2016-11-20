/* 
 * Copyright(c) 2016 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.hermes.api.listener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDAO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
import hk.hku.cecid.ebms.spa.handler.EbxmlMessageDAOConvertor;
import hk.hku.cecid.ebms.spa.handler.MessageClassifier;
import hk.hku.cecid.ebms.spa.task.MessageValidationException;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.json.JsonParseException;
import hk.hku.cecid.piazza.commons.rest.RestRequest;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.hermes.api.Constants;
import hk.hku.cecid.hermes.api.ErrorCode;


/**
 * HermesMessageReceiveApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesMessageReceiveApiListener extends HermesProtocolApiListener {

    public static int MAX_NUMBER = 2147483647;

    protected Map<String, Object> processGetRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = getProtocolFromPathInfo(httpRequest.getPathInfo());

        if (!protocol.equalsIgnoreCase(Constants.EBMS_PROTOCOL)) {
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, "Protocol unknown");
        }

        String partnershipId = httpRequest.getParameter("partnership_id");
        if (partnershipId == null) {
            return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, "Missing required field: partnership_id");
        }

        List results = null;
        try {
            PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao.createDAO(PartnershipDAO.class);
            PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();

            partnershipDVO.setPartnershipId(partnershipId);
            if (!partnershipDAO.retrieve(partnershipDVO)) {
                return createError(ErrorCode.ERROR_DATA_NOT_FOUND, "Cannot load partnership: " + partnershipId);
            }

            MessageDAO msgDAO = (MessageDAO) EbmsProcessor.core.dao.createDAO(MessageDAO.class);
            MessageDVO criteriaDVO = (MessageDVO) msgDAO.createDVO();
            criteriaDVO.setCpaId(partnershipDVO.getCpaId());
            criteriaDVO.setService(partnershipDVO.getService());
            criteriaDVO.setAction(partnershipDVO.getAction());
            criteriaDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_INBOX);
            results = msgDAO.findMessagesByHistory(criteriaDVO, MAX_NUMBER, 0);
        }
        catch (DAOException e) {
            return createError(ErrorCode.ERROR_DATA_NOT_FOUND, "Error loading messages");
        }

        if (results != null) {
            ArrayList<Object> messages = new ArrayList<Object>();
            for (Iterator i=results.iterator(); i.hasNext() ; ) {
                MessageDVO message = (MessageDVO) i.next();
                Map<String, Object> messageDict = new HashMap<String, Object>();
                messageDict.put("id", message.getMessageId());
                messageDict.put("timestamp", message.getTimeStamp().getTime() / 1000);
                messages.add(messageDict);
            }
            Map<String, Object> returnObj = new HashMap<String, Object>();
            returnObj.put("message_ids", messages);
            return returnObj;
        }
        else {
            return createError(ErrorCode.ERROR_DATA_NOT_FOUND, "No message can be loaded");
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

        String messageId = null;
        try {
            messageId = (String) inputDict.get("message_id");
            if (messageId == null) {
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, "Missing required partinership field: message_id");
            }
        } catch (Exception e) {
            return createError(ErrorCode.ERROR_PARSING_REQUEST, "Error parsing parameter: message_id");
        }

        try {
            MessageDAO msgDAO = (MessageDAO) EbmsProcessor.core.dao.createDAO(MessageDAO.class);
            MessageDVO message = (MessageDVO) msgDAO.createDVO();
            message.setMessageId(messageId);
            message.setMessageBox(MessageClassifier.MESSAGE_BOX_OUTBOX);

            if (msgDAO.findMessage(message)) {
                EbxmlMessage ebxmlMessage = null;
                try {
                    ebxmlMessage = EbxmlMessageDAOConvertor.getEbxmlMessage(message.getMessageId(),
                                                                            MessageClassifier.MESSAGE_BOX_INBOX);
                }
                catch (MessageValidationException e) {
                }

                if (ebxmlMessage == null) {
                    return createError(ErrorCode.ERROR_DATA_NOT_FOUND, "Unable to read message from repository");
                }

                Map<String, Object> returnObj = new HashMap<String, Object>();
                returnObj.put("id", message.getMessageId());
                returnObj.put("cpa_id", message.getCpaId());
                returnObj.put("service", message.getService());
                returnObj.put("action", message.getAction());
                returnObj.put("from_party_id", message.getFromPartyId());
                returnObj.put("to_party_id", message.getToPartyId());
                returnObj.put("conversation_id", message.getConvId());
                returnObj.put("timestamp", message.getTimeStamp().getTime() / 1000);
                returnObj.put("status", message.getStatus());

                try {
                    int numPayload = 0;
                    ArrayList<Object> payloads = new ArrayList<Object>();
                    for (Iterator i=ebxmlMessage.getSOAPMessage().getAttachments(); i.hasNext(); ) {
                        numPayload++;
                        Map<String, Object> payloadDict = new HashMap<String, Object>();
                        AttachmentPart attachmentPart = (AttachmentPart) i.next();
                        payloadDict.put("payload-" + numPayload, getPayload(attachmentPart));
                        payloads.add(payloadDict);
                    }

                    if (numPayload > 0) {
                        returnObj.put("payloads", payloads);
                    }
                }
                catch (Exception e) {
                    return createError(ErrorCode.ERROR_EXTRACTING_PAYLOAD_FROM_MESSAGE, "Error extracting message payload");
                }
                return returnObj;
            }
            else {
                return createError(ErrorCode.ERROR_DATA_NOT_FOUND, "Message with such id not found");
            }
        }
        catch (DAOException e) {
            return createError(ErrorCode.ERROR_READING_DATABASE, "Error loading message status");
        }
    }

    private String getPayload(AttachmentPart attachmentPart) throws SOAPException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream input = attachmentPart.getDataHandler().getInputStream();
        byte [] buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) > 0) {
            baos.write(buffer, 0, len);
        }
        byte[] payload = baos.toByteArray();
        input.close();
        baos.close();

        Base64.Encoder encoder = Base64.getEncoder();
        return new String(encoder.encode(payload));
    }
}
