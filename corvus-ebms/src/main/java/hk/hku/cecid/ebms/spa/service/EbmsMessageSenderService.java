package hk.hku.cecid.ebms.spa.service;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.pkg.MessageHeader;
import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.EbmsUtility;
import hk.hku.cecid.ebms.spa.dao.PartnershipDAO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
import hk.hku.cecid.ebms.spa.handler.MessageServiceHandler;
import hk.hku.cecid.ebms.spa.handler.MessageServiceHandlerException;
import hk.hku.cecid.ebms.spa.listener.EbmsRequest;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.soap.SOAPFaultException;
import hk.hku.cecid.piazza.commons.soap.SOAPRequest;
import hk.hku.cecid.piazza.commons.soap.SOAPRequestException;
import hk.hku.cecid.piazza.commons.soap.SOAPResponse;
import hk.hku.cecid.piazza.commons.soap.WebServicesAdaptor;
import hk.hku.cecid.piazza.commons.soap.WebServicesRequest;
import hk.hku.cecid.piazza.commons.soap.WebServicesResponse;
import hk.hku.cecid.piazza.commons.util.Generator;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

import java.util.Iterator;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

/**
 * EbmsMessageSenderService
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class EbmsMessageSenderService extends WebServicesAdaptor {

	public static final String NAMESPACE = "http://service.ebms.edi.cecid.hku.hk/";
	
    public void serviceRequested(WebServicesRequest request,
            WebServicesResponse response) throws SOAPRequestException,
            DAOException {

        String cpaId = null;
        String service = null;
        String serviceType = null;
        String action = null;        
        String convId = null;
        String fromPartyId = null;
        String[] fromPartyIds = null;
        String fromPartyType = null;
        String[] fromPartyTypes = null;
        String toPartyId = null;
        String[] toPartyIds = null;
        String toPartyType = null;
        String[] toPartyTypes = null;
        String refToMessageId = null;            
        
        boolean wsi = false;
        
        SOAPBodyElement[] bodies = (SOAPBodyElement[]) request.getBodies();
        // WS-I <RequestElement>
        if (bodies != null && bodies.length > 0 &&
                isElement(bodies[0], "RequestElement", NAMESPACE)) {

            // Kenneth Wong [20170811] : To reduce the noise in ebms.log
            // EbmsProcessor.core.log.debug("WS-I Request");

            wsi = true;

            SOAPElement[] childElement = getChildElementArray(bodies[0]);
            cpaId = getText(childElement, "cpaId");
            service = getText(childElement, "service");
            serviceType = getText(childElement, "serviceType");
            action = getText(childElement, "action");        
            convId = getText(childElement, "convId");
            fromPartyId = getText(childElement, "fromPartyId");
            fromPartyIds = StringUtilities.tokenize(fromPartyId, ",");
            fromPartyType = getText(childElement, "fromPartyType");
            fromPartyTypes = StringUtilities.tokenize(fromPartyType, ",");
            toPartyId = getText(childElement, "toPartyId");
            toPartyIds = StringUtilities.tokenize(toPartyId, ",");
            toPartyType = getText(childElement, "toPartyType");
            toPartyTypes = StringUtilities.tokenize(toPartyType, ",");
            refToMessageId = getText(childElement, "refToMessageId");
        } else {
            // Kenneth Wong [20170811] : To reduce the noise in ebms.log
            // EbmsProcessor.core.log.debug("Non WS-I Request");

            cpaId = getText(bodies, "cpaId");
            service = getText(bodies, "service");
            serviceType = getText(bodies, "serviceType");
            action = getText(bodies, "action");
            convId = getText(bodies, "convId");
            fromPartyId = getText(bodies, "fromPartyId");
            fromPartyIds = StringUtilities.tokenize(fromPartyId, ",");
            fromPartyType = getText(bodies, "fromPartyType");
            fromPartyTypes = StringUtilities.tokenize(fromPartyType, ",");
            toPartyId = getText(bodies, "toPartyId");
            toPartyIds = StringUtilities.tokenize(toPartyId, ",");
            toPartyType = getText(bodies, "toPartyType");
            toPartyTypes = StringUtilities.tokenize(toPartyType, ",");
            refToMessageId = getText(bodies, "refToMessageId");
        }
        
        if (cpaId == null || service == null || action == null
                || convId == null || fromPartyId == null
                || fromPartyType == null || toPartyId == null
                || toPartyType == null) {
            throw new SOAPFaultException(SOAPFaultException.SOAP_FAULT_CLIENT,
                    "Missing delivery information");
        } else if (fromPartyIds.length != fromPartyTypes.length
                || toPartyIds.length != toPartyTypes.length) {
            throw new SOAPFaultException(SOAPFaultException.SOAP_FAULT_CLIENT,
                    "The number of From/To Party and its type is not same.");
        } else {
            PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao
                    .createDAO(PartnershipDAO.class);
            PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO
                    .createDVO();
            partnershipDVO.setCpaId(cpaId);
            partnershipDVO.setService(service);
            partnershipDVO.setAction(action);
            if (!partnershipDAO.findPartnershipByCPA(partnershipDVO)) {
                throw new SOAPFaultException(
                        SOAPFaultException.SOAP_FAULT_CLIENT,
                        "No registered sender channel");
            }
        }

        EbmsProcessor.core.log.info("Outbound payload received - cpaId: " 
        		+ cpaId 
        		+ ", service: " 	+ service 
        		+ ", serviceType:" 	+ serviceType        		
                + ", action: " 		+ action 
                + ", convId: " 		+ convId 
                + ", fromPartyId: " + fromPartyId
                + ", fromPartyType: " + fromPartyType 
                + ", toPartyId: "	  + toPartyId 
                + ", toPartyType: " + toPartyType
                + ", refToMessageId: " + refToMessageId);

        EbxmlMessage ebxmlMessage;
        String messageId;
        try {
            ebxmlMessage = new EbxmlMessage();
            MessageHeader msgHeader = ebxmlMessage.addMessageHeader();
           
            msgHeader.setCpaId(cpaId);
            msgHeader.setConversationId(convId);
            msgHeader.setService(service);
            msgHeader.setAction(action);
            
            if (serviceType != null && !serviceType.equals(""))
            	msgHeader.setServiceType(serviceType);
            
            messageId = Generator.generateMessageID();
            ebxmlMessage.getMessageHeader().setMessageId(messageId);
            EbmsProcessor.core.log.info("Genereating message id: " + messageId);

            msgHeader.setTimestamp(EbmsUtility.getCurrentUTCDateTime());
            
            for (int i = 0; i < fromPartyIds.length; i++) {
                msgHeader.addFromPartyId(fromPartyIds[i], fromPartyTypes[i]);
            }

            for (int i = 0; i < toPartyIds.length; i++) {
                msgHeader.addToPartyId(toPartyIds[i], toPartyTypes[i]);
            }

            if (refToMessageId != null) {
                if (!refToMessageId.equals("")) {
                    msgHeader.setRefToMessageId(refToMessageId);
                }
            }
            
            SOAPRequest soapRequest = (SOAPRequest) request.getSource();
            SOAPMessage soapRequestMessage = soapRequest.getMessage();

            Iterator i = soapRequestMessage.getAttachments();

            for (int j = 0; i.hasNext(); j++) {
                AttachmentPart attachmentPart = (AttachmentPart) i.next();

                ebxmlMessage
                	.addPayloadContainer(attachmentPart.getDataHandler(),
                        "Payload-" + String.valueOf(j), null);
                
                /**
                 * Modifification by Jumbo
                 * The original code will always override the contentId
                 *  with "Payload-n" all the time, 
                 * 
                 * This change is pending until a complete study of content-id handling in H2O.
                 */
                /*
                String contentId = attachmentPart.getContentId();
                if(contentId == null || contentId.equals("")){
                	contentId =  "Payload-" + String.valueOf(j);
                }
                ebxmlMessage
                        .addPayloadContainer(attachmentPart.getDataHandler(),
                        		contentId , null);
                */
            }

        } catch (Exception e) {
            EbmsProcessor.core.log.error("Error in constructing ebxml message",
                    e);
            throw new SOAPRequestException(
                    "Error in constructing ebxml message", e);
        }

        // construct the ebmsRequest and pass to outbound listener
        EbmsRequest ebmsRequest = new EbmsRequest(request);
        ebmsRequest.setMessage(ebxmlMessage);

        MessageServiceHandler msh = MessageServiceHandler.getInstance();
        try {
            msh.processOutboundMessage(ebmsRequest, null);
        } catch (MessageServiceHandlerException e) {
            EbmsProcessor.core.log.error(
                    "Error in passing ebms Request to msh outbound", e);
            throw new SOAPRequestException(
                    "Error in passing ebms Request to msh outbound", e);
        }

        generateReply(response, messageId, wsi);

        EbmsProcessor.core.log.info("Outbound payload processed - cpaId: "
                + cpaId + ", service: " + service + ", action: " + action
                + ", convId: " + convId + ", fromPartyId: " + fromPartyId
                + ", fromPartyType: " + fromPartyType + ", toPartyId: "
                + toPartyId + ", toPartyType: " + toPartyType
                + ", refToMessageId: " + refToMessageId);
    }

    private void generateReply(WebServicesResponse response, String messageId, boolean wsi)
            throws SOAPRequestException {
        try {
            if (wsi) {
                // Kenneth Wong [20170811] : To reduce the noise in ebms.log
                // EbmsProcessor.core.log.debug("WS-I Response");

                SOAPResponse soapResponse = (SOAPResponse) response.getTarget();
                SOAPMessage soapResponseMessage = soapResponse.getMessage();
                soapResponseMessage.getMimeHeaders().setHeader("Content-Type", "application/xop+xml; type=\"text/xml\"");
                soapResponseMessage.getSOAPPart().addMimeHeader("Content-ID", "<SOAPBody>");
                soapResponseMessage.getSOAPPart().addMimeHeader("Content-Transfer-Encoding", "binary");

                SOAPElement responseElement = createElement("ResponseElement", NAMESPACE);
                SOAPElement messageIdElement = createElement("message_id", NAMESPACE, messageId);
                responseElement.addChildElement(messageIdElement);
                response.setBodies(new SOAPElement[] { responseElement });
            } else {
                // Kenneth Wong [20170811] : To reduce the noise in ebms.log
                // EbmsProcessor.core.log.debug("Non WS-I Response");

                SOAPElement responseElement = createElement("message_id", NAMESPACE, messageId);
                response.setBodies(new SOAPElement[] { responseElement });
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
