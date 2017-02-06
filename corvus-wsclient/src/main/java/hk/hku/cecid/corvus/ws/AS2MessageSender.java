/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.ws;

import java.io.File;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.soap.AttachmentPart;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.DataFactory;
import hk.hku.cecid.corvus.ws.data.AS2MessageData;
import hk.hku.cecid.corvus.ws.data.AS2PartnershipData;
import hk.hku.cecid.corvus.ws.data.Payload;

import hk.hku.cecid.piazza.commons.util.PropertyTree;

/**
 * The <code>AS2MessageSender</code> is a client sender sending 
 * SOAP web services request to B2BCollector <code>AS2</code>
 * plugin for transmission a AS2 message to other parnter.<br/>
 * 
 * The web service parameters are defined in the below: 
 * <pre>
 *  &lt;part name="as2_from" type="s:string" />
 *  &lt;part name="as2_to" type="s:string" />
 *  &lt;part name="type" type="s:string" />
 * </pre>   
 * 
 * @author 	Twinsen Tsang
 * @version	1.0.1
 * @since	Elf 0818
 */
public class AS2MessageSender extends MessageSender{

	/**
	 * The Namespace URI 
	 */
	protected static final String NS_URI = "http://service.as2.edi.cecid.hku.hk/";
	
	
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
	public AS2MessageSender(FileLogger 			 l
						   ,AS2MessageData       m
						   ,AS2PartnershipData   p) throws MessageSenderException{
		super(l, m, p);	
		AS2MessageData d = (AS2MessageData) m;		
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
			this.log.log("AS2 Message Client init at " + new Date().toString());
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
	public void initializeMessage() throws Exception{
		
		if (!(this.properties instanceof AS2MessageData))
			throw new ClassCastException("Invalid AS2 Message class data");		
		if (!(this.ps instanceof AS2PartnershipData))
			throw new ClassCastException("Invalid AS2 Partnership class data");
		
		AS2MessageData 	 d 		= (AS2MessageData) this.properties;
		AS2PartnershipData	ps 	= (AS2PartnershipData) this.ps;
		
		this.addRequestElementText("as2_from", ps.getAS2From(), NS_PREFIX, NS_URI);
		this.addRequestElementText("as2_to"  , ps.getAs2To()  , NS_PREFIX, NS_URI);
		this.addRequestElementText("type"    , d.getType()	  , NS_PREFIX, NS_URI);		
	}
	
	/**
	 * Record the message id. 
	 */
	public void onResponse() throws Exception{
		AS2MessageData d 	= (AS2MessageData) this.properties;
		// Get the first element with tagname is "message_id".		
		this.lastSuccessfulQueryMessageId 
			= this.getResponseElementText("message_id", NS_URI, 0);
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
	
	@Override
	public boolean addRequestPayload(Payload [] payloads){
		if (this.request == null)
			return false;

		for (int i = 0; i < payloads.length; i++){
			if (payloads[i] != null){
				AttachmentPart ap = this.request.createAttachmentPart();
				if (ap != null){
					// Create file datasource.							
					FileDataSource fileDS = new FileDataSource(new File(payloads[i].getFilePath()));
					ap.setDataHandler(new DataHandler(fileDS));
					ap.setContentType(payloads[i].getContentType());
					ap.addMimeHeader("Content-Disposition", "attachment; filename=" + fileDS.getName());
					this.request.addAttachmentPart(ap);
					if (this.log != null){
						this.log.info(fileDS.getName());
						this.log.info("Adding Payload " + i + " " + payloads[i].getFilePath());
					}
				} else{
					if (this.log != null){
						this.log.error("Unable to create attachment part in SOAP request at :" + i);
					}
				}
			}
		}
		this.setRequestDirty(true);
		return true;
	}
		
	/**
	 * The main method is for CLI mode.
	 */
	public static void main(String [] args){
		try{			
			if (args.length < 3){
				System.out.println("Usage: as2-send [partnership-xml] [config-xml] [log-path] [payload]");
				System.out.println();
				System.out.println("Example: as2-send " +
								   "./config/as2-partnership.xml " +
								   "./config/as2-send/as2-request.xml " +
								   "./logs/as2-send.log " +
								   "./config/as2-send/testpayload  ");
				System.exit(1);
			}					
			System.out.println("----------------------------------------------------");
			System.out.println("           AS2 Message Sender            ");
			System.out.println("----------------------------------------------------");

			// Initialize the logger.			
			System.out.println("Initialize Logger ... ");
			FileLogger logger = new FileLogger(new java.io.File(args[2]));
			
			// Initialize the query parameter.
			System.out.println("Importing  AS2 sending parameters ... " + args[1] );			
			AS2MessageData amd = 
				DataFactory.getInstance()
					.createAS2MessageDataFromXML(
						new PropertyTree(
							new java.io.File(args[1]).toURI().toURL()));						
								
			// Grab the partnership data from the XML file.
			System.out.println("Importing  AS2 partnership parameters ... " + args[0] );
			AS2PartnershipData ps = 
				DataFactory.getInstance()
					.createAS2PartnershipFromXML(
						new PropertyTree(
							new java.io.File(args[0]).toURI().toURL()));
								
			// Initialize the sender.
			System.out.println("Initialize AS2 message sender... ");
			AS2MessageSender sender = new AS2MessageSender(logger, amd, ps);
			
			// Add payload part
			System.out.println("Adding payload in the AS2 message... ");
			if (args.length >= 4){
				Payload    payload = new Payload(args[3], "application/octet-stream");																
				Payload[] payloads = new Payload[]{payload};
												
				sender.addRequestPayload(payloads);
				// Don't let the sender override the request.
				// TODO: try to remove "setRequestDirty()".
				sender.setRequestDirty(false);
			}
			
			System.out.println("Sending AS2 sending request ... ");
			sender.run();
											
			System.out.println();
			System.out.println("                    Sending Done:                   ");
			System.out.println("----------------------------------------------------");
			System.out.println("New message id: " + sender.getResponseMessageId());
			System.out.println();
			System.out.println("Please view log for details .. ");
			
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
}
