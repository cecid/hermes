/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.ws;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.util.SOAPUtilities;
import hk.hku.cecid.corvus.ws.data.AS2MessageData;
import hk.hku.cecid.corvus.ws.data.AS2MessageHistoryRequestData;
import hk.hku.cecid.corvus.ws.data.AS2StatusQueryData;
import hk.hku.cecid.corvus.ws.data.AS2StatusQueryResponseData;
import hk.hku.cecid.corvus.ws.data.DataFactory;
import hk.hku.cecid.piazza.commons.util.PropertyTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class AS2MessageHistoryQuerySender extends SOAPSender{
	
		public static String NAMESPACE = "http://service.as2.edi.cecid.hku.hk/";
		private ArrayList resultMessages = new ArrayList();

		public AS2MessageHistoryQuerySender(FileLogger l,AS2MessageHistoryRequestData m) {
			super(l, m);			
			AS2MessageHistoryRequestData d = (AS2MessageHistoryRequestData) m;		
			// 	Setup the sender config.
			//	this.setLoopTimes(1);
			this.setServiceEndPoint(d.getEndPoint());	
		}
		
		public void onStart(){
			super.onStart();
			if (this.log != null){
				// Log all information for this sender.
				this.log.log("AS2 Message History Query init at " + new Date().toString());
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
			if (!(this.properties instanceof AS2MessageHistoryRequestData))
				throw new Exception("Invalid AS2 Quest Request class data");
			
			if (this.properties == null)
				throw new Exception("Invalid AS2 Quest Request: Object null");
		
			AS2MessageHistoryRequestData 	requestData = (AS2MessageHistoryRequestData) this.properties;
			
			this.addRequestElementText(AS2MessageHistoryRequestData.PARAM_KEY_SET[0] , requestData.getMessageBox(), NS_PREFIX, NAMESPACE);
			this.addRequestElementText(AS2MessageHistoryRequestData.PARAM_KEY_SET[1] , requestData.getStatus(), 	NS_PREFIX, NAMESPACE);
				
			this.addRequestElementText(AS2MessageHistoryRequestData.PARAM_AS2_KEY_SET[0] , requestData.getMessageId(),NS_PREFIX, NAMESPACE);
			this.addRequestElementText(AS2MessageHistoryRequestData.PARAM_AS2_KEY_SET[1] , requestData.getAS2FromParty(), NS_PREFIX, NAMESPACE);
			this.addRequestElementText(AS2MessageHistoryRequestData.PARAM_AS2_KEY_SET[2] , requestData.getAS2ToParty(), NS_PREFIX, NAMESPACE);			
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
						
						// MessageBox
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
					System.out.println("Usage: as2-history [config-xml] [log-path]");
					System.out.println();
					System.out.println("Example: as2-history " +
									   "./config/as2-config/as2-request.xml " +
									   "./logs/as2-config.log ");
					System.exit(1);
				}					
				System.out.println("----------------------------------------------------");
				System.out.println("         AS2 Message History Web Service Client      ");
				System.out.println("----------------------------------------------------");

				// Initialize the logger.			
				System.out.println("Initialize Logger ... ");
				FileLogger logger = new FileLogger(new java.io.File(args[1]));
				
				// Initialize the query parameter.
				System.out.println("Importing  AS2 config parameters ... "  + args[0]);			
				AS2MessageHistoryRequestData requestData = 
					DataFactory.getInstance()
						.createAs2MessageHistoryQueryDataFromXML(new PropertyTree(new java.io.File(args[0]).toURI().toURL()));	
				
												
				// Initialize the sender.
				System.out.println("Initialize AS2 messsage history queryer ... ");
				AS2MessageHistoryQuerySender sender = new AS2MessageHistoryQuerySender(logger, requestData);
				// Send the message.
				System.out.println("Sending AS2 message history query request ... ");
				sender.run();
				
				// Get the receiver list.
				List msgList = sender.getAvailableMessages();
				System.out.println();
				System.out.println("                    Sending Done:                   ");
				System.out.println("----------------------------------------------------");
				System.out.println("         AS2 Message that are matched          ");
				System.out.println("----------------------------------------------------");
				if (msgList.size() == 0){
					System.out.println("No message found ..");
				} else{
					System.out.println("No. of message: " + msgList.size());
					
					int msgIdx = -1;
					// Ask the user to choose which message to download.
					BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					boolean askSelection = true;
					while (askSelection) {
						for (int i = 0; i < msgList.size(); i++){
							String messageId = (String)((List)msgList.get(i)).get(0);
							String messageBox = (String)((List)msgList.get(i)).get(1);
							System.out.println(i + "\t| Message id : " + messageId +"  MessageBox: " + messageBox);
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
						AS2MessageData msgData = new AS2MessageData();
						String host = 	requestData.getEndPoint().substring(0, 	requestData.getEndPoint().indexOf("/corvus"));
						host += "/corvus/httpd/as2/receiver";
						msgData.setRecvEndpoint(host);
						msgData.setMessageIdForReceive(messageId);
						
						//Ask for output folder
						File outDir = null;
						do{
							File currentDir = new File("");
							System.out.println("Currrent Dir: " + currentDir.getAbsolutePath());
							System.out.print("Please provide the folder to store the payload(s): ");
							
							
							outDir = new File( br.readLine().trim());
							if(outDir.exists() && !outDir.isDirectory()){
								System.out.println("That is not a valid directory");
								outDir = null;
								continue;
							}
						}while(outDir == null);
						
						AS2MessageReceiver msgReceiver = new AS2MessageReceiver(logger, msgData);
						msgReceiver.setOutputDirectory(outDir.getAbsolutePath());
						msgReceiver.run();
						
					}else if(messageBox.equalsIgnoreCase("outbox")){
						AS2StatusQueryData statusQuery = new AS2StatusQueryData();
						String host = 	requestData.getEndPoint().substring(0, 	requestData.getEndPoint().indexOf("/corvus"));
						host += "/corvus/httpd/as2/status";
						statusQuery.setSendEndpoint(host);
						statusQuery.setQueryMessageId(messageId);
						
						AS2StatusQuerySender statusQueryer = new AS2StatusQuerySender(logger, statusQuery);
						statusQueryer.run();
						AS2StatusQueryResponseData response = statusQueryer.getLastResponseData();
						System.out.println("Query Message ID          : " + response.getMessageId());
						System.out.println("Query Message Status      : " + response.getStatus());
						System.out.println("Query Message Status Desc : " + response.getStatusDescription());
						System.out.println("ACK   Message ID          : " + response.getACKMessageId());
						System.out.println("ACK   Message Status      : " + response.getACKStatus());
						System.out.println("ACK   Message Status Desc : " + response.getACKStatusDescription());
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

