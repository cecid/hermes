package hk.hku.cecid.ebms.spa.service;

import org.w3c.dom.Element;
import javax.xml.soap.SOAPElement;

import hk.hku.cecid.piazza.commons.soap.SOAPRequestException;
import hk.hku.cecid.piazza.commons.soap.WebServicesAdaptor;
import hk.hku.cecid.piazza.commons.soap.WebServicesRequest;
import hk.hku.cecid.piazza.commons.soap.WebServicesResponse;
import hk.hku.cecid.piazza.commons.module.ActiveTaskModule;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

import hk.hku.cecid.ebms.spa.EbmsProcessor;

/*
 * This is a Ebms Configuration Web Service. 
 * 
 * The XML SOAP Message should be liked this.
 * 
 * <active-module-status> true | false </active-module-status>
 * <outcollector-maxthread>15000</outcollector-maxthread>
 * <outcollector-interval>0</inmessage-maxthread>
 * <incollecotor-maxthread >15000</incollector-maxthread>
 * <incollector-interval>0</incollector-interval>
 * <mailcollector-maxthread>15000</mailcollector-maxthread>
 * <mailcollector-interval>0</mailcollector-interval >
 * 
 * @author Twinsen Tsang. 
 * @version 1.0
 */
public class EbmsConfigService extends WebServicesAdaptor
{
	 private static final String ACTIVEMODULE_STATUS 	  = "active-module-status";

	 private static final String [] EBMS_MODS_MAXTHREAD	 = {"incollector-maxthread"
		 												   ,"outcollector-maxthread"
		 												   ,"mailcollector-maxthread"};
	 
	 private static final String [] EBMS_MODS_EI		 = {"incollector-interval"
		 												   ,"outcollector-interval"
		 												   ,"mailcollector-interval"};
	 
	 private static final String [] EBMS_MODS_NAMES 	 = {"ebms.mail-collector"
		 												   ,"ebms.outbox-collector"
		 												   ,"ebms.inbox-collector"};
	
	 public void serviceRequested(WebServicesRequest request,
	            WebServicesResponse response) throws SOAPRequestException
	 {
	        Element[] bodies 			= request.getBodies();
	        
	        try
	        {
	        	long [] ebmsModEI 		= new long[EBMS_MODS_NAMES.length];
	        	int [] ebmsModMaxThread	= new int[EBMS_MODS_NAMES.length];
	        	boolean [] ebmsModFlag	= new boolean[EBMS_MODS_NAMES.length];
	        		        		        	
	        	boolean activeModuleStatus 	= Boolean.valueOf	(this.getText(bodies, ACTIVEMODULE_STATUS)).booleanValue();
	        	
	        	for (int i = 0; i < EBMS_MODS_NAMES.length; i++){
	        		ebmsModMaxThread[i]	= StringUtilities.parseInt(this.getText(bodies, EBMS_MODS_MAXTHREAD[i]), -1);
	        		ebmsModEI[i] 		= StringUtilities.parseInt(this.getText(bodies, EBMS_MODS_EI[i]), -1);
	        						        			        	
		        	if (ebmsModMaxThread[i] != -1 && ebmsModEI[i] != -1){
		        		ebmsModFlag[i] 		 = true;			        		
		        	}		        	
	        	}
	        			        		        
		        if (activeModuleStatus)
		        	EbmsProcessor.getModuleGroup().startActiveModules();		        	
		        else
		        	EbmsProcessor.getModuleGroup().stopActiveModules();		        	
		        
		        EbmsProcessor.core.log.info("Active Modules new status:" + String.valueOf(activeModuleStatus));
		        		        		        		        		        
		        ActiveTaskModule am = null;		        		       	        		       
		        
		        for (int i = 0; i < EBMS_MODS_NAMES.length; i++)
		        {
		        	// If both execution interval and thread exist.
		        	if (ebmsModFlag[i]){
			        	am = (ActiveTaskModule) (EbmsProcessor.getModuleGroup().getModule(EBMS_MODS_NAMES[i]));
			        	if (am != null && am.getMonitor() != null)
			        	{
			        		am.getMonitor().setMaxThreadCount(ebmsModMaxThread[i]);	
			        		am.setExecutionInterval(ebmsModEI[i]);
			        		EbmsProcessor.core.log.info(EBMS_MODS_NAMES[i] 
			        		                           +" Thread set to: " 
			        							  	   + am.getMonitor().getMaxThreadCount()
			        							  	   +" with interval "
			        							  	   + ebmsModEI[i]);		        	
			        	}
		        	}
		        }	        		      
		        
	            generateReply(response
	            			 ,"Success"
	            			 ,"Success in configuring Ebms Configuration"); 
	        }
	        catch(Exception e)
	        {
	        	EbmsProcessor.core.log.debug("Configuration Error", e);
	        	generateReply(response
	        				 ,"Fail"
	        				 ,"Error in configuring Ebms Configuration");
	        }	        	        	        	        	        	       	        	      	       
	    }

	    private void generateReply(WebServicesResponse response, String status,
	            String statusDescription) throws SOAPRequestException {
	        try {
	        	SOAPElement responseElement = createText("status", status, "http://service.ebms.edi.cecid.hku.hk/");
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
