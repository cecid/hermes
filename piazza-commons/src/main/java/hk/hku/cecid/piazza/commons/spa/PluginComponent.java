/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.spa;

/**
 * A PluginComponent represents any components compose the plugin, including the plugin itself.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class PluginComponent {

    private PluginComponent parent;
    
    
    /**
     * Creates a new instance of PluginComponent. 
     * 
     * @param parent the parent component.
     */
    public PluginComponent(PluginComponent parent) {
        super();
        this.parent = parent;
    }
    
    /**
     * Gets the parent component of this plugin component. 
     * 
     * @return the parent component of this plugin component.
     */
    public PluginComponent getParent() {
        return parent;
    }
    
    /**
     * Gets the plugin which holds this plugin component.
     * 
     * @return the plugin which holds this plugin component.
     */
    public Plugin getPlugin() {
        PluginComponent com = this;
        while (com!=null) {
            com = com.getParent();
            if (com instanceof Plugin) {
                return (Plugin)com;
            }
        }
        if (com == null && this instanceof Plugin) {
            return (Plugin)this;
        }
        else {
            return null;
        }   
    }
}
