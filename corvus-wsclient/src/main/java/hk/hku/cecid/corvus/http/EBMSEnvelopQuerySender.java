package hk.hku.cecid.corvus.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.EBMSMessageHistoryQuerySender;
import hk.hku.cecid.corvus.ws.data.DataFactory;
import hk.hku.cecid.corvus.ws.data.EBMSAdminData;
import hk.hku.cecid.corvus.ws.data.EBMSMessageHistoryRequestData;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.commons.io.IOHandler;

/** 
 * The <code>EBMSEnvelopQuerySender</code> is a client service sender using HTTP protocol
 * for query the message envelop (i.e EDI Header + payload) from the Hermes 2 Messaging Gateway.
 * <br/><br/>
 * 
 * To use it you have to provide the configuration instance called <code>AS2AdminData</code>.
 * it defines the URL end-point and credential for connecting to your Hermes 2 Restricted area.
 * <br/><br/>
 * An Example for adding partnership : 
 * <pre>
 * // Create an admin data for configuration.
 * EBMSAdminData adminData = new EBMSAdminData();
 * adminData.setManagePartnershipEndpoint("Your H2O location");
 * adminData.setUsername("Your username for logging H2O");
 * adminData.setPassword("Your password for logging H2O");
 * 
 * EBMSEnvelopQuerySender sender = new EBMSEnvelopQuerySender(someLogger, adminData, pData);
 * sender.setMessageCriteriaToDownload("The message id you want to query", "INBOX or OUTBOX");
 * sender.run();
 * InputStream ins = sender.getEnvelopStream();
 * // The envelop content ... process it.  
 * </pre>	  
 * 
 * <b>Note for setting the manage partnership end-point</b>
 * You should add <WEB-APP-NAME>/admin/ebms/partnership to your H2O host. 
 * For example, 'http://localhost:8080/admin/ebms/partnership'. 
 * 
 * Note that the client service does not guarantee <b>transactional</b> behavior meaning you are always 
 * able to down-load the envelop when invoking the client. (Different from the receiver Web service).   
 * <br/><br/>
 * 
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   H2O 28/11/2007
 * 
 * @see hk.hku.cecid.corvus.ws.data.EBMSAdminData
 */
public class EBMSEnvelopQuerySender extends EnvelopQuerySender  {

    // Command Prompt Input Reader
	
    /* The mapping from the standardized message box representation to AS2 proprietary representation. */
    private static Map MSGBOX_MAPPING = new HashMap()
	{
	    private static final long serialVersionUID = -984835701813212179L;
	    {
		this.put("INBOX", "inbox");
		this.put("OUTBOX", "outbox");
	    }
	};
					
    /**
     * Explicit Constructor. Create an instance of <code>AS2EnvelopQuerySender</code>.
     * 
     * @param logger The logger for log the sending process.
     * @param ad The <code>EBMSAdminData</code> for locating the HTTP end-point the request send to.   
     * 
     * @throws NullPointerException 
     * 			When <code>p</code> is null.
     * 			When the manage partnership end-point from <code>ad</code> is null or empty.
     */
    public EBMSEnvelopQuerySender(FileLogger logger, EBMSAdminData ad) {
	super(logger, ad, ad.getUsername(), new String(ad.getPassword()));
	String endpoint = ad.getEnvelopQueryEndpoint();
	if (endpoint == null || endpoint.equals(""))
	    throw new NullPointerException("Missing 'Envelop Partnership endpoint' in EBMS Admin Data.");		
	this.setServiceEndPoint(endpoint);			
	this.setMessageCriteriaToDownload(ad.getMessageIdCriteria(), ad.getMessageBoxCriteria());
    }
	
    /* (non-Javadoc)
     * @see hk.hku.cecid.corvus.http.EnvelopQuerySender#getMessageBoxMapping()
     */
    protected Map getMessageBoxMapping() {
	return MSGBOX_MAPPING;
    }	
	
	
    private static List listAvailableMessage(EBMSMessageHistoryRequestData queryData, FileLogger logger){
		
	EBMSMessageHistoryQuerySender historyQuery = 
	    new EBMSMessageHistoryQuerySender(logger, queryData);
	historyQuery.run();
		
	List msgList = historyQuery.getAvailableMessages();
		
	return msgList;
    }
	
    private static int promptForSelection(List msgList){
		
	System.out.println();
	System.out.println("Avaliable message envelop are listed below:");	
	for(int index =0; index < msgList.size(); index++){
	    String messageId = (String)((List)msgList.get(index)).get(0);
	    String messageBox = (String)((List)msgList.get(index)).get(1);
	    System.out.println(index + "\t|  MessageBox: " + messageBox  +"  | Message ID : " + messageId);
	}
	System.out.println("End of List.");
	System.out.println("");
	System.out.println("");
	System.out.print("Please enter your selection (-1 for exit): ");
	BufferedReader consoleReader =
	    new BufferedReader(new InputStreamReader(System.in));
		
	int selection = -1;
		
	boolean readSucess = false;
	while(!readSucess){
	    try {
		String input = consoleReader.readLine().trim();
		if(input == null || input.equals(""))
		    return -1;
				
		selection = Integer.parseInt(input);
		readSucess = true;
		break;
	    } catch (Exception e) {
		System.out.println("");
		System.out.println("Input Value not valid! ");
		continue;
	    }
	}
	return selection;
    }
	
    /**
     * The main method is for CLI mode.
     */
    public static void main(String [] args){
	try{			
	    java.io.PrintStream out = System.out;
			
	    if (args.length < 2){
		out.println("Usage: ebms-envelop [config-xml] [log-path]");
		out.println();
		out.println("Example: ebms-envelop ./config/ebms-envelop/ebms-request.xml ./logs/ebms-envelop.log");
		System.exit(1);
	    }
			
	    out.println("------------------------------------------------------");
	    out.println("      EBMS Message Envelop Queryer       ");
	    out.println("------------------------------------------------------");

	    // Initialize the logger.
	    out.println("Initialize logger .. ");
	    // The logger path is specified at the last argument.
	    FileLogger logger = new FileLogger(new File(args[args.length-1]));			

			
	    // Initialize the query parameter.
	    out.println("Importing EBMS administrative sending parameters ... ");	
	    EBMSAdminData acd = DataFactory.getInstance()
		.createEBMSAdminDataFromXML(
					    new PropertyTree(new File(args[0]).toURI().toURL()));			
				
	    // Initialize the sender.
	    out.println("Initialize EBMS HTTP data service client... ");
			
	    boolean historyQueryNeeded = false;
	    EBMSMessageHistoryRequestData queryData =new EBMSMessageHistoryRequestData();
	    if(acd.getMessageIdCriteria()==null || 
	       acd.getMessageIdCriteria().trim().equals("")){

		historyQueryNeeded = true;
				
		// print command prompt
		out.println("No messageID was specified!");
		out.println("Start querying message repositry ...");
				
		String endpoint = acd.getEnvelopQueryEndpoint();
		String host = endpoint.substring(0, endpoint.indexOf("/corvus"));
		host += "/corvus/httpd/ebms/msg_history";
		queryData.setEndPoint(host);
	    }/*
	       If the user has entered message id but no messagebox, 
	       using the messageid as serach criteria and as 
	       user to chose his target message
	     */
	    else if(acd.getMessageBoxCriteria()==null || 
		    acd.getMessageBoxCriteria().trim().equals("")){
				
		historyQueryNeeded = true;
				
		// print command prompt
		out.println("Message Box value haven't specified.");
		out.println("Start query message whcih matched with messageID: " + 
			    acd.getMessageIdCriteria());
				
		String endpoint = acd.getEnvelopQueryEndpoint();
		String host = endpoint.substring(0, endpoint.indexOf("/corvus"));
		host += "/corvus/httpd/ebms/msg_history";
				
		queryData.setEndPoint(host);				
		queryData.setMessageId(acd.getMessageIdCriteria());
	    }
	    //Debug Message
	    System.out.println("history Endpoint: " + queryData.getEndPoint());
	    System.out.println("Repositry Endpoint: " + acd.getEnvelopQueryEndpoint());
			
	    if(historyQueryNeeded){
		List msgList = listAvailableMessage(queryData, logger);
				
		if(msgList == null || msgList.size()==0){
		    out.println();
		    out.println();
		    out.println("No stream data found in repositry...");
		    out.println("Please view log for details .. ");		
		    return;
		}
				
		int selection = promptForSelection(msgList);
				
		if(selection == -1){
		    return;
		}
				
				
		String messageID = (String)((List)msgList.get(selection)).get(0);
		String messageBox = (String)((List)msgList.get(selection)).get(1);
		acd.setMessageIdCriteria(messageID);
		acd.setMessageBoxCriteria(messageBox.toUpperCase());
		out.println();
		out.println();
		out.println("Start download targeted message envelop ...");
	    }
			
	    EBMSEnvelopQuerySender sender = new EBMSEnvelopQuerySender(logger, acd);
			
	    out.println("Sending    EBMS HTTP Envelop Query request ... ");			
	    sender.run();			
						
	    out.println();
	    out.println("                    Sending Done:                   ");
	    out.println("----------------------------------------------------");
	    out.println("The Message Envelope : ");
	    InputStream eins = sender.getEnvelopStream();
	    if (eins.available() == 0){
		out.println("No stream data found.");
		out.println("The message envelop does not exist for message id " + 
			    sender.getMessageIdToDownload() +
			    " and message box " +  
			    sender.getMessageBoxToDownload());
	    } else 
		IOHandler.pipe(sender.getEnvelopStream(), out);
			
	    out.println("Please view log for details .. ");			
	}
	catch(Exception e){
	    e.printStackTrace(System.err);
	}		
    }
}
