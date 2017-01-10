package hk.hku.cecid.edi.as2.dao;

import java.sql.SQLException;

import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDVO;

/**
 * @author Donahue Sze
 *  
 */
public class RepositoryDataSourceDVO extends DataSourceDVO implements
        RepositoryDVO {

    public RepositoryDataSourceDVO() {
        super();
    }

    public String getMessageId() {
        return super.getString("messageId");
    }

    public void setMessageId(String messageId) {
        super.setString("messageId", messageId);
    }

    public byte[] getContent() {
    	return (byte[])super.get("content");

    }    
    
    public void setContent(byte[] content) {
        super.put("content", content);
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
}