/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.http;

import java.util.HashMap;
import java.util.Map;

import hk.hku.cecid.piazza.commons.util.PropertyTree;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.AS2PartnershipData;
import hk.hku.cecid.corvus.ws.data.AS2AdminData;
import hk.hku.cecid.corvus.ws.data.DataFactory;

/**
 * The <code>AS2PartnershipSender</code> is a client service sender using HTTP protocol
 * for maintaining the set of AS2 Partnership in Hermes 2 Messaging Gateway.
 * <br/><br/>
 * To use it you have to provide the configuration instance called <code>AS2AdminData</code>.
 * it defines the URL end-point and credential for connecting to your Hermes 2 Restricted area.
 * <br/><br/>
 * An Example for adding partnership : 
 * <pre>
 * // Create an administrative data for configuration.
 * AS2AdminData adminData = new AS2AdminData();
 * adminData.setManagePartnershipEndpoint("Your H2O location");
 * adminData.setUsername("Your username for logging H2O");
 * adminData.setPassword("Your password for logging H2O");
 * // Create a partnership data for doing maintenance operation.
 * AS2PartnershipData pData = new AS2PartnershipData();
 * 			.
 * 			.
 * 			.
 * AS2PartnershipSender sender = new AS2PartnershipSender(someLogger, adminData, pData);
 * sender.setExecuteOperation(PartnershipOp.Add);
 * sender.run();
 * </pre>	  
 * 
 * <b>Note for setting the manage partnership end-point</b>
 * You should add <WEB-APP-NAME>/admin/as2/partnership to your H2O host. 
 * For example, 'http://localhost:8080/admin/as2/partnership'.
 * 
 * <b>Technical Information</b>
 * The <code>AS2PartnershipSender</code> will generate a HTTP multi-part request to 
 * the manage partnership end-point. The request includes all parameter extracted 
 * from the <code>AS2Partnership</code>, each of them is represented as either text/plain multi-part,
 * or application binary multi-part (for the <code>certificates</code>). The type 
 * of partnership operation to execute also append at the end of the HTTP request in 
 * a text multi-part form.
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   H2O 28/11
 * 
 * @see hk.hku.cecid.corvus.ws.data.AS2AdminData
 * @see hk.hku.cecid.corvus.ws.data.AS2PartnershipData
 * @see hk.hku.cecid.corvus.http.PartnershipOp
 */
public class AS2PartnershipSender extends PartnershipSender 
{
	/*
	 * A mapping containing the mapping from the partnership data hash key to 
	 * the HTTP multi-part request parameters.
	 */
	static final Map PARTNERSHIP_DATA_2_FROM_PARAM_NAME_MAPPING = new HashMap()
	{	
		private static final long serialVersionUID = 8744122089368239608L;
	{	
		// The array containing the partnership data to HTTP request parameters
		String [] pdata2param = 
		{
			"partnership_id"		, "disabled"			, "is_sync_reply"				, 
			"subject"				, "recipient_address"	, "is_hostname_verified"	 	,
			"receipt_address"		, "is_receipt_requested", "is_outbound_sign_required"	,			
			"is_outbound_encrypt_required"	, "is_outbound_compress_required", 
			"is_receipt_sign_required"		, "is_inbound_sign_required",
			"is_inbound_encrypt_required"	, "retries", 
			"retry_interval"		, "sign_algorithm"		, "encrypt_algorithm" ,
			"mic_algorithm"			, "as2_from"			, "as2_to" ,			
			"encrypt_cert"			, "verify_cert"	  
		};
		
		/* Create the partnership data to web form mapping */
		for (int i = 0; i < AS2PartnershipData.PARAM_KEY_SET.length; i++){
			this.put(AS2PartnershipData.PARAM_KEY_SET[i], pdata2param[i]);
		}
	}
	};
	
	/*
	 * A mapping containing the partnership operation to the actual action textual 
	 * in the web form at the HTTP request.
	 */
	static final Map PARTNERSHIP_OP_2_WORD = new HashMap()
	{
		private static final long serialVersionUID = -4083408040950444946L;
	{
		this.put(new Integer(PartnershipOp.ADD)	  , "add");
		this.put(new Integer(PartnershipOp.DELETE), "delete");
		this.put(new Integer(PartnershipOp.UPDATE), "update");		
	}};
	
	// The administration data for setting the end-point and authentication
	// private AS2AdminData ad;
	
	/**
	 * Explicit Constructor. Create an instance of <code>AS2PartnershipSender</code>.
	 * 
	 * @param logger The logger for log the sending process.
	 * @param ad The <code>AS2AdminData</code> for locating the HTTP end-point the request send to. 
	 * @param p  The <code>AS2PartnershipData</code> you want to add/delete/update.  
	 * 
	 * @throws NullPointerException 
	 * 			When <code>p</code> is null.
	 * 			When the manage partnership end-point from <code>ad</code> is null or empty.
	 */
	public AS2PartnershipSender(FileLogger logger, AS2AdminData ad, AS2PartnershipData p) 
	{
		super(logger, p);
		if (p == null)
			throw new NullPointerException("Missing 'partnershipData' for creating partnerhsip sender.");
		String endpoint = ad.getManagePartnershipEndpoint();
		if (endpoint == null || endpoint.equals(""))
			throw new NullPointerException("Missing 'Manage Partnership endpoint' in AS2 Admin Data.");		
		this.setServiceEndPoint(endpoint);			
		this.setBasicAuthentication(ad.getUsername(), new String(ad.getPassword()));
		this.setExecuteOperation(ad.getPartnershipOperation());		
		//this.ad = ad;
	}
	
	/* (non-Javadoc)
	 * @see hk.hku.cecid.corvus.http.PartnershipSender#getPartnershipMapping()
	 */
	public Map getPartnershipMapping() {
		return PARTNERSHIP_DATA_2_FROM_PARAM_NAME_MAPPING;
	}

	/* (non-Javadoc)
	 * @see hk.hku.cecid.corvus.http.PartnershipSender#getPartnershipOperationMapping()
	 */
	public Map getPartnershipOperationMapping() {
		return PARTNERSHIP_OP_2_WORD; 
	}
	
	/**
	 * The main method for executing the partnership operation request.
	 * 
	 * @see hk.hku.cecid.corvus.http.HttpSender#run()
	 */
	// Override to give some javadoc. 
	public void run() {
		super.run();
	}
	
	/**
	 * The main method is for CLI mode.
	 */
	public static void main(String [] args){
		try{			
			java.io.PrintStream out = System.out;
			
			if (args.length < 3){
				out.println("Usage: as2-partnership [partnership-xml] [config-xml] [log-path]");
				out.println();
				out.println("Example: as2-partnership ./config/as2-partnership.xml ./config/as2-partnership/as2-request.xml ./logs/as2-partnership.log");
				System.exit(1);
			}			
			
			out.println("----------------------------------------------------");
			out.println("       AS2 Partnership Maintainance Tool       ");
			out.println("----------------------------------------------------");

			// Initialize the logger.
			out.println("Initialize logger .. ");
			// The logger path is specified at the last argument.
			FileLogger logger = new FileLogger(new java.io.File(args[args.length-1]));			

			out.println("Importing AS2 partnership parameters ...");
			AS2PartnershipData pd = DataFactory.getInstance()
				.createAS2PartnershipFromXML(
					new PropertyTree(new java.io.File(args[0]).toURI().toURL()));			
			
			// Initialize the query parameter.
			out.println("Importing AS2  sending parameters ... ");	
			AS2AdminData acd = DataFactory.getInstance()
				.createAS2AdminDataFromXML(
					new PropertyTree(new java.io.File(args[1]).toURI().toURL()));			
					
			// Initialize the sender.
			out.println("Initialize AS2 HTTP data service client... "); 
			AS2PartnershipSender sender = new AS2PartnershipSender(logger, acd, pd);
			
			out.println("Sending    AS2 HTTP partnership maintenance request ... ");			
			sender.run();			
						
			out.println();
			out.println("                    Sending Done:                   ");
			out.println("----------------------------------------------------");
			out.println("The result status : " + sender.getStatus());
			out.println("Please view log for details .. ");			
		}
		catch(Exception e){
			e.printStackTrace(System.err);
		}		
	}
}
