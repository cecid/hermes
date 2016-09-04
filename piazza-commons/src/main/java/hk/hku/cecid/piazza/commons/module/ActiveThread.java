/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.module;


/**
 * ActiveThread is a thread which executes its associated task and manages the
 * retry of the task. An active thread may or may not be managed by an active 
 * monitor.
 * 
 * @see ActiveTask
 * @see ActiveMonitor
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class ActiveThread implements Runnable {

    private ActiveTask task;

    private ActiveMonitor monitor;

    private int retried = -1;

    /**
     * Creates a new instance of ActiveThread.
     */
    public ActiveThread() {
        super();
    }
    
    /**
     * Creates a new instance of ActiveThread.
     * 
     * @param monitor the active monitor from which this active thread is acquired. 
     */
    public ActiveThread(ActiveMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     * Sets the active task of this thread.
     * 
     * @param task the active task to be executed.
     */
    public void setTask(ActiveTask task) {
        this.task = task;
    }
    
    /**
     * Gets the active task of this thread.
     * 
     * @return the active task of this thread.
     */
    public ActiveTask getTask() {
        return task;
    }

    /**
     * Starts a new thread to execute the associated task.
     * 
     * @see #run() 
     */
    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }
    
    /**
     * Executes its associated task and manages the retry of the task.
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            if (task != null) {            
                int maxRetries = task.getMaxRetries();
                
                do {
                    boolean failed = false;
                    try {
                        task.setRetried(++retried);
                        task.execute();
                    } catch (Throwable e) {
                        failed = true;
                        try {
                            task.onFailure(e);
                        }
                        catch (Throwable t) {}
                    }
                    
                    if (task.isSucceedFast()) {
                        if (!failed || retried == maxRetries) {
                            return;
                        }
                    }
                    
                    if (task.isRetryEnabled()) {
                        if (task.getRetryInterval()>0) {
                            try {
                                Thread.sleep(task.getRetryInterval());
                            } catch (InterruptedException e1) {
                            }
                        }
                        task.onAwake();
                    }
                } while (task.isRetryEnabled() && maxRetries > retried);
            }
        }
        finally {
            if (monitor != null) {
                monitor.releaseThread(this);
            }
        }
    }
}
