/* 
 * Copyright(c) 2016 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.hermes.api.listener;

import java.util.HashMap;
import java.util.Map;

import hk.hku.cecid.piazza.commons.rest.RestRequest;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.hermes.api.Constants;


/**
 * HermesApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesApiListener extends HermesAbstractApiListener {
    protected Map<String, Object> processGetRequest(RestRequest request) throws RequestListenerException {
        HashMap<String, Object> dict = new HashMap<String, Object>();
        dict.put("status", Constants.HEALTHY);
        fillDate(dict);
        return dict;
    }
}
