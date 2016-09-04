/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.module;

import hk.hku.cecid.piazza.commons.dao.DAOFactory;
import hk.hku.cecid.piazza.commons.util.Logger;
import hk.hku.cecid.piazza.commons.util.Messages;
import hk.hku.cecid.piazza.commons.util.PropertySheet;


/**
 * SystemModule is a module which contains some utility members which are 
 * common to a system.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class SystemModule extends Module {

    /**
     * The system logger, having a component name 'logger'.
     */
    public final Logger log = (Logger)getComponent("logger");
    
    /**
     * The system properties, having a component name 'properties'.
     */
    public final PropertySheet properties = (PropertySheet)getComponent("properties");
    
    /**
     * The system messages, having a component name 'messages'.
     */
    public final Messages messages = (Messages)getComponent("messages");
    
    /**
     * The system DAO factory, having a component name 'daofactory'.
     */
    public final DAOFactory dao = (DAOFactory)getComponent("daofactory");
    
    /**
     * Creates a new instance of SystemModule.
     * 
     * @param descriptorLocation the module descriptor.
     * @throws ModuleException if errors encountered when loading the module descriptor.
     */
    public SystemModule(String descriptorLocation) {
        super(descriptorLocation);
    }

    /**
     * Creates a new instance of SystemModule.
     * 
     * @param descriptorLocation the module descriptor.
     * @param shouldInitialize true if the module should be initialized.
     * @throws ModuleException if errors encountered when loading the module descriptor.
     */
    public SystemModule(String descriptorLocation, boolean shouldInitialize) {
        super(descriptorLocation, shouldInitialize);
    }
    
    /**
     * Creates a new instance of SystemModule.
     * 
     * @param descriptorLocation the module descriptor.
     * @param loader the class loader for this module.
     * @throws ModuleException if errors encountered when loading the module descriptor.
     */
    public SystemModule(String descriptorLocation, ClassLoader loader) {
        super(descriptorLocation, loader);
    }

    /**
     * Creates a new instance of SystemModule.
     * 
     * @param descriptorLocation the module descriptor.
     * @param loader the class loader for this module.
     * @param shouldInitialize true if the module should be initialized.
     * @throws ModuleException if errors encountered when loading the module descriptor.
     */
    public SystemModule(String descriptorLocation, ClassLoader loader,
            boolean shouldInitialize) {
        super(descriptorLocation, loader, shouldInitialize);
    }
    
    
    /**
     * @see hk.hku.cecid.piazza.commons.module.Module#getLogger()
     */
    public Logger getLogger() {
        if (log == null) {
            return super.getLogger();
        }
        else {
            return log;
        }
    }
}
