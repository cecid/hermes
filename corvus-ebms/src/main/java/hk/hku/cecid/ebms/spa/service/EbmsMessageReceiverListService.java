package hk.hku.cecid.ebms.spa.service;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.soap.SOAPFaultException;
import hk.hku.cecid.piazza.commons.soap.SOAPRequestException;
import hk.hku.cecid.piazza.commons.soap.WebServicesAdaptor;
import hk.hku.cecid.piazza.commons.soap.WebServicesRequest;
import hk.hku.cecid.piazza.commons.soap.WebServicesResponse;

import java.util.Iterator;
import java.util.List;

import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;

/**
 * EbmsMessageReceiverListService
 * 
 * @author Donahue Sze
 *  
 */
public class EbmsMessageReceiverListService extends WebServicesAdaptor {
    
    public static int MAX_NUMBER = 2147483647;
    
    public static String NAMESPACE = "http://service.ebms.edi.cecid.hku.hk/";

    public void serviceRequested(WebServicesRequest request,
            WebServicesResponse response) throws SOAPRequestException,
            DAOException {

        String cpaId = null;
        String service = null;
        String action = null;
        String convId = null;
        String fromPartyId = null;
        String fromPartyType = null;
        String toPartyId = null;
        String toPartyType = null;
        String strNumOfMessages = null;       
        
        boolean wsi = false;

		SOAPBodyElement[] bodies = (SOAPBodyElement[]) request.getBodies();
      	// WS-I <RequestElement> 
	    if (bodies != null && bodies.length == 1 && 
	    		isElement(bodies[0], "RequestElement", NAMESPACE)) {
	    	
	    	EbmsProcessor.core.log.debug("WS-I Request");
	    	
	    	wsi = true;
	    	
	    	SOAPElement[] childElement = getChildElementArray(bodies[0]);
	        cpaId = getText(childElement, "cpaId");
	        service = getText(childElement, "service");
	        action = getText(childElement, "action");
	        convId = getText(childElement, "convId");
	        fromPartyId = getText(childElement, "fromPartyId");
	        fromPartyType = getText(childElement, "fromPartyType");
	        toPartyId = getText(childElement, "toPartyId");
	        toPartyType = getText(childElement, "toPartyType");
	        strNumOfMessages = getText(childElement, "numOfMessages");
	    } else {
	    	EbmsProcessor.core.log.debug("Non WS-I Request");
	    	
	        cpaId = getText(bodies, "cpaId");
	        service = getText(bodies, "service");
	        action = getText(bodies, "action");
	        convId = getText(bodies, "convId");
	        fromPartyId = getText(bodies, "fromPartyId");
	        fromPartyType = getText(bodies, "fromPartyType");
	        toPartyId = getText(bodies, "toPartyId");
	        toPartyType = getText(bodies, "toPartyType");
	        strNumOfMessages = getText(bodies, "numOfMessages");
	    }
        
        if (cpaId == null || service == null || action == null) {
            throw new SOAPFaultException(SOAPFaultException.SOAP_FAULT_CLIENT,
                    "Missing request information");
        }

        int numOfMessages = 0;
        try {
            numOfMessages = Integer.valueOf(strNumOfMessages).intValue();
            if (numOfMessages <= 0) {
                numOfMessages = MAX_NUMBER;
            }
        } catch (Throwable t) {
            numOfMessages = MAX_NUMBER;
        }

        EbmsProcessor.core.log
                .info("Message Receiver received request - From: " + cpaId
                        + ", service: " + service + ", action: " + action 
                        + ", convId: " + convId + ", fromPartyId: "
                        + fromPartyId + ", fromPartyType: " + fromPartyType
                        + ", toPartyId: " + toPartyId + ", toPartyType: "
                        + toPartyType + ", Number of Messages: " + strNumOfMessages);

        String[] messageIds = null;
        try {
            MessageDAO messageDao = (MessageDAO) EbmsProcessor.core.dao
                    .createDAO(MessageDAO.class);
            MessageDVO messageDvo = (MessageDVO) messageDao.createDVO();
            messageDvo.setCpaId(cpaId);
            messageDvo.setService(service);
            messageDvo.setAction(action);
            
            messageDvo.setConvId(convId);
            messageDvo.setFromPartyId(fromPartyId);
            messageDvo.setFromPartyRole(fromPartyType);
            messageDvo.setToPartyId(toPartyId);
            messageDvo.setToPartyRole(toPartyType);
            
            List messagesList = messageDao.findMessageByCpa(messageDvo,
                    numOfMessages);
            Iterator messagesIterator = messagesList.iterator();

            messageIds = new String[messagesList.size()];

            for (int i = 0; messagesIterator.hasNext(); i++) {
                MessageDVO targetMessageDvo = (MessageDVO) messagesIterator
                        .next();
                messageIds[i] = new String(targetMessageDvo.getMessageId());
            }
        } catch (Exception e) {
            throw new SOAPRequestException("Unable to query the repository", e);
        }

        generateReply(response, messageIds, wsi);
    }

    private void generateReply(WebServicesResponse response,
            String[] message_ids, boolean wsi) throws SOAPRequestException {
        try {
    		SOAPElement messageIdsElement = createElement("messageIds", NAMESPACE);

            for (int i = 0; i < message_ids.length; i++) {
                SOAPElement childElement = createElement("messageId", NAMESPACE, message_ids[i]);
                messageIdsElement.addChildElement(childElement);
            }
            
        	if (wsi) {
        		EbmsProcessor.core.log.debug("WS-I Response");
        		
        		SOAPElement responseElement = createElement("ResponseElement", NAMESPACE);
                responseElement.addChildElement(messageIdsElement);
                response.setBodies(new SOAPElement[] { responseElement });        		
        	} else {
        		EbmsProcessor.core.log.debug("Non WS-I Response");
        		
                response.setBodies(new SOAPElement[] { messageIdsElement });        		        		
        	}

        } catch (Exception e) {
            throw new SOAPRequestException("Unable to generate reply message",
                    e);
        }
    }

    protected boolean isCacheEnabled() {
        return false;
    }
}