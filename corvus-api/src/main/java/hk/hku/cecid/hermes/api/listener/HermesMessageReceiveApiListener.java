package hk.hku.cecid.hermes.api.listener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.AttachmentPart;

import hk.hku.cecid.piazza.commons.json.JsonParseException;
import hk.hku.cecid.piazza.commons.rest.RestRequest;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.hermes.api.Constants;
import hk.hku.cecid.hermes.api.ErrorCode;
import hk.hku.cecid.hermes.api.handler.As2ReceiveMessageHandler;
import hk.hku.cecid.hermes.api.handler.EbmsReceiveMessageHandler;
import hk.hku.cecid.hermes.api.handler.ReceiveMessageHandler;
import hk.hku.cecid.hermes.api.spa.ApiPlugin;


/**
 * HermesMessageReceiveApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesMessageReceiveApiListener extends HermesProtocolApiListener {

    protected Map<String, ReceiveMessageHandler> handlers;

    public HermesMessageReceiveApiListener() {
        handlers = new HashMap<String, ReceiveMessageHandler>();
        handlers.put(Constants.EBMS_PROTOCOL, new EbmsReceiveMessageHandler(this));
        handlers.put(Constants.AS2_PROTOCOL, new As2ReceiveMessageHandler(this));
    }

    protected Map<String, Object> processGetRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = parseFromPathInfo(httpRequest.getPathInfo(), 2).get(1);
        ApiPlugin.core.log.info("Get received message list API invoked, protocol = " + protocol);

        String partnershipId = httpRequest.getParameter("partnership_id");
        if (partnershipId == null) {
            String errorMessage = "Missing required field: partnership_id";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_MISSING_REQUIRED_PARAMETER, errorMessage);
        }

        String includeReadString = httpRequest.getParameter("include_read");
        boolean includeRead = false;
        if (includeReadString != null && includeReadString.equalsIgnoreCase("true")) {
            includeRead = true;
        }

        ReceiveMessageHandler handler = this.handlers.get(protocol);
        if (handler != null) {
            return handler.getReceivedMessageList(partnershipId, includeRead);
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
        ApiPlugin.core.log.info("Get received message API invoked, protocol = " + protocol);

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

        ReceiveMessageHandler handler = this.handlers.get(protocol);
        if (handler != null) {
            return handler.getReceivedMessage(messageId, httpRequest);
        }
        else {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }
    }
}
