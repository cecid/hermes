/**
 * Provides implementation class for the database access object 
 * (DAO and DVO) for the database layer. 
 */
package hk.hku.cecid.edi.sfrm.dao.ds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.sql.Timestamp;

import hk.hku.cecid.edi.sfrm.spa.SFRMException;
import hk.hku.cecid.edi.sfrm.spa.SFRMProperties;

import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDVO;

import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDVO;


/**
 * The <code>SFRMPartnershipDSDVO</code> is a data value object representing
 * a tabular row in the <em>sfrm_partnership</em> in the persistence layer.<br/><br/>
 * 
 * Creation Date: 27/9/2006<br/>
 * 
 * It possesses caching automatically for most frequently fields shown below:
 * <ol>
 * 	<li>partnership Id</li>
 * 	<li>partnership endpoint</li>
 * 	<li>maximum retry allowed</li>
 * 	<li>retry interval</li>
 * 	<li>X509 verfication / encryption cerfiticates</li>
 * </ol><br/> 
 * 
 * So developers do not need to worry the issue of thread contention and 
 * can freely call the <em>get</em> and <em>set</em> with no performance impact.<br/>  
 * 
 * Version 1.0.1 - 
 * <ol>
 * 	<li>Added cache for hot access field, it requires extra <em>17</em> bytes + 1 soft reference 
 *      per <code>SFRMPartnershipDSDVO</code> object.</li>
 * 
 * 
 * @author Twinsen Tsang
 * @version 1.0.1
 * @since	1.0.0
 */
public class SFRMPartnershipDSDVO extends DataSourceDVO implements
		SFRMPartnershipDVO {		
	/**
	 * Backward Compatible serial version UID.
	 */
	private static final long serialVersionUID = 4453567496887231495L;
	
	/**
	 * The cached partnership Id; [4B]
	 */
	private String partnershipId;
	
	/**
	 * The cached partnership endpoint; [4B]
	 */
	private String partnerEndpoint;
	
	/**
	 * The cached retry max. [4B]
	 */
	private int retryMax = Integer.MIN_VALUE;
	
	/**
	 * The cached retry interval. [4B]
	 */
	private int retryInterval = Integer.MIN_VALUE;
	
	/** 
	 * Constructor.
	 */
	public SFRMPartnershipDSDVO(){}
	
	/**
	 * [@GET, THREAD-SAFETY] Get the partnership sequence no from this partnership DVO.
	 */
	public int getPartnershipSeq(){
		return super.getInt("partnershipSeq");
	}

	/**
	 * [@SET, THREAD-SAFETY] Set the partnership sequence
	 */
	public void setPartnershipSeq(int partnershipSeq){
		super.setInt("partnershipSeq", partnershipSeq);
	}
		
	/**
	 * [@GET, THREAD-SAFETY, CACHABLE]<br/><br/>
	 * 
	 * Get the partnership from this partnership DVO.
	 */
	public String getPartnershipId(){
		// TODO: Make it become thread-safety
		// Multiple access requires super.getString twice or more in order to cache it 
		// into variable.
		if (this.partnershipId == null){
			String partnershipId = super.getString("partnershipId");
			this.partnershipId = partnershipId; 
		}
		return this.partnershipId;
	}

	/**
	 * [@SET, THREAD-SAFETY] Set the new partnership id to this partnership DVO.
	 * 
	 * @param partnershipId The new partnership Id.
	 */
	public void setPartnershipId(String partnershipId){		
		super.setString("partnershipId", partnershipId);
		// cache value.
		this.partnershipId = partnershipId;
	}

	/**
	 * [@GET, THREAD-SAFETY] Get the description of the partnership DVO.
	 */
	public String getDescription(){
		return super.getString("description");
	}

	/**
	 * [@SET, THREAD-SAFETY] Set the new description to this partnership DVO. 
	 * 
	 * @param description The new description.
	 */
	public void setDescription(String description){
		super.setString("description", description);
	}

	/**
	 * [@GET, THREAD-SAFETY] Get the sending endpoint of the partnership.
	 * 
	 * The endpoint in the database stores only the 
	 * address of receiver. For example, like
	 * <strong>http://127.0.0.1:8080/</strong> or
	 * <strong>http://sfrm.partnership.com:8080/</strong><br><br>
	 * 
	 * But the endpoint returned here will concat a designated
	 * conext path = "corvus/httpd/sfrm/inbound".
	 */
	public String getPartnerEndpoint() {
		String endPoint =  super.getString("partnerEndpoint");
		if (this.partnerEndpoint == null && endPoint != null){
			if (!endPoint.endsWith("/"))
				endPoint += "/";		
			endPoint += CONTEXT_PATH;
			this.partnerEndpoint = endPoint;
		}		
		return this.partnerEndpoint;
	}
	
	/**
	 * Get the sending endpoint of the partnership without appended the context path
	 */
	public String getOrgPartnerEndpoint(){
		return super.getString("partnerEndpoint");
	}

	/**
	 * [@GET, THREAD-SAFETY] Set the partnership endpoint of the partnership DVO.
	 * 
	 * @param endpoint The new partnership endpoint.
	 */
	public void setPartnerEndPoint(String endpoint) {
		super.setString("partnerEndpoint", endpoint);
		this.partnerEndpoint = endpoint;
	}
	
	/**
	 * [@GET, THREAD-SAFETY] Get the partnership endpoint of this partnership DVO.
	 */
	public String getPartnerCertFingerprint(){
		return super.getString("partnerCertFingerprint");
	}
	
	/**
	 * [@SET, THREAD-SAFETY] Set the partnership endpoint of this partnership DVO.
	 */
	public void setPartnerCertFingerprint(String partnerCertFingerprint){
		super.setString("partnerCertFingerprint", partnerCertFingerprint);
	}	

	/**
	 * [@GET, THREAD-SAFETY] whether the partnership requires SSL hostname verified.
	 */
	public boolean isHostnameVerified() {
		return super.getBoolean("isHostnameVerified");
	}

	/**
	 * [@SET, THREAD-SAFETY] set whether the partnership requires SSL hostname verified.   
	 */
	public void setIsHostnameVerified(boolean isHostnameVerified) {
		super.setBoolean("isHostnameVerified", isHostnameVerified);
	}

	public String getSignAlgorithm() {
		return super.getString("signAlgorithm");
	}

	public void setSignAlgorithm(String signAlgorithm) {
		super.setString("signAlgorithm", signAlgorithm);
	}

	public String getEncryptAlgorithm() {
		return super.getString("encryptAlgorithm");
	}

	public void setEncryptAlgorithm(String encryptAlgorithm) {
		super.setString("encryptAlgorithm", encryptAlgorithm);
	}
	
	/**
	 * [@GET, THREAD-SAFETY] Get the maximum retry allowed for this partnership DVO.  
	 */
	public int getRetryMax() {
		if (this.retryMax == Integer.MIN_VALUE){
			int ret = this.getInt("retryMax");
			this.retryMax = ret;
		}
		return this.retryMax;
	}

	/**
	 * [@SET, THREAD-SAFETY] Set the maximum retry allowed for this partnership DVO.
	 */
	public void setRetryMax(int retryMax) {		
		this.setInt("retryMax", retryMax);
		this.retryMax = retryMax;
	}

	/**
	 * [@GET, NON-THREAD-SAFETY] Get the retry interval of this partnership DVO.
	 */
	public int getRetryInterval() {
		if (this.retryInterval == Integer.MIN_VALUE){
			int ret = this.getInt("retryInterval");
			this.retryInterval = ret;
		}
		return this.retryInterval;
	}

	/**
	 * [@SET, THREAD-SAFETY] Set the retry interval of this partnership DVO.
	 */
	public void setRetryInterval(int retryInterval) {
		super.setInt("retryInterval", retryInterval);
		this.retryInterval = retryInterval;
	}

	public boolean isDisabled() {
		return super.getBoolean("isDisabled");
	}

	public void setIsDisabled(boolean isDisabled) {
		super.setBoolean("isDisabled", isDisabled);
	}

	/**
	 * [@GET, THREAD-SAFETY] 
	 * 
	 * @param get the creation timestamp of this partnership record.
	 */
	public Timestamp getCreationTimestamp() {
		return (Timestamp)super.get("createdTimestamp");
	}

	/**
	 * [@SET, THREAD-SAFETY] Set the creation timestamp.
	 * 
	 * @param creationTimestamp the new value of the creation time stamp for this partnership DVO.
	 */
	public void setCreationTimestamp(Timestamp creationTimestamp) {
		super.put("createdTimestamp", creationTimestamp);
	}

	/**
	 * [@GET, THREAD-SAFETY] 
	 * 
	 * @return Get the last modified timestamp
	 */
	public Timestamp getModifiedTimestamp() {
		return (Timestamp)this.get("modifiedTimestamp");
	}

	/**
	 * [@GET, THREAD-SAFETY] Set the last modified timestamp
	 * 
	 * @param modifiedTimestamp the last modified timestamp.
	 */
	public void setModifiedTimestamp(Timestamp modifiedTimestamp) {
		this.put("modifiedTimestamp", modifiedTimestamp);
	}

	
	/**
	 * Get X509 certificate from trusted certificate store specified in SFRM properties
	 * 
	 * @return X509 certificate
	 * @throws SFRMException 
	 */
	public X509Certificate getVerifyX509Certificate() throws SFRMException{
		return getX509Certificate(new File(SFRMProperties.getTrustedCertStore()
				,this.getPartnerCertFingerprint()));
	}
	
	/**
	 * Get X509 certificate from trusted certificate store specified in SFRM properties
	 * 
	 * @return X509 certificate
	 * @throws SFRMException 
	 */
	public X509Certificate getEncryptX509Certificate() throws SFRMException{
		if(this.getPartnerCertFingerprint() != null){
			return getX509Certificate(new File(SFRMProperties.getTrustedCertStore()
					,this.getPartnerCertFingerprint()));
		}
		return null;
	}
	
	/**
	 * Get the X509 Verification / Encryption
	 * certificates.
	 * 
	 * @param certFile The file with fingerprint as file name of the public certificate 			
	 * @return X509 certificate 
	 */
	private X509Certificate getX509Certificate(File certFile) throws SFRMException {
		try {
			X509Certificate cert;
			
			if (!certFile.exists())
				throw new SFRMException("Missing certs with finger print:" + 
						certFile);
			
			BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(certFile));			
			byte[] bs = IOHandler.readBytes(bis);							 			
			InputStream certStream = new ByteArrayInputStream(bs);
			cert = (X509Certificate) CertificateFactory
				.getInstance("X.509")
				.generateCertificate(certStream);
			bis.close();
			certStream.close();

			return cert;
			
		} catch (Exception e){
			throw new SFRMException("Unable to load the certificates with fingerprint: "
				   + certFile, e);
		}
	}
	
	public String getEncryptX509CertificateBase64() throws FileNotFoundException, IOException{
		File certFile = new File(SFRMProperties.getTrustedCertStore(), this.getPartnerCertFingerprint());
		InputStreamReader certReader = new InputStreamReader(new FileInputStream(certFile));
		String certContent = IOHandler.readString(certReader);
		return certContent;
	}
	
}
