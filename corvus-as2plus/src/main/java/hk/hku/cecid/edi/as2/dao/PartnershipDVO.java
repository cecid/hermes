/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.dao;

import java.security.cert.X509Certificate;

import hk.hku.cecid.piazza.commons.dao.DVO;

/**
 * @author Donahue Sze
 *  
 */
public interface PartnershipDVO extends DVO {

    public static final String ALG_ENCRYPT_RC2  = "rc2";

    public static final String ALG_ENCRYPT_3DES = "3des";

    public static final String ALG_MIC_SHA1     = "sha1";

    public static final String ALG_MIC_MD5      = "md5";

    public static final String ALG_SIGN_SHA1    = "sha1";

    public static final String ALG_SIGN_MD5     = "md5";

    public String getPartnershipId();

    public void setPartnershipId(String partnershipId);

    public boolean isDisabled();

    public void setIsDisabled(boolean isDisabled);

    public boolean isSyncReply();

    public void setIsSyncReply(boolean IsSyncReply);

    public String getSubject();

    public void setSubject(String subject);

    public String getRecipientAddress();

    public void setRecipientAddress(String recipientAddress);

    public boolean isHostnameVerified();
    
    public void setIsHostnameVerified(boolean isHostnameVerified);    
    
    public String getReceiptAddress();

    public void setReceiptAddress(String receiptAddress);

    public boolean isReceiptRequired();

    public void setIsReceiptRequired(boolean isReceiptRequired);

    public boolean isOutboundSignRequired();

    public void setIsOutboundSignRequired(boolean isOutboundSignRequired);

    public boolean isOutboundEncryptRequired();

    public void setIsOutboundEncryptRequired(boolean isOutboundEncryptRequired);

    public boolean isOutboundCompressRequired();

    public void setIsOutboundCompressRequired(boolean isOutboundEncryptRequired);

    public boolean isReceiptSignRequired();

    public void setIsReceiptSignRequired(boolean isReceiptSignRequired);

    public boolean isInboundSignRequired();

    public void setIsInboundSignRequired(boolean isInboundSignRequired);

    public boolean isInboundEncryptRequired();

    public void setIsInboundEncryptRequired(boolean isInboundEncryptRequired);

    public int getRetries();

    public void setRetries(int retries);

    public int getRetryInterval();

    public void setRetryInterval(int retryInterval);

    public String getSignAlgorithm();

    public void setSignAlgorithm(String signAlgorithm);

    public String getEncryptAlgorithm();

    public void setEncryptAlgorithm(String encryptAlgorithm);

    public String getMicAlgorithm();

    public void setMicAlgorithm(String micAlgorithm);

//    public String getPrincipalId();

//    public void setPrincipalId(String principalId);

    public String getAS2From();

    public void setAs2From(String as2From);

    public String getAs2To();

    public void setAs2To(String as2To);

    public byte[] getEncryptCert();

    public X509Certificate getEncryptX509Certificate();

    public void setEncryptCert(byte[] encryptCert);

    public byte[] getVerifyCert();

    public void setVerifyCert(byte[] verifyCert);

    public X509Certificate getVerifyX509Certificate();
    
    public X509Certificate getEffectiveVerifyCertificate();
}