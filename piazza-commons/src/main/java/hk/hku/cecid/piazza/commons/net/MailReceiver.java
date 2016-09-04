/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 * 
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.net;

import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;

/**
 * MailReceiver is a mail connector for making connections to incoming mail servers.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class MailReceiver extends MailConnector {

    private static final String DEFAULT_PROTOCOL = "pop3";

    private Store store;

    /**
     * Creates a new instance of MailReceiver.
     * The default protocol is POP3.
     * 
     * @param host the mail host.
     * @param username the user name for authentication.
     * @param password the password for authentication.
     */
    public MailReceiver(String host, String username, String password) {
        super(DEFAULT_PROTOCOL, host, username, password);
    }

    /**
     * Creates a new instance of MailReceiver.
     * 
     * @param protocol the mail protocol.
     * @param host the mail host.
     * @param username the user name for authentication.
     * @param password the password for authentication.
     */
    public MailReceiver(String protocol, String host, String username, String password) {
        super(protocol, host, username, password);
    }

    /**
     * Connects to the incoming mail server.
     * 
     * @throws ConnectionException if unable to connect to the incoming mail server
     *                  or a connection to the server has already been established.
     */
    public synchronized void connect() throws ConnectionException {
        if (store == null) {
            try {
                Session session = createSession();
                store = session.getStore(getProtocol());
                store.connect();
            }
            catch (Exception e) {
                throw new ConnectionException("Unable to connect to incoming mail server", e);
            }
        }
        else {
            throw new ConnectionException("Connection to incoming mail server already established");
        }
    }
    
    /**
     * Disconnects from the incoming mail server.
     * 
     * @throws ConnectionException if unable to disconnect from the incoming mail server
     *                  or the connection to the server has not been established.
     */
    public synchronized void disconnect() throws ConnectionException {
        if (store == null) {
            throw new ConnectionException("Connection to incoming mail server has not been established");
        }
        else {
            try {
                store.close();
                store = null;
            }
            catch (Exception e) {
                throw new ConnectionException("Unable to disconnect from incoming mail server");
            }
        }
    }

    /**
     * Opens the inbox for read-write access.
     * 
     * @return the opened inbox.
     * @throws ConnectionException if unable to open the inbox 
     *              or the connection to the incoming mail server has not yet been established.
     */
    public synchronized Folder openInbox() throws ConnectionException {
        return openFolder("INBOX", false);
    }
    
    /**
     * Opens a specified folder for read-write access.
     * 
     * @param folderName the name of the folder to be opened.
     * @return the opened folder.
     * @throws ConnectionException if unable to open the specified folder 
     *              or the connection to the incoming mail server has not yet been established.
     */
    public synchronized Folder openFolder(String folderName) throws ConnectionException {
        return openFolder(folderName, false);
    }
    
    /**
     * Opens a specified folder.
     * 
     * @param folderName the name of the folder to be opened.
     * @param readOnly true if the folder should be readonly.
     * @return the opened folder.
     * @throws ConnectionException if unable to open the specified folder 
     *              or the connection to the incoming mail server has not yet been established.
     */
    public synchronized Folder openFolder(String folderName, boolean readOnly) throws ConnectionException {
        if (store == null) {
            throw new ConnectionException("Connection to incoming mail server not yet established");
        }
        try {
            Folder folder = store.getFolder(folderName);
            if (folder == null) {
                throw new ConnectionException("Folder '" + folderName
                        + "' not found");
            }
            else {
                folder.open(readOnly? Folder.READ_ONLY:Folder.READ_WRITE);
                return folder;
            }
        }
        catch (Exception e) {
            throw new ConnectionException("Unable to open folder: "+folderName, e);
        }
    }
}