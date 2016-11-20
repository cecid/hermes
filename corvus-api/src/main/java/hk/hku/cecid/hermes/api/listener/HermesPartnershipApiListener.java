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


/**
 * HermesPartnershipApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesPartnershipApiListener extends HermesProtocolApiListener {

    protected Map<String, Object> processGetRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = getProtocolFromPathInfo(httpRequest.getPathInfo());

        if (!protocol.equalsIgnoreCase(Constants.EBMS_PROTOCOL)) {
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, "Protocol unknown");
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
            return returnObj;

        } catch (DAOException e) {
            return createError(ErrorCode.ERROR_READING_DATABASE, "DAO exception");
        } catch (Exception e) {
            return createError(ErrorCode.ERROR_UNKNOWN, "Unknown exception: " + e.getMessage());
        }
    }

    protected Map<String, Object> processPostRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = this.getProtocolFromPathInfo(httpRequest.getPathInfo());

        if (!protocol.equalsIgnoreCase(Constants.EBMS_PROTOCOL)) {
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, "Protocol unknown");
        }

        Map<String, Object> inputDict = null;
        try {
            inputDict = getDictionaryFromRequest(httpRequest);
        } catch (IOException e) {
            return createError(ErrorCode.ERROR_READING_REQUEST, "Exception while reading input stream");
        } catch (JsonParseException e) {
            return createError(ErrorCode.ERROR_PARSING_REQUEST, "Exception while parsing input stream");
        }

        String id = null;
        try {
            id = (String) inputDict.get("id");
            if (id == null) {
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, "Missing required partinership field: id");
            }
        } catch (Exception e) {
            return createError(ErrorCode.ERROR_PARSING_REQUEST, "Error parsing parameter: id");
        }
        String cpa_id = null;
        try {
            cpa_id = (String) inputDict.get("cpa_id");
            if (cpa_id == null) {
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, "Missing required partinership field: cpa_id");
            }
        } catch (Exception e) {
            return createError(ErrorCode.ERROR_PARSING_REQUEST, "Error parsing parameter: cpa_id");
        }
        String service = null;
        try {
            service = (String) inputDict.get("service");
            if (service == null) {
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, "Missing required partinership field: service");
            }
        } catch (Exception e) {
            return createError(ErrorCode.ERROR_PARSING_REQUEST, "Error parsing parameter: service");
        }
        String action = null;
        try {
            action = (String) inputDict.get("action");
            if (action == null) {
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, "Missing required partinership field: action");
            }
        } catch (Exception e) {
            return createError(ErrorCode.ERROR_PARSING_REQUEST, "Error parsing parameter: action");
        }
        String transport_endpoint = null;
        try {
            transport_endpoint = (String) inputDict.get("transport_endpoint");
            if (transport_endpoint == null) {
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, "Missing required partinership field: transport_endpoint");
            }
        } catch (Exception e) {
            return createError(ErrorCode.ERROR_PARSING_REQUEST, "Error parsing parameter: transport_endpoint");
        }

        try {
            // check if partnership id already exists
            PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao.createDAO(PartnershipDAO.class);
            PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
            partnershipDVO.setPartnershipId(id);
            if (partnershipDAO.retrieve(partnershipDVO)) {
                return createError(ErrorCode.ERROR_RECORD_ALREADY_EXIST, "Partnership [" + id + "] already exists");
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
            return createError(ErrorCode.ERROR_WRITING_DATABASE, "Error saving partinership");
        } catch (Exception e) {
            return createError(ErrorCode.ERROR_UNKNOWN, "Unknown exception: " + e.getMessage());
        }
    }
}
