/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.spa;

import java.util.Collection;


/**
 * An ExtensionPointHandler handles all the extensions of the 
 * extension point it represents.
 * 
 * @see ExtensionPoint
 * 
 * @author Hugo Y. K. Lam
 *
 */
public interface ExtensionPointHandler {

    /**
     * Processes the extensions of the extension point it represents.
     * It is invoked when the plugin registry is being activated.  
     * 
     * @param extensions the extensions of the extension point it represents.
     * @throws PluginException if failed in the processing extensions.
     */
    public void processExtensions(Collection extensions) throws PluginException;
}
