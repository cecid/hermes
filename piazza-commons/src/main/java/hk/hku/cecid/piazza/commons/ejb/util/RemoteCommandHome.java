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