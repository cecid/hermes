package hk.hku.cecid.corvus.ws;

import java.util.Date;
import java.util.Map;

import hk.hku.cecid.piazza.commons.util.PropertyTree;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.Data;
import hk.hku.cecid.corvus.ws.data.DataFactory;
import hk.hku.cecid.corvus.ws.data.AS2StatusQueryData;
import hk.hku.cecid.corvus.ws.data.AS2StatusQueryResponseData;

/** 
 * The <code>AS2StatusQuerySender</code> is a client sender sending SOAP web
 * services request to Corvus <code>AS2</code> plugin for query the status 
 * of particular message. 
 * 
 * The web service parameters are defined in the below:
 * <PRE>
 * &lt;messageId&gt; 20070418-124233-75006@147.8.177.42 &lt;/messageId&gt;  
 * </PRE>   
 *
 * Creation Date: 02/05/2007
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	Dwarf 10329
 */
public class AS2StatusQuerySender extends SOAPSender {

	private final String NS_URI = AS2MessageSender.NS_URI;
	
	/*
	 * The AS2 status response data by the last AS2 status query SOAP Call. 
	 */
	private AS2StatusQueryResponseData lastResponseData = null;
	
	/**
	 * Explicit Constructor.
	 * 
	 * @param l		The logger used for log message and exception.
	 * @param data	The AS2 Status query parameter.
	 */
	public AS2StatusQuerySender(FileLogger l, AS2StatusQueryData data)
	{	
		super(l, (Data)data, data.getSendEndpoint());
		this.setLoopTimes(1);		
	}	
	
	/**
	 * [@EVENT] The method <code>onStart</code> log all new configuration. 
	 */	
	public void onStart(){
		if (!(this.properties instanceof AS2StatusQueryData))
			return;
		
		AS2StatusQueryData data = (AS2StatusQueryData) this.properties;		
		if (this.log != null)
		{			
			// Log all information for this sender.
			this.log.log("AS2 Status query Client init at " + new Date().toString());
			this.log.log("");
			this.log.log("Sending AS2 Status Query SOAP Message with following configuration");				
			this.log.log("-------------------------------------------------------------------");
			if (data != null)
				this.log.log(data.toString());
			this.log.log("-------------------------------------------------------------------");
			this.log.log("");						
		}
		// Initial the message.
		try{
			this.initializeMessage();
		}catch(Exception e){
			if (this.log != null)
				this.log.log("Unable to initialize the SOAP Message");
			this.onError(e);
		}		
	}
	
	/**
	 * The SOAPRequest in the creation stage should be liked this.	
	 * 
	 * @throws Exceptions
	 */
	public void initializeMessage() throws Exception
	{	
		if (!(this.properties instanceof AS2StatusQueryData))
			return;
		
		AS2StatusQueryData data = (AS2StatusQueryData) this.properties;
		Map map 	       = data.getProperties();
		int len			   = AS2StatusQueryData.PARAM_KEY_SET.length;
		// All key are conformed to the WSDL schema. so use 
		// KV-iteration is ok.
		for (int i = 0; i < len; i++){
			String key = AS2StatusQueryData.PARAM_KEY_SET[i];
			String value = (String) map.get(key);
			this.addRequestElementText(key, value, "", NS_URI);
		}	
	}
	
	/**
	 * Get the SOAP Body and analyze the result of configuration.<p>
	 * The result of SOAP body: 
	 * <PRE>
	 * 		&lt;status&gt; <em>The current status of message</em> &lt;/status&gt;
	 *  	&lt;statusDescription&gt; <em>The current status description of message</em> &lt;/statusDescription&gt;
	 *  	&lt;ackMessageId&gt; <em>The message id of acknowledgment / receipt if any</em> &lt;/ackMessageId&gt;
	 *  	&lt;ackStatus&gt; <em>The status of acknowledgment / receipt if any</em> &lt;/ackStatus&gt;
	 *  	&lt;ackStatusDescription&gt; <em>The status description of acknowledgment / receipt if any</em> &lt;/ackStatusDescription&gt;  
	 * </PRE>  
	 */	
	public void onResponse() throws Exception{
		AS2StatusQueryData data = (AS2StatusQueryData) this.properties;
		// Create response object.
		lastResponseData = new AS2StatusQueryResponseData();
		Map hm = lastResponseData.getProperties();
						
		String [] keySet = AS2StatusQueryResponseData.PARAM_KEY_SET;
		int len = keySet.length;		 
		 
		hm.put(keySet[0], data.getQueryMessageId());
		// Extract the value based on the key-set.
		for (int i = 1; i < len; i++){
			hm.put(keySet[i], this.getResponseElementText(keySet[i], NS_URI, 0 ));
		}
		lastResponseData.setProperties(hm);		
	}
	
	/** 
	 * @return The AS2 status response data by the last SFRM status query SOAP Call.  
	 */
	public AS2StatusQueryResponseData getLastResponseData(){
		return lastResponseData;
	}
	
	/**
	 * The main method is for CLI mode.
	 */
	public static void main(String [] args){
		try{
			if (args.length < 2){
				System.out.println("Usage: as2-status [config-xml] [log-path]");
				System.out.println();
				System.out.println("Example: as2-status ./config/as2-status/as2-request.xml ./logs/as2-status.log");
				System.exit(1);
			}					
			System.out.println("----------------------------------------------------");
			System.out.println("             AS2 Status Queryer start             ");
			System.out.println("----------------------------------------------------");

			// Initialize the logger.
			System.out.println("Initialize logger .. ");
			FileLogger logger = new FileLogger(new java.io.File(args[1]));			
			
			// Initialize the query parameter.
			System.out.println("Importing  AS2 sending parameters ... ");	
			AS2StatusQueryData sqd = DataFactory.getInstance()
				.createAS2StatusQueryDataFromXML(
					new PropertyTree(
						new java.io.File(args[0]).toURI().toURL()));
			
			// Initialize the sender.
			System.out.println("Initialize AS2 status queryer ... "); 
			AS2StatusQuerySender sender = new AS2StatusQuerySender(logger, sqd);
			
			System.out.println("Sending    AS2-status sending request ... ");
			sender.run();			
						
			System.out.println();
			System.out.println("                    Sending Done:                   ");
			System.out.println("----------------------------------------------------");
			
			// Print response data.
			AS2StatusQueryResponseData response = sender.getLastResponseData();
			System.out.println("Query Message ID          : " + response.getMessageId());
			System.out.println("Query Message Status      : " + response.getStatus());
			System.out.println("Query Message Status Desc : " + response.getStatusDescription());
			System.out.println("ACK   Message ID          : " + response.getACKMessageId());
			System.out.println("ACK   Message Status      : " + response.getACKStatus());
			System.out.println("ACK   Message Status Desc : " + response.getACKStatusDescription());
			
			System.out.println();
			System.out.println("Please view log for details .. ");
			
		}catch(Exception e){
			e.printStackTrace(System.err);
		}		
	}	
}
