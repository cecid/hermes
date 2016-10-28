/* 
 * Copyright(c) 2016 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.hermes.api.listener;

import java.util.Base64;
import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.PartnershipDAO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;


/**
 * HermesPartnershipApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesPartnershipApiListener extends HermesProtocolApiListener {

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
                        this.addString(jsonItem, "cpa_id", partnershipDVO.getPartnershipId());
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
                // TODO: implement set partnership API here...
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
