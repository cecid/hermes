package hk.hku.cecid.hermes.api.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import hk.hku.cecid.edi.as2.AS2Processor;
import hk.hku.cecid.edi.as2.dao.MessageDAO;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.hermes.api.ErrorCode;
import hk.hku.cecid.hermes.api.listener.HermesAbstractApiListener;
import hk.hku.cecid.hermes.api.spa.ApiPlugin;


/**
 * As2MessageHistoryHandler
 *
 * @author Patrick Yee
 *
 */
public class As2MessageHistoryHandler extends MessageHandler implements MessageHistoryHandler {

    public As2MessageHistoryHandler(HermesAbstractApiListener listener) {
        super(listener);
    }

    public Map<String, Object> getMessageHistory(HttpServletRequest httpRequest) {
        String messageId = httpRequest.getParameter("message_id");
        if (messageId == null) {
            messageId = "%";
        }
        String messageBox = httpRequest.getParameter("message_box");
        if (messageBox != null) {
            if (!messageBox.equalsIgnoreCase(MessageDVO.MSGBOX_IN) &&
                !messageBox.equalsIgnoreCase(MessageDVO.MSGBOX_OUT)) {
                String errorMessage = "Error parsing parameter: message_box";
                ApiPlugin.core.log.error(errorMessage);
                return listener.createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
            }
        }
        else {
            messageBox = "%";
        }
        String as2From = httpRequest.getParameter("as2_from");
        if (as2From == null) {
            as2From = "%";
        }
        String as2To = httpRequest.getParameter("as2_to");
        if (as2To == null) {
            as2To = "%";
        }
        String status = httpRequest.getParameter("status");
        if (status != null) {
            if (!status.equalsIgnoreCase(MessageDVO.STATUS_RECEIVED) &&
                !status.equalsIgnoreCase(MessageDVO.STATUS_PENDING) &&
                !status.equalsIgnoreCase(MessageDVO.STATUS_PROCESSING) &&
                !status.equalsIgnoreCase(MessageDVO.STATUS_PROCESSED) &&
                !status.equalsIgnoreCase(MessageDVO.STATUS_PROCESSED_ERROR) &&
                !status.equalsIgnoreCase(MessageDVO.STATUS_DELIVERED) &&
                !status.equalsIgnoreCase(MessageDVO.STATUS_DELIVERY_FAILURE)) {
                String errorMessage = "Error parsing parameter: status";
                ApiPlugin.core.log.error(errorMessage);
                return listener.createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
            }
        }
        else {
            status = "%";
        }
        String limitString = httpRequest.getParameter("limit");
        int limit = MAX_NUMBER;
        if (limitString != null) {
            try {
                limit = Integer.parseInt(limitString);
                if (limit < 0) {
                    limit = MAX_NUMBER;
                }
            } catch (NumberFormatException e) {}
        }

        ApiPlugin.core.log.debug("Parameters: message_id=" + messageId + ", message_box=" + messageBox +
                                 ", as2_from=" + as2From + ", as2_to=" + as2To +
                                 ", status=" + status + ", limit=" + limit);

        try {
            MessageDAO msgDAO = (MessageDAO) AS2Processor.core.dao.createDAO(MessageDAO.class);
            MessageDVO criteriaDVO = (MessageDVO)msgDAO.createDVO();
            criteriaDVO.setMessageId(messageId);
            criteriaDVO.setMessageBox(messageBox);
            criteriaDVO.setAs2From(as2From);
            criteriaDVO.setAs2To(as2To);
            criteriaDVO.setStatus(status);
            criteriaDVO.setPrincipalId("%");

            List results = msgDAO.findMessagesByHistory(criteriaDVO, limit, 0);

            if (results != null && results.size() > 0) {
                ArrayList<Object> messages = new ArrayList<Object>();
                for (Iterator i=results.iterator(); i.hasNext() ; ) {
                    MessageDVO message = (MessageDVO) i.next();
                    Map<String, Object> messageDict = new HashMap<String, Object>();
                    messageDict.put("id", message.getMessageId());
                    messageDict.put("timestamp", message.getTimeStamp().getTime() / 1000);
                    messageDict.put("status", message.getStatus());
                    messageDict.put("as2_from", message.getAs2From());
                    messageDict.put("as2_to", message.getAs2To());
                    messageDict.put("message_box", message.getMessageBox());
                    messages.add(messageDict);
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

    public Map<String, Object> resetMessage(String messageId, String action) {
        ApiPlugin.core.log.debug("Parameters: message_id=" + messageId + ", action=" + action);

        List results;
        MessageDAO msgDAO;
        try {
            msgDAO = (MessageDAO) AS2Processor.core.dao.createDAO(MessageDAO.class);
            MessageDVO criteriaDVO = (MessageDVO)msgDAO.createDVO();
            criteriaDVO.setMessageId(messageId);
            criteriaDVO.setMessageBox(MessageDVO.MSGBOX_IN);
            criteriaDVO.setAs2From("%");
            criteriaDVO.setAs2To("%");
            criteriaDVO.setStatus(MessageDVO.STATUS_DELIVERED);
            criteriaDVO.setPrincipalId("%");

            results = msgDAO.findMessagesByHistory(criteriaDVO, MAX_NUMBER, 0);
        } catch (DAOException e) {
            String errorMessage = "Error loading messages";
            ApiPlugin.core.log.error(errorMessage);
            return listener.createError(ErrorCode.ERROR_READING_DATABASE, errorMessage);
        }

        try {
            if (results.size() > 0) {
                MessageDVO message = (MessageDVO) results.get(0);
                message.setStatus(MessageDVO.STATUS_PROCESSED);
                msgDAO.persist(message);
            }
        } catch (DAOException e) {
            String errorMessage = "Error writing message status";
            ApiPlugin.core.log.error(errorMessage);
            return listener.createError(ErrorCode.ERROR_WRITING_DATABASE, errorMessage);
        }
        return listener.createActionResult(messageId, true);
    }
}
