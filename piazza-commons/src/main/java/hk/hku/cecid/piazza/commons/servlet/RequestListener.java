package hk.hku.cecid.piazza.commons.servlet;

import java.util.Properties;

/**
 * RequestListener is a listener for handling servlet requests. 
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public interface RequestListener {

    /**
     * Invoked after the listener has been created and before 
     * it can handle any request. 
     * 
     * @throws RequestListenerException if the invoked process has errors.
     */
    public void listenerCreated() throws RequestListenerException;

    /**
     * Invoked after the listener has been taken out from service 
     * and before disposal.
     * 
     * @throws RequestListenerException if the invoked process has errors.
     */
    public void listenerDestroyed() throws RequestListenerException;
    
    
    /**
     * Gets the parameters of this listener.
     * 
     * @return the parameters of this listener.
     */
    public Properties getParameters();
}