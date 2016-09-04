/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.util;

/**
 * Logger is a common interface of a logger.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public interface Logger {

    /**
     * Logs a debug message.
     * 
     * @param msg the message to be logged.
     */
    public void debug(Object msg);

    /**
     * Logs a debug message.
     * 
     * @param msg the message to be logged.
     * @param throwable the associated exception.
     */
    public void debug(Object msg, Throwable throwable);

    /**
     * Logs an error message.
     * 
     * @param msg the message to be logged.
     */
    public void error(Object msg);

    /**
     * Logs an error message.
     * 
     * @param msg the message to be logged.
     * @param throwable the associated exception.
     */
    public void error(Object msg, Throwable throwable);

    /**
     * Logs a fatal error message.
     * 
     * @param msg the message to be logged.
     */
    public void fatal(Object msg);

    /**
     * Logs a fatal error message.
     * 
     * @param msg the message to be logged.
     * @param throwable the associated exception.
     */
    public void fatal(Object msg, Throwable throwable);

    /**
     * Logs a warning error message.
     * 
     * @param msg the message to be logged.
     */
    public void warn(Object msg);

    /**
     * Logs a warning error message.
     * 
     * @param msg the message to be logged.
     * @param throwable the associated exception.
     */
    public void warn(Object msg, Throwable throwable);

    /**
     * Logs an informative message.
     * 
     * @param msg the message to be logged.
     */
    public void info(Object msg);

    /**
     * Logs an informative message.
     * 
     * @param msg the message to be logged.
     * @param throwable the associated exception.
     */
    public void info(Object msg, Throwable throwable);
}