/**
 * Provides implementation class for the database access object 
 * (DAO and DVO) for the database layer. 
 */
package hk.hku.cecid.edi.sfrm.dao.ds;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.util.Hashtable;

import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.spa.SFRMException;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDVO;

/**
 * The <code>SFRMMessageDSDVO</code> is a data value object that represent
 * a tabular row in <em>sfrm_message</em> at the persistence layer.<br><br>   
 * 
 * It possesses caching automatically for most frequently fields shown below:
 * <ol>
 * 	<li>message id</li>
 * 	<li>message box</li>
 * 	<li>partnership id</li>
 * 	<li>partnership endpoint</li>
 * 	<li>requires signing / encryption</li>
 * 	<li>status</li>
 * </ol><br/>
 * 
 * So developers do not need to worry the issue of thread contention and 
 * can freely call the <em>get</em> and <em>set</em> with no performance impact.<br/> 
 * 
 * Creation Date: 29/9/2006<br><br>
 * 
 * Version 1.0.3 - 
 * <ul>
 * 	<li>Added cache for hot access field, it requires extra <em>22</em> bytes 
 *      per <code>SFRMMessageDSDVO</code> object.</li>
 * </ul>
 * 
 * Version 1.0.2 - 
 * <ul>
 * 	<li>Added conversation id</li>
 * </ul>
 * 
 * Version 2.0.0 -
 * <ul>
 * 	<li>Added sign algorithm</li>
 *  <li>Removed is signed</li>
 *  <li>Added encrypt algorithm</li>
 *  <li>Removed is encrypted</li>
 * 
 * </ul>
 * @author Twinsen Tsang
 * @version 1.0.3
 * @since	1.0.0
 */
public class SFRMMessageDSDVO extends DataSourceDVO implements SFRMMessageDVO {

	/**
	 * Compiler Generated Serial Version ID.
	 */
	private static final long serialVersionUID = 9058156951853018190L;
	
	/**
	 * The cached message id. [4B]
	 */
	private String messageId;

	/**
	 * The cached message box. [4B] 
	 */
	private String messageBox;
	
	/**
	 * The cached partnership id [4B]
	 */
	private String partnershipId; 
	
	/**
	 * The cached partnership endpoint [4B]
	 */
	private String partnerEndpoint;
	
	/**
	 * The cached sign algorithm ? [1B]
	 */
	private String signAlgorithm;
	
	/**
	 * The cached encrypt algorithm ? [1B]
	 */
	private String encryptAlgorithm;
		
	/**
	 * The cached status value. [4B]
	 */
	private String status;
	
	private boolean isHostnameVerified;
	
	private String partnerCertContent;
	
	/**
	 * [@OVERRIDE] set the DVO interval dataset and update some 
	 * <code>boolean<code> cached values. 
	 */
	public void setData(Hashtable hs){
		super.setData(hs);
		
		this.signAlgorithm = super.getString("signAlgorithm");
		this.encryptAlgorithm = super.getString("encryptAlgorithm");
	}

	/**
	 * [@GET, THREAD-SAFETY, CACHABLE] Get the message id from the message DVO.
	 */
	public String getMessageId(){
		if (this.messageId == null)
			this.messageId = super.getString("messageId");
		return this.messageId;
	}
	
	/**
	 * [@SET, THREAD-SAFETY, CACHABLE] Set the message id from the message DVO.
	 * 
	 * @param messageId the new message id.
	 */
	public void setMessageId(String messageId){
		super.setString("messageId", messageId);
		this.messageId = messageId;
	}
		
	/**
	 * [@GET, THREAD-SAFETY, CACHABLE] 
	 * 
	 * @return the message box from the message DVO.
	 */
	public String getMessageBox(){
		if (this.messageBox == null)
			this.messageBox = super.getString("messageBox");
		return this.messageBox;
	}
	
	/**
	 * [@SET, THREAD-SAFETY, CACHABLE] Set the message box to the message DVO.
	 *  
	 * @param message box either <strong>INBOX</strong> OR <strong>OUTBOX</strong> 
	 */
	public void setMessageBox(String messageBox){
		super.setString("messageBox", messageBox);
		this.messageBox = messageBox;
	}
	
	/**
	 * [@GET, THREAD-SAFETY, CACHABLE] 
	 * 
	 * @return the partnership id from the message DVO.
	 */
	public String getPartnershipId(){
		if (this.partnershipId == null)			
			this.partnershipId = super.getString("partnershipId");
		return this.partnershipId;
	}
		
	/**
	 * [@SET, THREAD-SAFETY, CACHABLE] Set the partnership id to the message DVO.
	 *  
	 * @param partnershipId the partnership id of this message DVO. 
	 */
	public void setPartnershipId(String partnershipId){		
		super.setString("partnershipId", partnershipId);
		this.partnershipId = partnershipId;
	}
		
	/**
	 * [@GET, THREAD-SAFETY, CACHABLE] 
	 * 
	 * @return the partnership endpoint from the message DVO.
	 */
	public String getPartnerEndpoint(){
		if (this.partnerEndpoint == null) 
			this.partnerEndpoint = super.getString("partnerEndpoint");
		return this.partnerEndpoint;
	}
	
	/**
	 * [@SET, THREAD-SAFETY, CACHABLE] Set the partnership endpoint to the message DVO.
	 *  
	 * @param partnershipId the partnership endpoint of this message DVO. 
	 */
	public void setPartnerEndpoint(String partnerEndpoint){
		super.setString("partnerEndpoint", partnerEndpoint);
		this.partnerEndpoint = partnerEndpoint;
	}
	
	/**
	 * [@GET, THREAD-SAFETY]
	 * 
	 * @return the total segment of this message DVO.
	 */
	public int getTotalSegment(){
		return super.getInt("totalSegment");
	}
	
	/**
	 * [@SET, THREAD-SAFETY] Set the total segment of this message DVO.
	 * 
	 * @param totalSegment the total segment of this message DVO.
	 */
	public void setTotalSegment(int totalSegment){
		super.setInt("totalSegment", totalSegment);
	}
	
	/**
	 * [@GET, THREAD-SAFETY]
	 * 
	 * @return the total size of this message DVO.
	 */
	public long getTotalSize(){
		return super.getLong("totalSize");
	}	
	
	/**
	 * [@SET, THREAD-SAFETY] Set the total size of this message DVO.
	 * 
	 * @param totalSegment the total size of this message DVO.
	 */
	public void setTotalSize(long totalSize){
		super.setLong("totalSize", totalSize);
	}		
	
	public void setIsHostnameVerified(boolean isHostnameVerified) {
		this.isHostnameVerified = isHostnameVerified;
		super.setBoolean("isHostnameVerified", isHostnameVerified);
	}

	public boolean getIsHostnameVerified() {
		return isHostnameVerified;
	}

	public void setPartnerCertContent(String partnerCertContent) {
		this.partnerCertContent = partnerCertContent;
		super.setString("partnerCertContent", partnerCertContent);
	}

	public String getPartnerCertContent() {
		return getString("partnerCertContent");
	}
	
	public X509Certificate getPartnerX509Certificate() throws SFRMException{
		String certContent = getPartnerCertContent();
		if(certContent == null){
			return null;
		}
				
		ByteArrayInputStream certStream = new ByteArrayInputStream(certContent.getBytes());
						
		try{
			X509Certificate cert = (X509Certificate) CertificateFactory
				.getInstance("X.509")
				.generateCertificate(certStream);
			return cert;
		}catch(Exception e){
			throw new SFRMException("Unable to load the SFRM partnership certificate" + e);
		}finally{
			try{
				certStream.close();
				certStream = null;
			}catch(Exception ie){
				throw new SFRMException("Error when closing the certificate stream", ie);
			}
		}
	}

	/**
	 * @return the sign algorithm of this message, return null if message not need to sign
	 */
	public String getSignAlgorithm(){
		return this.signAlgorithm;
	}
	
	/**
	 * Set the signing algorithm of this message
	 * @param sAlgorithm sign algorithm, null if message didn't require signing
	 */
	public void setSignAlgorithm(String sAlgorithm){
		super.setString("signAlgorithm", sAlgorithm);
		this.signAlgorithm = sAlgorithm;
	}
	
	/**
	 * @return the encrypt algorithm of this message, return null if message not need to encrypt
	 */
	public String getEncryptAlgorithm(){
		return this.encryptAlgorithm;
	}
	
	/**
	 * Set the encrypt algorithm of this message
	 * @param eAlgorithm encrypt algorithm, null if message didn't require encryption	
	 */
	public void setEncryptAlgorithm(String eAlgorithm){
		super.setString("encryptAlgorithm", eAlgorithm);
		this.encryptAlgorithm = eAlgorithm;
	}
	
	/**
	 * [@GET, THREAD-SAFETY, CACHABLE] 
	 * 
	 * @return get the status of the message DVO. 
	 */
	public String getStatus(){
		if (this.status == null)
			this.status = super.getString("status");
		return this.status;
	}
	
	/**
	 * [@SET, THREAD-SAFETY, CACHABLE]
	 * 
	 * @param status The new status of message DVO.
	 */
	public void setStatus(String status){
		SFRMProcessor.getInstance().getLogger().debug("Inside mDVO status to: " + status);
		super.setString("status", status);
		this.status = status;
	}
	
	/**
	 * [@GET, THREAD-SAFETY]
	 * 
	 * @return the brief description about the message status.
	 */
	public String getStatusDescription(){
		return super.getString("statusDescription");
	}
	
	/**
	 * [@SET, THREAD-SAFETY] Set the brief description about the message status.
	 * 
	 * @param statusDescription the brief description about the message status. 
	 */
	public void setStatusDescription(String statusDescription){
		super.setString("statusDescription", statusDescription);
	}
	
	/**
	 * [@GET, THREAD-SAFETY] 
	 * 
	 * @return the creation timestamp of this message.
	 */
	public Timestamp getCreatedTimestamp(){
		return (Timestamp) super.get("createdTimestamp");
	}
	
	/**
	 * [@SET, THREAD-SAFETY]
	 * 
	 * @param createdTimestamp set the creation timestamp of this message.
	 */
	public void setCreatedTimestamp(Timestamp createdTimestamp){
		super.put("createdTimestamp", createdTimestamp);
	}
	
	/**
	 * [@GET, THREAD-SAFETY] 
	 * 
	 * @return the timestamp that message is proceeding.
	 */
	public Timestamp getProceedTimestamp(){
		return (Timestamp) super.get("proceedTimestamp");
	}
	
	/**
	 * [@SET, THREAD-SAFETY]
	 * 
	 * @param proceedTimestamp set the timestamp that message is proceeding.
	 */
	public void setProceedTimestamp(Timestamp proceedTimestamp){
		super.put("proceedTimestamp", proceedTimestamp);
	}
	
	/**
	 * [@GET, THREAD-SAFETY] 
	 * 
	 * @return the timstamp that the message has been processed completely. 	
	 */
	public Timestamp getCompletedTimestamp(){
		return (Timestamp) super.get("completedTimestamp");
	}
	
	/**
	 * [@SET, THREAD-SAFETY]
	 * 
	 * @param the timestamp that the message has been processed completely. 
	 */
	public void setCompletedTimestamp(Timestamp completedTimestamp){
		super.put("completedTimestamp", completedTimestamp);
	}
	
	/**
	 * [@GET, THREAD-SAFETY]
	 * 
	 * @return filename field that represent the filename of the original file, if the message payload is not packed in tar format
	 */
	public String getFilename(){
		return super.getString("filename");
	}
	
	/**
	 * [@SET, THREAD-SAFETY]
	 * 
	 * @param filename filename field that represent the filename of the original file, if the message payload is not packed in tar format
	 */
	public void setFilename(String filename){
		super.setString("filename", filename);
	}
	
}
