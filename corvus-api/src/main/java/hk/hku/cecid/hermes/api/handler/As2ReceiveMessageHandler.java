package hk.hku.cecid.hermes.api.handler;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

import hk.hku.cecid.edi.as2.AS2Processor;
import hk.hku.cecid.edi.as2.dao.MessageDAO;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.edi.as2.dao.PartnershipDAO;
import hk.hku.cecid.edi.as2.dao.PartnershipDVO;
import hk.hku.cecid.edi.as2.module.PayloadCache;
import hk.hku.cecid.edi.as2.module.PayloadRepository;
import hk.hku.cecid.hermes.api.ErrorCode;
import hk.hku.cecid.hermes.api.listener.HermesAbstractApiListener;
import hk.hku.cecid.hermes.api.spa.ApiPlugin;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.io.IOHandler;


public class As2ReceiveMessageHandler extends MessageHandler implements ReceiveMessageHandler {

    public As2ReceiveMessageHandler(HermesAbstractApiListener listener) {
        super(listener);
    }

    public Map<String, Object> getReceivedMessageList(String partnershipId, boolean includeRead) {
        ApiPlugin.core.log.debug("Parameters: partnership_id=" + partnershipId + ", include_read=" + includeRead);

        try {
            PartnershipDAO partnershipDAO = (PartnershipDAO) AS2Processor.core.dao.createDAO(PartnershipDAO.class);
            List<PartnershipDVO> partnerships = partnershipDAO.findAllPartnerships();
            boolean found = false;
            PartnershipDVO partnership = null;
            for (int i=0 ; i<partnerships.size() ; i++) {
                partnership = partnerships.get(i);
                if (partnership.getPartnershipId().equals(partnershipId)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                String errorMessage = "Cannot load partnership: " + partnershipId;
                ApiPlugin.core.log.error(errorMessage);
                return listener.createError(ErrorCode.ERROR_DATA_NOT_FOUND, errorMessage);
            }

            MessageDAO messageDAO = (MessageDAO) AS2Processor.core.dao.createDAO(MessageDAO.class);
            MessageDVO messageDVO = (MessageDVO) messageDAO.createDVO();
            messageDVO.setMessageId("%");
            messageDVO.setMessageBox(MessageDVO.MSGBOX_IN);
            // has to flip the as2 from / to here to correctly receive messages
            messageDVO.setAs2From(partnership.getAs2To());
            messageDVO.setAs2To(partnership.getAS2From());
            messageDVO.setPrincipalId("%");
            if (!includeRead) {
                messageDVO.setStatus(MessageDVO.STATUS_PROCESSED);
            }
            else {
                messageDVO.setStatus("%");
            }

            List results = messageDAO.findMessagesByHistory(messageDVO, MAX_NUMBER, 0);

            if (results != null) {
                ArrayList<Object> messages = new ArrayList<Object>();
                for (Iterator i=results.iterator(); i.hasNext() ; ) {
                    MessageDVO message = (MessageDVO) i.next();
                    Map<String, Object> messageDict = new HashMap<String, Object>();
                    messageDict.put("id", message.getMessageId());
                    messageDict.put("timestamp", message.getTimeStamp().getTime() / 1000);
                    messageDict.put("status", message.getStatus());
                    messages.add(messageDict);

                    // save delivered status and clear message from inbox
                    message.setStatus(MessageDVO.STATUS_DELIVERED);
                    messageDAO.persist(message);
                }
                Map<String, Object> returnObj = new HashMap<String, Object>();
                returnObj.put("message_ids", messages);
                ApiPlugin.core.log.info("" + messages.size() + " messages returned");
                return returnObj;
            }
            else {
                String errorMessage = "No message can be loaded";
                ApiPlugin.core.log.error(errorMessage);
                return listener.createError(ErrorCode.ERROR_DATA_NOT_FOUND, errorMessage);
            }
        }
        catch (DAOException e) {
            String errorMessage = "Error loading messages";
            ApiPlugin.core.log.error(errorMessage);
            return listener.createError(ErrorCode.ERROR_DATA_NOT_FOUND, errorMessage);
        }
    }

    public Map<String, Object> getReceivedMessage(String messageId, HttpServletRequest request) {
        ApiPlugin.core.log.debug("Parameters: message_id=" + messageId);

        try {
            MessageDAO msgDAO = (MessageDAO) AS2Processor.core.dao.createDAO(MessageDAO.class);
            MessageDVO message = (MessageDVO) msgDAO.createDVO();
            message.setMessageId(messageId);
            message.setMessageBox(MessageDVO.MSGBOX_IN);
            message.setAs2From("%");
            message.setAs2To("%");
            message.setPrincipalId("%");
            message.setStatus("%");

            List messagesFound = msgDAO.findMessagesByHistory(message, 1, 0);
            if (messagesFound.size() > 0) {

                message = (MessageDVO) messagesFound.get(0);

                Map<String, Object> returnObj = new HashMap<String, Object>();
                returnObj.put("id", message.getMessageId());
                returnObj.put("as2_from", message.getAs2From());
                returnObj.put("as2_to", message.getAs2To());
                returnObj.put("timestamp", message.getTimeStamp().getTime() / 1000);
                returnObj.put("status", message.getStatus());

                PayloadRepository repository = AS2Processor.getIncomingPayloadRepository();
                Iterator payloadCachesIterator = repository.getPayloadCaches().iterator();

                int numPayload = 0;
                ArrayList<Object> payloads = new ArrayList<Object>();
                while (payloadCachesIterator.hasNext()) {
                    PayloadCache cache = (PayloadCache) payloadCachesIterator.next();
                    String cacheMessageID = cache.getMessageID();
                    if (cacheMessageID.equals(message.getMessageId())) {
                        try {
                            FileInputStream fis = new FileInputStream(cache.getCache());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();

                            if ("true".equals(getHeader(request, "is_compress"))) {
                                DeflaterOutputStream dos = new DeflaterOutputStream(baos);
                                IOHandler.pipe(fis, dos);
                                dos.finish();
                            }
                            else {
                                IOHandler.pipe(fis, baos);
                            }

                            numPayload++;
                            Map<String, Object> payloadDict = new HashMap<String, Object>();
                            payloadDict.put("payload-" + numPayload,
                                            new String(Base64.encodeBase64(baos.toByteArray())));
                            payloads.add(payloadDict);
                        } catch (Exception e) {
                            AS2Processor.core.log.error("Error in collecting message", e);
                        }
                    }

                    if (numPayload > 0) {
                        returnObj.put("payloads", payloads);
                    }
                }

                return returnObj;
            }
            else {
                String errorMessage = "Message with such id not found";
                ApiPlugin.core.log.error(errorMessage);
                return listener.createError(ErrorCode.ERROR_DATA_NOT_FOUND, errorMessage);
            }
        }
        catch (DAOException e) {
            String errorMessage = "Error loading message status";
            ApiPlugin.core.log.error(errorMessage, e);
            return listener.createError(ErrorCode.ERROR_READING_DATABASE, errorMessage);
        }
    }

    protected String getHeader(HttpServletRequest request, String headerName) {
        Enumeration<String> values = request.getHeaders(headerName);
        if (values.hasMoreElements()) {
            return values.nextElement();
        }
        else {
            return null;
        }
    }
}
