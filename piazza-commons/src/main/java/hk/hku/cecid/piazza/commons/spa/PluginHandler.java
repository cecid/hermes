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
 * A PluginHandler handles the activation process of a plugin.
 * 
 * 
 * @see Plugin
 * 
 * @author Hugo Y. K. Lam
 *
 */
public interface PluginHandler {

    /**
     * Processes the activation of the plugin this handler represents.
     * It is invoked when the plugin is being activated.  
     * 
     * @param plugin the plugin this handler represents.
     * @throws PluginException if activation failed.
     */
    public void processActivation(Plugin plugin) throws PluginException;

    /**
     * Processes the deactivation of the plugin this handler represents.
     * It is invoked when the plugin is being deactivated.  
     * 
     * @param plugin the plugin this handler represents.
     * @throws PluginException if deactivation failed.
     */
    public void processDeactivation(Plugin plugin) throws PluginException;
}
