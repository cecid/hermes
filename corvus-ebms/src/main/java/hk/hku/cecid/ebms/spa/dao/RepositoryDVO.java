/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.dao;

import hk.hku.cecid.piazza.commons.dao.DVO;

import java.io.InputStream;
import java.sql.Timestamp;

/**
 * @author Donahue Sze
 * 
 */
public interface RepositoryDVO extends DVO {
    /**
     * @return Returns the messageId.
     */
    public String getMessageId();

    /**
     * @param messageId The messageId to set.
     */
    public void setMessageId(String messageId);
    
    public String getContentType();
    
    public void setContentType(String contentType);

    /**
     * @return Returns the content.
     */
    public byte[] getContent();

    /**
     * @param content The content to set.
     */
    public void setContent(byte[] content);
    
    public void setContent(InputStream is);

    /**
     * @return Returns the timeStamp.
     */
    public Timestamp getTimeStamp();

    /**
     * @param timeStamp The timeStamp to set.
     */
    public void setTimeStamp(Timestamp timeStamp);

    /**
     * @return Returns the messageBox.
     */
    public String getMessageBox();

    /**
     * @param messageBox The messageBox to set.
     */
    public void setMessageBox(String messageBox);
}