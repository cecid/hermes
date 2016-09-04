package hk.hku.cecid.corvus.ws;

import javax.xml.soap.SOAPElement;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.util.SOAPUtilities;
import hk.hku.cecid.corvus.ws.data.Data;
import hk.hku.cecid.corvus.ws.data.DataFactory;
import hk.hku.cecid.corvus.ws.data.PermitRedownloadData;
import hk.hku.cecid.piazza.commons.util.PropertyTree;

public class EBMSPermitRedownloadSender extends PermitRedownloadServiceSender {

	public EBMSPermitRedownloadSender(FileLogger l, Data m) {
		super(l, m);
	}

	@Override
	protected String getNSURI() {
		return "http://service.ebms.edi.cecid.hku.hk/";
	}
	
	@Override
	public void onResponse() throws Exception {
		super.onResponse();
		
		SOAPElement elem = 
			SOAPUtilities.getElement(this.response, "messageId", getNSURI(), 0);
		
		if (elem != null){
			String msgId = elem.getValue();
			System.out.println("");
			System.out.println("Message [" + msgId +"] redownload permition request approved.");
			System.out.println(" = = = = = = = = = = = = = = = = = = = = = ");
		}
	}
	
	@Override
	public void onError(Throwable t) {
		// TODO Auto-generated method stub
		super.onError(t);
		
		/**
		 *  If there is output caught from SOAPFault
		 *  Print the error message on commmand prompt
		 */
		System.out.println("");
		System.out.println("Error caught on sever: " + t.getMessage());
		System.out.println(" = = = = = = = = = = = = = = = = = = = = = ");
	}
	
	/**
	 * The main method is for CLI mode.
	 */
	public static void main(String [] args){
		try{			
			if (args.length < 2){
				System.out.println("Usage: ebms-permitdl [config-xml] [log-path]");
				System.out.println();
				System.out.println("Example: ebms-permitdl " +
								   "./config/ebms-permitdl/ebms-request.xml " +
								   "./logs/ebms-permitdl.log");
				System.exit(1);
			}					
			System.out.println("----------------------------------------------------");
			System.out.println("           ebMS Permit Redownload Request Sender            ");
			System.out.println("----------------------------------------------------");

			// Initialize the logger.			
			System.out.println("Initialize Logger ... ");
			FileLogger logger = new FileLogger(new java.io.File(args[1]));

			// Initialize the query parameter.
			System.out.println("Importing  EBMS Redownload Permition Request parameters ... " + args[0] );			
			PermitRedownloadData data = 
				DataFactory.getInstance()
					.createEBMSPermitRedownloadDataFromXML(
						new PropertyTree(
							new java.io.File(args[0]).toURI().toURL()));									
			// Initialize the sender.
			System.out.println("Initialize EBMS Permit Redownload Request ... ");
			EBMSPermitRedownloadSender sender = new EBMSPermitRedownloadSender(logger, data);
			
			System.out.println("Sending EBMS Redownload Permition Request ... ");
			sender.run();
											
			System.out.println("Please view log for details .. ");
			
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	

}
