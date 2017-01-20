package hk.hku.cecid.piazza.commons.module;

import java.util.List;


/**
 * ActiveTaskList is a module component which represents a collection of active
 * tasks.
 * 
 * @see ActiveTask
 * 
 * @author Hugo Y. K. Lam
 *
 */
public abstract class ActiveTaskList extends Component {

    /**
     * Gets the active task list.
     * 
     * @return the active task list.
     */
    public abstract List getTaskList();
}