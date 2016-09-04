/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.dao;

import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDVO;

/**
 * @author Donahue Sze
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InboxDataSourceDVO extends DataSourceDVO implements
        InboxDVO {

    public InboxDataSourceDVO() {
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
    public long getOrderNo() {
        return super.getLong("orderNo");
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.RepositoryDVO#setMessageId(java.lang.String)
     */
    public void setOrderNo(long orderNo) {
        super.setLong("orderNo", orderNo);
    }

}