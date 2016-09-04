/*
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved. This software is licensed
 * under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1] [1]
 * http://www.gnu.org/licenses/gpl.txt
 */
package hk.hku.cecid.corvus.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
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
 * The <code>EbmsSender</code> is a simple web service client for sending
 * web services request to Hermes2 for sending <code>EbXML Message</code>
 * to recipient (self, loopback).
 * 
 * @author kochiu, Twinsen Tsang (modifers)
 */
public class EbmsSender {

	// This is the XML namspace URI for Ebms. It is used when creating web services request.
	private String nsURI = "http://service.ebms.edi.cecid.hku.hk/";
		
	// This is the XML namespace prefix for Ebms. 
	private String nsPrefix="tns";

	// The "sender" web services URL. It should be "http://[ip]:[port]/corvus/httpd/ebms/sender".
	private URL senderWSURL;
	
	/*
	 * The following are the required parameters to deliver the EbXML Message. 
	 */
	private String cpaId;
	private String service;
	private String action;
	private String conversationId;
	private String fromPartyId;
	private String fromPartyType;
	private String toPartyId;
	private String toPartyType;
	private String refToMessageId;
	
	/** 
	 * Explicit Constructor.
	 * 
	 * @param senderWSURL
	 * 			The "sender" web services URL. It should be "http://[ip]:[port]/corvus/httpd/ebms/sender".
	 * @param cpaId 			 
	 * 			The CPAid value of ebms message you want to generate.
	 * @param service
	 * 			The Service value of ebms message you want to generate.
	 * @param action
	 * 			The Action value of ebms message you want to generate.
	 * @param conversationId
	 * 			The conversation value of ebms message you want to generate.
	 * @param fromPartyId
	 * 			The fromPartyId value of ebms message you want to generate.
	 * @param fromPartyType
	 * 			The fromPartyType value of ebms message you want to generate.
	 * @param toPartyId
	 * 			The toPartyId value of ebms message you want to generate.
	 * @param toPartyType
	 * 			The toPartyType value of ebms message you want to generate.
	 * @param refToMessageId
	 * 			The refToMessageId value of ebms message you want to generate.
	 * 
	 * @throws MalformedURLException
	 * 			When <code>senderWSURL</code> is an invalid URL.
	 */
	public EbmsSender(String senderWSURL, String cpaId, String service,
		String action, String conversationId, String fromPartyId,
		String fromPartyType, String toPartyId, String toPartyType,
		String refToMessageId) throws MalformedURLException 
	{
		this.senderWSURL = new URL(senderWSURL);
		this.cpaId = cpaId;
		this.service = service;
		this.action = action;
		this.conversationId = conversationId;
		this.fromPartyId = fromPartyId;
		this.fromPartyType = fromPartyType;
		this.toPartyId = toPartyId;
		this.toPartyType = toPartyType;
		this.refToMessageId = refToMessageId;
	}

	/**
	 * Send the web service request to Hermes2 requesting for sending  
	 * a <code>EbXML message</code> loopback with a set of <code>payloads</code>.
	 * 
	 * @param payloads
	 * 			The payload set acting as the attachment in <code>EbXML Message</code>. 
	 * @return 
	 * 			A String representing the ID of the message you request to send. 			
	 * @throws Exception 			
	 * 
	 * @see hk.hku.cecid.corvus.test.Payload
	 */
	public String send(Payload [] payloads) throws Exception {
		// Make a SOAP Connection and SOAP Message.
		SOAPConnection soapConn = SOAPConnectionFactory.newInstance().createConnection();
		SOAPMessage request = MessageFactory.newInstance().createMessage();
		
		// Populate the SOAP Body by filling the required parameters.
		/* This is the sample WSDL request for the sending EbMS message WS request.
		 *  
		 * <cpaId> ebmscpaid </cpaId>
		 * <service> http://localhost:8080/corvus/httpd/ebms/inbound <service>
		 * <action> action </action>
		 * <convId> convId </convId> 
		 * <fromPartyId> fromPartyId </fromPartyId>
		 * <fromPartyType> fromPartyType </fromPartyType>
		 * <toPartyId> toPartyId </toPartyId> 
		 * <toPartyType> toPartyType </toPartyType> 
		 * <refToMessageId> </refToMessageId>  
		 */
		SOAPBody soapBody = request.getSOAPBody();
		soapBody.addChildElement(createElement("cpaId", nsPrefix, nsURI, cpaId));
		soapBody.addChildElement(createElement("service", nsPrefix, nsURI, service));
		soapBody.addChildElement(createElement("action", nsPrefix, nsURI, action));
		soapBody.addChildElement(createElement("convId", nsPrefix, nsURI, conversationId));
		soapBody.addChildElement(createElement("fromPartyId", nsPrefix, nsURI, fromPartyId));
		soapBody.addChildElement(createElement("fromPartyType", nsPrefix, nsURI, fromPartyType));
		soapBody.addChildElement(createElement("toPartyId", nsPrefix, nsURI, toPartyId));
		soapBody.addChildElement(createElement("toPartyType", nsPrefix, nsURI, toPartyType));
		soapBody.addChildElement(createElement("refToMessageId", nsPrefix, nsURI, refToMessageId));
		
		// Add the payloads
		for (int i=0; i < payloads.length; i++) {
			AttachmentPart attachmentPart = request.createAttachmentPart();
			FileDataSource fileDS = new FileDataSource(new File(payloads[i].getFilePath()));
			attachmentPart.setDataHandler(new DataHandler(fileDS));
			attachmentPart.setContentType(payloads[i].getContentType());
			request.addAttachmentPart(attachmentPart);
		}
		
		// Send the request to Hermes and return the message Id to "sender" web services.		
		SOAPMessage response = soapConn.call(request, senderWSURL);
		SOAPBody responseBody = response.getSOAPBody();		
		
		if (!responseBody.hasFault()){
			SOAPElement messageIdElement = getFirstChild(responseBody, "message_id", nsURI);
			return messageIdElement == null ? null : messageIdElement.getValue();
		} else {
			throw new SOAPException(responseBody.getFault().getFaultString());
		}		
	}

	/**
	 * Create a SOAP Element with specified <code>localName</code> (TagName), <code>nsPrefix</code>
	 * (Namespace prefix), <code>nsURI</code> (Namespace URI) and <code>value</code> (TagValue).
	 * 
	 * @param localName
	 * 			The tag name of SOAP Element.
	 * @param nsPrefix
	 * 			The namespace prefix of SOAP Element.
	 * @param nsURI
	 * 			The namespace URI of SOAP Element
	 * @param value
	 * 			The tag value of SOAP Element
	 * @return an SOAP Element. 			
	 * @throws SOAPException unable to create the SOAPElement
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
	private SOAPElement getFirstChild(SOAPElement soapElement,
			String childLocalName, String childNsURI) throws SOAPException {
		Name childName = SOAPFactory.newInstance().createName(childLocalName, null, childNsURI);
		Iterator childIter = soapElement.getChildElements(childName);
		if (childIter.hasNext())
			return (SOAPElement)childIter.next();
		return null;
	}
}