/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.ws.data;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
 
import hk.hku.cecid.corvus.util.DateUtil;

/**
 * The <code>MessageStatusResponseData</code> is the data structures
 * representing the parameters set for the response in the message status requestquery.
 * <br/><br/>
 * 
 * This is the WSDL schema for the message status WS request.
 * 
 * <pre> 
 * &lt;xs:complexType name="MessageInfo.Type">
 *  &lt;xs:sequence>
 *   &lt;xs:element name="messageId" type="xs:string"/>
 *   &lt;xs:element name="messageType" type="xs:string"/>
 *   &lt;xs:element name="timestamp" type="xs:dateTime"/>
 *   &lt;xs:element name="messageStatus" type="xs:string"/>
 *   &lt;xs:element name="messageDetail" type="xs:string" minOccurs="0"/>
 *   &lt;xs:element name="partnershipId" type="xs:string" minOccurs="0"/>
 *   &lt;xs:element name="channelType" type="xs:string" minOccurs="0"/>
 *   &lt;xs:element name="channelId" type="xs:string" minOccurs="0"/>
 *   &lt;xs:element name="folderName" type="xs:string" minOccurs="0"/>
 *   &lt;xs:element name="fileName" type="xs:string" minOccurs="0"/>
 *   &lt;xs:element name="conversationId" type="xs:string" minOccurs="0"/>
 *  &lt;/xs:sequence>
 * &lt;/xs:complexType> 
 * </pre>
 * 
 * Creation Date: 19/3/2007
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	Dwarf 10315
 */
public class MessageStatusResponseData extends KVPairData {

	/**
	 * This is the key set for XML serialization / de-serialization.<br/><br/>
	 */
	public static final String [] PARAM_KEY_SET = 
	{
		"messageId"    ,"messageType"  ,"timestamp"  ,"messageStatus", 
		"messageDetail","partnershipId","channelType","channelId",
		"folderName"   ,"fileName"     ,"conversationId"
	};
		
	/**
	 * Default Constructor.
	 */
	public MessageStatusResponseData(){
		super(PARAM_KEY_SET.length);
	}
	
	/** 
	 * @return the properties set for this MessageStatusResponseData.
	 */
	public Map getProperties(){
		return this.props;
	}
	
	/**
	 * Set the message status request properties and overwrite 
	 * the existing one.
	 * 
	 * @param hm The new properties set.
	 */
	public void setProperties(HashMap hm){
		if (hm != null)
			this.props = hm;
	}
	
	/**
	 * Gets the messageId value for this MessageInfoType.
	 * 
	 * @return messageId
	 */
	public String getMessageId() {
		return (String) props.get(PARAM_KEY_SET[0]);
	}

	/**
	 * Sets the messageId value for this MessageInfoType.
	 * 
	 * @param messageId
	 */
	public void setMessageId(String messageId) {
		props.put(PARAM_KEY_SET[0], messageId);
	}

	/**
	 * Gets the messageType value for this MessageInfoType.
	 * 
	 * @return messageType
	 */
	public String getMessageType() {
		return (String) props.get(PARAM_KEY_SET[1]);
	}

	/**
	 * Sets the messageType value for this MessageInfoType.
	 * 
	 * @param messageType
	 */
	public void setMessageType(String messageType) {
		props.put(PARAM_KEY_SET[1], messageType);
	}

	/**
	 * Gets the timestamp value for this MessageInfoType.
	 * 
	 * @return timestamp
	 */
	public String getTimestamp() {
		return (String) props.get(PARAM_KEY_SET[2]);
	}
		
	/**
	 * Sets the timestamp value for this MessageInfoType.
	 * 
	 * @param timestamp
	 */
	public void setTimestamp(Calendar timestamp) {		
		try{
			String ts = DateUtil.calendar2UTC(timestamp);
			props.put(PARAM_KEY_SET[2], ts);
		}catch(Exception ex){}		
	}

	/**
	 * Gets the messageStatus value for this MessageInfoType.
	 * 
	 * @return messageStatus
	 */
	public String getMessageStatus() {
		return (String) props.get(PARAM_KEY_SET[3]);
	}

	/**
	 * Sets the messageStatus value for this MessageInfoType.
	 * 
	 * @param messageStatus
	 */
	public void setMessageStatus(String messageStatus) {
		props.put(PARAM_KEY_SET[3], messageStatus);
	}

	/**
	 * Gets the messageDetail value for this MessageInfoType.
	 * 
	 * @return messageDetail
	 */
	public String getMessageDetail() {
		return (String) props.get(PARAM_KEY_SET[4]);
	}

	/**
	 * Sets the messageDetail value for this MessageInfoType.
	 * 
	 * @param messageDetail
	 */
	public void setMessageDetail(String messageDetail) {
		props.put(PARAM_KEY_SET[4], messageDetail);
	}

	/**
	 * Gets the partnershipId value for this MessageInfoType.
	 * 
	 * @return partnershipId
	 */
	public String getPartnershipId() {
		return (String) props.get(PARAM_KEY_SET[5]);
	}

	/**
	 * Sets the partnershipId value for this MessageInfoType.
	 * 
	 * @param partnershipId
	 */
	public void setPartnershipId(String partnershipId) {
		props.put(PARAM_KEY_SET[5], partnershipId);
	}

	/**
	 * Gets the channelType value for this MessageInfoType.
	 * 
	 * @return channelType
	 */
	public String getChannelType() {
		return (String) props.get(PARAM_KEY_SET[6]);
	}

	/**
	 * Sets the channelType value for this MessageInfoType.
	 * 
	 * @param channelType
	 */
	public void setChannelType(String channelType) {
		props.put(PARAM_KEY_SET[6], channelType);
	}

	/**
	 * Gets the channelId value for this MessageInfoType.
	 * 
	 * @return channelId
	 */
	public String getChannelId() {
		return (String) props.get(PARAM_KEY_SET[7]);
	}

	/**
	 * Sets the channelId value for this MessageInfoType.
	 * 
	 * @param channelId
	 */
	public void setChannelId(String channelId) {
		props.put(PARAM_KEY_SET[7], channelId);
	}

	/**
	 * Gets the folderName value for this MessageInfoType.
	 * 
	 * @return folderName
	 */
	public String getFolderName() {
		return (String) props.get(PARAM_KEY_SET[8]);
	}

	/**
	 * Sets the folderName value for this MessageInfoType.
	 * 
	 * @param folderName
	 */
	public void setFolderName(String folderName) {
		props.put(PARAM_KEY_SET[8], folderName);
	}

	/**
	 * Gets the fileName value for this MessageInfoType.
	 * 
	 * @return fileName
	 */
	public String getFileName() {
		return (String) props.get(PARAM_KEY_SET[9]);
	}

	/**
	 * Sets the fileName value for this MessageInfoType.
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		props.put(PARAM_KEY_SET[9], fileName);
	}

	/**
	 * Gets the conversationId value for this MessageInfoType.
	 * 
	 * @return conversationId
	 */
	public String getConversationId() {
		return (String) props.get(PARAM_KEY_SET[10]);
	}

	/**
	 * Sets the conversationId value for this MessageInfoType.
	 * 
	 * @param conversationId
	 */
	public void setConversationId(String conversationId) {
		props.put(PARAM_KEY_SET[10], conversationId);
	}
	
	/**
	 * toString method().
	 */
	public String toString(){
		// Instead using hash map iteration, we want to preseve the order
		// of value, so do it myself.
		return new StringBuffer(
			"Key: Message Id     \t\t Value: " + this.getMessageId()     + "\n" + 
			"Key: Message Type   \t\t Value: " + this.getMessageType() 	 + "\n" + 
			"Key: Timestamp      \t\t Value: " + this.getTimestamp()	 + "\n" + 
			"Key: Message Status \t\t Value: " + this.getMessageStatus() + "\n" +
			"Key: Message Detail \t\t Value: " + this.getMessageDetail() + "\n" + 			
			"Key: Partnership Id \t\t Value: " + this.getPartnershipId() + "\n" +
			"Key: Channel Type   \t\t Value: " + this.getChannelType()   + "\n" +			
			"Key: Channel Id     \t\t Value: " + this.getChannelId()     + "\n" +  
			"Key: Folder name    \t\t Value: " + this.getFolderName()    + "\n" +
			"Key: File name      \t\t Value: " + this.getFileName()      + "\n" +
			"Key: Conversation Id\t\t Value: " + this.getConversationId()+ "\n").toString();
	}
}
