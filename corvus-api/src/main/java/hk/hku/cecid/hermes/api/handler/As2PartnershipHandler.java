package hk.hku.cecid.hermes.api.handler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import hk.hku.cecid.edi.as2.AS2Processor;
import hk.hku.cecid.edi.as2.dao.PartnershipDAO;
import hk.hku.cecid.edi.as2.dao.PartnershipDVO;
import hk.hku.cecid.hermes.api.listener.HermesAbstractApiListener;
import hk.hku.cecid.hermes.api.Constants;
import hk.hku.cecid.hermes.api.ErrorCode;
import hk.hku.cecid.hermes.api.spa.ApiPlugin;
import hk.hku.cecid.piazza.commons.dao.DAOException;

import org.apache.commons.codec.binary.Base64;


public class As2PartnershipHandler extends MessageHandler implements PartnershipHandler {

    public As2PartnershipHandler(HermesAbstractApiListener listener) {
        super(listener);
    }

    public Map<String, Object> getPartnerships() {
        try {
            PartnershipDAO partnershipDAO = (PartnershipDAO) AS2Processor.core.dao.createDAO(PartnershipDAO.class);
            ArrayList<Object> partnershipList = new ArrayList<Object>();
            for (Iterator i = partnershipDAO.findAllPartnerships().iterator(); i.hasNext(); ) {
                PartnershipDVO partnershipDVO = (PartnershipDVO) i.next();

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
                    encryptCert = new String(Base64.encodeBase64(partnershipDVO.getEncryptCert()));
                }
                partnershipDict.put("encrypt_certicate", encryptCert);
                String verifyCert = null;
                if (partnershipDVO.getVerifyCert() != null) {
                    verifyCert = new String(Base64.encodeBase64(partnershipDVO.getVerifyCert()));
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
            return listener.createError(ErrorCode.ERROR_READING_DATABASE, errorMessage);
        } catch (Exception e) {
            String errorMessage = "Unknown exception: " + e.getMessage();
            ApiPlugin.core.log.error(errorMessage, e);
            return listener.createError(ErrorCode.ERROR_UNKNOWN, errorMessage);
        }
    }

    public Map<String, Object> addPartnership(Map<String, Object> inputDict) {
        Map<String, Object> errorObject = new HashMap<String, Object>();
        String id = listener.getStringFromInput(inputDict, "id", errorObject);
        if (id == null) {
            return errorObject;
        }
        String as2_from = listener.getStringFromInput(inputDict, "as2_from", errorObject);
        if (as2_from == null) {
            return errorObject;
        }
        String as2_to = listener.getStringFromInput(inputDict, "as2_to", errorObject);
        if (as2_to == null) {
            return errorObject;
        }
        String subject = listener.getStringFromInput(inputDict, "subject", errorObject);
        if (subject == null) {
            return errorObject;
        }
        String recipient_address = listener.getStringFromInput(inputDict, "recipient_address", errorObject);
        if (recipient_address == null) {
            return errorObject;
        }
        // check transport endpoint as URL
        try {
            URL url = new URL(recipient_address);
        } catch (MalformedURLException e) {
            listener.fillError(errorObject, ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, "Unknown URL: " + recipient_address);
            return errorObject;
        }
        Boolean booleanObj;
        boolean disabled = Constants.DEFAULT_AS2_PARTNERSHIP_DISABLED;
        booleanObj = listener.getOptionalBooleanFromInput(inputDict, "disabled",
                                Boolean.valueOf(Constants.DEFAULT_AS2_PARTNERSHIP_DISABLED),
                                errorObject);
        if (booleanObj != null) {
            disabled = booleanObj.booleanValue();
        }
        boolean syncReply = Constants.DEFAULT_AS2_PARTNERSHIP_SYNC_REPLY;
        booleanObj = listener.getOptionalBooleanFromInput(inputDict, "sync_reply",
                                Constants.DEFAULT_AS2_PARTNERSHIP_SYNC_REPLY,
                                errorObject);
        if (booleanObj != null) {
            syncReply = booleanObj.booleanValue();
        }
        boolean isHostnameVerified = Constants.DEFAULT_AS2_PARTNERSHIP_HOSTNAME_VERIFY;
        booleanObj = listener.getOptionalBooleanFromInput(inputDict, "hostname_verified",
                                Constants.DEFAULT_AS2_PARTNERSHIP_HOSTNAME_VERIFY,
                                errorObject);
        if (booleanObj != null) {
            isHostnameVerified = booleanObj.booleanValue();
        }
        String receiptAddress = listener.getOptionalStringFromInput(inputDict, "receipt_address",
                                Constants.DEFAULT_AS2_PARTNERSHIP_RECEIPT_ADDRESS,
                                errorObject);
        boolean isReceiptRequired = Constants.DEFAULT_AS2_PARTNERSHIP_RECEIPT_REQUIRED;
        booleanObj = listener.getOptionalBooleanFromInput(inputDict, "receipt_required",
                                Constants.DEFAULT_AS2_PARTNERSHIP_RECEIPT_REQUIRED,
                                errorObject);
        if (booleanObj != null) {
            isReceiptRequired = booleanObj.booleanValue();
        }
        boolean isOutboundSignRequired = Constants.DEFAULT_AS2_PARTNERSHIP_IS_OUTBOUND_SIGN_REQUIRED;
        booleanObj = listener.getOptionalBooleanFromInput(inputDict, "outbound_sign_required",
                                Constants.DEFAULT_AS2_PARTNERSHIP_IS_OUTBOUND_SIGN_REQUIRED,
                                errorObject);
        if (booleanObj != null) {
            isOutboundSignRequired = booleanObj.booleanValue();
        }
        boolean isOutboundEncryptRequired = Constants.DEFAULT_AS2_PARTNERSHIP_IS_OUTBOUND_ENCRYPT_REQUIRED;
        booleanObj = listener.getOptionalBooleanFromInput(inputDict, "outbound_encrypt_required",
                                Constants.DEFAULT_AS2_PARTNERSHIP_IS_OUTBOUND_ENCRYPT_REQUIRED,
                                errorObject);
        if (booleanObj != null) {
            isOutboundEncryptRequired = booleanObj.booleanValue();
        }
        boolean isOutboundCompressRequired = Constants.DEFAULT_AS2_PARTNERSHIP_IS_OUTBOUND_COMPRESS_REQUIRED;
        booleanObj = listener.getOptionalBooleanFromInput(inputDict, "outbound_compress_required",
                                Constants.DEFAULT_AS2_PARTNERSHIP_IS_OUTBOUND_COMPRESS_REQUIRED,
                                errorObject);
        if (booleanObj != null) {
            isOutboundCompressRequired = booleanObj.booleanValue();
        }
        boolean isReceiptSignRequired = Constants.DEFAULT_AS2_PARTNERSHIP_IS_RECEIPT_SIGN_REQUIRED;
        booleanObj = listener.getOptionalBooleanFromInput(inputDict, "receipt_sign_required",
                                Constants.DEFAULT_AS2_PARTNERSHIP_IS_RECEIPT_SIGN_REQUIRED,
                                errorObject);
        if (booleanObj != null) {
            isReceiptSignRequired = booleanObj.booleanValue();
        }
        boolean isInboundSignRequired = Constants.DEFAULT_AS2_PARTNERSHIP_IS_INBOUND_SIGN_REQUIRED;
        booleanObj = listener.getOptionalBooleanFromInput(inputDict, "inbound_sign_required",
                                Constants.DEFAULT_AS2_PARTNERSHIP_IS_INBOUND_SIGN_REQUIRED,
                                errorObject);
        if (booleanObj != null) {
            isInboundSignRequired = booleanObj.booleanValue();
        }
        boolean isInboundEncryptRequired = Constants.DEFAULT_AS2_PARTNERSHIP_IS_INBOUND_ENCRYPT_REQUIRED;
        booleanObj = listener.getOptionalBooleanFromInput(inputDict, "inbound_encrypt_required",
                                Constants.DEFAULT_AS2_PARTNERSHIP_IS_INBOUND_ENCRYPT_REQUIRED,
                                errorObject);
        if (booleanObj != null) {
            isInboundEncryptRequired = booleanObj.booleanValue();
        }
        Long retryIntervalObj = listener.getOptionalLongFromInput(inputDict, "retry_interval",
                                Constants.DEFAULT_AS2_PARTNERSHIP_RETRY_INTERVAL,
                                errorObject);
        int retryInterval = Constants.DEFAULT_AS2_PARTNERSHIP_RETRY_INTERVAL;
        if (retryIntervalObj != null) {
            retryInterval = retryIntervalObj.intValue();
        }
        Long retriesObj = listener.getOptionalLongFromInput(inputDict, "retries",
                                Constants.DEFAULT_AS2_PARTNERSHIP_RETRY_COUNT,
                                errorObject);
        int retries = Constants.DEFAULT_AS2_PARTNERSHIP_RETRY_COUNT;
        if (retriesObj != null) {
            retries = retriesObj.intValue();
        }
        String signAlgorithm = listener.getOptionalStringFromInput(inputDict, "sign_algorithm",
                                Constants.DEFAULT_AS2_PARTNERSHIP_SIGN_ALGORITHM,
                                errorObject);
        String encryptAlgorithm = listener.getOptionalStringFromInput(inputDict, "encrypt_algorithm",
                                Constants.DEFAULT_AS2_PARTNERSHIP_ENCRYPT_ALGORITHM,
                                errorObject);
        String micAlgorithm = listener.getOptionalStringFromInput(inputDict, "mic_algorithm",
                                Constants.DEFAULT_AS2_PARTNERSHIP_MIC_ALGORITHM,
                                errorObject);
        String encryptCert = listener.getOptionalStringFromInput(inputDict, "encrypt_certicate", "", errorObject);
        String verifyCert = listener.getOptionalStringFromInput(inputDict, "verify_certicate", "", errorObject);
        if (errorObject.size() > 0) {
            return errorObject;
        }

        ApiPlugin.core.log.debug("Parameters: id=" + id + ", as2_from=" + as2_from +
                                 ", as2_to=" + as2_to + ", subject=" + subject +
                                 ", recipient_address=" + recipient_address);

        try {
            // check if partnership id already exists
            PartnershipDAO partnershipDAO = (PartnershipDAO) AS2Processor.core.dao.createDAO(PartnershipDAO.class);
            PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
            partnershipDVO.setPartnershipId(id);
            boolean editMode = false;
            if (partnershipDAO.retrieve(partnershipDVO)) {
                String message = "Partnership [" + id + "] already exists. Edit the existing partnership.";
                ApiPlugin.core.log.info(message);
                editMode = true;
            }

            if (!editMode) {
                if (partnershipDAO.findPartnershipsByPartyID(as2_from, as2_to).size() > 0) {
                    String errorMessage = "Partnership with same from/to parameters already exists";
                    ApiPlugin.core.log.error(errorMessage);
                    return listener.createError(ErrorCode.ERROR_RECORD_ALREADY_EXIST, errorMessage);
                }

                // create a brand new object for setting
                partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
                partnershipDVO.setPartnershipId(id);
            }

            partnershipDVO.setAs2From(as2_from);
            partnershipDVO.setAs2To(as2_to);
            partnershipDVO.setSubject(subject);
            partnershipDVO.setRecipientAddress(recipient_address);
            partnershipDVO.setIsDisabled(disabled);
            partnershipDVO.setIsSyncReply(syncReply);
            partnershipDVO.setIsHostnameVerified(isHostnameVerified);
            partnershipDVO.setReceiptAddress(receiptAddress);
            partnershipDVO.setIsReceiptRequired(isReceiptRequired);
            partnershipDVO.setIsOutboundSignRequired(isOutboundSignRequired);
            partnershipDVO.setIsOutboundEncryptRequired(isOutboundEncryptRequired);
            partnershipDVO.setIsOutboundCompressRequired(isOutboundCompressRequired);
            partnershipDVO.setIsReceiptSignRequired(isReceiptSignRequired);
            partnershipDVO.setIsInboundSignRequired(isInboundSignRequired);
            partnershipDVO.setIsInboundEncryptRequired(isInboundEncryptRequired);
            partnershipDVO.setRetries(retries);
            partnershipDVO.setRetryInterval(retryInterval);
            partnershipDVO.setSignAlgorithm(signAlgorithm);
            partnershipDVO.setEncryptAlgorithm(encryptAlgorithm);
            partnershipDVO.setMicAlgorithm(micAlgorithm);
            if (!encryptCert.equals("")) {
                partnershipDVO.setEncryptCert(Base64.decodeBase64(encryptCert));
            }
            if (!verifyCert.equals("")) {
                partnershipDVO.setVerifyCert(Base64.decodeBase64(verifyCert));
            }

            if (!editMode) {
                partnershipDAO.create(partnershipDVO);
            }
            else {
                partnershipDAO.persist(partnershipDVO);
            }

            Map<String, Object> returnObj = new HashMap<String, Object>();
            returnObj.put("id", id);
            return returnObj;
        } catch (DAOException e) {
            String errorMessage = "Error saving partnership";
            ApiPlugin.core.log.error(errorMessage, e);
            return listener.createError(ErrorCode.ERROR_WRITING_DATABASE, errorMessage);
        } catch (Exception e) {
            String errorMessage = "Unknown exception: " + e.getMessage();
            ApiPlugin.core.log.error(errorMessage, e);
            return listener.createError(ErrorCode.ERROR_UNKNOWN, errorMessage);
        }
    }

    public Map<String, Object> removePartnership(String id) {
        if ("".equals(id)) {
            String errorMessage = "Missing required field: id";
            ApiPlugin.core.log.error(errorMessage);
            return listener.createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
        }

        boolean success = false;
        try {
            PartnershipDAO partnershipDAO = (PartnershipDAO) AS2Processor.core.dao.createDAO(PartnershipDAO.class);
            PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
            partnershipDVO.setPartnershipId(id);
            if (!partnershipDAO.retrieve(partnershipDVO)) {
                String errorMessage = "Partnership [" + id + "] not found";
                ApiPlugin.core.log.error(errorMessage);
                return listener.createError(ErrorCode.ERROR_READING_DATABASE, errorMessage);
            }

            success = partnershipDAO.remove(partnershipDVO);
        } catch (DAOException e) {
            String errorMessage = "Error deleting partnership";
            ApiPlugin.core.log.error(errorMessage, e);
            return listener.createError(ErrorCode.ERROR_WRITING_DATABASE, errorMessage);
        }

        return listener.createActionResult(id, success);
    }
}
