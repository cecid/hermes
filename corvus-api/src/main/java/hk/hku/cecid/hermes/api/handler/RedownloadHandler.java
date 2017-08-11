package hk.hku.cecid.hermes.api.handler;

import java.util.Map;

public interface RedownloadHandler {
    public Map<String, Object> redownload(String messageId);
}
