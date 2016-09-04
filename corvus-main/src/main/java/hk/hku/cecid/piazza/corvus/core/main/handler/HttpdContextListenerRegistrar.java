/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.corvus.core.main.handler;

import hk.hku.cecid.piazza.commons.spa.Extension;
import hk.hku.cecid.piazza.commons.spa.PluginException;

/**
 * HttpdContextListenerRegistrar handles the registration of an HTTP dispatcher 
 * context listener with the default HTTP dispatcher context.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class HttpdContextListenerRegistrar extends HttpdContextRegistrar {

    /**
     * Adds an HTTP dispatcher context listener to the default HTTP dispatcher context.
     * 
     * @param extension the extension which represents the listener.
     * @throws PluginException if unable to add the context listener.
     * @see hk.hku.cecid.piazza.commons.spa.ExtensionPointHandler#processExtensions(java.util.Collection)
     */
    public void processExtension(Extension extension) throws PluginException {
        String className = extension.getParameter("class");
        try {
            Class theClass = extension.getPlugin().loadClass(className);
            getHttpdContext(extension).addContextListener(theClass);
        }
        catch (Exception e) {
            throw new PluginException("Unable to add the context listener: "+className, e);
        }
    }
}
