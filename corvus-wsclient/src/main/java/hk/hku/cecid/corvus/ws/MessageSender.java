package hk.hku.cecid.corvus.ws;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.Iterator;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.Data;
import hk.hku.cecid.corvus.ws.data.Payload;


/**
 * The <code>MessageSender</code> is the abstract class which is used to send 
 * SOAP messsage request to B2B Collector series product.
 * 
 * @author 	Joel Matsumoto, Twinsen Tsang
 * @version	1.0.2
 * @since	Elf 0818 
 */
public abstract class MessageSender extends SOAPSender {
		
	/**
	 * The partnership properties used for sending. 
	 */
	protected Data ps;
			
	/**
	 * The default content type of payload. 
	 * In profiling, the content type of payload message is plain text.  
	 */
	private String msgContentType = "text/plain";
	
	/**
	 * The start time of sending. It is used for sender which
	 * want to track how long it take to execute.
	 */
	private long startTime = 0;

	/**
	 * The end time of sending. It is used for sender which
	 * want to track how long it take to execute.
	 */
	private long endTime = 0;
	
	/**
	 * Explicit Constructor. 
	 * 
	 * @param l			The logger used for log message and exception.
	 * @param m			The message properties including how many message need to 
	 * 					be sent and some performance parameter.
	 * @param p			The partnership properties used for sending.			
	 */
	public MessageSender(FileLogger l, Data m, Data p){
		super(l,m);
		this.ps = p;
	}		
			
	/** 
	 * [@EVENT] This method is invoked when the sender begins to execute the 
	 * run method.  
	 */
	public void onStart(){
		this.startTime = new Date().getTime();			
	}
	
	/**
	 * [@EVENT] This method is invoked when the sending execution is ended. 
	 */
	public void onEnd(){
		this.endTime = new Date().getTime();
	}		
	
	/**
	 * [@EVENT] Log all known exceptions and stack trace.
	 * 
	 * @param t 	The exception encountered.
	 */
	public void onError(Throwable t){
		String msg = t.getMessage();
		
		if (this.log != null){
			if (t instanceof SOAPException){
				this.log.log("Could not send the SOAP message: " + msg);				
			} else if (t instanceof MalformedURLException){
				this.log.log("Could not find the URL: " + this.getServiceEndPoint());
			} else if (t instanceof UnsupportedOperationException){
				this.log.log("Unsupported SOAP class and web services: " + msg);
			} else if (t instanceof NullPointerException){
				this.log.log("Null Pointer Exception");
			}
			this.log.logStackTrace(t);		
		} else {
			t.printStackTrace();
		}
	}	 	
			
	/**
	 * Add a set of payloads to the SOAP Request.<br/><br/>
	 * 
	 * The payloads are attached in the attachment part in the SOAP Message.
	 * 
	 * @param payloads	The array of payload.
	 * @return			true if payloads is added succesfully.	
	 */
	public boolean addRequestPayload(Payload [] payloads){
		if (this.request == null)
			return false;

		for (int i = 0; i < payloads.length; i++){
			if (payloads[i] != null){
				AttachmentPart ap = this.request.createAttachmentPart();
				if (ap != null){
					// Create file datasource.							
					FileDataSource fileDS = null;
					fileDS = new FileDataSource(new File(payloads[i].getFilePath()));
					ap.setDataHandler(new DataHandler(fileDS));
					ap.setContentType(payloads[i].getContentType());
					
					this.request.addAttachmentPart(ap);
					if (this.log != null){
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
	 * Get the payload from the SOAP response. This should be called 
	 * during {@link #onResponse()}.
	 * 
	 * @return A set of payload in SOAP message.
	 * 
	 * @throws SOAPException
	 * 			When unable to extract the payload in the SOAP Response.
	 * @throws IOException
	 * 			When unable to open the input stream for the payload.
	 */
	public Payload[] getResponsePayloads() throws SOAPException, IOException{
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
			index++;
		}
		return payloads;				
	}
	
	/**
	 * Clear all payload in the request.
	 */
	public boolean clearRequestPayload() throws SOAPException{
		if (this.request == null)
			return false;
		this.request.removeAllAttachments();
		this.setRequestDirty(true);
		return true;
	}
	
	/**
	 * Set the content type of the message to be sent.
	 * 
	 * @param contentType 	The content type of message to be sent.
	 */
	public void setContentType(String contentType){
		this.msgContentType = contentType;
	}
	
	/**
	 * Return the content type of the message.
	 * 
	 * @return The content type of message to bes sent.
	 */
	public String getContentType(){
		return this.msgContentType;
	}			 
	
	/**
	 * Get how long it tasks for the sender to do it's tasks.
	 * 
	 * @return The times for the task from start to end in milleseconds.
	 */
	public long getElapsedTime(){
		return (this.endTime - this.startTime);
	}	
	
	/** 
	 * @return Return the start time of the sending process.
	 */
	public long getStartTime(){
		return this.startTime;
	}
	
	/**
	 * @return Return the end time of the sender process.
	 */
	public long getEndTime(){
		return this.endTime;
	}
}
