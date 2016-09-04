/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package hk.hku.cecid.ebms.spa.dao;

import java.sql.Timestamp;
import java.sql.Types;

import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDVO;
import hk.hku.cecid.piazza.commons.dao.ds.NullableObject;

/**
 * The <code>MessageDataSourceDVO</code> is a implementation of interface
 * <code>MesageDVO</code> and representing one persistence record in the
 * table <em>message</em>.
 * 
 * @author Donahue Sze, Twinsen Tsang (modifier)   
 */
public class MessageDataSourceDVO extends DataSourceDVO implements MessageDVO {

	// serialization version ID. 
	private static final long serialVersionUID = -8189217954155888533L;

	public MessageDataSourceDVO() {
        super();
    }

    /**
     * @return Returns the messageId.
     */
    public String getMessageId() {
        return super.getString("messageId");
    }

    /**
     * @param messageId
     *            The messageId to set.
     */
    public void setMessageId(String messageId) {
        super.setString("messageId", messageId);
    }

    /**
     * @return Returns the messageType.
     */
    public String getMessageBox() {
        return super.getString("messageBox");
    }

    /**
     * @param messageBox The messageBox to set.
     */
    public void setMessageBox(String messageBox) {
        super.setString("messageBox", messageBox);
    }
    
    /**
     * @return Returns the messageType.
     */
    public String getMessageType() {
        return super.getString("messageType");
    }
    
    /**
     * @param messageType
     *            The messageType to set.
     */
    public void setMessageType(String messageType) {
        super.setString("messageType", messageType);
    }
    
    /**
     * @return Returns the fromPartyId.
     */
    public String getFromPartyId() {
        return super.getString("fromPartyId");
    }

    /**
     * @param fromPartyId
     *            The fromPartyId to set.
     */
    public void setFromPartyId(String fromPartyId) {
        super.setString("fromPartyId", fromPartyId);
    }
    

    /**
     * @return Returns the fromPartyRole.
     */
    public String getFromPartyRole() {
        return super.getString("fromPartyRole");
    }

    /**
     * @param fromPartyRole
     *            The fromPartyRole to set.
     */
    public void setFromPartyRole(String fromPartyRole) {
        super.setString("fromPartyRole", fromPartyRole);
    }

    /**
     * @return Returns the toPartyId.
     */
    public String getToPartyId() {
        return super.getString("toPartyId");
    }

    /**
     * @param toPartyId
     *            The toPartyId to set.
     */
    public void setToPartyId(String toPartyId) {
        super.setString("toPartyId", toPartyId);
    }

    /**
     * @return Returns the toPartyRole.
     */
    public String getToPartyRole() {
        return super.getString("toPartyRole");
    }

    /**
     * @param toPartyRole
     *            The toPartyRole to set.
     */
    public void setToPartyRole(String toPartyRole) {
        super.setString("toPartyRole", toPartyRole);
    }
    

    /**
     * @return Returns the cpaId.
     */
    public String getCpaId() {
        return super.getString("cpaId");
    }

    /**
     * @param cpaId
     *            The cpaId to set.
     */
    public void setCpaId(String cpaId) {
        super.setString("cpaId", cpaId);
    }
    
    /**
     * @return Returns the service.
     */
    public String getService() {
        return super.getString("service");
    }

    /**
     * @param service
     *            The service to set.
     */
    public void setService(String service) {
        super.setString("service", service);
    }
    
    /**
     * @return Returns the action.
     */
    public String getAction() {
        return super.getString("action");
    }

    /**
     * @param action
     *            The action to set.
     */
    public void setAction(String action) {
        super.setString("action", action);
    }
    
    /**
     * @return Returns the conversation id of the message record.
     */
    public String getConvId() {
        return super.getString("convId");
    }

    /**
     * @param convId
     *            The coversation id of this message record.
     */
    public void setConvId(String convId) {
        super.setString("convId", convId);
    }
	
    /**
     * @return Returns the refToMessageId.
     */
    public String getRefToMessageId() {
        return super.getString("refToMessageId");
    }

    /**
     * @param refToMessageId
     *            The refToMessageId to set.
     */
    public void setRefToMessageId(String refToMessageId) {
        super.setString("refToMessageId", refToMessageId);
    }

    /**
     * @return Return whether the response EbMS message should be included in same SOAP connection. 
     */
    public String getSyncReply() {
        return super.getString("syncReply");
    }

    /**
     * The available <code>syncReply</code> option in EbMS are listed below:
     * <ol>	
     * 	<li>mshSignalsOnly (same connection reply)</li>
     * 	<li>none (different connection reply)</li>
     * </ol>
     * 
     * @param syncReply
     * 			The syncReply option for this message.  
     */
    public void setSyncReply(String syncReply) {
        super.setString("syncReply", syncReply);
    }
    
    /**
     * @return Returns the dupElimination.
     */
    public String getDupElimination() {
        return super.getString("dupElimination");
    }

    /**
     * @param dupElimination
     *            The dupElimination to set.
     */
    public void setDupElimination(String dupElimination) {
        super.setString("dupElimination", dupElimination);
    }
    
    /**
     * @return Returns the ackRequested.
     */
    public String getAckRequested() {
        return super.getString("ackRequested");
    }

    /**
     * @param ackRequested
     *            The ackRequested to set.
     */
    public void setAckRequested(String ackRequested) {
        super.setString("ackRequested", ackRequested);
    }

    /**
     * @return String 
     */
    public String getAckSignRequested() {
        return super.getString("ackSignRequested");
    }

    /**
     * @param ackSignRequested
     */
    public void setAckSignRequested(String ackSignRequested) {
        super.setString("ackSignRequested", ackSignRequested);
    }
    
    /**
     * @return Returns the sequenceNo.
     */
    public int getSequenceNo() {
        return super.getInt("sequenceNo");
    }

    /**
     * @param sequenceNo
     *            The sequenceNo to set.
     */
    public void setSequenceNo(int sequenceNo) {
        super.setInt("sequenceNo", sequenceNo);
    }

    /**
     * @return Returns the sequenceGroup.
     */
    public int getSequenceGroup() {
        return super.getInt("sequenceGroup");
    }

    /**
     * @param sequenceGroup
     *            The sequenceGroup to set.
     */
    public void setSequenceGroup(int sequenceGroup) {
        super.setInt("sequenceGroup", sequenceGroup);
    }

    /**
     * 
     */
    public int getSequenceStatus() {
        return super.getInt("sequenceStatus");
    }

    /**
     * 
     */
    public void setSequenceStatus(int sequenceStatus) {
        super.setInt("sequenceStatus", sequenceStatus);
    }

    /**
     * @return Returns the timeToLive.
     */
    public Timestamp getTimeToLive() {
        return (Timestamp) super.get("timeToLive");
    }

    /**
     * @param timeToLive
     *            The timeToLive to set.
     */
    public void setTimeToLive(Timestamp timeToLive) {
        super.put("timeToLive", new NullableObject(timeToLive, Types.TIMESTAMP));
    }
    
    /**
     * @return Returns the timeStamp.
     */
    public Timestamp getTimeStamp() {
        return (Timestamp)super.getTimestamp("timeStamp");
    }

    /**
     * @param timeStamp
     *            The timeStamp to set.
     */
    public void setTimeStamp(Timestamp timeStamp) {
        super.put("timeStamp", new NullableObject(timeStamp, Types.TIMESTAMP));
    }
    
    /** 
     * @return 	Returns the timeout timestamp for this message. return null 
     * 			if the message does not requires acknowledgment.  			 
     */
    public Timestamp getTimeoutTimestamp(){
    	return (Timestamp) super.getTimestamp("timeoutTimestamp");
    }
    
    /** 
     * @param timeoutTimestamp
     * 			The timeout timestamp for this message. 
     */
    public void setTimeoutTimestamp(Timestamp timeoutTimestamp){
    	super.put("timeoutTimestamp", new NullableObject(timeoutTimestamp, Types.TIMESTAMP));
    }

    /**
     * @return Returns the principalId.
     * @deprecated principal Id is no longer used.
     */
    public String getPrincipalId() {
        return super.getString("principalId");
    }

    /**
     * @param principalId The principalId to set.
     * @deprecated principal Id is no longer used.
     */
    public void setPrincipalId(String principalId) {
        super.setString("principalId", principalId);
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return super.getString("status");
    }

    /**
     * @param status The status to set.
     */
    public void setStatus(String status) {
        super.setString("status", status);
    }

    /**
     * 
     */
    public String getStatusDescription() {
        return super.getString("statusDescription");
    }

    /**
     * 
     */
    public void setStatusDescription(String statusDescription) {
        super.setString("statusDescription", statusDescription);
    }

    /**
     * @return The primalMessageID which represent the message triggered "Resend as New"
     */
	public String getPrimalMessageId() {
		return super.getString("primalMessageId");
	}

    /**
     * Set the primalMessageID which represent the message triggered "Resend as New"
     * @param primalMessageId
     */
	public void setPrimalMessageId(String primalMessageId) {
		super.setString("primalMessageId", primalMessageId);
	}

    /**
     * @return "true" if message has triggered "Resend as New", "false" if otherwise
     */
	public String getHasResendAsNew() {
		return super.getString("hasResendAsNew");
	}

    /**
     * @param hasResendAsNew Set to "true" if message has triggered "Resend as New", "false" if otherwise
     */
	public void setHasResendAsNew(String hasResendAsNew) {
		super.setString("hasResendAsNew", hasResendAsNew);		
	}
	
    public void setPartnershipId(String partnershipId) {
    	super.setString("partnershipId", partnershipId);
    }
    
    public String getPartnershipId() {
    	return super.getString("partnershipId");
    }
}