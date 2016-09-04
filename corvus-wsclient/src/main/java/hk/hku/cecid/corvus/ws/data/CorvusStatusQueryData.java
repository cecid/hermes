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
 * The <code>CorvusStatusQueryData</code> is the data structure representing 
 * the parameters set for status query web services for all protocol using 
 * Corvus level.<br/><br/>
 *   
 * This is the sample WSDL request for the status query WS request. 
 * <PRE>
 * &lt;messageId&gt; 20070418-124233-75006@147.8.177.42 &lt;/messageId&gt;  
 * </PRE>  
 * Creation Date: 2/5/2007
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	Dwarf 10327
 */
public class CorvusStatusQueryData extends KVPairData {

	/**
	 * This is the key set for XML serialization / de-serialization.<br/><br/>
	 */
	public static final String [] PARAM_KEY_SET = 
	{
		"messageId", 
	};
	
	/**
	 * This is the configuration key set for XML serialization / de-serialization.<br/><br/>
	 */
	public static final String [] CONFIG_KEY_SET = 
	{
		"endpoint"    
	};
	
	/**
	 * This is the configuration prefix for serialization / de-serialization.<br/><br/>
	 */
	public String configPrefix = "status-request/config";
	
	/**
	 * This is the param prefix for serialzation / de-serialization.<br/><br/>
	 */
	public String paramPrefix  = "status-request/param";
	
	/** 
	 * Explicit Constructor.
	 * 
	 * @param ConfigXPath
	 * 			The XPath for the configuration data in status query.
	 * @param ParamXPath 
	 * 			The XPath for the paramter data in status query.
	 */
	public CorvusStatusQueryData(String ConfigXPath, String ParamXPath){
		super(PARAM_KEY_SET.length + CONFIG_KEY_SET.length);
		this.configPrefix = ConfigXPath;
		this.paramPrefix  = ParamXPath;		
	}
	
	/**
	 * @return Get the web service endpoint for sending status query message to corvus.
	 */
	public String getSendEndpoint(){
		return (String)this.props.get(CONFIG_KEY_SET[0]);
	}
	
	/** 
	 * Set the web service endpoint for sending status query message to corvus.
	 * 
	 * @param endpoint
	 * 			The web service endpoint for sending status query message to corvus.
	 */
	public void setSendEndpoint(String endpoint){
		this.props.put(CONFIG_KEY_SET[0], endpoint);
	}
		
	
	/**   
	 * @return
	 *		The message id to query the status. 
	 */
	public String getQueryMessageId(){
		return (String)this.props.get(PARAM_KEY_SET[0]);
	}
	
	/** 
	 * @param messageId
	 * 			The message id to query the status.
	 */
	public void setQueryMessageId(String messageId){
		this.props.put(PARAM_KEY_SET[0], messageId);
	}
}
