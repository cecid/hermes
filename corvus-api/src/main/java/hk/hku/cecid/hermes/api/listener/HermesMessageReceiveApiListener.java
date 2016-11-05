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
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
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
import hk.hku.cecid.piazza.commons.rest.RestRequest;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;

/**
 * HermesMessageReceiveApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesMessageReceiveApiListener extends HermesProtocolApiListener {

    public static int MAX_NUMBER = 2147483647;

    protected void processApi(RestRequest request, JsonObjectBuilder jsonBuilder) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = this.getProtocolFromPathInfo(httpRequest.getPathInfo());

        if (protocol.equalsIgnoreCase("ebms")) {
            if (httpRequest.getMethod().equalsIgnoreCase("GET")) {
                String partnershipId = httpRequest.getParameter("partnership_id");
                if (partnershipId == null) {
                    this.fillError(jsonBuilder, -1, "Missing required field: partnership_id");
                    return;
                }

                List results = null;
                try {
                    PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao.createDAO(PartnershipDAO.class);
                    PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();

                    partnershipDVO.setPartnershipId(partnershipId);
                    if (!partnershipDAO.retrieve(partnershipDVO)) {
                        this.fillError(jsonBuilder, -1, "Cannot load partnership: " + partnershipId);
                        return;
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
                    this.fillError(jsonBuilder, -1, "Error loading messages");
                    return;
                }

                if (results != null) {
                    JsonArrayBuilder arrayBuilder = this.createJsonArray();
                    for (Iterator i=results.iterator(); i.hasNext() ; ) {
                        JsonObjectBuilder jsonItem = this.createJsonObject();
                        MessageDVO message = (MessageDVO) i.next();
                        jsonItem.add("id", message.getMessageId());
                        jsonItem.add("timestamp", message.getTimeStamp().getTime() / 1000);
                        arrayBuilder.add(jsonItem);
                    }
                    jsonBuilder.add("message_ids", arrayBuilder);
                }
                else {
                    this.fillError(jsonBuilder, -1, "No message can be loaded");
                    return;
                }
            }
            else if (httpRequest.getMethod().equalsIgnoreCase("POST")) {
                JsonObject jsonObject = this.getJsonObjectFromRequest(httpRequest);
                if (jsonObject == null) {
                    this.fillError(jsonBuilder, -1, "Error reading request data");
                    return;
                }

                String message_id = jsonObject.getString("message_id");
                if (message_id == null) {
                    this.fillError(jsonBuilder, -1, "Missing required field: message_id");
                    return;
                }

                try {
                    MessageDAO msgDAO = (MessageDAO) EbmsProcessor.core.dao.createDAO(MessageDAO.class);
                    MessageDVO message = (MessageDVO) msgDAO.createDVO();
                    message.setMessageId(message_id);
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
                            this.fillError(jsonBuilder, -1, "Unable to read message from repository");
                            return;
                        }

                        jsonBuilder.add("id", message.getMessageId());
                        jsonBuilder.add("cpa_id", message.getCpaId());
                        jsonBuilder.add("service", message.getService());
                        jsonBuilder.add("action", message.getAction());
                        jsonBuilder.add("from_party_id", message.getFromPartyId());
                        jsonBuilder.add("to_party_id", message.getToPartyId());
                        jsonBuilder.add("conversation_id", message.getConvId());
                        jsonBuilder.add("timestamp", message.getTimeStamp().getTime() / 1000);
                        jsonBuilder.add("status", message.getStatus());

                        try {
                            JsonArrayBuilder arrayBuilder = this.createJsonArray();
                            int numPayload = 0;
                            for (Iterator i=ebxmlMessage.getSOAPMessage().getAttachments(); i.hasNext(); ) {
                                numPayload++;
                                AttachmentPart attachmentPart = (AttachmentPart) i.next();
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
                                String payload_string = new String(encoder.encode(payload));

                                JsonObjectBuilder payloadObj = this.createJsonObject();
                                payloadObj.add("payload", payload_string);
                                arrayBuilder.add(payloadObj);
                            }

                            if (numPayload > 0) {
                                jsonBuilder.add("payloads", arrayBuilder);
                            }
                        }
                        catch (SOAPException e) {
                            this.fillError(jsonBuilder, -1, "Error extracting message payload");
                            return;
                        }
                        catch (IOException e) {
                            this.fillError(jsonBuilder, -1, "Error reading/encoding message payload");
                            return;
                        }
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
            else {
                throw new RequestListenerException("Request method not supported");
            }
        }
        else {
            this.fillError(jsonBuilder, -1, "Protocol unknown");
        }
    }
}
