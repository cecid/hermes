/*
 * Created on Nov 3, 2004
 *
 */
package hk.hku.cecid.ebms.admin.listener;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.PartnershipDAO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.module.ComponentException;
import hk.hku.cecid.piazza.commons.security.SMimeMessage;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.commons.util.StringUtilities;
import hk.hku.cecid.piazza.commons.util.UtilitiesException;
import hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.dom4j.DocumentException;

/**
 * @author Donahue Sze
 * 
 */
public class AgreementUploadPageletAdaptor extends AdminPageletAdaptor {

    String selectedPartyName = null;

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.pagelet.xslt.BorderLayoutPageletAdaptor#getCenterSource(javax.servlet.http.HttpServletRequest)
     */
    protected Source getCenterSource(HttpServletRequest request) {

        PropertyTree dom = new PropertyTree();
        dom.setProperty("/partnership", "");

        boolean isMultipart = FileUpload.isMultipartContent(request);

        if (isMultipart) {
            DiskFileUpload upload = new DiskFileUpload();
            try {
                FileItem realFileItem = null;
                boolean hasFileField = false;
                List fileItems = upload.parseRequest(request);

                Iterator iter = fileItems.iterator();
                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();

                    if (item.isFormField()) {
                        if (item.getFieldName().equals("party_name")) {
                            selectedPartyName = item.getString();
                        }
                    } else {
                        hasFileField = true;

                        if (item.getName().equals("")) {
                            request.setAttribute(ATTR_MESSAGE,
                                    "No file specified");
                        } else if (item.getSize() == 0) {
                            request.setAttribute(ATTR_MESSAGE,
                                    "The file is no content");
                        } else if (!item.getContentType().equalsIgnoreCase(
                                "text/xml")) {
                            request.setAttribute(ATTR_MESSAGE,
                                    "It is not a xml file");
                        } else {
                            realFileItem = item;
                        }
                    }
                }

                if (!hasFileField) {
                    request.setAttribute(ATTR_MESSAGE,
                            "There is no file field in the request paramters");
                }

                if (selectedPartyName.equalsIgnoreCase("")) {
                    request
                            .setAttribute(ATTR_MESSAGE,
                                    "There is no party name field in the request paramters");
                } else {
                    X_ATTR_PARTY_NAME = "[@" + X_TP_NAMESPACE + "partyName='"
                            + selectedPartyName + "']";
                }

                if (realFileItem != null
                        && !selectedPartyName.equalsIgnoreCase("")) {
                    String errorMessage = processUploadedXml(dom, realFileItem);
                    if (errorMessage != null) {
                        request.setAttribute(ATTR_MESSAGE, errorMessage);
                    }

                }
            } catch (Exception e) {
                EbmsProcessor.core.log.error(
                        "Exception throw when upload the file", e);
                request.setAttribute(ATTR_MESSAGE,
                        "Exception throw when upload the file");
            }
        }

        return dom.getSource();
    }

    /**
     * @param item
     * @throws IOException
     * @throws DocumentException
     * @throws UtilitiesException
     * @throws ComponentException
     * @throws DAOException
     */
    private String processUploadedXml(PropertyTree dom, FileItem item)
            throws IOException, DocumentException, UtilitiesException,
            ComponentException {
        InputStream uploadedStream = item.getInputStream();
        PropertyTree cpa = new PropertyTree(uploadedStream);

        try {
            String[] partyNames = cpa
                    .getProperties(X_COLLABORATION_PROTOCOL_AGREEMENT
                            + X_PARTY_INFO + "/@" + X_TP_NAMESPACE
                            + "partyName");

            if (!selectedPartyName.equals(partyNames[0])
                    && !selectedPartyName.equals(partyNames[1])) {
                throw new RuntimeException(
                        "There is no party name match in the cpa");
            }

            addPartnerships(cpa, dom);

        } catch (Exception e) {
            EbmsProcessor.core.log
                    .error("Error in processing upploaded xml", e);
            return new String(e.getMessage());
        }
        return null;
    }

    // XPath constants
    String X_TP_NAMESPACE = "tp:";

    String X_COLLABORATION_PROTOCOL_AGREEMENT = "/" + X_TP_NAMESPACE
            + "CollaborationProtocolAgreement";

    String X_PARTY_INFO = "/" + X_TP_NAMESPACE + "PartyInfo";

    String X_ATTR_PARTY_NAME = null;

    String X_COLLABORATION_ROLE = "/" + X_TP_NAMESPACE + "CollaborationRole";

    String X_SERVICE_BINDING = "/" + X_TP_NAMESPACE + "ServiceBinding";

    String X_SERVICE = "/" + X_TP_NAMESPACE + "Service";

    String X_CAN_SEND = "/" + X_TP_NAMESPACE + "CanSend";

    String X_THIS_PARTY_ACTION_BINDING = "/" + X_TP_NAMESPACE
            + "ThisPartyActionBinding";

    String X_CHANNEL_ID = "/" + X_TP_NAMESPACE + "ChannelId";

    String X_DELIVERY_CHANNEL = "/" + X_TP_NAMESPACE + "DeliveryChannel";

    String X_MESSAGING_CHARACTERISTICS = "/" + X_TP_NAMESPACE
            + "MessagingCharacteristics";

    String X_TRANSPORT = "/" + X_TP_NAMESPACE + "Transport";

    String X_TRANSPORT_RECEIVER = "/" + X_TP_NAMESPACE + "TransportReceiver";

    String X_ENDPOINT = "/" + X_TP_NAMESPACE + "Endpoint";

    String X_DOC_EXCHANGE = "/" + X_TP_NAMESPACE + "DocExchange";

    String X_EBXML_SENDER_BINDING = "/" + X_TP_NAMESPACE + "ebXMLSenderBinding";

    String X_RELIABLE_MESSAGING = "/" + X_TP_NAMESPACE + "ReliableMessaging";

    String X_SENDER_NON_REPUDIATION = "/" + X_TP_NAMESPACE
            + "SenderNonRepudiation";

    String X_SENDER_DIGITAL_ENVELOPE = "/" + X_TP_NAMESPACE
            + "SenderDigitalEnvelope";

    /**
     * @param cpa
     * @throws DAOException
     */
    private void addPartnerships(PropertyTree cpa, PropertyTree dom)
            throws DAOException {

        int pi = 0;

        // get the value
        String cpaId = cpa.getProperty(X_COLLABORATION_PROTOCOL_AGREEMENT
                + "/@" + X_TP_NAMESPACE + "cpaid");

        String[] service = cpa.getProperties(X_COLLABORATION_PROTOCOL_AGREEMENT
                + X_PARTY_INFO + X_ATTR_PARTY_NAME + X_COLLABORATION_ROLE
                + X_SERVICE_BINDING + X_SERVICE);

        for (int serviceIndex = 0; serviceIndex < service.length; serviceIndex++) {
            String X_ATTR_SERVICE = "[" + X_TP_NAMESPACE + "Service='"
                    + service[serviceIndex] + "']";

            String[] action = cpa
                    .getProperties(X_COLLABORATION_PROTOCOL_AGREEMENT
                            + X_PARTY_INFO + X_ATTR_PARTY_NAME
                            + X_COLLABORATION_ROLE + X_SERVICE_BINDING
                            + X_ATTR_SERVICE + X_CAN_SEND
                            + X_THIS_PARTY_ACTION_BINDING + "/@"
                            + X_TP_NAMESPACE + "action");

            for (int actionIndex = 0; actionIndex < action.length; actionIndex++) {
                String X_ATTR_ACTION = "[@" + X_TP_NAMESPACE + "action='"
                        + action[actionIndex] + "']";

                // get the value
                /*
                 * String defaultMshChannelId = cpa
                 * .getProperty(X_COLLABORATION_PROTOCOL_AGREEMENT +
                 * X_PARTY_INFO + "/@" + X_TP_NAMESPACE +
                 * "defaultMshChannelId");
                 */

                String[] channelId = cpa
                        .getProperties(X_COLLABORATION_PROTOCOL_AGREEMENT
                                + X_PARTY_INFO + X_ATTR_PARTY_NAME
                                + X_COLLABORATION_ROLE + X_SERVICE_BINDING
                                + X_ATTR_SERVICE + X_CAN_SEND
                                + X_THIS_PARTY_ACTION_BINDING + X_ATTR_ACTION
                                + X_CHANNEL_ID);
                String[] transportId = cpa
                        .getProperties(X_COLLABORATION_PROTOCOL_AGREEMENT
                                + X_PARTY_INFO + X_ATTR_PARTY_NAME
                                + X_DELIVERY_CHANNEL + "/@" + X_TP_NAMESPACE
                                + "transportId");
                String[] docExchangeId = cpa
                        .getProperties(X_COLLABORATION_PROTOCOL_AGREEMENT
                                + X_PARTY_INFO + X_ATTR_PARTY_NAME
                                + X_DELIVERY_CHANNEL + "/@" + X_TP_NAMESPACE
                                + "docExchangeId");

                for (int channelIdIndex = 0; channelIdIndex < channelId.length; channelIdIndex++) {
                    String X_ATTR_CHANNEL_ID = "[@" + X_TP_NAMESPACE
                            + "channelId='" + channelId[channelIdIndex] + "']";
                    String X_ATTR_TRANSPORT_ID = "[@" + X_TP_NAMESPACE
                            + "transportId='" + transportId[channelIdIndex]
                            + "']";
                    String X_ATTR_DOC_EXCHANGE_ID = "[@" + X_TP_NAMESPACE
                            + "docExchangeId='" + docExchangeId[channelIdIndex]
                            + "']";

                    String syncReplyMode = cpa
                            .getProperty(X_COLLABORATION_PROTOCOL_AGREEMENT
                                    + X_PARTY_INFO + X_ATTR_PARTY_NAME
                                    + X_DELIVERY_CHANNEL + X_ATTR_CHANNEL_ID
                                    + X_MESSAGING_CHARACTERISTICS + "/@"
                                    + X_TP_NAMESPACE + "syncReplyMode");

                    String ackRequested = cpa
                            .getProperty(X_COLLABORATION_PROTOCOL_AGREEMENT
                                    + X_PARTY_INFO + X_ATTR_PARTY_NAME
                                    + X_DELIVERY_CHANNEL + X_ATTR_CHANNEL_ID
                                    + X_MESSAGING_CHARACTERISTICS + "/@"
                                    + X_TP_NAMESPACE + "ackRequested");

                    String ackSignRequested = cpa
                            .getProperty(X_COLLABORATION_PROTOCOL_AGREEMENT
                                    + X_PARTY_INFO + X_ATTR_PARTY_NAME
                                    + X_DELIVERY_CHANNEL + X_ATTR_CHANNEL_ID
                                    + X_MESSAGING_CHARACTERISTICS + "/@"
                                    + X_TP_NAMESPACE + "ackSignatureRequested");

                    String dupElimination = cpa
                            .getProperty(X_COLLABORATION_PROTOCOL_AGREEMENT
                                    + X_PARTY_INFO + X_ATTR_PARTY_NAME
                                    + X_DELIVERY_CHANNEL + X_ATTR_CHANNEL_ID
                                    + X_MESSAGING_CHARACTERISTICS + "/@"
                                    + X_TP_NAMESPACE + "duplicateElimination");

                    String actor = cpa
                            .getProperty(X_COLLABORATION_PROTOCOL_AGREEMENT
                                    + X_PARTY_INFO + X_ATTR_PARTY_NAME
                                    + X_DELIVERY_CHANNEL + X_ATTR_CHANNEL_ID
                                    + X_MESSAGING_CHARACTERISTICS + "/@"
                                    + X_TP_NAMESPACE + "actor");

                    String transportProtcol = cpa.getProperty(
                            X_COLLABORATION_PROTOCOL_AGREEMENT + X_PARTY_INFO
                                    + X_ATTR_PARTY_NAME + X_TRANSPORT
                                    + X_ATTR_TRANSPORT_ID
                                    + X_TRANSPORT_RECEIVER + "/"
                                    + X_TP_NAMESPACE + "TransportProtocol")
                            .toLowerCase();

                    String transportEndpoint = cpa
                            .getProperty(X_COLLABORATION_PROTOCOL_AGREEMENT
                                    + X_PARTY_INFO + X_ATTR_PARTY_NAME
                                    + X_TRANSPORT + X_ATTR_TRANSPORT_ID
                                    + X_TRANSPORT_RECEIVER + X_ENDPOINT + "/@"
                                    + X_TP_NAMESPACE + "uri");

                    String retries = cpa
                            .getProperty(X_COLLABORATION_PROTOCOL_AGREEMENT
                                    + X_PARTY_INFO + X_ATTR_PARTY_NAME
                                    + X_DOC_EXCHANGE + X_ATTR_DOC_EXCHANGE_ID
                                    + X_EBXML_SENDER_BINDING
                                    + X_RELIABLE_MESSAGING + "/"
                                    + X_TP_NAMESPACE + "Retries");

                    String retryInterval = cpa
                            .getProperty(X_COLLABORATION_PROTOCOL_AGREEMENT
                                    + X_PARTY_INFO + X_ATTR_PARTY_NAME
                                    + X_DOC_EXCHANGE + X_ATTR_DOC_EXCHANGE_ID
                                    + X_EBXML_SENDER_BINDING
                                    + X_RELIABLE_MESSAGING + "/"
                                    + X_TP_NAMESPACE + "RetryInterval");

                    String messageOrder = cpa
                            .getProperty(X_COLLABORATION_PROTOCOL_AGREEMENT
                                    + X_PARTY_INFO + X_ATTR_PARTY_NAME
                                    + X_DOC_EXCHANGE + X_ATTR_DOC_EXCHANGE_ID
                                    + X_EBXML_SENDER_BINDING
                                    + X_RELIABLE_MESSAGING + "/"
                                    + X_TP_NAMESPACE + "MessageOrderSemantics");

                    String persistDuration = cpa
                            .getProperty(X_COLLABORATION_PROTOCOL_AGREEMENT
                                    + X_PARTY_INFO + X_ATTR_PARTY_NAME
                                    + X_DOC_EXCHANGE + X_ATTR_DOC_EXCHANGE_ID
                                    + X_EBXML_SENDER_BINDING + "/"
                                    + X_TP_NAMESPACE + "PersistDuration");

                    String mdAlgorithm = cpa
                            .getProperty(X_COLLABORATION_PROTOCOL_AGREEMENT
                                    + X_PARTY_INFO + X_ATTR_PARTY_NAME
                                    + X_DOC_EXCHANGE + X_ATTR_DOC_EXCHANGE_ID
                                    + X_EBXML_SENDER_BINDING
                                    + X_SENDER_NON_REPUDIATION + "/"
                                    + X_TP_NAMESPACE + "HashFunction");
                    if (mdAlgorithm != null) {
                        mdAlgorithm = mdAlgorithm.substring(mdAlgorithm
                                .indexOf("#") + 1);
                    }

                    String dsAlgorithm = cpa
                            .getProperty(X_COLLABORATION_PROTOCOL_AGREEMENT
                                    + X_PARTY_INFO + X_ATTR_PARTY_NAME
                                    + X_DOC_EXCHANGE + X_ATTR_DOC_EXCHANGE_ID
                                    + X_EBXML_SENDER_BINDING
                                    + X_SENDER_NON_REPUDIATION + "/"
                                    + X_TP_NAMESPACE + "SignatureAlgorithm");

                    if (dsAlgorithm != null) {
                        dsAlgorithm = dsAlgorithm.substring(dsAlgorithm
                                .indexOf("#") + 1);
                    }

                    String digitalEnvelopeProtocol = cpa
                            .getProperty(X_COLLABORATION_PROTOCOL_AGREEMENT
                                    + X_PARTY_INFO + X_ATTR_PARTY_NAME
                                    + X_DOC_EXCHANGE + X_ATTR_DOC_EXCHANGE_ID
                                    + X_EBXML_SENDER_BINDING
                                    + X_SENDER_DIGITAL_ENVELOPE + "/"
                                    + X_TP_NAMESPACE
                                    + "DigitalEnvelopeProtocol");

                    String encryptionAlgorithm = cpa
                            .getProperty(X_COLLABORATION_PROTOCOL_AGREEMENT
                                    + X_PARTY_INFO + X_ATTR_PARTY_NAME
                                    + X_DOC_EXCHANGE + X_ATTR_DOC_EXCHANGE_ID
                                    + X_EBXML_SENDER_BINDING
                                    + X_SENDER_DIGITAL_ENVELOPE + "/"
                                    + X_TP_NAMESPACE + "EncryptionAlgorithm");

                    if (encryptionAlgorithm != null) {
                        if (encryptionAlgorithm.toLowerCase().indexOf("rc2") != -1) {
                            encryptionAlgorithm = SMimeMessage.ENCRYPT_ALG_RC2_CBC;
                        } else {
                            encryptionAlgorithm = SMimeMessage.ENCRYPT_ALG_DES_EDE3_CBC;
                        }
                    }

                    // add dao
                    PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao
                            .createDAO(PartnershipDAO.class);
                    PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO
                            .createDVO();
                    partnershipDVO.setPartnershipId(cpaId + ","
                            + channelId[channelIdIndex] + "," + action[actionIndex]);
                    partnershipDVO.setCpaId(cpaId);
                    partnershipDVO.setService(service[serviceIndex]);
                    partnershipDVO.setAction(action[actionIndex]);
                    partnershipDVO.setTransportProtocol(transportProtcol);
                    partnershipDVO.setTransportEndpoint(transportEndpoint);
                    partnershipDVO.setIsHostnameVerified("false");

                    if (syncReplyMode != null) {
                        partnershipDVO.setSyncReplyMode(syncReplyMode);
                    } else {
                        partnershipDVO.setSyncReplyMode("none");
                    }
                    if (ackRequested != null) {
                        partnershipDVO.setAckRequested(ackRequested);
                    } else {
                        partnershipDVO.setAckRequested("never");
                    }
                    if (ackSignRequested != null) {
                        partnershipDVO.setAckSignRequested(ackSignRequested);
                    } else {
                        partnershipDVO.setAckSignRequested("never");
                    }
                    if (dupElimination != null) {
                        partnershipDVO.setDupElimination(dupElimination);
                    } else {
                        partnershipDVO.setDupElimination("never");
                    }
                    partnershipDVO.setActor(actor);
                    partnershipDVO.setDisabled("false");
                    int retriesInt = 0;
                    if (retries != null) {
                        retriesInt = Integer.valueOf(retries).intValue();
                    }
                    partnershipDVO.setRetries(retriesInt);
                    int retryIntervalInt = 30000;
                    if (retryInterval != null) {
                    	try{
	                    	DatatypeFactory df = javax.xml.datatype.DatatypeFactory.newInstance();
	                    	Duration duration = df.newDuration(retryInterval);
	                    	retryIntervalInt = (int) duration.getTimeInMillis(new Date());
                    	}catch(Exception ex){
                    		// Use default retry interval when parsing error.                     		
                    	}
                    }
                    partnershipDVO.setRetryInterval(retryIntervalInt);
                    partnershipDVO.setPersistDuration(persistDuration);
                    if (messageOrder != null) {
                        partnershipDVO.setMessageOrder(messageOrder);
                    } else {
                        partnershipDVO.setMessageOrder("NotGuaranteed");
                    }
                    if (mdAlgorithm != null && dsAlgorithm != null) {
                        partnershipDVO.setSignRequested("true");
                        partnershipDVO.setDsAlgorithm(dsAlgorithm);
                        partnershipDVO.setMdAlgorithm(mdAlgorithm);
                    } else {
                        partnershipDVO.setSignRequested("false");
                    }
                    if (digitalEnvelopeProtocol != null) {
                        if (digitalEnvelopeProtocol.equalsIgnoreCase("s/mime")) {
                            partnershipDVO.setEncryptRequested("true");
                            partnershipDVO
                                    .setEncryptAlgorithm(encryptionAlgorithm);
                        } else {
                            partnershipDVO.setEncryptRequested("false");
                        }
                    } else {
                        partnershipDVO.setEncryptRequested("false");
                    }
                    
                    EbmsProcessor.core.log.info("Adding partnership: " + partnershipDVO.getPartnershipId());
                    
                    if (partnershipDAO.retrieve(partnershipDVO)) {
                        throw new DAOException("Duplicate Partnership exists");
                    } else {
                        partnershipDAO.create(partnershipDVO);
                    }

                    pi++;

                    dom.setProperty("partnership[" + pi + "]/partnership_id",
                            partnershipDVO.getPartnershipId());
                    dom.setProperty("partnership[" + pi + "]/cpa_id",
                            partnershipDVO.getCpaId());
                    dom.setProperty("partnership[" + pi + "]/service",
                            partnershipDVO.getService());
                    dom.setProperty("partnership[" + pi + "]/action_id",
                            partnershipDVO.getAction());
                    String transportProtcolElement = partnershipDVO
                            .getTransportProtocol();
                    dom
                            .setProperty(
                                    "partnership[" + pi
                                            + "]/transport_protocol",
                                    transportProtcolElement != null ? transportProtcolElement
                                            : "");
                    String transportEndpointElement = partnershipDVO
                            .getTransportEndpoint();
                    dom
                            .setProperty(
                                    "partnership[" + pi
                                            + "]/transport_endpoint",
                                    transportEndpointElement != null ? transportEndpointElement
                                            : "");
                    String syncReplyModeElement = partnershipDVO
                            .getSyncReplyMode();
                    dom.setProperty("partnership[" + pi + "]/sync_reply_mode",
                            syncReplyModeElement != null ? syncReplyModeElement
                                    : "");
                    dom.setProperty("partnership[" + pi + "]/ack_requested",
                            partnershipDVO.getAckRequested());
                    dom.setProperty("partnership[" + pi
                            + "]/ack_sign_requested", partnershipDVO
                            .getAckSignRequested());
                    dom.setProperty("partnership[" + pi + "]/dup_elimination",
                            partnershipDVO.getDupElimination());
                    String actorElement = partnershipDVO.getActor();
                    dom.setProperty("partnership[" + pi + "]/actor",
                            actorElement != null ? actorElement : "");
                    dom.setProperty("partnership[" + pi + "]/disabled",
                            partnershipDVO.getDisabled());
                    dom.setProperty("partnership[" + pi + "]/retries", String
                            .valueOf(partnershipDVO.getRetries()));
                    dom.setProperty("partnership[" + pi + "]/retry_interval",
                            String.valueOf(partnershipDVO.getRetryInterval()));
                    String persistDurationElement = partnershipDVO
                            .getPersistDuration();
                    dom
                            .setProperty(
                                    "partnership[" + pi + "]/persist_duration",
                                    persistDurationElement != null ? persistDurationElement
                                            : "");
                    String messageOrderElement = partnershipDVO
                            .getMessageOrder();
                    dom.setProperty("partnership[" + pi + "]/message_order",
                            messageOrderElement != null ? messageOrderElement
                                    : "");
                    dom.setProperty("partnership[" + pi + "]/sign_requested",
                            partnershipDVO.getSignRequested());
                    String dsAlgorithmElement = partnershipDVO.getDsAlgorithm();
                    dom.setProperty("partnership[" + pi + "]/ds_algorithm",
                            dsAlgorithmElement != null ? dsAlgorithmElement
                                    : "");
                    String mdAlgorithmElement = partnershipDVO.getMdAlgorithm();
                    dom.setProperty("partnership[" + pi + "]/md_algorithm",
                            mdAlgorithmElement != null ? mdAlgorithmElement
                                    : "");
                    dom.setProperty(
                            "partnership[" + pi + "]/encrypt_requested",
                            partnershipDVO.getEncryptRequested());
                    String encryptAlgorithmElement = partnershipDVO
                            .getEncryptAlgorithm();
                    dom
                            .setProperty(
                                    "partnership[" + pi + "]/encrypt_algorithm",
                                    encryptAlgorithmElement != null ? encryptAlgorithmElement
                                            : "");
                }
            }
        }
    }
}