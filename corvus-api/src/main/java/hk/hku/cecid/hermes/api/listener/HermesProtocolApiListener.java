/* 
 * Copyright(c) 2016 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.hermes.api.listener;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.piazza.commons.servlet.http.HttpRequestAdaptor;


/**
 * HermesProtocolApiListener
 * 
 * @author Patrick Yee
 *
 */
public abstract class HermesProtocolApiListener extends HermesAbstractApiListener {

    protected String getProtocolFromPathInfo(String pathInfo) {
        int startIndex = pathInfo.indexOf("/", 1) + 1;
        int endIndex = pathInfo.indexOf("/", startIndex);
        if (endIndex == -1) {
            endIndex = pathInfo.length();
        }
        return pathInfo.substring(startIndex, endIndex);
    }

}
