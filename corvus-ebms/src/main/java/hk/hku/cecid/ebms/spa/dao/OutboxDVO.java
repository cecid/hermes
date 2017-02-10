package hk.hku.cecid.ebms.spa.dao;

import hk.hku.cecid.piazza.commons.dao.DVO;

/**
 * @author Donahue Sze
 * 
 */
public interface OutboxDVO extends DVO {

    /**
     * @return Returns the ackRequested.
     */
    public String getMessageId();

    /**
     * @param messageId The messageId to set.
     */
    public void setMessageId(String messageId);

    /**
     * @return Returns the retried.
     */
    public int getRetried();

    /**
     * @param retried The retried to set.
     */
    public void setRetried(int retried);

}