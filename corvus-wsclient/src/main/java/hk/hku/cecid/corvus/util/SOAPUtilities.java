package hk.hku.cecid.corvus.util;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * The SOAPUtilities acts as a SOAP Helper for some operations
 * that frequently used.<br>
 * <br>
 * Currently supported operations:
 * <ul>
 * 	<li>create a SOAP Element with specified tag name, value, ns prefix and uri
 * 		(Also an attributes set). 
 *  </li>
 *  <li>get a SOAP Element with specified tag name, ns prefix and uri and which nth
 *      tag from a SOAP Message.
 *  </li>
 *  <li>get a list of SOAP Element with specified tag name, ns prefix and uri from
 *  	a SOAP Message.
 *  </li>
 * 	
 * @author Twinsen
 * @version 1.0.0
 */
public class SOAPUtilities {
	
	/**
	 * Validate the existence of child SOAP element given the parent element.  
	 * 
	 * @param message
	 * @param parentTagName
	 * @param parentNsURI
	 * @param childTagName
	 * @param childNsURI
	 * @return true if child element exists
	 * @throws SOAPException
	 */
	public static boolean linkElements(SOAPMessage message, 
									   String parentTagName, 
									   String parentNsURI,
									   String childTagName,
									   String childNsURI) throws SOAPException {
		// SOAP message validation. 
		if (message == null)
			throw new NullPointerException("Missing SOAPMessage.");
		// Get the soap body from the response.
		SOAPBody elementBody = message.getSOAPPart().getEnvelope().getBody();
		
		if (elementBody == null)	// return null if the body is null.
			throw new NullPointerException("Missing SOAPBody in message.");
		
		Node parent, child = null;
		
		// Replace null to empty.
		if (parentNsURI == null)
			parentNsURI = "";
		if (childNsURI == null)
			childNsURI = "";
		
		parent = elementBody.getElementsByTagNameNS(parentNsURI, parentTagName).item(0);
		child  = elementBody.getElementsByTagNameNS(childNsURI, childTagName).item(0);
		
		// Parent and child validation. 
		if (parent == null)
			throw new NullPointerException("Missing parent element: " + parentTagName);
		if (child == null)
			throw new NullPointerException("Missing parent element: " + childTagName);
			
		parent.appendChild(child);
		return true;
	}
		
	 /**
	 * Create a SOAP Element with the specified parameters.<p>
	 * 
	 * @param tagName 		the name of XML tag.
	 * @param tagValue		the value of XML tag. 
	 * @param nsPrefix		the namespace prefix.
	 * @param nsURI			the namespace URL.
	 * 
	 * @return 				the new SOAP element created.
	 */
	public static SOAPElement createElement(String tagName
										   ,String tagValue
										   ,String nsPrefix
										   ,String nsURI) throws SOAPException {
		// 	Create a new SOAP Element according to tagname, prefix, uri
		// and the text value.			
		SOAPElement soapElement = null;
		if (nsURI != null)		
			soapElement = SOAPFactory.newInstance().createElement(tagName, nsPrefix, nsURI);
		else
			soapElement = SOAPFactory.newInstance().createElement(tagName);
		
		if (tagValue == null)
			tagValue = "";
		
		soapElement.addTextNode(tagValue);
		return soapElement;
	}
	
	 /**
	 * Create a SOAP Element with the specified parameters.<p>
	 * 
	 * Also it create the attributes set as the parameter properties.
	 * 
	 * @param tagName 		the name of XML tag.
	 * @param tagValue		the value of XML tag. 
	 * @param nsPrefix		the namespace prefix.
	 * @param nsURI			the namespace URL.
	 * @param attrSet		the attributes set in the element.
	 * 
	 * @return 				the new SOAP element created.
	 */
	public static SOAPElement createElement(String tagName
										   ,String tagValue
										   ,String nsPrefix
										   ,String nsURI
										   ,Hashtable attrSet) throws SOAPException {
		SOAPElement newElem = 
			SOAPUtilities.createElement(tagName, tagValue, nsPrefix, nsURI);
		
		if (newElem == null || attrSet.size() == 0)
			return newElem;		
		
		// Iterate all attributes in the set.
		Enumeration keys = attrSet.keys();
		while (keys.hasMoreElements()){								
			Object key 		= keys.nextElement();
			Object value 	= attrSet.get(key);
			// Create SOAP Name for the tag.
			Name   attrName = SOAPFactory.newInstance().createName(key.toString(), null, null);
			newElem.addAttribute(attrName, value.toString());
		}
		return newElem;
	}
		
	/**
	 * Get a SOAP Element from the SOAPMessage (SOAPbody inside).
	 * 
	 * @param message	The SOAP message to be searched with.
	 * @param tagname	The tag name of element to be retrieved.
	 * @param nsURI		The namespace URI.
	 * @param whichOne	The nth child element to be returned.
	 * 
	 * @return The element in the tagname specified.
	 */
	public static SOAPElement getElement(SOAPMessage message
										,String 	 tagname
										,String 	 nsURI
										,int		 whichOne) throws SOAPException{
		if (message == null)	// return null if the message is null.
			return null;
		// Get the soap body from the response.
		SOAPBody elementBody = message.getSOAPPart().getEnvelope().getBody();
		
		if (elementBody == null)	// return null if the body is null.
			return null;
		
		// Find all the child with the childname.			
		NodeList nl = elementBody.getElementsByTagNameNS(nsURI, tagname);
		if (nl.getLength() <= whichOne){
			return null;
		}
		return (SOAPElement) nl.item(whichOne);	
	}
	
	/**
	 * Get a SOAP Element list from the SOAPMessage (SOAPbody inside). 
	 * 
	 * @param message	The SOAP message to be searched with.
	 * @param tagname	The tag name of element to be retrieved.
	 * @param nsURI		The namespace URI.
	 * @return 			list of SOAP element 
	 * @throws SOAPException
	 */
	public static List getElementList(SOAPMessage message
									 ,String	  tagname
									 ,String	  nsURI) throws SOAPException{
		if (message == null)	// return null if the message is null.
			return null;
		// Get the soap body from the response.
		SOAPBody elementBody = message.getSOAPPart().getEnvelope().getBody();
		
		if (elementBody == null)	// return null if the body is null.
			return null;
				
		NodeList nl 	= elementBody.getElementsByTagNameNS(nsURI, tagname);				
		List	 list	= new Vector();
		
		for (int i = 0; i < nl.getLength(); i++){
			if (nl.item(i) instanceof SOAPElement){
				list.add((SOAPElement)nl.item(i));
			}
		}	
		return list;
	}
	
	/**
	 * Count how many element with <code>tagname</code> and <code>nsURI</code>
	 * is in SOAPMessage <code>message</code>.
	 * 
	 * @param message
	 * 			The SOAP message 
	 * @param tagname
	 * 			The name of the tag to be counted.
	 * @param nsURI
	 * 			The namespace URI of the tag.
	 * @return
	 * 			The number of element in the SOAP message.
	 * @throws SOAPException
	 * 			
	 */
	public static int countElement(SOAPMessage 	message
								  ,String		tagname
								  ,String		nsURI) throws SOAPException{
		if (message == null)	// return null if the message is null.
			throw new NullPointerException("SOAP Message is null.");
		// Get the soap body from the response.
		SOAPBody elementBody = message.getSOAPPart().getEnvelope().getBody();
		
		if (elementBody == null)	// return null if the body is null.
			throw new NullPointerException("SOAP Body is null.");
		
		// Find all the child with the childname.			
		NodeList nl = elementBody.getElementsByTagNameNS(nsURI, tagname);
		return nl.getLength();	
	}
}
