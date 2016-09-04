/**
 * Provides inferace for the database access object (DAO and DVO) 
 * for the database layer. 
 */
package hk.hku.cecid.edi.sfrm.dao;

import hk.hku.cecid.piazza.commons.dao.DVO;

import java.sql.Timestamp;

public interface SFRMMessageSegmentDVO extends DVO {

	public String getMessageId();
	
	public void setMessageId(String messageId);
	
	public String getMessageBox();
	
	public void setMessageBox(String messageBox);
	
	public int getSegmentNo();
	
	public void setSegmentNo(int segmentNo);
	
	public String getSegmentType();
	
	public void setSegmentType(String segmentType);
	
	public long getSegmentStart();
	
	public void setSegmentStart(long segmentStart);
	
	public long getSegmentEnd();
	
	public void setSegmentEnd(long segmentEnd);
	
	public int getRetried();
	
	public void setRetried(int retried);
	
	public String getMD5Value();
	
	public void setMD5Value(String MD5Value);
	
	public String getStatus();
	
	public void setStatus(String status);
	
	public Timestamp getCreatedTimestamp();
	
	public void setCreatedTimestamp(Timestamp createdTimestamp);
	
	public Timestamp getProceedTimestamp();
	
	public void setProceedTimestamp(Timestamp proceedTimestamp);
	
	public Timestamp getCompletedTimestamp();
	
	public void setCompletedTimestamp(Timestamp completedTimestamp);
}