package hk.hku.cecid.corvus.ws;

import java.util.Date;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import hk.hku.cecid.piazza.commons.util.PropertyTree;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.MessageStatusRequestData;
import hk.hku.cecid.corvus.ws.data.MessageStatusResponseData;
import hk.hku.cecid.corvus.ws.data.DataFactory;

/**
 * The <code>MessageStatusSender</code> is the SOAP Message client that query 
 * the message status record through the<em>Message status engine (MS-E)</em>.
 * <br/><br/>
 * 
 * From the description in (MS-E), a series of parameters have been defined 
 * and required to send to the MS-E.<br/><br/> 
 * <ol>
 * 	<li>partnershipId</li>
 * 	<li>channelType (OUTGOING | INCOMING)</li>
 * 	<li>channelId</li>
 * 	<li>folderName</li>
 * 	<li>fileName</li>
 * 	<li>fromTimestamp (in UTC format)</li>
 * 	<li>toTimestamp (in UTC format) </li>
 * 	<li>numOfRecords</li>
 * 	<li>conversationId</li>
 * 	<li>messageId</li>
 * 	<li>messageType</li>
 * 	<li>messageStatus</li>
 * 	<li>protocol</li>
 * 	<li>locale (EN)</li>
 * 	<li>levelOfDetails (0 | 1)</li>
 * 	<li>offset</li>
 * </ol><br/><br/>
 * 
 * Creation Date: 12/03/2007
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	Dwarf 10315	
 */
public class MessageStatusSender extends SOAPSender {
	
	private final String NS_URI = "urn:CECID-Elf-Schema:MessageStatus";
	
	/**
	 * The complex type object for the message status request.
	 */
	public static final String MSE_REQUEST_TYPE  = "statusRequest";
	
	/**
	 * The complex type object for the message response request.
	 */
	public static final String MSE_RESPONSE_TYPE = "messageInfo";
	
	/**
	 * The response data.
	 */
	private ArrayList msResponse = null; 

	/**
	 * Explicit Constructor. 
	 * 
	 * @param l			The logger used for log message and exception.
	 * @param m			The message status properties including the 
	 * 					querying parameter.
	 */
	public MessageStatusSender(FileLogger l, MessageStatusRequestData m){
		super(l,m, m.getWSEndpoint());
		this.setLoopTimes(1);		
	}	
	
	/**
	 * Initialize the SOAP Message.
	 */
	public void onStart(){
		super.onStart();
		if (this.log != null){
			// Log all information for this sender.
			this.log.log("Message Status Client init at " + new Date().toString());
			this.log.log("----------------------------------------------------");
			if (this.properties != null)
				this.log.log(this.properties.toString());
		}		
		try{
			this.initializeMessage();
			this.setRequestDirty(false);
		}catch(Exception e){
			if (this.log != null)
				this.log.log("Unable to initialize the SOAP Message");
			this.onError(e);
		}
	}
	
	/**
	 * Initialize the message using the properties in the <code>MessageStatusRequestData</code>.
	 * 
	 * @throws ClassCastException the properties set is not a message status request data. 
	 */
	public void initializeMessage() throws Exception {
		
		if (!(this.properties instanceof MessageStatusRequestData))
			throw new ClassCastException("Invalid Message status class data");
										 
		MessageStatusRequestData d = (MessageStatusRequestData) this.properties;
		
		// Message status request requires XML decl and basic-auth.
		this.setRequireXMLDeclaraction(true);				
		this.setBasicAuthentication(d.getUsername(), d.getPassword());
		
		Map props    = d.getProperties();
		int len      = MessageStatusRequestData.PARAM_KEY_SET.length;
		String key 	 = null;
		String value = null;
		Object obj   = null;		
		
		// Add <statusRequest> Parent.
		this.addRequestElementText(MSE_REQUEST_TYPE, "", "", NS_URI);
		// Iterate all key available in message status request data and link to the parent.
		for (int i = 0; i < len; i++){
			key   = MessageStatusRequestData.PARAM_KEY_SET[i];
			obj   = props.get(key);
			value = (obj != null && !obj.equals("")) ? obj.toString() : null;
			// Add it to SOAP message and link to parent.
			if (value != null)
				this.addRequestElementText(MSE_REQUEST_TYPE, NS_URI,	key, value, null, null);
		}					
	}
		
	/**
	 * [@EVENT] Retrieve the message status record from the message soap message.
	 * <br><br/> 
	 * 
	 * It parse the response and transform each <code>MessageInfo</code> complex 
	 * type into one <code>MessageStatusResponseData</code>.
	 * 
	 * @throws Exception
	 */
	public void onResponse() throws Exception {		
		// Declare variable				
		//String nsURI = ((MessageStatusRequestData) this.properties).getNsURI();
		int len		 = this.countResponseElementText(MSE_RESPONSE_TYPE, NS_URI);
		int keylen 	 = MessageStatusResponseData.PARAM_KEY_SET.length;
		// Initalizae the 2D array.
		String [][] value = new String[keylen][];
				
		for (int i = 0; i < keylen; i++){
			value[i] = this.getResponseElementAsList(
				MessageStatusResponseData.PARAM_KEY_SET[i],
				NS_URI);			
		}				
		// Extract the information.
		// TODO: refactoring on data binding.
		ArrayList al = new ArrayList(len);		
		MessageStatusResponseData d = null;
		for (int i = 0; i < len; i++){
			d = new MessageStatusResponseData();
			Map hm = d.getProperties();
			for (int j = 0; j < keylen; j++){
				if (value[j] != null && value[j].length > i && value[j][i] != null){
					hm.put(MessageStatusResponseData.PARAM_KEY_SET[j], value[j][i]);
				}
			}
			d.setProperties((HashMap)hm);
			al.add(d);	
			this.log.log(d.toString());	
		}		
		this.msResponse = al;
	}
	
	/**
	 * [@GET] Get the list of message status record. It should be 
	 * called only after the invocation of {@link #onResponse()}.
	 * 
	 * @return 	a list of message status record using the filter from the
	 * 			last web services call query. 
	 */
	public ArrayList getMessageStatusList(){
		return this.msResponse;
	}
	
	/**
	 * The main method is for CLI mode.
	 */
	public static void main(String [] args){
		try{
			if (args.length < 2){
				System.out.println("Usage: message-status [config-xml] [log-path]");
				System.out.println();
				System.out.println("Example: message-status ./config/message-status/ms-request.xml ./logs/message-status.log");
				System.exit(1);
			}					
			System.out.println("----------------------------------------------------");
			System.out.println("        Message Status Web service query            ");
			System.out.println("----------------------------------------------------");
			
			// Initalize the logger.			
			System.out.println("Initialize Logger ... ");			
			FileLogger logger 	   		 = new FileLogger(new java.io.File(args[1]));
						
			// Initialize the query parameter.
			System.out.println("Importing  query parameters ... ");			
			MessageStatusRequestData msd = 
				DataFactory.createMessageRequestStatusDataFromXML(
					new PropertyTree(
						new java.io.File(args[0]).toURI().toURL()));						
			
			// Send the query.
			System.out.println("Sending    message status query ... ");
			MessageStatusSender sender  = new MessageStatusSender(logger, msd);
			sender.run();			
						
			System.out.println();
			System.out.println("                   Query Done:                      ");
			System.out.println("----------------------------------------------------");
			System.out.println("Number of records found: " + sender.getMessageStatusList().size());
			System.out.println();
			System.out.println("Please view log for detail .. ");
			
		}catch(Exception e){
			e.printStackTrace(System.err);
		}
	}	
}
