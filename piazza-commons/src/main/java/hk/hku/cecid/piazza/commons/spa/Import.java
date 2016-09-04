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
 * An Import is a plugin component which represents the import element
 * in the plugin descriptor. 
 * 
 * @see Plugin
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class Import extends PluginComponent {

    private String importedPluginId;
    
    /**
     * Creates a new instance of Import.
     * 
     * @param parent the parent plugin component.
     * @param importedPluginId the ID of the imported plugin. 
     */
    public Import(PluginComponent parent, String importedPluginId) {
        super(parent);
        this.importedPluginId = importedPluginId;
    }

    /**
     * Gets the ID of the imported plugin.
     * 
     * @return the ID of the imported plugin.
     */
    public String getImportedPluginId() {
        return importedPluginId;
    }
    
    /**
     * Gets the imported plugin.
     * 
     * @return the imported plugin.
     */
    public Plugin getImportedPlugin() {
        Plugin plugin = getPlugin();
        if (plugin != null) {
            PluginRegistry pluginRegistry = plugin.getPluginRegistry();
            if (pluginRegistry!=null) {
                return pluginRegistry.getPlugin(getImportedPluginId());
            }
        }
        return null;
    }
    
    /**
     * Returns a string representation of this import.
     * 
     * @return a string representation of this import.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Imported plugin: " + getImportedPluginId();
    }
}
