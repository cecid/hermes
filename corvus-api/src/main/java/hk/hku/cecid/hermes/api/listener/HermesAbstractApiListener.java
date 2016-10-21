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
import javax.json.JsonArrayBuilder;
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

    protected JsonObjectBuilder createJsonObject() {
        return jsonFactory.createObjectBuilder();
    }

    protected JsonArrayBuilder createJsonArray() {
        return jsonFactory.createArrayBuilder();
    }

    protected void addDate(JsonObjectBuilder jsonBuilder) {
        jsonBuilder.add("server_time", (new Date()).getTime() / 1000);
    }

    protected void addString(JsonObjectBuilder jsonBuilder, String key, String value) {
        if (value != null) {
            jsonBuilder.add(key, value);
        }
        else {
            jsonBuilder.addNull(key);
        }
    }

    protected void fillError(JsonObjectBuilder jsonBuilder, int code, String message) {
        jsonBuilder.add("code", code);
        jsonBuilder.add("message", message);
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

        JsonObjectBuilder jsonBuilder = createJsonObject();

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
