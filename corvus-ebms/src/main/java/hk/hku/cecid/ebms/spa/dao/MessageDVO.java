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

import hk.hku.cecid.piazza.commons.dao.DVO;

/**
 * The <code>MessageDataSourceDVO</code> is a implementation of interface
 * <code>MesageDVO</code> and representing one persistence record in the
 * table <em>message</em>.
 * 
 * @author Donahue Sze, Twinsen Tsang (modifier)   
 */
public interface MessageDVO extends DVO {
	
	/**
     * @return Returns the messageId.
     */
    public String getMessageId();

    /**
     * @param messageId The messageId to set.
     */
    public void setMessageId(String messageId);
    
    /**
     * @return Returns the messageType.
     */
    public String getMessageBox();

    /**
     * @param messageBox The messageBox to set.
     */
    public void setMessageBox(String messageBox);
    
    /**
     * @return Returns the messageType.
     */
    public String getMessageType();

    /**
     * @param messageType The messageType to set.
     */
    public void setMessageType(String messageType);
    
    /**
     * @return Returns the fromPartyId.
     */
    public String getFromPartyId();

    /**
     * @param fromPartyId The fromPartyId to set.
     */
    public void setFromPartyId(String fromPartyId);

    /**
     * @return Returns the fromPartyRole.
     */
    public String getFromPartyRole();

    /**
     * @param fromPartyRole The fromPartyRole to set.
     */
    public void setFromPartyRole(String fromPartyRole);

    /**
     * @return Returns the toPartyId.
     */
    public String getToPartyId();

    /**
     * @param toPartyId The toPartyId to set.
     */
    public void setToPartyId(String toPartyId);

    /**
     * @return Returns the toPartyRole.
     */
    public String getToPartyRole();

    /**
     * @param toPartyRole The toPartyRole to set.
     */
    public void setToPartyRole(String toPartyRole);
    
    /**
     * @return Returns the cpaId.
     */
    public String getCpaId();

    /**
     * @param cpaId The cpaId to set.
     */
    public void setCpaId(String cpaId);

    /**
     * @return Returns the action.
     */
    public String getAction();

    /**
     * @param action The action to set.
     */
    public void setAction(String action);

    /**
     * @return Returns the service.
     */
    public String getService();

    /**
     * @param service  The service to set.
     */
    public void setService(String service);
    
    /**
     * @return Returns the convId.
     */
    public String getConvId();

    /**
     * @param convId The convId to set.
     */
    public void setConvId(String convId);

    /**
     * @return Returns the refToMessageId.
     */
    public String getRefToMessageId();

    /**
     * @param refToMessageId The refToMessageId to set.
     */
    public void setRefToMessageId(String refToMessageId);
    
    /**
     * Set the primalMessageID which represent the message triggered "Resend as New"
     * @param primalMessageId
     */
    public void setPrimalMessageId(String primalMessageId);
    
    /**
     * @return String primalMessageId refer to the message that triggered "Resend as New"
     */
    public String getPrimalMessageId();
    
    /**
     * @param hasResendAsNew Set to "true" if message has triggered "Resend as New", "false" if otherwise
     */
    public void setHasResendAsNew(String hasResendAsNew);
    
    /**
     * @return "true" if message has triggered "Resend as New", "false" if otherwise.
     */
    public String getHasResendAsNew();
    
    /**
     * @return Return whether the response EbMS message should be included in same SOAP connection. 
     */
    public String getSyncReply();

    /**
     * The available <code>syncReply</code> option in EbMS are listed below:
     * <ol>	
     * 	<li>mshSignalsOnly (same connection reply)</li>
     * 	<li>none (different connection reply)</li>
     * </ol>
     * 
     * @param syncReply The syncReply option for this message.  
     */
    public void setSyncReply(String syncReply);
   
    /**
     * @return Returns the dupElimination.
     */
    public String getDupElimination();

    /**
     * @param dupElimination The dupElimination to set.
     */
    public void setDupElimination(String dupElimination);

    /**
     * @return Returns the ackRequested.
     */
    public String getAckRequested();

    /**
     * @param ackRequested The ackRequested to set.
     */
    public void setAckRequested(String ackRequested);
    
    public String getAckSignRequested();

    public void setAckSignRequested(String ackSignRequested);
    
    /**
     * @return Returns the sequenceNo.
     */
    public int getSequenceNo();

    /**
     * @param sequenceNo The sequenceNo to set.
     */
    public void setSequenceNo(int sequenceNo);
    
    public int getSequenceGroup();
    
    public void setSequenceGroup(int sequenceGroup);

    public int getSequenceStatus();
    
    public void setSequenceStatus(int sequenceStatus);

    /**
     * @return Returns the timeToLive.
     */
    public Timestamp getTimeToLive();
    
    /**
     * @param timeToLive The timeToLive to set.
     */
    public void setTimeToLive(Timestamp timeToLive);
    
    /**
     * @return Returns the timeStamp.
     */
    public Timestamp getTimeStamp();

    /**
     * @param timeStamp The timeStamp to set.
     */
    public void setTimeStamp(Timestamp timeStamp);

    /** 
     * @return 	Returns the timeout timestamp for this message. return null 
     * 			if the message does not requires acknowledgment.  			 
     */
    public Timestamp getTimeoutTimestamp();
    
    /** 
     * @param timeoutTimestamp The timeout timestamp for this message. 
     */
    public void setTimeoutTimestamp(Timestamp timeoutTimestamp);
    
    /**
     * @return Returns the principal id of this message.
     * @deprecated principle Id is no longer used.
     */
    public String getPrincipalId();

    /**
     * @param principalId The principalId to set.
     * @deprecated principle Id is no longer used.
     */
    public void setPrincipalId(String principalId);

    /**
     * @return Returns the status.
     */
    public String getStatus();

    /**
     * @param status The status to set.
     */
    public void setStatus(String status);
    
    public String getStatusDescription();
    
    public void setStatusDescription(String statusDescription);
    
    public void setPartnershipId(String partnershipId);
    
    public String getPartnershipId();
}