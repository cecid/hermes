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
import hk.hku.cecid.hermes.api.ErrorCode;
import hk.hku.cecid.hermes.api.spa.ApiPlugin;


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

    public void fillError(Map<String, Object> error, int code, String message) {
        error.put("code", new Integer(code));
        error.put("message", message);
    }

    public Map<String, Object> createError(int code, String message) {
        Map<String, Object> error = new HashMap<String, Object>();
        fillError(error, code, message);
        return error;
    }

    public Map<String, Object> createActionResult(String id, boolean success) {
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

    protected void logError(String message, Throwable e) {
        if (ApiPlugin.core != null) {
            if (e == null) {
                ApiPlugin.core.log.error(message);
            } else {
                ApiPlugin.core.log.error(message, e);
            }
        }
    }

    protected void logError(String message) {
        logError(message, null);
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

    public String getStringFromInput(Map<String, Object> inputDict, String key, Map<String, Object> error) {
        String value = null;
        try {
            value = (String) inputDict.get(key);
            if (value == null) {
                String errorMessage = "Missing required input field: " + key;
                logError(errorMessage);
                fillError(error, ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: " + key;
            logError(errorMessage, e);
            fillError(error, ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }
        return value;
    }

    public Long getLongFromInput(Map<String, Object> inputDict, String key, Map<String, Object> error) {
        Long value = null;
        try {
            Object valueObj = inputDict.get(key);
            if (valueObj == null) {
                String errorMessage = "Missing required input field: " + key;
                logError(errorMessage);
                fillError(error, ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            } else {
                value = (Long) valueObj;
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: " + key;
            logError(errorMessage, e);
            fillError(error, ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }
        return value;
    }

    public String getOptionalStringFromInput(Map<String, Object> inputDict, String key, String defaultValue, Map<String, Object> error) {
        String value = null;
        try {
            value = (String) inputDict.get(key);
            if (value == null) {
                value = defaultValue;
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: " + key;
            logError(errorMessage, e);
            fillError(error, ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }
        return value;
    }

    public Long getOptionalLongFromInput(Map<String, Object> inputDict, String key, long defaultValue, Map<String, Object> error) {
        Long value = null;
        try {
            value = (Long) inputDict.get(key);
            if (value == null) {
                value = defaultValue;
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: " + key;
            logError(errorMessage, e);
            fillError(error, ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }
        return value;
    }

    public Boolean getOptionalBooleanFromInput(Map<String, Object> inputDict, String key, boolean defaultValue, Map<String, Object> error) {
        Boolean value = null;
        try {
            value = (Boolean) inputDict.get(key);
            if (value == null) {
                value = defaultValue;
            }
        } catch (Exception e) {
            String errorMessage = "Error parsing parameter: " + key;
            logError(errorMessage, e);
            fillError(error, ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }
        return value;
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
