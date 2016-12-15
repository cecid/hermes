/* 
 * Copyright(c) 2016 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.hermes.api.listener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hk.hku.cecid.piazza.commons.json.JsonParseException;
import hk.hku.cecid.piazza.commons.json.JsonUtil;
import hk.hku.cecid.piazza.commons.rest.RestRequest;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.piazza.commons.servlet.http.HttpRequestAdaptor;
import hk.hku.cecid.hermes.api.Constants;


/**
 * HermesAbstractApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesAbstractApiListener extends HttpRequestAdaptor {

    public HermesAbstractApiListener() {
    }

    protected void fillDate(Map<String, Object> dictionary) {
        dictionary.put("server_time", new Long((new Date()).getTime() / 1000));
    }

    protected Map<String, Object> createError(int code, String message) {
        Map<String, Object> dictionary = new HashMap<String, Object>();
        dictionary.put("code", new Integer(code));
        dictionary.put("message", message);
        return dictionary;
    }

    protected Map<String, Object> createActionResult(String id, boolean success) {
        Map<String, Object> dictionary = new HashMap<String, Object>();
        dictionary.put("id", id);
        dictionary.put("success", success);
        return dictionary;
    }

    protected Map<String, Object> getDictionaryFromRequest(HttpServletRequest request) throws IOException, JsonParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        InputStream in = request.getInputStream();
        int readSize = in.read(buf);
        while (readSize > 0) {
            baos.write(buf, 0, readSize);
            readSize = in.read(buf);
        }
        return JsonUtil.toDictionary(baos.toString());
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

        try {
            RestRequest restRequest = new RestRequest(request);
            Map<String, Object> dictionaryResponse = processApi(restRequest);
            String jsonResponse = JsonUtil.fromDictionary(dictionaryResponse);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(Constants.CONTENT_TYPE);

            OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());
            osw.write(jsonResponse);
            osw.close();
        }
        catch (Exception e) {
            throw new RequestListenerException(e.getMessage(), e);
        }

        return null;
    }

    protected Map<String, Object> processApi(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        try {
            if (httpRequest.getMethod().equalsIgnoreCase(Constants.METHOD_GET)) {
                return processGetRequest(request);
            }
            else if (httpRequest.getMethod().equalsIgnoreCase(Constants.METHOD_POST)) {
                return processPostRequest(request);
            }
            else if (httpRequest.getMethod().equalsIgnoreCase(Constants.METHOD_DELETE)) {
                return processDeleteRequest(request);
            }
            else {
                throw new RequestListenerException("Request method not supported");
            }

        } catch (UnsupportedOperationException e) {
            throw new RequestListenerException("Request method not supported");
        }
    }

    protected Map<String, Object> processGetRequest(RestRequest request) throws RequestListenerException {
        throw new UnsupportedOperationException();
    }

    protected Map<String, Object> processPostRequest(RestRequest request) throws RequestListenerException {
        throw new UnsupportedOperationException();
    }

    protected Map<String, Object> processDeleteRequest(RestRequest request) throws RequestListenerException {
        throw new UnsupportedOperationException();
    }
}
