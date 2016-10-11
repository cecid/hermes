/* 
 * Copyright(c) 2016 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.hermes.api.listener;

import java.io.OutputStreamWriter;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.piazza.commons.servlet.http.HttpRequestAdaptor;


/**
 * HermesApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesApiListener extends HttpRequestAdaptor {

    private JsonBuilderFactory jsonFactory;

    public HermesApiListener() {
        jsonFactory = Json.createBuilderFactory(null);
    }

    /**
     * processRequest
     * @param request
     * @param response
     * @return String
     * @throws RequestListenerException
     * @see hk.hku.cecid.piazza.commons.servlet.http.HttpRequestListener#processRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public String processRequest(HttpServletRequest request, HttpServletResponse response) throws RequestListenerException {
        if (request.getMethod().equalsIgnoreCase("GET")) {
            try {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/vnd.api+json");
                OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());

                JsonObjectBuilder jsonBuilder = jsonFactory.createObjectBuilder();
                jsonBuilder.add("message", "Welcome to Hermes API");
                jsonBuilder.add("status", "healthy");
                osw.write(jsonBuilder.build().toString());
                osw.close();
                return null;
            }
            catch (Exception e) {
                throw new RequestListenerException("Error in processing API request", e);
            }
        }
        else {
            throw new RequestListenerException("Request method not supported");            
        }
    }
}
