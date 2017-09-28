package hk.hku.cecid.hermes.api.listener;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import hk.hku.cecid.piazza.commons.json.JsonParseException;
import hk.hku.cecid.piazza.commons.rest.RestRequest;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.hermes.api.Constants;
import hk.hku.cecid.hermes.api.ErrorCode;
import hk.hku.cecid.hermes.api.handler.As2PartnershipHandler;
import hk.hku.cecid.hermes.api.handler.EbmsPartnershipHandler;
import hk.hku.cecid.hermes.api.handler.PartnershipHandler;
import hk.hku.cecid.hermes.api.spa.ApiPlugin;

/**
 * HermesPartnershipApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesPartnershipApiListener extends HermesProtocolApiListener {

    protected Map<String, PartnershipHandler> handlers;

    public HermesPartnershipApiListener() {
        handlers = new HashMap<String, PartnershipHandler>();
        handlers.put(Constants.EBMS_PROTOCOL, new EbmsPartnershipHandler(this));
        handlers.put(Constants.AS2_PROTOCOL, new As2PartnershipHandler(this));
    }

    protected Map<String, Object> processGetRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = parseFromPathInfo(httpRequest.getPathInfo()).get(1).toLowerCase();
        ApiPlugin.core.log.info("Get partnership API invoked, protocol = " + protocol);

        PartnershipHandler handler = this.handlers.get(protocol);
        if (handler != null) {
            return handler.getPartnerships();
        }
        else {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }
    }

    protected Map<String, Object> processPostRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = parseFromPathInfo(httpRequest.getPathInfo()).get(1).toLowerCase();
        ApiPlugin.core.log.info("Add partnership API invoked, protocol = " + protocol);

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

        PartnershipHandler handler = this.handlers.get(protocol);
        if (handler != null) {
            return handler.addPartnership(inputDict);
        }
        else {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }
    }

    protected Map<String, Object> processDeleteRequest(RestRequest request) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        List<String> pathInfo = parseFromPathInfo(httpRequest.getPathInfo());
        String protocol = pathInfo.get(1).toLowerCase();
        String id = pathInfo.get(2);
        ApiPlugin.core.log.info("Delete partnership API invoked, protocol = " + protocol + ", id = " + id);

        PartnershipHandler handler = this.handlers.get(protocol);
        if (handler != null) {
            return handler.removePartnership(id);
        }
        else {
            String errorMessage = "Protocol unknown";
            ApiPlugin.core.log.error(errorMessage);
            return createError(ErrorCode.ERROR_PROTOCOL_UNSUPPORTED, errorMessage);
        }
    }
}
