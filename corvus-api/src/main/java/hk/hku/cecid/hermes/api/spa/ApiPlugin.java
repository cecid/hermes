package hk.hku.cecid.hermes.api.spa;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.module.ModuleGroup;
import hk.hku.cecid.piazza.commons.module.SystemModule;
import hk.hku.cecid.piazza.commons.spa.Plugin;
import hk.hku.cecid.piazza.commons.spa.PluginException;
import hk.hku.cecid.piazza.commons.spa.PluginHandler;

/**
 * @author Patrick Yee
 *
 */
public class ApiPlugin implements PluginHandler {

    private static ModuleGroup moduleGroup;

    public static SystemModule core;

    public void processActivation(Plugin plugin) throws PluginException {
        String mgDescriptor = plugin.getParameters().getProperty("module-group-descriptor");
        moduleGroup = new ModuleGroup(mgDescriptor, plugin.getClassLoader());
        Sys.getModuleGroup().addChild(moduleGroup);

        core = moduleGroup.getSystemModule();
        moduleGroup.startActiveModules();
        
        if (core == null) {
            throw new PluginException("API core system module not found");
        }
    }

    /* (non-Javadoc)
     * @see hk.hku.cecid.piazza.commons.spa.PluginHandler#processDeactivation(hk.hku.cecid.piazza.commons.spa.Plugin)
     */
    public void processDeactivation(Plugin arg0) throws PluginException {
        moduleGroup.stopActiveModules();
    }

    /**
     * @return the Api module group
     */
    public static ModuleGroup getModuleGroup() {
        if (moduleGroup == null) {
            throw new RuntimeException("API module group not initialized");
        }
        else {
            return moduleGroup;
        }
    }
}
