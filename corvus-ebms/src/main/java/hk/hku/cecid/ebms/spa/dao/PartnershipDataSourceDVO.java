/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.dao;

import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDVO;

/**
 * @author Donahue Sze
 * 
 */
public class PartnershipDataSourceDVO extends DataSourceDVO implements
        PartnershipDVO {

    public PartnershipDataSourceDVO() {
        super();
    }

    /**
     * @param partnershipId The partnershipId to set.
     */
    public void setPartnershipId(String partnershipId) {
        super.setString("partnershipId", partnershipId);
    }

    /**
     * @return Returns the partnershipId.
     */
    public String getPartnershipId() {
        return super.getString("partnershipId");
    }

    /**
     * @return Returns the cpaId.
     */
    public String getCpaId() {
        return super.getString("cpaId");
    }

    /**
     * @param cpaId
     *            The cpaId to set.
     */
    public void setCpaId(String cpaId) {
        super.setString("cpaId", cpaId);
    }

    /**
     * @return Returns the service.
     */
    public String getService() {
        return super.getString("service");
    }

    /**
     * @param service
     *            The service to set.
     */
    public void setService(String service) {
        super.setString("service", service);
    }

    /**
     * @return Returns the action.
     */
    public String getAction() {
        return super.getString("action");
    }

    /**
     * @param action
     *            The action to set.
     */
    public void setAction(String action) {
        super.setString("action", action);
    }

    /**
     * @return Returns the disabled.
     */
    public String getDisabled() {
        return super.getString("disabled");
    }

    /**
     * @param disabled
     *            The disabled to set.
     */
    public void setDisabled(String disabled) {
        super.setString("disabled", disabled);
    }

    /**
     * @return Returns the syncReplyMode.
     */
    public String getSyncReplyMode() {
        return super.getString("syncReplyMode");
    }

    /**
     * @param syncReplyMode
     *            The syncReplyMode to set.
     */
    public void setSyncReplyMode(String syncReplyMode) {
        super.setString("syncReplyMode", syncReplyMode);
    }

    /**
     * @return Returns the transportEndpoint.
     */
    public String getTransportEndpoint() {
        return super.getString("transportEndpoint");
    }

    /**
     * @param transportEndpoint
     *            The transportEndpoint to set.
     */
    public void setTransportEndpoint(String transportEndpoint) {
        super.setString("transportEndpoint", transportEndpoint);
    }

    /**
     * @return Returns the transportProtocol.
     */
    public String getTransportProtocol() {
        return super.getString("transportProtocol");
    }

    /**
     * @param transportProtocol
     *            The transportProtocol to set.
     */
    public void setTransportProtocol(String transportProtocol) {
        super.setString("transportProtocol", transportProtocol);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#getAckRequested()
     */
    public String getAckRequested() {
        return super.getString("ackRequested");
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#setAckRequested(java.lang.String)
     */
    public void setAckRequested(String ackRequested) {
        super.setString("ackRequested", ackRequested);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#getAckSignRequested()
     */
    public String getAckSignRequested() {
        return super.getString("ackSignRequested");
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#setAckSignRequested(java.lang.String)
     */
    public void setAckSignRequested(String ackSignRequested) {
        super.setString("ackSignRequested", ackSignRequested);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#getDupElimination()
     */
    public String getDupElimination() {
        return super.getString("dupElimination");
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#setDupElimination(java.lang.String)
     */
    public void setDupElimination(String dupElimination) {
        super.setString("dupElimination", dupElimination);

    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#getActor()
     */
    public String getActor() {
        return super.getString("actor");
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#setActor(java.lang.String)
     */
    public void setActor(String actor) {
        super.setString("actor", actor);
    }

    /**
     * @return Returns the messageOrder.
     */
    public String getMessageOrder() {
        return super.getString("messageOrder");
    }

    /**
     * @param messageOrder
     *            The messageOrder to set.
     */
    public void setMessageOrder(String messageOrder) {
        super.setString("messageOrder", messageOrder);
    }

    /**
     * @return Returns the persistDuration.
     */
    public String getPersistDuration() {
        return super.getString("persistDuration");
    }

    /**
     * @param persistDuration
     *            The persistDuration to set.
     */
    public void setPersistDuration(String persistDuration) {
        super.setString("persistDuration", persistDuration);
    }

    /**
     * @return Returns the retries.
     */
    public int getRetries() {
        return super.getInt("retries");
    }

    /**
     * @param retries
     *            The retries to set.
     */
    public void setRetries(int retries) {
        super.setInt("retries", retries);
    }

    /**
     * @return Returns the retryInterval.
     */
    public int getRetryInterval() {
        return super.getInt("retryInterval");
    }

    /**
     * @param retryInterval
     *            The retryInterval to set.
     */
    public void setRetryInterval(int retryInterval) {
        super.setInt("retryInterval", retryInterval);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.AgreementDVO#getSignRequested()
     */
    public String getSignRequested() {
        return super.getString("signRequested");
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.AgreementDVO#setSignRequested(java.lang.String)
     */
    public void setSignRequested(String signRequested) {
        super.setString("signRequested", signRequested);

    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.AgreementDVO#getDsAlgorithm()
     */
    public String getDsAlgorithm() {
        return super.getString("dsAlgorithm");
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.AgreementDVO#setDsAlgorithm(java.lang.String)
     */
    public void setDsAlgorithm(String dsAlgorithm) {
        super.setString("dsAlgorithm", dsAlgorithm);

    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.AgreementDVO#getMdAlgorithm()
     */
    public String getMdAlgorithm() {
        return super.getString("mdAlgorithm");
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.AgreementDVO#setMdAlgorithm(java.lang.String)
     */
    public void setMdAlgorithm(String mdAlgorithm) {
        super.setString("mdAlgorithm", mdAlgorithm);

    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#getEncryptRequested()
     */
    public String getEncryptRequested() {
        return super.getString("encryptRequested");
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#setEncryptRequested(java.lang.String)
     */
    public void setEncryptRequested(String encryptRequested) {
        super.setString("encryptRequested", encryptRequested);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#getEncryptAlgorithm()
     */
    public String getEncryptAlgorithm() {
        return super.getString("encryptAlgorithm");
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#setEncryptAlgorithm(java.lang.String)
     */
    public void setEncryptAlgorithm(String encryptAlgorithm) {
        super.setString("encryptAlgorithm", encryptAlgorithm);
    }

    /**
     * @return Returns the principalId.
     * @deprecated principle Id is no longer used.
     */
    public String getPrincipalId() {
        return super.getString("principalId");
    }

    /**
     * @param principalId The principalId to set.
     * @deprecated principle Id is no longer used.
     */
    public void setPrincipalId(String principalId) {
        super.setString("principalId", principalId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#getServerCert()
     */
    public byte[] getSignCert() {
        return (byte[]) super.get("signCert");
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#setServerCert(byte[])
     */
    public void setSignCert(byte[] signCert) {
        super.put("signCert", signCert);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#getServerCert()
     */
    public byte[] getEncryptCert() {
        return (byte[]) super.get("encryptCert");
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#setServerCert(byte[])
     */
    public void setEncryptCert(byte[] encryptCert) {
        super.put("encryptCert", encryptCert);
    }

    /* (non-Javadoc)
     * @see hk.hku.cecid.ebms.spa.dao.PartnershipDVO#setIsHostnameVerified(java.lang.String)
     */
    public void setIsHostnameVerified(String isHostnameVerified) {
        super.setString("isHostnameVerified", isHostnameVerified);
    }

    /* (non-Javadoc)
     * @see hk.hku.cecid.ebms.spa.dao.PartnershipDVO#getIsHostnameVerified()
     */
    public String getIsHostnameVerified() {
        return super.getString("isHostnameVerified");
    }
}