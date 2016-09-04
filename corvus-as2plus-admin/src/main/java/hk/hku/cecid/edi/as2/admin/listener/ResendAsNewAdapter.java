package hk.hku.cecid.edi.as2.admin.listener;

import hk.hku.cecid.edi.as2.AS2PlusProcessor;
import hk.hku.cecid.edi.as2.dao.AS2DAOHandler;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.edi.as2.module.OutgoingMessageProcessor;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
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
            
            OutgoingMessageProcessor outProcessor = AS2PlusProcessor.getInstance().getOutgoingMessageProcessor();
            AS2Message as2Message = outProcessor.resendAsNew(primalMessageId);
            AS2DAOHandler daoHandler = new AS2DAOHandler(outProcessor.getDAOFactory());
            MessageDVO msgDVO = daoHandler.findMessageDVO(as2Message.getMessageID(), MessageDVO.MSGBOX_OUT);
            
            dom = new PropertyTree();
            dom.setProperty("/message_history", "");
            
            if (null != msgDVO) {
            	setDisplayMessage(dom, msgDVO);
            }
            setSearchCriteria(dom);
        } catch (Exception e) {
        	dom = new PropertyTree();
            dom.setProperty("/error", "");            
            dom.setProperty("operation", "Resend as New");
            //dom.setProperty("home_directory", Sys.main.properties.getProperty("/corvus/home"));
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            dom.setProperty("exception_message", stringWriter.toString());
        	
        	AS2PlusProcessor.getInstance().getLogger().debug(
                    "Unable to process the \"Resend as New\" request", e);
        }
        return dom.getSource();
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
    
    private String checkNullAndReturnEmpty(String value) {
        if (value == null) {
            return new String("");
        }
        return value;
    }
}
