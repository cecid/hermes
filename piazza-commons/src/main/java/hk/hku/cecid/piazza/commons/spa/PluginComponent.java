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
