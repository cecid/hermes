package hk.hku.cecid.edi.as2.service;

import java.util.Iterator;
import java.util.List;

import javax.xml.soap.SOAPElement;

import org.w3c.dom.Element;

import hk.hku.cecid.edi.as2.dao.MessageDAO;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.edi.as2.AS2Processor;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.soap.SOAPRequestException;
import hk.hku.cecid.piazza.commons.soap.WebServicesAdaptor;
import hk.hku.cecid.piazza.commons.soap.WebServicesRequest;
import hk.hku.cecid.piazza.commons.soap.WebServicesResponse;

public class AS2MessageHistoryService extends WebServicesAdaptor{
	
	public static int MAX_NUMBER = 2147483647;	
	public static String NAMESPACE = "http://service.as2.edi.cecid.hku.hk/";
	
	 public void serviceRequested(WebServicesRequest request, WebServicesResponse response) 
		throws SOAPRequestException, DAOException{
	  
		  Element[] bodies = request.getBodies();
		  String msgId = getText(bodies, "messageId");
		  String msgBox = getText(bodies, "messageBox");
		  String as2From = getText(bodies, "as2From");
		  String as2To = getText(bodies, "as2To");
		  String status = getText(bodies, "status");
		  String limit = getText(bodies, "limit");
		  
		  AS2Processor.core.log.debug("Message History Query received request - "+
        		  "MessageID : " + (msgId ==null?"NULL":msgId)
                  + ", MessageBox: " + (msgBox ==null?"NULL":msgBox)
                  + ", AS2 From: " + (as2From ==null?"NULL":as2From)
                  + ", AS2 To: " + (as2To ==null?"NULL":as2To)
                  + ", Status: " +  (status ==null?"NULL":status)
                  +", Number of Messages: " + (limit ==null?"NULL":limit));
		  
		  int resultLimit = -1;
		  try{
			  resultLimit = Integer.parseInt(limit);
			  if(resultLimit < 1 ){
				  resultLimit = MAX_NUMBER;
			  }
		  }catch (NumberFormatException e) {
			  resultLimit = MAX_NUMBER;
		  }
		  
		  try{
			  MessageDAO msgDAO =  
				  		(MessageDAO)AS2Processor.core.dao.createDAO(MessageDAO.class);
			  
			  /**
			   * As Dynamic SQL has not been implemented in AS2 yet, 
			   * Hence, the program need to cast those null value to wild cart character
			   */ 
			  MessageDVO criteriaDVO = (MessageDVO)msgDAO.createDVO();
			  criteriaDVO.setMessageId(checkString(msgId));
			  criteriaDVO.setMessageBox(checkMessageBox(msgBox));			  			  
			  criteriaDVO.setAs2From(checkString(as2From));
			  criteriaDVO.setAs2To(checkString(as2To));
			  criteriaDVO.setPrincipalId("%");
			  criteriaDVO.setStatus(checkMessageStatus(status));
			  List results = msgDAO.findMessagesByHistory(criteriaDVO, resultLimit, 0);
			  generateReply(response, results);
		  }catch(DAOException daoExp){
			  throw new DAOException("Unable to query the repository", daoExp);
		  } catch (SOAPRequestException e) {
			  throw e;
		  }
	 }
	 
	 private String checkString(String input)throws SOAPRequestException{
		 if(input == null || input.equalsIgnoreCase("")){
			 return "%";
		 }else{
			 return input;
		 }
	 }
	 
	 private String checkMessageStatus(String input)throws SOAPRequestException{
		  if(input !=null &&
				  !(input.equalsIgnoreCase(MessageDVO.STATUS_PROCESSING) ||
				  input.equalsIgnoreCase(MessageDVO.STATUS_PENDING) ||
				  input.equalsIgnoreCase(MessageDVO.STATUS_PROCESSING) ||
				  input.equalsIgnoreCase(MessageDVO.STATUS_PROCESSED) ||
				  input.equalsIgnoreCase(MessageDVO.STATUS_PROCESSED_ERROR) ||
				  input.equalsIgnoreCase(MessageDVO.STATUS_DELIVERED) ||
				  input.equalsIgnoreCase(MessageDVO.STATUS_DELIVERY_FAILURE))){
			  String errMsg = "No such message status, you have entered ["+input+"]";
			  throw new SOAPRequestException(errMsg);
		  }else{
			  return (input==null?"%":input.toUpperCase());
		  }
	  }
	 
	  private String checkMessageBox(String input)throws SOAPRequestException{
		  if(input !=null &&
				  !(input.equalsIgnoreCase("INBOX") ||
				  !input.equalsIgnoreCase("OUTBOX"))){
			  String errMsg = "Wrong Message Box entered, you have entered: ["+ input+"]";
			  throw new SOAPRequestException(errMsg);
		  }else{
			  if(input == null || input.trim().equals("")){
				  return "%";
			  }else if(input.equalsIgnoreCase("INBOX")){
				  return MessageDVO.MSGBOX_IN;
			  }else if(input.equalsIgnoreCase("OUTBOX")){
				  return input = MessageDVO.MSGBOX_OUT;
			  }else {
				  String errMsg = "Wrong Message Box entered, you have entered: ["+ input+"]";
				  throw new SOAPRequestException(errMsg);
			  }
		  }
	  }
	  
	   /**
	     * 
	     * @param response
	     * @param messageList
	     * @throws SOAPRequestException
	     */
	    private void generateReply(WebServicesResponse response,
	    	List  messageList) throws SOAPRequestException {
	        try {
	            SOAPElement rootElement = createElement("messageList", "",
	            		NAMESPACE, "MessageList");

	            Iterator messagesIterator = messageList.iterator();

	            for (int i = 0; messagesIterator.hasNext(); i++) {
	                MessageDVO currentMessage = (MessageDVO) messagesIterator.next();

	                // Create Message Element and append Value of MessageID and MessageBox 
	                SOAPElement msgElement = createElement("messageElement", "", NAMESPACE, "MessageElement")	;                
	                SOAPElement childElement_MsgId = createText("messageId", currentMessage.getMessageId(), NAMESPACE);
	                
	                String msgBox = currentMessage.getMessageBox();
	                if(msgBox.equalsIgnoreCase(MessageDVO.MSGBOX_IN))
	                	msgBox = "inbox";
	                if(msgBox.equalsIgnoreCase(MessageDVO.MSGBOX_OUT))
	                	msgBox = "outbox";
	                SOAPElement childElement_MsgBox = createText("messageBox", msgBox, NAMESPACE);
	                msgElement.addChildElement(childElement_MsgId);
	                msgElement.addChildElement(childElement_MsgBox);
	                
	                rootElement.addChildElement(msgElement);
	            }

	            response.setBodies(new SOAPElement[] { rootElement });
	        } catch (Exception e) {
	            throw new SOAPRequestException("Unable to generate reply message", e);
	        }
	    }
}
