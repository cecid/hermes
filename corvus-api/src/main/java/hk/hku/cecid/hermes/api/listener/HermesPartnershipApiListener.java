/* 
 * Copyright(c) 2016 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.hermes.api.listener;

import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;


/**
 * HermesApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesPartnershipApiListener extends HermesAbstractApiListener {

    protected void processApi(HttpServletRequest request, HttpServletResponse response, JsonObjectBuilder jsonBuilder) throws RequestListenerException {
        if (request.getMethod().equalsIgnoreCase("GET")) {
            jsonBuilder.add("message", "Welcome to Hermes Partnership API");
            jsonBuilder.add("status", "healthy");
        }
        else {
            throw new RequestListenerException("Request method not supported");
        }
    }
}
