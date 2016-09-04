/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.spa;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

/**
 * A Plugin represents a plugin descriptor.
 * It contains the elements specified in the descriptor.
 * It also contains a class loader for loading classes which 
 * stored in the libraries specified in the descriptor.  
 * 
 * If there is a plugin handler specified in the descriptor, 
 * it will be invoked when the plugin is being activated.
 * 
 * @see PluginRegistry
 * @see PluginClassLoader
 * @see PluginHandler
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class Plugin extends PluginComponent {

    private boolean activated;
    
    private Collection  libraries;

    private Collection  extensionPoints;
    
    private Collection  extensions;

    private Collection  imports;

    private File        pluginFolder;

    private PluginDescriptor pluginDescriptor;

    private PluginClassLoader pluginClassLoader;
    
    private PluginRegistry pluginRegistry;

    /**
     * Creates a new instance of Plugin.
     * 
     * @param registry the plugin registry which stores this plugin.
     * @param folder the folder which holds this plugin.
     * @param descriptor the descriptor of this plugin. 
     * @throws PluginException if this plugin cannot be initialized according to its plugin descriptor. 
     */
    public Plugin(PluginRegistry registry, File folder, String descriptor) throws PluginException {
        super(null);
        try {
            pluginRegistry = registry;
            pluginFolder = folder;
            pluginDescriptor = new PluginDescriptor(new File(folder, descriptor));
            pluginDescriptor.setParent(this);

            libraries = pluginDescriptor.getLibraries();
            extensionPoints = pluginDescriptor.getExtensionPoints();
            extensions = pluginDescriptor.getExtensions();
            imports = pluginDescriptor.getImports();

            pluginClassLoader = createClassLoader();
        }
        catch (Exception e) {
            throw new PluginException("Unable to initialize plugin", e);
        }
    }

    /**
     * Creates a class loader for loading classes which 
     * stored in the libraries specified in the plugin descriptor.  
     * 
     * @return a new class loader instance. 
     * @throws PluginException if the libraries specified in the descriptor are invalid.
     */
    private PluginClassLoader createClassLoader() throws PluginException {
        URL[] urls = getLibraryURLs();
        return new PluginClassLoader(this, urls, this.getClass()
                .getClassLoader());
    }

    /**
     * Gets the library URLs specified in the plugin descriptor.
     * 
     * @return the library URLs specified in the plugin descriptor.
     * @throws PluginException if the libraries specified in the descriptor are invalid.
     */
    private URL[] getLibraryURLs() throws PluginException {
        ArrayList urls = new ArrayList();
        Iterator libraryNames = libraries.iterator();
        while (libraryNames.hasNext()) {
            Library library = (Library) libraryNames.next();
            File libFile = new File(pluginFolder, library.getName());
            if (libFile.exists()) {
                try {
                    urls.add(libFile.toURI().toURL());
                }
                catch (MalformedURLException e) {
                    throw new PluginException("Invalid library URL", e);
                }
            }
        }
        return (URL[]) urls.toArray(new URL[]{});
    }

    /**
     * Activates this plugin by invoking the plugin handler specified in the plugin descriptor.
     * If the plugin has already been activated, nothing will be done. 
     * 
     * @throws PluginException if there is any error in processing activation by the handler.
     */
    public synchronized void activate() throws PluginException {
        if (!activated) {
            if (getHandlerClass() != null && !"".equals(getHandlerClass())) {
                try {
                    Class theClass = loadClass(getHandlerClass());
                    PluginHandler handler = (PluginHandler)theClass.newInstance();
                    handler.processActivation(this);
                }
                catch (Throwable e) {
                    throw new PluginException("Error in processing activation by handler: "+getHandlerClass(), e);
                }
            }
            activated = true;
        }
    }
    
    /**
     * Deactivates this plugin by invoking the plugin handler specified in the plugin descriptor.
     * If the plugin has not yet been activated, nothing will be done. 
     * 
     * @throws PluginException if there is any error in processing deactivation by the handler.
     */
    public synchronized void deactivate() throws PluginException {
        if (activated) {
            if (getHandlerClass() != null && !"".equals(getHandlerClass())) {
                try {
                    Class theClass = loadClass(getHandlerClass());
                    PluginHandler handler = (PluginHandler)theClass.newInstance();
                    handler.processDeactivation(this);
                }
                catch (Throwable e) {
                    throw new PluginException("Error in processing deactivation by handler: "+getHandlerClass(), e);
                }
            }
            activated = false;
        }        
    }
    
    /**
     * Checks if this plugin has been activated.
     * 
     * @return true if this plugin has been activated.
     */
    public boolean isActivated() {
        return activated;
    }
    
    /**
     * Gets all the extension points declared in the plugin descriptor.
     * 
     * @return all the extension points declared in the plugin descriptor.
     */
    public Collection getExtensionPoints() {
        return extensionPoints;
    }

    /**
     * Gets all extensions declared in the plugin descriptor. 
     * 
     * @return all extensions declared in the plugin descriptor.
     */
    public Collection getExtensions() {
        return extensions;
    }

    /**
     * Gets all extensions declared in the plugin descriptor which 
     * extends the specified extension point.
     *  
     * @param point the extension point.
     * @return all extensions declared in the plugin descriptor which 
     *          extends the specified extension point. 
     */
    public Collection getExtensions(String point) {
        ArrayList list = new ArrayList();
        if (point != null) {
            Iterator exts = extensions.iterator();
            while (exts.hasNext()) {
                Extension ext = (Extension) exts.next();
                if (point.equals(ext.getPoint())) {
                    list.add(ext);
                }
            }
        }
        return list;
    }

    /**
     * Gets all libraries declared in the plugin descriptor.
     * 
     * @return all libraries declared in the plugin descriptor.
     */
    public Collection getLibraries() {
        return libraries;
    }

    /**
     * Gets all plugin imports declared in the plugin descriptor.
     * 
     * @return all plugin imports declared in the plugin descriptor.
     */
    public Collection getImports() {
        return imports;
    }

    /**
     * Gets the plugin ID.
     * 
     * @return the plugin ID.
     */
    public String getId() {
        return pluginDescriptor.getId();
    }

    /**
     * Gets the plugin name.
     * 
     * @return the plugin name.
     */
    public String getName() {
        return pluginDescriptor.getName();
    }

    /**
     * Gets the handler class of this plugin.
     * 
     * @return the handler class of this plugin.
     */
    public String getHandlerClass() {
        return pluginDescriptor.getHandlerClass();
    }

    /**
     * Gets the provider name of this plugin.
     * 
     * @return the provider name of this plugin.
     */
    public String getProviderName() {
        return pluginDescriptor.getProviderName();
    }

    /**
     * Gets the version of this plugin.
     * 
     * @return the version of this plugin.
     */
    public String getVersion() {
        return pluginDescriptor.getVersion();
    }

    /**
     * Gets the parameters of this plugin.
     * 
     * @return the parameters of this plugin.
     */
    public Properties getParameters() {
        return pluginDescriptor.getParameters();
    }
    
    /**
     * Returns a string representation of this plugin.
     * 
     * @return a string representation of this plugin.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Plugin ID: "+getId();
    }

    /**
     * Loads a class from this plugin's class loader.
     * 
     * @param name the class name.
     * @return the Class instance for the specified class name.
     * @throws PluginException if the class was not found.
     */
    public Class loadClass(String name) throws PluginException {
        try {
            return Class.forName(name, true, pluginClassLoader);
        }
        catch (Throwable e) {
            throw new PluginException("Unable to load class '" + name
                    + "' from plugin classloader :" + pluginClassLoader, e);
        }
    }

    /**
     * Gets the class loader for this plugin.  
     * 
     * @return the class loader for this plugin. 
     */
    public PluginClassLoader getClassLoader() {
        return pluginClassLoader;
    }

    /**
     * Gets the plugin registry of this plugin.
     * 
     * @return the plugin registry of this plugin.
     */
    public PluginRegistry getPluginRegistry() {
        return pluginRegistry;
    }
}