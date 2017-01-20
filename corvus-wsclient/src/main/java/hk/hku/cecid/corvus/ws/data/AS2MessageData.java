package hk.hku.cecid.corvus.ws.data;

/**
 * The <code>AS2MessageData</code> is the data structure representing 
 * the parameters set for sending as2 messagw with payload to Hermes2.
 * 
 * This is the sample WSDL request for the message status WS request. 
 * <pre>
 * 	&lt;as2_from&gt;as2loopback&lt;/as2_from&gt;
 * 	&lt;as2_to&gt;as2loopback&lt;/as2_to&gt;
 * 	&lt;type&gt;xml&lt;/type&gt;
 * </pre>
 * 
 * The first three parameters are derived from 
 * {@link AS2PartnershipData#getAS2From()} and {@link AS2PartnershipData#getAs2To()}.
 * <br/><br/>
 * 
 * @author Twinsen Tsang
 * @version 1.0.1
 * @since	Elf 0818
 */
public class AS2MessageData extends KVPairData{
	
	/**
	 * This is the key set for XML serialization / de-serialization.<br/><br/>
	 */
	public static final String [] PARAM_KEY_SET = 
	{
		"type", "messageId"
	};
	
	/**
	 * This is the configuration key set for XML serialization / de-serialization.<br/><br/>
	 */
	public static final String [] CONFIG_KEY_SET = 
	{
		"sendEndpoint"	, "recvEndpoint" , "recvListEndpoint"      
	};
	
	/**
	 * This is the configuration prefix for serialization / de-serialization.<br/><br/>
	 */
	public static final String CONFIG_PREFIX = "/as2-request/config";
	
	/**
	 * This is the param prefix for serialzation / de-serialization.<br/><br/>
	 */
	public static final String PARAM_PREFIX  = "/as2-request/param";
		
	/** 
	 * Default Constructor.
	 */
	public AS2MessageData(){
		super(PARAM_KEY_SET.length);
	}
	
	/**
	 * @return Get the web service End-point for sending AS2 message to CORVUS.
	 */
	public String getSendEndpoint(){
		return (String)this.props.get(CONFIG_KEY_SET[0]);
	}
	
	/** 
	 * Set the web service End-point for sending AS2 message to CORVUS.
	 * 
	 * @param endpoint
	 * 			The web service End-point for sending AS2 message to CORVUS.
	 */
	public void setSendEndpoint(String endpoint){
		this.props.put(CONFIG_KEY_SET[0], endpoint);
	}
	
	/** 
	 * @return Get the web service End-point for receiving AS2 message from CORVUS.
	 */
	public String getRecvEndpoint(){
		return (String)this.props.get(CONFIG_KEY_SET[1]);
	}
	
	/** 
	 * Set the web service End-point for receiving AS2 message from CORVUS.
	 * 
	 * @param endpoint
	 * 			The web service End-point for receiving AS2 message from CORVUS.
	 */
	public void setRecvEndpoint(String endpoint){
		this.props.put(CONFIG_KEY_SET[1], endpoint);
	}
	
	/** 
	 * @return Get the web service End-point for receiving a list of AS2 message
	 * 		   which are ready to down-load from CORVUS.
	 */
	public String getRecvlistEndpoint(){
		return (String)this.props.get(CONFIG_KEY_SET[2]);
	}
	
	/** 
	 * Set the web service End-point for receiving a list of AS2 message
	 * which are ready to down-load from CORVUS.
	 * 
	 * @param endpoint 
	 * 			the web service End-point for receiving a list of AS2 message
	 * 			which are ready to download from CORVUS.
	 */
	public void setRecvlistEndpoint(String endpoint){
		this.props.put(CONFIG_KEY_SET[2], endpoint);
	}
	
		
	/**
	 * Get the type of the payload. 
	 * 
	 * @return the type of payload in the AS2 message.
	 */
	public String getType(){
		return (String) this.props.get(PARAM_KEY_SET[0]);
	}
	
	/** 
	 * Set the type of the payload.
	 * 
	 * @param type The type of payload in the AS2 message.
	 */
	public void setType(String type){
		this.props.put(PARAM_KEY_SET[0], type);
	}
	
	/**
	 * Get the Message ID that targeted to retrieve  
	 * 
	 * @return the value of Message ID in the AS2 message.
	 */
	public String getMessageIdForReceive(){
		return (String) this.props.get(PARAM_KEY_SET[1]);
	}
	
	/** 
	 * Set the Message ID that targeted to retrieve  
	 * 
	 * @param value The Message ID of the AS2 message.
	 */
	public void setMessageIdForReceive(String value){
		this.props.put(PARAM_KEY_SET[1], value);
	}
	
	/**
	 * toString method.
	 */
	public String toString()
	{
		// Instead using hash map iteration, we want to preserve the order
		// of value, so do it myself.		
		return new StringBuffer(
		    "Key: Send endpoint    \t\t Value: " + this.getSendEndpoint()     	  + "\n" +
		    "Key: Recv endpoint    \t\t Value: " + this.getRecvEndpoint()     	  + "\n" +
		    "Key: Recvlist endpoint\t\t Value: " + this.getRecvlistEndpoint() 	  + "\n" +
			"Key: Type             \t\t Value: " + this.getType()                 + "\n" +
			"Key: Message ID       \t\t Value: " + this.getMessageIdForReceive()  + "\n" ).toString();		  
	}
}
