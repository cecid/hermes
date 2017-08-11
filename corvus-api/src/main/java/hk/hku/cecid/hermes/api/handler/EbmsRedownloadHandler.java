package hk.hku.cecid.hermes.api.handler;

import java.util.HashMap;
import java.util.Map;
import hk.hku.cecid.ebms.spa.util.EbmsMessageStatusReverser;
import hk.hku.cecid.hermes.api.ErrorCode;
import hk.hku.cecid.hermes.api.listener.HermesAbstractApiListener;
import hk.hku.cecid.hermes.api.spa.ApiPlugin;


public class EbmsRedownloadHandler extends MessageHandler implements RedownloadHandler {

    public EbmsRedownloadHandler(HermesAbstractApiListener listener) {
        super(listener);
    }

    public Map<String, Object> redownload(String messageId) {
        try {
            EbmsMessageStatusReverser msgReverser = new EbmsMessageStatusReverser();
            msgReverser.updateToDownload(messageId);
            HashMap<String, Object> result = new HashMap<String, Object>();
            result.put("message_id", messageId);
            return result;
        } catch (Exception e) {
            String errorMessage = "Cannot set message to redownload: " + messageId;
            ApiPlugin.core.log.error(errorMessage);
            return listener.createError(ErrorCode.ERROR_WRITING_DATABASE, errorMessage);
        }
    }
}
