/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Apache License Version 2.0 [1]
 * 
 * [1] http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package hk.hku.cecid.edi.as2.admin.listener;

import hk.hku.cecid.edi.as2.AS2PlusProcessor;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.edi.as2.util.AS2MessageStatusReverser;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;

/**
 * @author Donahue Sze
 *  
 */
public class ChangeMessageStatusPageletAdaptor extends AdminPageletAdaptor {

    protected Source getCenterSource(HttpServletRequest request) {

        // construct the dom tree
        PropertyTree dom = new PropertyTree();
        dom.setProperty("/message_history", "");

        try {
            String messageId = request.getParameter("message_id");
            
            AS2MessageStatusReverser reverser = new AS2MessageStatusReverser();
            MessageDVO messageDVO = reverser.updateToSend(messageId);
            
            if (null != messageDVO) {
            	setDisplayMessage(dom, messageDVO);
            }

            setSearchCriteria(dom);

        } catch (Exception e) {
            AS2PlusProcessor.getInstance().getLogger().debug(
                    "Unable to process the pagelet request", e);
        }
        return dom.getSource();
    }

    private String checkNullAndReturnEmpty(String value) {
        if (value == null) {
            return new String("");
        }
        return value;
    }
    
    private void setDisplayMessage(PropertyTree dom, MessageDVO messageDVO) {
        dom.setProperty("message[0]/message_id",
                checkNullAndReturnEmpty(messageDVO.getMessageId()));
        dom.setProperty("message[0]/message_box",
                checkNullAndReturnEmpty(messageDVO.getMessageBox()));
        dom.setProperty("message[0]/as2_from",
                checkNullAndReturnEmpty(messageDVO.getAs2From()));
        dom.setProperty("message[0]/as2_to",
                checkNullAndReturnEmpty(messageDVO.getAs2To()));
        dom.setProperty("message[0]/time_stamp", messageDVO
                .getTimeStamp().toString());
        dom.setProperty("message[0]/status",
                checkNullAndReturnEmpty(messageDVO.getStatus()));
        dom.setProperty("message[0]/is_acknowledged", String
                .valueOf(messageDVO.isAcknowledged()));
        dom.setProperty("message[0]/is_receipt", String
                .valueOf(messageDVO.isReceipt()));
        dom.setProperty("message[0]/is_receipt_requested", String
                .valueOf(messageDVO.isReceiptRequested()));
    }
    
    private void setSearchCriteria(PropertyTree dom) {
        // set the search criteria
        dom.setProperty("search_criteria/message_id", "");
        dom.setProperty("search_criteria/message_box", "");
        dom.setProperty("search_criteria/as2_from", "");
        dom.setProperty("search_criteria/as2_to", "");
        dom.setProperty("search_criteria/status", "");
        dom.setProperty("search_criteria/num_of_messages", "");
        dom.setProperty("search_criteria/offset", "0");
        dom.setProperty("search_criteria/is_detail", "");
        //change the message status for message_time
        dom.setProperty("search_criteria/message_time","");
    }
}