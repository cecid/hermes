/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Apache License Version 2.0 [1]
 * 
 * [1] http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package hk.hku.cecid.ebms.admin.listener;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.handler.MessageClassifier;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor;

import java.util.ArrayList;
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
            EbmsProcessor.core.log.debug(
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
            String refToMessageType = request
                    .getParameter("ref_to_message_type");
            String refToMessageBox = originalMessageBox
                    .equals(MessageClassifier.MESSAGE_BOX_INBOX) ? MessageClassifier.MESSAGE_BOX_OUTBOX
                    : MessageClassifier.MESSAGE_BOX_INBOX;

            isDetail = true;

            // search the corresponding messages
            MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                    .createDAO(MessageDAO.class);
            MessageDVO messageDVO = (MessageDVO) messageDAO.createDVO();
            messageDVO.setRefToMessageId(originalMessageId);
            messageDVO.setMessageBox(refToMessageBox);
            messageDVO.setMessageType(refToMessageType);

            List messageList = new ArrayList();
            if (messageDAO.findRefToMessage(messageDVO)) {
                messageList.add(messageDVO);
            }

            messageIterator = messageList.iterator();

            dom.setProperty("total_no_of_messages", String.valueOf(messageList
                    .size()));
        } else {
            // text field
            String messageId = checkStarAndConvertToPercent(request
                    .getParameter("message_id"));
            String cpaId = checkStarAndConvertToPercent(request
                    .getParameter("cpa_id"));
            String service = checkStarAndConvertToPercent(request
                    .getParameter("service"));
            String action = checkStarAndConvertToPercent(request
                    .getParameter("action"));
            String convId = checkStarAndConvertToPercent(request
                    .getParameter("conv_id"));     
            
            String primalMessageId = checkEmptyAndReturnNull(request
            		.getParameter("primal_message_id"));
            // radio button and menu
            String messageBox = checkEmptyAndReturnNull(request
                    .getParameter("message_box"));
            String status = checkEmptyAndReturnNull(request
                    .getParameter("status"));
            
            //get the message_time value
            String displayLast = request.getParameter("message_time");
            if(displayLast != null){
            	if(!(displayLast.equals(""))){
            		displayLastInt = Integer.valueOf(displayLast).intValue();
            		isTime = true;
            	}
            }

            String numOfMessages = request.getParameter("num_of_messages");
            if (numOfMessages != null) {
                numberOfMessagesInt = Integer.valueOf(numOfMessages).intValue();
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
            MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                    .createDAO(MessageDAO.class);
            MessageDVO messageDVO = (MessageDVO) messageDAO.createDVO();

            messageDVO.setMessageId(messageId);
            messageDVO.setCpaId(cpaId);
            messageDVO.setService(service);
            messageDVO.setAction(action);
            messageDVO.setConvId(convId);
            messageDVO.setMessageBox(messageBox);
            messageDVO.setStatus(status);            
            messageDVO.setPrimalMessageId(primalMessageId);

			messageIterator = findMessageWithPagination(messageDVO,messageDAO, numberOfMessagesInt, offsetInt,displayLastInt, isTime);
            dom.setProperty("total_no_of_messages", String.valueOf(messageDAO
                    .findNumberOfMessagesByHistory(messageDVO)));
        }

        // pass the search criteria
        dom.setProperty("search_criteria/message_id", request
                .getParameter("message_id"));
        dom.setProperty("search_criteria/message_box", request
                .getParameter("message_box"));
        dom.setProperty("search_criteria/cpa_id", request
                .getParameter("cpa_id"));
        dom.setProperty("search_criteria/service", request
                .getParameter("service"));
        dom.setProperty("search_criteria/action", request
                .getParameter("action"));
        dom.setProperty("search_criteria/conv_id", request
                .getParameter("conv_id"));
        dom.setProperty("search_criteria/status", request
                .getParameter("status"));
        dom.setProperty("search_criteria/num_of_messages", String
                .valueOf(numberOfMessagesInt));
        dom.setProperty("search_criteria/message_time",String.valueOf(displayLastInt));
        dom.setProperty("search_criteria/offset", String.valueOf(offsetInt));
        dom.setProperty("search_criteria/is_detail", String.valueOf(isDetail));
        dom.setProperty("search_criteria/primal_message_id", request
                .getParameter("primal_message_id"));

        for (int pi = 1; messageIterator.hasNext(); pi++) {
            MessageDVO returnData = (MessageDVO) messageIterator.next();

            dom.setProperty("message[" + pi + "]/message_id",
                    checkNullAndReturnEmpty(returnData.getMessageId()));
            dom.setProperty("message[" + pi + "]/message_box",
                    checkNullAndReturnEmpty(returnData.getMessageBox()));
            dom.setProperty("message[" + pi + "]/ack_requested",
                    checkNullAndReturnEmpty(returnData.getAckRequested()));
            dom.setProperty("message[" + pi + "]/ref_to_message_id",
                    checkNullAndReturnEmpty(returnData.getRefToMessageId()));
            dom.setProperty("message[" + pi + "]/message_type",
                    checkNullAndReturnEmpty(returnData.getMessageType()));
            dom.setProperty("message[" + pi + "]/cpa_id",
                    checkNullAndReturnEmpty(returnData.getCpaId()));
            dom.setProperty("message[" + pi + "]/service",
                    checkNullAndReturnEmpty(returnData.getService()));
            dom.setProperty("message[" + pi + "]/action",
                    checkNullAndReturnEmpty(returnData.getAction()));
            dom.setProperty("message[" + pi + "]/conv_id",
                    checkNullAndReturnEmpty(returnData.getConvId()));
            dom.setProperty("message[" + pi + "]/time_stamp", returnData
                    .getTimeStamp().toString());
            dom.setProperty("message[" + pi + "]/primal_message_id", 
            		checkNullAndReturnEmpty(returnData.getPrimalMessageId()));
            dom.setProperty("message[" + pi + "]/status",
                    checkNullAndReturnEmpty(returnData.getStatus()));
            dom.setProperty("message[" + pi + "]/has_resend_as_new",
                    checkNullAndReturnEmpty(returnData.getHasResendAsNew()));

            if (isDetail) {
                dom.setProperty("message[" + pi + "]/from_party_id",
                        checkNullAndReturnEmpty(returnData.getFromPartyId()));
                dom.setProperty("message[" + pi + "]/from_party_type",
                        checkNullAndReturnEmpty(returnData.getFromPartyRole()));
                dom.setProperty("message[" + pi + "]/to_party_id",
                        checkNullAndReturnEmpty(returnData.getToPartyId()));
                dom.setProperty("message[" + pi + "]/to_party_type",
                        checkNullAndReturnEmpty(returnData.getToPartyRole()));
                dom.setProperty("message[" + pi + "]/ack_sign_requested",
                        checkNullAndReturnEmpty(returnData
                                .getAckSignRequested()));
                if (returnData.getSequenceStatus() != -1) {
                    dom.setProperty("message[" + pi + "]/sequence_group",
                            String.valueOf(returnData.getSequenceGroup()));
                    dom.setProperty("message[" + pi + "]/sequence_no", String
                            .valueOf(returnData.getSequenceNo()));
                    dom.setProperty("message[" + pi + "]/sequence_status",
                            String.valueOf(returnData.getSequenceStatus()));
                }
                dom.setProperty("message[" + pi + "]/status_description",
                        checkNullAndReturnEmpty(returnData
                                .getStatusDescription()));
            }
        }

        return dom;
    }

    private String checkEmptyAndReturnNull(String parameter) {
        if (parameter == null || parameter.equals("")) {
            return null;
        }
        return parameter;
    }

    private String checkNullAndReturnEmpty(String parameter) {
        if (parameter == null) {
            return new String("");
        }
        return parameter;
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

	private Iterator findMessageWithPagination(MessageDVO data, MessageDAO messageDAO, int numberOfMessage, int offset,int displayLastInt, boolean isTime) throws DAOException{
		if(!isTime){
			return messageDAO.findMessagesByHistory(data,
					numberOfMessage, offset).iterator();
		}else{
			return messageDAO.findMessagesByTime(displayLastInt,data,
					numberOfMessage, offset).iterator();
		}
	}
}