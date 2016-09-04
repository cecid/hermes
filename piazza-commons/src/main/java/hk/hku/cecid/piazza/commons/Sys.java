/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons;

import hk.hku.cecid.piazza.commons.module.ModuleException;
import hk.hku.cecid.piazza.commons.module.ModuleGroup;
import hk.hku.cecid.piazza.commons.module.SystemModule;

import java.io.InputStream;
import java.net.URL;

/**
 * Sys represents a system in an application and contains one system module,
 * which is described by a module descriptor. The descriptor location and the 
 * system module class will be looked up from a module group descriptor which is 
 * located by the following logic:
 * <ol>
 *  <li>Search for the Sys properties (sys.properties) in the classpaths of the 
 *      classloader used to load the Sys class.</li>
 *  <li>If the properties was not found, use the Java System Properties.</li>
 *  <li>Look for a property 'sys.module.group' for the system module group 
 *      descriptor location.</li>
 *  <li>If the property is not found, the location will be default to 
 *      'hk/hku/cecid/piazza/commons/conf/sys.module-group.xml'</li>
 * </ol>
 *
 * @see hk.hku.cecid.piazza.commons.module.Module
 * @see hk.hku.cecid.piazza.commons.module.ModuleGroup
 *  
 * @author Hugo Y. K. Lam
 *  
 */
public final class Sys {

    /**
     * The main system module.
     */
    public static final SystemModule main;

    private static final ModuleGroup group;
    
    static {
        try {
            java.util.Properties sysprops = new java.util.Properties();
            URL purl = Sys.class.getClassLoader().getResource("sys.properties");

            if (purl != null) {
                InputStream pins = purl.openStream();
                sysprops.load(pins);
                pins.close();
            }
            else {
                sysprops = System.getProperties();
            }

            String descriptor = sysprops.getProperty(
                    "sys.module.group", 
                    Sys.class.getPackage().getName().replace('.', '/') + 
                    "/conf/sys.module-group.xml");
            
            group = new ModuleGroup(descriptor);
            main = group.getSystemModule();
            
            if (main == null) {
                throw new ModuleException("System main module not defined");
            }
        }
        catch (Throwable e) {
            ModuleException re = new ModuleException(
                    "FATAL ERROR: Unable to initialize the system module group",
                    e);
            re.printStackTrace();
            throw re;
        }
    }
    
    /**
     * Gets the system module group.
     * 
     * @return the system module group.
     */
    public static ModuleGroup getModuleGroup() {
        return group;
    }
}