/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.ejb.util;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.ejb.EjbConnection;
import hk.hku.cecid.piazza.commons.ejb.EjbConnectionFactory;
import hk.hku.cecid.piazza.commons.util.ArrayUtilities;
import hk.hku.cecid.piazza.commons.util.Instance;
import hk.hku.cecid.piazza.commons.util.InstanceException;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * RemoteCommandHandler is a handler class for invoking the RemoteCommand bean. It
 * invokes the bean by using the command name and parameters specified by the 
 * caller. The given command must be registered beforehand on both side with a 
 * set of command properties in Java properties format:
 * <p>
 * <table border=1>
 * <tr>
 * <th>Key</th>
 * <th>Value</th>
 * </tr>
 * <tr>
 * <td>url</td>
 * <td>The connection URL (see EjbConnection)</td>
 * </tr>
 * <tr>
 * <td>username</td>
 * <td>The username for the connection</td>
 * </tr>
 * <tr>
 * <td>password</td>
 * <td>The password for the connection</td>
 * </tr>
 * <tr>
 * <td>class</td>
 * <td>The class name/object to be excuted</td>
 * </tr>
 * <tr>
 * <td>method</td>
 * <td>The method of the class to be excuted</td>
 * </tr>
 * <tr>
 * <td>parameters</td>
 * <td>The parameter types, separated by comma, of the method to be excuted</td>
 * </tr>
 * </table>
 * <p>
 * If it fails to invoke the bean, it will invoke the target class locally.
 * 
 * @author Hugo Y. K. Lam
 * 
 * @see RemoteCommandBean
 * @see EjbConnection
 */
public final class RemoteCommandHandler {

    private static Hashtable commands = new Hashtable();

    /**
     * Creates a new instance of RemoteCommandHandler.
     */
    private RemoteCommandHandler() {
        super();
    }

    /**
     * Register a command to this handler.
     * 
     * @param cmdName the command name.
     * @param command the command properties.
     */
    public static void register(String cmdName, Properties command) {
        if (cmdName != null) {
            if (command == null) {
                unregister(cmdName);
            }
            else {
                commands.put(cmdName, command);
            }
        }
    }

    /**
     * Unregister a command from this handler.
     * 
     * @param cmdName the command name.
     */
    public static void unregister(String cmdName) {
        if (cmdName != null) {
            commands.remove(cmdName);
        }
    }

    /**
     * Gets the names of the regsitered commands. 
     * 
     * @return the names of the regsitered commands.
     */
    public static Enumeration getCommandNames() {
        return commands.keys();
    }
    
    /**
     * Gets the command properties by its name.
     * 
     * @param name the command name.
     * @return the command properties.
     * @throws NullPointerException if the command is not registered.
     */
    public static Properties getCommand(String name) {
        Properties command = name == null? null : (Properties) commands.get(name);
        if (command == null) {
            throw new NullPointerException("Non-registered command: " + name);
        }
        else return command;
    }
    
    /**
     * Executes a registered command with the given parameters.
     * 
     * @param cmdName the command name.
     * @param parameters parameters for the target method invocation.
     * @return the object returned by the invoked method.
     * @throws RemoteException if there is a remote exception occurred.
     * @throws InstanceException if the instance of the target class cannot be
     *             created or the method could not be invoked.
     * @throws InvocationTargetException if the invoked method has thrown an
     *             exception.
     * @throws NullPointerException if the command is not registered.
     */
    public static Object execute(String cmdName, Object[] parameters)
            throws RemoteException, InstanceException,
            InvocationTargetException {

        Properties command = getCommand(cmdName);
        
        boolean useLocalClass = false;

        String url = command.getProperty("url");
        String username = command.getProperty("username");
        String password = command.getProperty("password");

        if (url == null || "".equals((url = url.trim()))) {
            useLocalClass = true;
        }
        else {
            try {
                InetAddress addr = InetAddress.getByName(url);
                useLocalClass = addr.isLoopbackAddress();
            }
            catch (UnknownHostException e) {
                useLocalClass = false;
            }
        }

        if (!useLocalClass) {
            RemoteCommand runner = null;
            try {
                EjbConnection conn = EjbConnectionFactory.createConnection(url);
                conn.connect(username, password);

                RemoteCommandHome home = (RemoteCommandHome) conn.lookupHome(
                        "java:comp/env/ejb/RemoteCommand", RemoteCommandHome.class);
                runner = home.create();
            }
            catch (Throwable e) {
                Sys.main.log.error("Unable to create remote RemoteCommand. Local instance will be invoked.", e);
                useLocalClass = true;
            }

            if (!useLocalClass) {
                Sys.main.log.debug("Using remote instance for command: "+cmdName);
                return runner.execute(cmdName, parameters);
            }
        }

        Sys.main.log.debug("Using local instance for command: "+cmdName);
        return executeLocal(cmdName, parameters);
    }

    /**
     * Executes a registered command locally with the given parameters.
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
    static Object executeLocal(String cmdName, Object[] parameters)
            throws InstanceException, InvocationTargetException {
        Properties command = getCommand(cmdName);
        Object clazz = command.get("class");
        String methodName = command.getProperty("method");
        String[] parameterTypeNames = ArrayUtilities.toArray(command
                .getProperty("parameters"), ", ");
        return new Instance(clazz).invoke(methodName, parameterTypeNames,
                parameters);
    }
}