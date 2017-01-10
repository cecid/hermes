package hk.hku.cecid.corvus.ws;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.util.SOAPUtilities;
import hk.hku.cecid.corvus.ws.data.DataFactory;
import hk.hku.cecid.corvus.ws.data.EBMSMessageData;
import hk.hku.cecid.corvus.ws.data.EBMSStatusQueryData;
import hk.hku.cecid.corvus.ws.data.EBMSStatusQueryResponseData;
import hk.hku.cecid.corvus.ws.data.EBMSMessageHistoryRequestData;
import hk.hku.cecid.piazza.commons.util.PropertyTree;

public class EBMSMessageHistoryQuerySender extends SOAPSender {

	public static String NAMESPACE = "http://service.ebms.edi.cecid.hku.hk/";
	private ArrayList resultMessages = new ArrayList();
	
	public EBMSMessageHistoryQuerySender(FileLogger l
											,EBMSMessageHistoryRequestData m) {
		super(l, m);			
		EBMSMessageHistoryRequestData d = (EBMSMessageHistoryRequestData) m;		
		// Setup the sender config.
		//this.setLoopTimes(1);
		this.setServiceEndPoint(d.getEndPoint());	
	}
	
	public void onStart(){
		super.onStart();
		if (this.log != null){
			// Log all information for this sender.
			this.log.log("EBMS EbMS Message History Query init at " + new Date().toString());
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
	
	public void onResponse() throws Exception{
		EBMSMessageHistoryRequestData  requestData= (EBMSMessageHistoryRequestData) this.properties;		
		List msgList = this.getResponseElementList("messageList", NAMESPACE, 0);
		
		this.log.log("Available Message(s): ");
		this.log.log("----------------------------------------------------");
		
		if(msgList == null)
			return;
			
		Iterator msgIterator = msgList.iterator();
		while(msgIterator.hasNext()){
			List msgElement = (List)msgIterator.next();
			this.resultMessages.add(msgElement);			
			if (this.log != null)
				this.log.log("Message Id: " + (String)msgElement.get(0) +
								"  MessageBox: " + (String)msgElement.get(1));	
		}
		for(int i =0; i < this.resultMessages.size(); i ++){
			int swapIndex = this.resultMessages.size()-1-i;
			
			if((swapIndex) == i || 
					((swapIndex-1) != i &&(swapIndex+1) == i))
				break;

			List temp = (List) this.resultMessages.get(swapIndex); // Get the last index to temp
			this.resultMessages.set(swapIndex, this.resultMessages.get(i));
			this.resultMessages.set(i, temp);
		}
	}
		
	
	public void initializeMessage() throws Exception{
		if (!(this.properties instanceof EBMSMessageHistoryRequestData))
			throw new Exception("Invalid EBMS Quest Request class data");
		
		if (this.properties == null)
			throw new Exception("Invalid EBMS Quest Request: Object null");
	
		EBMSMessageHistoryRequestData 	requestData = (EBMSMessageHistoryRequestData) this.properties;
		
		this.addRequestElementText(EBMSMessageHistoryRequestData.PARAM_KEY_SET[0] , requestData.getMessageBox(), NS_PREFIX, NAMESPACE);
		this.addRequestElementText(EBMSMessageHistoryRequestData.PARAM_KEY_SET[1] , requestData.getStatus(), 	NS_PREFIX, NAMESPACE);
			
				
		this.addRequestElementText(EBMSMessageHistoryRequestData.PARAM_EBMS_KEY_SET[0] , requestData.getMessageId(), NS_PREFIX, NAMESPACE);
		this.addRequestElementText(EBMSMessageHistoryRequestData.PARAM_EBMS_KEY_SET[1] , requestData.getConversationId(), NS_PREFIX, NAMESPACE);
		this.addRequestElementText(EBMSMessageHistoryRequestData.PARAM_EBMS_KEY_SET[2] , requestData.getCpaId() ,NS_PREFIX, NAMESPACE);
		this.addRequestElementText(EBMSMessageHistoryRequestData.PARAM_EBMS_KEY_SET[3] , requestData.getService(),NS_PREFIX, NAMESPACE);
		this.addRequestElementText(EBMSMessageHistoryRequestData.PARAM_EBMS_KEY_SET[4] , requestData.getAction() ,NS_PREFIX, NAMESPACE);
		
	}
	
	public List getResponseElementList(String tagname
			,String nsURI
			,int	whichOne) throws SOAPException{
		SOAPElement msgList = SOAPUtilities.getElement(this.response, tagname, nsURI, whichOne);
		List resultList = new ArrayList();
		if (msgList != null){
			try{
				
				Iterator msgIterator =  msgList.getChildElements();
				while(msgIterator.hasNext()){

					List elementList = new ArrayList();
					
					SOAPElement messageElement = 
									(SOAPElement)msgIterator.next();
					
					Iterator elements = messageElement.getChildElements();
					
					// MessageId
					SOAPElement msgId = (SOAPElement)(elements.next());					
					elementList.add(msgId.getValue());
					
					// MessageId
					SOAPElement msgBox = (SOAPElement)(elements.next());
					elementList.add(msgBox.getValue());
					resultList.add(elementList);
				}
				return resultList;
			}catch(NullPointerException nullExp){
				throw new SOAPException("A NULL value was found in response.\n"+nullExp.getMessage(), nullExp);
			}
		}

		return null;
	}
	
	public List getAvailableMessages(){
		return this.resultMessages;
	}
	
	
	/**
	 * The main method is for CLI mode.
	 */
	public static void main(String [] args){
		try{			
			if (args.length < 2){
				System.out.println("Usage: ebms-history [config-xml] [log-path]");
				System.out.println();
				System.out.println("Example: ebms-history " +
								   "./config/ebms-history/ebms-request.xml " +
								   "./logs/ebms-history.log ");
				System.exit(1);
			}					
			System.out.println("----------------------------------------------------");
			System.out.println("         EbMS Message History Queryer      ");
			System.out.println("----------------------------------------------------");

			// Initialize the logger.			
			System.out.println("Initialize Logger ... ");
			FileLogger logger = new FileLogger(new java.io.File(args[1]));
			
			// Initialize the query parameter.
			System.out.println("Importing  ebMS config parameters ... "  + args[0]);			
			EBMSMessageHistoryRequestData requestData = 
				DataFactory.getInstance()
					.createEbmsMessageHistoryQueryDataFromXML(new PropertyTree(new java.io.File(args[0]).toURI().toURL()));	
			
											
			// Initialize the sender.
			System.out.println("Initialize ebMS messsage history queryer ... ");
			EBMSMessageHistoryQuerySender sender = new EBMSMessageHistoryQuerySender(logger, requestData);
			// Send the message.
			System.out.println("Sending ebMS message history query request ... ");
			sender.run();
			
			// Get the receiver list.
			List msgList = sender.getAvailableMessages();
			System.out.println();
			System.out.println("                    Sending Done:                   ");
			System.out.println("----------------------------------------------------");
			if (msgList.size() == 0){
				System.out.println("                  No message found ..");
				System.out.println("----------------------------------------------------");
			} else{
				int msgIdx = -1;
				// Ask the user to choose which message to download.
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				boolean askSelection = true;
				while (askSelection) {
					System.out.println("----------------------------------------------------");
					System.out.println("         EbMS Message Query Result          ");
					System.out.println("----------------------------------------------------");
					for (int i = 0; i < msgList.size(); i++){
						String messageId = (String)((List)msgList.get(i)).get(0);
						String messageBox = (String)((List)msgList.get(i)).get(1);
						System.out.println(i + "\t| Message id : " + messageId +" | MessageBox: " + messageBox);
					}
					System.out.println("----------------------------------------------------");
					System.out.println("");
					System.out.print("Select message (0 - " + (msgList.size()-1) + "), -1 to exit: ");				
										
					try{					
						msgIdx = Integer.parseInt(br.readLine());
						if (msgIdx < -1) {
							System.out.println("Input must be greater than or equal to -1");
						} else if (msgIdx >= msgList.size()) {
							System.out.println("Input must be less than " + msgList.size());
						} else {
							askSelection = false;
						}
					}catch(Exception e){
						System.out.println("Invalid Input. It must be number");						
					}
				}
				
				if (msgIdx == -1){
					System.out.println("System exits ...");
					System.exit(0);
				}
				
				String messageId = (String)((List)msgList.get(msgIdx)).get(0);
				String messageBox = (String)((List)msgList.get(msgIdx)).get(1);
				
				/**
				 * 
				 */
				if(messageBox.equalsIgnoreCase("inbox")){
					


					File outDir = null;
					do{
						File currentDir = new File("");
						System.out.println("Currrent Dir: " + currentDir.getAbsolutePath());
						System.out.print("Please provide the folder to store the payload(s): ");
						outDir = new File(br.readLine().trim());
						if(outDir.exists() && !outDir.isDirectory()){
							System.out.println("That is not a valid directory");
							outDir = null;
							continue;
						}						
					}while(outDir == null);
					
					EBMSMessageData recvData = new EBMSMessageData();
					String host = 	requestData.getEndPoint().substring(0, 	requestData.getEndPoint().indexOf("/corvus"));
					host += "/corvus/httpd/ebms/receiver";
					recvData.setRecvEndpoint(host);

					//Add target MessageID value 
					recvData.setMessageIdForReceive(messageId);
					
					System.out.println("Initialize ebMS receiving web service client... ");
					
					EBMSMessageReceiver msgReceiver =	new EBMSMessageReceiver(logger, recvData);
					msgReceiver.setOutputDirectory(outDir.getAbsolutePath());
					System.out.println("Sending    ebMS receiving request ... for " + messageId);
					msgReceiver.run();
					
				}/*
				*
				*/
				else if(messageBox.equalsIgnoreCase("outbox")){

					EBMSStatusQueryData queryData = new EBMSStatusQueryData();
					String host = 	requestData.getEndPoint().substring(0, 	requestData.getEndPoint().indexOf("/corvus"));
					host += "/corvus/httpd/ebms/status";
					queryData.setSendEndpoint(host);
					queryData.setQueryMessageId(messageId);
				
					boolean historyQueryNeeded = false;
					EBMSStatusQuerySender statusQuerySender = new EBMSStatusQuerySender(logger, queryData);
					
					System.out.println("Sending    EBMS-status sending request ... ");
					statusQuerySender.run();			
								
					System.out.println();
					System.out.println("                    Sending Done:                   ");
					System.out.println("----------------------------------------------------");
					
					// Print respone data.
					EBMSStatusQueryResponseData response = statusQuerySender.getLastResponseData();
					System.out.println("Query Message ID          : " + response.getMessageId());
					System.out.println("Query Message Status      : " + response.getStatus());
					System.out.println("Query Message Status Desc : " + response.getStatusDescription());
					System.out.println("ACK   Message ID          : " + response.getACKMessageId());
					System.out.println("ACK   Message Status      : " + response.getACKStatus());
					System.out.println("ACK   Message Status Desc : " + response.getACKStatusDescription());
					
					System.out.println();
				}
			}
			System.out.println("----------------------------------------------------");
			System.out.println();
			System.out.println("Please view log for details .. ");			
											
		}catch(Exception e){
			e.printStackTrace(System.err);
		}	
	}

}
