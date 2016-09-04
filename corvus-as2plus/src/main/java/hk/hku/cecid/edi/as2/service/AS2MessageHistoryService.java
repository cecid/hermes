package hk.hku.cecid.edi.as2.service;

import java.util.Iterator;
import java.util.List;

import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;

import hk.hku.cecid.edi.as2.dao.MessageDAO;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.edi.as2.AS2PlusProcessor;
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
	
		  String msgId = null;
		  String msgBox = null;
		  String as2From = null;
		  String as2To = null;
		  String status = null;
		  String limit = null;
		 
	      boolean wsi = false;
  	      
	      SOAPBodyElement[] bodies = (SOAPBodyElement[]) request.getBodies();
	      // WS-I <RequestElement> 
	      if (bodies != null && bodies.length == 1 && 
	    		  isElement(bodies[0], "RequestElement", NAMESPACE)) {
			
			AS2PlusProcessor.getInstance().getLogger().debug("WS-I Request");
			
			wsi = true;
			
			SOAPElement[] childElement = getChildElementArray(bodies[0]);
			msgId = getText(childElement, "messageId");
			msgBox = getText(childElement, "messageBox");
			as2From = getText(childElement, "as2From");
			as2To = getText(childElement, "as2To");
			status = getText(childElement, "status");
			limit = getText(childElement, "limit");
			
	      } else {
	    	  AS2PlusProcessor.getInstance().getLogger().debug("Non WS-I Request"); 
	    	  
	    	  msgId = getText(bodies, "messageId");
	    	  msgBox = getText(bodies, "messageBox");
	    	  as2From = getText(bodies, "as2From");
	    	  as2To = getText(bodies, "as2To");
	    	  status = getText(bodies, "status");
	    	  limit = getText(bodies, "limit");			
	      }
		  
		  AS2PlusProcessor.getInstance().getLogger().debug("Message History Query received request - "+
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
				  		(MessageDAO)AS2PlusProcessor.getInstance().getDAOFactory().createDAO(MessageDAO.class);
			  
			  /**
			   * As Dynamic SQL has not been implemented in AS2 yet, 
			   * Hence, the program need to cast those null value to wild cart character
			   */ 
			  MessageDVO criteriaDVO = (MessageDVO)msgDAO.createDVO();
			  criteriaDVO.setMessageId(checkString(msgId));
			  criteriaDVO.setMessageBox(checkMessageBox(msgBox));			  			  
			  criteriaDVO.setAs2From(checkString(as2From));
			  criteriaDVO.setAs2To(checkString(as2To));
			  criteriaDVO.setStatus(checkMessageStatus(status));
			  List results = msgDAO.findMessagesByHistory(criteriaDVO, resultLimit, 0);
			  generateReply(response, results, wsi);
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
	    	List  messageList, boolean wsi) throws SOAPRequestException {
	        try {
	    		SOAPElement listElement = createElement("messageList", NAMESPACE); 
	        	
	            Iterator messagesIterator = messageList.iterator();
	            for (int i = 0; messagesIterator.hasNext(); i++) {
	                MessageDVO currentMessage = (MessageDVO) messagesIterator.next();
	            	
	                // Create Message Element and append Value of MessageID and MessageBox 
	                SOAPElement msgElement = createElement("messageElement", NAMESPACE);                
	                SOAPElement childElement_MsgId = createElement("messageId", NAMESPACE, currentMessage.getMessageId());
	                
	                String msgBox = currentMessage.getMessageBox();
	                if(msgBox.equalsIgnoreCase(MessageDVO.MSGBOX_IN))
	                	msgBox = "inbox";
	                if(msgBox.equalsIgnoreCase(MessageDVO.MSGBOX_OUT))
	                	msgBox = "outbox";
	                SOAPElement childElement_MsgBox = createElement("messageBox", NAMESPACE, msgBox);
	                msgElement.addChildElement(childElement_MsgId);
	                msgElement.addChildElement(childElement_MsgBox);
	                
	                listElement.addChildElement(msgElement);
	            }
	            
	        	if (wsi) {
	        		AS2PlusProcessor.getInstance().getLogger().debug("WS-I Response");
	        		
		    		SOAPElement responseElement = createElement("ResponseElement", NAMESPACE); 
		            responseElement.addChildElement(listElement);
		            response.setBodies(new SOAPElement[] { responseElement });
	        	} else {
	        		AS2PlusProcessor.getInstance().getLogger().debug("Non WS-I Response");
	        		
	        		response.setBodies(new SOAPElement[] { listElement });
	        	}
	        } catch (Exception e) {
	            throw new SOAPRequestException("Unable to generate reply message", e);
	        }
	    }
}
