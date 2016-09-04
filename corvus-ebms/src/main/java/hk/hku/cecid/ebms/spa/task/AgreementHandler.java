/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.task;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDAO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
import hk.hku.cecid.ebms.spa.handler.EbxmlMessageDAOConvertor;
import hk.hku.cecid.ebms.spa.handler.MessageClassifier;
import hk.hku.cecid.piazza.commons.dao.DAOException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Donahue Sze
 * 
 */
public class AgreementHandler {

    private URL toPartyURL = null;

    private String toPartyProtocol = null;

    private String syncReply;

    private String ackRequested;

    private String ackSignRequested;

    private String dupElimination;

    private String actor = null;

    private String disabled;

    // private String persistDuration = null;

    private String messageOrder;

    private int retries = 3;

    private int retryInterval = 5000;

    private boolean isSign;

    private String dsAlgorithm;

    private String mdAlgorithm;

    private boolean isEncrypt;

    private String encryptAlgorithm;

    private byte[] signCert;

    private byte[] encryptCert;

    private boolean isHostnameVerified;

    public AgreementHandler(MessageDVO message, boolean isCheckAgreement)
            throws MessageValidationException, DAOException {

        // Check if there is any predefined Sender Channel
        findPartnership(message);

        // agreement guard, check the message does not violate the agreement
        // if no, throw messagevalidation exception
        if (isCheckAgreement) {
            if (message.getMessageType().equalsIgnoreCase(
                    MessageClassifier.MESSAGE_TYPE_ORDER)) {
                checkAgreementViolation(message);
            }
        }
    }

    public AgreementHandler(EbxmlMessage ebxmlMessage, String messageBox,
            String messageType, boolean isCheckAgreement)
            throws MessageValidationException, DAOException {
        EbxmlMessageDAOConvertor message = new EbxmlMessageDAOConvertor(
                ebxmlMessage, MessageClassifier.MESSAGE_BOX_INBOX, messageType);
    	new AgreementHandler(message.getMessageDVO(), isCheckAgreement);
    }

    /**
     * @param message
     * @throws DAOException
     * @throws MessageValidationException
     */
    private void findPartnership(MessageDVO message) throws DAOException,
            MessageValidationException {
        PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao
                .createDAO(PartnershipDAO.class);
        PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO
                .createDVO();
        partnershipDVO.setCpaId(message.getCpaId());
        if (message.getMessageType().equalsIgnoreCase(
                MessageClassifier.MESSAGE_TYPE_ORDER)) {
            partnershipDVO.setService(message.getService());
            partnershipDVO.setAction(message.getAction());
        } else {
            // it is not an order msg.
            MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                    .createDAO(MessageDAO.class);
            MessageDVO orgMessageDVO = (MessageDVO) messageDAO.createDVO();
            orgMessageDVO.setMessageId(message.getRefToMessageId());
            orgMessageDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_INBOX);
            if (messageDAO.findMessage(orgMessageDVO)) {
                if (orgMessageDVO.getAction()
                        .equalsIgnoreCase("Acknowledgment")) {
                    // it is the error message related to acknowledgement
                    orgMessageDVO.setMessageId(orgMessageDVO
                            .getRefToMessageId());
                    orgMessageDVO
                            .setMessageBox(MessageClassifier.MESSAGE_BOX_OUTBOX);
                    if (!messageDAO.findMessage(orgMessageDVO)) {
                        throw new MessageValidationException(
                                "Cannot find the ref to message: "
                                        + orgMessageDVO.getRefToMessageId());
                    }
                }
                partnershipDVO.setService(orgMessageDVO.getService());
                partnershipDVO.setAction(orgMessageDVO.getAction());
            } else {
                throw new MessageValidationException(
                        "Cannot find the ref to message: "
                                + message.getRefToMessageId());
            }
        }

        if (partnershipDAO.findPartnershipByCPA(partnershipDVO)) {
            // there is a predefined sender channel
            signCert = partnershipDVO.getSignCert();
            encryptCert = partnershipDVO.getEncryptCert();

            // there is a predefined delivery channel
            String protocol = partnershipDVO.getTransportProtocol();
            String endpoint = partnershipDVO.getTransportEndpoint();
            try {
                if (protocol == null && endpoint == null) {
                    // use perMessage to party id
                    toPartyURL = new URL(message.getToPartyId());
                    toPartyProtocol = toPartyURL.getProtocol();
                } else {
                    toPartyURL = new URL(endpoint);
                    if (protocol.equalsIgnoreCase("smtp")) {
                        toPartyProtocol = "mailto";
                    } else {
                        toPartyProtocol = protocol;
                    }
                }
            } catch (MalformedURLException e2) {
                EbmsProcessor.core.log.error("Url is wrong", e2);
                throw new MessageValidationException("Url is wrong", e2);
            }

            syncReply = partnershipDVO.getSyncReplyMode();
            ackRequested = partnershipDVO.getAckRequested();
            ackSignRequested = partnershipDVO.getAckSignRequested();
            dupElimination = partnershipDVO.getDupElimination();
            actor = partnershipDVO.getActor();
            disabled = partnershipDVO.getDisabled();
            retries = partnershipDVO.getRetries();
            retryInterval = partnershipDVO.getRetryInterval();
            // persistDuration = partnershipDVO.getPersistDuration();
            messageOrder = partnershipDVO.getMessageOrder();
            isSign = new Boolean(partnershipDVO.getSignRequested())
                    .booleanValue();
            dsAlgorithm = partnershipDVO.getDsAlgorithm();
            mdAlgorithm = partnershipDVO.getMdAlgorithm();
            isEncrypt = new Boolean(partnershipDVO.getEncryptRequested())
                    .booleanValue();
            encryptAlgorithm = partnershipDVO.getEncryptAlgorithm();
            isHostnameVerified = new Boolean(partnershipDVO
                    .getIsHostnameVerified()).booleanValue();
        } else {
            EbmsProcessor.core.log.error("Partnership not found: (CPA ID: "
                    + partnershipDVO.getCpaId() + ", Service: "
                    + partnershipDVO.getService() + " , Action: "
                    + partnershipDVO.getAction() + ")");
            throw new MessageValidationException(
                    "Partnership channel not found");
        }
    }

    /**
     * @param message
     * @throws MessageValidationException
     */
    private void checkAgreementViolation(MessageDVO message)
            throws MessageValidationException {

        /*
         * cpa standards
         * 
         * syncReplyMode - "mshSignalsOnly", "none"
         * 
         * ackRequested - "always", "never", "perMessage"
         * 
         * ackSignatureRequested - "always", "never", "perMessage"
         * 
         * duplicateElimination - "always", "never", "perMessage"
         * 
         * disabled - "true", "false"
         * 
         * MessageOrder - "Guaranteed", "NotGuaranteed"
         */

        // check supported protocol
        if (!toPartyProtocol.equalsIgnoreCase("mailto")
                && !toPartyProtocol.equalsIgnoreCase("http")
                && !toPartyProtocol.equalsIgnoreCase("https")) {
            EbmsProcessor.core.log.error("Protocol: " + toPartyProtocol
                    + " does not support");
            throw new MessageValidationException("Protocol: " + toPartyProtocol
                    + " does not support");
        }

        // check sync reply
        String SYNC_REPLY_MODE_MSH_SIGNALS_ONLY = "mshSignalsOnly";
        String SYNC_REPLY_MODE_NONE = "none";

        if (syncReply != null) {
            if (syncReply.equalsIgnoreCase(SYNC_REPLY_MODE_MSH_SIGNALS_ONLY)) {
                if (!message.getSyncReply().equalsIgnoreCase("true")) {
                    EbmsProcessor.core.log
                            .error("Agreement Violation - the sync reply mode should be "
                                    + SYNC_REPLY_MODE_MSH_SIGNALS_ONLY);
                    throw new MessageValidationException(
                            "Agreement Violation - the sync reply mode should be "
                                    + SYNC_REPLY_MODE_MSH_SIGNALS_ONLY);
                }
            } else if (syncReply.equalsIgnoreCase(SYNC_REPLY_MODE_NONE)) {
                if (!message.getSyncReply().equalsIgnoreCase("false")) {
                    EbmsProcessor.core.log
                            .error("Agreement Violation - the sync reply mode should be "
                                    + SYNC_REPLY_MODE_NONE);
                    throw new MessageValidationException(
                            "Agreement Violation - the sync reply mode should be "
                                    + SYNC_REPLY_MODE_NONE);
                }
            }
        }

        // check ack requested
        String ACK_REQUESTED_ALWAYS = "always";
        String ACK_REQUESTED_NEVER = "never";
        // String ACK_REQUESTED_PERMESSAGE = "perMessage";

        if (ackRequested != null) {
            if (ackRequested.equalsIgnoreCase(ACK_REQUESTED_ALWAYS)) {
                if (!message.getAckRequested().equalsIgnoreCase("true")) {
                    EbmsProcessor.core.log
                            .error("Agreement Violation - the ack requested is "
                                    + ACK_REQUESTED_ALWAYS + " needed");
                    throw new MessageValidationException(
                            "Agreement Violation - the ack requested is "
                                    + ACK_REQUESTED_ALWAYS + " needed");
                }
            } else if (ackRequested.equalsIgnoreCase(ACK_REQUESTED_NEVER)) {
                if (!message.getAckRequested().equalsIgnoreCase("false")) {
                    EbmsProcessor.core.log
                            .error("Agreement Violation - the ack requested is "
                                    + ACK_REQUESTED_NEVER + " needed");
                    throw new MessageValidationException(
                            "Agreement Violation - the ack requested is "
                                    + ACK_REQUESTED_NEVER + " needed");
                }
            }
        }

        // check ack sign requested
        String ACK_SIGN_REQUESTED_ALWAYS = "always";
        String ACK_SIGN_REQUESTED_NEVER = "never";
        // String ACK_SIGN_REQUESTED_PERMESSAGE = "perMessage";

        if (ackSignRequested != null) {
            if (ackSignRequested.equalsIgnoreCase(ACK_SIGN_REQUESTED_ALWAYS)) {
                if (!message.getAckSignRequested().equalsIgnoreCase("true")) {
                    EbmsProcessor.core.log
                            .error("Agreement Violation - the ack sign requested is "
                                    + ACK_SIGN_REQUESTED_ALWAYS + " needed");
                    throw new MessageValidationException(
                            "Agreement Violation - the ack sign requested is "
                                    + ACK_SIGN_REQUESTED_ALWAYS + " needed");
                }
            } else if (ackSignRequested
                    .equalsIgnoreCase(ACK_SIGN_REQUESTED_NEVER)) {
                if (!message.getAckSignRequested().equalsIgnoreCase("false")) {
                    EbmsProcessor.core.log
                            .error("Agreement Violation - the ack sign requested is "
                                    + ACK_SIGN_REQUESTED_NEVER + " needed");
                    throw new MessageValidationException(
                            "Agreement Violation - the ack sign requested is "
                                    + ACK_SIGN_REQUESTED_NEVER + " needed");
                }
            }
        }

        // check duplicate elimination
        String DUP_ELIMINATION_ALWAYS = "always";
        String DUP_ELIMINATION_NEVER = "never";
        // String DUP_ELIMINATION_PERMESSAGE = "perMessage";

        if (dupElimination != null) {
            if (dupElimination.equalsIgnoreCase(DUP_ELIMINATION_ALWAYS)) {
                if (!message.getDupElimination().equalsIgnoreCase("true")) {
                    EbmsProcessor.core.log
                            .error("Agreement Violation - the dup elimination is "
                                    + DUP_ELIMINATION_ALWAYS + " needed");
                    throw new MessageValidationException(
                            "Agreement Violation - the dup elimination is "
                                    + DUP_ELIMINATION_ALWAYS + " needed");
                }
            } else if (dupElimination.equalsIgnoreCase(DUP_ELIMINATION_NEVER)) {
                if (!message.getDupElimination().equalsIgnoreCase("false")) {
                    EbmsProcessor.core.log
                            .error("Agreement Violation - dup elimination is "
                                    + DUP_ELIMINATION_NEVER + " needed");
                    throw new MessageValidationException(
                            "Agreement Violation - the dup elimination is "
                                    + DUP_ELIMINATION_NEVER + " needed");
                }
            }
        }

        // check channel disabled
        if (disabled != null) {
            if (disabled.equalsIgnoreCase("true")) {
                EbmsProcessor.core.log
                        .error("the channel is disabled currently");
                throw new MessageValidationException(
                        "the channel is disabled currently");
            }
        }

        // check message order
        String MESSAGE_ORDER_GUARANTEED = "Guaranteed";
        String MESSAGE_ORDER_NOTGUARANTEED = "NotGuaranteed";

        if (messageOrder != null) {
            if (messageOrder.equalsIgnoreCase(MESSAGE_ORDER_GUARANTEED)) {
                if (!(message.getSequenceNo() >= 0)) {
                    EbmsProcessor.core.log
                            .error("Agreement Violation - the message order should be "
                                    + MESSAGE_ORDER_GUARANTEED);
                    throw new MessageValidationException(
                            "Agreement Violation - the message order should be "
                                    + MESSAGE_ORDER_GUARANTEED);
                }
            } else if (messageOrder
                    .equalsIgnoreCase(MESSAGE_ORDER_NOTGUARANTEED)) {
                if (!(message.getSequenceNo() == -1)) {
                    EbmsProcessor.core.log
                            .error("Agreement Violation - the message order should be "
                                    + MESSAGE_ORDER_NOTGUARANTEED);
                    throw new MessageValidationException(
                            "Agreement Violation - the message order should be "
                                    + MESSAGE_ORDER_NOTGUARANTEED);
                }
            }
        }
    }

    /**
     * @return Returns the actor.
     */
    public String getActor() {
        return actor;
    }

    /**
     * @return Returns the retries.
     */
    public int getRetries() {
        return retries;
    }

    /**
     * @return Returns the retryInterval.
     */
    public int getRetryInterval() {
        return retryInterval;
    }

    /**
     * @return Returns the toPartyURL.
     */
    public URL getToPartyURL() {
        return toPartyURL;
    }

    /**
     * @return Returns the toPartyProtocol.
     */
    public String getToPartyProtocol() {
        return toPartyProtocol;
    }

    /**
     * @return Returns the dsAlgorithm.
     */
    public String getDsAlgorithm() {
        return dsAlgorithm;
    }

    /**
     * @return Returns the isSign.
     */
    public boolean isSign() {
        return isSign;
    }

    /**
     * @return Returns the mdAlgorithm.
     */
    public String getMdAlgorithm() {
        return mdAlgorithm;
    }

    /**
     * @return Returns the isEncrypt.
     */
    public boolean isEncrypt() {
        return isEncrypt;
    }

    /**
     * @return Returns the encryptAlgorithm.
     */
    public String getEncryptAlgorithm() {
        return encryptAlgorithm;
    }

    /**
     * @return Returns the encryptCert.
     */
    public byte[] getEncryptCert() {
        return encryptCert;
    }

    /**
     * @return Returns the signCert.
     */
    public byte[] getSignCert() {
        return signCert;
    }

    /**
     * @return Returns the isHostnameVerified.
     */
    public boolean isHostnameVerified() {
        return isHostnameVerified;
    }
}