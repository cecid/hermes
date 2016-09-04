/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.ejb;

import javax.ejb.MessageDrivenContext;


/**
 * The AbstractMessageDrivenBean class is an abstract class which implements 
 * the methods required for a Message Driven Bean, excepts the onMessage() method.
 *
 * @author Hugo Y. K. Lam
 *
 */
public abstract class AbstractMessageDrivenBean implements javax.ejb.MessageDrivenBean, javax.jms.MessageListener {
    
    protected MessageDrivenContext mdc;
    
    /** 
     * A container invokes this method before it begins the life of the message-driven object.
     */ 
    public void ejbCreate() {}
    
    /** 
     * Sets the associated message-driven context. The container calls this method after the instance creation.
     * @param mdc A MessageDrivenContext interface for the instance.
     */ 
    public void setMessageDrivenContext(MessageDrivenContext mdc) {
        this.mdc = mdc;
    }
    
    /** 
     * A container invokes this method before it ends the life of the message-driven object.
     */ 
    public void ejbRemove() {
    }
}
