package hk.hku.cecid.corvus.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.KVPairData;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.AS2MessageHistoryQuerySender;
import hk.hku.cecid.corvus.ws.data.AS2AdminData;
import hk.hku.cecid.corvus.ws.data.AS2MessageHistoryRequestData;
import hk.hku.cecid.corvus.ws.data.DataFactory;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.util.PropertyTree;

/** 
 * The <code>AS2EnvelopQuerySender</code> is a client service sender using HTTP protocol
 * for query the message envelop (i.e EDI Header + payload) from the Hermes 2 Messaging Gateway.
 * <br/><br/>
 * To use it you have to provide the configuration instance called <code>AS2AdminData</code>.
 * it defines the URL end-point and credential for connecting to your Hermes 2 Restricted area.
 * <br/><br/>
 * An Example for adding partnership : 
 * <pre>
 * // Create an admin data for configuration.
 * AS2AdminData adminData = new AS2AdminData();
 * adminData.setManagePartnershipEndpoint("Your H2O location");
 * adminData.setUsername("Your username for logging H2O");
 * adminData.setPassword("Your password for logging H2O");
 * 
 * AS2EnvelopQuerySender sender = new AS2EnvelopQuerySender(someLogger, adminData, pData);
 * sender.setMessageCriteriaToDownload("The message id you want to query", "INBOX or OUTBOX");
 * sender.run();
 * InputStream ins = sender.getEnvelopStream();
 * // The envelop content ... process it.  
 * </pre>	  
 * 
 * <b>Note for setting the manage partnership end-point</b>
 * You should add <WEB-APP-NAME>/admin/as2/partnership to your H2O host. 
 * For example, 'http://localhost:8080/admin/as2/partnership'. 
 * 
 * Note that the client service does not guarantee <b>transactional</b> behavior meaning you are always 
 * able to down-load the envelop when invoking the client. (Different from the receiver Web service).   
 * <br/><br/>
 * 
 * @author 	Twinsen Tsang
 * @version 1.0.0 $STABLE$
 * @since   H2O 28/11/2007
 * 
 * @see hk.hku.cecid.corvus.ws.data.AS2AdminData
 */
public class AS2EnvelopQuerySender extends EnvelopQuerySender 
{	
    /* The mapping from the standardized message box representation to AS2 proprietary representation. */
    private static Map MSGBOX_MAPPING = new HashMap()
	{
	    private static final long serialVersionUID = -4738564483228612026L;
	    {
		this.put("INBOX", "IN");
		this.put("OUTBOX", "OUT");
	    }
	};
		
    protected static final String DL_RECEIPT_FORM_PARAM	= "is_download_receipt";
			
    // TODO: Does not support configure to down-load receipt.
    private boolean isDownloadReceipt = false;
	
    /**
     * Explicit Constructor. Create an instance of <code>AS2EnvelopQuerySender</code>.
     * 
     * @param logger The logger for log the sending process.
     * @param ad The <code>AS2AdminData</code> for locating the HTTP end-point the request send to. 
     * 
     * @throws NullPointerException 
     * 			When <code>p</code> is null.
     * 			When the manage partnership end-point from <code>ad</code> is null or empty.
     */
    public AS2EnvelopQuerySender(FileLogger logger, AS2AdminData ad) {
	super(logger, ad, ad.getUsername(), new String(ad.getPassword()));
	String endpoint = ad.getEnvelopQueryEndpoint();
	if (endpoint == null || endpoint.equals(""))
	    throw new NullPointerException("Missing 'Envelop Partnership endpoint' in AS2 Admin Data.");		
	this.setServiceEndPoint(endpoint);			
	this.setMessageCriteriaToDownload(ad.getMessageIdCriteria(), ad.getMessageBoxCriteria());
    }
	
    /* (non-Javadoc)
     * @see hk.hku.cecid.corvus.http.EnvelopQuerySender#getMessageBoxMapping()
     */
    protected Map getMessageBoxMapping() {
	return MSGBOX_MAPPING;
    }
	
    /* (non-Javadoc)
     * @see hk.hku.cecid.corvus.http.EnvelopQuerySender#onCreateRequest()
     */
    protected HttpRequestBase onCreateRequest() throws Exception {
	HttpPost post = (HttpPost) super.onCreateRequest();
	HttpEntity entity = post.getEntity();
	
	List<NameValuePair> params = URLEncodedUtils.parse(entity);
	params.add(new BasicNameValuePair(DL_RECEIPT_FORM_PARAM, String.valueOf(isDownloadReceipt)));
	post.setEntity(new UrlEncodedFormEntity(params));
		
	return post;
    }
	
    private static List listAvailableMessage(AS2MessageHistoryRequestData queryData, FileLogger logger){
		
	AS2MessageHistoryQuerySender historyQuery = 
	    new AS2MessageHistoryQuerySender(logger, queryData);
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
		out.println("Usage: as2-envelop [config-xml] [log-path]");
		out.println();
		out.println("Example: as2-envelop ./config/as2-envelop/as2-request.xml ./logs/as2-envelop.log");
		System.exit(1);
	    }			
			
	    out.println("------------------------------------------------------");
	    out.println("       AS2 Envelop Queryer       ");
	    out.println("------------------------------------------------------");

	    // Initialize the logger.
	    out.println("Initialize logger .. ");
	    // The logger path is specified at the last argument.
	    FileLogger logger = new FileLogger(new File(args[args.length-1]));			

	    // Initialize the query parameter.
	    out.println("Importing AS2 administrative sending parameters ... ");	
	    AS2AdminData acd = DataFactory.getInstance()
		.createAS2AdminDataFromXML(
					   new PropertyTree(new File(args[0]).toURI().toURL()));	
			
	    boolean historyQueryNeeded = false;
	    AS2MessageHistoryRequestData queryData =new AS2MessageHistoryRequestData();
	    if(acd.getMessageIdCriteria()==null || 
	       acd.getMessageIdCriteria().trim().equals("")){

		historyQueryNeeded = true;
				
		// print command prompt
		out.println("No messageID was specified!");
		out.println("Start querying message repositry ...");
				
		String endpoint = acd.getEnvelopQueryEndpoint();
		String host = endpoint.substring(0, endpoint.indexOf("/corvus"));
		host += "/corvus/httpd/as2/msg_history";
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
		host += "/corvus/httpd/as2/msg_history";
				
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
					
	    // Initialize the sender.
	    out.println("Initialize AS2 HTTP data service client... "); 
	    AS2EnvelopQuerySender sender = new AS2EnvelopQuerySender(logger, acd);
			
	    out.println("Sending    AS2 HTTP Envelop Query request ... ");			
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
