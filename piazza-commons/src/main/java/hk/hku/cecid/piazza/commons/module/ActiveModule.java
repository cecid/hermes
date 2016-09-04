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

import hk.hku.cecid.piazza.commons.util.StringUtilities;


/**
 * ActiveModule is a runnable module which runs as a separated thread after
 * started. Subclasses are expected to implement the run() method in the 
 * Runnable interface.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public abstract class ActiveModule extends Module implements Runnable {

    private Thread thread;

    private String groupExecution;
    private long executionInterval = 60000;
    private long stopTimeout;
    private boolean isStopping;

    /**
     * Creates a new instance of ActiveModule.
     * 
     * @param descriptorLocation the module descriptor.
     * @throws ModuleException if errors encountered when loading the module descriptor.
     */
    public ActiveModule(String descriptorLocation) {
        super(descriptorLocation);
    }

    /**
     * Creates a new instance of ActiveModule.
     * 
     * @param descriptorLocation the module descriptor.
     * @param shouldInitialize true if the module should be initialized.
     * @throws ModuleException if errors encountered when loading the module descriptor.
     */
    public ActiveModule(String descriptorLocation, boolean shouldInitialize) {
        super(descriptorLocation, shouldInitialize);
    }

    /**
     * Creates a new instance of ActiveModule.
     * 
     * @param descriptorLocation the module descriptor.
     * @param loader the class loader for this module.
     * @throws ModuleException if errors encountered when loading the module descriptor.
     */
    public ActiveModule(String descriptorLocation, ClassLoader loader) {
        super(descriptorLocation, loader);
    }

    /**
     * Creates a new instance of ActiveModule.
     * 
     * @param descriptorLocation the module descriptor.
     * @param loader the class loader for this module.
     * @param shouldInitialize true if the module should be initialized.
     * @throws ModuleException if errors encountered when loading the module descriptor.
     */
    public ActiveModule(String descriptorLocation, ClassLoader loader,
            boolean shouldInitialize) {
        super(descriptorLocation, loader, shouldInitialize);
    }

    /**
     * Checks if this module should be started by its group, if any.
     * 
     * @return true if this module should be started by its group.
     */
    public boolean isGroupStart() {
        return groupExecution.equalsIgnoreCase("all") ||
               groupExecution.equalsIgnoreCase("start");
    }
    
    /**
     * Checks if this module should be stopped by its group, if any.
     * 
     * @return true if this module should be stopped by its group.
     */
    public boolean isGroupStop() {
        return groupExecution.equalsIgnoreCase("all") ||
               groupExecution.equalsIgnoreCase("stop");
    }
    
    /**
     * Initializes this module by the following module parameters:
     * 
     * <ul> 
     *   <li>group-execution:    all - started and stopped by its group; 
     *                           start - only started by its group;
     *                           stop - only stopped by its group;
     *                           none - not started or stopped by its group.
     *   <li>execution-interval: the interval (milliseconds) that this module 
     *                           should wait until the next execution. A 
     *                           negative number indicates a one-time execution.
     *   <li>stop-timeout:       the maximum time (milliseconds) to wait for 
     *                           stopping this module.
     * </ul>
     */
    public void init() {
        super.init();
        Properties params = getParameters();
        groupExecution = params.getProperty("group-execution", "all");      
        executionInterval = StringUtilities.parseLong(params.getProperty("execution-interval"), -1);
        stopTimeout = StringUtilities.parseLong(params.getProperty("stop-timeout"), 0);
    }
    
    /**  
     * Set the execution interval in the module.
     *      
     */
    public void setExecutionInterval(long newInterval){
    	this.executionInterval = newInterval;
    }
    
    /**
     * Starts this module. This method will invoke onStart() before starting its
     * own thread.
     * 
     * @see java.lang.Thread#start()
     * @see #onStart()
     */
    public synchronized void start() {
        if (thread == null) {
            thread = new Thread(this);
            onStart();
            thread.start();
        }
    }

    /**
     * Stops this module. This method will invoke onStop() before waiting for 
     * its thread to die.
     * 
     * @see #waitForStop()
     * @see #onStop() 
     */
    public synchronized void stop() {
        if (thread != null) {
            if (thread.isAlive()) {
                isStopping = true;
                onStop();
                thread.interrupt();
                waitForStop();
                isStopping = false;
            }
            thread = null;
        }
    }
    
    /**
     * Waits for this module's thread to die.
     */
    public synchronized void waitForStop() {
        try {
            thread.join(stopTimeout);
        }
        catch (Exception e) {
            return;
        }
    }
    
    /**
     * Invoked when this module starts.
     * 
     * @see #start()
     */
    public void onStart() {
    }
    
    /**
     * Invoked when this module stops.
     * 
     * @see #stop()
     */
    public void onStop() {
    }
    
    /**
     * Gets the thread of this module.
     * 
     * @return the thread of this module.
     */
    public Thread getThread() {
        return thread;
    }
    
    /**
     * Invoked by the start() method and will continuously call the execute() 
     * method to carry out the execution. This method should not be invoked 
     * directly.
     * 
     * @see #start()
     * @see #execute()
     * @see java.lang.Runnable#run()
     */
    public void run() {
        do {
            if (!execute()) {
                break;
            }
            try {
                if (!isStopping && executionInterval>0) {
                    Thread.sleep(executionInterval);
                }
            } catch (InterruptedException e) {
            }
        }
        while (!isStopping && executionInterval>-1);
    }
    
    /**
     * Invoked by the run() method to execute this module's job.
     * 
     * @return true if this method should be invoked again after a defined interval.
     */
    public abstract boolean execute();
}
