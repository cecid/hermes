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
import hk.hku.cecid.hermes.api.handler.As2SendMessageHandler;
import hk.hku.cecid.hermes.api.handler.EbmsSendMessageHandler;
import hk.hku.cecid.hermes.api.handler.SendMessageHandler;
import hk.hku.cecid.hermes.api.spa.ApiPlugin;

/**
 * HermesMessageSendApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesMessageSendApiListener extends HermesProtocolApiListener {

    protected Map<String, SendMessageHandler> handlers;

    public HermesMessageSendApiListener() {
        handlers = new HashMap<String, SendMessageHandler>();
        handlers.put(Constants.EBMS_PROTOCOL, new EbmsSendMessageHandler(this));
        handlers.put(Constants.AS2_PROTOCOL, new As2SendMessageHandler(this));
    }

    protected Map<String, Object> processGetRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = parseFromPathInfo(httpRequest.getPathInfo(), 2).get(1).toLowerCase();
        ApiPlugin.core.log.info("Get message sending status API invoked, protocol = " + protocol);

        String messageId = httpRequest.getParameter("id");
        if (messageId == null) {
            String errorMessage = "Missing required field: id";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
        }

        SendMessageHandler handler = this.handlers.get(protocol);
        if (handler != null) {
            return handler.getMessageStatus(messageId);
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
        ApiPlugin.core.log.info("Send message API invoked, protocol = " + protocol);

        Map<String, Object> inputDict = null;
        try {
            inputDict = getDictionaryFromRequest(httpRequest);
        } catch (IOException e) {
            String errorMessage = "Exception while reading input stream";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_READING_REQUEST, errorMessage);
        } catch (JsonParseException e) {
            String errorMessage = "Exception while parsing input stream";
            ApiPlugin.core.log.error(errorMessage, e);
            return createError(ErrorCode.ERROR_PARSING_REQUEST, errorMessage);
        }

        SendMessageHandler handler = this.handlers.get(protocol);
        if (handler != null) {
            return handler.sendMessage(inputDict, request);
        }
        else {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }
    }
}
