/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.dao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDVO;

/**
 * @author Donahue Sze
 *  
 */
public class PartnershipDataSourceDVO extends DataSourceDVO implements
        PartnershipDVO {

    public String getPartnershipId() {
        return super.getString("partnershipId");
    }

    public void setPartnershipId(String partnershipId) {
        super.setString("partnershipId", partnershipId);
    }

    public boolean isDisabled() {
        return super.getBoolean("isDisabled");
    }

    public void setIsDisabled(boolean isDisabled) {
        super.setString("isDisabled", String.valueOf(isDisabled));
    }

    public boolean isSyncReply() {
        return super.getBoolean("isSyncReply");
    }

    public void setIsSyncReply(boolean isSyncReply) {
        super.setString("isSyncReply", String.valueOf(isSyncReply));
    }

    public String getSubject() {
        return super.getString("subject");
    }

    public void setSubject(String subject) {
        super.setString("subject", subject);
    }

    public String getRecipientAddress() {
        return super.getString("recipientAddress");
    }

    public void setRecipientAddress(String recipientAddress) {
        super.setString("recipientAddress", recipientAddress);
    }

    public boolean isHostnameVerified() {
        return super.getBoolean("isHostnameVerified");
    }

    public void setIsHostnameVerified(boolean isHostnameVerified) {
        super.setString("isHostnameVerified", String.valueOf(isHostnameVerified));
    }

    public String getReceiptAddress() {
        return super.getString("receiptAddress");
    }

    public void setReceiptAddress(String receiptAddress) {
        super.setString("receiptAddress", receiptAddress);
    }

    public boolean isReceiptRequired() {
        return super.getBoolean("isReceiptRequired");
    }

    public void setIsReceiptRequired(boolean isReceiptRequired) {
        super.setString("isReceiptRequired", String.valueOf(isReceiptRequired));
    }

    public boolean isOutboundSignRequired() {
        return super.getBoolean("isOutboundSignRequired");
    }

    public void setIsOutboundSignRequired(boolean isOutboundSignRequired) {
        super.setString("isOutboundSignRequired", String
                .valueOf(isOutboundSignRequired));
    }

    public boolean isOutboundEncryptRequired() {
        return super.getBoolean("isOutboundEncryptRequired");
    }

    public void setIsOutboundEncryptRequired(boolean isOutboundEncryptRequired) {
        super.setString("isOutboundEncryptRequired", String
                .valueOf(isOutboundEncryptRequired));
    }

    public boolean isOutboundCompressRequired() {
        return super.getBoolean("isOutboundCompressRequired");
    }

    public void setIsOutboundCompressRequired(boolean isOutboundCompressRequired) {
        super.setString("isOutboundCompressRequired", String
                .valueOf(isOutboundCompressRequired));
    }

    public boolean isReceiptSignRequired() {
        return super.getBoolean("isReceiptSignRequired");
    }

    public void setIsReceiptSignRequired(boolean isReceiptSignRequired) {
        super.setString("isReceiptSignRequired", String
                .valueOf(isReceiptSignRequired));
    }

    public boolean isInboundSignRequired() {
        return super.getBoolean("isInboundSignRequired");
    }

    public void setIsInboundSignRequired(boolean isInboundSignRequired) {
        super.setString("isInboundSignRequired", String
                .valueOf(isInboundSignRequired));
    }

    public boolean isInboundEncryptRequired() {
        return super.getBoolean("isInboundEncryptRequired");
    }

    public void setIsInboundEncryptRequired(boolean isInboundEncryptRequired) {
        super.setString("isInboundEncryptRequired", String
                .valueOf(isInboundEncryptRequired));
    }

    public int getRetries() {
        return super.getInt("retries");
    }

    public void setRetries(int retries) {
        super.setInt("retries", retries);
    }

    public int getRetryInterval() {
        return super.getInt("retryInterval");
    }

    public void setRetryInterval(int retryInterval) {
        super.setInt("retryInterval", retryInterval);
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

    public String getMicAlgorithm() {
        return super.getString("micAlgorithm");
    }

    public void setMicAlgorithm(String micAlgorithm) {
        super.setString("micAlgorithm", micAlgorithm);
    }
/*
    public String getPrincipalId() {
        return super.getString("principalId");
    }

    public void setPrincipalId(String principalId) {
        super.setString("principalId", principalId);
    }
*/
    public String getAS2From() {
        return super.getString("as2From");
    }

    public void setAs2From(String as2From) {
        super.setString("as2From", as2From);
    }

    public String getAs2To() {
        return super.getString("as2To");
    }

    public void setAs2To(String as2To) {
        super.setString("as2To", as2To);
    }

    public byte[] getEncryptCert() {
        return (byte[]) super.get("encryptCert");
    }

    public void setEncryptCert(byte[] encryptCert) {
        super.put("encryptCert", encryptCert);
    }

    public byte[] getVerifyCert() {
        return (byte[]) super.get("verifyCert");
    }

    public void setVerifyCert(byte[] verifyCert) {
        super.put("verifyCert", verifyCert);
    }

    public X509Certificate getEncryptX509Certificate() {
        return getX509Certificate(getEncryptCert());
    }

    public X509Certificate getVerifyX509Certificate() {
        return getX509Certificate(getVerifyCert());
    }

    private X509Certificate getX509Certificate(byte[] bs) {
        try {
            InputStream certStream = new ByteArrayInputStream(bs);
            X509Certificate cert = (X509Certificate) CertificateFactory
                    .getInstance("X.509").generateCertificate(certStream);
            return cert;
        }
        catch (Exception e) {
            return null;
        }
    }

    public X509Certificate getEffectiveVerifyCertificate() {
        X509Certificate cert = getVerifyX509Certificate();
        if (cert == null) {
            cert = getEncryptX509Certificate();
        }
        return cert;
    }
}