package hk.hku.cecid.piazza.commons.module;

import hk.hku.cecid.piazza.commons.util.StringUtilities;

import java.util.Properties;

/**
 * A limited active task list is a active task list that 
 * can be set it's limit in the parameters list.<br><br>
 * 
 * But it has not effect on how big the active task list 
 * can return (no bound, only a limit value).<br><br>
 * 
 * Creation Date: 31/10/2006.
 * 
 * @author Twinsen
 * @version 1.0.0
 * @since	1.0.1
 */
public abstract class LimitedActiveTaskList extends ActiveTaskList {

	/**
	 * The field indicating how many tasks can be added to the task list 
	 * for each {@link #getTaskList()} invocation.
	 */
	private int maxTasksPerList;
	
	
	/**
     * Component Initialization.
     * 
     * @throws Exception
     */
    protected void init() throws Exception {
        super.init();
        Properties params = getParameters();
        this.maxTasksPerList =
    		StringUtilities.parseInt(
    				params.getProperty("max-task-per-list"), 500);
        
    }
    
	/**
	 * @return the maxTasksPerList
	 */
	public int getMaxTasksPerList() {
		return maxTasksPerList;
	}
	
	
	
	
}
