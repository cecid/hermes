/**
 * Provides inferace for the database access object (DAO and DVO) 
 * for the database layer. 
 */
package hk.hku.cecid.edi.sfrm.dao;

import java.io.FileNotFoundException;
import java.sql.Timestamp;

import java.security.cert.X509Certificate;
import java.io.IOException;
//import hk.hku.cecid.edi.sfrm.dao.ds.IOException;
import hk.hku.cecid.edi.sfrm.spa.SFRMException;
import hk.hku.cecid.piazza.commons.dao.DVO;

/**
 * 
 * 
 * Creation Date: 27/9/2006
 * 
 * @author Twinsen
 * @version 1.0.0 
 */
public interface SFRMPartnershipDVO extends DVO {

	public static final String ALG_ENCRYPT_RC2 = "rc2";

	public static final String ALG_ENCRYPT_3DES = "3des";

	public static final String ALG_SIGN_SHA1 = "sha1";

	public static final String ALG_SIGN_MD5 = "md5";
	
	public static final String PARTNERSHIPID_REGEXP = "[\\w@_\\+-]+";
	/**
	 * The context path for sfrm inbound.
	 */
	public static final String CONTEXT_PATH		= "corvus/httpd/sfrm/inbound";

	public int getPartnershipSeq();

	public void setPartnershipSeq(int partnershipSeq);

	public String getDescription();

	public void setDescription(String description);

	public String getPartnershipId();

	public void setPartnershipId(String partnershipId);

	public boolean isHostnameVerified();

	public void setIsHostnameVerified(boolean isHostnameVerified);
	
	public String getPartnerEndpoint();
	
	public String getOrgPartnerEndpoint();
	
	public void setPartnerEndPoint(String endpoint);
	
	public String getPartnerCertFingerprint();
	
	public void setPartnerCertFingerprint(String partnerCertFingerprint);

	public String getSignAlgorithm();

	public void setSignAlgorithm(String signAlgorithm);

	public X509Certificate getVerifyX509Certificate() throws SFRMException;

	public String getEncryptAlgorithm();

	public void setEncryptAlgorithm(String encryptAlgorithm);

	public X509Certificate getEncryptX509Certificate() throws SFRMException;
	
	public String getEncryptX509CertificateBase64() throws FileNotFoundException, IOException;

	public int getRetryMax();

	public void setRetryMax(int retryMax);

	public int getRetryInterval();

	public void setRetryInterval(int retryInterval);

	public boolean isDisabled();

	public void setIsDisabled(boolean isDisabled);

	public Timestamp getCreationTimestamp();

	public void setCreationTimestamp(Timestamp creationTimestamp);

	public Timestamp getModifiedTimestamp();

	public void setModifiedTimestamp(Timestamp modifiedTimestamp);	
}
