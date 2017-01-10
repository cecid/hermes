package hk.hku.cecid.edi.as2.service;

import hk.hku.cecid.edi.as2.AS2PlusProcessor;
import hk.hku.cecid.edi.as2.dao.AS2DAOHandler;
import hk.hku.cecid.edi.as2.dao.PartnershipDAO;
import hk.hku.cecid.edi.as2.dao.PartnershipDVO;
import hk.hku.cecid.edi.as2.module.OutgoingMessageProcessor;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.edi.as2.util.AS2Util;
import hk.hku.cecid.piazza.commons.activation.InputStreamDataSource;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DAOFactory;
import hk.hku.cecid.piazza.commons.soap.SOAPFaultException;
import hk.hku.cecid.piazza.commons.soap.SOAPRequest;
import hk.hku.cecid.piazza.commons.soap.SOAPRequestException;
import hk.hku.cecid.piazza.commons.soap.SOAPResponse;
import hk.hku.cecid.piazza.commons.soap.WebServicesAdaptor;
import hk.hku.cecid.piazza.commons.soap.WebServicesRequest;
import hk.hku.cecid.piazza.commons.soap.WebServicesResponse;

import java.io.InputStream;
import java.util.Iterator;
import java.util.zip.InflaterInputStream;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

/**
 * AS2MessageSenderService
 * 
 * @author Hugo Y. K. Lam
 * 
 */
public class AS2MessageSenderService extends WebServicesAdaptor {

	public static final String NAMESPACE = "http://service.as2.edi.cecid.hku.hk/";
	
	public void serviceRequested(WebServicesRequest request,
			WebServicesResponse response) throws SOAPRequestException,
			DAOException {

		String as2From = null;
		String as2To = null;
		String type = null;
		
        boolean wsi = false;
        
        SOAPBodyElement[] bodies = (SOAPBodyElement[]) request.getBodies();
		// WS-I <RequestElement> 
	    if (bodies != null && bodies.length > 0 && 
	    		isElement(bodies[0], "RequestElement", NAMESPACE)) {
        	
	    	AS2PlusProcessor.getInstance().getLogger().debug("WS-I Request");
	    	
	    	wsi = true;
	    	
	    	SOAPElement[] childElement = getChildElementArray(bodies[0]);
			as2From = getText(childElement, "as2_from");
			as2To = getText(childElement, "as2_to");
			type = getText(childElement, "type");
		
	    } else {
	    	AS2PlusProcessor.getInstance().getLogger().debug("Non WS-I Request");
	    	
			as2From = getText(bodies, "as2_from");
			as2To = getText(bodies, "as2_to");
			type = getText(bodies, "type");
	    }

		if (as2From == null || as2To == null || type == null) {
			throwSoapClientFault("Missing delivery information");
		}
		
		DAOFactory daoFactory = AS2PlusProcessor.getInstance().getDAOFactory();		
		AS2DAOHandler daoHandler = new AS2DAOHandler(daoFactory);
		
		PartnershipDAO partnershipDAO = daoHandler.createPartnershipDAO();
		PartnershipDVO partnership = partnershipDAO.findByParty(as2From, as2To);		
		if (partnership == null) {
			throwSoapClientFault("No registered partnership");
		}		

		AS2PlusProcessor.getInstance().getLogger().info(
				"Outbound payload received - From: " + as2From + ", To: "
						+ as2To + ", Type: " + type);

		SOAPRequest soapRequest = (SOAPRequest) request.getSource();
		SOAPMessage soapRequestMessage = soapRequest.getMessage();		
		
		Iterator<?> attachments = soapRequestMessage.getAttachments();
		if (!attachments.hasNext()) {
			throwSoapClientFault("Missing payload");
		}
		
		AttachmentPart attachment = (AttachmentPart) attachments.next();
		String[] mimeHeaders = attachment.getMimeHeader("Content-Disposition");
		String fileName = AS2Util.getFileNameFromMIMEHeader(mimeHeaders);

		if (fileName != null) {
			// Exception is thrown if invalid
			checkFileNameValid(fileName);
		}

		InputStreamDataSource inputStreamDataSource = null;
		try {
			InputStream inputStream = attachment.getDataHandler().getInputStream();
			if ("application/deflate".equals(attachment.getContentType())) {
				inputStream = new InflaterInputStream(inputStream);
			}
			inputStreamDataSource = new InputStreamDataSource(
					inputStream, type, fileName);
		} catch (Exception e) {
			throwSoapServerFault("Unable to extract payload", e);
		}
		
		AS2Message as2Msg = null;
		try {
			OutgoingMessageProcessor outgoing = 
					AS2PlusProcessor.getInstance().getOutgoingMessageProcessor();
	
			as2Msg = outgoing.storeOutgoingMessage(
					AS2Message.generateID(), type, partnership, inputStreamDataSource);
		} catch (Exception e) {
			throwSoapServerFault("Unable to store message", e);
		}

		generateReply(response, as2Msg.getMessageID(), wsi);
	}

	private void checkFileNameValid(String fileName) throws SOAPFaultException {
		if (fileName.length() > 56) {
			// 56 characters limits :
			// On RFC 2183 stated, each parameter value should not longer than
			// 78 charactes,
			// hence, 78 characters deduct by the 22 character (which are
			// "attachment; filename=")
			// is equal to 56.
			throwSoapClientFault(
					"Filename of attachement is too long. Suggesting filename should not longer than 56 characters.");
		}

		char[] filenameChars = fileName.toCharArray();
		for (char charValue : filenameChars) {
			// As the MimeValue support ASCII values only,
			// Check if the filename contains Non_ASCII characters to avoid any
			// abnormal transformation caused durning message transfer
			if (charValue >= 128) {
				throwSoapClientFault(
						"Filename contains Non-ASCII characters, and it may cause error on Receiver.");
			}
		}
	}

	private void generateReply(WebServicesResponse response, String messageId, boolean wsi)
			throws SOAPRequestException {
		try {
			SOAPElement messageIdElement = createElement("message_id", NAMESPACE, messageId);

			if (wsi) {
        		AS2PlusProcessor.getInstance().getLogger().debug("WS-I Response");
        		
	            SOAPResponse soapResponse = (SOAPResponse) response.getTarget();
	            SOAPMessage soapResponseMessage = soapResponse.getMessage();
	            soapResponseMessage.getMimeHeaders().setHeader("Content-Type", "application/xop+xml; type=\"text/xml\"");
	            soapResponseMessage.getSOAPPart().addMimeHeader("Content-ID", "<SOAPBody>");
	            soapResponseMessage.getSOAPPart().addMimeHeader("Content-Transfer-Encoding", "binary");

	            SOAPElement responseElement = createElement("ResponseElement", NAMESPACE);	            
	            responseElement.addChildElement(messageIdElement);            
	            response.setBodies(new SOAPElement[] { responseElement });
			} else {
				AS2PlusProcessor.getInstance().getLogger().debug("Non WS-I Response");
				
				response.setBodies(new SOAPElement[] { messageIdElement });
			}
		} catch (Exception e) {
			throwSoapServerFault("Unable to generate reply message", e);
		}
	}

	protected boolean isCacheEnabled() {
		return false;
	}
	
	private void throwSoapServerFault(String message, Exception e) throws SOAPFaultException {
		throw new SOAPFaultException(SOAPFaultException.SOAP_FAULT_SERVER, message, e);
	}
	
	private void throwSoapClientFault(String message) throws SOAPFaultException {
		throw new SOAPFaultException(SOAPFaultException.SOAP_FAULT_CLIENT, message);
	}
}
