package hk.hku.cecid.ebms.spa.dao;

import hk.hku.cecid.piazza.commons.dao.DVO;

/**
 * @author Donahue Sze
 * 
 */
public interface InboxDVO extends DVO {

    /**
     * @return Returns the ackRequested.
     */
    public String getMessageId();

    /**
     * @param messageId The messageId to set.
     */
    public void setMessageId(String messageId);
    
    public long getOrderNo();
    
    public void setOrderNo(long orderNo);

}