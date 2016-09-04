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
 * ActiveTask represents a task which can be executed under a separated thread.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public interface ActiveTask {

    /**
     * Executes this task.
     * 
     * @throws Exception if unable to carry out the task.
     */
    public void execute() throws Exception;

    /**
     * Indicates if retry should be enabled.
     * 
     * @return true if retry should be enabled.
     */
    public boolean isRetryEnabled();

    /**
     * Gets the retry interval in milliseconds.
     * 
     * @return the retry interval.
     */
    public long getRetryInterval();

    /**
     * Gets the maximum number of retries. 
     * 
     * @return the maximum number of retries.
     */
    public int getMaxRetries();

    /**
     * Sets the number of times this task has been retried.
     * 
     * @param retried the number of times this task has been retried.
     */
    public void setRetried(int retried);

    /**
     * Invoked when there is any exception thrown from the execution.
     * 
     * @param e the exception cause.
     */
    public void onFailure(Throwable e);

    /**
     * Invoked when the retry interval has passed and before execution.
     */
    public void onAwake();

    /**
     * Indicates if the task should succeed fast. false if this task should
     * be retried depending solely on the indication of isRetryEnabled(). 
     * 
     * @return true if the task should succeed fast.
     * 
     * @see #isRetryEnabled()
     */
    public boolean isSucceedFast();
}
