package hk.hku.cecid.hermes.api.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.PartnershipDAO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
import hk.hku.cecid.hermes.api.listener.HermesAbstractApiListener;
import hk.hku.cecid.hermes.api.Constants;
import hk.hku.cecid.hermes.api.ErrorCode;
import hk.hku.cecid.hermes.api.spa.ApiPlugin;
import hk.hku.cecid.piazza.commons.dao.DAOException;

import org.apache.commons.codec.binary.Base64;


public class EbmsPartnershipHandler extends MessageHandler implements PartnershipHandler {

    public EbmsPartnershipHandler(HermesAbstractApiListener listener) {
        super(listener);
    }

    public Map<String, Object> getPartnerships() {
        try {
            PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao.createDAO(PartnershipDAO.class);
            ArrayList<Object> partnershipList = new ArrayList<Object>();
            for (Iterator i = partnershipDAO.findAllPartnerships().iterator(); i.hasNext(); ) {
                PartnershipDVO partnershipDVO = (PartnershipDVO) i.next();

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
                    cert = new String(Base64.encodeBase64(partnershipDVO.getSignCert()));
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
        String cpa_id = listener.getStringFromInput(inputDict, "cpa_id", errorObject);
        if (cpa_id == null) {
            return errorObject;
        }
        String service = listener.getStringFromInput(inputDict, "service", errorObject);
        if (service == null) {
            return errorObject;
        }
        String action = listener.getStringFromInput(inputDict, "action", errorObject);
        if (action == null) {
            return errorObject;
        }
        String transport_endpoint = listener.getStringFromInput(inputDict, "transport_endpoint", errorObject);
        if (transport_endpoint == null) {
            return errorObject;
        }

        ApiPlugin.core.log.debug("Parameters: id=" + id + ", cpa_id=" + cpa_id +
                                 ", service=" + service + ", action=" + action +
                                 ", transport_endpoint=" + transport_endpoint);

        try {
            // check if partnership id already exists
            PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao.createDAO(PartnershipDAO.class);
            PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
            partnershipDVO.setPartnershipId(id);
            if (partnershipDAO.retrieve(partnershipDVO)) {
                String errorMessage = "Partnership [" + id + "] already exists";
                ApiPlugin.core.log.error(errorMessage);
                return listener.createError(ErrorCode.ERROR_RECORD_ALREADY_EXIST, errorMessage);
            }

            partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
            partnershipDVO.setCpaId(cpa_id);
            partnershipDVO.setService(service);
            partnershipDVO.setAction(action);

            if (partnershipDAO.findPartnershipByCPA(partnershipDVO)) {
                String errorMessage = "Partnership with same CPA parameters already exists";
                ApiPlugin.core.log.error(errorMessage);
                return listener.createError(ErrorCode.ERROR_RECORD_ALREADY_EXIST, errorMessage);
            }

            partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
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
            PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao.createDAO(PartnershipDAO.class);
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
