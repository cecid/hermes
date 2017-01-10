package hk.hku.cecid.corvus.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

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
 * The <code>EbmsReceiverList</code> is a simple web service client for sending
 * web services request to Hermes2 for querying all incoming <code>EbXML Message</code>
 * which are ready to download.
 * 
 * @author kochiu, Twinsen Tsang (modifiers)
 * @version 1.0.0
 */
public class EbmsReceiverList {

	// This is the XML namspace URI for Ebms. It is used when creating web services request.
	private String nsURI = "http://service.ebms.edi.cecid.hku.hk/";
	
	// This is the XML namespace prefix for Ebms.
	private String nsPrefix="tns";
	
	// The "receiverList" web services URL. It should be "http://[ip]:[port]/corvus/httpd/ebms/receiver_list".	
	private URL receiverListWSURL;
	
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
	
	// The maximum number of ready d/l messages retrieved.  
	private int numOfMessages;

	/** 
	 * Explicit Constructor.
	 * 
	 * @param receiverListWSURL
	 * 			The "receiverList" web services URL. It should be "http://[ip]:[port]/corvus/httpd/ebms/receiver_list".
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
	 * @param numOfMessages
	 * 			The maximum number of ready d/l messages retrieved.  
	 * 
	 * @throws MalformedURLException
	 * 			When <code>receiverListWSURL</code> is an invalid URL.
	 */
	public EbmsReceiverList(String receiverListWSURL, String cpaId,
		String service, String action, String conversationId,
		String fromPartyId, String fromPartyType, String toPartyId,
		String toPartyType, int numOfMessages) throws MalformedURLException 
	{
		this.receiverListWSURL = new URL(receiverListWSURL);
		this.cpaId = cpaId;
		this.service = service;
		this.action = action;
		this.conversationId = conversationId;
		this.fromPartyId = fromPartyId;
		this.fromPartyType = fromPartyType;
		this.toPartyId = toPartyId;
		this.toPartyType = toPartyType;
		this.numOfMessages = numOfMessages;
	}	
	
	/**
	 * Send the web service request to Hermes2 requesting for querying  
	 * all message id of the EbMS message(s) which are ready to download from Hermes2.
	 * 
	 * @return 	An iterator which contains all message id of the message(s) which 
	 * 			are ready to download.
	 * 			 			
	 * @throws Exception 			
	 */
	public Iterator getReceivedMessagesIds() throws Exception {
		// Make a SOAP Connection and SOAP Message.		
		SOAPConnection soapConn = SOAPConnectionFactory.newInstance().createConnection();
		SOAPMessage request = MessageFactory.newInstance().createMessage();
		
		// Populate the SOAP Body
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
		 * <numOfMessages> 100 </numOfMessages>  
		 */
		SOAPBody soapBody = request.getSOAPPart().getEnvelope().getBody();
		soapBody.addChildElement(createElement("cpaId", nsPrefix, nsURI, cpaId));
		soapBody.addChildElement(createElement("service", nsPrefix, nsURI, service));
		soapBody.addChildElement(createElement("action", nsPrefix, nsURI, action));
		soapBody.addChildElement(createElement("convId", nsPrefix, nsURI, conversationId));
		soapBody.addChildElement(createElement("fromPartyId", nsPrefix, nsURI, fromPartyId));
		soapBody.addChildElement(createElement("fromPartyType", nsPrefix, nsURI, fromPartyType));
		soapBody.addChildElement(createElement("toPartyId", nsPrefix, nsURI, toPartyId));
		soapBody.addChildElement(createElement("toPartyType", nsPrefix, nsURI, toPartyType));
		soapBody.addChildElement(createElement("numOfMessages", nsPrefix, nsURI, numOfMessages + ""));
		 
		// Send the request to Hermes and return the set of message id that are ready to d/l.
		SOAPMessage response = soapConn.call(request, receiverListWSURL);
		SOAPBody responseBody = response.getSOAPBody();
				
		/*
		 * The response is something like:
		 * <soap-body>
		 * 	<messageIds>
		 * 		<messageId> .. </messageId>
		 * 		<messageId>	.. </messageId>
		 * 			..
		 * 			..
		 * 	</messageIds>
		 * </soap-body>	 
		 */ 
		if (!responseBody.hasFault()){
			SOAPElement messageIdsElement = getFirstChild(responseBody, "messageIds", nsURI);
			Iterator messageIdElementIter = getChildren(messageIdsElement, "messageId", nsURI);
			
			ArrayList messageIdsList = new ArrayList();
			while(messageIdElementIter.hasNext()) {
				SOAPElement messageIdElement = (SOAPElement)messageIdElementIter.next();
				messageIdsList.add(messageIdElement.getValue());
			}
			return messageIdsList.iterator();
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
	private SOAPElement getFirstChild(SOAPElement soapElement, String childLocalName, String childNsURI) 
		throws SOAPException 
	{
		Name childName = SOAPFactory.newInstance().createName(childLocalName, null, childNsURI);
		Iterator childIter = soapElement.getChildElements(childName);
		if (childIter.hasNext())
			return (SOAPElement)childIter.next();	
		return null;
	}
	
	/**
	 * Get the iterator of children with <code>childLocalName</code> and <code>childNsURI</code>
	 * at the specified <code>soapElement</code>.  
	 * 
	 * @param soapElement
	 * 			The parent element you want to search from.
	 * @param childLocalName
	 * 			The elements' tag name you want to search.
	 * @param childNsURI
	 * 			The element's namespace uri you want to search. 
	 * @return
	 * @throws SOAPException unable to get the children.
	 */
	private Iterator getChildren(SOAPElement soapElement, String childLocalName, String childNsURI) 
		throws SOAPException 
	{
		Name childrenName = SOAPFactory.newInstance().createName(childLocalName, null, childNsURI);
		Iterator childrenIter = soapElement.getChildElements(childrenName);
		return childrenIter;
	}
}
