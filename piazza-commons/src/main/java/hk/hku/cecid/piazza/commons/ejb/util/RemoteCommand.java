package hk.hku.cecid.piazza.commons.ejb.util;

import hk.hku.cecid.piazza.commons.util.InstanceException;

import java.lang.reflect.InvocationTargetException;

/**
 * RemoteCommand is the remote interface of RemoteCommandBean.
 * It can execute a remote command with the given parameters.
 * 
 * @author Hugo Y. K. Lam
 * 
 * @see RemoteCommandBean
 * @see RemoteCommandHandler
 */
public interface RemoteCommand extends javax.ejb.EJBObject {

    /**
     * Executes a registered command with the given parameters.
     * 
     * @param cmdName the command name.
     * @param parameters parameters for the target method invocation.
     * @return the object returned by the invoked method.
     * @throws java.rmi.RemoteException if there is a remote exception occurred.
     * @throws InstanceException if the instance of the target class cannot be
     *             created or the method could not be invoked.
     * @throws InvocationTargetException if the invoked method has thrown an
     *             exception.
     * @throws NullPointerException if the command is not registered.
     */
    public Object execute(String cmdName, Object[] parameters)
            throws java.rmi.RemoteException, InstanceException,
            InvocationTargetException;
}