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
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.Date;
import java.util.List;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.DataFactory;
import hk.hku.cecid.corvus.ws.data.EBMSMessageData;
import hk.hku.cecid.corvus.ws.data.EBMSPartnershipData;
import hk.hku.cecid.corvus.ws.data.Payload;

import hk.hku.cecid.piazza.commons.util.PropertyTree;

import hk.hku.cecid.piazza.commons.io.NIOHandler;

/**
 * The <code>EBMSReceiverSender</code> is a client sender sending 
 * SOAP web services request to B2BCollector <code>EbMS</code>
 * plugin for downloading the EbMS message and it's corresponding
 * payload.  
 * 
 * @author Twinsen Tsang
 * @version 1.0.1
 * @since	Elf 0818 
 */
public class EBMSReceiverSender extends EBMSMessageSender{
	 	
	/**
	 * The output directory.
	 */
	private String outputDir = "./output";
	
	/**
	 * The message id used for receive the files.
	 */
	private String messageId;	
	 
	/**
	 * Explicit Constructor.
	 * 
	 * @param l		The logger used for log message and exception.
	 * @param m		The message data for party information and send/recv configuration.
	 * @param ps	The partnership data.
	 */
	public EBMSReceiverSender(FileLogger 		   l
							 ,EBMSMessageData	   m
							 ,EBMSPartnershipData  ps) throws MessageSenderException{
		super(l, m, ps);			
		EBMSMessageData d = (EBMSMessageData) m;		
		// Setup the sender config.
		this.setLoopTimes(1);
		this.setServiceEndPoint(d.getRecvEndpoint());				
	}
	
	/**
	 * Initialize the SOAP Message.
	 */
	public void onStart(){		
		// TODO: Unable to start the profiling timer because it is bound by superclass.
		if (this.log != null){
			// Log all information for this sender.
			this.log.log("EBMS Recevier Client init at " + new Date().toString());
			this.log.log("----------------------------------------------------");
			this.log.log("Output Directory : " + this.outputDir);
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
			
		EBMSMessageData d = (EBMSMessageData) this.properties;		
		this.addRequestElementText("messageId", this.messageId, NS_PREFIX, NS_URI);
	}
			
	/**
	 * Retrieve the payload from the message.<br>
	 * 
	 * The default receiver stores the payload as a files at the particular
	 * place specified in the configuration.  
	 */
	public void onResponse() throws Exception{
		EBMSMessageData d 	= (EBMSMessageData) this.properties;
		// Get the first element with tagname is "hasMessage".
		String result		= this.getResponseElementText("hasMessage",NS_URI, 0);
		
		if (log != null){
			this.log.log("Received Message id: " + this.getMessageIdToRetreive());			
			this.log.log("Has payload ?      : " + result);
		}
						
		if (Boolean.valueOf(result).booleanValue()){
			// Retreive the actual file path.
			 			
			
			File outputFolder = new File(this.getOutputDirectory());
			if (!outputFolder.exists())
				outputFolder.mkdirs();
			
			// Get payload.
			Payload[] payloads = this.getResponsePayloads();
						
			// For each payload, we get the input stream
			// from the payload and read it to buffer.
			// then open the output file path and write the buffer.
			for (int i = 0; i < payloads.length; i++){
				String filename = "ebms."+this.getMessageIdToRetreive()+".Payload."+i;
				
				File outputFile = new File(outputFolder.getAbsolutePath()+File.separator+filename);
				// Pipe the payload to the designated file.
				NIOHandler.pipe(payloads[i].getInputStream(), new FileOutputStream(outputFile));
			}			
		}
	}	
	
	/**
	 * @param messageId the message id to retreive the payload / message. 
	 */
	public void setMessageIdToRetreive(String messageId){
		this.messageId = messageId;
		this.setRequestDirty(true);
	}
	
	/**
	 * @return the message id to retreive. 
	 */
	public String getMessageIdToRetreive(){
		return this.messageId;
	}		
	
	/**
	 * @throws Operation does not support.
	 */
	public String getResponseMessageId() {
		throw new UnsupportedOperationException("Receiver WS does not support this operation.");
	}

	/**
	 * Set the output directory of received payload if any.
	 */
	public void setOutputDirectory(String path){
		this.outputDir = path;
	}
	
	/**
	 * @return the output directory of the received payload.
	 */
	public String getOutputDirectory(){
		return this.outputDir;
	}

	/**
	 * The main method is for CLI mode.
	 */
	public static void main(String [] args){
		try{			
			if (args.length < 3){
				System.out.println("Usage: ebms-recv [partnership-xml] [config-xml] [log-path] [output folder]");
				System.out.println();
				System.out.println("Example: ebms-recv " +
								   "./config/ebms-partnership.xml " +
								   "./config/ebms-recv/ebms-request.xml " +
								   "./logs/ebms-recv.log " +
								   "./output/ebms-recv/ ");
				System.exit(1);
			}					
			System.out.println("----------------------------------------------------");
			System.out.println("       EBMS Message Receiver          ");
			System.out.println("----------------------------------------------------");

			// Initalize the logger.			
			System.out.println("Initialize Logger ... ");
			FileLogger logger = new FileLogger(new java.io.File(args[2]));
			
			// Initialize the query parameter.
			System.out.println("Importing  ebMS sending parameters ... "  + args[1] );			
			EBMSMessageData emd = 
				DataFactory.getInstance()
					.createEBMSMessageDataFromXML(
						new PropertyTree(
							new java.io.File(args[1]).toURI().toURL()));	

			// Initialize the query parameter.
			System.out.println("Importing  ebMS partnership parameters ... "  + args[0]);					
			// Grab the partnership data from the XML file.
			EBMSPartnershipData ps 	 = 
				DataFactory.getInstance()
					.createEBMSPartnershipFromXML(
					new PropertyTree(
						new java.io.File(args[0]).toURI().toURL()));

			// Initalize the receiver client for downloading available message.
			System.out.println("Initialize ebMS message receiver ... ");
			EBMSReceiverSender recvSender =	new EBMSReceiverSender(logger, emd,	ps);				
			recvSender.setOutputDirectory(args[3]);
					
			
			// Initalize the receiver list for finding available message.
			System.out.println("Initialize ebMS receiver list queryer... ");
			EBMSReceiverListSender recvListSender = new EBMSReceiverListSender(logger, emd, ps);
			// Send the message.
			System.out.println("Sending    ebMS receiving list request ... ");
			recvListSender.run();			
			
			List msgsList = recvListSender.getAvailableMessages();
			
			System.out.println();
			System.out.println("                    Sending Done:                   ");
			if (msgsList.size() == 0){
				System.out.println("----------------------------------------------------");
				System.out.println("No message found ..");
			} else{
				int msgIdx = -1;
				// Ask the user to choose which message to download.
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				do{
					boolean askSelection = true;
					while (askSelection) {
						System.out.println("----------------------------------------------------");
						System.out.println("         EbMS Message that can be download          ");
						System.out.println("----------------------------------------------------");
						for (int i = 0; i < msgsList.size(); i++)
							System.out.println(i + " Message id : " + msgsList.get(i));
						System.out.println("----------------------------------------------------");
						System.out.print("Select message (0 - " + (msgsList.size()-1) + "), -1 to exit: ");				
						
						try{					
							msgIdx = Integer.parseInt(br.readLine());
							if (msgIdx < -1) {
								System.out.println("Input must be greater than or equal to -1");
							} else if (msgIdx >= msgsList.size()) {
								System.out.println("Input must be less than " + msgsList.size());
							} else {
								askSelection = false;
							}
						}catch(Exception e){
							System.out.println("Invalid Input. It must be number");						
						}
					}
					if (msgIdx == -1)
						break;
					
					String mid = (String)msgsList.get(msgIdx);
					recvSender.setMessageIdToRetreive(mid);
					System.out.println("Sending    ebMS receiving request ... for " + mid);
					recvSender.run();
					msgsList.remove(msgIdx);
					System.out.println();
				}
				while(msgsList.size() != 0);
			}
			System.out.println("----------------------------------------------------");
			System.out.println();
			System.out.println("Please view logs for details .. ");
		}catch(Exception e){
			e.printStackTrace(System.err);
		}		
	}
}
