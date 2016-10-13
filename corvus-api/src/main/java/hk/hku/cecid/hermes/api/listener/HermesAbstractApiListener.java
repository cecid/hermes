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
import java.io.OutputStreamWriter;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.piazza.commons.servlet.http.HttpRequestAdaptor;


/**
 * HermesAbstractApiListener
 * 
 * @author Patrick Yee
 *
 */
public abstract class HermesAbstractApiListener extends HttpRequestAdaptor {

    private JsonBuilderFactory jsonFactory;

    public HermesAbstractApiListener() {
        jsonFactory = Json.createBuilderFactory(null);
    }

    private JsonObjectBuilder createJson() {
        return jsonFactory.createObjectBuilder();
    }

    protected void addDate(JsonObjectBuilder jsonBuilder) {
        jsonBuilder.add("Date", (new Date()).getTime() / 1000);
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

        JsonObjectBuilder jsonBuilder = createJson();

        try {
            processApi(request, response, jsonBuilder);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/vnd.api+json");

            OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());
            osw.write(jsonBuilder.build().toString());
            osw.close();
        }
        catch (Exception e) {
            throw new RequestListenerException(e.getMessage());
        }

        return null;
    }

    protected abstract void processApi(HttpServletRequest request, HttpServletResponse response, JsonObjectBuilder jsonBuilder) throws RequestListenerException;
}
