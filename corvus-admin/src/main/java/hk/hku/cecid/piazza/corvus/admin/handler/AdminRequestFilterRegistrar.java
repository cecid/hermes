package hk.hku.cecid.piazza.corvus.admin.handler;

import hk.hku.cecid.piazza.commons.servlet.http.HttpDispatcherContext;
import hk.hku.cecid.piazza.commons.spa.Extension;
import hk.hku.cecid.piazza.corvus.core.main.handler.HttpdRequestFilterRegistrar;


/**
 * AdminRequestFilterRegistrar handles the registration of an HTTP request filter
 * with the admin dispatcher context.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class AdminRequestFilterRegistrar extends HttpdRequestFilterRegistrar {

    /**
     * Gets the admin dispatcher context.
     * 
     * @see hk.hku.cecid.piazza.corvus.core.main.handler.HttpdContextRegistrar#getHttpdContext(hk.hku.cecid.piazza.commons.spa.Extension)
     */
    protected HttpDispatcherContext getHttpdContext(Extension extension) {
        return HttpDispatcherContext.getContext(AdminPluginHandler.ADMIN_CONTEXT_ID);
    }
}
