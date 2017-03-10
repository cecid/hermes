package hk.hku.cecid.hermes.api.handler;

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

        ApiPlugin.core.log.debug("Parameters: id=" + id + ", as2_from=" + as2_from +
                                 ", as2_to=" + as2_to + ", subject=" + subject +
                                 ", recipient_address=" + recipient_address);

        try {
            // check if partnership id already exists
            PartnershipDAO partnershipDAO = (PartnershipDAO) AS2Processor.core.dao.createDAO(PartnershipDAO.class);
            PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
            partnershipDVO.setPartnershipId(id);
            if (partnershipDAO.retrieve(partnershipDVO)) {
                String errorMessage = "Partnership [" + id + "] already exists";
                ApiPlugin.core.log.error(errorMessage);
                return listener.createError(ErrorCode.ERROR_RECORD_ALREADY_EXIST, errorMessage);
            }

            if (partnershipDAO.findPartnershipsByPartyID(as2_from, as2_to).size() > 0) {
                String errorMessage = "Partnership with same from/to parameters already exists";
                ApiPlugin.core.log.error(errorMessage);
                return listener.createError(ErrorCode.ERROR_RECORD_ALREADY_EXIST, errorMessage);
            }

            partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
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
