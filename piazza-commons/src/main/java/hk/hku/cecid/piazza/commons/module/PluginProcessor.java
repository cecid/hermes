package hk.hku.cecid.piazza.commons.module;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.dao.DAOFactory;
import hk.hku.cecid.piazza.commons.spa.Plugin;
import hk.hku.cecid.piazza.commons.spa.PluginException;
import hk.hku.cecid.piazza.commons.spa.PluginHandler;
import hk.hku.cecid.piazza.commons.util.Logger;
import hk.hku.cecid.piazza.commons.util.PropertySheet;

public abstract class PluginProcessor implements PluginHandler {
	
	protected abstract ModuleGroup getModuleGroupImpl();
	
	protected abstract void setModuleGroupImpl(ModuleGroup moduleGroup);
	
	/**
	 * @see hk.hku.cecid.piazza.commons.spa.PluginHandler#processActivation(Plugin)
	 * 
	 */
	public void processActivation(Plugin plugin) throws PluginException {
		try {
			String moduleDescriptor = plugin.getParameters().getProperty("module-group-descriptor");
			setModuleGroup(new ModuleGroup(moduleDescriptor, plugin.getClassLoader()));
		} catch (Exception e) {
			throw new PluginException("Unable to initialize plugin", e);
		}
	}
	
	/**
	 * @see hk.hku.cecid.piazza.commons.spa.PluginHandler#processDeactivation(Plugin)
	 * 
	 */
	public void processDeactivation(Plugin plugin) throws PluginException {
		try {
			getModuleGroup().stopActiveModules();
			Sys.getModuleGroup().removeChild(getModuleGroup());
		} catch (Exception e) {
            throw new PluginException("Unable to shutdown plugin", e);
        }
	}
	
	public ModuleGroup getModuleGroup() throws ModuleException {
		ModuleGroup moduleGroup = getModuleGroupImpl();
		if (moduleGroup == null)
			throw new ModuleException("Module group not found");
		return moduleGroup;
	}
		
	public void setModuleGroup(ModuleGroup moduleGroup) throws ModuleException {
		if (moduleGroup == null){
			throw new ModuleException("Module group not found");
		}
		setModuleGroupImpl(moduleGroup);
		
		Sys.getModuleGroup().addChild(moduleGroup);
		moduleGroup.startActiveModules();
	}
	
	public SystemModule getSystemModule() throws ModuleException {
		if (getModuleGroup().getSystemModule() == null)
			throw new ModuleException("System module not found");
		return getModuleGroup().getSystemModule();
	}
	
	public Component getSystemComponent(String id) throws ModuleException {
		Component com = (Component)getSystemModule().getComponent(id);
		if (com == null)
			throw new ModuleException("System component " + id + " not found");
		return com;
	}
	
	public Logger getLogger() {
		return (Logger) getSystemModule().getComponent("logger");
	}
	
	public DAOFactory getDAOFactory() {
		return (DAOFactory) getSystemModule().getComponent("daofactory");
	}
	
	public PropertySheet getProperties() {
		return (PropertySheet) getSystemModule().getComponent("properties");
	}
	
	public String getProperty(String key) {
		return getProperties().getProperty(key);
	}
	
	public String getProperty(String key, String def) {
		return getProperties().getProperty(key, def);
	}
	
	public String[] getProperties(String key) {
		return getProperties().getProperties(key);
	}
}
