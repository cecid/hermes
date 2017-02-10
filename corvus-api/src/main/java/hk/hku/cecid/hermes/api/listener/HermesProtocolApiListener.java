package hk.hku.cecid.hermes.api.listener;

import java.util.ArrayList;

/**
 * HermesProtocolApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesProtocolApiListener extends HermesAbstractApiListener {

    protected ArrayList<String> parseFromPathInfo(String pathInfo) {
        return parseFromPathInfo(pathInfo, 1);
    }

    /**
     * Returns a list of three strings:
     * [ action, protocol, parameter ]
     */
    protected ArrayList<String> parseFromPathInfo(String pathInfo, int numActionParts) {
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

        int searchFrom = 0;
        int index;
        ArrayList<String> parts = new ArrayList<String>();
        while (searchFrom != -1) {
            index = pathInfo.indexOf("/", searchFrom);
            if (index != -1) {
                parts.add(pathInfo.substring(searchFrom, index));
                searchFrom = index + 1;
            }
            else {
                parts.add(pathInfo.substring(searchFrom));
                searchFrom = -1;
            }
        }

        StringBuffer action = new StringBuffer();
        index = 0;
        for (int i=0 ; i<numActionParts ; i++) {
            if (index < parts.size()) {
                if (i != 0) {
                    action.append("/");
                }
                action.append(parts.get(index++));
            }
            else {
                break;
            }
        }
        String protocol = "";
        if (index < parts.size()) {
            protocol = parts.get(index++);
        }
        StringBuffer parameter = new StringBuffer();
        if (index < parts.size()) {
            for (int i=index ; i<parts.size() ; i++) {
                if (i != index) {
                    parameter.append("/");
                }
                parameter.append(parts.get(i));
            }
        }

        result.add(action.toString());
        result.add(protocol);
        result.add(parameter.toString());

        return result;
    }
}
