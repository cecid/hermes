/**
 * Provides inferace for the database access object (DAO and DVO) 
 * for the database layer. 
 */
package hk.hku.cecid.edi.sfrm.dao;

import java.security.cert.X509Certificate;
import java.sql.Timestamp;

import hk.hku.cecid.edi.sfrm.spa.SFRMException;
import hk.hku.cecid.piazza.commons.dao.DVO;

/**
 * This is the object that represents a record of SFRMMessage 
 * in the database.<br><br>
 * 
 * Creation Date: 29/9/2006
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	Hermes 0818
 */
public interface SFRMMessageDVO extends DVO {

	/** 
	 * @return the message id of the Message DVO.
	 */
	public String getMessageId();
	
	/** 
	 * @param messageId
	 * 			the message id of the Message DVO.
	 */
	public void setMessageId(String messageId);
	
	/** 
	 * @return the message box of the Message DVO.
	 */
	public String getMessageBox();
	
	/** 
	 * @param messageBox
	 * 			the message box of the Message DVO.
	 */
	public void setMessageBox(String messageBox);

	
	/** 
	 * @return the partnership id of the Message DVO. 
	 */
	public String getPartnershipId();
	
	/** 
	 * @param partnershipId
	 * 			the partnership id of the Message DVO.
	 */
	public void setPartnershipId(String partnershipId);

	/** 
	 * @return the partnership endpoint of the Message DVO.
	 */
	public String getPartnerEndpoint();
	
	/** 
	 * @param partnerEndpoint
	 * 			the partnership endpoint of the Message DVO.
	 */
	public void setPartnerEndpoint(String partnerEndpoint);
	
	public int getTotalSegment();
	
	public void setTotalSegment(int totalSegment);
	
	public long getTotalSize();
	
	public void setTotalSize(long totalSize);
		
//	public boolean getIsSigned();
//	
//	public void setIsSigned(boolean isSigned);
//	
//	public boolean getIsEncrypted();
//	
//	public void setIsEncryped(boolean isEncrypted);
	
	//Newly added column
	public boolean getIsHostnameVerified();
	
	public void setIsHostnameVerified(boolean isVerified);
	
	public String getPartnerCertContent();
	
	public void setPartnerCertContent(String certContent);
	
	public X509Certificate getPartnerX509Certificate() throws SFRMException;
	//End Newly Added column
	public String getSignAlgorithm();
	
	public void setSignAlgorithm(String aignAlgorithm);
	
	public String getEncryptAlgorithm();
	
	public void setEncryptAlgorithm(String encryptAlgorithm);
			
	public String getStatus();
	
	public void setStatus(String status);
	
	public String getStatusDescription();
	
	public void setStatusDescription(String statusDescription);
	
	public Timestamp getCreatedTimestamp();
	
	public void setCreatedTimestamp(Timestamp createdTimestamp);
	
	public Timestamp getProceedTimestamp();
	
	public void setProceedTimestamp(Timestamp proceedTimestamp);
	
	public Timestamp getCompletedTimestamp();
	
	public void setCompletedTimestamp(Timestamp completedTimestamp);
	
	public String getFilename();
	
	public void setFilename(String filename);
}
