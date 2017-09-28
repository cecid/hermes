package hk.hku.cecid.hermes.api.listener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import hk.hku.cecid.hermes.api.Constants;
import hk.hku.cecid.hermes.api.ErrorCode;
import hk.hku.cecid.hermes.api.handler.As2MessageHistoryHandler;
import hk.hku.cecid.hermes.api.handler.EbmsMessageHistoryHandler;
import hk.hku.cecid.hermes.api.handler.MessageHistoryHandler;
import hk.hku.cecid.hermes.api.spa.ApiPlugin;
import hk.hku.cecid.piazza.commons.json.JsonParseException;
import hk.hku.cecid.piazza.commons.rest.RestRequest;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;


/**
 * HermesMessageHistoryApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesMessageHistoryApiListener extends HermesProtocolApiListener {

    protected Map<String, MessageHistoryHandler> handlers;

    public HermesMessageHistoryApiListener() {
        handlers = new HashMap<String, MessageHistoryHandler>();
        handlers.put(Constants.EBMS_PROTOCOL, new EbmsMessageHistoryHandler(this));
        handlers.put(Constants.AS2_PROTOCOL, new As2MessageHistoryHandler(this));
    }

    protected Map<String, Object> processGetRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = parseFromPathInfo(httpRequest.getPathInfo(), 2).get(1);
        ApiPlugin.core.log.info("Get message history API invoked, protocol = " + protocol);

        MessageHistoryHandler handler = this.handlers.get(protocol);
        if (handler != null) {
            return handler.getMessageHistory(httpRequest);
        }
        else {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }
    }

    protected Map<String, Object> processPostRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = parseFromPathInfo(httpRequest.getPathInfo(), 2).get(1);
        ApiPlugin.core.log.info("Alter message history API invoked, protocol = " + protocol);

        Map<String, Object> inputDict = null;
        try {
            inputDict = getDictionaryFromRequest(httpRequest);
        } catch (IOException e) {
            String errorMessage = "Exception while reading input stream";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_READING_REQUEST, errorMessage);
        } catch (JsonParseException e) {
            String errorMessage = "Exception while parsing input stream";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }

        Map<String, Object> errorObject = new HashMap<String, Object>();
        String messageId = getStringFromInput(inputDict, "message_id", errorObject);
        if (messageId == null) {
            return errorObject;
        }
        String action = getStringFromInput(inputDict, "action", errorObject);
        if (action == null) {
            return errorObject;
        }
        else if (!action.equalsIgnoreCase(Constants.RESET_ACTION)) {
            String errorMessage = "Unknown action: " + action;
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_UNKNOWN_ACTION, errorMessage);
        }

        ApiPlugin.core.log.debug("Parameters: message_id=" + messageId + ", action=" + action);

        MessageHistoryHandler handler = this.handlers.get(protocol);
        if (handler != null) {
            return handler.resetMessage(messageId, action);
        }
        else {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }
    }
}
