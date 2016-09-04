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
 * An ExtensionPoint is a plugin component which represents the extension-point element
 * in the plugin descriptor. 
 * 
 * @see Plugin
 * @see Extension
 * @see ExtensionPointHandler
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class ExtensionPoint extends PluginComponent {

    private String id;
    private String name;
    private String handlerClass;
    
    /**
     * Creates a new instance of ExtensionPoint.
     * 
     * @param parent the parent plugin component.
     * @param id the extension point ID.
     * @param handlerClass the handler class of this extension point.  
     */
    public ExtensionPoint(PluginComponent parent, String id, String name, String handlerClass) {
        super(parent);
        this.id = id;
        this.name = name;
        this.handlerClass = handlerClass;
    }
    
    /**
     * Processes extensions by invoking the handler class of this extension point.
     * 
     * @param extensions the extensions to be processed.
     * @throws PluginException if there is error in processing extensions by the handler
     */
    public void processExtensions(Collection extensions) throws PluginException {
        if (extensions!=null) {
            if (handlerClass != null && !"".equals(handlerClass)) {
                try {
                    Class theClass = getPlugin().loadClass(handlerClass);
                    ExtensionPointHandler handler = (ExtensionPointHandler)theClass.newInstance();
                    handler.processExtensions(extensions);
                }
                catch (Throwable e) {
                    throw new PluginException("Error in processing extensions by handler: "+handlerClass, e);
                }
            }
        }
    }
    
    /**
     * Gets the ID of this extention point.
     * 
     * @return the ID of this extention point.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Gets the handler class of this extension point.
     * 
     * @return the handler class of this extension point.
     */
    public String getHandlerClass() {
        return handlerClass;
    }
    
    /**
     * Gets the name of this extension point.
     * 
     * @return the name of this extension point.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a string representation of this extension point.
     * 
     * @return a string representation of this extension point.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Extension point: "+getId();
    }
}
