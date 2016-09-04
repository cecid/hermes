/*
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved. This software is licensed
 * under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1] [1]
 * http://www.gnu.org/licenses/gpl.txt
 */
package hk.hku.cecid.corvus.test;

import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
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
 * The <code>AS2Sender</code> is a simple web service client for sending
 * web services request to Hermes2 for sending <code>AS2 Message</code>
 * to recipient (self, loopback). 
 * 
 * Creation Date: 21/05/2007
 * 
 * @author Twinsen Tsang
 */
public class AS2Sender {

	// This is the XML namspace URI for Ebms. It is used when creating web services request.
	private String nsURI = "http://service.as2.edi.cecid.hku.hk/";
		
	// This is the XML namespace prefix for Ebms. 
	private String nsPrefix="tns";

	// The "sender" web services URL. It should be "http://[ip]:[port]/corvus/httpd/as2/sender".
	private URL senderWSURL;
	
	/*
	 * The following are the required parameters to deliver the AS2 Message. 
	 */
	private String as2From;
	private String as2To;
	private String type;

	/**
	 * Explicit Constructor.
	 * 
	 * @param senderWSURL
	 * 			The "sender" web services URL. It should be "http://[ip]:[port]/corvus/httpd/as2/sender".
	 * @param as2From   		
	 * @param as2To 
	 * @param type
	 */
	public AS2Sender(String senderWSURL, String as2From, String as2To, String type) 
		throws MalformedURLException
	{	
		this.senderWSURL = new URL(senderWSURL);
		this.as2From = as2From;
		this.as2To	 = as2To;
		this.type 	 = type == null ? "xml" : type;		
	}
	
	/**
	 * Send the web service request to Hermes2 requesting for sending  
	 * a <code>AS2 message</code> loopback with a set of <code>payloads</code>.
	 * 
	 * @param payloads
	 * 			The payload set acting as the attachment in <code>AS2 Message</code>. 
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
		/* This is the sample WSDL request for the sending AS2 message WS request.
		 *  
		 * <as2_from> as2from </as2_from>
		 * <as2_to> as2to </as2_to>
		 * <type> type </type>  
		 */
		SOAPBody soapBody = request.getSOAPBody();
		soapBody.addChildElement(createElement("as2_from", nsPrefix, nsURI, this.as2From));
		soapBody.addChildElement(createElement("as2_to"	 , nsPrefix, nsURI, this.as2To));
		soapBody.addChildElement(createElement("type"	 , nsPrefix, nsURI, this.type));		
		
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
