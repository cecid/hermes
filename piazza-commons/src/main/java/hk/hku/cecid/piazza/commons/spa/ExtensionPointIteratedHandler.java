/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.spa;

import hk.hku.cecid.piazza.commons.Sys;

import java.util.Collection;
import java.util.Iterator;


/**
 * An ExtensionPointIteratedHandler handles all the extensions of the 
 * extension point it represents one by one.
 * 
 * @see ExtensionPoint
 *  
 * @author Hugo Y. K. Lam
 *
 */
public abstract class ExtensionPointIteratedHandler implements ExtensionPointHandler {

    /**
     * Checks if fault tolerance is enabled.
     * 
     * @return true if fault tolerance is enabled.
     */
    protected boolean isFaultTolerated() {
        return true;
    }
    
    /**
     * Processes the extensions one by one.
     * If fault tolerance is enabled, no exception will be thrown and processes
     * will be carried on even if there are any exceptions.
     * 
     * @param extensions the extensions of the extension point it represents.
     * @throws PluginException if failed in processing the extensions.
     * @see hk.hku.cecid.piazza.commons.spa.ExtensionPointHandler#processExtensions(java.util.Collection)
     */
    public void processExtensions(Collection extensions) throws PluginException {

        Iterator allExtensions = extensions.iterator();
        
        while (allExtensions.hasNext()) {
            Extension extension = (Extension) allExtensions.next();
            try {
                processExtension(extension);
            }
            catch (Throwable e) {
                String err = "Error in processing extension: " + 
                                extension.getName() + "@" + extension.getPoint();
                if (isFaultTolerated()) {
                    Sys.main.log.error(err, e);
                }
                else {
                    throw new PluginException(err, e);
                }
            }
        }
    }

    /**
     * Invoked by processExtensions() for processing each extension. 
     * 
     * @param extension the extension to be processed.
     */
    public abstract void processExtension(Extension extension) throws PluginException;
}
