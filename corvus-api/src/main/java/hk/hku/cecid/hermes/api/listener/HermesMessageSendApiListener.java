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
 * HermesMessageSendApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesMessageSendApiListener extends HermesProtocolApiListener {

    protected void processApi(HttpServletRequest request, HttpServletResponse response, JsonObjectBuilder jsonBuilder) throws RequestListenerException {
        String protocol = this.getProtocolFromPathInfo(request.getPathInfo());

        if (protocol.equalsIgnoreCase("ebms")) {
            if (request.getMethod().equalsIgnoreCase("GET")) {
            }
            else if (request.getMethod().equalsIgnoreCase("POST")) {
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
