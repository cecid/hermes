/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.ws.data;

/**
 * The <code>EBMSPartnershipData</code> is a duplicate-class data-structures for representing
 * a EBMS partnership DVO object. It reduces the dependency from "corvus-ebms-core.jar" 
 * which changed quite frequently. 
 * 
 * @author Twinsen Tsang
 * @version 1.0.0 
 */
public class EBMSPartnershipData extends KVPairData{	
		
	/** This is the key set for XML serialization / de-serialization. */
	public static final String [] PARAM_KEY_SET = 
	{
		"id"                , "cpaId"            , "service", 
		"action"			, "disabled"		 , "syncReplyMode"   , 
		"transportEndpoint" , "transportProtocol", "ackRequested"    , 
		"ackSignRequested"  , "dupElimination"	 , "actor"           ,	
		"messageOrder"		, "retries"          , "retryInterval"   , 
		"signRequested"     , "dsAlgorithm"      , "mdAlgorithm"	 , 
		"encryptRequested"  , "encryptAlgorithm" , 
		"signCert"          , "encryptCert"      , "isHostnameVerified"		
	};
	
	/**
	 * This is the parameter prefix for serialization / de-serialization.<br/><br/>
	 */
	public static final String PARAM_PREFIX  = "/partnership/ebms";
	
	static final Class [] PARAM_CLASS_SET = 
	{
		String.class		, String.class,		String.class,
		String.class		, String.class,		String.class,
		String.class		, String.class,		String.class,
		String.class		, String.class,		String.class,
		String.class		, String.class,		String.class,
		String.class		, String.class,		String.class,		
		String.class		, String.class,				
		byte[].class		, byte[].class,		String.class		
	};
	
	/**
	 * Default constructor.
	 */
	public EBMSPartnershipData()
	{	
		super(PARAM_KEY_SET.length);
	}
	
	/**
     * @return Returns the channel ID.
     */
    public String getPartnershipId(){
    	return (String)props.get("id");
    }
	
	/**
     * @param partnershipId The partnership ID to set.
     */
    public void setPartnershipId(String partnershipId){
    	props.put("id", partnershipId);
    }

    /**
     * @return Returns the CPA ID.
     */
    public String getCpaId(){
    	return (String) props.get("cpaId");
    }
    
    /**
     * @param cpaId
     *            The CPA ID to set.
     */
    public void setCpaId(String cpaId){
    	props.put("cpaId", cpaId);
    }

    /**
     * @return Returns the service.
     */
    public String getService(){
    	return (String) props.get("service");
    }
    
    /**
     * @param service
     *            The service to set.
     */
    public void setService(String service){   
    	props.put("service",service);
    }
    
    /**
     * @return Returns the action.
     */
    public String getAction(){
    	return (String) props.get("action");
    }
    
    /**
     * @param action
     *            The action to set.
     */
    public void setAction(String action){
    	props.put("action", action); 
    }
    
    /**
     * @return Returns the disabled.
     */
    public String getDisabled(){
    	return (String) props.get("disabled");
    }

    /**
     * @param disabled
     *            The disabled to set.
     */
    public void setDisabled(String disabled){
    	props.put("disabled", disabled);
    } 
    
    /**
     * @return Returns the syncReplyMode.
     */
    public String getSyncReplyMode(){
    	return (String) props.get("syncReplyMode");
    }    
    
    /**
     * @param syncReplyMode
     *            The syncReplyMode to set.
     */
    public void setSyncReplyMode(String syncReplyMode){
    	props.put("syncReplyMode", syncReplyMode);
    }

    /**
     * @return The transportEndpoint.
     */
    public String getTransportEndpoint(){
    	return (String) props.get("transportEndpoint");
    }  
    
    /**
     * @param transportEndpoint
     *            The transportEndpoint to set.
     */
    public void setTransportEndpoint(String transportEndpoint){
    	props.put("transportEndpoint", transportEndpoint);
    }
    
    /**
     * @return The transportProtocol.
     */
    public String getTransportProtocol(){
    	return (String) props.get("transportProtocol");
    }

    /**
     * @param transportProtocol
     *            The transportProtocol to set.
     */
    public void setTransportProtocol(String transportProtocol){
    	props.put("transportProtocol", transportProtocol);
    }
    
    /**
     * @return The acknowledgment requested.
     */
    public String getAckRequested(){
    	return (String) props.get("ackRequested");
    }    
    
    /**
     * @param ackRequested
     * 			The acknowledgment requested to set.
     */
    public void setAckRequested(String ackRequested){
    	props.put("ackRequested", ackRequested);
    }

    /**
     * @return The acknowledgment signing requested.
     */
    public String getAckSignRequested(){
    	return (String) props.get("ackSignRequested");    
    }    
   
    /**
     * @param ackSignRequested
     * 			The acknowledgment signing requested to set.
     */
    public void setAckSignRequested(String ackSignRequested){
    	props.put("ackSignRequested", ackSignRequested);
    }

    /**
     * @return The duplicated elimination.
     */
    public String getDupElimination(){
    	return (String) props.get("dupElimination");
    }   
    
    /**
     * @param dupElimination
     * 			The duplicated elimination to set.
     */
    public void setDupElimination(String dupElimination){
    	props.put("dupElimination", dupElimination);
    }

    /**
     * @return The actor.
     */
    public String getActor(){
    	return (String) props.get("actor");
    }     

    /**
     * @param actor
     * 			The actor to set.
     */
    public void setActor(String actor){
    	props.put("actor", actor);
    }

    /**
     * @return The messageOrder.
     */
    public String getMessageOrder(){
    	return (String) props.get("messageOrder");
    }
        
    /**
     * @param messageOrder
     *            The messageOrder to set.
     */
    public void setMessageOrder(String messageOrder){
    	props.put("messageOrder", messageOrder);
    }

    /**
     * @return The persistDuration.
     */
    public String getPersistDuration(){
    	return (String) props.get("persistDuration");
    }
       
    /**
     * @param persistDuration
     *            The persistDuration to set.
     */
    public void setPersistDuration(String persistDuration){
    	props.put("persistDuration", persistDuration); 
    }
    
    /**
     * @return The retries.
     */
    public int getRetries(){
    	try{
    		return Integer.parseInt((String)props.get("retries"));
    	}
    	catch(Exception e){
    		return Integer.MIN_VALUE;
    	}
    }

    /**
     * @param retries
     *            The retries to set.
     */
    public void setRetries(int retries){
    	props.put("retries", String.valueOf(retries));
    }

    /**
     * @return The retryInterval.
     */
    public int getRetryInterval(){
    	try{
    		return Integer.parseInt((String)props.get("retryInterval"));
    	}
    	catch(Exception e){
    		return Integer.MIN_VALUE;
    	}
    }
    
    /**
     * @param retryInterval
     *            The retryInterval to set.
     */
    public void setRetryInterval(int retryInterval){
    	props.put("retryInterval", String.valueOf(retryInterval));
    }
    
    /**
     * @param signRequested
     * 			The signing requested to set.
     */
    public void setSignRequested(String signRequested){
    	props.put("signRequested", signRequested);
    }
    
    /**
     * @return The signing requested.
     * 			
     */
    public String getSignRequested(){
    	return (String) props.get("signRequested");
    } 

    /**
     * @return The digital signing algorithm.
     * 			
     */
    public String getDsAlgorithm(){
    	return (String) props.get("dsAlgorithm");
    }

    /**
     * @param dsAlgorithm 
     * 			The digital signing algorithm to set.
     */
    public void setDsAlgorithm(String dsAlgorithm){
    	props.put("dsAlgorithm", dsAlgorithm); 
    }

    /**
     * @return The digital signing algorithm.
     * 			
     */
    public String getMdAlgorithm(){
    	return (String) props.get("mdAlgorithm");
    }

    /**
     * @param mdAlgorithm 
     * 			The checksum algorithm to set.
     */
    public void setMdAlgorithm(String mdAlgorithm){
    	 props.put("mdAlgorithm", mdAlgorithm);
     }

    /**
     * @return The checksum algorithm. 
     * 			
     */
    public String getEncryptRequested(){
    	return (String) props.get("encryptRequested");
    }

    /**
     * @param encryptRequested
     * 			The encryption requested to set.
     */
    public void setEncryptRequested(String encryptRequested){
    	props.put("encryptRequested", encryptRequested);
    }

    /**
     * @return The encryption algorithm.
     */
    public String getEncryptAlgorithm(){
    	return (String) props.get("encryptAlgorithm");
    }

    /**
     * @param encryptAlgorithm 
     * 			The encryption algorithm to set.
     */
    public void setEncryptAlgorithm(String encryptAlgorithm){
    	props.put("encryptAlgorithm", encryptAlgorithm);
    }
  
    /**
     * @param signCert 
     * 			The certificate for verification to set in byte array.
     */
    public void setSignCert(byte[] signCert){
    	props.put("signCert", signCert);
    }
    
    /**
     * @return The certificate for verification in byte array.
     */
    public byte[] getSignCert(){
    	return (byte[]) props.get("signCert");
    }

    /**
     * @return The certificate for encryption in byte array.
     */
    public byte[] getEncryptCert(){
    	return (byte[]) props.get("encryptCert");
    }
  
    /**
     * @param encryptCert 
     * 			The certificate for encryption in byte array.
     */
    public void setEncryptCert(byte[] encryptCert){
    	 props.put("encryptCert", encryptCert);
    }
    
    /**
     * @return True if the hostname is verified.
     */
    public String getIsHostnameVerified(){
    	return (String) props.get("isHostnameVerified");
    }
    
    /**
     * @param isHostnameVerified
     * 			True if the hostname verified to set.
     */
    public void setIsHostnameVerified(String isHostnameVerified){
    	props.put("isHostnameVerified", isHostnameVerified);
    }
}
