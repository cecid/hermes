package hk.hku.cecid.corvus.test;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

/**
 * A very simple loopback test client for testing <code>AS2</code>.  
 * 
 * Creation Date: 21/05/2007
 * 
 * @author Twinsen Tsang
 */
public class AS2Loopback {

	/*
	 * The webservices URL endpoint that can communicate Hermes2. 
	 */
	private String hermes2SenderWSURLStr = "http://127.0.0.1:8080/corvus/httpd/as2/sender";
	private String hermes2ReceiverListWSURLStr ="http://127.0.0.1:8080/corvus/httpd/as2/receiver_list";
	private String hermes2ReceiverWSURLStr = "http://127.0.0.1:8080/corvus/httpd/as2/receiver";
	
	/*
	 * The following are the required parameters to deliver the AS2 Message. 
	 * In loopback test, they are hard-coded as following.
	 */
	private String as2From 	= "Sender";
	private String as2To  	= "Receiver";
	private String type		= "xml";
	
	/*
	 * The newly created message id by method send. 
	 */
	private String newMessageId; 

	/**
	 * The entry point of the program.
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		AS2Loopback as2Loopback = new AS2Loopback();
		as2Loopback.run();
    }

	/**
	 * Run the loopback test for sending and receiving AS2 message.    
	 */
	private void run() throws Exception {
		send();
		receive();
	}
	
    /** 
     * Try to send web service request to Hermes2 through the <code>AS2Sender</code>.
     *  
     * @return The MID (message id) of the newly message created by your request.
     * @throws Exception 
     * 
     * @see {@link hk.hku.cecid.corvus.test.AS2Sender#send(Payload[])}
     */
    private String send() throws Exception {
    	System.out.println("Sending loopback messages to Hermes2 under AS2 protocol...");
    	
    	// Create a AS2 sender for sending web services request to Hermes2 
    	// for delivering an EbXML message loopback.
    	AS2Sender as2Sender = new AS2Sender(hermes2SenderWSURLStr, as2From, as2To, type);
		
		// Attach two sample payload in our requests.
    	// Currently, AS2 only supports 1 payload only.
		Payload [] payloads = { (new Payload("data/data1.txt", "text/plain")) };								
		
		// Send the request to Hermes 2.
		this.newMessageId = as2Sender.send(payloads);
		if (this.newMessageId == null){
			String detail = "Unable to create message, possible : No partnership is defined.";
			System.out.println(detail);
			throw new Exception(detail);
		}
		
		System.out.println("...finished. The message id is '" + this.newMessageId +	"'");		
		return this.newMessageId;    	
    }
    
    /**
     * Try to receive the loopback message.
     * 
     * @throws Exception
     * 
     * @see {@link hk.hku.cecid.corvus.test.AS2ReceiverList#getReceivedMessagesIds()}
     * @see {@link hk.hku.cecid.corvus.test.AS2Receiver#downloadPayloads(String)}
     */
    private void receive() throws Exception {
    	System.out.println("Trying to download the received messages...");
    	
    	// Create an EbMS receiver list for sending web services request to Hermes2
    	// and return the list of message that are ready to download.
		AS2ReceiverList as2ReceiverList = new AS2ReceiverList(
			hermes2ReceiverListWSURLStr, as2From, as2To, 100);
		
		// Create an EbMS receiver for sending web services request to Hermes2 
		// and return the payload of particular message if any. 
		AS2Receiver as2Receiver = new AS2Receiver(hermes2ReceiverWSURLStr);

		// polling the Hermes2 for downloading the message.
		while(true){
			System.out.println("Waiting 5s for downloading the messages..."); 
			Thread.sleep(5000);
			// Query Hermes2 and get the list of ready download messages.
	        Iterator messageIdsIter = as2ReceiverList.getReceivedMessagesIds();
	                
	        while(messageIdsIter.hasNext()){	
	        	String messageId = (String) messageIdsIter.next();
	
	        	if (messageId.equalsIgnoreCase(this.newMessageId)){
	            	// The message we sent has been processed, download it. 
	        		System.out.println("The message has been received. The message id is '" + messageId + "'");
	        		// Query Hermes2 and get the payload of that message.
	            	Iterator payloadsIter = as2Receiver.downloadPayloads(messageId);
	            	int i = 0;
	            	while(payloadsIter.hasNext()){
	            		Payload payload = (Payload)payloadsIter.next();
	            		System.out.println("A payload is found. Its content type is '" + payload.getContentType() + "'");            		
	            		System.out.println("Saving as file 'payload" + i + "'.");
	            		// Pipe the payload stream to the specified file.
	            		BufferedInputStream bis = new BufferedInputStream(payload.getInputStream());
	            		FileOutputStream 	fos = new FileOutputStream("payload" + i);
	            		int bsize = bis.available() > 1024 ? 1024 : bis.available();	            		
	            		byte [] buf = new byte[bsize];
	            		int j = 0;
	            		while ((j = bis.read(buf)) != -1){
	            			fos.write(buf, 0, j);
	            		}
	            		i++;
	            	}
	            	return;
	        	}
	        }
		}
    }
}
