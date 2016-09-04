/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.dao;

import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDVO;
import hk.hku.cecid.piazza.commons.util.Convertor;

import java.util.Date;

/**
 * @author Donahue Sze
 *  
 */
public class MessageDataSourceDVO extends DataSourceDVO implements
        MessageDVO {

    public MessageDataSourceDVO() {
        super();
    }

    /**
     * @return Returns the AS2 From.
     */
    public String getAs2From() {
        return super.getString("as2From");
    }

    /**
     * @param as2From The AS2 From to set.
     */
    public void setAs2From(String as2From) {
        super.setString("as2From", as2From);
    }

    /**
     * @return Returns the AS2 To.
     */
    public String getAs2To() {
        return super.getString("as2To");
    }

    /**
     * @param as2To The AS2 To to set.
     */
    public void setAs2To(String as2To) {
        super.setString("as2To", as2To);
    }

    /**
     * @return Returns the messageId.
     */
    public String getMessageId() {
        return super.getString("messageId");
    }

    /**
     * @param messageId The messageId to set.
     */
    public void setMessageId(String messageId) {
        super.setString("messageId", messageId);
    }

    /**
     * @return Returns the messageBox.
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
     * @return Returns the originalToMessageId.
     */
    public String getOriginalMessageId() {
        return super.getString("originalMessageId");
    }

    /**
     * @param originalToMessageId The originalToMessageId to set.
     */
    public void setOriginalMessageId(String originalToMessageId) {
        super.setString("originalMessageId", originalToMessageId);
    }

    /**
     * @return Returns the MIC value.
     */
    public String getMicValue() {
        return super.getString("micValue");
    }

    /**
     * @param micValue The MIC value to set.
     */
    public void setMicValue(String micValue) {
        super.setString("micValue", micValue);
    }

    /**
     * @return Returns the timeStamp.
     */
    public Date getTimeStamp() {
        return super.getDate("timeStamp");
    }

    /**
     * @param timeStamp The timeStamp to set.
     */
    public void setTimeStamp(Date timeStamp) {
        super.put("timeStamp", Convertor.toTimestamp(timeStamp));
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return super.getString("status");
    }

    /**
     * @param status The service to set.
     */
    public void setStatus(String status) {
        super.setString("status", status);
    }

    /**
     * setIsReceiptRequested
     * 
     * @param isReceiptRequested
     * @see hk.hku.cecid.edi.as2.dao.MessageDVO#setIsReceiptRequested(boolean)
     */
    public void setIsReceiptRequested(boolean isReceiptRequested) {
        super.setString("isReceiptRequested", String
                .valueOf(isReceiptRequested));
    }

    /**
     * setIsAcknowledged
     * 
     * @param isAcknowledged
     * @see hk.hku.cecid.edi.as2.dao.MessageDVO#setIsAcknowledged(boolean)
     */
    public void setIsAcknowledged(boolean isAcknowledged) {
        super.setString("isAcknowledged", String.valueOf(isAcknowledged));
    }

    /**
     * setIsReceipt
     * 
     * @param isReceipt
     * @see hk.hku.cecid.edi.as2.dao.MessageDVO#setIsReceipt(boolean)
     */
    public void setIsReceipt(boolean isReceipt) {
        super.setString("isReceipt", String.valueOf(isReceipt));
    }

    /**
     * isReceiptRequested
     * 
     * @return @see hk.hku.cecid.edi.as2.dao.MessageDVO#isReceiptRequested()
     */
    public boolean isReceiptRequested() {
        return super.getBoolean("isReceiptRequested");
    }

    /**
     * isAcknowledged
     * 
     * @return @see hk.hku.cecid.edi.as2.dao.MessageDVO#isAcknowledged()
     */
    public boolean isAcknowledged() {
        return super.getBoolean("isAcknowledged");
    }

    /**
     * isReceipt
     * 
     * @return @see hk.hku.cecid.edi.as2.dao.MessageDVO#isReceipt()
     */
    public boolean isReceipt() {
        return super.getBoolean("isReceipt");
    }

    /**
     * getReceiptUrl
     * 
     * @return @see hk.hku.cecid.edi.as2.dao.MessageDVO#getReceiptUrl()
     */
    public String getReceiptUrl() {
        return super.getString("receiptUrl");
    }

    /**
     * setReceiptUrl
     * 
     * @param url
     * @see hk.hku.cecid.edi.as2.dao.MessageDVO#setReceiptUrl(java.lang.String)
     */
    public void setReceiptUrl(String url) {
        super.setString("receiptUrl", url);
    }

    /**
     * setStatusDescription
     * @param desc
     * @see hk.hku.cecid.edi.as2.dao.MessageDVO#setStatusDescription(java.lang.String)
     */
    public void setStatusDescription(String desc) {
        super.setString("statusDesc", desc);
    }

    /**
     * getStatusDescription
     * @return String
     * @see hk.hku.cecid.edi.as2.dao.MessageDVO#getStatusDescription()
     */
    public String getStatusDescription() {
        return super.getString("statusDesc");
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
	
	public String getPartnershipId() {
		return super.getString("partnershipId");
	}
	
	public void setPartnershipId(String partnershipId) {
		super.setString("partnershipId", partnershipId);
	}
}