package hk.hku.cecid.hermes.api.handler;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;


public interface MessageHistoryHandler {
    public Map<String, Object> getMessageHistory(HttpServletRequest request);
    public Map<String, Object> resetMessage(String messageId, String action);
}
