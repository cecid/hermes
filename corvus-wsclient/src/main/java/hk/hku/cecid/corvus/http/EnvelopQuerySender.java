/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.http;

import java.util.Date;
import java.util.Map;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.Channels;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.Data;
import hk.hku.cecid.corvus.ws.data.KVPairData;
import hk.hku.cecid.piazza.commons.io.IOHandler;

/** 
 * The <code>EnvelopSender</code> is abstract base class for sending HTTP remote request
 * to H2O for querying the message envelop through the administration page.  
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0 
 * @since   H2O 28/11/2007
 */
public class EnvelopQuerySender extends HttpSender 
{	
	/** The constant field representing the standardized outgoing message box representation */ 
	public static final String MSGBOX_OUT 	= "OUTBOX";
	/** The constant field representing the standardized incoming message box representation */
	public static final String MSGBOX_IN	= "INBOX";
		
	/** The constant field representing the HTTP request parameter name for Message id. */
	protected static final String MSGID_FORM_PARAM 		= "message_id";
	/** The constant field representing the HTTP request parameter name for Message box. */
	protected static final String MSGBOX_FORM_PARAM		= "message_box";
	
	// Get the base path for storing the response.
	private static String BASE_PATH = System.getProperty("java.io.tmpdir") + File.separator;	
	private static int THRESHOLD	= 1048576;  
	
	/* The message id to query the message envelop. */
	private String messageIdToDownload = null;
	/* The message box to query the message envelop. */
	private String messageBoxToDownload = null;		
	/* The message envelop returned stream. */
	private InputStream envelopStream;
	
	/**
	 * Explicit Constructor. Create an instance of <code>EnvelopQuerySender</code>
	 * 
	 * @param logger The logger for log the sending process.
	 * @param d The data used for generate Envelop query request. It must be a kind of Admin data.  
	 */
	protected EnvelopQuerySender(FileLogger logger, Data d){ 
		super(logger, d);
	}
	
	/**
	 * Get the message box mapping from standardized representation to proprietary representation.
	 * <br/><br/>
	 * It should return a HashMap like this:
	 * <pre>
	 * HashMap m = new HashMap();
	 * m.put(MSGBOX_IN , "Your Inbox representation");
	 * m.put(MSGBOX_OUT, "Your outbox representation");
	 * 
	 * @return the message box mapping.
	 * 
	 * @see EnvelopQuerySender#MSGBOX_IN 
	 * @see EnvelopQuerySender#MSGBOX_OUT
	 */
	protected Map getMessageBoxMapping(){
		return null;
	}
	
	/**
	 * Set the message criteria for down-load the message envelop (and payload).
	 * 
	 * @param messageId The message id to down-load the message envelop.
	 * @param messageBox The message box to down-load the message envelop. either INBOX or OUTBOX.
	 * 
	 * @throws NullPointerException
	 * 			When {@link #getMessageIdToDownload()} return null.<br/>
	 * 			When {@link #getMessageBoxToDownload()} return empty or null.
	 * @throws IllegalArgumentException 			
	 * 			When {@link #getMessageBoxToDownload()} return string not equal to 'INBOX' and 'OUTBOX' 
	 */
	public final void setMessageCriteriaToDownload(String messageId, String messageBox)
	{
		this.checkArguments(messageId, messageBox);
		this.messageBoxToDownload = messageBox;
		this.messageIdToDownload 	= messageId;
	}
	
	/** 
	 * @return  The message id to down-load the message envelop.
	 */
	public final String getMessageIdToDownload(){
		return this.messageIdToDownload;
	}
	
	/** 
	 * @return The message box to donw-load the message envelop. either INBOX or OUTBOX.
	 */
	public final String getMessageBoxToDownload(){
		return this.messageBoxToDownload;
	}
	
	/** 
	 * This method should be called after executed {@link #run()} successfully. 
	 * 
	 * @return The message envelop stream.  
	 */
	public final InputStream getEnvelopStream() throws IOException {
		if (this.envelopStream == null)
			throw new IOException("There is no envelop stream available, exeucted run() properly ?.");
		return this.envelopStream;
	}
	
	/**
	 * [@EVENT] The method <code>onStart</code> log all new configuration. 
	 */	
	protected void onStart()
	{
		// Get the data.
		KVPairData data = (KVPairData) this.properties;
		super.onStart();
		if (this.log != null){			
			FileLogger l = this.log;
			// Log all information for this sender.
			l.log("Envelop Query HTTP Client init at " + new Date().toString());
			l.log("");
			l.log("Sending Envelop Query HTTP Request with following configuration");				
			l.log("------------------------------------------------------------------");
			if (data != null)
				l.log(data.toString());			
			l.log("------------------------------------------------------------------");
			l.log("");		
		}		
	}	
	
	/** 
	 * [@EVENT] This method is invoked when the sender is required to create a HTTP Request from configuration.
	 * <br/><br/>
	 * It generates a form-url-encoded content embedded in the HTTP POST request. It contains 
	 * two parameters, message_id and message_box. The value of these parameters are  
	 * extracted from {@link #getMessageIdToDownload()} and {@link #getMessageBoxToDownload()} 
	 * respectively.
	 * <br/><br/>
	 * <b>NOTE</b>: The values of message_box parameter may differ to what you see because it
	 * may transform {@link #getMessageBoxMapping()}. 
	 * 
	 * @throws NullPointerException
	 * 			When {@link #getMessageIdToDownload()} return null.<br/>
	 * 			When {@link #getMessageBoxToDownload()} return empty or null.
	 * @throws IllegalArgumentException 			
	 * 			When {@link #getMessageBoxToDownload()} return string not equal to 'INBOX' and 'OUTBOX'
	 */
	protected HttpMethod onCreateRequest() throws Exception 
	{		
		this.checkArguments(this.messageIdToDownload, this.messageBoxToDownload);		
		// Create HTTP Form POST method 
		PostMethod post = new PostMethod(this.getServiceEndPoint().toExternalForm());
		
		// transform the message box value.
		String mappedMsgBox;
		Map messageBoxMapping = this.getMessageBoxMapping();
		
		if (messageBoxMapping == null){
			this.log.warn("No Message Box mapping found, use NO-OP mapping.");
			mappedMsgBox = this.messageBoxToDownload;
		} else {
			mappedMsgBox = (String) messageBoxMapping.get(this.messageBoxToDownload);
		}		
		// Assign the message_id, message_box as the post parameter.
		post.setParameter(MSGID_FORM_PARAM		, this.messageIdToDownload);
		post.setParameter(MSGBOX_FORM_PARAM		, mappedMsgBox);
		return post;
	}
	
	/**
	 * [@EVENT] This method is invoked when received the reply HTTP  response from the server.
	 * <br/><br/>
	 * It saves the response body stream and then available to get through by {@link #getEnvelopStream()}
	 */
	protected void onResponse() throws Exception 
	{
		HttpMethod post = this.getExecutedMethod();
		InputStream ins = post.getResponseBodyAsStream();
		
		/*
		 * We have to pipe the content to either memory or storage because the response stream
		 * is directly extracted from socket which is going to close upon the connection 
		 * has been closed.
		 */
		if (ins.available() < THRESHOLD){
			byte [] envelop = IOHandler.readBytes(ins);
			this.envelopStream = new ByteArrayInputStream(envelop);			
		} else {
			// Create a temporary file at TMP directory.
			File envelopTmp = new File(BASE_PATH + this.hashCode());			
			envelopTmp.deleteOnExit(); 
			// Pipe the content to the TMP file.
			FileChannel fChannel = new FileInputStream(envelopTmp).getChannel(); 
			fChannel.transferFrom(Channels.newChannel(ins), 0, ins.available());
			fChannel.close();			
			// Create an buffered stream to the file.
			this.envelopStream = new BufferedInputStream(new FileInputStream(envelopTmp));			
			// InputStream is closed automatically.
		}		
	}
	
	/*
	 * A Helper method to check whether the arguments passed is valid.  
	 */
	private final void checkArguments(String messageId, String messageBox){
		if (messageId == null)
			throw new NullPointerException("Missing 'messageId' in the argument.");
		if (messageBox == null || messageBox.equals(""))
			throw new NullPointerException("Missing 'messageBox' or it should not be empty.");		
		if (!messageBox.equals(MSGBOX_OUT) && !messageBox.equals(MSGBOX_IN))
			throw new IllegalArgumentException("Invalid 'messageBox' arugments. It should either INBOX or OUTBOX.");
	}
}
