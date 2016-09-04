/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.ws;

import java.util.Date;
import java.util.Map;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.Data;
import hk.hku.cecid.corvus.ws.data.DataFactory;
import hk.hku.cecid.corvus.ws.data.AS2ConfigData;

import hk.hku.cecid.piazza.commons.util.PropertyTree;

/**
 * The <code>AS2ConfigSender</code> is a client sender sending SOAP web
 * services request to B2BCollector <code>AS2</code> plugin for configurating
 * the performance factor.
 * 
 * The web service parameters are defined in the below:
 * 
 * <pre>
 *   &lt;active-module-status&gt; true | false &lt;/active-module-status&gt;
 *   &lt;inmessage-interval&gt;15000&lt;/inmessage-interval&gt;
 *   &lt;inmessage-maxthread&gt;0&lt;/inmessage-maxthread&gt;
 *   &lt;outmessage-interval&gt;15000&lt;/outmessage-interval&gt;
 *   &lt;outmessage-maxthread&gt;0&lt;/outmessage-maxthread&gt;
 *   &lt;outpayload-interval&gt;15000&lt;/outpayload-interval&gt;
 *   &lt;outpayload-maxthread&gt;0&lt;/outpayload-maxthread&gt; 
 * </pre>
 * 
 * @author Twinsen Tsang
 * @version 1.0.2
 * @since	Elf 0818
 */
public class AS2ConfigSender extends SOAPSender
{			
	/**
	 * The result status for the last successful web services call. 
	 */
	private String lastSuccessfulConfigStatus;
	
	/**
	 * Explicit Constructor.
	 * 
	 * @param l		The logger used for log message and exception.
	 * @param data	The AS2 Configuration parameters.
	 */
	public AS2ConfigSender(FileLogger l, AS2ConfigData data)
	{	
		super(l, (Data)data, data.getSendEndpoint());
		this.setLoopTimes(1);		
	}	
	
	/**
	 * [@EVENT] The method <code>onStart</code> log all new configuration. 
	 */	
	public void onStart(){
		if (!(this.properties instanceof AS2ConfigData))
			return;
		
		AS2ConfigData data = (AS2ConfigData) this.properties;		
		if (this.log != null)
		{			
			// Log all information for this sender.
			this.log.log("AS2 Configurator Client init at " + new Date().toString());
			this.log.log("");
			this.log.log("Sending AS2 Config SOAP Message with following configuration");				
			this.log.log("------------------------------------------------------------------");
			if (data != null)
				this.log.log(data.toString());
			this.log.log("------------------------------------------------------------------");
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
		if (!(this.properties instanceof AS2ConfigData)){
			return;
		}		
		AS2ConfigData data = (AS2ConfigData) this.properties;
		Map map 	       = data.getProperties();
		int len			   = AS2ConfigData.PARAM_KEY_SET.length;
		// All key are conformed to the WSDL schema. so use 
		// KV-iteration is ok.
		for (int i = 0; i < len; i++){
			String key = AS2ConfigData.PARAM_KEY_SET[i];
			String value = (String) map.get(key);
			this.addRequestElementText(key, value, NS_PREFIX, AS2MessageSender.NS_URI);
		}	
	}
	
	/**
	 * Get the SOAP Body and analyze the result of configuration.<p>
	 * The result of SOAP body: 
	 * <pre>
	 *  	&lt;status&gt;success | fail&lt;/status&gt;
	 *  </pre>
	 */	
	public void onResponse() throws Exception{
		AS2ConfigData data = (AS2ConfigData) this.properties;
		// Get the first element with name "status".
		this.lastSuccessfulConfigStatus= 
			this.getResponseElementText("status", AS2MessageSender.NS_URI, 0);		
		if (this.log != null)
			log.log("Configuration Result: " + this.lastSuccessfulConfigStatus);						
	}		
	
	/**
	 * @return Get the result status for the last successful web services call. 
	 */
	public String getStatus(){
		return this.lastSuccessfulConfigStatus;
	}
					
	/**
	 * The main method is for CLI mode.
	 */
	public static void main(String [] args){
		try{
			if (args.length < 2){
				System.out.println("Usage: as2-config [config-xml] [log-path]");
				System.out.println();
				System.out.println("Example: as2-config ./config/as2-config/as2-request.xml l ./logs/as2-config.log");
				System.exit(1);
			}					
			System.out.println("----------------------------------------------------");
			System.out.println("        AS2 Configuration Updater start           ");
			System.out.println("----------------------------------------------------");

			// Initialize the logger.
			System.out.println("Initialize logger .. ");
			FileLogger logger = new FileLogger(new java.io.File(args[1]));			
			
			// Initialize the query parameter.
			System.out.println("Importing  AS2 sending parameters ... ");	
			AS2ConfigData acd = DataFactory.getInstance()
				.createAS2ConfigDataFromXML(
					new PropertyTree(
						new java.io.File(args[0]).toURI().toURL()));
			
			// Initialize the sender.
			System.out.println("Initialize AS2 configuration updater... "); 
			AS2ConfigSender sender = new AS2ConfigSender(logger, acd);
			
			System.out.println("Sending    AS2-config sending request ... ");
			sender.run();			
						
			System.out.println();
			System.out.println("                    Sending Done:                   ");
			System.out.println("----------------------------------------------------");
			System.out.println("The result of query: " + sender.getStatus());
			System.out.println();
			System.out.println("Please view log for details .. ");
			
		}catch(Exception e){
			e.printStackTrace(System.err);
		}		
	}
}
