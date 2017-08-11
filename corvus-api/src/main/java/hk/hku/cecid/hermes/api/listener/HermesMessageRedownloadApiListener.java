package hk.hku.cecid.hermes.api.listener;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import hk.hku.cecid.piazza.commons.json.JsonParseException;
import hk.hku.cecid.piazza.commons.rest.RestRequest;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.hermes.api.Constants;
import hk.hku.cecid.hermes.api.ErrorCode;
import hk.hku.cecid.hermes.api.handler.EbmsRedownloadHandler;
import hk.hku.cecid.hermes.api.handler.RedownloadHandler;
import hk.hku.cecid.hermes.api.spa.ApiPlugin;

/**
 * HermesMessageRedownloadApiListener
 *
 * @author Patrick Yee
 *
 */
public class HermesMessageRedownloadApiListener extends HermesProtocolApiListener {

    protected Map<String, RedownloadHandler> handlers;

    public HermesMessageRedownloadApiListener() {
        handlers = new HashMap<String, RedownloadHandler>();
        handlers.put(Constants.EBMS_PROTOCOL, new EbmsRedownloadHandler(this));
    }

    protected Map<String, Object> processPostRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = parseFromPathInfo(httpRequest.getPathInfo(), 2).get(1);
        ApiPlugin.core.log.info("Redownload message API invoked, protocol = " + protocol);

        String messageId = null;
        try {
            Map<String, Object> inputDict = getDictionaryFromRequest(httpRequest);
            if (inputDict.containsKey("message_id")) {
                messageId = (String) inputDict.get("message_id");
            }
            else {
                String errorMessage = "Missing required field: message_id";
                ApiPlugin.core.log.error(errorMessage);
                return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
            }
        } catch (IOException e) {
            String errorMessage = "Exception while reading input stream";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_READING_REQUEST, errorMessage);
        } catch (JsonParseException e) {
            String errorMessage = "Exception while parsing input stream";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }

        RedownloadHandler handler = this.handlers.get(protocol);
        if (handler != null) {
            return handler.redownload(messageId);
        }
        else {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }
    }
}
