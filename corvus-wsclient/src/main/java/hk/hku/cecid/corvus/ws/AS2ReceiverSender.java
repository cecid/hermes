package hk.hku.cecid.corvus.ws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.Date;
import java.util.List;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.DataFactory;
import hk.hku.cecid.corvus.ws.data.AS2MessageData;
import hk.hku.cecid.corvus.ws.data.AS2PartnershipData;
import hk.hku.cecid.corvus.ws.data.Payload;

import hk.hku.cecid.piazza.commons.util.PropertyTree;

import hk.hku.cecid.piazza.commons.io.NIOHandler;

/**
 * The <code>AS2ReceiverSender</code> is a client sender sending SOAP web services request to Hermes 
 * Messaging Gateway <code>AS2</code> plugin for down-loading the AS2 message and it's corresponding
 * payload.  
 * 
 * @author Twinsen Tsang
 * @version 1.0.1
 * @since	Elf 0818 
 */
public class AS2ReceiverSender extends AS2MessageSender{
	 	
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
	public AS2ReceiverSender(FileLogger 		   l
							 ,AS2MessageData	   m
							 ,AS2PartnershipData  ps) throws MessageSenderException{
		super(l, m, ps);			
		AS2MessageData d = (AS2MessageData) m;		
		// Setup the sender config.
		this.setLoopTimes(1);
		this.setServiceEndPoint(d.getRecvEndpoint());				
	}
	
	/**
	 * Initialize the SOAP Message.
	 */
	public void onStart(){		
		if (this.log != null){
			// Log all information for this sender.
			this.log.log("AS2 Recevier Client init at " + new Date().toString());
			this.log.log("----------------------------------------------------");
			this.log.log("Output Directory : " + this.outputDir);
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
			throw new ClassCastException("Invalid AS2 Message class data");	
			
		AS2MessageData d = (AS2MessageData) this.properties;		
		this.addRequestElementText("messageId", this.messageId, NS_PREFIX, NS_URI);
	}
			
	/**
	 * Retrieve the payload from the message.<br>
	 * 
	 * The default receiver stores the payload as a files at the particular
	 * place specified in the configuration.  
	 */
	public void onResponse() throws Exception{
		AS2MessageData d 	= (AS2MessageData) this.properties;
		// Get the first element with tag-name is "hasMessage".
		String result		= this.getResponseElementText("hasMessage", NS_URI, 0);
		
		if (log != null){
			this.log.log("Received Message id: " + this.getMessageIdToRetreive());			
			this.log.log("Has payload ?      : " + result);
		}
						
		if (Boolean.valueOf(result).booleanValue()){
			// Retreives the actual file path.
			File outputDir = new File(this.getOutputDirectory());
			if (!outputDir.exists())
				outputDir.mkdirs();
			
			// Get payload.
			Payload[] payloads = this.getResponsePayloads();
						
			// For each payload, we get the input stream
			// from the payload and read it to buffer.
			// then open the output file path and write the buffer.
			for (int i = 0; i < payloads.length; i++){
				String filename = "as2."+this.getMessageIdToRetreive()+".Payload."+i;
				File outputFile = new File(outputDir.getAbsolutePath()+File.separator + filename);
				
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
				System.out.println("Usage: as2-recv [partnership-xml] [config-xml] [log-path] [output folders]");
				System.out.println();
				System.out.println("Example: as2-recv " +
								   "./config/as2-partnership.xml " +
								   "./config/as2-recv/as2-request.xml " +
								   "./logs/as2-recv.log " +
								   "./output/as2-recv/ " );
				System.exit(1);
			}					
			System.out.println("----------------------------------------------------");
			System.out.println("       AS2 Message Receiver           ");
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

			// Initialize the receiver client for downloading available message.
			System.out.println("Initialize AS2 receiving web service client... ");
			AS2ReceiverSender recvSender =	new AS2ReceiverSender(logger, emd,	ps);				
			recvSender.setOutputDirectory(args[3]);			
			
			// Initialize the receiver list for finding available message.
			System.out.println("Initialize AS2 message receiver... ");
			AS2ReceiverListSender recvListSender = new AS2ReceiverListSender(logger, emd, ps);
			// Send the message.
			System.out.println("Sending AS2 receiving list request ... ");
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
						System.out.println("         AS2 Message that can be download          ");
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
					System.out.println("Sending    AS2 receiving request ... for " + mid);
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
