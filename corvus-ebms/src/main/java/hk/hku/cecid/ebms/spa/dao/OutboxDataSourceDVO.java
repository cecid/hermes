package hk.hku.cecid.ebms.spa.dao;

import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDVO;

/**
 * @author Donahue Sze
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OutboxDataSourceDVO extends DataSourceDVO implements
        OutboxDVO {

    public OutboxDataSourceDVO() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.RepositoryDVO#getMessageId()
     */
    public String getMessageId() {
        return super.getString("messageId");
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.RepositoryDVO#setMessageId(java.lang.String)
     */
    public void setMessageId(String messageId) {
        super.setString("messageId", messageId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.RepositoryDVO#getMessageId()
     */
    public int getRetried() {
        return super.getInt("retried");
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.RepositoryDVO#setMessageId(java.lang.String)
     */
    public void setRetried(int retried) {
        super.setInt("retried", retried);
    }

}