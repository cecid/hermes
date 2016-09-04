/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.ejb;

import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

/**
 * The AbstractEntityBean class is an abstract class which implements 
 * the methods required for an Entity Bean, except the ejbCreateXXX() 
 * methods which have been defined in the home interface. 
 *  
 * @author Hugo Y. K. Lam
 *  
 */
public class AbstractEntityBean implements EntityBean {

    /**
     * The entity context.
     */
    protected EntityContext context;

    /**
     *  Creates a new instance of AbstractEntityBean.
     */
    public AbstractEntityBean() {
        super();
    }

    /**
     * A container invokes this method when the instance is taken out of the pool of available instances to become associated with a specific EJB object. 
     * @see javax.ejb.EntityBean#ejbActivate()
     */
    public void ejbActivate() {
    }

    /**
     * A container invokes this method to instruct the instance to synchronize its state by loading it state from the underlying database. 
     * @see javax.ejb.EntityBean#ejbLoad()
     */
    public void ejbLoad() {
    }

    /**
     * A container invokes this method on an instance before the instance becomes disassociated with a specific EJB object. 
     * @see javax.ejb.EntityBean#ejbPassivate()
     */
    public void ejbPassivate() {
    }

    /**
     * A container invokes this method before it removes the EJB object that is currently associated with the instance.
     * @see javax.ejb.EntityBean#ejbRemove()
     */
    public void ejbRemove() {
    }

    /**
     * @see javax.ejb.EntityBean#ejbStore()
     */
    public void ejbStore() {
    }

    /**
     * Sets the associated entity context.
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(EntityContext ctx) {
        this.context = ctx;
    }

    /**
     * Unset the associated entity context.
     * @see javax.ejb.EntityBean#unsetEntityContext()
     */
    public void unsetEntityContext() {
        context = null;
    }
}