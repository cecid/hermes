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
