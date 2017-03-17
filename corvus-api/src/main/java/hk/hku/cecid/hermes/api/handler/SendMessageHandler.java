package hk.hku.cecid.hermes.api.handler;

import java.util.Map;

import hk.hku.cecid.piazza.commons.rest.RestRequest;

public interface SendMessageHandler {
    public Map<String, Object> getMessageStatus(String messageId);
    public Map<String, Object> sendMessage(Map<String, Object> inputDict, RestRequest sourceRequest);
}
