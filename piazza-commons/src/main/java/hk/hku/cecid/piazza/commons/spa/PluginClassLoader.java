/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.spa;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.Iterator;
import java.util.Vector;

/**
 * A PluginClassLoader is a class loader for loading classes which 
 * stored in the libraries specified in a plugin descriptor.  
 * 
 * @see Plugin
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class PluginClassLoader extends URLClassLoader {

    private Vector importedLoaders = new Vector();
    
    private Plugin plugin;
    
    /**
     * Creates a new instance of PluginClassLoader. 
     * 
     * @param plugin the Plugin corresponds to this class loader.
     * @param urls the search paths.
     */
    public PluginClassLoader(Plugin plugin, URL[] urls) {
        super(urls);
        this.plugin = plugin;
    }

    /**
     * Creates a new instance of PluginClassLoader. 
     * 
     * @param plugin the Plugin corresponds to this class loader.
     * @param urls the search paths.
     * @param parent the parent class loader.
     */
    public PluginClassLoader(Plugin plugin, URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.plugin = plugin;
    }

    /**
     * Creates a new instance of PluginClassLoader. 
     * 
     * @param plugin the Plugin corresponds to this class loader.
     * @param urls the search paths.
     * @param parent the parent class loader.
     * @param factory the URL stream handler factory.
     */
    public PluginClassLoader(Plugin plugin, URL[] urls, ClassLoader parent,
            URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
        this.plugin = plugin;
    }

    /**
     * Finds and loads the class with the specified name from the URL search path. 
     * Any URLs referring to JAR files are loaded and opened as needed until the class is found.
     * If the class is not found from the search paths of this class loader, it will be searched 
     * from the imported class loaders. 
     * 
     * @param name the name of the class.
     * @return the resulting class.
     * @throws java.lang.ClassNotFoundException if the class could not be found.
     * @see java.lang.ClassLoader#findClass(java.lang.String)
     */
    protected Class findClass(String name) throws ClassNotFoundException {
        try {
            return super.findClass(name);
        }
        catch (ClassNotFoundException e) {
            return findImportedClass(name);
        }
    }
    
    /**
     * Finds and loads the class with the specified name from the imported class loaders. 
     * 
     * @param name the name of the class.
     * @return the resulting class.
     * @throws java.lang.ClassNotFoundException if the class could not be found.
     * @see java.lang.ClassLoader#findClass(java.lang.String)
     */
    protected Class findImportedClass(String name) throws ClassNotFoundException {
        Iterator loaders = importedLoaders.iterator();
        while (loaders.hasNext()) {
            ClassLoader loader = (ClassLoader)loaders.next();
            try {
                return loader.loadClass(name);
            }
            catch (ClassNotFoundException e) {
                if (loaders.hasNext()) {
                    continue;
                }
                else {
                    throw e;
                }
            }
        }
        throw new ClassNotFoundException("No class definition found: "+name);
    }

    /**
     * Imports a class loader.
     * 
     * @param loader the class loader to be imported.
     */
    void importClassLoader(ClassLoader loader) {
        if (loader!=null) {
            importedLoaders.addElement(loader);
        }
    }
    
    /**
     * Finds the resource with the specified name on the URL search paths.
     * 
     * @param name the name of the resource.
     * @return a URL for the resource, or null if the resource could not be found.
     * @see java.lang.ClassLoader#findResource(java.lang.String)
     */
    public URL findResource(String name) {
        return super.findResource(name);
    }
    
    /**
     * Gets the plugin corresponds to this class loader.
     * 
     * @return the plugin corresponds to this class loader.
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Returns a string representation of this class loader.
     * 
     * @return a string representation of this class loader.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String lf = System.getProperty("line.separator");
        String ln = lf + "------------------------------" + lf;
        String desc = lf + "Class" + ln + this.getClass().getName() + lf + lf;
        desc += "Plugin" + ln + plugin + lf + lf;
        desc += "Classpaths" + ln;
        Object[] urls = super.getURLs();
        for (int i = 0; i < urls.length; i++) {
            desc += urls[i] + lf;
        }
        desc += lf + "Imported Loaders" + ln;
        for (int i = 0; i < importedLoaders.size(); i++) {
            desc += importedLoaders.get(i) + lf;
        }
        desc += lf + "Parent" + ln + getParent();
        return desc;
    }
}