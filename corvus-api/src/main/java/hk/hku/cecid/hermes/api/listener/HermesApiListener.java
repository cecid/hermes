package hk.hku.cecid.hermes.api.listener;

import java.util.HashMap;
import java.util.Map;

import hk.hku.cecid.piazza.commons.rest.RestRequest;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.hermes.api.Constants;
import hk.hku.cecid.hermes.api.spa.ApiPlugin;


/**
 * HermesApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesApiListener extends HermesAbstractApiListener {

    protected Map<String, Object> createStatusObject() {
        HashMap<String, Object> dict = new HashMap<String, Object>();
        dict.put("status", Constants.HEALTHY);
        fillDate(dict);
        return dict;
    }

    protected Map<String, Object> processGetRequest(RestRequest request) throws RequestListenerException {
        ApiPlugin.core.log.info("Status API invoked");
        return createStatusObject();
    }
}
