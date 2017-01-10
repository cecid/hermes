package hk.hku.cecid.corvus.ws.data;

import java.util.Calendar;
import java.math.BigInteger;

import hk.hku.cecid.corvus.util.DateUtil;

/**
 * The <code>MessageStatusRequestData</code> is the data structures
 * representing the parameters set for message status request query.<br/><br/>
 * 
 * This is the WSDL schema for the message status WS request.
 * <pre>
 * &lt;xs:sequence&gt;
 *  &lt;xs:element name="partnershipId" type="xs:string" minOccurs="0"/>
 *  &lt;xs:element name="channelType" type="xs:string" minOccurs="0"/>
 *  &lt;xs:element name="channelId" type="xs:string" minOccurs="0"/>
 *  &lt;xs:element name="folderName" type="xs:string" minOccurs="0"/>
 *  &lt;xs:element name="fileName" type="xs:string" minOccurs="0"/>
 *  &lt;xs:element name="fromTimestamp" type="xs:dateTime" minOccurs="0"/>
 *  &lt;xs:element name="toTimestamp" type="xs:dateTime" minOccurs="0"/>
 *  &lt;xs:element name="numOfRecords" type="xs:integer" minOccurs="0"/>
 *  &lt;xs:element name="conversationId" type="xs:string" minOccurs="0"/>
 *  &lt;xs:element name="messageId" type="xs:string" minOccurs="0"/>
 *  &lt;xs:element name="messageType" type="xs:string" minOccurs="0"/>
 *  &lt;xs:element name="messageStatus" type="xs:string" minOccurs="0"/>
 *  &lt;xs:element name="protocol" type="xs:string" minOccurs="0"/>
 *  &lt;xs:element name="locale" type="xs:string" minOccurs="0"/>
 *  &lt;xs:element name="levelOfDetails" type="xs:integer" minOccurs="0"/>
 *  &lt;xs:element name="offset" type="xs:integer" minOccurs="0"/>
 * &lt;/xs:sequence>  
 * </pre>
 *
 * Creation Date: 12/3/2007
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	Dwarf 10315
 */
public class MessageStatusRequestData extends KVPairData {
	
	/**
	 * This is the key set for XML serialization / de-serialization.<br/><br/>
	 */
	public static final String [] PARAM_KEY_SET = 
	{
		"partnershipId" ,"channelType"  ,"channelId"     ,"folderName", 
		"fileName"      ,"fromTimestamp","toTimestamp"   ,"numOfRecords",
		"conversationId","messageId"	,"messageType"   ,"messageStatus",
		"protocol"      ,"locale"       ,"levelOfDetails","offset"
	};

	/**
	 * This is the configuration key set for XML serialization / de-serialization.<br/><br/>
	 */
	public static final String [] CONFIG_KEY_SET = 
	{
		"endpoint"        ,"username"		,"password"      
	};
	
	/**
	 * This is the prefix for serialization / de-serialization.<br/><br/>
	 */
	public static final String CONFIG_PREFIX = "message-status-request/config";
	
	/**
	 * This is the prefix for serialzation / de-serialization.<br/><br/>
	 */
	public static final String PARAM_PREFIX  = "message-status-request/param";
		
	/**
	 * Default Constructor.
	 */
	public MessageStatusRequestData(){
		super(PARAM_KEY_SET.length + CONFIG_KEY_SET.length);
	}
	
	/**
	 * Get the web service end point for this MessageStatusRequestData.
	 */
	public String getWSEndpoint(){
		return (String) props.get(CONFIG_KEY_SET[0]);
	}
	
	/**
	 * Set the web service end point for this MessageStatusRequestData.
	 * 
	 * @param endpoint The new web service end point.
	 */
	public void setWSEndpoint(String endpoint){
		try{
			new java.net.URL(endpoint);
			props.put(CONFIG_KEY_SET[0], endpoint);
		} catch (Exception e){
			// Invalid URL, do nothing.
		}
	}
	
	/**
	 * Get the username for authentication.
	 * 
	 * @return Get the username for authentication. 
	 */
	public String getUsername(){
		return (String) props.get(CONFIG_KEY_SET[1]);
	}
	
	/**
	 * Set the username for authentication.
	 * 
	 * @param username The username for authentication.
	 */
	public void setUsername(String username){
		props.put(CONFIG_KEY_SET[1], username);
	}
	
	/**
	 * Get the password for authentication.
	 */
	public String getPassword(){
		try{
			return new String(
				new sun.misc.BASE64Decoder()
					.decodeBuffer((String) props.get(CONFIG_KEY_SET[2])));
		}catch(java.io.IOException ioe){
			ioe.printStackTrace(System.err);
		}
		return null;
	}
	
	/**
	 * Set the password for authentication. 
	 */
	public void setPassword(String password){
		if (password != null){
			String b64encode = new sun.misc.BASE64Encoder().encode(password.getBytes());
			props.put(CONFIG_KEY_SET[2], b64encode);
		}
	}
	
	/**
	 * Gets the partnershipId value for this MessageStatusRequestData.
	 * 
	 * @return partnershipId
	 */
	public String getPartnershipId() {
		return (String) props.get(PARAM_KEY_SET[0]);
	}

	/**
	 * Sets the partnershipId value for this MessageStatusRequestData.
	 * 
	 * @param partnershipId
	 */
	public void setPartnershipId(String partnershipId) {
		props.put(PARAM_KEY_SET[0], partnershipId);
	}

	/**
	 * Gets the channelType value for this MessageStatusRequestData.
	 * 
	 * @return channelType
	 */
	public String getChannelType() {
		return (String) props.get(PARAM_KEY_SET[1]);
	}

	/**
	 * Sets the channelType value for this MessageStatusRequestData.
	 * 
	 * @param channelType
	 */
	public void setChannelType(String channelType) {
		props.put(PARAM_KEY_SET[1], channelType);
	}

	/**
	 * Gets the channelId value for this MessageStatusRequestData.
	 * 
	 * @return channelId
	 */
	public String getChannelId() {
		return (String) props.get(PARAM_KEY_SET[2]);
	}

	/**
	 * Sets the channelId value for this MessageStatusRequestData.
	 * 
	 * @param channelId
	 */
	public void setChannelId(String channelId) {
		props.put(PARAM_KEY_SET[2], channelId);
	}

	/**
	 * Gets the folderName value for this MessageStatusRequestData.
	 * 
	 * @return folderName
	 */
	public String getFolderName() {
		return (String) props.get(PARAM_KEY_SET[3]);
	}

	/**
	 * Sets the folderName value for this MessageStatusRequestData.
	 * 
	 * @param folderName
	 */
	public void setFolderName(String folderName) {
		props.put(PARAM_KEY_SET[3], folderName);
	}

	/**
	 * Gets the fileName value for this MessageStatusRequestData.
	 * 
	 * @return fileName
	 */
	public String getFileName() {
		return (String) props.get(PARAM_KEY_SET[4]);
	}

	/**
	 * Sets the fileName value for this MessageStatusRequestData.
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		props.put(PARAM_KEY_SET[4], fileName);
	}

	/**
	 * Gets the fromTimestamp value for this MessageStatusRequestData.
	 * 
	 * @return fromTimestamp
	 */
	public String getFromTimestamp() {		
		return (String) props.get(PARAM_KEY_SET[5]);
	}

	/**
	 * Sets the fromTimestamp value for this MessageStatusRequestData.
	 * 
	 * @param fromTimestamp
	 */
	public void setFromTimestamp(Calendar fromTimestamp) {
		try {
			String ts = DateUtil.calendar2UTC(fromTimestamp);
			props.put(PARAM_KEY_SET[5], ts);
		} catch (Exception ex) {}
		
	}

	/**
	 * Gets the toTimestamp value for this MessageStatusRequestData.
	 * 
	 * @return toTimestamp
	 */
	public String getToTimestamp() {
		return (String) props.get(PARAM_KEY_SET[6]);		
	}

	/**
	 * Sets the toTimestamp value for this MessageStatusRequestData.
	 * 
	 * @param toTimestamp
	 */
	public void setToTimestamp(Calendar toTimestamp) {
		try {
			String ts = DateUtil.calendar2UTC(toTimestamp);
			props.put(PARAM_KEY_SET[6], ts);
		} catch (Exception ex) {}
		
	}

	/**
	 * Gets the numOfRecords value for this MessageStatusRequestData.<br/><br/>
	 * 
	 * The default value is 1.
	 * 
	 * @return numOfRecords
	 */
	public BigInteger getNumOfRecords() {
		BigInteger bi = (BigInteger) props.get(PARAM_KEY_SET[7]);
		return (bi == null) ? BigInteger.ONE : bi;
	}

	/**
	 * Sets the numOfRecords value for this MessageStatusRequestData.
	 * 
	 * @param numOfRecords
	 */
	public void setNumOfRecords(BigInteger numOfRecords) {
		if (numOfRecords.intValue() >= 0)
			props.put(PARAM_KEY_SET[7], numOfRecords);
	}

	/**
	 * Gets the conversationId value for this MessageStatusRequestData.
	 * 
	 * @return conversationId
	 */
	public String getConversationId() {
		return (String) props.get(PARAM_KEY_SET[8]);
	}

	/**
	 * Sets the conversationId value for this MessageStatusRequestData.
	 * 
	 * @param conversationId
	 */
	public void setConversationId(String conversationId) {
		props.put(PARAM_KEY_SET[8], conversationId);
	}

	/**
	 * Gets the messageId value for this MessageStatusRequestData.
	 * 
	 * @return messageId
	 */
	public String getMessageId() {
		return (String) props.get(PARAM_KEY_SET[9]);
	}

	/**
	 * Sets the messageId value for this MessageStatusRequestData.
	 * 
	 * @param messageId
	 */
	public void setMessageId(String messageId) {
		props.put(PARAM_KEY_SET[9], messageId);
	}

	/**
	 * Gets the messageType value for this MessageStatusRequestData.
	 * 
	 * @return messageType
	 */
	public String getMessageType() {
		return (String) props.get(PARAM_KEY_SET[10]);
	}

	/**
	 * Sets the messageType value for this MessageStatusRequestData.
	 * 
	 * @param messageType
	 */
	public void setMessageType(String messageType) {
		props.put(PARAM_KEY_SET[10], messageType);
	}

	/**
	 * Gets the messageStatus value for this MessageStatusRequestData.
	 * 
	 * @return messageStatus
	 */
	public String getMessageStatus() {
		return (String) props.get(PARAM_KEY_SET[11]);
	}

	/**
	 * Sets the messageStatus value for this MessageStatusRequestData.
	 * 
	 * @param messageStatus
	 */
	public void setMessageStatus(String messageStatus) {
		props.put(PARAM_KEY_SET[11], messageStatus);
	}

	/**
	 * Gets the protocol value for this MessageStatusRequestData.
	 * 
	 * @return protocol
	 */
	public String getProtocol() {
		return (String) props.get(PARAM_KEY_SET[12]);
	}

	/**
	 * Sets the protocol value for this MessageStatusRequestData.
	 * 
	 * @param protocol
	 */
	public void setProtocol(String protocol) {
		props.put(PARAM_KEY_SET[12], protocol);
	}

	/**
	 * Gets the locale value for this MessageStatusRequestData.
	 * 
	 * @return locale
	 */
	public String getLocale() {
		return (String) props.get(PARAM_KEY_SET[13]);
	}

	/**
	 * Sets the locale value for this MessageStatusRequestData.
	 * 
	 * @param locale
	 */
	public void setLocale(String locale) {
		props.put(PARAM_KEY_SET[13], locale);
	}

	/**
	 * Gets the levelOfDetails value for this MessageStatusRequestData.
	 * 
	 * The default value is 1. 
	 * 
	 * @return levelOfDetails
	 */
	public BigInteger getLevelOfDetails() {
		BigInteger bi = (BigInteger) props.get(PARAM_KEY_SET[14]);
		return (bi == null) ? BigInteger.ONE : bi;
	}

	/**
	 * Sets the levelOfDetails value for this MessageStatusRequestData.<br/<br/>
	 * 
	 * @param levelOfDetails
	 */
	public void setLevelOfDetails(BigInteger levelOfDetails){
		if (levelOfDetails.intValue() >= 0)
			props.put(PARAM_KEY_SET[14], levelOfDetails);		
	}

	/**
	 * Gets the offset value for this MessageStatusRequestData.
	 * 
	 * The default value is 1.
	 *  
	 * @return offset
	 */
	public BigInteger getOffset() {
		BigInteger bi = (BigInteger) props.get(PARAM_KEY_SET[15]);
		return (bi == null) ? BigInteger.ZERO : bi;
	}

	/**
	 * Sets the offset value for this MessageStatusRequestData.
	 * 
	 * @param offset
	 */
	public void setOffset(BigInteger offset) {
		if (offset.intValue() >= 0)
			props.put(PARAM_KEY_SET[15], offset);
	}
		
	/**
	 * toString method().
	 */
	public String toString(){
		// Instead using hash map iteration, we want to preseve the order
		// of value, so do it myself.		
		return new StringBuffer(
		    "Key: Query endpoint \t\t Value: " + this.getWSEndpoint()    + "\n" +
		    "Key: Username       \t\t Value: " + this.getUsername()      + "\n" +
		    "Key: Password       \t\t Value: " + this.getPassword()      + "\n" +
			"Key: Partnership Id \t\t Value: " + this.getPartnershipId() + "\n" + 
			"Key: Channel Id     \t\t Value: " + this.getChannelId()     + "\n" + 
			"Key: Channel Type   \t\t Value: " + this.getChannelType()   + "\n" + 
			"Key: Folder name    \t\t Value: " + this.getFolderName()    + "\n" +
			"Key: File name      \t\t Value: " + this.getFileName()      + "\n" +
			"Key: From timestamp \t\t Value: " + this.getFromTimestamp() + "\n" +
			"Key: To timestamp   \t\t Value: " + this.getToTimestamp()   + "\n" + 
			"Key: Num of Record  \t\t Value: " + this.getNumOfRecords()  + "\n" + 
			"Key: Conversation Id\t\t Value: " + this.getConversationId()+ "\n" +
			"Key: Message Id     \t\t Value: " + this.getMessageId()     + "\n" + 
			"Key: Message Type   \t\t Value: " + this.getMessageType()   + "\n" + 
			"Key: Message Status \t\t Value: " + this.getMessageStatus() + "\n" +
			"Key: Protocol       \t\t Value: " + this.getProtocol()      + "\n" + 
			"Key: Locale         \t\t Value: " + this.getLocale()        + "\n" +
			"Key: LOD            \t\t Value: " + this.getLevelOfDetails()+ "\n" +
			"Key: Offset         \t\t Value: " + this.getOffset() + "\n").toString();		
	}
}
