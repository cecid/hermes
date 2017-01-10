package hk.hku.cecid.corvus.ws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.AS2MessageData;
import hk.hku.cecid.corvus.ws.data.DataFactory;
import hk.hku.cecid.corvus.ws.data.Payload;
import hk.hku.cecid.piazza.commons.io.NIOHandler;
import hk.hku.cecid.piazza.commons.util.PropertyTree;

public class AS2MessageReceiver extends MessageReceiver{

	private final String NS_URI = AS2MessageSender.NS_URI;
	
	/**
	 * The output directory.
	 */
	private String outputDir = "./output";
	
	/**
	 * The message id used for receive the files.
	 */
	private String messageId;	
	 
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
		
		System.out.println("");
		// Print Console message
		if(Boolean.valueOf(result).booleanValue())
			System.out.println("Payload(s) found in message id:: " + this.messageId);
		else
			System.out.println("There is no payload for this message, id: " + this.messageId);
		
		if (log != null){
			this.log.log("Received Message id: " + this.getMessageIdToRetreive());			
			this.log.log("Has payload ?      : " + result);
		}
						
		if (Boolean.valueOf(result).booleanValue()){
			
			//Make the output folder for storing the payload
			File outputFolder = new File(this.outputDir);
			if(!outputFolder.exists())
				outputFolder.mkdirs();
			
			// Get payload.
			Payload[] payloads = this.getResponsePayloads();
						
			// For each payload, we get the input stream
			// from the payload and read it to buffer.
			// then open the output file path and write the buffer.
			for (int i = 0; i < payloads.length; i++){
				
				String filename = 
					(payloads[i].getFilePath()!=null? payloads[i].getFilePath() :"as2."+this.messageId+".Payload."+i);
				File outputFile = new File(outputFolder.getAbsolutePath()+File.separator+filename);
				
				try{
					//If the file cannot be created, and caused any exception
					outputFile.createNewFile();
				}catch(IOException ioExp){
					//Probably  there is any problem on filenmae
					this.log.error("Exception occur and create payload file: " + 
							filename +"\r\nDefaul Filename used.", ioExp);
					//Apply Default value on filename
					outputFile = new File(outputFolder.getAbsolutePath()+File.separator+ "as2."+this.messageId+".Payload."+i);
					outputFile.createNewFile();
				}
				
				System.out.println("Payload " + i + " saved in: " + outputFile.getCanonicalPath());
				// Pipe the payload to the designated file.
				NIOHandler.pipe(payloads[i].getInputStream(), new FileOutputStream(outputFile));
			}			
		}
	}
	
	@Override
	public Payload[] getResponsePayloads() throws SOAPException ,IOException {
		if (this.response == null)
			return null;	
		
		int index			 = 0;
		Iterator   itr		 = this.response.getAttachments();
		Payload[]  payloads  = new Payload[this.response.countAttachments()];
		while(itr.hasNext()){			
			AttachmentPart ap = (AttachmentPart) itr.next();
			payloads[index]   = new Payload(
    				ap.getDataHandler().getInputStream(),
    				ap.getContentType());

			//Retrieve filename of the payload
			String filename = retrieveAttachmentFilename(ap);
            if(filename != null){
            	payloads[index].setFilePath(filename);
            	this.log.log("Payload filename: " + filename);
            }
            	
			
			index++;
		}
		return payloads;		
	}
	
	private String retrieveAttachmentFilename(AttachmentPart attachment){
		//Check if the filename is set in MIME Header
        String[] values = attachment.getMimeHeader("Content-Disposition");
        String filename = null;
        if(values != null && values.length > 0){
        	for(String value : values){
        		//Debug Message
        		this.log.log("Value: " + value);
        		
        		String[] tokens = value.split(";");
        		if(tokens!= null && tokens.length > 1 &&
        				tokens[0].trim().equalsIgnoreCase("attachment")){
        			for(int i =1; i < tokens.length; i++){
        				if(tokens[i].trim().startsWith("filename")){
        					filename = tokens[i].substring(tokens[i].indexOf("=") +1);
        					if(filename.trim().length() == 0){
        						filename = null;
        						continue;
        					}
        					break;
        				}
        			}
        		}
        	}
        }
        return filename;
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
	
	public AS2MessageReceiver(FileLogger l, 
							AS2MessageData m) {
		super(l, m);
		this.messageId = m.getMessageIdForReceive();
		this.setLoopTimes(1);
		this.setServiceEndPoint(m.getRecvEndpoint());
	}
	
	/**
	 * The main method is for CLI mode.
	 */
	public static void main(String [] args){
		try{			
			if (args.length < 3){
				System.out.println("Usage: as2-recv [config-xml] [log-path] [output folders] ");
				System.out.println();
				System.out.println("Example: as2-recv " +
								   "./config/as2-recv/as2-request.xml  " +
								   "./logs/as2-recv.log  " +
								   "./output/as2-recv/  " );
				System.exit(1);
			}					
			System.out.println("----------------------------------------------------");
			System.out.println("       AS2 Message Receiver start           ");
			System.out.println("----------------------------------------------------");

			// Initialize the logger.			
			System.out.println("Initialize Logger ... ");
			FileLogger logger = new FileLogger(new java.io.File(args[1]));
			
			// Initialize the query parameter.
			System.out.println("Importing  AS2 sending parameters ... " + args[0] );			
			AS2MessageData emd = 
				DataFactory.getInstance()
					.createAS2MessageDataFromXML(
						new PropertyTree(
							new java.io.File(args[0]).toURI().toURL()));	

					// Initialize the receiver client for downloading available message.
			System.out.println("Initialize AS2 message receiver ... ");
			AS2MessageReceiver recvSender =	new AS2MessageReceiver(logger, emd);				
			recvSender.setOutputDirectory(args[2]);
			
			System.out.println("Sending AS2 receiving request ... for " + recvSender.getMessageIdToRetreive());
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
