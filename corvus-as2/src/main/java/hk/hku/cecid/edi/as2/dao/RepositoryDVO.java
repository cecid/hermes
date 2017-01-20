package hk.hku.cecid.edi.as2.dao;

import hk.hku.cecid.piazza.commons.dao.DVO;

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

    /**
     * @return Returns the content.
     */
    public byte[] getContent();

    /**
     * @param content The action to set.
     */
    public void setContent(byte[] content);

    /**
     * @return Returns the messageBox.
     */
    public String getMessageBox();

    /**
     * @param messageBox The messageBox to set.
     */
    public void setMessageBox(String messageBox);
}