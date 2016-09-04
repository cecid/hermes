/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.net;

/**
 * ConnectionException represents all kinds of exception related to connectivity.
 * 
 * @author Hugo Y. K. Lam
 */
public class ConnectionException extends hk.hku.cecid.piazza.commons.GenericException {

    /**
     * Creates a new instance of ConnectionException.
     */
    public ConnectionException() {
        super();
    }

    /**
     * Creates a new instance of ConnectionException.
     * 
     * @param message the error message.
     */
    public ConnectionException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of ConnectionException.
     * 
     * @param cause the cause of this exception.
     */
    public ConnectionException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of ConnectionException.
     * 
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}