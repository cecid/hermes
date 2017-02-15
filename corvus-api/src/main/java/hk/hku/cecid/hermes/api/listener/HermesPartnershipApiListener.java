package hk.hku.cecid.hermes.api.listener;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.edi.as2.AS2Processor;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.json.JsonParseException;
import hk.hku.cecid.piazza.commons.rest.RestRequest;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.hermes.api.Constants;
import hk.hku.cecid.hermes.api.ErrorCode;
import hk.hku.cecid.hermes.api.spa.ApiPlugin;

/**
 * HermesPartnershipApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesPartnershipApiListener extends HermesProtocolApiListener {

    protected Map<String, Object> processGetRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = parseFromPathInfo(httpRequest.getPathInfo()).get(1);
        ApiPlugin.core.log.info("Get partnership API invoked, protocol = " + protocol);

        if (protocol.equalsIgnoreCase(Constants.EBMS_PROTOCOL)) {
            return getEbmsPartnership();
        }
        else if (protocol.equalsIgnoreCase(Constants.AS2_PROTOCOL)) {
            return getAs2Partnership();
        }
        else {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }
    }

    protected Map<String, Object> getEbmsPartnership() {
        try {
            hk.hku.cecid.ebms.spa.dao.PartnershipDAO partnershipDAO =
                (hk.hku.cecid.ebms.spa.dao.PartnershipDAO) EbmsProcessor.core.dao.createDAO(hk.hku.cecid.ebms.spa.dao.PartnershipDAO.class);
            ArrayList<Object> partnershipList = new ArrayList<Object>();
            for (Iterator i = partnershipDAO.findAllPartnerships().iterator(); i.hasNext(); ) {
                hk.hku.cecid.ebms.spa.dao.PartnershipDVO partnershipDVO = (hk.hku.cecid.ebms.spa.dao.PartnershipDVO) i.next();

                Map<String, Object> partnershipDict = new HashMap<String, Object>();
                partnershipDict.put("id", partnershipDVO.getPartnershipId());
                partnershipDict.put("cpa_id", partnershipDVO.getCpaId());
                partnershipDict.put("service", partnershipDVO.getService());
                partnershipDict.put("action", partnershipDVO.getAction());
                if (partnershipDVO.getDisabled() != null && partnershipDVO.getDisabled().equals("true")) {
                    partnershipDict.put("disabled", new Boolean(true));
                }
                else {
                    partnershipDict.put("disabled", new Boolean(false));
                }
                partnershipDict.put("transport_endpoint", partnershipDVO.getTransportEndpoint());
                partnershipDict.put("ack_requested", partnershipDVO.getAckRequested());
                partnershipDict.put("signed_ack_requested", partnershipDVO.getAckSignRequested());
                partnershipDict.put("duplicate_elimination", partnershipDVO.getDupElimination());
                partnershipDict.put("message_order", partnershipDVO.getMessageOrder());
                partnershipDict.put("retries", new Integer(partnershipDVO.getRetries()));
                partnershipDict.put("retry_interval", new Integer(partnershipDVO.getRetryInterval()));
                if (partnershipDVO.getSignRequested() != null && partnershipDVO.getSignRequested().equals("true")) {
                    partnershipDict.put("sign_requested", new Boolean(true));
                }
                else {
                    partnershipDict.put("sign_requested", new Boolean(false));
                }
                String cert = null;
                if (partnershipDVO.getSignCert() != null) {
                    Base64.Encoder encoder = Base64.getEncoder();
                    cert = new String(encoder.encode(partnershipDVO.getSignCert()));
                }
                partnershipDict.put("sign_certicate", cert);
                partnershipList.add(partnershipDict);
            }

            Map<String, Object> returnObj = new HashMap<String, Object>();
            returnObj.put("partnerships", partnershipList);
            ApiPlugin.core.log.info("" + partnershipList.size() + " partnerships returned");
            return returnObj;

        } catch (DAOException e) {
            String errorMessage = "DAO exception";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_READING_DATABASE, errorMessage);
        } catch (Exception e) {
            String errorMessage = "Unknown exception: " + e.getMessage();
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_UNKNOWN, errorMessage);
        }
    }

    protected Map<String, Object> getAs2Partnership() {
        try {
            hk.hku.cecid.edi.as2.dao.PartnershipDAO partnershipDAO =
                (hk.hku.cecid.edi.as2.dao.PartnershipDAO) AS2Processor.core.dao.createDAO(hk.hku.cecid.edi.as2.dao.PartnershipDAO.class);
            ArrayList<Object> partnershipList = new ArrayList<Object>();
            for (Iterator i = partnershipDAO.findAllPartnerships().iterator(); i.hasNext(); ) {
                hk.hku.cecid.edi.as2.dao.PartnershipDVO partnershipDVO = (hk.hku.cecid.edi.as2.dao.PartnershipDVO) i.next();

                Map<String, Object> partnershipDict = new HashMap<String, Object>();
                partnershipDict.put("id", partnershipDVO.getPartnershipId());
                partnershipDict.put("as2_from", partnershipDVO.getAS2From());
                partnershipDict.put("as2_to", partnershipDVO.getAs2To());
                partnershipDict.put("id", partnershipDVO.getPartnershipId());
                partnershipDict.put("disabled", new Boolean(partnershipDVO.isDisabled()));
                partnershipDict.put("sync_reply", new Boolean(partnershipDVO.isSyncReply()));
                partnershipDict.put("subject", partnershipDVO.getSubject());
                partnershipDict.put("recipient_address", partnershipDVO.getRecipientAddress());
                partnershipDict.put("hostname_verified", new Boolean(partnershipDVO.isHostnameVerified()));
                partnershipDict.put("receipt_address", partnershipDVO.getReceiptAddress());
                partnershipDict.put("receipt_required", new Boolean(partnershipDVO.isReceiptRequired()));
                partnershipDict.put("outbound_sign_required", new Boolean(partnershipDVO.isOutboundSignRequired()));
                partnershipDict.put("outbound_encrypt_required", new Boolean(partnershipDVO.isOutboundEncryptRequired()));
                partnershipDict.put("outbound_compress_required", new Boolean(partnershipDVO.isOutboundCompressRequired()));
                partnershipDict.put("receipt_sign_required", new Boolean(partnershipDVO.isReceiptSignRequired()));
                partnershipDict.put("inbound_sign_required", new Boolean(partnershipDVO.isInboundSignRequired()));
                partnershipDict.put("inbound_encrypt_required", new Boolean(partnershipDVO.isInboundEncryptRequired()));
                partnershipDict.put("retries", new Integer(partnershipDVO.getRetries()));
                partnershipDict.put("retry_interval", new Integer(partnershipDVO.getRetryInterval()));
                partnershipDict.put("sign_algorithm", partnershipDVO.getSignAlgorithm());
                partnershipDict.put("encrypt_algorithm", partnershipDVO.getEncryptAlgorithm());
                partnershipDict.put("mic_algorithm", partnershipDVO.getMicAlgorithm());
                String encryptCert = null;
                if (partnershipDVO.getEncryptCert() != null) {
                    Base64.Encoder encoder = Base64.getEncoder();
                    encryptCert = new String(encoder.encode(partnershipDVO.getEncryptCert()));
                }
                partnershipDict.put("encrypt_certicate", encryptCert);
                String verifyCert = null;
                if (partnershipDVO.getVerifyCert() != null) {
                    Base64.Encoder encoder = Base64.getEncoder();
                    verifyCert = new String(encoder.encode(partnershipDVO.getVerifyCert()));
                }
                partnershipDict.put("verify_certicate", verifyCert);
                partnershipList.add(partnershipDict);
            }

            Map<String, Object> returnObj = new HashMap<String, Object>();
            returnObj.put("partnerships", partnershipList);
            ApiPlugin.core.log.info("" + partnershipList.size() + " partnerships returned");
            return returnObj;

        } catch (DAOException e) {
            String errorMessage = "DAO exception";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_READING_DATABASE, errorMessage);
        } catch (Exception e) {
            String errorMessage = "Unknown exception: " + e.getMessage();
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_UNKNOWN, errorMessage);
        }
    }

    protected Map<String, Object> processPostRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = parseFromPathInfo(httpRequest.getPathInfo()).get(1);
        ApiPlugin.core.log.info("Add partnership API invoked, protocol = " + protocol);

        Map<String, Object> inputDict = null;
        try {
            inputDict = getDictionaryFromRequest(httpRequest);
        } catch (IOException e) {
            String errorMessage = "Exception while reading input stream";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_READING_REQUEST, errorMessage);
        } catch (JsonParseException e) {
            String errorMessage = "Exception while parsing input stream";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }

        if (protocol.equalsIgnoreCase(Constants.EBMS_PROTOCOL)) {
            return addEbmsPartnership(inputDict);
        }
        else if (protocol.equalsIgnoreCase(Constants.AS2_PROTOCOL)) {
            return addAs2Partnership(inputDict);
        }
        else {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }
    }

    protected Map<String, Object> addEbmsPartnership(Map<String, Object> inputDict) {
        String id = null;
        try {
            id = (String) inputDict.get("id");
            if (id == null) {
                String errorMessage = "Missing required partnership field: id";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: id";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }
        String cpa_id = null;
        try {
            cpa_id = (String) inputDict.get("cpa_id");
            if (cpa_id == null) {
                String errorMessage = "Missing required partnership field: cpa_id";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: cpa_id";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }
        String service = null;
        try {
            service = (String) inputDict.get("service");
            if (service == null) {
                String errorMessage = "Missing required partnership field: service";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: service";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }
        String action = null;
        try {
            action = (String) inputDict.get("action");
            if (action == null) {
                String errorMessage = "Missing required partnership field: action";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: action";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }
        String transport_endpoint = null;
        try {
            transport_endpoint = (String) inputDict.get("transport_endpoint");
            if (transport_endpoint == null) {
                String errorMessage = "Missing required partnership field: transport_endpoint";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: transport_endpoint";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }

        ApiPlugin.core.log.debug("Parameters: id=" + id + ", cpa_id=" + cpa_id +
                                 ", service=" + service + ", action=" + action +
                                 ", transport_endpoint=" + transport_endpoint);

        try {
            // check if partnership id already exists
            hk.hku.cecid.ebms.spa.dao.PartnershipDAO partnershipDAO = (hk.hku.cecid.ebms.spa.dao.PartnershipDAO) EbmsProcessor.core.dao.createDAO(hk.hku.cecid.ebms.spa.dao.PartnershipDAO.class);
            hk.hku.cecid.ebms.spa.dao.PartnershipDVO partnershipDVO = (hk.hku.cecid.ebms.spa.dao.PartnershipDVO) partnershipDAO.createDVO();
            partnershipDVO.setPartnershipId(id);
            if (partnershipDAO.retrieve(partnershipDVO)) {
                String errorMessage = "Partnership [" + id + "] already exists";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_RECORD_ALREADY_EXIST, errorMessage);
            }

            partnershipDVO = (hk.hku.cecid.ebms.spa.dao.PartnershipDVO) partnershipDAO.createDVO();
            partnershipDVO.setCpaId(cpa_id);
            partnershipDVO.setService(service);
            partnershipDVO.setAction(action);

            if (partnershipDAO.findPartnershipByCPA(partnershipDVO)) {
                String errorMessage = "Partnership with same CPA parameters already exists";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_RECORD_ALREADY_EXIST, errorMessage);
            }

            partnershipDVO = (hk.hku.cecid.ebms.spa.dao.PartnershipDVO) partnershipDAO.createDVO();
            partnershipDVO.setDisabled(Constants.DEFAULT_EBMS_PARTNERSHIP_DISABLED);
            partnershipDVO.setPartnershipId(id);
            partnershipDVO.setCpaId(cpa_id);
            partnershipDVO.setService(service);
            partnershipDVO.setAction(action);
            partnershipDVO.setTransportEndpoint(transport_endpoint);
            partnershipDVO.setRetryInterval(Constants.DEFAULT_EBMS_PARTNERSHIP_RETRY_INTERVAL);
            partnershipDVO.setRetries(Constants.DEFAULT_EBMS_PARTNERSHIP_RETRY_COUNT);
            partnershipDVO.setTransportProtocol(Constants.DEFAULT_EBMS_PARTNERSHIP_TRANSPORT_PROTOCOL);
            partnershipDVO.setIsHostnameVerified(Constants.DEFAULT_EBMS_PARTNERSHIP_HOSTNAME_VERIFY);
            partnershipDVO.setSyncReplyMode(Constants.DEFAULT_EBMS_PARTNERSHIP_SYNC_REPLY_MODE);
            partnershipDVO.setAckRequested(Constants.DEFAULT_EBMS_PARTNERSHIP_ACK_REQUESTED);
            partnershipDVO.setAckSignRequested(Constants.DEFAULT_EBMS_PARTNERSHIP_ACK_SIGN_REQUESTED);
            partnershipDVO.setDupElimination(Constants.DEFAULT_EBMS_PARTNERSHIP_DUPLICATE_ELIMINATION);
            partnershipDVO.setMessageOrder(Constants.DEFAULT_EBMS_PARTNERSHIP_MESSAGE_ORDER);
            partnershipDVO.setSignRequested(Constants.DEFAULT_EBMS_PARTNERSHIP_SIGN_REQUESTED);
            partnershipDVO.setEncryptRequested(Constants.DEFAULT_EBMS_PARTNERSHIP_ENCRYPT_REQUESTED);

            partnershipDAO.create(partnershipDVO);
            Map<String, Object> returnObj = new HashMap<String, Object>();
            returnObj.put("id", id);
            return returnObj;
        } catch (DAOException e) {
            String errorMessage = "Error saving partnership";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_WRITING_DATABASE, errorMessage);
        } catch (Exception e) {
            String errorMessage = "Unknown exception: " + e.getMessage();
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_UNKNOWN, errorMessage);
        }
    }

    protected Map<String, Object> addAs2Partnership(Map<String, Object> inputDict) {
        String id = null;
        try {
            id = (String) inputDict.get("id");
            if (id == null) {
                String errorMessage = "Missing required partnership field: id";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: id";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }
        String as2_from = null;
        try {
            as2_from = (String) inputDict.get("as2_from");
            if (as2_from == null) {
                String errorMessage = "Missing required partnership field: as2_from";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: as2_from";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }
        String as2_to = null;
        try {
            as2_to = (String) inputDict.get("as2_to");
            if (as2_to == null) {
                String errorMessage = "Missing required partnership field: as2_to";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: as2_to";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }
        String subject = null;
        try {
            subject = (String) inputDict.get("subject");
            if (subject == null) {
                String errorMessage = "Missing required partnership field: subject";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: subject";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }
        String recipient_address = null;
        try {
            recipient_address = (String) inputDict.get("recipient_address");
            if (recipient_address == null) {
                String errorMessage = "Missing required partnership field: recipient_address";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: recipient_address";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }

        ApiPlugin.core.log.debug("Parameters: id=" + id + ", as2_from=" + as2_from +
                                 ", as2_to=" + as2_to + ", subject=" + subject +
                                 ", recipient_address=" + recipient_address);

        try {
            // check if partnership id already exists
            hk.hku.cecid.edi.as2.dao.PartnershipDAO partnershipDAO = (hk.hku.cecid.edi.as2.dao.PartnershipDAO) AS2Processor.core.dao.createDAO(hk.hku.cecid.edi.as2.dao.PartnershipDAO.class);
            hk.hku.cecid.edi.as2.dao.PartnershipDVO partnershipDVO = (hk.hku.cecid.edi.as2.dao.PartnershipDVO) partnershipDAO.createDVO();
            partnershipDVO.setPartnershipId(id);
            if (partnershipDAO.retrieve(partnershipDVO)) {
                String errorMessage = "Partnership [" + id + "] already exists";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_RECORD_ALREADY_EXIST, errorMessage);
            }

            if (partnershipDAO.findPartnershipsByPartyID(as2_from, as2_to).size() > 0) {
                String errorMessage = "Partnership with same from/to parameters already exists";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_RECORD_ALREADY_EXIST, errorMessage);
            }

            partnershipDVO = (hk.hku.cecid.edi.as2.dao.PartnershipDVO) partnershipDAO.createDVO();
            partnershipDVO.setPartnershipId(id);
            partnershipDVO.setAs2From(as2_from);
            partnershipDVO.setAs2To(as2_to);
            partnershipDVO.setSubject(subject);
            partnershipDVO.setRecipientAddress(recipient_address);
            partnershipDVO.setIsDisabled(Constants.DEFAULT_AS2_PARTNERSHIP_DISABLED);
            partnershipDVO.setIsSyncReply(Constants.DEFAULT_AS2_PARTNERSHIP_SYNC_REPLY);
            partnershipDVO.setIsHostnameVerified(Constants.DEFAULT_AS2_PARTNERSHIP_HOSTNAME_VERIFY);
            partnershipDVO.setReceiptAddress(Constants.DEFAULT_AS2_PARTNERSHIP_RECEIPT_ADDRESS);
            partnershipDVO.setIsReceiptRequired(Constants.DEFAULT_AS2_PARTNERSHIP_RECEIPT_REQUIRED);
            partnershipDVO.setIsOutboundSignRequired(Constants.DEFAULT_AS2_PARTNERSHIP_IS_OUTBOUND_SIGN_REQUIRED);
            partnershipDVO.setIsOutboundEncryptRequired(Constants.DEFAULT_AS2_PARTNERSHIP_IS_OUTBOUND_ENCRYPT_REQUIRED);
            partnershipDVO.setIsOutboundCompressRequired(Constants.DEFAULT_AS2_PARTNERSHIP_IS_OUTBOUND_COMPRESS_REQUIRED);
            partnershipDVO.setIsReceiptSignRequired(Constants.DEFAULT_AS2_PARTNERSHIP_IS_RECEIPT_SIGN_REQUIRED);
            partnershipDVO.setIsInboundSignRequired(Constants.DEFAULT_AS2_PARTNERSHIP_IS_INBOUND_SIGN_REQUIRED);
            partnershipDVO.setIsInboundEncryptRequired(Constants.DEFAULT_AS2_PARTNERSHIP_IS_INBOUND_ENCRYPT_REQUIRED);
            partnershipDVO.setRetries(Constants.DEFAULT_AS2_PARTNERSHIP_RETRY_COUNT);
            partnershipDVO.setRetryInterval(Constants.DEFAULT_AS2_PARTNERSHIP_RETRY_INTERVAL);
            partnershipDVO.setSignAlgorithm(Constants.DEFAULT_AS2_PARTNERSHIP_SIGN_ALGORITHM);
            partnershipDVO.setEncryptAlgorithm(Constants.DEFAULT_AS2_PARTNERSHIP_ENCRYPT_ALGORITHM);
            partnershipDVO.setMicAlgorithm(Constants.DEFAULT_AS2_PARTNERSHIP_MIC_ALGORITHM);

            partnershipDAO.create(partnershipDVO);
            Map<String, Object> returnObj = new HashMap<String, Object>();
            returnObj.put("id", id);
            return returnObj;
        } catch (DAOException e) {
            String errorMessage = "Error saving partnership";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_WRITING_DATABASE, errorMessage);
        } catch (Exception e) {
            String errorMessage = "Unknown exception: " + e.getMessage();
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_UNKNOWN, errorMessage);
        }
    }

    protected Map<String, Object> processDeleteRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        ArrayList<String> pathInfo = parseFromPathInfo(httpRequest.getPathInfo());
        String protocol = pathInfo.get(1);
        String id = pathInfo.get(2);
        ApiPlugin.core.log.info("Delete partnership API invoked, protocol = " + protocol + ", id = " + id);

        if (protocol.equalsIgnoreCase(Constants.EBMS_PROTOCOL)) {
            return removeEbmsPartnership(id);
        }
        else if (protocol.equalsIgnoreCase(Constants.AS2_PROTOCOL)) {
            return removeAs2Partnership(id);
        }
        else {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }
    }

    protected Map<String, Object> removeEbmsPartnership(String id) {
        if ("".equals(id)) {
            String errorMessage = "Missing required partnership field: id";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
        }

        boolean success = false;
        try {
            hk.hku.cecid.ebms.spa.dao.PartnershipDAO partnershipDAO = (hk.hku.cecid.ebms.spa.dao.PartnershipDAO) EbmsProcessor.core.dao.createDAO(hk.hku.cecid.ebms.spa.dao.PartnershipDAO.class);
            hk.hku.cecid.ebms.spa.dao.PartnershipDVO partnershipDVO = (hk.hku.cecid.ebms.spa.dao.PartnershipDVO) partnershipDAO.createDVO();
            partnershipDVO.setPartnershipId(id);
            if (!partnershipDAO.retrieve(partnershipDVO)) {
                String errorMessage = "Partnership [" + id + "] not found";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_READING_DATABASE, errorMessage);
            }

            success = partnershipDAO.remove(partnershipDVO);
        } catch (DAOException e) {
            String errorMessage = "Error deleting partnership";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_WRITING_DATABASE, errorMessage);
        }

        return createActionResult(id, success);
    }

    protected Map<String, Object> removeAs2Partnership(String id) {
        if ("".equals(id)) {
            String errorMessage = "Missing required partnership field: id";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
        }

        boolean success = false;
        try {
            hk.hku.cecid.edi.as2.dao.PartnershipDAO partnershipDAO = (hk.hku.cecid.edi.as2.dao.PartnershipDAO) AS2Processor.core.dao.createDAO(hk.hku.cecid.edi.as2.dao.PartnershipDAO.class);
            hk.hku.cecid.edi.as2.dao.PartnershipDVO partnershipDVO = (hk.hku.cecid.edi.as2.dao.PartnershipDVO) partnershipDAO.createDVO();
            partnershipDVO.setPartnershipId(id);
            if (!partnershipDAO.retrieve(partnershipDVO)) {
                String errorMessage = "Partnership [" + id + "] not found";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_READING_DATABASE, errorMessage);
            }

            success = partnershipDAO.remove(partnershipDVO);
        } catch (DAOException e) {
            String errorMessage = "Error deleting partnership";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_WRITING_DATABASE, errorMessage);
        }

        return createActionResult(id, success);
    }
}
