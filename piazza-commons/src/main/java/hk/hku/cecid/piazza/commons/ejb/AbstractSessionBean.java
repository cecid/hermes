/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.ejb;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

/**
 * The AbstractSessionBean class is an abstract class which implements 
 * the methods required for a Session Bean, except the ejbCreate(...) methods 
 * and the business methods. 
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class AbstractSessionBean implements SessionBean {

    /**
     * The session context.
     */
    protected SessionContext context;

    /**
     *  Creates a new instance of AbstractSessionBean.
     */
    public AbstractSessionBean() {
        super();
    }

    /**
     * A container invokes this method when the instance is created.
     * This is a default and mandatory creator in a stateless session bean. 
     * @see javax.ejb.SessionBean#ejbActivate()
     */
    public void ejbCreate() {
    }

    /**
     * A container invokes this method when the instance is activated from its "passive" state. 
     * @see javax.ejb.SessionBean#ejbActivate()
     */
    public void ejbActivate() {
    }

    /**
     * A container invokes this method before the instance enters the "passive" state. 
     * @see javax.ejb.SessionBean#ejbPassivate()
     */
    public void ejbPassivate() {
    }

    /**
     * A container invokes this method before it ends the life of the session object. 
     * @see javax.ejb.SessionBean#ejbRemove()
     */
    public void ejbRemove() {
    }

    /**
     * Set the associated session context.
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx) {
        context = ctx;
    }

}