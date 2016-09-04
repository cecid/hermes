/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Apache License Version 2.0 [1]
 * 
 * [1] http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package hk.hku.cecid.piazza.corvus.admin.handler;

import hk.hku.cecid.piazza.commons.servlet.http.HttpDispatcherContext;
import hk.hku.cecid.piazza.commons.spa.Plugin;
import hk.hku.cecid.piazza.commons.spa.PluginException;
import hk.hku.cecid.piazza.commons.spa.PluginHandler;


/**
 * AdminPluginHandler is responsible for creating a new HTTP dispatcher context
 * for the admin dispatcher.
 * 
 * @see hk.hku.cecid.piazza.commons.servlet.http.HttpDispatcherContext
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class AdminPluginHandler implements PluginHandler {

    /**
     * The ID of the admin dispatcher context.
     */
    public static final String ADMIN_CONTEXT_ID = "admin"; 
        
    /**
     * Processes the admin plugin activation and creates a new HTTP dispatcher
     * context for the admin dispatcher.
     * 
     * @param plugin the plugin this handler represents.
     * @throws PluginException if activation failed.
     * @see hk.hku.cecid.piazza.commons.spa.PluginHandler#processActivation(hk.hku.cecid.piazza.commons.spa.Plugin)
     */
    public void processActivation(Plugin plugin) throws PluginException {
       
        HttpDispatcherContext context = new HttpDispatcherContext();
        context.setRequestEncoding(HttpDispatcherContext.getDefaultContext().getRequestEncoding());
        context.setResponseEncoding(HttpDispatcherContext.getDefaultContext().getResponseEncoding());
        HttpDispatcherContext.addContext(ADMIN_CONTEXT_ID, context);
    }

    /**
     * Processes the admin plugin deactivation.
     * 
     * @param plugin the plugin this handler represents.
     * @throws PluginException if deactivation failed.
     * @see hk.hku.cecid.piazza.commons.spa.PluginHandler#processDeactivation(hk.hku.cecid.piazza.commons.spa.Plugin)
     */
    public void processDeactivation(Plugin plugin) throws PluginException {
    }
}
