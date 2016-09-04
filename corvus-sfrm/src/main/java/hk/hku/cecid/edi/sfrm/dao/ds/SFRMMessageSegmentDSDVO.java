/**
 * Provides implementation class for the database access object 
 * (DAO and DVO) for the database layer. 
 */
package hk.hku.cecid.edi.sfrm.dao.ds;

import java.sql.Timestamp;

import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDVO;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDVO;

/**
 * 
 * Creation Date: 29/9/2006
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	Hermes 0818
 */
public class SFRMMessageSegmentDSDVO extends DataSourceDVO implements
		SFRMMessageSegmentDVO {

	/**
	 * Compiler Generated Serial Version ID.
	 */
	private static final long serialVersionUID = 8480293048512018760L;

	public String getMessageId(){
		return super.getString("messageId");
	}
	
	public void setMessageId(String messageId){
		super.setString("messageId", messageId);
	}
	
	public String getMessageBox(){
		return super.getString("messageBox");
	}
	
	public void setMessageBox(String messageBox){
		super.setString("messageBox", messageBox);
	}
	
	public int getSegmentNo(){
		return super.getInt("segmentNo");
	}
	
	public void setSegmentNo(int segmentNo){
		super.setInt("segmentNo", segmentNo);
	}
	
	public String getSegmentType(){
		return super.getString("segmentType");
	}
	
	public void setSegmentType(String segmentType){
		super.setString("segmentType", segmentType);
	}
	
	public long getSegmentStart(){
		return super.getLong("segmentStart");
	}
	
	public void setSegmentStart(long segmentStart){
		super.setLong("segmentStart", segmentStart);
	}
	
	public long getSegmentEnd(){
		return super.getLong("segmentEnd");
	}
	
	public void setSegmentEnd(long segmentEnd){
		super.setLong("segmentEnd", segmentEnd);
	}
	
	public int getRetried(){
		return super.getInt("retried");
	}
	
	public void setRetried(int retried){
		super.setInt("retried", retried);
	}
		
	public String getMD5Value(){
		return super.getString("MD5Value");
	}
	
	public void setMD5Value(String MD5Value){
		super.setString("MD5Value", MD5Value);
	}
	
	public String getStatus(){
		return super.getString("status");
	}
	
	public void setStatus(String status){
		super.setString("status", status);
	}
	
	public Timestamp getCreatedTimestamp(){
		return (Timestamp) super.get("createdTimestamp");
	}
	
	public void setCreatedTimestamp(Timestamp createdTimestamp){
		super.put("createdTimestamp", createdTimestamp); 
	}
	
	public Timestamp getProceedTimestamp(){
		return (Timestamp) super.get("proceedTimestamp");
	}
	
	public void setProceedTimestamp(Timestamp proceedTimestamp){
		super.put("proceedTimestamp", proceedTimestamp); 
	}
	
	public Timestamp getCompletedTimestamp(){
		return (Timestamp) super.get("completedTimestamp");
	}
	
	public void setCompletedTimestamp(Timestamp completedTimestamp){
		super.put("completedTimestamp", completedTimestamp); 
	}
}
