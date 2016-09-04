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
import hk.hku.cecid.piazza.commons.dao.ds.NullableObject;

import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * @author Donahue Sze
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RepositoryDataSourceDVO extends DataSourceDVO implements
        RepositoryDVO {

    public RepositoryDataSourceDVO() {
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

    public String getContentType() {
        return super.getString("contentType");
    }

    public void setContentType(String contentType) {
        super.setString("contentType", contentType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.RepositoryDVO#getContent()
     */
    public byte[] getContent() {
    	return (byte[])super.get("content");
    }    

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.RepositoryDVO#setContent(java.lang.String)
     */
    public void setContent(byte[] content) {
        super.put("content", content);
    }

    public void setContent(InputStream is) {
        super.put("content", is);
    }

    /**
     * @return Returns the timeStamp.
     */
    public Timestamp getTimeStamp() {
        return (Timestamp) super.get("timeStamp");
    }

    /**
     * @param timeStamp
     *            The timeStamp to set.
     */
    public void setTimeStamp(Timestamp timeStamp) {
        super.put("timeStamp", new NullableObject(timeStamp, Types.TIMESTAMP));
    }

    /**
     * @return Returns the messageBox.
     */
    public String getMessageBox() {
        return super.getString("messageBox");
    }

    /**
     * @param messageBox
     *            The messageBox to set.
     */
    public void setMessageBox(String messageBox) {
        super.setString("messageBox", messageBox);
    }
}