/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.spa;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.io.FileSystem;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.util.Zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A PluginRegistry is a registry which stores all the plugin configurations. It
 * has a fixed registry location and the plugin configurations are specified in
 * plugin descriptors.
 * 
 * @see Plugin
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class PluginRegistry {
    
    private String  pluginExtension  = PluginFile.DEFAULT_FILE_EXT;

    private String  pluginDescriptor = PluginDescriptor.DEFAULT_DESCRIPTOR_NAME;

    private Map     plugins          = new HashMap();

    private boolean activated        = false;

    private boolean hasErrors        = false;

    private FileSystem    registrySystem;

    /**
     * Creates a new instance of PluginRegistry.
     * 
     * @param registry the registry location.
     * @throws PluginException if the registry is invalid.
     */
    public PluginRegistry(String registry)
            throws PluginException {
        this(registry, null);
    }

    /**
     * Creates a new instance of PluginRegistry.
     * 
     * @param registry the registry location.
     * @throws PluginException if the registry is invalid.
     */
    public PluginRegistry(File registry)
            throws PluginException {
        this(registry, null);
    }

    /**
     * Creates a new instance of PluginRegistry.
     * 
     * @param registry the registry location.
     * @param descriptor the plugin descriptor name.
     * @throws PluginException if the registry is invalid.
     */
    public PluginRegistry(String registry, String descriptor)
            throws PluginException {
        this(registry == null ? null : new File(registry), descriptor);
    }

    /**
     * Creates a new instance of PluginRegistry.
     * 
     * @param registry the registry location.
     * @param descriptor the plugin descriptor name.
     * @throws PluginException if the registry is invalid.
     */
    public PluginRegistry(File registry, String descriptor)
            throws PluginException {
        if (registry != null && !registry.exists()) {
            registry.mkdirs();
        }
        if (registry == null || !registry.exists() || !registry.isDirectory()) {
            throw new PluginException("Invalid plugin registry: " + registry);
        }
        if (descriptor != null && !"".equals(descriptor = descriptor.trim())) {
            pluginDescriptor = descriptor;
        }

        registrySystem = new FileSystem(registry);
        
        deploySpaFiles(registrySystem.getFiles(false, ".*\\."+pluginExtension));

        createPlugins(registrySystem.getDirectories(false));
        
        setClasspaths(plugins.values());

        if (hasErrors) {
            Sys.main.log.info("Plugin registry (" + registrySystem
                    + ") initialized with errors.");
        }
        else {
            Sys.main.log.info("Plugin registry (" + registrySystem
                    + ") initialized successfully.");
        }
    }

    /**
     * Deploys the SPA files in this plugin registry. Any previously deployed 
     * plugin folders will be overwritten. The SPA file will be deleted upon a 
     * successful deployment.
     * 
     * @param spaCollection the SPA files to be deployed.
     * @return the plugin folders of the deployed SPA files. 
     */
    private Collection deploySpaFiles(Collection spaCollection) {
        ArrayList spaDirs = new ArrayList();
        Iterator spaFiles = spaCollection.iterator(); 
        
        while (spaFiles.hasNext()) {
            File spaFile = (File)spaFiles.next();
            try {
                Sys.main.log.debug("Deploying SPA file: " + spaFile);

                PluginFile pluginFile = new PluginFile(spaFile, pluginDescriptor);
                String pluginID = pluginFile.getDescriptor().getId();
                File pluginDir = new File(registrySystem.getRoot(), pluginID);
                
                try {
                    undeploy(pluginID);
                }
                catch (Exception e) {
                    Sys.main.log.warn("Cannot undeploy plugin for re-deployment", e);
                }
                
                Zip.extract(spaFile, pluginDir);
                spaFile.delete();
                spaDirs.add(pluginDir);
                Sys.main.log.info("SPA file (" + spaFile + ") deployed successfully");
            }
            catch (Throwable e) {
                hasErrors = true;
                Sys.main.log.error("Error in deploying SPA file: "
                        + spaFile, e);
            }
        }
        return spaDirs;
    }
    
    /**
     * Creates all the plugins in this plugin registry.
     * 
     * @param pluginDirCollection the plugin folders for creating the plugins.
     * @return the created plugins.
     */
    private Collection createPlugins(Collection pluginDirCollection) {
        ArrayList createdPlugins = new ArrayList(); 
        Iterator pluginDirs = pluginDirCollection.iterator();
        
        while (pluginDirs.hasNext()) {
            try {
                File pluginDir = (File)pluginDirs.next();
                if (!new File(pluginDir, pluginDescriptor).exists()) {
                    Sys.main.log.info("Cleaning invalid plugin directory: " + pluginDir);
                    new FileSystem(pluginDir).remove();
                    continue;
                }

                Plugin plugin = new Plugin(this, pluginDir,
                        pluginDescriptor);
                plugins.put(plugin.getId(), plugin);
                createdPlugins.add(plugin);
                Sys.main.log.info("Plugin '" + plugin.getId()
                        + "' created successfully");
            }
            catch (Throwable e) {
                hasErrors = true;
                Sys.main.log.error("Error in creating plugin", e);
            }
        }
        return createdPlugins;
    }
    
    /**
     * Sets imported classpaths.
     * 
     * @param pluginCollection the plugins for setting the classpaths.
     */
    private void setClasspaths(Collection pluginCollection) {
        Iterator allPlugins = pluginCollection.iterator();
        while (allPlugins.hasNext()) {
            Plugin plugin = (Plugin) allPlugins.next();
            Iterator imports = plugin.getImports().iterator();
            while (imports.hasNext()) {
                Import imp = (Import) imports.next();
                Plugin importedPlugin = imp.getImportedPlugin();
                if (importedPlugin != null) {
                    plugin.getClassLoader().importClassLoader(
                            importedPlugin.getClassLoader());
                }
            }
        }
    }

    /**
     * Deploys a plugin to the plugin registry.
     * 
     * @param spa the SPA file input stream.
     * @return the plugin ID of the deployed plugin.
     * @throws PluginException if unable to deploy the plugin.
     */
    public String deploy(InputStream spa) throws PluginException {
        try {
            File spaFile = File.createTempFile("plugin-", "."+pluginExtension);
            FileOutputStream fos = new FileOutputStream(spaFile); 
            IOHandler.pipe(spa, fos);
            fos.close();
            
            Collection spaFiles = new ArrayList();
            spaFiles.add(spaFile);
            
            Collection spaDirs = deploySpaFiles(spaFiles);
            if (spaDirs.size() < 1) {
                throw new PluginException("Unable to deploy SPA file: "+spaFile);
            }
            
            String id = ((File)spaDirs.iterator().next()).getName();
            Collection createdPlugins = createPlugins(spaDirs);
            if (createdPlugins.size() < 1) {
                throw new PluginException("Unable to create plugin: "+id);
            }
            
            setClasspaths(createdPlugins);
            return id;
        }
        catch (Exception e) {
            throw new PluginException("Unable to deploy plugin", e);
        }
    }
    
    /**
     * Undeploys a plugin from the plugin registry.
     * 
     * @param pluginID the plugin ID to undeploy.
     * @throws PluginException if unable to undeploy the plugin.
     */
    public void undeploy(String pluginID) throws PluginException {
        try {
            if (pluginID != null) {
                pluginID = pluginID.trim();
                File pluginDir = new File(registrySystem.getRoot(), pluginID);
                if (pluginDir.exists()) {
                    new FileSystem(pluginDir).purge();
                    Sys.main.log.info("Plugin '"+pluginID+"' undeployed successfully");
                }
            }
        }
        catch (Exception e) {
            throw new PluginException("Unable to undeploy plugin: "+pluginID, e);
        }
    }
    
    /**
     * Activates the plugin registry if it is not yet activated.
     */
    public synchronized void activate() {
        if (!activated) {
            hasErrors = hasErrors | activatePlugins() | bindExtensionPoints();
            activated = true;
        }
    }

    /**
     * Deactivates the plugin registry if it is activated.
     */
    public synchronized void deactivate() {
        if (activated) {
            deactivatePlugins();
            activated = false;
        }
    }
    
    /**
     * Activate all plugins in this registry.
     * 
     * @return true if there is any error occurred during activating plugins.
     */
    private boolean activatePlugins() {
        boolean hasErrors = false;
        Iterator allPlugins = plugins.values().iterator();
        while (allPlugins.hasNext()) {
            Plugin plugin = (Plugin) allPlugins.next();
            try {
                plugin.activate();
            }
            catch (Throwable e) {
                hasErrors = true;
                Sys.main.log.error("Error in activating plugin: "
                        + plugin.getId(), e);
            }
        }
        return hasErrors;
    }

    /**
     * Deactivate all plugins in this registry.
     * 
     * @return true if there is any error occurred during activating plugins.
     */
    private boolean deactivatePlugins() {
        boolean hasErrors = false;
        Iterator allPlugins = plugins.values().iterator();
        while (allPlugins.hasNext()) {
            Plugin plugin = (Plugin) allPlugins.next();
            try {
                plugin.deactivate();
            }
            catch (Throwable e) {
                hasErrors = true;
                Sys.main.log.error("Error in deactivating plugin: "
                        + plugin.getId(), e);
            }
        }
        return hasErrors;
    }
    
    /**
     * Binds all extensions to their corresponding extension points.
     * 
     * @return true if there is any error occurred during binding extensions.
     */
    private boolean bindExtensionPoints() {
        boolean hasErrors = false;
        Iterator allPlugins = plugins.values().iterator();
        while (allPlugins.hasNext()) {
            Plugin plugin = (Plugin) allPlugins.next();
            Iterator extensionPoints = plugin.getExtensionPoints().iterator();
            while (extensionPoints.hasNext()) {
                ExtensionPoint extensionPoint = (ExtensionPoint) extensionPoints
                        .next();
                try {
                    Collection extensions = getAllExtensions(extensionPoint
                            .getId());
                    extensionPoint.processExtensions(extensions);
                }
                catch (Throwable e) {
                    hasErrors = true;
                    Sys.main.log.error("Error in binding extension point: "
                            + extensionPoint.getId(), e);
                }
            }
        }
        return hasErrors;
    }

    /**
     * Checks if this plugin registry has already been activated.
     * 
     * @return true if this plugin registry has already been activated.
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * Checks if there is any error occurred during the initialization or
     * activation of this plugin registry.
     * 
     * @return true if there is any error occurred during the activation of this
     *         plugin registry.
     */
    public boolean hasErrors() {
        return hasErrors;
    }

    /**
     * Gets a plugin from this plugin registry.
     * 
     * @param id the plugin ID.
     * @return the plugin corresponding to the specified plugin ID.
     */
    public Plugin getPlugin(String id) {
        if (id == null) {
            return null;
        }
        else {
            return (Plugin) plugins.get(id);
        }
    }

    /**
     * Gets all plugins in this plugin registry.
     * 
     * @return all plugins in this plugin registry.
     */
    public Collection getPlugins() {
        return plugins.values();
    }

    /**
     * Gets all extensions corresponding to the specified extension point.
     * 
     * @param point the extension point.
     * @return all extensions corresponding to the specified extension point.
     */
    public Collection getAllExtensions(String point) {
        ArrayList list = new ArrayList();
        if (point != null) {
            Iterator plugins = getPlugins().iterator();
            while (plugins.hasNext()) {
                Plugin plugin = (Plugin) plugins.next();
                Collection extensions = plugin.getExtensions(point);
                list.addAll(extensions);
            }
        }
        return list;
    }

    /**
     * Gets the plugin registry location.
     * 
     * @return the plugin registry location.
     */
    public String getLocation() {
        return registrySystem.toString();
    }

    /**
     * Returns a string representation of this plugin registry.
     * 
     * @return a string representation of this plugin registry.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Plugin registry: " + getLocation();
    }
}