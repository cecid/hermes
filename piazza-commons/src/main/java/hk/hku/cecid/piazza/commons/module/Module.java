/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.module;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.util.ConsoleLogger;
import hk.hku.cecid.piazza.commons.util.Instance;
import hk.hku.cecid.piazza.commons.util.Logger;
import hk.hku.cecid.piazza.commons.util.PropertyTree;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A Module is described by a module descriptor and contains zero to many
 * components. Each module has its own classloader for loading its components,
 * which are defined in the module descriptor, and its resources.
 * 
 * @see Component
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class Module extends Component {

    private ModuleGroup group;
    
    private final PropertyTree descriptor;

    private final ClassLoader  classLoader;

    private final Map          components;

    /**
     * Creates and initializes a new instance of Module.
     * 
     * @param descriptorLocation the module descriptor.
     * @throws ModuleException if errors encountered when loading the module
     *             descriptor.
     */
    public Module(String descriptorLocation) {
        this(descriptorLocation, true);
    }

    /**
     * Creates a new instance of Module.
     * 
     * @param descriptorLocation the module descriptor.
     * @param shouldInitialize true if the module should be initialized.
     * @throws ModuleException if errors encountered when loading the module
     *             descriptor.
     */
    public Module(String descriptorLocation, boolean shouldInitialize) {
        this(descriptorLocation, null, shouldInitialize);
    }

    /**
     * Creates and initializes a new instance of Module.
     * 
     * @param descriptorLocation the module descriptor.
     * @param loader the class loader for this module.
     * @throws ModuleException if errors encountered when loading the module
     *             descriptor.
     */
    public Module(String descriptorLocation, ClassLoader loader) {
        this(descriptorLocation, loader, true);
    }

    /**
     * Creates a new instance of Module.
     * 
     * @param descriptorLocation the module descriptor.
     * @param loader the class loader for this module.
     * @param shouldInitialize true if the module should be initialized.
     * @throws ModuleException if errors encountered when loading the module
     *             descriptor.
     */
    public Module(String descriptorLocation, ClassLoader loader,
            boolean shouldInitialize) {
        if (descriptorLocation == null) {
            throw new ModuleException("No module descriptor specified");
        }

        if (loader == null) {
            loader = getClass().getClassLoader();
        }

        URL resrc = getResource(descriptorLocation, loader);
        if (resrc == null) {
            throw new ModuleException("Module descriptor not found: "
                    + descriptorLocation);
        }

        try {
            this.classLoader = loader;
            this.components = Collections.synchronizedMap(new LinkedHashMap());
            this.descriptor = new PropertyTree(resrc);
            
            super.setModule(this);
            super.setId(getString("/module/@id"));
            super.setName(getString("/module/@name"));
            super.setParameters(descriptor.createProperties("/module/parameters/parameter"));            
            
            createComponents();
            if (shouldInitialize) {
                init();
            }
        }
        catch (Exception e) {
            throw new ModuleException("Unable to initialize module '"+getName()+"'", e);
        }
    }

    /**
     * Initializes the module and all its components.
     * 
     * @throws ModuleException if unable to initialize the module.
     */
    public void init() {
        initComponents();
        getLogger().info("Module '" + getName() + "' initialized successfully.");
    }

    /**
     * Creates all the components defined in the module descriptor.
     * 
     * @throws ModuleException if unable to create any component.
     */
    protected void createComponents() {
        // get all defined components
        String[] componentIds = descriptor
                .getProperties("/module/component/@id");

        for (int i = 0; i < componentIds.length; i++) {
            // retrieve the component information
            String id = componentIds[i];
            String key = "/module/component[@id='" + id + "']";
            String name = descriptor.getProperty(key + "/@name");
            String className = descriptor.getProperty(key + "/class");
            Properties parameters = descriptor.createProperties(key
                    + "/parameter");

            try {
                // create the component
                Instance instance = new Instance(className, classLoader);
                Component component = (Component) instance.getObject();
                component.setId(id);
                component.setName(name);
                component.setParameters(parameters);
                setComponent(component);
            }
            catch (Exception e) {
                throw new ModuleException("Unable to create module component '"
                        + name + "' of class " + className, e);
            }
        }
    }

    /**
     * Initializes all the created components.
     * 
     * @throws ModuleException if unable to initialize any component.
     */
    protected void initComponents() {
        Iterator orderedComponents = components.values().iterator();
        while (orderedComponents.hasNext()) {
            Component component = (Component) orderedComponents.next();
            try {
                component.init();
                getLogger().debug(
                        "Component '" + component.getName() + "' in module '"
                                + getName() + "' initialized successfully.");
            }
            catch (Exception e) {
                throw new ModuleException("Unable to initialize component '"
                        + component.getName() + "'", e);
            }
        }
    }

    /**
     * Gets the specified component in this module.
     * 
     * @param id the ID of the module component.
     * @return the module component.
     */
    public Component getComponent(String id) {
        if (id == null) {
            return null;
        }
        else {
            return (Component) components.get(id);
        }
    }
    
    /**
     * Gets all the components in this module.
     * 
     * @return all module components
     */
    public Collection getComponents() {
    	return components.values();
    }

    /**
     * Sets a component to this module.
     * 
     * @param component the component to be set.
     */
    public void setComponent(Component component) {
        String id = component.getId();
        if (component != null && id!=null) {
            component.setModule(this);
            components.put(id, component);
        }
    }

    /**
     * Gets the number of components in this module.
     * 
     * @return the number of components in this module.
     */
    public int getComponentCount() {
        return components.size();
    }
    
    /**
     * Gets the string value of the specified key from the module descriptor.
     * 
     * @param key the key in the resource bundle.
     * @return the string value.
     */
    protected String getString(String key) {
        try {
            return descriptor.getProperty(key).trim();
        }
        catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Get the mandatory parameter from the current module, throw ModuleException if not found.
     * 
     * @param key the key in the parameters list.
     * @return The value of the parameter if found.
     * @throws ModuleException if the parameter with <code>key</code> does not exist.
     */
    protected String getRequiredParameter(String key)
    {
    	if (key == null)
    	{
    		throw new NullPointerException("Missing 'key' in the arguments.");
    	}
    	    	
    	String value = super.getParameters().getProperty(key);
    	
    	if (value == null)
    	{
    		throw new ModuleException("Missing required parameter '" + key + "' in module:" + super.getId());
    	}
    	
    	return value;
    }
    
    
    /**
     * Gets a resource as stream.
     * 
     * @param name the name of the resource.
     * @return an input stream of the resource.
     * 
     * @see #getResource(String)
     */
    public InputStream getResourceAsStream(String name) {
        try {
            URL url = getResource(name);
            return url == null ? null : url.openStream();
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets a resource as URL.
     * 
     * @param name the name of the resource.
     * @return the URL of the resource.
     * 
     * @see #getResource(String, ClassLoader)
     */
    public URL getResource(String name) {
        return getResource(name, classLoader);
    }

    /**
     * Gets a resource as URL.
     * <p>
     * The specified name can be an absolute path or a relative path to the
     * current directory or classpaths.
     * <p>
     * If the specified name is a relative path, it will be searched through the
     * current directory and then the classpaths.
     * 
     * @param name the name of the resource.
     * @param loader the class loader for finding the resource.
     * @return the URL of the resource.
     */
    public static URL getResource(String name, ClassLoader loader) {
        if (name == null || "".equals((name = name.trim()))) {
            return null;
        }

        if (loader == null) {
            loader = Module.class.getClassLoader();
        }

        try {
            File f = new File(name);
            return f.exists() ? f.toURI().toURL() : loader.getResource(name);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets the class loader that this module uses to load classes.
     * 
     * @return the class loader for this module.
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Gets the module descriptor.
     * 
     * @return the module descriptor.
     */
    public URL getDescriptor() {
        return descriptor.getURL();
    }

    /**
     * Gets the logger of this module.
     * 
     * @return the logger of this module.
     */
    public Logger getLogger() {
        ModuleGroup group = getGroup();
        if (group != null) {
            SystemModule sysmod = group.getSystemModule();
            if (sysmod != null) {
                Logger logger = sysmod.getLogger();
                if (logger != null) {
                    return logger;
                }
            }
        }
        if (Sys.main == null || Sys.main.log == null) {
            return ConsoleLogger.getInstance();
        }
        else {
            return Sys.main.log;
        }
    }

    /**
     * Gets the version of this module.
     *
     * @return the version of this module.
     */
    public String getVersion() {
        return getString("/module/@version");
    }

    /**
     * Gets the module group to which this module belongs.
     * 
     * @return the module group.
     */
    public ModuleGroup getGroup() {
        return group;
    }
    
    /**
     * Sets the module group to which this module belongs.
     * 
     * @param group the module group.
     */
    public void setGroup(ModuleGroup group) {
        this.group = group;
    }

    /**
     * Returns a string representation of this module.
     * 
     * @return a string representation of this module.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Module: " + getName();
    }
}