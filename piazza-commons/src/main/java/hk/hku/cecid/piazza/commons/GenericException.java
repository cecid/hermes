/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons;

import hk.hku.cecid.piazza.commons.util.StringUtilities;

/**
 * GenericException represents all kinds of non-runtime exception in the
 * application.
 * 
 * @author Hugo Y. K. Lam
 */
public class GenericException extends Exception {

    /**
     * Creates a new instance of GenericException.
     */
    public GenericException() {
        super();
    }

    /**
     * Creates a new instance of GenericException.
     * 
     * @param message the error message.
     */
    public GenericException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of GenericException.
     * 
     * @param cause the cause of this exception.
     */
    public GenericException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of GenericException.
     * 
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public GenericException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Returns a string representation of this exception.
     * 
     * @return a string representation of this exception.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return StringUtilities.toString(this);
    }
}