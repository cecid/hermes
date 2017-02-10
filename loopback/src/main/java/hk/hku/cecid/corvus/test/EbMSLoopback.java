package hk.hku.cecid.corvus.test;

import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.util.Iterator;

/**
 * A very simple loopback test client for testing <code>EbMS</code>. 
 * 
 * @author Kochiu, Twinsen Tsang (modifiers)
 * 
 * @see hk.hku.cecid.corvus.test.EbmsSender
 * @see hk.hku.cecid.corvus.test.EbmsReceiverList
 * @see hk.hku.cecid.corvus.test.EbmsReceiver 
 */
public class EbMSLoopback {
	
	/*
	 * The webservices URL endpoint that can communicate Hermes2. 
	 */
	private String hermes2SenderWSURLStr = "http://127.0.0.1:8080/corvus/httpd/ebms/sender";
	private String hermes2ReceiverListWSURLStr ="http://127.0.0.1:8080/corvus/httpd/ebms/receiver_list";
	private String hermes2ReceiverWSURLStr = "http://127.0.0.1:8080/corvus/httpd/ebms/receiver";
	
	/*
	 * The following are the required parameters to deliver the EbXML Message. 
	 * In loopback test, they are hard-coded as following.
	 */
	private String cpaId = "cecid";
	private String service = "cecid:cecid";
	private String action = "order";
	private String conversationId = "convId";
	private String fromPartyId = "fromPartyId";
	private String fromPartyType = "fromPartyType";
	private String toPartyId = "toPartyId";
	private String toPartyType = "toPartyType";
	private String refToMessageId = "refToMessageId";
	
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
		EbMSLoopback ebMSLoopback = new EbMSLoopback();
		ebMSLoopback.run();
    }

	/**
	 * Run the loopback test for sending and receiving EbXML message.    
	 */
	private void run() throws Exception {
		send();
		receive();
	}
	
    /** 
     * Try to send web service request to Hermes2 through the <code>EbmsSender</code>.
     *  
     * @return The MID (message id) of the newly message created by your request.
     * @throws Exception 
     * 
     * @see {@link hk.hku.cecid.corvus.test.EbmsSender#send(Payload[])}
     */
    private String send() throws Exception {
    	System.out.println("Sending loopback messages to Hermes2 under EbMS protocol...");
    	
    	// Create a EbMS sender for sending web services request to Hermes2 
    	// for delivering an EbXML message loopback.
		EbmsSender ebmsSender = new EbmsSender(hermes2SenderWSURLStr, cpaId,
				service, action, conversationId, fromPartyId, fromPartyType,
				toPartyId, toPartyType, refToMessageId);
		
		// Attach two sample payload in our requests.
		Payload [] payloads = { (new Payload("data/data1.txt", "text/plain")),
								(new Payload("data/data2.txt", "text/plain"))};
		
		// Send the request to Hermes 2.
		this.newMessageId = ebmsSender.send(payloads);
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
     * @see {@link hk.hku.cecid.corvus.test.EbmsReceiverList#getReceivedMessagesIds()}
     * @see {@link hk.hku.cecid.corvus.test.EbmsReceiver#downloadPayloads(String)}
     */
    private void receive() throws Exception {
    	System.out.println("Trying to download the received messages...");
    	
    	// Create an EbMS receiver list for sending web services request to Hermes2
    	// and return the list of message that are ready to download.
		EbmsReceiverList ebmsReceiverList = new EbmsReceiverList(
		  	hermes2ReceiverListWSURLStr, cpaId, service, action,
			conversationId, fromPartyId, fromPartyType, toPartyId,
			toPartyType, 100);
		
		// Create an EbMS receiver for sending web services request to Hermes2 
		// and return the payload of particular message if any. 
		EbmsReceiver ebmsReceiver = new EbmsReceiver(hermes2ReceiverWSURLStr);

		// polling the Hermes2 for downloading the message.
		while(true){
			System.out.println("Waiting 5s for downloading the messages..."); 
			Thread.sleep(5000);
			// Query Hermes2 and get the list of ready download messages.
	        Iterator messageIdsIter = ebmsReceiverList.getReceivedMessagesIds();
	                
	        while(messageIdsIter.hasNext()){	
	        	String messageId = (String) messageIdsIter.next();
	
	        	if (messageId.equalsIgnoreCase(this.newMessageId)){
	            	// The message we sent has been processed, download it. 
	        		System.out.println("The message has been received. The message id is '" + messageId + "'");
	        		// Query Hermes2 and get the payload of that message.
	            	Iterator payloadsIter = ebmsReceiver.downloadPayloads(messageId);
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
