package hk.hku.cecid.edi.as2.dao;

import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDVO;

public class RawRepositoryDataSourceDVO extends DataSourceDVO implements RawRepositoryDVO {
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
}
