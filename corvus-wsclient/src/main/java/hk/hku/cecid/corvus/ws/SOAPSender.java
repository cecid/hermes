/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.ws;

import java.util.List;

import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.Authenticator;
import java.net.MalformedURLException;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.MessageFactory;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.util.SOAPUtilities;
import hk.hku.cecid.corvus.ws.data.Data;
import hk.hku.cecid.piazza.commons.module.Component;
import hk.hku.cecid.piazza.commons.util.UtilitiesException;
import hk.hku.cecid.piazza.commons.util.PropertyTree;

/**
 * The <code>SOAPSender</code> is a abstract class for the <em>SOAP</em>-Based 
 * protocol client. It reduces the complexity and version incompatible induced 
 * from the apache AXIS by using only Built-in java SOAP XML Package.<br/><br/>
 * 
 * The target developer is for those whose are not familiar with the complex
 * AXIS framework.<br/><br/> 
 * 
 * It is implemented using the event-driven model so that developer 
 * are only required to implement several event method.<br/><br/>
 * 
 * The package is under development and will be standardized in the future 
 * release.<br/><br/>  
 * 
 * @author Twinsen Tsang
 * @version 1.0.3
 * @since	0.9.0
 */
public abstract class SOAPSender extends Component implements Runnable {

	/**
	 * The namespace prefix 
	 */
	protected static final String NS_PREFIX = "tns";		
	
	/**
	 * The logger used for log message and exception
	 * 
	 * @see hk.hku.cecid.corvus.util.FileLogger
	 */
	protected FileLogger log;
	
	/**
	 * The data properties for this sender.
	 */
	protected Data properties;
	
	/**
	 * The SOAP request 
	 */
	protected SOAPMessage request = null;
	
	/**
	 * The SOAP response
	 */
	protected SOAPMessage response = null;
	
	/**
	 * The url of service end point.
	 */
	protected URL serviceEndPoint = null;
		
	/**
	 * The flag indicate whether requires XML declaration at the top.
	 */
	private boolean isRequireXMLDecl = false;
	
	/**
	 * The dirty flag for the SOAP request.
	 */
	private boolean isRequestDirty = true;
	
	/**
	 * Number of times to sent to Hermes2. 
	 */
	private int loopTimes = 1;
	
	/**
	 * The current looping times.
	 */
	private int curTimes  = 0;
	
	/**
	 * THe custom user object for the callback. 
	 */
	private Object userObj = null;	
			
	/**
	 * Inner class for web-services authentication.  
	 */
	private class SOAPAuthenticator extends Authenticator {
		
		/**
		 * The password authentication pair.
		 */
		private PasswordAuthentication pwAuth;
		
		/** 
		 * Constructor.
		 * 
		 * @param username
		 * 			The username for authentication.
		 * @param password
		 * 			The password for authentication.
		 */
		protected SOAPAuthenticator(String username, String password){
			pwAuth = new PasswordAuthentication(username, password.toCharArray()); 
		}
				
		/**
		 * Get the authentication pair.
		 */
		protected PasswordAuthentication getPasswordAuthentication() {
			return pwAuth;
		}		
	}
	
	/**
	 * SPA Constructor.<br<br>
	 * 
	 * It is used when the SOAP Sender is a component in the spa.
	 */
	public SOAPSender(){}
	
	/**
	 * Constructor
	 * 
	 * @param l			The logger used for log message and exception.
	 */
	public SOAPSender(FileLogger l, Data d){
		this.log 		= l;		
		this.properties	= d;
		try{
			this.request = MessageFactory.newInstance().createMessage();
		}catch(SOAPException se){
			this.onError(se);
		}
	}
	
	/**
	 * Constructor
	 * 
	 * @param l			The logger used for log message and exception.
	 * @param endpoint	The url of service end point.
	 */	
	public SOAPSender(FileLogger l, Data d, String endpoint){
		this(l,d);
		try{
			this.serviceEndPoint = new URL(endpoint);
		}catch(MalformedURLException mue){
			this.onError(mue);
		}
	}	
	
	/**
	 * Constructor.
	 * 
	 * @param l			The logger used for log message and exception.
	 * @param endpoint	The url of service end point.
	 */	
	public SOAPSender(FileLogger l, Data d, URL endpoint){
		this(l,d);
		this.serviceEndPoint = endpoint;
	}
	
	/**
	 * Implements this method if you want to send messages without
	 * much different between other message to sent.   
	 */
	public void initializeMessage() throws Exception{
		// Implement by sub-class.
	}
	
	/** 
	 * [@EVENT] This method is invoked when the sender begins to execute the 
	 * run method.<br/>
	 *  
	 */
	public void onStart(){
		this.curTimes = 0;
	}
		
	/**
	 * [@EVENT] This method is invoked when each loop iteration 
	 * start.<br/>
	 * 
	 * @throws Exception Any type of exception will be processed at onError(throwable t).
	 */	
	public void onEachLoopStart() throws Exception{		
		// Implement by-sub-class
	}
	
	/** 
	 * [@EVENT] This method is invoked when the sender is required to 
	 * sent a SOAP Request for configuration.<br/><br/>
	 * 
	 * The default return value is the request in the sender.<br/>
	 * <br/>
	 * <strong>
	 * If developer want to send a custom SOAP request other than the sender
	 * SOAP request, override the function and return your
	 * customizing SOAP Request.
	 * </strong><br/>
	 * <br/>
	 * For example, if you want to send a SOAP request always with
	 * soap element "test". Then you should override this function
	 * called. 
	 * <br/>
	 * <PRE>
	 * public SOAPMessage onCreateRequest() throws Exception{
	 *     SOAPMessage request = MessageFactory.newInstance().createMessage();
	 *              ..
	 *              ..
	 *            add the element "test".
	 *     return request;
	 * }
	 * </PRE>   				 
	 * <br/>
	 * @return javax.xml.SOAPMessage
	 *
	 * @throws Exception Any type of exception will be processed at onError(throwable t). 
	 */
	public SOAPMessage onCreateRequest() throws Exception{
		// Reinitialize the message is the request is dirty.
		if (this.isRequestDirty()){
			this.resetSOAPRequest();
			this.initializeMessage();
		}
		return this.request;
	}
	
	/**
	 * [@EVENT] This method is invoked just before sending the request to
	 * Web service endpoints.
	 * 
	 * @param conn 		The SOAP Connection used for sending SOAP request.
	 * @param request	The request created by {@link #onCreateRequest()}. 
	 * 
	 * @throws Exception Any type of exception will be processed at onError(throwable t). 
	 */
	public void onBeforeRequest(SOAPConnection conn, SOAPMessage request) throws Exception{	
		// Implement by sub-class
	}
	
	/**
	 * [@EVENT] This method is invoked when received the reply SOAP 
	 * response from the server
	 * 
	 * Developer can use {@link #getSOAPResponse()} to get
	 * the SOAP response for self-handling.
	 * 
	 * Otherwise, developer can use {@link #getResponseElementText(String, String, int)}
	 * to get the response element text from the response object.
	 * 
	 * @throws Exception Any type of exception will be processed at onError(throwable t).
	 */
	public void onResponse() throws Exception{		
		// Implement by sub-class
	}		
	
	/**
	 * [@EVENT] This method is invoked when the sending execution is ended. 
	 */
	public void onEnd(){	
		// Implement by sub-class
	}
	
	/**
	 * [@EVENT] This method is invoked when there is any exception thrown
	 * during web service call.
	 */
	public void onError(Throwable t){		
		t.printStackTrace();
		if (this.log != null){
			this.log.logStackTrace(t);
		}
	}
	
	/**
	 * Set how many times should the sender to be send.
	 * 
	 * @param loopTimes		the new loopTimes.
	 */
	public void setLoopTimes(int loopTimes){
		if (loopTimes > 0){
			this.loopTimes = loopTimes;
		}
	}
	
	/**
	 * Set a user object for callback.
	 * 
	 * @param obj 		The user object.
	 */
	public void setUserObject(Object obj){
		this.userObj = obj;
	}
	
	/**
	 * Set the service endpoint.
	 * 
	 * @param endpoint	The URL of the web service endpoint. 
	 */
	public void setServiceEndPoint(URL endpoint){
		if (endpoint != null){
			this.serviceEndPoint = endpoint;	
		}		
	}
	
	/**
	 * Set the service endpoint.
	 * 
	 * @param endpoint	The String of the web service endpoint. 
	 */
	public void setServiceEndPoint(String endpoint){
		try{
			this.serviceEndPoint = new URL(endpoint);			
		}		
		catch(MalformedURLException mue){
			if (this.log != null){
				this.log.logStackTrace(mue);
			}
		}
	}
	
	/**
	 * Set if the request is dirty. We considered "dirty"
	 * as the request has been modified by someone during
	 * last sending.  
	 */
	public void setRequestDirty(boolean dirty){
		this.isRequestDirty = dirty;
	}
	
	/**
	 * Set if the request requires XML declaration at the top 
	 * of the request.<br/>
	 * <br/>
	 * This is equivalent to: <br/> 
	 * <pre>
	 *    SOAPRequest.setProperty(WRITE_XML_DECLARATION, "true");
	 * </pre>
	 * 
	 * @param require
	 * 			true if requires XML declaration.
	 * 
	 * @see javax.xml.soap.SOAPMessage#setProperty(String, Object) 
	 * @see javax.xml.soap.SOAPMessage#WRITE_XML_DECLARATION
	 */
	public void setRequireXMLDeclaraction(boolean require){
		this.isRequireXMLDecl = require;
		this.isRequestDirty = true;
	}
	
	/**
	 * Set to use the basic authentication when calling 
	 * the web service.
	 * 
	 * @param username
	 * 			The username for basic authentication. 
	 * @param password
	 * 			The password for basic authentication. 			
	 */
	public void setBasicAuthentication(final String username, final String password){
		Authenticator.setDefault(new SOAPAuthenticator(username, password));
	}

	/**
	 * Get how many times should the sender to be send.
	 */
	public int getLoopTimes(){
		return this.loopTimes;
	}
	
	/**
	 * Get what is the current loop times for looping 
	 */
	public int getCurrentLoopTimes(){
		return this.curTimes;
	}
	
	/**
	 * Get a user object. 
	 */
	public Object getUserObject(){
		return this.userObj;
	}
	
	/**
	 * Get the service end-point.
	 * 
	 * @return the service endpoint URL.
	 */
	public URL getServiceEndPoint(){
		return this.serviceEndPoint;
	}
	
	/**
	 * return return true if the request requires the XML declaration to sent. 
	 */
	public boolean isRequireXMLDeclaraction(){
		return this.isRequireXMLDecl;
	}
	
	/**
	 * @return return true if the request is dirty.
	 */
	public boolean isRequestDirty(){
		return this.isRequestDirty;
	}
	
	/**	  
	 * Get the SOAP request. 
	 * 
	 * @return The SOAP Request Body.
	 */
	protected SOAPMessage getSOAPRequest(){
		return this.request;
	}
	
	/**
	 * Reset the request to empty SOAP Body.<br>
	 * 
	 * It is commonly used when for each loop times the request
	 * 
	 * @throws SOAPException 
	 */
	protected void resetSOAPRequest() throws SOAPException{
		this.request = MessageFactory.newInstance().createMessage();
	}
	
	/**
	 * Get the SOAP response.<br>
	 * 
	 * The method should only be called inside {@link #onResponse()}.<br>
	 * 
	 * @return The SOAP Response Body.
	 */
	protected SOAPMessage getSOAPResponse(){
		return this.response;
	}

	/**
	 * Reset the request to empty SOAP Body.<br>
	 * 
	 * It is commonly used when for each loop times the response. 
	 */
	protected void resetSOAPResponse() throws SOAPException{
		this.response = MessageFactory.newInstance().createMessage();
	}
	
	/**
	 * Add SOAP element to body with name and value.
	 * 
	 * @param tagName	The tag name of element to be retrieved.
	 * @param tagValue	The value of the element to be added.
	 * @param nsPrefix	The namespace Prefix
	 * @param nsURI		The namespace URI.
	 * @return			true if the creation and addition is successfully.
	 * @throws SOAPException
	 */
	public boolean addRequestElementText(String tagName
										,String tagValue
										,String nsPrefix
										,String nsURI) throws SOAPException{
		if (this.request == null)
			return false;		
					
		SOAPBody soapBody = request.getSOAPPart().getEnvelope().getBody();
		
		if (soapBody == null)
			return false;
		
		// 	Create new element.
		SOAPElement newElement = SOAPUtilities.createElement(tagName, tagValue, nsPrefix, nsURI);
		soapBody.addChildElement(newElement);
		return true;
	}
	
	/**
	 * Add SOAP element to specify parent element
	 * 
	 * @param parentTagName The tag name of parent element.
	 * @param parentNsURI The namespace URI of parent element.
	 * @param tagName	The tag name of element to be retrieved.
	 * @param tagValue	The value of the element to be added.
	 * @param nsPrefix	The namespace Prefix
	 * @param nsURI		The namespace URI.
	 * @return			true if the creation and linking is successfully.
	 * @throws SOAPException
	 */
	public boolean addRequestElementText(String parentTagName
										,String parentNsURI
										,String tagName
										,String tagValue
										,String nsPrefix
										,String nsURI) throws SOAPException{		
		return 
			this.addRequestElementText(tagName, tagValue, nsPrefix, nsURI) &&		
			SOAPUtilities.linkElements(this.request, parentTagName, parentNsURI, tagName, nsURI);	
	}
	
	/**
	 * This method should only be called inside {@link #onCreateRequest()}.
	 * because the request object will be deleted upon each ws call.
	 * 
	 * @param tagname	The tag name of element to be retrieved.
	 * @param nsURI		The namespace URI.
	 * @param whichOne	The nth child element to be returned.
	 */
	public String getRequestElementText(String tagname
									   ,String nsURI
									   ,int	   whichOne) throws SOAPException{
		SOAPElement elem = SOAPUtilities.getElement(this.request, tagname, nsURI, whichOne);
		if (elem != null)
			return elem.getValue();
		return "";
	}
	
	/**
	 * This method should only be called inside {@link #onResponse()}.
	 * because the response object will be deleted upon each ws call.
	 * 
	 * @param tagname	The tag name of element to be retrieved.
	 * @param nsURI		The namespace URI.
	 * @param whichOne	The nth child element to be returned.	 
	 * 
	 * @return The element text in the tagname specified.
	 */
	public String getResponseElementText(String tagname
										,String nsURI
										,int	whichOne) throws SOAPException{
		SOAPElement elem = SOAPUtilities.getElement(this.response, tagname, nsURI, whichOne);
		if (elem != null)
			return elem.getValue();
		return "";
	}
	
	/**
	 * This methods count number of specified <code>tagname</code> 
	 * in the response. It should only be called inside {@link #onResponse()}.
	 * 
	 * @param tagname	The tag name of element to be retrieved.
	 * @param nsURI		The namespace URI.
	 * @return The element text in the tagname specified.
	 * @throws SOAPException
	 */
	public int countResponseElementText(String tagname
									   ,String nsURI) throws SOAPException{
		return SOAPUtilities.countElement(this.response, tagname, nsURI);
	}
	
	/**
	 * Transform the response into a property tree.
	 * It should only be called inside {@link #onResponse()}.
	 * 
	 * @return An XML Property tree having the same tag content in the response.
	 */
	public PropertyTree transformResponseContent() throws UtilitiesException, SOAPException {
		SOAPBody soapBody = response.getSOAPBody();				
		if (soapBody == null) return null;
		try{
			return new PropertyTree(soapBody);
		}catch(Exception e){
			throw new UtilitiesException("Unable to transform soapBody due to", e);
		}
	}
 
	/**
	 * This method should only be called inside {@link #onResponse()}.
	 * because the response object will be deleted upon each ws call.<br/><br/>
 	 *
 	 * This method get the element by it's <code>tagname</code> and return 
 	 * a list of text value inside.
 	 *
	 * @param tagname
	 * 			The name of the XML tag to be extraceted.
	 * @param nsURI
	 * 			The namespace URI.
	 * @return The elements' text in the tagname specified.
	 * @throws SOAPException
	 */
	public String [] getResponseElementAsList(String tagname
											 ,String nsURI) throws SOAPException{
		List list 		= SOAPUtilities.getElementList(this.response, tagname, nsURI);
		String [] props = new String[list.size()];
		for (int i = 0; i < list.size(); i++){
			if (list.get(i) != null)
				props[i] = ((SOAPElement)list.get(i)).getValue();
		}		
		return props;
	}
											
	/**
	 * The thread execution method. 
	 */
	public void run()
	{
		// Variable declaration.
		SOAPMessage request				= null;
		SOAPConnectionFactory factory 	= null;
				
		// Signals a kick-off event.
		this.onStart();						
		try{				
			for(int i = 0; i < this.getLoopTimes(); i++){				
				this.curTimes = i;			
				// Signals a loop start event.
				this.onEachLoopStart();				
					
				// Asks child class for creating the request.				
				request = this.onCreateRequest();								
								
				factory = SOAPConnectionFactory.newInstance();
				SOAPConnection soapConn  = factory.createConnection();
					 
				this.onBeforeRequest(soapConn, request);
					
				// Save the request if the developers modify the request
				// at somewhere.
				if (this.isRequestDirty()){
					if (this.isRequireXMLDecl)
						this.request.setProperty(SOAPMessage.WRITE_XML_DECLARATION , "true");
					this.request.saveChanges();
					this.setRequestDirty(false);
				}												
				this.response = soapConn.call(request, this.serviceEndPoint);
				
				// Check whether has SOAP fault from server side.
				SOAPBody sb = this.response.getSOAPBody();
				if (sb.hasFault()){
					throw new SOAPException(
						sb.getFault().getFaultCode() 
					  + " " 
					  + sb.getFault().getFaultString()); 
				}
				this.onResponse();
			}					
			this.onEnd();

			// Reset soap request and response.
			this.resetSOAPRequest();
			this.resetSOAPResponse();
		}			
		catch(Exception e){				
			this.onError(e);				
		}									
	}
}
