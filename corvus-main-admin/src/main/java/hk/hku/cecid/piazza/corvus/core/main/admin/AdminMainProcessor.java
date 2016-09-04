package hk.hku.cecid.piazza.corvus.core.main.admin;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.module.ModuleGroup;
import hk.hku.cecid.piazza.commons.module.SystemModule;
import hk.hku.cecid.piazza.commons.spa.Plugin;
import hk.hku.cecid.piazza.commons.spa.PluginException;
import hk.hku.cecid.piazza.commons.spa.PluginHandler;

/**
 * The plugin handler for the Admin Main module plugin
 * @author Joel Matsumoto
 *
 */
public class AdminMainProcessor implements PluginHandler {

    private static ModuleGroup moduleGroup;
    
    public static SystemModule core;

    /**
     * Housecleaning Scheduler Active Task module id
     */
    public static String ACTIVE_MODULE_SCHEDULER = "admin.scheduler";
	
    /**
     * Activate the module group from the conf files
     */
	public void processActivation(Plugin plugin) throws PluginException {
        String descriptor = plugin.getParameters().getProperty("module-group-descriptor");
        moduleGroup = new ModuleGroup(descriptor, plugin.getClassLoader());
        Sys.getModuleGroup().addChild(moduleGroup);
        
        core = moduleGroup.getSystemModule();
        moduleGroup.startActiveModules();
        
        if (core == null) {
            throw new PluginException("Admin Main core system module not found");
        }

	}

	/**
	 * Deactivate the modules for this module group
	 */
	public void processDeactivation(Plugin plugin) throws PluginException {
		moduleGroup.stopActiveModules();
	}
	
    public static ModuleGroup getModuleGroup() {
        if (moduleGroup == null) {
            throw new RuntimeException("Ebms module group not initialized");
        }
        else {
            return moduleGroup;
        }
    }

}
