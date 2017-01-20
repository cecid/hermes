package hk.hku.cecid.corvus.ws;

import java.util.Date;
import java.util.Map;

import hk.hku.cecid.piazza.commons.util.PropertyTree;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.Data;
import hk.hku.cecid.corvus.ws.data.DataFactory;
import hk.hku.cecid.corvus.ws.data.SFRMStatusQueryData;
import hk.hku.cecid.corvus.ws.data.SFRMStatusQueryResponseData;

/**
 * The <code>SFRMStatusQuerySender</code> is a client sender sending SOAP web
 * services request to Corvus <code>SFRM</code> plugin for query the status 
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
public class SFRMStatusQuerySender extends SOAPSender {
	
	private final String NS_URI = "http://localhost:8080/corvus/httpd/sfrm/status";
	
	/*
	 * The SFRM status response data by the last SFRM status query SOAP Call. 
	 */
	private SFRMStatusQueryResponseData lastResponseData = null;
	
	/**
	 * Explicit Constructor.
	 * 
	 * @param l		The logger used for log message and exception.
	 * @param data	The SFRM Status query parameter.
	 */
	public SFRMStatusQuerySender(FileLogger l, SFRMStatusQueryData data)
	{	
		super(l, (Data)data, data.getSendEndpoint());
		this.setLoopTimes(1);		
	}	
	
	/**
	 * [@EVENT] The method <code>onStart</code> log all new configuration. 
	 */	
	public void onStart(){
		if (!(this.properties instanceof SFRMStatusQueryData))
			return;
		
		SFRMStatusQueryData data = (SFRMStatusQueryData) this.properties;		
		if (this.log != null)
		{			
			// Log all information for this sender.
			this.log.log("SFRM Status query Client init at " + new Date().toString());
			this.log.log("");
			this.log.log("Sending SFRM Status Query SOAP Message with following configuration");				
			this.log.log("-------------------------------------------------------------------");
			if (data != null)
				this.log.log(data.toString());
			this.log.log("-------------------------------------------------------------------");
			this.log.log("");						
		}
		// Initial the message.
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
	 * The SOAPRequest in the creation stage should be liked this.	
	 * 
	 * @throws Exceptions
	 */
	public void initializeMessage() throws Exception
	{	
		if (!(this.properties instanceof SFRMStatusQueryData))
			return;
		
		SFRMStatusQueryData data = (SFRMStatusQueryData) this.properties;
		Map map 	       = data.getProperties();
		int len			   = SFRMStatusQueryData.PARAM_KEY_SET.length;
		// All key are conformed to the WSDL schema. so use 
		// KV-iteration is ok.
		for (int i = 0; i < len; i++){
			String key = SFRMStatusQueryData.PARAM_KEY_SET[i];
			String value = (String) map.get(key);
			this.addRequestElementText(key, value, "", NS_URI);
		}	
	}
	
	/**
	 * Get the SOAP Body and analyze the result of configuration.<p>
	 * The result of SOAP body: 
	 * <pre>
	 *  	&lt;messageInfo&gt;
	 * 	 		&lt;status&gt; <em>The current status of message</em> &lt;/status&gt;
	 *  		&lt;statusDescription&gt; <em>The current status description of message</em> &lt;/statusDescription&gt;
	 *  		&lt;numberOfSegments&gt; <em>Maximum number of segments</em> &lt;/numberOfSegments&gt;
	 *  		&lt;numberOfProcessedSegments&gt; <em>Number of processed segments</em> &lt;/numberOfProcessedSegments&gt;
	 *  		&lt;lastUpdatedTime&gt; <em> The last updated timestamp </em> &lt;/lastUpdatedTime&gt;
	 *  	&lt;/messageInfo&gt;
	 *  </pre>
	 */	
	public void onResponse() throws Exception{
		SFRMStatusQueryData data = (SFRMStatusQueryData) this.properties;
		// Create response object.
		lastResponseData = new SFRMStatusQueryResponseData();
		Map hm = lastResponseData.getProperties();
						
		String [] keySet = SFRMStatusQueryResponseData.PARAM_KEY_SET;
		int len = keySet.length;		 
		 
		hm.put(keySet[0], data.getQueryMessageId());
		// Extract the value based on the key-set.
		for (int i = 1; i < len; i++){
			hm.put(keySet[i], this.getResponseElementText(keySet[i], NS_URI, 0 ));
		}
		lastResponseData.setProperties(hm);		
	}
	
	/** 
	 * @return The SFRM status response data by the last SFRM status query SOAP Call.  
	 */
	public SFRMStatusQueryResponseData getLastResponseData(){
		return lastResponseData;
	}
	
	/**
	 * The main method is for CLI mode.
	 */
	public static void main(String [] args){
		try{
			if (args.length < 2){
				System.out.println("Usage: sfrm-status [config-xml] [log-path]");
				System.out.println();
				System.out.println("Example: sfrm-status ./config/sfrm-status/sfrm-request.xml ./logs/sfrm-status.log");
				System.exit(1);
			}					
			System.out.println("----------------------------------------------------");
			System.out.println("            SFRM sender start             ");
			System.out.println("----------------------------------------------------");

			// Initalize the logger.
			System.out.println("Initialize logger .. ");
			FileLogger logger = new FileLogger(new java.io.File(args[1]));			
			
			// Initialize the query parameter.
			System.out.println("Importing  SFRM sending parameters ... ");	
			SFRMStatusQueryData sqd = DataFactory.getInstance()
				.createSFRMStatusQueryDataFromXML(
					new PropertyTree(
						new java.io.File(args[0]).toURI().toURL()));
			
			// Initalize the sender.
			System.out.println("Initialize SFRM-status web service client... "); 
			SFRMStatusQuerySender sender = new SFRMStatusQuerySender(logger, sqd);
			
			System.out.println("Sending    SFRM-status sending request ... ");
			sender.run();			
						
			System.out.println();
			System.out.println("                    Sending Done:                   ");
			System.out.println("----------------------------------------------------");
			
			// Print respone data.
			SFRMStatusQueryResponseData response = sender.getLastResponseData();
			System.out.println("Query Message ID            : " + response.getMessageId());
			System.out.println("Query Message Status        : " + response.getStatus());
			System.out.println("Query Message Status Desc   : " + response.getStatusDescription());
			System.out.println("Maximum nummber of segments : " + response.getNumberOfSegments());
			System.out.println("Number of processed segments: " + response.getNumberOfProcessedSegments());
			System.out.println("Last activities timestamp   : " + response.getLastUpdatedTimestamp());
			
			System.out.println();
			System.out.println("Please view log for details .. ");
			
		}catch(Exception e){
			e.printStackTrace(System.err);
		}		
	}	
}
