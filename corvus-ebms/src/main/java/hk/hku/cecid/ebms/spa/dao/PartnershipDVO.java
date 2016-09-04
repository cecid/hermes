/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.dao;

import hk.hku.cecid.piazza.commons.dao.DVO;

/**
 * @author Donahue Sze
 * 
 */
public interface PartnershipDVO extends DVO {
    /**
     * @param partnershipId The partnershipId to set.
     */
    public void setPartnershipId(String partnershipId);

    /**
     * @return Returns the channelId.
     */
    public String getPartnershipId();

    /**
     * @return Returns the cpaId.
     */
    public String getCpaId();

    /**
     * @param cpaId The cpaId to set.
     */
    public void setCpaId(String cpaId);

    /**
     * @return Returns the service.
     */
    public String getService();

    /**
     * @param service The service to set.
     */
    public void setService(String service);

    /**
     * @return Returns the action.
     */
    public String getAction();

    /**
     * @param action The action to set.
     */
    public void setAction(String action);

    /**
     * @return Returns the disabled.
     */
    public String getDisabled();

    /**
     * @param disabled The disabled to set.
     */
    public void setDisabled(String disabled);

    /**
     * @return Returns the syncReplyMode.
     */
    public String getSyncReplyMode();

    /**
     * @param syncReplyMode The syncReplyMode to set.
     */
    public void setSyncReplyMode(String syncReplyMode);

    /**
     * @return Returns the transportEndpoint.
     */
    public String getTransportEndpoint();

    /**
     * @param transportEndpoint The transportEndpoint to set.
     */
    public void setTransportEndpoint(String transportEndpoint);

    /**
     * @return Returns the transportProtocol.
     */
    public String getTransportProtocol();

    /**
     * @param transportProtocol The transportProtocol to set.
     */
    public void setTransportProtocol(String transportProtocol);

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#getAckRequested()
     */public String getAckRequested();

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#setAckRequested(java.lang.String)
     */public void setAckRequested(String ackRequested);

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#getAckSignRequested()
     */public String getAckSignRequested();

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#setAckSignRequested(java.lang.String)
     */public void setAckSignRequested(String ackSignRequested);

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#getDupElimination()
     */public String getDupElimination();

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#setDupElimination(java.lang.String)
     */public void setDupElimination(String dupElimination);

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#getActor()
     */public String getActor();

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#setActor(java.lang.String)
     */public void setActor(String actor);

    /**
     * @return Returns the messageOrder.
     */
    public String getMessageOrder();

    /**
     * @param messageOrder
     *            The messageOrder to set.
     */
    public void setMessageOrder(String messageOrder);

    /**
     * @return Returns the persistDuration.
     */
    public String getPersistDuration();

    /**
     * @param persistDuration
     *            The persistDuration to set.
     */
    public void setPersistDuration(String persistDuration);

    /**
     * @return Returns the retries.
     */
    public int getRetries();

    /**
     * @param retries
     *            The retries to set.
     */
    public void setRetries(int retries);

    /**
     * @return Returns the retryInterval.
     */
    public int getRetryInterval();

    /**
     * @param retryInterval
     *            The retryInterval to set.
     */
    public void setRetryInterval(int retryInterval);

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.AgreementDVO#getSignRequested()
     */public String getSignRequested();

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.AgreementDVO#setSignRequested(java.lang.String)
     */public void setSignRequested(String signRequested);

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.AgreementDVO#getDsAlgorithm()
     */public String getDsAlgorithm();

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.AgreementDVO#setDsAlgorithm(java.lang.String)
     */public void setDsAlgorithm(String dsAlgorithm);

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.AgreementDVO#getMdAlgorithm()
     */public String getMdAlgorithm();

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.AgreementDVO#setMdAlgorithm(java.lang.String)
     */public void setMdAlgorithm(String mdAlgorithm);

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#getEncryptRequested()
     */public String getEncryptRequested();

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#setEncryptRequested(java.lang.String)
     */public void setEncryptRequested(String encryptRequested);

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#getEncryptAlgorithm()
     */public String getEncryptAlgorithm();

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#setEncryptAlgorithm(java.lang.String)
     */public void setEncryptAlgorithm(String encryptAlgorithm);

    /**
     * @return Returns the principalId.
     * @deprecated principle Id is no longer used.
     */
    public String getPrincipalId();

    /**
     * @param principalId The principalId to set.
     * @deprecated principle Id is no longer used.
     */
    public void setPrincipalId(String principalId);

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#getServerCert()
     */public byte[] getSignCert();

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#setServerCert(byte[])
     */public void setSignCert(byte[] signCert);

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#getServerCert()
     */public byte[] getEncryptCert();

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.dao.DeliveryChannelDVO#setServerCert(byte[])
     */public void setEncryptCert(byte[] encryptCert);
     
     public void setIsHostnameVerified(String isHostnameVerified);
     
     public String getIsHostnameVerified();
}