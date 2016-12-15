/* 
 * Copyright(c) 2016 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.hermes.api.listener;

import java.util.ArrayList;

/**
 * HermesProtocolApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesProtocolApiListener extends HermesAbstractApiListener {

    /**
     * Returns a list of three strings:
     * [ action, protocol, parameter ]
     */
    protected ArrayList<String> parseFromPathInfo(String pathInfo) {
        final int NUM_PARTS = 3;

        ArrayList<String> result = new ArrayList<String>();

        if (pathInfo == null || pathInfo.equals("")) {
            for (int i=0 ; i<NUM_PARTS ; i++) {
                result.add("");
            }
            return result;
        }

        if (pathInfo.charAt(0) == '/') {
            pathInfo = pathInfo.substring(1);
        }

        if (pathInfo.charAt(pathInfo.length() - 1) == '/') {
            pathInfo = pathInfo.substring(0, pathInfo.length() - 1);
        }

        String part;
        int searchFrom = 0;
        int index;
        for (int i=0 ; i<NUM_PARTS ; i++) {
            if (searchFrom >= 0 && searchFrom < pathInfo.length()) {
                index = pathInfo.indexOf("/", searchFrom);
                if (index != -1 && i != NUM_PARTS - 1) {
                    part = pathInfo.substring(searchFrom, index);
                    searchFrom = index + 1;
                } else {
                    part = pathInfo.substring(searchFrom);
                    searchFrom = -1;
                }
            } else {
                part = "";
            }
            result.add(part);
        }
        return result;
    }

}
