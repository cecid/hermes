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
