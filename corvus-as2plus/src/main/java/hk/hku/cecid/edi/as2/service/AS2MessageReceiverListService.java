/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.service;

import hk.hku.cecid.edi.as2.AS2PlusProcessor;
import hk.hku.cecid.edi.as2.dao.MessageDAO;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
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
 * AS2MessageReceiverListService
 * 
 * @author Donahue Sze
 *  
 */
public class AS2MessageReceiverListService extends WebServicesAdaptor {

	public static final String NAMESPACE = "http://service.as2.edi.cecid.hku.hk/";
	
    public void serviceRequested(WebServicesRequest request,
            WebServicesResponse response) throws SOAPRequestException,
            DAOException {

        String as2From = null;
        String as2To = null;
        String strNumOfMessages = null;
    	
        boolean wsi = false;

		SOAPBodyElement[] bodies = (SOAPBodyElement[]) request.getBodies();
      	// WS-I <RequestElement> 
	    if (bodies != null && bodies.length == 1 && 
	    		isElement(bodies[0], "RequestElement", NAMESPACE)) {
	    	
	    	AS2PlusProcessor.getInstance().getLogger().debug("WS-I Request");
	    	
	    	wsi = true;
	    	
	    	SOAPElement[] childElement = getChildElementArray(bodies[0]);
	        as2From = getText(childElement, "as2From");
	        as2To = getText(childElement, "as2To");
	        strNumOfMessages = getText(childElement, "numOfMessages");
	    } else {
	    	AS2PlusProcessor.getInstance().getLogger().debug("Non WS-I Request");
	    	
	    	as2From = getText(bodies, "as2From");
	    	as2To = getText(bodies, "as2To");
	    	strNumOfMessages = getText(bodies, "numOfMessages");
	    }

        if (as2To == null || as2From == null || strNumOfMessages == null) {
            throw new SOAPFaultException(SOAPFaultException.SOAP_FAULT_CLIENT,
                    "Missing request information");
        }

        int numOfMessages = 0;
        try {
            numOfMessages = Integer.valueOf(strNumOfMessages).intValue();
            if (numOfMessages <= 0) {
                numOfMessages = 2147483647;
            }
        } catch (Throwable t) {
            throw new SOAPFaultException(SOAPFaultException.SOAP_FAULT_CLIENT,
                    "Incorrect request information");
        }

        AS2PlusProcessor.getInstance().getLogger().info("Message Receiver received request - From: "
                + as2From + ", To: " + as2To + ", Number of Messages: "
                + strNumOfMessages);

        MessageDAO messageDao = (MessageDAO) AS2PlusProcessor.getInstance().getDAOFactory()
                .createDAO(MessageDAO.class);
        MessageDVO messageDvo = (MessageDVO) messageDao.createDVO();
        messageDvo.setMessageId("%");
        messageDvo.setMessageBox(MessageDVO.MSGBOX_IN);
        messageDvo.setAs2From(checkStarAndConvertToPercent(as2From));
        messageDvo.setAs2To(checkStarAndConvertToPercent(as2To));
        messageDvo.setStatus(MessageDVO.STATUS_PROCESSED);

        List messagesList = messageDao.findMessagesByHistory(messageDvo,
                numOfMessages, 0);
        Iterator messagesIterator = messagesList.iterator();

        String[] messageIds = new String[messagesList.size()];

        for (int i = 0; messagesIterator.hasNext(); i++) {
            MessageDVO targetMessageDvo = (MessageDVO) messagesIterator.next();
            messageIds[i] = new String(targetMessageDvo.getMessageId());
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
        		AS2PlusProcessor.getInstance().getLogger().debug("WS-I Response");
        		
        		SOAPElement responseElement = createElement("ResponseElement", NAMESPACE);
                responseElement.addChildElement(messageIdsElement);
                response.setBodies(new SOAPElement[] { responseElement });        		
        	} else {
        		AS2PlusProcessor.getInstance().getLogger().debug("Non WS-I Response");
        		
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

    /**
     * @param parameter
     * @return
     */
    private String checkStarAndConvertToPercent(String parameter) {
        if (parameter.equals("")) {
            return new String("%");
        }
        return parameter.replace('*', '%');
    }
}