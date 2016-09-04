/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.module;

import java.util.Vector;


/**
 * ActiveMonitor is a thread monitor of active threads.
 * 
 * @see ActiveThread
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class ActiveMonitor {

    private int maxThreadCount = 1;

    private int peekThreadCount = 0;
    
    private final Vector activeThreads = new Vector();

    private boolean suspended = false;
    
    /**
     * Creares a new instance of ActiveMonitor.
     */
    public ActiveMonitor() {
        super();
    }

    /**
     * Acquires a new active thread. This method will block if the maximum 
     * number of threads has been reached.
     * 
     * @return a new active thread or null if the monitor is suspended.
     */
    public synchronized ActiveThread acquireThread() {
        if (suspended) {
            throw new ModuleException("Active monitor has already suspended");
        }
        while (activeThreads.size() >= maxThreadCount) {
            try {
                wait();
            } catch (InterruptedException e) {}
            if (suspended) {
                return null;
            }
        }
        ActiveThread thread = new ActiveThread(this);
        activeThreads.add(thread);
        int curThreadCount = getThreadCount(); 
        if (curThreadCount > peekThreadCount) {
            peekThreadCount = curThreadCount; 
        }
        return thread;
    }

    /**
     * Releases a previously acquired thread.
     * 
     * @param thread the previously acquired thread.
     */
    public synchronized void releaseThread(ActiveThread thread) {
        if (thread == null) {
            return;
        } else if (activeThreads.remove(thread)) {
            notifyAll();
        }
    }

    /**
     * Gets the peek number of threads being acquired.
     * 
     * @return the peek number of threads being acquired.
     */
    public int getPeekThreadCount() {
        return peekThreadCount;
    }
    
    /**
     * Gets the current number of threads being acquired.
     * 
     * @return the current number of threads being acquired.
     */
    public int getThreadCount() {
        return activeThreads.size();
    }

    /**
     * Resumes this monitor.
     */
    public synchronized void resume() {
        if (suspended) {
            suspended = false;
        }
    }
    
    /**
     * Suspends this monitor.
     * 
     * @see #waitForEmpty()
     */
    public synchronized void suspend() {
        if (!suspended) {
            suspended = true;
            waitForEmpty();
        }
    }
    
    /**
     * Waits until all the acquired threads have been released. 
     */
    public synchronized void waitForEmpty() {
        while (activeThreads.size() > 0) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        notifyAll();
    }

    /**
     * Sets the maximum number of threads this monitor allows to acquire.
     * 
     * @param maxThreadCount the maximum number.
     */
    public void setMaxThreadCount(int maxThreadCount) {
        if (maxThreadCount > 0) {
            this.maxThreadCount = maxThreadCount;
        }
    }
    
    /**
     * Gets the maximum number of threads this monitor allows to acquire.
     * 
     * @return the maximum number.
     */
    public int getMaxThreadCount() {
        return maxThreadCount;
    }
}