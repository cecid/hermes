package hk.hku.cecid.ebms.admin.listener;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.handler.MessageClassifier;
import hk.hku.cecid.ebms.spa.handler.OutboundMessageProcessor;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;

public class ResendAsNewAdapter extends AdminPageletAdaptor {
	protected Source getCenterSource(HttpServletRequest request) {
		PropertyTree dom;

        try {
            String primalMessageId = request.getParameter("primal_message_id");
            
            if (null == primalMessageId) {
            	throw new Exception("Primal Message ID is null");
            }
            
            OutboundMessageProcessor outProcessor = OutboundMessageProcessor.getInstance();
            EbxmlMessage ebxmlMessage = outProcessor.resendAsNew(primalMessageId);
            
            MessageDAO msgDAO = (MessageDAO) EbmsProcessor.core.dao.createDAO(MessageDAO.class);
            hk.hku.cecid.ebms.spa.dao.MessageDVO msgDVO = (MessageDVO) msgDAO.createDVO();
            msgDVO.setMessageId(ebxmlMessage.getMessageId());
            msgDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_OUTBOX);            
            
            dom = new PropertyTree();
            dom.setProperty("/message_history", "");
            
            if (msgDAO.findMessage(msgDVO)) {
            	setDisplayMessage(dom, msgDVO);
            }
            
            setSearchCriteria(dom);            
        } catch (Exception e) {
        	dom = new PropertyTree();
            dom.setProperty("/error", "");            
            dom.setProperty("operation", "Resend as New");
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            dom.setProperty("exception_message", stringWriter.toString());
        	
            EbmsProcessor.core.log.debug(
                    "Unable to process the \"Resend as New\" request", e);
        }
        return dom.getSource();
	}
	
    private void setDisplayMessage(PropertyTree dom, MessageDVO messageDVO) {
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
	
	private void setSearchCriteria(PropertyTree dom) {
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
    }
    
    private String checkNullAndReturnEmpty(String value) {
        if (value == null) {
            return new String("");
        }
        return value;
    }
}
