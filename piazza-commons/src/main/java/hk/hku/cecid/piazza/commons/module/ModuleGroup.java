/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.module;

import hk.hku.cecid.piazza.commons.util.Instance;
import hk.hku.cecid.piazza.commons.util.PropertyTree;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


/**
 * ModuleGroup represents a group of modules and is capable of starting 
 * and stopping its active modules. A module group can have multiple system
 * modules and the first one defined in the module group descriptor will be 
 * treated as the default system module.  
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class ModuleGroup {

    private Map allModules;
    private List normalModules;
    private List systemModules;
    private List activeModules;
    private String name;
    private ClassLoader loader;
    private PropertyTree descriptor;
    
    private ModuleGroup parent;
    private Set children;
    
    /**
     * Creates a new instance of ModuleGroup. 
     * 
     * @param descriptorLocation the descriptor location.
     */
    public ModuleGroup(String descriptorLocation) {
        this(descriptorLocation, null);
    }
    
    /**
     * Creates a new instance of ModuleGroup.
     * 
     * @param descriptorLocation the descriptor location.
     * @param loader the class loader for loading the modules.
     */
    public ModuleGroup(String descriptorLocation, ClassLoader loader) {
        if (descriptorLocation == null) {
            throw new ModuleException("No module group descriptor specified");
        }

        if (loader == null) {
            loader = getClass().getClassLoader();
        }

        URL resrc = Module.getResource(descriptorLocation, loader);
        if (resrc == null) {
            throw new ModuleException("Module group descriptor not found: " 
                        + descriptorLocation);
        }
            
        try {
            this.loader = loader;
            this.allModules = Collections.synchronizedMap(new LinkedHashMap());
            this.normalModules = new Vector();
            this.systemModules = new Vector();
            this.activeModules = new Vector();
            this.children = Collections.synchronizedSet(new HashSet());
            this.descriptor = new PropertyTree(resrc);
            this.name = descriptor.getProperty("/module-group/@name");
            
            loadModules();
        }
        catch (Exception e) {
            throw new ModuleException("Unable to initialize module group '"+getName()+"'", e);
        }
    }

    /**
     * Loads and initializes the defined modules.
     */
    private void loadModules() {
        try {
            int ms = descriptor.countProperties("/module-group/module");
            for (int i=1; i<=ms; i++) {
                String mclass = descriptor.getProperty("/module-group/module["+i+"]/class");
                String mdesc  = descriptor.getProperty("/module-group/module["+i+"]/descriptor");
                
                Instance instance = new Instance(mclass, loader,
                    new Class[]{String.class, ClassLoader.class, Boolean.TYPE}, 
                    new Object[]{mdesc, loader, Boolean.FALSE});
                
                Module module = (Module)instance.getObject();
                module.setGroup(this);
                module.init();
                
                if (module instanceof ActiveModule) {
                    activeModules.add(module);
                }
                else if (module instanceof SystemModule) {
                    systemModules.add(module);
                }
                else {
                    normalModules.add(module);
                }
                
                if (module.getId() != null) {
                    allModules.put(module.getId(), module);
                }
            }
        }
        catch (Exception e) {
            throw new ModuleException("Unable to load modules", e);
        }
    }

    /**
     * Starts all active modules.
     */
    public void startActiveModules() {
        Iterator ms = activeModules.iterator();
        while (ms.hasNext()) {
            ActiveModule am = (ActiveModule)ms.next();
            if (am.isGroupStart()) {
                am.start();
            }
        }
    }

    /**
     * Stops all active modules.
     */
    public void stopActiveModules() {
        Iterator ms = activeModules.iterator();
        while (ms.hasNext()) {
            ActiveModule am = (ActiveModule)ms.next();
            if (am.isGroupStop()) {
                am.stop();
            }
        }
    }

    /**
     * Gets the default system module.
     * 
     * @return the default system module or null if there is none.
     */
    public SystemModule getSystemModule() {
        if (systemModules.size() > 0) {
            return (SystemModule)systemModules.get(0);
        }
        else {
            return null;
        }
    }
    
    /**
     * Gets all modules in this module group.
     * 
     * @return all modules in this module group.
     */
    public Collection getModules() {
        return allModules.values();
    }
    
    /**
     * Gets the specified module in this module group.
     * 
     * @param id the module ID.
     * @return the specified module. 
     */
    public Module getModule(String id) {
        if (id != null) {
            return (Module)allModules.get(id);
        }
        else {
            return null;
        }
    }
    
    /**
     * Gets the name of this module group.
     * 
     * @return the name of this module group.
     */
    public String getName() {
        return name == null? "unknown":name;
    }
    
    /**
     * Gets the parent module group.
     * 
     * @return the parent module group.
     */
    public ModuleGroup getParent() {
        return parent;
    }
    
    /**
     * Sets the parent module group.
     * 
     * @param parent the parent module group.
     */
    public void setParent(ModuleGroup parent) {
        this.parent = parent;
    }
    
    /**
     * Adds a child module group.
     * 
     * @param group the child module group.
     */
    public void addChild(ModuleGroup group) {
        if (group != null) {
            if (children.add(group)) {
                group.setParent(this);
            }
        }
    }
    
    public void removeChild(ModuleGroup group) {
        if (group != null) {
            if (children.remove(group)) {
                group.setParent(null);
            }
        }
    }
    
    /**
     * Gets the child module groups.
     * 
     * @return the child module groups.
     */
    public Collection getChildren() {
        return children;
    }
    
    /**
     * Returns a string representation of this module group.
     * 
     * @return a string representation of this module group.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Module Group ["+getName()+"]";
    }
}
