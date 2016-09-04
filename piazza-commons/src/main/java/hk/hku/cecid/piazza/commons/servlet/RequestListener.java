/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

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