package hk.hku.cecid.edi.as2.service;

import org.w3c.dom.Element;
import javax.xml.soap.SOAPElement;

import hk.hku.cecid.piazza.commons.soap.SOAPRequestException;
import hk.hku.cecid.piazza.commons.soap.WebServicesAdaptor;
import hk.hku.cecid.piazza.commons.soap.WebServicesRequest;
import hk.hku.cecid.piazza.commons.soap.WebServicesResponse;
import hk.hku.cecid.piazza.commons.module.ActiveTaskModule;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

import hk.hku.cecid.edi.as2.AS2PlusProcessor;

/**
 * This is a AS2 Configuration Web Service. 
 * 
 * The XML SOAP Message should be liked this.
 * 
 * <pre>
 * 	&lt;active-module-status> true | false &lt;/active-module-status>
 * 	&lt;inmessage-interval>15000 &lt;/inmessage-interval>
 * 	&lt;inmessage-maxthread>0&lt;/inmessage-maxthread>
 * 	&lt;outmessage-interval>15000&lt;/outmessage-interval>
 * 	&lt;outmessage-maxthread>0&lt;/outmessage-maxthread>
 * 	&lt;outpayload-interval>15000&lt;/outpayload-interval>
 * 	&lt;outpayload-maxthread>0&lt;/outpayload-maxthread>
 * <pre>
 * 
 * @author 	Twinsen Tsang. 
 * @version 1.0.0 
 * @since	1.0.0
 */
public class AS2ConfigService extends WebServicesAdaptor
{
	 private static final String ACTIVEMODULE_STATUS 	 = "active-module-status";
	 
	 private static final String [] AS2_MODS_EI = {"inmessage-interval"
		 										  ,"outmessage-interval"
		 										  ,"outpayload-interval"};
	 private static final String [] AS2_MODS_MAXTHREAD = {"inmessage-maxthread"
		 												 ,"outmessage-maxthread"
		 												 ,"outpayload-maxthread"};
	 
	 private static final String [] AS2_MODS_NAME  = {"as2.core.incoming.message"
		 											 ,"as2.core.outgoing.message"
		 											 ,"as2.core.outgoing.payload"};
	
	 public void serviceRequested(WebServicesRequest request,
	            WebServicesResponse response) throws SOAPRequestException
	 {
	        Element[] bodies 			= request.getBodies();
	        
	        try
	        {	        		        		        
	        	long [] as2ModEI 		= new long[AS2_MODS_NAME.length];
	        	int [] as2ModMaxThread	= new int[AS2_MODS_NAME.length];
	        	boolean [] as2ModFlag	= new boolean[AS2_MODS_NAME.length];
	        		        		        	
	        	boolean activeModuleStatus 	= Boolean.valueOf	(this.getText(bodies, ACTIVEMODULE_STATUS)).booleanValue();
	        	
	        	for (int i = 0; i < AS2_MODS_NAME.length; i++){
	        		as2ModMaxThread[i] 	= StringUtilities.parseInt(this.getText(bodies, AS2_MODS_MAXTHREAD[i]), -1);
	        		as2ModEI[i] 		= StringUtilities.parseInt(this.getText(bodies, AS2_MODS_EI[i]), -1);
	        						        	
		        	AS2PlusProcessor.getInstance().getLogger().info("test");
		        	
		        	if (as2ModMaxThread[i] != -1 && as2ModEI[i] != -1){
		        		as2ModFlag[i] 		= true;			        		
		        		AS2PlusProcessor.getInstance().getLogger().info("thread" + as2ModMaxThread[i]);
		        		AS2PlusProcessor.getInstance().getLogger().info("ei" + as2ModEI[i]);
		        	}		        	
	        	}
	        		        		        		        		        	               		        		        		    				       
		        if (activeModuleStatus)
		        	AS2PlusProcessor.getInstance().getModuleGroup().startActiveModules();		        	
		        else
		        	AS2PlusProcessor.getInstance().getModuleGroup().stopActiveModules();
		        
		        AS2PlusProcessor.getInstance().getLogger().info("AS2 Active Modules new status: " + String.valueOf(activeModuleStatus));
		        
		        ActiveTaskModule am = null;		        		       	        		       
		        
		        for (int i = 0; i < AS2_MODS_NAME.length; i++)
		        {
		        	// If both execution interval and thread exist.
		        	if (as2ModFlag[i]){
			        	am = (ActiveTaskModule) (AS2PlusProcessor.getInstance().getModuleGroup().getModule(AS2_MODS_NAME[i]));
			        	if (am != null && am.getMonitor() != null)
			        	{
			        		am.getMonitor().setMaxThreadCount(as2ModMaxThread[i]);	
			        		am.setExecutionInterval(as2ModEI[i]);
			        		AS2PlusProcessor.getInstance().getLogger().info(AS2_MODS_NAME[i] 
			        		                          +" Thread set to: " 
			        							  	  + am.getMonitor().getMaxThreadCount()
			        							  	  +" with interval "
			        							  	  + as2ModEI[i]);		        	
			        	}
		        	}
		        }	        		      		        			        		        		        		        	        		        				     
	            generateReply(response
	            			 ,"Success"
	            			 ,"Success in configuring AS2 Configuration"); 
	        }
	        catch(Exception e)
	        {
	        	AS2PlusProcessor.getInstance().getLogger().debug("Configuration Error", e);
	        	generateReply(response
	        				 ,"Fail"
	        				 ,"Error in configuring AS2 Configuration");
	        }	        	        	        	        	        	       	        	      	       
	    }

	    private void generateReply(WebServicesResponse response, String status,
	            String statusDescription) throws SOAPRequestException 
	    {
	        try {
	        	SOAPElement responseElement = createText("status", status, "http://service.as2.edi.cecid.hku.hk/");
	            response.setBodies(new SOAPElement[]{responseElement});	           
	        } catch (Exception e) {
	            throw new SOAPRequestException("Unable to generate reply message", e);
	        }
	    }
	    
	    public String replaceNullToEmpty(String value) {
	        if (value == null) {
	            return new String("");
	        } else {
	            return value;
	        }
	    }
	   
	    protected boolean isCacheEnabled() {
	        return false;
	    }
}
