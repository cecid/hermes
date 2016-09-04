/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

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