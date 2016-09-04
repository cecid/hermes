/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Apache License Version 2.0 [1]
 * 
 * [1] http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package hk.hku.cecid.edi.as2.admin.listener;

import hk.hku.cecid.edi.as2.AS2Processor;
import hk.hku.cecid.edi.as2.dao.MessageDAO;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;

/**
 * @author Donahue Sze
 *  
 */
public class MessageHistoryPageletAdaptor extends AdminPageletAdaptor {

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.pagelet.xslt.BorderLayoutPageletAdaptor#getCenterSource(javax.servlet.http.HttpServletRequest)
     */
    protected Source getCenterSource(HttpServletRequest request) {

        PropertyTree dom = null;

        try {
            // construct updated delivery channels property tree
            dom = getMessageHistory(request);
        } catch (DAOException e) {
            AS2Processor.core.log.debug(
                    "Unable to process the message search page request", e);
            throw new RuntimeException(
                    "Unable to process the message search page request", e);
        }

        return dom.getSource();
    }

    /**
     * @param request
     * @return
     * @throws DAOException
     */
    private PropertyTree getMessageHistory(HttpServletRequest request)
            throws DAOException {

        // construct the dom tree
        PropertyTree dom = new PropertyTree();
        dom.setProperty("/message_history", "");
        
        int numberOfMessagesInt = 20; // default value
        int offsetInt = 0;
        boolean isDetail = false;
        boolean isTime = false;
        int displayLastInt = 0;

        // get the input parameters
        Iterator messageIterator = null;

        if (request.getParameter("original_message_id") != null) {
            String originalMessageId = request
                    .getParameter("original_message_id");
            String originalMessageBox = request
                    .getParameter("original_message_box");

            isDetail = true;

            // search the corresponding messages
            MessageDAO messageDAO = (MessageDAO) AS2Processor.core.dao
                    .createDAO(MessageDAO.class);

            List messageList = messageDAO.findMessageByOriginalMessageID(
                    originalMessageId, originalMessageBox);

            messageIterator = messageList.iterator();

            dom.setProperty("total_no_of_messages", String.valueOf(messageList
                    .size()));
        } else {
            // text field
            String messageId = checkStarAndConvertToPercent(request
                    .getParameter("message_id"));
            String as2From = checkStarAndConvertToPercent(request
                    .getParameter("as2_from"));
            String as2To = checkStarAndConvertToPercent(request
                    .getParameter("as2_to"));

            // radio button and menu
            String messageBox = checkEmptyAndConvertToPercent(request
                    .getParameter("message_box"));
            String status = checkEmptyAndConvertToPercent(request
                    .getParameter("status"));

            String numOfMessages = request.getParameter("num_of_messages");
            if (numOfMessages != null) {
                numberOfMessagesInt = Integer.valueOf(numOfMessages).intValue();
            }
            
            //get the message_time value
            String displayLast = request.getParameter("message_time");
            if(displayLast != null){
            	if(!(displayLast.equals(""))){
            		displayLastInt = Integer.valueOf(displayLast).intValue();
            		isTime = true;
            	}
            }

            String offset = request.getParameter("offset");
            if (offset != null) {
                offsetInt = Integer.valueOf(offset).intValue();
            }

            String isDetailStr = request.getParameter("is_detail");
            if (isDetailStr != null) {
                if (isDetailStr.equalsIgnoreCase("true")) {
                    isDetail = true;
                }
            }

            // search the corresponding messages
            MessageDAO messageDAO = (MessageDAO) AS2Processor.core.dao
                    .createDAO(MessageDAO.class);
            MessageDVO messageDAOData = (MessageDVO) messageDAO.createDVO();

            messageDAOData.setMessageId(messageId);
            messageDAOData.setAs2From(as2From);
            messageDAOData.setAs2To(as2To);
            messageDAOData.setMessageBox(messageBox);
            messageDAOData.setStatus(status);
            messageDAOData.setPrincipalId("%");
			
			
			messageIterator = findMessageWithPagination(messageDAOData, messageDAO, numberOfMessagesInt, offsetInt, displayLastInt, isTime);
			/*
            if(!isTime){
		        messageIterator = messageDAO.findMessagesByHistory(messageDAOData,
		                numberOfMessagesInt, offsetInt).iterator();
            }else{
                messageIterator = messageDAO.findMessagesByTime(displayLastInt,messageDAOData,
                        numberOfMessagesInt, offsetInt).iterator();
            }
			*/
            dom.setProperty("total_no_of_messages", String.valueOf(messageDAO
                    .findNumberOfMessagesByHistory(messageDAOData)));
        }

        // pass the search criteria
        dom.setProperty("search_criteria/message_id", request
                .getParameter("message_id"));
        dom.setProperty("search_criteria/message_box", request
                .getParameter("message_box"));
        dom.setProperty("search_criteria/as2_from", request
                .getParameter("as2_from"));
        dom.setProperty("search_criteria/as2_to", request
                .getParameter("as2_to"));
        dom.setProperty("search_criteria/status", request
                .getParameter("status"));
        dom.setProperty("search_criteria/num_of_messages", String
                .valueOf(numberOfMessagesInt));
        //set the message criteria for message_time
        dom.setProperty("search_criteria/message_time",String.valueOf(displayLastInt));
        dom.setProperty("search_criteria/offset", String.valueOf(offsetInt));
        dom.setProperty("search_criteria/is_detail", String.valueOf(isDetail));

        for (int pi = 1; messageIterator.hasNext(); pi++) {
            MessageDVO returnData = (MessageDVO) messageIterator.next();

            dom.setProperty("message[" + pi + "]/message_id",
                    checkNullAndReturnEmpty(returnData.getMessageId()));
            dom.setProperty("message[" + pi + "]/message_box",
                    checkNullAndReturnEmpty(returnData.getMessageBox()));
            dom.setProperty("message[" + pi + "]/as2_from",
                    checkNullAndReturnEmpty(returnData.getAs2From()));
            dom.setProperty("message[" + pi + "]/as2_to",
                    checkNullAndReturnEmpty(returnData.getAs2To()));
            dom.setProperty("message[" + pi + "]/time_stamp", returnData
                    .getTimeStamp().toString());
            dom.setProperty("message[" + pi + "]/status",
                    checkNullAndReturnEmpty(returnData.getStatus()));
            dom.setProperty("message[" + pi + "]/is_acknowledged", String
                    .valueOf(returnData.isAcknowledged()));
            dom.setProperty("message[" + pi + "]/is_receipt", String
                    .valueOf(returnData.isReceipt()));
            dom.setProperty("message[" + pi + "]/is_receipt_requested", String
                    .valueOf(returnData.isReceiptRequested()));

            if (isDetail) {
                dom.setProperty("message[" + pi + "]/receipt_url",
                        checkNullAndReturnEmpty(returnData.getReceiptUrl()));
                dom.setProperty("message[" + pi + "]/mic_value",
                        checkNullAndReturnEmpty(returnData.getMicValue()));
                dom.setProperty("message[" + pi + "]/original_message_id",
                        checkNullAndReturnEmpty(returnData
                                .getOriginalMessageId()));
                dom.setProperty("message[" + pi + "]/status_description",
                        checkNullAndReturnEmpty(returnData
                                .getStatusDescription()));
            }
        }

        return dom;
    }
		
    /**
     * @param parameter
     * @return
     */
    private String checkEmptyAndConvertToPercent(String parameter) {
        if (parameter == null || parameter.equals("")) {
            return "%";
        }
        return parameter;
    }

    /**
     * @param messageId
     * @return
     */
    private String checkNullAndReturnEmpty(String value) {
        if (value == null) {
            return new String("");
        }
        return value;
    }

    /**
     * @param parameter
     * @return
     */
    private String checkStarAndConvertToPercent(String parameter) {
        if (parameter == null || parameter.equals("")) {
            return "%";
        }
        return parameter.replace("_", "\\_").replace("%", "\\%").replace('*', '%');
    }

	private Iterator findMessageWithPagination(MessageDVO data, MessageDAO messageDAO, int numberOfMessage, int offset, int displayLastInt, boolean isTime) throws DAOException{
		if(!isTime){
			return  messageDAO.findMessagesByHistory(data,
					numberOfMessage, offset).iterator();
		}else{
			return messageDAO.findMessagesByTime(displayLastInt,data,
					numberOfMessage, offset).iterator();
		}
	}
}