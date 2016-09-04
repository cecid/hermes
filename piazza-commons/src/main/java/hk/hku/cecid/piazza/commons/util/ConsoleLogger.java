/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.util;

import hk.hku.cecid.piazza.commons.module.Component;

import java.io.PrintStream;


/**
 * ConsoleLogger is a logger which logs messages to System.out and System.err.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class ConsoleLogger extends Component implements Logger {

    private static ConsoleLogger instance = new ConsoleLogger();
    
    /**
     * Creates a new instance of ConsoleLogger.
     */
    public ConsoleLogger() {
        super();
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#debug(java.lang.Object)
     */
    public void debug(Object msg) {
        debug(msg, null);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#debug(java.lang.Object, java.lang.Throwable)
     */
    public void debug(Object msg, Throwable throwable) {
        log(System.out, "DEBUG", msg, throwable);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#error(java.lang.Object)
     */
    public void error(Object msg) {
        error(msg, null);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#error(java.lang.Object, java.lang.Throwable)
     */
    public void error(Object msg, Throwable throwable) {
        log(System.err, "ERROR", msg, throwable);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#fatal(java.lang.Object)
     */
    public void fatal(Object msg) {
        fatal(msg, null);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#fatal(java.lang.Object, java.lang.Throwable)
     */
    public void fatal(Object msg, Throwable throwable) {
        log(System.err, "FATAL", msg, throwable);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#warn(java.lang.Object)
     */
    public void warn(Object msg) {
        warn(msg, null);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#warn(java.lang.Object, java.lang.Throwable)
     */
    public void warn(Object msg, Throwable throwable) {
        log(System.out, "WARN", msg, throwable);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#info(java.lang.Object)
     */
    public void info(Object msg) {
        info(msg, null);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#info(java.lang.Object, java.lang.Throwable)
     */
    public void info(Object msg, Throwable throwable) {
        log(System.out, "INFO", msg, throwable);
    }

    /**
     * Logs the message to the specified print stream.
     * 
     * @param out the output print stream.
     * @param level the log level.
     * @param description the log description.
     * @param throwable the exception cause.
     */
    private void log(PrintStream out, String level, Object description, Throwable throwable) {
        out.println(level+": "+description);
        if (throwable != null) {
            throwable.printStackTrace(out);
        }
    }
    
    /**
     * Gets the default instance.
     * 
     * @return the default instance.
     */
    public static ConsoleLogger getInstance() {
        return instance;
    }
}
