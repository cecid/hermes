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
import java.util.Date;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.DataFactory;
import hk.hku.cecid.corvus.ws.data.EBMSMessageData;
import hk.hku.cecid.corvus.ws.data.Payload;
import hk.hku.cecid.piazza.commons.io.NIOHandler;
import hk.hku.cecid.piazza.commons.util.PropertyTree;


/**
 * The <code>EBMSMessageReceiver</code> is a client sender sending 
 * SOAP web services request to B2BCollector <code>EbMS</code>
 * plugin for retrieving a ebMS message according to provided message id.<br/>
 * 
 * The web service parameters are defined in the below:
 * <pre>
 *   &lt;part name="messageId" type="s:string" /> *   
 * </pre>
 *
 * @author Jumbo Cheung
 * @version 1.0.1a
 * @since	H2O June, 2008
 * 
 * @see hk.hku.cecid.corvus.ws.data.EBMSMessageData
 */
public class EBMSMessageReceiver extends MessageReceiver{

	private final String NS_URI = EBMSMessageSender.NS_URI;
	/**
	 * The output directory.
	 */
	private String outputDir = "./output";
	
	/**
	 * The message id used for receive the files.
	 */
	private String messageId;	
	
	public EBMSMessageReceiver(FileLogger 		 l
			,EBMSMessageData 	 m) {
		super(l, m);
		EBMSMessageData d = (EBMSMessageData) m;
		
		this.messageId = d.getMessageIdForReceive();
		
		// Setup the receiver config.
		this.setLoopTimes(1);
		this.setServiceEndPoint(d.getRecvEndpoint());		
	}
	
	/**
	 * Initialize the message using the properties in the MessageProps.
	 */
	public void initializeMessage() throws Exception {
		
		if (!(this.properties instanceof EBMSMessageData))
			throw new ClassCastException("Invalid EBMS Message class data");		
		EBMSMessageData 	 d = (EBMSMessageData) this.properties;		
		this.addRequestElementText("messageId", d.getMessageIdForReceive() , NS_PREFIX, NS_URI);		
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
	 * Initialize the SOAP Message.
	 */
	public void onStart(){
		super.onStart();
		if (this.log != null){
			// Log all information for this sender.
			this.log.log("EBMS Message Receiver Client init at " + new Date().toString());
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
			this.log.log("Received Message id: " + this.messageId);			
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
				String filename = "ebms."+this.messageId+".Payload."+i;
				
				File outputFile = new File(outputFolder.getAbsolutePath()+File.separator+filename);
				// Pipe the payload to the designated file.
				NIOHandler.pipe(payloads[i].getInputStream(), new FileOutputStream(outputFile));
			}			
		}
	}	
	
	public static void main(String [] args){
		try{			
			if (args.length < 3){
				System.out.println("Usage: ebms-recv [config-xml] [log-path] [output folders] ");
				System.out.println();
				System.out.println("Example: ebms-recv " +
								   "./config/ebms-recv/ebms-request.xml " +
								   "./logs/ebms-recv.log " +
								   "./output/ebms-recv/ ");
				System.exit(1);
			}					
			System.out.println("----------------------------------------------------");
			System.out.println("       EBMS Message Receiver           ");
			System.out.println("----------------------------------------------------");

			// Initalize the logger.			
			System.out.println("Initialize Logger ... ");
			FileLogger logger = new FileLogger(new java.io.File(args[1]));
			
			// Initialize the query parameter.
			System.out.println("Importing  ebMS sending parameters ... "  + args[0] );			
			EBMSMessageData requestData = 
				DataFactory.getInstance().createEBMSMessageDataFromXML(
								new PropertyTree(new java.io.File(args[0]).toURI().toURL()));	

			
			
			// Initalize the receiver client for downloading available message.
			System.out.println("Initialize ebMS message receiver... ");
			EBMSMessageReceiver recvSender =	new EBMSMessageReceiver(logger, requestData);				
			recvSender.setOutputDirectory(args[2]);
			

			System.out.println("Sending  ebMS receiving request ... for " + requestData.getMessageIdForReceive());
			recvSender.run();
			System.out.println();

			System.out.println("----------------------------------------------------");
			System.out.println();
			System.out.println("Please view logs for details .. ");
		}catch(Exception e){
			e.printStackTrace(System.err);
		}		
	}
}
