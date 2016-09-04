/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.spa;

import java.util.Properties;


/**
 * An Extension is a plugin component which represents the extension element
 * in the plugin descriptor. 
 * 
 * @see Plugin
 * @see ExtensionPoint
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class Extension extends PluginComponent {

    private String point;
    private String name;
    private Properties parameters;
    
    /**
     * Creates a new instance of Extension.
     * 
     * @param parent the parent plugin component.
     * @param point the extension point this extension extends. 
     * @param name the extension name.
     */
    public Extension(PluginComponent parent, String point, String name) {
        this(parent, point, name, null);
    }
    
    /**
     * Creates a new instance of Extension.
     * 
     * @param parent the parent plugin component.
     * @param point the extension point this extension extends.
     * @param name the extension name.
     * @param parameters the extension parameters.  
     */
    public Extension(PluginComponent parent, String point, String name, Properties parameters) {
        super(parent);
        this.point = point;
        this.name = name;
        this.parameters = parameters == null? new Properties() : parameters;
    }
    
    /**
     * Gets an extension parameter.
     * 
     * @param key the key of the parameter.
     * @return the parameter.
     */
    public String getParameter(String key) {
        return parameters.getProperty(key);
    }

    /**
     * Gets the extension parameters.
     * 
     * @return the parameters.
     */
    public Properties getParameters() {
        return parameters;
    }
    
    /**
     * Gets the extension point this extension extends.
     * 
     * @return the extension point.
     */
    public String getPoint() {
        return point;
    }
    
    /**
     * Gets the extension name.
     * 
     * @return the extension name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns a string representation of this extension.
     * 
     * @return a string representation of this extension.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Extension: "+getName()+"@"+getPoint();
    }
}
