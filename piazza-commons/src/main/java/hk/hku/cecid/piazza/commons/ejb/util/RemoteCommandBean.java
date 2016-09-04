/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.ejb.util;

import hk.hku.cecid.piazza.commons.ejb.AbstractSessionBean;
import hk.hku.cecid.piazza.commons.util.InstanceException;

import java.lang.reflect.InvocationTargetException;

/**
 * RemoteCommandBean is the session bean class of RemoteCommand.
 * It can execute a registered command with the given parameters.
 * 
 * @author Hugo Y. K. Lam
 * 
 * @see RemoteCommand
 * @see RemoteCommandHandler
 */
public class RemoteCommandBean extends AbstractSessionBean {

    /**
     * Executes a registered command with the given parameters.
     * 
     * @param cmdName the command name.
     * @param parameters parameters for the target method invocation.
     * @return the object returned by the invoked method.
     * @throws InstanceException if the instance of the target class cannot be
     *             created or the method could not be invoked.
     * @throws InvocationTargetException if the invoked method has thrown an
     *             exception.
     * @throws NullPointerException if the command is not registered.
     */
    public Object execute(String cmdName, Object[] parameters)
            throws InstanceException, InvocationTargetException {
        return RemoteCommandHandler.executeLocal(cmdName, parameters);
    }
}