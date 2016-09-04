/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.ejb.util;

/**
 * RemoteCommandHome is the home interface of RemoteCommand.
 * 
 * @author Hugo Y. K. Lam
 * 
 * @see RemoteCommand
 */
public interface RemoteCommandHome extends javax.ejb.EJBHome {

    /**
     * Creates a remote instance of RemoteCommand.
     * 
     * @return a RemoteCommand remote instance.
     * @throws javax.ejb.CreateException if the remote instance could not be created.
     * @throws java.rmi.RemoteException if there is a remote exception occurred.
     */
    public RemoteCommand create() throws javax.ejb.CreateException,
            java.rmi.RemoteException;
}