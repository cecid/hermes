/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.corvus.core.main.handler;

import hk.hku.cecid.piazza.commons.servlet.http.HttpDispatcherContext;
import hk.hku.cecid.piazza.commons.spa.Extension;
import hk.hku.cecid.piazza.commons.spa.ExtensionPointIteratedHandler;

/**
 * HttpdContextRegistrar is a generic registrar for any extension point handler 
 * which handles reqistration, or alike, with an HTTP dispatcher context. 
 * 
 * @author Hugo Y. K. Lam
 *
 */
public abstract class HttpdContextRegistrar extends
        ExtensionPointIteratedHandler {

    /**
     * Gets the HTTP dispatcher context this registrar manages.
     * 
     * @param extension the extension being processed.
     * @return the default HTTP dispatcher context.
     */
    protected HttpDispatcherContext getHttpdContext(Extension extension) {
        return HttpDispatcherContext.getDefaultContext();
    }
}