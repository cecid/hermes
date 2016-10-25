/* 
 * Copyright(c) 2016 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.hermes.api.listener;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.PartnershipDAO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;


/**
 * HermesApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesPartnershipApiListener extends HermesAbstractApiListener {

    private String getProtocolFromPathInfo(String pathInfo) {
        int startIndex = pathInfo.indexOf("/", 1) + 1;
        int endIndex = pathInfo.indexOf("/", startIndex);
        if (endIndex == -1) {
            endIndex = pathInfo.length();
        }
        return pathInfo.substring(startIndex, endIndex);
    }

    protected void processApi(HttpServletRequest request, HttpServletResponse response, JsonObjectBuilder jsonBuilder) throws RequestListenerException {
        String protocol = this.getProtocolFromPathInfo(request.getPathInfo());

        if (protocol.equalsIgnoreCase("ebms")) {
            if (request.getMethod().equalsIgnoreCase("GET")) {
                try {
                    PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao.createDAO(PartnershipDAO.class);
                    JsonArrayBuilder arrayBuilder = this.createJsonArray();
                    for (Iterator i = partnershipDAO.findAllPartnerships().iterator(); i.hasNext(); ) {
                        PartnershipDVO partnershipDVO = (PartnershipDVO) i.next();
                        JsonObjectBuilder jsonItem = this.createJsonObject();
                        this.addString(jsonItem, "id", partnershipDVO.getPartnershipId());
                        this.addString(jsonItem, "cpa_id", partnershipDVO.getCpaId());
                        this.addString(jsonItem, "service", partnershipDVO.getService());
                        this.addString(jsonItem, "action", partnershipDVO.getAction());
                        jsonItem.add("disabled", partnershipDVO.getDisabled().equals("true"));
                        this.addString(jsonItem, "transport_endpoint", partnershipDVO.getTransportEndpoint());
                        this.addString(jsonItem, "ack_requested", partnershipDVO.getAckRequested());
                        this.addString(jsonItem, "signed_ack_requested", partnershipDVO.getAckSignRequested());
                        this.addString(jsonItem, "duplicate_elimination", partnershipDVO.getDupElimination());
                        this.addString(jsonItem, "message_order", partnershipDVO.getMessageOrder());
                        jsonItem.add("retries", partnershipDVO.getRetries());
                        jsonItem.add("retry_interval", partnershipDVO.getRetryInterval());
                        jsonItem.add("sign_requested", partnershipDVO.getSignRequested().equals("true"));
                        String cert = null;
                        if (partnershipDVO.getSignCert() != null) {
                            Base64.Encoder encoder = Base64.getEncoder();
                            cert = new String(encoder.encode(partnershipDVO.getSignCert()));
                        }
                        this.addString(jsonItem, "sign_certicate", cert);

                        arrayBuilder.add(jsonItem);
                    }
                    jsonBuilder.add("partnerships", arrayBuilder);
                }
                catch (DAOException e) {
                    this.fillError(jsonBuilder, -1, "DAO exception");
                }
            }
            else if (request.getMethod().equalsIgnoreCase("POST")) {

                JsonObject jsonObject = null;
                String id = null;
                String cpa_id = null;
                String service = null;
                String action = null;
                String transport_endpoint = null;

                try {
                    JsonReaderFactory factory = Json.createReaderFactory(null);
                    JsonReader jsonReader = factory.createReader(request.getInputStream());
                    jsonObject = jsonReader.readObject();
                    jsonReader.close();

                    id = jsonObject.getString("id");
                    cpa_id = jsonObject.getString("cpa_id");
                    service = jsonObject.getString("service");
                    action = jsonObject.getString("action");
                    transport_endpoint = jsonObject.getString("transport_endpoint");
                }
                catch (IOException e) {
                    this.fillError(jsonBuilder, -1, "Error reading request data");
                    return;
                }
                catch (Exception e) {
                    this.fillError(jsonBuilder, -1, "Error parsing request data");
                    return;
                }

                if (id == null) {
                    this.fillError(jsonBuilder, -1, "Missing required partinership field: id");
                    return;
                }
                if (cpa_id == null) {
                    this.fillError(jsonBuilder, -1, "Missing required partinership field: cpa_id");
                    return;
                }
                if (service == null) {
                    this.fillError(jsonBuilder, -1, "Missing required partinership field: service");
                    return;
                }
                if (action == null) {
                    this.fillError(jsonBuilder, -1, "Missing required partinership field: action");
                    return;
                }
                if (transport_endpoint == null) {
                    this.fillError(jsonBuilder, -1, "Missing required partinership field: transport_endpoint");
                    return;
                }

                try {
                    PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao.createDAO(PartnershipDAO.class);
                    PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
                    partnershipDVO.setPartnershipId(id);
                    partnershipDVO.setCpaId(cpa_id);
                    partnershipDVO.setService(service);
                    partnershipDVO.setAction(action);
                    partnershipDVO.setTransportEndpoint(transport_endpoint);

                    partnershipDAO.create(partnershipDVO);
                    jsonBuilder.add("id", id);
                }
                catch (DAOException e) {
                    this.fillError(jsonBuilder, -1, "Error saving partinership");
                    return;
                }
            }
            else {
                throw new RequestListenerException("Request method not supported");
            }
        }
        else {
            this.fillError(jsonBuilder, -1, "Protocol unknown");
        }
    }
}
