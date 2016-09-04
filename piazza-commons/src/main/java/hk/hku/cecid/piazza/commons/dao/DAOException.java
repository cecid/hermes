/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.dao;

/**
 * DAOException is a class representing the exception related to or thrown in
 * the DAO framework.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class DAOException extends hk.hku.cecid.piazza.commons.GenericException {

    /**
     * Creates a new DAOException.
     */
    public DAOException() {
        super();
    }

    /**
     * Creates a new instance of DAOException.
     * 
     * @param message the error message.
     */
    public DAOException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of DAOException.
     * 
     * @param cause the cause of this exception.
     */
    public DAOException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of DAOException.
     * 
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}