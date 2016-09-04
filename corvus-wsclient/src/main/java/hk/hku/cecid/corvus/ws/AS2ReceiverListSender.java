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
import java.util.List;
import java.util.ArrayList;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.DataFactory;
import hk.hku.cecid.corvus.ws.data.AS2MessageData;
import hk.hku.cecid.corvus.ws.data.AS2PartnershipData;
import hk.hku.cecid.piazza.commons.util.PropertyTree;

/**
 * The <code>AS2ReceiverListSender</code> is a client sender sending 
 * SOAP web services request to B2BCollector <code>AS2</code>
 * plugin for query whether if there is any message 
 * that are available.<br/><br/> 
 * 
 * The web service parameters are defined in the below: 
 * <pre>
 *  &lt;part name="as2From" type="s:string" />
 *  &lt;part name="as2To" type="s:string" />
 *  &lt;part name="numOfMessages" type="s:int" />
 * </pre> 
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	Dwarf 10315
 */
public class AS2ReceiverListSender extends MessageSender {

	private final String NS_URI = AS2MessageSender.NS_URI;
	
	/**
	 * Number of message to retreive for one soap call.
	 */
	private int numMessageToRetreive = 100;
	
	/**
	 * The result message id list. 
	 */
	private ArrayList resultMessages = new ArrayList(); 
	
	/**
	 * Explicit Constructor.
	 * 
	 * @param l		The logger used for log message and exception.
	 * @param m		The message data for party information and send/recv configuration.
	 * @param p		The partnership data.
	 */
	public AS2ReceiverListSender(FileLogger 		l
								,AS2MessageData	    m
								,AS2PartnershipData p){
		super(l, m, p);			
		AS2MessageData d = (AS2MessageData) m;		
		// Setup the sender config.
		this.setLoopTimes(1);
		this.setServiceEndPoint(d.getRecvlistEndpoint());				
	}
	
	/**
	 * Initialize the SOAP Message.
	 */
	public void onStart(){
		super.onStart();
		if (this.log != null){
			// Log all information for this sender.
			this.log.log("AS2 Recevier List Client init at " + new Date().toString());
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
		
		if (!(this.properties instanceof AS2MessageData))
			throw new Exception("Invalid AS2 Message class data");	
		
		if (!(this.ps instanceof AS2PartnershipData))
			throw new Exception("Invalid AS2 Partnership class data");
		
		AS2MessageData 	 d 	   = (AS2MessageData) this.properties;
		AS2PartnershipData	ps = (AS2PartnershipData) this.ps;
		String numOfMessages   = String.valueOf(this.numMessageToRetreive);		
		this.addRequestElementText("as2From"		, ps.getAS2From(),	NS_PREFIX, NS_URI);
		this.addRequestElementText("as2To"			, ps.getAs2To(), 	NS_PREFIX, NS_URI);		
		this.addRequestElementText("numOfMessages"  , numOfMessages,   	NS_PREFIX, NS_URI);
	}
	
	/**
	 * [@EVENT] Record all the AS2 message that ready to download.<br/><br/>
	 * 
	 * Developer should invocate {@link #getAvailableMessages()} to 
	 * get a list of all ready AS2 message. 
	 */
	public void onResponse() throws Exception{
		AS2MessageData d 	= (AS2MessageData) this.properties;		
		String [] messageIds = this.getResponseElementAsList("messageId", NS_URI);
		
		this.log.log("Available Message(s): ");
		this.log.log("----------------------------------------------------");
		
		for(int i = messageIds.length -1; i >= 0; i--){
			this.resultMessages.add(messageIds[i]);
			if (this.log != null)
				this.log.log("Message Id: " + messageIds[i]);					
		}
	}		
	
	/**
	 * Set number of message to retrieve for one soap call.
	 * 
	 * @param numMsgs number of message to retrieve for one soap call. 			
	 */
	public void setNumOfMessageToRetrieve(int numMsgs){
		this.numMessageToRetreive = numMsgs;
		this.setRequestDirty(true);
	}
	
	/**
	 * @return number of message to retrieve for one soap call. 
	 */
	public int getNumOfMessageToRetrieve(){
		return this.numMessageToRetreive;
	}
	
	/**
	 * This method should be called after the event {@link #onResponse()}
	 * 
	 * @return 	a list of message id that are ready to download. 			
	 */
	public List getAvailableMessages(){
		return this.resultMessages;
	}
	
	/**
	 * The main method is for CLI mode.
	 */
	public static void main(String [] args){
		try{			
			if (args.length < 3){
				System.out.println("Usage: as2-recvlist [partnership-xml] [config-xml] [log-path]");
				System.out.println();
				System.out.println("Example: as2-recvlist " +
								   "./config/as2-partnership.xml " +
								   "./config/as2-recvlist/as2-request.xml " +
								   "./logs/as2-recvlist.log ");
				System.exit(1);
			}					
			System.out.println("----------------------------------------------------");
			System.out.println("          AS2 Receiver List Queryer      ");
			System.out.println("----------------------------------------------------");

			// Initialize the logger.			
			System.out.println("Initialize Logger ... ");
			FileLogger logger = new FileLogger(new java.io.File(args[2]));
			
			// Initialize the query parameter.
			System.out.println("Importing  AS2 sending parameters ... " + args[1] );			
			AS2MessageData emd = 
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
			System.out.println("Initialize AS2 receiver list queryer... ");
			AS2ReceiverListSender sender = new AS2ReceiverListSender(logger, emd, ps);
			// Send the message.
			System.out.println("Sending    AS2 receiving list request ... ");
			sender.run();
			
			// Get the receiver list.
			List l = sender.getAvailableMessages();
			System.out.println();
			System.out.println("                    Sending Done:                   ");
			System.out.println("----------------------------------------------------");
			System.out.println("         AS2 Message that can be download          ");
			System.out.println("----------------------------------------------------");
			if (l.size() == 0){
				System.out.println("No message found ..");
			} else{
				for (int i = 0; i < l.size(); i++)
					System.out.println(i + " Message id : " + l.get(i));
			}
			System.out.println("----------------------------------------------------");
			System.out.println();
			System.out.println("Please view log for details .. ");			
											
		}catch(Exception e){
			e.printStackTrace(System.err);
		}		
	}
}
