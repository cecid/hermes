/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.ws.data;

import hk.hku.cecid.corvus.http.PartnershipOp;
import hk.hku.cecid.corvus.ws.data.KVPairData;

/** 
 * The <code>AdminData</code> is 
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   H2O 28/11/2007
 */
abstract class AdminData extends KVPairData 
{
	/**
	 * This is the configuration key set for XML serialization / de-serialization.<br/><br/>
	 */
	public static final String [] CONFIG_KEY_SET = 
	{
		"username", "password", "managePartnershipEndpoint", "envelopQueryEndpoint"      
	};
	
	/**
	 * For consistent, used for {@link DataFactory#createEBMSAdminDataFromXML(hk.hku.cecid.piazza.commons.util.PropertyTree)}
	 */
	public static final String [] PARAM_KEY_SET = 
	{
		"partnershipOperation", "criteria/messageId", "criteria/messageBox"
	};
	
	/** Default constructor. */
	public AdminData(int maxCapacity) {
		super(maxCapacity);
	}
	
	/** 
	 * @param username The user name for authenticate administrator page.
	 */
	public void setUsername(String username){		
		props.put(CONFIG_KEY_SET[0], username);
	}
	
	/** 
	 * @return The user name for authenticate administrator page.
	 */
	public String getUsername(){
		return (String) props.get(CONFIG_KEY_SET[0]);
	}
	
	/** 
	 * @param password the password for authenticate administrator page
	 */
	public void setPassword(String password){
		char[] pw = password.toCharArray();
		props.put(CONFIG_KEY_SET[1], pw);
	}
	
	/** 
	 * @return The password for authenticate the administrator page.
	 */
	public char[] getPassword(){
		/*
		 * A quick hack for fixing DataFactory can only pass string to the properties. 
		 * Making the data-type mismatch as expected.
		 */ 
		Object obj = props.get(CONFIG_KEY_SET[1]);
		if (obj instanceof String)
			this.setPassword((String)obj);				
		return (char[]) props.get(CONFIG_KEY_SET[1]);
	}	
	
	/**
	 * Set the end-point for managing the set of EBMS partnership. 
	 *  
	 * @param manPartnershipEndpoint The end-point for managing the set of EBMS partnership. 
	 */
	public void setManagePartnershipEndpoint(String manPartnershipEndpoint){
		props.put(CONFIG_KEY_SET[2], manPartnershipEndpoint);
	}
	
	/** 
	 * @return The end-point for managing the set of EBMS partnership.
	 */
	public String getManagePartnershipEndpoint(){
		return (String) props.get(CONFIG_KEY_SET[2]);
	}
	
	/**
	 * Set the end-point for query the message envelop of EBMS partnership.
	 * 
	 * @param envelopQueryEndpoint The end-point for query the message envelop of EBMS partnership.
	 */
	public void setEnvelopQueryEndpoint(String envelopQueryEndpoint){
		props.put(CONFIG_KEY_SET[3], envelopQueryEndpoint);
	}
	
	/**
	 * @return The end-point for query the message envelop of EBMS partnership.
	 */
	public String getEnvelopQueryEndpoint(){
		return (String) props.get(CONFIG_KEY_SET[3]);
	}	
	
	/**   
	 * [USED ONLY BY PartnershipSender]
	 * 
	 * @param pOp The partnership operation you want to set, either 0, 1, 2. 
	 * 
	 * @see hk.hku.cecid.corvus.http.PartnershipOp
	 * @see hk.hku.cecid.corvus.http.EBMSPartnershipSender
	 */
	public void setPartnershipOperation(int pOp){
		if (pOp < 0 || pOp >= PartnershipOp.OP_LEN)
			throw new IllegalArgumentException("Expected operation value : 0, 1, 2");				
		props.put(PARAM_KEY_SET[0], String.valueOf(pOp));
	}
	
	/** 
	 * [USED ONLY BY PartnershipSender]
	 * 
	 * @return The partnership operation you have set, either 0, 1, 2.
	 * 
	 * @throws NumberFormatException 
	 * 			If the data format of the value of partnership operation is invalid.
	 */
	public int getPartnershipOperation(){
		String s = (String) props.get(PARAM_KEY_SET[0]);
		return Integer.parseInt(s);					
	}
	
	/**
	 * [USED ONLY BY EnvelopQuerySender]
	 * 
	 * Set the message id acting as the criteria for search the message envelop.
	 *  
	 * @param messageId the message id acting as the criteria for search the message envelop.
	 */
	public void setMessageIdCriteria(String messageId){
		if (messageId == null)
			throw new NullPointerException("Missing 'messageId' in the arguments.");
		props.put(PARAM_KEY_SET[1], messageId);
	}
	
	/**
	 * [USED ONLY BY EnvelopQuerySender]
	 * 
	 * @return the message id acting as the criteria for search the message envelop.
	 */
	public String getMessageIdCriteria(){
		return (String) props.get(PARAM_KEY_SET[1]);
	}
	
	/**
	 * [USED ONLY BY EnvelopQuerySender]
	 * 
	 * Set the message box acting as the criteria for search the message envelop.
	 * 
	 * @param messageBox the message box acting as the criteria for search the message envelop.
	 */
	public void setMessageBoxCriteria(String messageBox){
		if (messageBox == null)
			throw new NullPointerException("Missing 'messageBox' in the arguments.");
		props.put(PARAM_KEY_SET[2], messageBox);
	}
	
	/**
	 * [USED ONLY BY EnvelopQuerySender]
	 * 
	 * @return the message box acting as the criteria for search the message envelop.
	 */
	public String getMessageBoxCriteria(){
		return (String) props.get(PARAM_KEY_SET[2]);
	}
}
