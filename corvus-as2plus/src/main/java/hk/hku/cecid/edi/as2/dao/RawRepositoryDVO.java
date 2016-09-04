package hk.hku.cecid.edi.as2.dao;

import hk.hku.cecid.piazza.commons.dao.DVO;

public interface RawRepositoryDVO extends DVO {
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
}
