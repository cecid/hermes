/* 
 * Copyright(c) 2016 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.hermes.api.listener;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.PartnershipDAO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
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

        if (!protocol.equalsIgnoreCase(Constants.EBMS_PROTOCOL)) {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }

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

    protected Map<String, Object> processPostRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = parseFromPathInfo(httpRequest.getPathInfo()).get(1);
        ApiPlugin.core.log.info("Add partnership API invoked, protocol = " + protocol);

        if (!protocol.equalsIgnoreCase(Constants.EBMS_PROTOCOL)) {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }

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
            PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao.createDAO(PartnershipDAO.class);
            PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
            partnershipDVO.setPartnershipId(id);
            if (partnershipDAO.retrieve(partnershipDVO)) {
                String errorMessage = "Partnership [" + id + "] already exists";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_RECORD_ALREADY_EXIST, errorMessage);
            }

            partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
            partnershipDVO.setCpaId(cpa_id);
            partnershipDVO.setService(service);
            partnershipDVO.setAction(action);

            if (partnershipDAO.findPartnershipByCPA(partnershipDVO)) {
                String errorMessage = "Partnership with same CPA parameters already exists";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_RECORD_ALREADY_EXIST, errorMessage);
            }

            partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
            partnershipDVO.setDisabled(Constants.DEFAULT_PARTNERSHIP_DISABLED);
            partnershipDVO.setPartnershipId(id);
            partnershipDVO.setCpaId(cpa_id);
            partnershipDVO.setService(service);
            partnershipDVO.setAction(action);
            partnershipDVO.setTransportEndpoint(transport_endpoint);
            partnershipDVO.setRetryInterval(Constants.DEFAULT_PARTNERSHIP_RETRY_INTERVAL);
            partnershipDVO.setRetries(Constants.DEFAULT_PARTNERSHIP_RETRY_COUNT);
            partnershipDVO.setTransportProtocol(Constants.DEFAULT_PARTNERSHIP_TRANSPORT_PROTOCOL);
            partnershipDVO.setIsHostnameVerified(Constants.DEFAULT_PARTNERSHIP_HOSTNAME_VERIFY);
            partnershipDVO.setSyncReplyMode(Constants.DEFAULT_PARTNERSHIP_SYNC_REPLY_MODE);
            partnershipDVO.setAckRequested(Constants.DEFAULT_PARTNERSHIP_ACK_REQUESTED);
            partnershipDVO.setAckSignRequested(Constants.DEFAULT_PARTNERSHIP_ACK_SIGN_REQUESTED);
            partnershipDVO.setDupElimination(Constants.DEFAULT_PARTNERSHIP_DUPLICATE_ELIMINATION);
            partnershipDVO.setMessageOrder(Constants.DEFAULT_PARTNERSHIP_MESSAGE_ORDER);
            partnershipDVO.setSignRequested(Constants.DEFAULT_PARTNERSHIP_SIGN_REQUESTED);
            partnershipDVO.setEncryptRequested(Constants.DEFAULT_PARTNERSHIP_ENCRYPT_REQUESTED);

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

        if (!protocol.equalsIgnoreCase(Constants.EBMS_PROTOCOL)) {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }

        if ("".equals(id)) {
            String errorMessage = "Missing required partnership field: id";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
        }

        boolean success = false;
        try {
            PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao.createDAO(PartnershipDAO.class);
            PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
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
