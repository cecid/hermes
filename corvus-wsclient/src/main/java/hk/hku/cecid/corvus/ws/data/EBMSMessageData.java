package hk.hku.cecid.corvus.ws.data;

/**
 * The <code>EBMSMessageData</code> is the data structure representing 
 * the parameters set for sending ebms message with payload to Hermes2.
 * <br/><br/>
 * 
 * This is the sample WSDL request for the sending EbMS message WS request. *  
 * <pre>
 *   &lt;cpaId&gt; ebmscpaid &lt;/cpaId&gt;
 *   &lt;service&gt; http://localhost:8080/corvus/httpd/ebms/inbound &lt;service&gt;
 *   &lt;action&gt; action &lt;/action&gt;
 *   &lt;convId&gt; convId &lt;/convId&gt; 
 *   &lt;fromPartyId&gt; fromPartyId &lt;/fromPartyId&gt;
 *   &lt;fromPartyType&gt; fromPartyType &lt;/fromPartyType&gt;
 *   &lt;toPartyId&gt; toPartyId &lt;/toPartyId&gt; 
 *   &lt;toPartyType&gt; toPartyType &lt;/toPartyType&gt; 
 *   &lt;refToMessageId&gt; &lt;/refToMessageId&gt;  
 *   &lt;serviceType&gt; &lt;/serviceType&gt;
 * </pre>
 * 
 * This is the sample WSDL request for the retrieve EbMS message WS request.*  
 * <pre>
 *   &lt;messageId&gt; target-messageId &lt;/messageId&gt;
 * </pre>
 * 
 * The first three parameters are derived from 
 * {@link EBMSPartnershipData#getCpaId()},
 * {@link EBMSPartnershipData#getService()} and 
 * {@link EBMSPartnershipData#getAction()}
 * 
 * @author Twinsen Tsang
 * @version 1.0.1
 * @since	Elf 0818
 */
public class EBMSMessageData extends KVPairData
{
	/**
	 * This is the key set for XML serialization / de-serialization.<br/><br/>
	 */
	public static final String [] PARAM_KEY_SET = 
	{
		"conversationId","fromPartyId"   ,"fromPartyType" ,"toPartyId", 
		"toPartyType"   ,"refToMessageId","serviceType", "messageId"
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
	public static final String CONFIG_PREFIX = "/ebms-request/config";
	
	/**
	 * This is the parameters prefix for serialization / de-serialization.<br/><br/>
	 */
	public static final String PARAM_PREFIX  = "/ebms-request/param";
	
	/** 
	 * Default Constructor. 
	 */
	public EBMSMessageData()
	{
		super(PARAM_KEY_SET.length);		
	}
	
	/**
	 * @return Get the web service End-point for sending ebMS message to CORVUS.
	 */
	public String getSendEndpoint(){
		return (String)this.props.get(CONFIG_KEY_SET[0]);
	}
	
	/** 
	 * Set the web service End-point for sending ebMS message to CORVUS.
	 * 
	 * @param endpoint
	 * 			The web service End-point for sending ebMS message to CORVUS.
	 */
	public void setSendEndpoint(String endpoint){
		this.props.put(CONFIG_KEY_SET[0], endpoint);
	}
	
	/** 
	 * @return Get the web service End-point for receiving ebMS message from CORVUS.
	 */
	public String getRecvEndpoint(){
		return (String)this.props.get(CONFIG_KEY_SET[1]);
	}
	
	/** 
	 * Set the web service End-point for receiving ebMS message from CORVUS.
	 * 
	 * @param endpoint
	 * 			The web service End-point for receiving ebMS message from CORVUS.
	 */
	public void setRecvEndpoint(String endpoint){
		this.props.put(CONFIG_KEY_SET[1], endpoint);
	}
	
	/** 
	 * @return Get the web service End-point for receiving a list of ebMS message
	 * 		   which are ready to download from CORVUS.
	 */
	public String getRecvlistEndpoint(){
		return (String)this.props.get(CONFIG_KEY_SET[2]);
	}
	
	/** 
	 * Set the web service End-point for receiving a list of ebMS message
	 * which are ready to download from CORVUS.
	 * 
	 * @param endpoint 
	 * 			the web service End-point for receiving a list of ebMS message
	 * 			which are ready to download from CORVUS.
	 */
	public void setRecvlistEndpoint(String endpoint){
		this.props.put(CONFIG_KEY_SET[2], endpoint);
	}
	
	/**
	 * @return the conversationId
	 */
	public String getConversationId() {
		return (String)this.props.get(PARAM_KEY_SET[0]);
	}

	/**
	 * @param conversationId the conversationId to set
	 */
	public void setConversationId(String conversationId) {
		this.props.put(PARAM_KEY_SET[0], conversationId);
	}

	/**
	 * @return the fromPartyId
	 */
	public String getFromPartyId() {
		return (String)this.props.get(PARAM_KEY_SET[1]);
	}

	/**
	 * @param fromPartyId the fromPartyId to set
	 */
	public void setFromPartyId(String fromPartyId) {
		this.props.put(PARAM_KEY_SET[1], fromPartyId);
	}

	/**
	 * @return the fromPartyType
	 */
	public String getFromPartyType() {
		return (String) this.props.get(PARAM_KEY_SET[2]);
	}

	/**
	 * @param fromPartyType the fromPartyType to set
	 */
	public void setFromPartyType(String fromPartyType) {
		this.props.put(PARAM_KEY_SET[2], fromPartyType);
	}

	/**
	 * @return the toPartyId
	 */
	public String getToPartyId() {
		return (String) this.props.get(PARAM_KEY_SET[3]);
	}

	/**
	 * @param toPartyId the toPartyId to set
	 */
	public void setToPartyId(String toPartyId) {
		this.props.put(PARAM_KEY_SET[3], toPartyId);
	}

	/**
	 * @return the toPartyType
	 */
	public String getToPartyType() {
		return (String) this.props.get(PARAM_KEY_SET[4]);
	}

	/**
	 * @param toPartyType the toPartyType to set
	 */
	public void setToPartyType(String toPartyType) {
		this.props.put(PARAM_KEY_SET[4], toPartyType);
	}

	/**
	 * @return the refToMessageId
	 */
	public String getRefToMessageId() {
		return (String) this.props.get(PARAM_KEY_SET[5]);
	}

	/**
	 * @param refToMessageId the refToMessageId to set
	 */
	public void setRefToMessageId(String refToMessageId) {
		this.props.put(PARAM_KEY_SET[5], refToMessageId);
	}
	
	/** 
	 * @return the service type.
	 */
	public String getServiceType(){
		return (String) this.props.get(PARAM_KEY_SET[6]);
	}
	
	/** 
	 * @param serviceType the service type to set.
	 */
	public void setServiceType(String serviceType){
		this.props.put(PARAM_KEY_SET[6], serviceType);
	}
	
	/** 
	 * @return the targeted Message ID for message receiver.
	 */
	public String getMessageIdForReceive(){
		return (String) this.props.get(PARAM_KEY_SET[7]);
	}
	
	/** 
	 * Set the Message ID that targeted to retrieve  
	 * 
	 * @param value The MessageId of the message.
	 */
	public void setMessageIdForReceive(String value){
		this.props.put(PARAM_KEY_SET[7], value);
	}

	/**
	 * It the data is dirty.
	 */
	public boolean isDirty = false;

	/* (non-Javadoc)
	 * @see hk.hku.cecid.corvus.ws.data.KVPairData#toString()
	 */
	public String toString(){
		// Instead using hash map iteration, we want to preseve the order
		// of value, so do it myself.		
		return new StringBuffer(
		    "Key: Send endpoint    \t\t Value: " + this.getSendEndpoint()     + "\n" +
		    "Key: Recv endpoint    \t\t Value: " + this.getRecvEndpoint()     + "\n" +
		    "Key: Recvlist endpoint\t\t Value: " + this.getRecvlistEndpoint() + "\n" +
			"Key: Conversation Id  \t\t Value: " + this.getConversationId()   + "\n" +
			"Key: From party id    \t\t Value: " + this.getFromPartyId()      + "\n" + 
			"Key: From party type  \t\t Value: " + this.getFromPartyType()    + "\n" + 
			"Key: To party id      \t\t Value: " + this.getToPartyId()        + "\n" +
			"Key: To party type    \t\t Value: " + this.getToPartyType()      + "\n" + 
			"Key: Reference msg id \t\t Value: " + this.getRefToMessageId()   + "\n" +
			"Key: service type     \t\t Value: " + this.getServiceType()      + "\n" +
			"Key: MessageID for Receive     \t\t Value: " + this.getMessageIdForReceive()   + "\n").toString();					 
	}
}
