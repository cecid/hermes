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
import hk.hku.cecid.corvus.ws.data.EBMSMessageData;
import hk.hku.cecid.corvus.ws.data.EBMSPartnershipData;
import hk.hku.cecid.piazza.commons.util.PropertyTree;

/**
 * The <code>EBMSReceiverListSender</code> is a client sender sending SOAP web services request to H2O 
 * <code>EbMS</code> plugin for query whether if there is any message 
 * that are available.<br/><br/> 
 * 
 * The web service parameters are defined in the below: 
 * <pre>
 *  &lt;part name="cpaId" type="s:string" />
 *  &lt;part name="service" type="s:string" />
 *  &lt;part name="action" type="s:string" />
 *  &lt;part name="convId" type="s:string" />
 *  &lt;part name="fromPartyId" type="s:string" />
 *  &lt;part name="fromPartyType" type="s:string" />
 *  &lt;part name="toPartyId" type="s:string" />
 *  &lt;part name="toPartyType" type="s:string" />
 *  &lt;part name="numOfMessages" type="s:int" />
 * </pre> 
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	Elf 0818
 */
public class EBMSReceiverListSender extends MessageSender {

	private final String NS_URI = EBMSMessageSender.NS_URI;
	
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
	public EBMSReceiverListSender(FileLogger 		   l
								 ,EBMSMessageData	   m
								 ,EBMSPartnershipData  p){
		super(l, m, p);			
		EBMSMessageData d = (EBMSMessageData) m;		
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
			this.log.log("EBMS Recevier List Client init at " + new Date().toString());
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
			throw new Exception("Invalid EBMS Message class data");	
		
		if (!(this.ps instanceof EBMSPartnershipData))
			throw new Exception("Invalid EBMS Partnership class data");
		
		EBMSMessageData 	 d = (EBMSMessageData) this.properties;
		EBMSPartnershipData	ps = (EBMSPartnershipData) this.ps;
		String numOfMessages   = String.valueOf(this.numMessageToRetreive);		
		// TODO: Refactor using PARAM_KEY_SET.
		this.addRequestElementText("cpaId"			, ps.getCpaId(), 		NS_PREFIX, NS_URI);
		this.addRequestElementText("service"		, ps.getService(), 		NS_PREFIX, NS_URI);
		this.addRequestElementText("action"			, ps.getAction(), 		NS_PREFIX, NS_URI);
		this.addRequestElementText("convId"			, d.getConversationId(),NS_PREFIX, NS_URI);
		this.addRequestElementText("fromPartyId"	, d.getFromPartyId(), 	NS_PREFIX, NS_URI);
		this.addRequestElementText("fromPartyType"	, d.getFromPartyType(),	NS_PREFIX, NS_URI);
		this.addRequestElementText("toPartyId"		, d.getToPartyId(),		NS_PREFIX, NS_URI);
		this.addRequestElementText("toPartyType"	, d.getToPartyType(),	NS_PREFIX, NS_URI);
		this.addRequestElementText("numOfMessages"  , numOfMessages,    	NS_PREFIX, NS_URI);
	}
	
	/**
	 * [@EVENT] Record all the EbMS message that ready to download.<br/><br/>
	 * 
	 * Developer should invocate {@link #getAvailableMessages()} to 
	 * get a list of all ready EbMS message. 
	 */
	public void onResponse() throws Exception{
		EBMSMessageData d 	= (EBMSMessageData) this.properties;		
		String [] messageIds = this.getResponseElementAsList("messageId", NS_URI);
		
		this.log.log("Available Message(s): ");
		this.log.log("----------------------------------------------------");
		/*
		for(int i = messageIds.length -1; i >=  0; i--){
			this.resultMessages.add(messageIds[i]);
			if (this.log != null)
				this.log.log("Message Id: " + messageIds[i]);					
		}*/
		for(int i = 0; i <  messageIds.length ; i++){
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
				System.out.println("Usage: ebms-recvlist [partnership-xml] [config-xml] [log-path]");
				System.out.println();
				System.out.println("Example: ebms-recvlist " +
								   "./config/ebms-partnership.xml " +
								   "./config/ebms-recvlist/ebms-request.xml " +
								   "./logs/ebms-recvlist.log ");
				System.exit(1);
			}					
			System.out.println("----------------------------------------------------");
			System.out.println("         EbMS receiver list web service client      ");
			System.out.println("----------------------------------------------------");

			// Initialize the logger.			
			System.out.println("Initialize Logger ... ");
			FileLogger logger = new FileLogger(new java.io.File(args[2]));
			
			// Initialize the query parameter.
			System.out.println("Importing  ebMS sending parameters ... "  + args[1]);			
			EBMSMessageData emd = 
				DataFactory.getInstance()
					.createEBMSMessageDataFromXML(
						new PropertyTree(
							new java.io.File(args[1]).toURI().toURL()));	
			
			// Read the partnership. here we use full package name because we
			// don't the classes that haven't used in all operation
			// of this class are imported in the begining of the classes.
			System.out.println("Importing  ebMS partnership parameters ... "  + args[0]);			
			EBMSPartnershipData ps 	 = 
				DataFactory.getInstance()
					.createEBMSPartnershipFromXML(
					new PropertyTree(
						new java.io.File(args[0]).toURI().toURL()));
								
			// Initialize the sender.
			System.out.println("Initialize ebMS receiver list queryer ... ");
			EBMSReceiverListSender sender = new EBMSReceiverListSender(logger, emd, ps);
			// Send the message.
			System.out.println("Sending    ebMS receiving list request ... ");
			sender.run();
			
			// Get the receiver list.
			List l = sender.getAvailableMessages();
			System.out.println();
			System.out.println("                    Sending Done:                   ");
			System.out.println("----------------------------------------------------");
			System.out.println("         EbMS Message that can be download          ");
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
