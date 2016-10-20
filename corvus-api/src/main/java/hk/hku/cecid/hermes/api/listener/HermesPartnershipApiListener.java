/* 
 * Copyright(c) 2016 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.hermes.api.listener;

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
                        jsonItem.add("cpa_id", partnershipDVO.getPartnershipId());
                        jsonItem.add("service", partnershipDVO.getService());
                        jsonItem.add("action", partnershipDVO.getAction());
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
