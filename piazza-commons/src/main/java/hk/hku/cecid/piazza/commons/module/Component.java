/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.module;

import java.util.Properties;

/**
 * Component represents a module component. Subclasses should 
 * override the init() method for their own initializations.
 * 
 * @see Module
 * 
 * @author Hugo Y. K. Lam
 * 
 */
public abstract class Component {

    private Module module;
    
    private String name;
    
    private Properties parameters;
    
    private String id; 
    
    /**
     * Gets the component ID.
     * 
     * @return the component ID.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Sets the component ID.
     * 
     * @param id the component ID.
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Gets the name of this component.
     * 
     * @return the name of this component.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of this component.
     * 
     * @param name the name of this component.
     */
    protected void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the parent module of this component.
     * 
     * @return the parent module of this component. 
     */
    public Module getModule() {
        return module;
    }
    
    /**
     * Sets the parent module of this component.
     * 
     * @param module the parent module of this component.
     */
    protected void setModule(Module module) {
        this.module = module;
    }
    
    /**
     * Gets the parameters of this component.
     * 
     * @return the parameters of this component.
     */
    public Properties getParameters() {
        return parameters;
    }
    
    /**
     * Sets the parameters of this component.
     * 
     * @param parameters the parameters of this component.
     */
    protected void setParameters(Properties parameters) {
        this.parameters = parameters;
    }
    
    /**
     * Invoked for initialization.
     * 
     * @throws Exception if there is any error in the initialization.
     */
    protected void init() throws Exception {
    }
}