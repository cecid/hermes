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
