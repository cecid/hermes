/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.servlet;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.util.Instance;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.UnavailableException;


/**
 * A StatefulServletContext represents a context of a stateful servlet.
 * A stateful servlet has two states, a halted state and a running state.
 * 
 * @see StatefulServletContextListener
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class StatefulServletContext {

    /**
     * Creates a new instance of StatefulServletContext. 
     */
    public StatefulServletContext() {
        super();
    }

    private Vector contextListeners = new Vector();
    private boolean halted = false;
    private boolean haltRequested = false;
    private int threadCount = 0;
    private String requestEncoding = null;
    private String responseEncoding = null;

    /**
     * Halts this context. The calling thread will be blocked until a lock on the
     * context has been acquired and no more threads are running under this
     * context. Nothing will be done if the servlet is already halted.
     * 
     * @return true if and only if this context is not halted or being halted.
     * 
     */
    public synchronized boolean halt() {
        if (!halted) {
            Sys.main.log.info(this.getClass().getName()+" is being halted");
            halted = true;
            haltRequested = true;
    
            while (threadCount > 0) {
                try {
                    wait();
                }
                catch (Exception e) {
                }
            }
    
            Iterator listeners = contextListeners.iterator();
            while (listeners.hasNext()) {
                StatefulServletContextListener listener = (StatefulServletContextListener) listeners
                        .next();
                try {
                    listener.servletContextHalted();
                }
                catch (Exception e) {
                    Sys.main.log.error("Error in invoking listener '"
                            + listener.getClass().getName()
                            + "' after context halted", e);
                }
            }
    
            haltRequested = false;
            Sys.main.log.info(this.getClass().getName()+" has been halted");
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Checks if the context is currently halted.
     * 
     * @return true if the servlet is currently halted.
     */
    public boolean isHalted() {
        return halted;
    }

    /**
     * Checks if the context is currently being halted.
     * 
     * @return true if the servlet is currently being halted.
     */
    public boolean isHalting() {
        return haltRequested;
    }

    /**
     * Gets the current count of threads running under this context.
     * 
     * @return the current count of threads running under this context.
     */
    public int getCurrentThreadCount() {
        return threadCount;
    }

    /**
     * Acquires a permission from this context. Any servlet which employs 
     * this stateful context must call this method before processing any requests. 
     * 
     * @throws UnavailableException if the context is currently halted.
     */
    public synchronized void acquire() throws UnavailableException {
        if (!halted) {
            threadCount++;
            return;
        }
        else {
            throw new UnavailableException("The servlet is halted", 0);
        }
    }

    /**
     * Releases a permission to this context. Any servlet which employs 
     * this stateful context must call this method after processing any requests.
     */
    public synchronized void release() {
        threadCount--;
        notify();
    }

    /**
     * Resumes this context. The calling thread will be blocked until a lock on
     * the context has been acquired. Nothing will be done if the servlet is not
     * halted.
     * 
     * @return true if and only if this context is halted and not being halted.
     */
    public synchronized boolean resume() {
        if (halted && !haltRequested) {
            Sys.main.log.info(this.getClass().getName()+" is being resumed");
            Iterator listeners = contextListeners.iterator();
            while (listeners.hasNext()) {
                StatefulServletContextListener listener = (StatefulServletContextListener) listeners
                        .next();
                try {
                    listener.servletContextResumed();
                }
                catch (Exception e) {
                    Sys.main.log.error("Error in invoking listener '"
                            + listener.getClass().getName()
                            + "' after context resumed", e);
                }
            }
            halted = false;
            Sys.main.log.info(this.getClass().getName()+" has been resumed");
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Adds a context listener for receiving events from this context.
     * 
     * @param contextListener the stateful servlet context listener.
     * @return true if the operation is successful, false otherwise.
     */
    public boolean addContextListener(Object contextListener) {
        try {
            StatefulServletContextListener listener = (StatefulServletContextListener) new Instance(
                    contextListener).getObject();
            contextListeners.addElement(listener);
            return true;
        }
        catch (Exception e) {
            Sys.main.log.error("Unable to add context listener '"
                    + contextListener + "'", e);
            return false;
        }
    }

    /**
     * Gets all the stateful servlet context listeners in this context. 
     * 
     * @return all the stateful servlet context listeners in this context.
     */
    public Collection getContextListeners() {
        return contextListeners;
    }
    
    /**
     * Gets the request encoding that the servlet should use. 
     * 
     * @return the request encoding.
     */
    public String getRequestEncoding() {
        return requestEncoding;
    }

    /**
     * Sets the request encoding that the servlet should use. 
     * 
     * @param requestEncoding the request encoding.
     */
    public void setRequestEncoding(String requestEncoding) {
        this.requestEncoding = requestEncoding;
    }

    /**
     * Gets the response encoding that the servlet should use. 
     * 
     * @return the response encoding.
     */
    public String getResponseEncoding() {
        return responseEncoding;
    }

    /**
     * Sets the response encoding that the servlet should use. 
     * 
     * @param responseEncoding the response encoding.
     */
    public void setResponseEncoding(String responseEncoding) {
        this.responseEncoding = responseEncoding;
    }

}
