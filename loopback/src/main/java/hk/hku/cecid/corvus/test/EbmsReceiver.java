/*
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved. This software is licensed
 * under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1] [1]
 * http://www.gnu.org/licenses/gpl.txt
 */
package hk.hku.cecid.corvus.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

/**
 * The <code>EbmsReceiver</code> is 
 * 
 * 
 * @author kochiu, Twinsen (modifiers)
 */
public class EbmsReceiver {

	// This is the XML namspace URI for Ebms. It is used when creating web services request.
	private String nsURI = "http://service.ebms.edi.cecid.hku.hk/";
	
	// This is the XML namespace prefix for Ebms.
	private String nsPrefix="tns";
	
	// The "receiver" web services URL. It should be "http://[ip]:[port]/corvus/httpd/ebms/receiver".
	private URL hermes2ReceiverWSURL;
	
	/** 
	 * Explicit Constructor.
	 * 
	 * @param receiverWSURLStr
	 * 			The "receiver" web services URL. It should be "http://[ip]:[port]/corvus/httpd/ebms/receiver".
	 * @throws MalformedURLException
	 * 			When <code>receiverListWSURL</code> is an invalid URL.
	 */
	public EbmsReceiver(String receiverWSURLStr) throws MalformedURLException {
		this.hermes2ReceiverWSURL = new URL(receiverWSURLStr);
	}
	
	/**
	 * Send the web service request to Hermes2 requesting for downloading the payload  
	 * of particular message. 
	 * 
	 * @return 	An iterator which contains all message id of the message(s) which 
	 * 			are ready to download.
	 * 			 			
	 * @throws Exception 			
	 */
	public Iterator downloadPayloads(String messageId) throws Exception {
		// Make a SOAP Connection and SOAP Message.	
		SOAPConnection soapConn = SOAPConnectionFactory.newInstance().createConnection();
		SOAPMessage request = MessageFactory.newInstance().createMessage();
		
		// Populate the SOAP Body
		SOAPBody soapBody = request.getSOAPPart().getEnvelope().getBody();
		soapBody.addChildElement(createElement("messageId", nsPrefix, nsURI, messageId));
		
		// Send the request to Hermes and return the payload if any.		
		SOAPMessage response = soapConn.call(request, hermes2ReceiverWSURL);		
		SOAPBody responseBody = response.getSOAPBody();
		
		/*
		 * The response is something like:
		 * <soap-body>
		 * 	<hasMessage>
		 * </soap-body>	
		 * 		.
		 * 		.
		 * attachment as a MIME part. 
		 */ 		
		if (!responseBody.hasFault()){
			// See whether has <hasMessage> element.
			SOAPElement hasMessageElement = getFirstChild(responseBody, "hasMessage", nsURI);			
			ArrayList payloadsList = new ArrayList();
			if (hasMessageElement != null){				
				Iterator attachmentPartIter = response.getAttachments();
				while(attachmentPartIter.hasNext()) {
					AttachmentPart attachmentPart = (AttachmentPart) attachmentPartIter.next();
					// Add a new payload to the payload list.
					Payload payload = new Payload(
						attachmentPart.getDataHandler().getInputStream(), 
						attachmentPart.getContentType());
					payloadsList.add(payload);
				}
			}
			return payloadsList.iterator();
		} else {
			throw new SOAPException(responseBody.getFault().getFaultString());			
		}
	}
	
	/**
	 * Get the first child with <code>childLocalName</code> and <code>childNsURI</code>
	 * at the specified <code>soapElement</code>. 
	 * 
	 * @param soapElement
	 * 			The parent element you want to search from.
	 * @param childLocalName
	 * 			The element's tag name you want to search.
	 * @param childNsURI
	 * 			The element's namespace uri you want to search. 
	 * @return an SOAP Element if found, otherwise null.
	 * @throws SOAPException unable to get the children. 				
	 */
	private SOAPElement createElement(String localName, String nsPrefix, String nsURI, String value) 
		throws SOAPException 
	{
		SOAPElement soapElement = SOAPFactory.newInstance().createElement(localName, nsPrefix, nsURI); 
		soapElement.addTextNode(value);
		return soapElement;
	}
	
	/**
	 * Get the first child with <code>childLocalName</code> and <code>childNsURI</code>
	 * at the specified <code>soapElement</code>. 
	 * 
	 * @param soapElement
	 * 			The parent element you want to search from.
	 * @param childLocalName
	 * 			The element's tag name you want to search.
	 * @param childNsURI
	 * 			The element's namespace uri you want to search. 
	 * @return an SOAP Element if found, otherwise null.
	 * @throws SOAPException unable to get the children. 				
	 */
	private SOAPElement getFirstChild(SOAPElement soapElement, String childLocalName, String childNsURI) 
		throws SOAPException 
	{
		Name childName = SOAPFactory.newInstance().createName(childLocalName, null, childNsURI);
		Iterator childIter = soapElement.getChildElements(childName);
		if (childIter.hasNext())
			return (SOAPElement)childIter.next();
		else
			return null;
	}
}
