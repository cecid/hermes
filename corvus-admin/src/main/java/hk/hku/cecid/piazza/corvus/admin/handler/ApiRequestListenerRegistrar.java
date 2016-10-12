/*
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Academic Free License Version 1.0
 */

package hk.hku.cecid.piazza.corvus.admin.handler;

import hk.hku.cecid.piazza.commons.servlet.http.HttpDispatcherContext;
import hk.hku.cecid.piazza.commons.spa.Extension;
import hk.hku.cecid.piazza.corvus.core.main.handler.HttpdRequestListenerRegistrar;

/**
 * ApiRequestListenerRegistrar handles the registration of an HTTP request
 * listener with the API dispatcher context.
 *
 * @author Patrick Yee
 *
 */
public class ApiRequestListenerRegistrar extends HttpdRequestListenerRegistrar {

    /**
     * Gets the admin dispatcher context.
     * 
     * @see hk.hku.cecid.piazza.corvus.core.main.handler.HttpdContextRegistrar#getHttpdContext(hk.hku.cecid.piazza.commons.spa.Extension)
     */
    protected HttpDispatcherContext getHttpdContext(Extension extension) {
        return HttpDispatcherContext.getContext(AdminPluginHandler.API_CONTEXT_ID);
    }
}
