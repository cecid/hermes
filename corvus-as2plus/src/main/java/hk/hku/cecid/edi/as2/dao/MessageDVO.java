/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.dao;

import hk.hku.cecid.piazza.commons.dao.DVO;

import java.util.Date;

/**
 * @author Donahue Sze
 *  
 */
public interface MessageDVO extends DVO {

    public static String STATUS_RECEIVED         = "RC";
    
    public static String STATUS_PENDING          = "PD";

    public static String STATUS_PROCESSING       = "PR";

    public static String STATUS_PROCESSED        = "PS";

    public static String STATUS_PROCESSED_ERROR  = "PE";

    public static String STATUS_DELIVERED        = "DL";

    public static String STATUS_DELIVERY_FAILURE = "DF";
    
    public static String MSGBOX_IN               = "IN";

    public static String MSGBOX_OUT              = "OUT";

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
     * @return Returns the AS2 From.
     */
    public String getAs2From();

    /**
     * @param as2From The AS2 From to set.
     */
    public void setAs2From(String as2From);

    /**
     * @return Returns the AS2 To.
     */
    public String getAs2To();

    /**
     * @param as2To The AS2 To to set.
     */
    public void setAs2To(String as2To);

    /**
     * @param isReceiptRequested The isReceiptRequested to set.
     */
    public void setIsReceiptRequested(boolean isReceiptRequested);

    /**
     * @param isAcknowledged The isAcknowledged to set.
     */
    public void setIsAcknowledged(boolean isAcknowledged);

    /**
     * @param isReceipt The isReceipt to set.
     */
    public void setIsReceipt(boolean isReceipt);

    /**
     * @return Returns the originalToMessageId.
     */
    public String getOriginalMessageId();

    /**
     * @param originalToMessageId The originalToMessageId to set.
     */
    public void setOriginalMessageId(String originalToMessageId);

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
    
    public String getReceiptUrl();

    public void setReceiptUrl(String url);

    /**
     * @return Returns the MIC value.
     */
    public String getMicValue();

    /**
     * @param micValue The MIC value to set.
     */
    public void setMicValue(String micValue);

    /**
     * @return Returns the timeStamp.
     */
    public Date getTimeStamp();

    /**
     * @param timeStamp The timeStamp to set.
     */
    public void setTimeStamp(Date timeStamp);

    /**
     * @return Returns the status.
     */
    public String getStatus();
    
    public void setStatusDescription(String desc);
    
    public String getStatusDescription();

    /**
     * @param status The status to set.
     */
    public void setStatus(String status);

    public boolean isReceiptRequested();

    public boolean isAcknowledged();

    public boolean isReceipt();
    
    public String getPartnershipId();
    
    public void setPartnershipId(String partnershipId);
}