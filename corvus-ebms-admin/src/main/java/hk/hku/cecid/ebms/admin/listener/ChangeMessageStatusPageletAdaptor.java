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
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.util.EbmsMessageStatusReverser;
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
            
            EbmsMessageStatusReverser reverser = new EbmsMessageStatusReverser();
            MessageDVO messageDVO = reverser.updateToSend(messageId);

            if (messageDVO != null) {
                dom.setProperty("message[0]/message_id",
                        checkNullAndReturnEmpty(messageDVO.getMessageId()));
                dom.setProperty("message[0]/message_box",
                        checkNullAndReturnEmpty(messageDVO.getMessageBox()));
                dom.setProperty("message[0]/ref_to_message_id",
                                checkNullAndReturnEmpty(messageDVO
                                        .getRefToMessageId()));
                dom.setProperty("message[0]/message_type",
                        checkNullAndReturnEmpty(messageDVO.getMessageType()));
                dom.setProperty("message[0]/cpa_id",
                        checkNullAndReturnEmpty(messageDVO.getCpaId()));
                dom.setProperty("message[0]/service",
                        checkNullAndReturnEmpty(messageDVO.getService()));
                dom.setProperty("message[0]/action",
                        checkNullAndReturnEmpty(messageDVO.getAction()));
                dom.setProperty("message[0]/conv_id",
                        checkNullAndReturnEmpty(messageDVO.getConvId()));
                dom.setProperty("message[0]/time_stamp", messageDVO
                        .getTimeStamp().toString());
                dom.setProperty("message[0]/status",
                        checkNullAndReturnEmpty(messageDVO.getStatus()));
                dom.setProperty("message[0]/status_description", String
                        .valueOf(checkNullAndReturnEmpty(messageDVO
                                .getStatusDescription())));
                dom.setProperty("message[0]/from_party_id",
                        checkNullAndReturnEmpty(messageDVO.getFromPartyId()));
                dom.setProperty("message[0]/to_party_id",
                        checkNullAndReturnEmpty(messageDVO.getToPartyId()));

            }
            
            // set the search criteria
            dom.setProperty("search_criteria/message_id", "");
            dom.setProperty("search_criteria/message_box", "");
            dom.setProperty("search_criteria/cpa_id", "");
            dom.setProperty("search_criteria/service", "");
            dom.setProperty("search_criteria/action", "");
            dom.setProperty("search_criteria/conv_id", "");
            dom.setProperty("search_criteria/principal_id", "");
            dom.setProperty("search_criteria/status", "");
            dom.setProperty("search_criteria/num_of_messages", "");
            dom.setProperty("search_criteria/offset", "0");
            dom.setProperty("search_criteria/is_detail", "");
            dom.setProperty("search_criteria/message_time","");

        } catch (Exception e) {
            EbmsProcessor.core.log.debug(
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

}