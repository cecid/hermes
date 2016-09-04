/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Apache License Version 2.0 [1]
 * 
 * [1] http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package hk.hku.cecid.piazza.corvus.core.main.admin.listener;

import hk.hku.cecid.piazza.commons.servlet.http.HttpDispatcherContext;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;

/**
 * HttpdPageletAdaptor is an admin pagelet adaptor which provides an admin 
 * function of the default HTTP dispatcher.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class HttpdPageletAdaptor extends AdminPageletAdaptor {

    /**
     * Generates the transformation source of the default HTTP dispatcher.
     * 
     * @see hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor#getCenterSource(javax.servlet.http.HttpServletRequest)
     */
    protected Source getCenterSource(HttpServletRequest request) {
        
        PropertyTree dom = new PropertyTree();
        dom.setProperty("/httpd", "");

        String STATUS_HALTED = "Halted";
        String STATUS_RUNNING = "Running";

        String chstatus = request.getParameter(REQ_PARAM_ACTION);
        if ("halt".equals(chstatus)) {
            HttpDispatcherContext.getDefaultContext().halt();
        }
        else if ("resume".equals(chstatus)) {
            HttpDispatcherContext.getDefaultContext().resume();
        }

        HttpDispatcherContext dispatcherContext = HttpDispatcherContext.getDefaultContext();
        String status = dispatcherContext.isHalted()? (dispatcherContext.isHalting()? "Being ":"")+STATUS_HALTED:STATUS_RUNNING;
        
        String action = "";
        if (status.equals(STATUS_HALTED)) {
            action = "resume";
        }
        else if (status.equals(STATUS_RUNNING)) {
            action = "halt";
        }
        
        dom.setProperty("status/state", status);
        dom.setProperty("status/action", action);
        dom.setProperty("status/threads", String.valueOf(dispatcherContext.getCurrentThreadCount()));
        
        Iterator contextListeners = dispatcherContext.getContextListeners().iterator();
        for (int i=1; contextListeners.hasNext(); i++) {
            String listener = contextListeners.next().getClass().getName();
            dom.setProperty("context-listeners/listener["+i+"]", listener);
        }
        
        Iterator filters = dispatcherContext.getRequestFilters().iterator();
        for (int i=1; filters.hasNext(); i++) {
            String filter = filters.next().getClass().getName();
            dom.setProperty("request-filters/filter["+i+"]", filter);
        }
        
        Properties info = dispatcherContext.getRegisteredListenersInfo();
        Enumeration pathInfos = info.keys();
        for (int i=1; pathInfos.hasMoreElements(); i++) {
            String pathInfo = pathInfos.nextElement().toString();
            String listener = info.getProperty(pathInfo);
            dom.setProperty("request-listeners/listener["+i+"]/context", pathInfo);
            dom.setProperty("request-listeners/listener["+i+"]/listener", listener);
        }
        
        return dom.getSource(); 
    }
}
