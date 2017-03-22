package hk.hku.cecid.hermes.api.handler;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;


public interface ReceiveMessageHandler {
    public Map<String, Object> getReceivedMessageList(String partnershipId, boolean includeRead);
    public Map<String, Object> getReceivedMessage(String messageId, HttpServletRequest request);
}
