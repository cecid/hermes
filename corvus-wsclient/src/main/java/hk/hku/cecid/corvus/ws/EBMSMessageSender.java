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

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.EBMSMessageData;
import hk.hku.cecid.corvus.ws.data.EBMSPartnershipData;
import hk.hku.cecid.corvus.ws.data.DataFactory;
import hk.hku.cecid.corvus.ws.data.Payload;

import hk.hku.cecid.piazza.commons.util.PropertyTree;

/**
 * The <code>EBMSMessageSender</code> is a client sender sending 
 * SOAP web services request to B2BCollector <code>EbMS</code>
 * plugin for transmission a ebMS message to other parnter.<br/>
 * 
 * The web service parameters are defined in the below:
 * <pre>
 *   &lt;part name="cpaId" type="s:string" />
 *   &lt;part name="service" type="s:string" />
 *   &lt;part name="action" type="s:string" />
 *   &lt;part name="convId" type="s:string" />
 *   &lt;part name="fromPartyId" type="s:string" />
 *   &lt;part name="fromPartyType" type="s:string" />
 *   &lt;part name="toPartyId" type="s:string" />
 *   &lt;part name="toPartyType" type="s:string" />
 *   &lt;part name="refToMessageId" type="s:string" />
 *   &lt;part name="serviceType" type="s:string" />
 * </pre>
 *
 * @author Twinsen Tsang
 * @version 1.0.1a
 * @since	Elf 0818
 * 
 * @see hk.hku.cecid.corvus.ws.data.EBMSMessageData
 * @see hk.hku.cecid.corvus.ws.data.EBMSPartnershipData
 */
public class EBMSMessageSender extends MessageSender {
		
	/**
	 * The Namespace URI 
	 */
	protected static final String NS_URI = "http://service.ebms.edi.cecid.hku.hk/";
	
	
	/**
	 * This is the message id from the SOAP response in the 
	 * last successful web service qeury.
	 */
	private String lastSuccessfulQueryMessageId;
			
	/**
	 * Explicit Constructor.
	 * 
	 * @param l		The logger used for log message and exception.
	 * @param m		The message data for party information and send/recv configuration.
	 * @param p		The partnership data.
	 */
	public EBMSMessageSender(FileLogger 		 l
							,EBMSMessageData 	 m
							,EBMSPartnershipData p) throws MessageSenderException{
		super(l, m, p);											
		EBMSMessageData d = (EBMSMessageData) m;		
		// Setup the sender config.
		this.setLoopTimes(1);
		this.setServiceEndPoint(d.getSendEndpoint());
	}
	
	/**
	 * Initialize the SOAP Message.
	 */
	public void onStart(){
		super.onStart();
		if (this.log != null){
			// Log all information for this sender.
			this.log.log("EBMS Message Client init at " + new Date().toString());
			this.log.log("----------------------------------------------------");
			this.log.log("Partnership Data using: ");
			this.log.log("----------------------------------------------------");
			if (this.ps != null){				
				this.log.log(this.ps.toString());
			}
			this.log.log("");
			this.log.log("----------------------------------------------------");
			this.log.log("Configuration Data using: ");
			this.log.log("----------------------------------------------------");
			if (this.properties != null){
				this.log.log(this.properties.toString());
			}			
			this.log.log("----------------------------------------------------");
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
	 * Initialize the message using the properties in the MessageProps.
	 */
	public void initializeMessage() throws Exception {
		
		if (!(this.properties instanceof EBMSMessageData))
			throw new ClassCastException("Invalid EBMS Message class data");		
		if (!(this.ps instanceof EBMSPartnershipData))
			throw new ClassCastException("Invalid EBMS Partnership class data");
		
		EBMSMessageData 	 d = (EBMSMessageData) this.properties;		
		EBMSPartnershipData	ps = (EBMSPartnershipData) this.ps;
		
		this.addRequestElementText("cpaId"			, ps.getCpaId(), 	    NS_PREFIX, NS_URI);
		this.addRequestElementText("service"		, ps.getService(), 		NS_PREFIX, NS_URI);
		this.addRequestElementText("action"			, ps.getAction(), 		NS_PREFIX, NS_URI);
		this.addRequestElementText("convId"			, d.getConversationId(),NS_PREFIX, NS_URI);
		this.addRequestElementText("fromPartyId"	, d.getFromPartyId(),   NS_PREFIX, NS_URI);
		this.addRequestElementText("fromPartyType"	, d.getFromPartyType(), NS_PREFIX, NS_URI);
		this.addRequestElementText("toPartyId"		, d.getToPartyId(),     NS_PREFIX, NS_URI);
		this.addRequestElementText("toPartyType"	, d.getToPartyType(),	NS_PREFIX, NS_URI);
		this.addRequestElementText("refToMessageId"	, d.getRefToMessageId(),NS_PREFIX, NS_URI);	
		this.addRequestElementText("serviceType"	, d.getServiceType()   ,NS_PREFIX, NS_URI);
	}
	
	/**
	 * [@EVENT] Retrieve the id of newly created message from the SOAP message. 
	 */
	public void onResponse() throws Exception{
		EBMSMessageData d 	= (EBMSMessageData) this.properties;
		// Get the first element with tagname is "message_id".
		this.lastSuccessfulQueryMessageId = 
			this.getResponseElementText("message_id", NS_URI, 0);
		
		if (this.log != null)		
			this.log.log("Message Id: " + lastSuccessfulQueryMessageId);
	}		
	
	/** 
	 * Get the message id of last successful web service query.
	 * This should be called only after {@link #onResponse()}
	 * 
	 * @return the message id 
	 */
	public String getResponseMessageId(){
		return this.lastSuccessfulQueryMessageId;
	}
	
	/**
	 * The main method is for CLI mode.
	 */
	public static void main(String [] args){
		try{			
			if (args.length < 3){
				System.out.println("Usage: ebms-send [partnership-xml] [config-xml] [log-path] [payload]");
				System.out.println();
				System.out.println("Example: ebms-send " +
								   "./config/ebms-send/ebms-request.xml " +
								   "./config/ebms-partnership.xml " +
								   "./logs/ebms-send.log " +
								   "./config/ebms-send/testpayload");
				System.exit(1);
			}					
			System.out.println("----------------------------------------------------");
			System.out.println("          EbMS sender web service client            ");
			System.out.println("----------------------------------------------------");

			// Initialize the logger.			
			System.out.println("Initialize Logger ... ");
			FileLogger logger = new FileLogger(new java.io.File(args[2]));
			
			// Initialize the query parameter.
			System.out.println("Importing  ebMS sending parameters ... " + args[1] );
			EBMSMessageData emd = 
				DataFactory.getInstance()
					.createEBMSMessageDataFromXML(
						new PropertyTree(
							new java.io.File(args[1]).toURI().toURL()));						
									
			// Grab the partnership data from the XML file.
			System.out.println("Importing  ebMS partnership parameters ... " + args[0]);	
			EBMSPartnershipData ps 	 = 
				DataFactory.getInstance()
					.createEBMSPartnershipFromXML(
					new PropertyTree(
						new java.io.File(args[0]).toURI().toURL()));
								
			// Initialize the sender.
			System.out.println("Initialize ebMS web service client... ");
			EBMSMessageSender sender = new EBMSMessageSender(logger, emd, ps);
			
			// Add payload part
			System.out.println("Adding     payload in the ebMS message... ");
			if (args.length >= 4){
				Payload    payload = new Payload(args[3], "application/octet-stream");																
				Payload[] payloads = new Payload[]{payload};
												
				sender.addRequestPayload(payloads);
				// Don't let the sender override the request.
				sender.setRequestDirty(false);
			}
			
			System.out.println("Sending    ebMS sending request ... ");
			sender.run();
											
			System.out.println();
			System.out.println("                    Sending Done:                   ");
			System.out.println("----------------------------------------------------");
			System.out.println("New message id: " + sender.getResponseMessageId());
			System.out.println();
			System.out.println("Please view log for details .. ");
			
		}catch(Exception e){
			e.printStackTrace(System.err);
		}		
	}
}
