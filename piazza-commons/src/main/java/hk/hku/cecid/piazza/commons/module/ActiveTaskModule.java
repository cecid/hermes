/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.module;

import hk.hku.cecid.piazza.commons.util.StringUtilities;

import java.util.Iterator;
import java.util.Properties;


/**
 * ActiveTaskModule is an active module which manages an active task list. As
 * an active module, it runs as a separated thread and loops through the task
 * list for executing the tasks. It also contains an active monitor responsible 
 * for monitoring and controlling the thread count.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class ActiveTaskModule extends ActiveModule {

    private ActiveMonitor monitor = new ActiveMonitor();
    private ActiveTaskList taskList;
    private int maxThreadCount = 1;
    private boolean isWaitForList;
    /**
     * Creates a new instance of ActiveTaskModule.
     * 
     * @param descriptorLocation the module descriptor.
     * @throws ModuleException when errors encountered when loading the module descriptor.
     */
    public ActiveTaskModule(String descriptorLocation) {
        super(descriptorLocation);
    }

    /**
     * Creates a new instance of ActiveTaskModule.
     * 
     * @param descriptorLocation the module descriptor.
     * @param shouldInitialize true if the module should be initialized.
     * @throws ModuleException when errors encountered when loading the module descriptor.
     */
    public ActiveTaskModule(String descriptorLocation, boolean shouldInitialize) {
        super(descriptorLocation, shouldInitialize);
    }

    /**
     * Creates a new instance of ActiveTaskModule.
     * 
     * @param descriptorLocation the module descriptor.
     * @param loader the class loader for this module.
     * @throws ModuleException when errors encountered when loading the module descriptor.
     */
    public ActiveTaskModule(String descriptorLocation, ClassLoader loader) {
        super(descriptorLocation, loader);
    }

    /**
     * Creates a new instance of ActiveTaskModule.
     * 
     * @param descriptorLocation the module descriptor.
     * @param loader the class loader for this module.
     * @param shouldInitialize true if the module should be initialized.
     * @throws ModuleException when errors encountered when loading the module descriptor.
     */
    public ActiveTaskModule(String descriptorLocation, ClassLoader loader,
            boolean shouldInitialize) {
        super(descriptorLocation, loader, shouldInitialize);
    }

    /**
     * Initializes this module by loading the active task list component named 
     * "task-list". The component may have the following parameters:
     * <ul>
     *   <li>max-thread-count: the maximum number of threads which can be acquired 
     *       to execute the tasks in the task list. A number smaller than 1 
     *       indicates the tasks should be run under the same thread as this 
     *       module.
     *   <li>wait-for-list: true if the tasks in the task list should be 
     *       completely executed before being refreshed.
     * </ul>
     * 
     * @see hk.hku.cecid.piazza.commons.module.Module#init()
     */
    public void init() {
        super.init();
        taskList = (ActiveTaskList)getComponent("task-list");
        Properties taskListParams = taskList.getParameters();
        maxThreadCount = StringUtilities.parseInt(taskListParams.getProperty("max-thread-count"), 0);
        isWaitForList = StringUtilities.parseBoolean(taskListParams.getProperty("wait-for-list"));
        monitor.setMaxThreadCount(maxThreadCount);
    }
    
    /**
     * Suspends the active monitor so that no more threads can be acquired for 
     * executing tasks. 
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveModule#stop()
     * @see hk.hku.cecid.piazza.commons.module.ActiveModule#onStop()
     */
    public void onStop() {
        getLogger().debug("Suspending active monitor in module ("+getName()+"). Current active threads: "+monitor.getThreadCount());
        monitor.suspend();
    }

    /**
     * Resumes the active monitor so that new threads can be acquired for 
     * executing tasks.
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveModule#start()
     * @see hk.hku.cecid.piazza.commons.module.ActiveModule#onStart()
     */
    public void onStart() {
        getLogger().debug("Resuming active monitor in module ("+getName()+"). Current active threads: "+monitor.getThreadCount());
        monitor.resume();
    }
    
    /**
     * Invoked by the start() method to start executing the managed task list 
     * and its tasks.
     * 
     * @return true if the active monitor of this module is not suspended.
     * @see hk.hku.cecid.piazza.commons.module.ActiveModule#execute()
     */
    public boolean execute() {
        if (taskList != null) {
            if (isWaitForList) {
                monitor.waitForEmpty();
            }
            Iterator tasks = taskList.getTaskList().iterator();
            while (tasks.hasNext()) {
                ActiveTask task = (ActiveTask) tasks.next();
                ActiveThread thread = null;
                try {
                    thread = monitor.acquireThread();
                    if (thread == null) {
                        return false;
                    }
                    thread.setTask(task);
                    thread.start();			

                } catch (Throwable e) {
                    monitor.releaseThread(thread);
                    getLogger().error("Error in executing active task", e);
                }
            }
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Gets the monitor of this module.
     * 
     * @return the monitor of this module.
     */
    public ActiveMonitor getMonitor() {
        return monitor;
    }
}
