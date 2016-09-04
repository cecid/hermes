/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.corvus.core;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import hk.hku.cecid.piazza.commons.servlet.http.HttpDispatcherContext;
import hk.hku.cecid.piazza.commons.spa.PluginRegistry;
import hk.hku.cecid.piazza.commons.Sys;

/**
 * Kernel is the nucleus of Piazza Corvus. It initializes the basic configuration and
 * sets up the plugin registry. After setting up the plugin registry, it
 * activates the plugins and extension points and then Corvus is started.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class Kernel {

    private static Kernel  kernel = new Kernel();

    private PluginRegistry pluginRegistry;

    private boolean        hasErrors;
    
    private Date           startupTime;

    /**
     * Creates a new instance of Kernel.
     */
    private Kernel() {
        try {
            startupTime = new Date();
    
            /*
             * Set the default system module group descriptor
             */
            String sysModuleGroupProp = "sys.module.group";
            if (System.getProperty(sysModuleGroupProp) == null) {
                System.setProperty(sysModuleGroupProp, 
                        getClass().getPackage().getName().replace('.', '/') + 
                        "/conf/corvus.module-group.xml");
            }
            
            /*
             * Create the home directory, if it does not exist.
             */
            String homeDir = Sys.main.properties
                    .getProperty("/corvus/home");
            if (homeDir != null) {
                File home = new File(homeDir);
                if (!home.exists()) {
                    home.mkdirs();
                }
            }
            
            /*
             * Set the environment properties.
             */
            Properties env = Sys.main.properties
                    .createProperties("/corvus/environment/properties/*");
            System.getProperties().putAll(env);
    
            /*
             * Set the default servlet encoding.
             */
            String requestEncoding = Sys.main.properties
                    .getProperty("/corvus/encoding/servlet-request");
            String responseEncoding = Sys.main.properties
                    .getProperty("/corvus/encoding/servlet-response");
            HttpDispatcherContext.getDefaultContext().setRequestEncoding(
                    requestEncoding);
            HttpDispatcherContext.getDefaultContext().setResponseEncoding(
                    responseEncoding);
    
            /*
             * Construct and activate the plugin registry.
             */
            String pluginRegistryLocation = Sys.main.properties.getProperty(
                    "/corvus/plugin/registry", System.getProperty("user.dir")
                            + "/plugins");
            String pluginDescriptorName = Sys.main.properties.getProperty(
                    "/corvus/plugin/descriptor", "plugin.xml");

            pluginRegistry = new PluginRegistry(pluginRegistryLocation,
                    pluginDescriptorName);

            pluginRegistry.activate();
            
            if (pluginRegistry.hasErrors()) {
                hasErrors = true;
            } else {
                hasErrors = false;
                Sys.main.log.info("Corvus Kernel initialized successfully");
            }
        }
        catch (Throwable e) {
            hasErrors = true;
            Sys.main.log.error("Corvus Kernel initialized with errors", e);
        }
    }

    /**
     * Shutdowns this kernel and deactivates all the plugin registry.
     */
    public void shutdown() {
        if (pluginRegistry != null) {
            pluginRegistry.deactivate();
        }
        Sys.main.log.error("Corvus Kernel has been shutdown successfully");
    }
    
    /**
     * Retrieves the plugin registry managed by this kernel.
     * 
     * @return the plugin registry.
     */
    public PluginRegistry getPluginRegistry() {
        return pluginRegistry;
    }

    /**
     * Retrieves the startup time of this kernel. 
     * 
     * @return the startup time.
     */
    public Date getStartupTime() {
        return startupTime;
    }
    
    /**
     * Checks whether there were any errors in the start up process of this
     * kernel.
     * 
     * @return true if there were any errors in the start up process.
     */
    public boolean hasErrors() {
        return hasErrors;
    }

    /**
     * Gets the single kernel instance.
     * 
     * @return the kernel instance.
     */
    public static Kernel getInstance() {
        return kernel;
    }
}