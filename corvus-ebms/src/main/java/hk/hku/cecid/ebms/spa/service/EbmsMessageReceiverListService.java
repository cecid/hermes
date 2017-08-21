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

import javax.xml.soap.SOAPElement;

import org.w3c.dom.Element;

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

        Element[] bodies = request.getBodies();
        String cpaId = getText(bodies, "cpaId");
        String service = getText(bodies, "service");
        String action = getText(bodies, "action");
        String convId = getText(bodies, "convId");
        String fromPartyId = getText(bodies, "fromPartyId");
        String fromPartyType = getText(bodies, "fromPartyType");
        String toPartyId = getText(bodies, "toPartyId");
        String toPartyType = getText(bodies, "toPartyType");
        String strNumOfMessages = getText(bodies, "numOfMessages");
        
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

        generateReply(response, messageIds);
    }

    private void generateReply(WebServicesResponse response,
            String[] message_ids) throws SOAPRequestException {
        try {
            SOAPElement rootElement = createElement("messageIds", "",
                    "http://service.ebms.edi.cecid.hku.hk/", "MessageIDs");

            for (int i = 0; i < message_ids.length; i++) {
                SOAPElement childElement = createText("messageId",
                        message_ids[i], "http://service.ebms.edi.cecid.hku.hk/");
                rootElement.addChildElement(childElement);
            }

            response.setBodies(new SOAPElement[] { rootElement });
        } catch (Exception e) {
            throw new SOAPRequestException("Unable to generate reply message",
                    e);
        }
    }

    protected boolean isCacheEnabled() {
        return false;
    }
}