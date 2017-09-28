package hk.hku.cecid.ebms.spa.service;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.dao.MessageServerDAO;
import hk.hku.cecid.ebms.spa.handler.EbxmlMessageDAOConvertor;
import hk.hku.cecid.ebms.spa.handler.MessageClassifier;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.soap.SOAPFaultException;
import hk.hku.cecid.piazza.commons.soap.SOAPRequestException;
import hk.hku.cecid.piazza.commons.soap.SOAPResponse;
import hk.hku.cecid.piazza.commons.soap.WebServicesAdaptor;
import hk.hku.cecid.piazza.commons.soap.WebServicesRequest;
import hk.hku.cecid.piazza.commons.soap.WebServicesResponse;

import java.util.Iterator;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Element;

/**
 * EbmsMessageReceiverListService
 * 
 * @author Donahue Sze
 *  
 */
public class EbmsMessageReceiverService extends WebServicesAdaptor {
	
	public static String NAMESPACE = "http://service.ebms.edi.cecid.hku.hk/";
    
    public void serviceRequested(WebServicesRequest request,
            WebServicesResponse response) throws SOAPRequestException,
            DAOException {

        Element[] bodies = request.getBodies();
        String messageId = getText(bodies, "messageId");

        if (messageId == null) {
            throw new SOAPFaultException(SOAPFaultException.SOAP_FAULT_CLIENT,
                    "Missing request information");
        }

        EbmsProcessor.core.log
                .info("Message Receiver received download request - Message ID: "
                        + messageId);

        SOAPResponse soapResponse = (SOAPResponse) response.getTarget();
        SOAPMessage soapResponseMessage = soapResponse.getMessage();
        
        try {
            MessageDAO messageDao = (MessageDAO) EbmsProcessor.core.dao
                    .createDAO(MessageDAO.class);
            MessageDVO messageDvo = (MessageDVO) messageDao.createDVO();

            messageDvo.setMessageId(messageId);
            messageDvo.setMessageBox(MessageClassifier.MESSAGE_BOX_INBOX);

            if (messageDao.findMessage(messageDvo)) {
                if (messageDvo.getStatus().equals(
                        MessageClassifier.INTERNAL_STATUS_PROCESSED)) {
                    EbxmlMessage ebxmlMessage = EbxmlMessageDAOConvertor
                            .getEbxmlMessage(messageDvo.getMessageId(),
                                    MessageClassifier.MESSAGE_BOX_INBOX);

                    // add to reply attachment
                    Iterator i = ebxmlMessage.getSOAPMessage().getAttachments();

                    while (i.hasNext()) {
                        AttachmentPart attachmentPart = (AttachmentPart) i
                                .next();
                        soapResponseMessage.addAttachmentPart(attachmentPart);
                    }
                    
                    MessageServerDAO messageServerDao = (MessageServerDAO) EbmsProcessor.core.dao
                    	.createDAO(MessageServerDAO.class);
                    messageDvo.setStatus(MessageClassifier.INTERNAL_STATUS_DELIVERED);
                    messageDvo.setStatusDescription("Message is delivered");
                    messageServerDao.clearMessage(messageDvo);
                }
            }
        } catch (Exception e) {
            EbmsProcessor.core.log
                    .error(
                            "Error in processing request of message in receiver service",
                            e);
            throw new SOAPRequestException(
                    "Error in processing request of message in receiver service",
                    e);
        }

        generateReply(response, soapResponseMessage.countAttachments() > 0);
    }

    private void generateReply(WebServicesResponse response, boolean isReturned)
            throws SOAPRequestException {
        try {
            SOAPElement responseElement = createElement("hasMessage", NAMESPACE, Boolean.toString(isReturned));
            response.setBodies(new SOAPElement[] { responseElement });
        } catch (Exception e) {
            throw new SOAPRequestException("Unable to generate reply message",
                    e);
        }
    }

    protected boolean isCacheEnabled() {
        return false;
    }

}