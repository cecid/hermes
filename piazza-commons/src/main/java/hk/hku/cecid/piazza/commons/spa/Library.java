package hk.hku.cecid.piazza.commons.spa;



/**
 * A Library is a plugin component which represents the library element
 * in the plugin descriptor. 
 * 
 * @see Plugin
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class Library extends PluginComponent {

    private String name;
    
    /**
     * Creates a new instance of Library.
     * 
     * @param parent the parent plugin component.
     * @param name the name of this library.
     */
    public Library(PluginComponent parent, String name) {
        super(parent);
        this.name = name;
    }

    /**
     * Gets the name of this library.
     * 
     * @return the name of this library.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns a string representation of this library.
     * 
     * @return a string representation of this library.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Library name: "+getName();
    }
}
