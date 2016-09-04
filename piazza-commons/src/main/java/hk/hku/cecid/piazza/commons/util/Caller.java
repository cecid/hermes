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
 * A Caller represents the class calling the method which generates this Caller.
 * It retrieves information from the stack trace and is a relatively expensive
 * operation. As a result, it should be used only when there is an absolute need.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class Caller {

    private StackTraceElement callerElement;

    /**
     * Creates a new instance of Caller.
     */
    public Caller() {
        this(0);
    }
    
    /**
     * Creates a new instance of Caller.
     *
     * @param backSteps number of steps backward from the point of the immediate caller in the trace.
     */
    public Caller(int backSteps) {
        Exception e = new Exception();
        StackTraceElement[] trace = e.getStackTrace();

        for (int i = trace.length - 1; i >= 0; i--) {
            StackTraceElement element = trace[i];
            if (element.getClassName().equals(Caller.class.getName())) {
                int previ = i + 2 + backSteps;
                if (previ < trace.length) {
                    callerElement = trace[previ];
                }
                else {
                    callerElement = null;
                }
                break;
            }
        }
    }

    /**
     * Gets the name of the calling class.
     * 
     * @return the name of the calling class.
     */
    public static String getName() {
        return new Caller().getClassName();
    }

    /**
     * Checks if the calling class is undetermined. 
     * 
     * @return true if the calling class is undetermined.
     */
    public boolean isUnknown() {
        return callerElement == null;
    }

    /**
     * Gets the name of the calling class.
     * 
     * @return the name of the calling class.
     */
    public String getClassName() {
        return isUnknown() ? "" : callerElement.getClassName();
    }

    /**
     * Gets the file name of the calling class.
     * 
     * @return the file name of the calling class.
     */
    public String getFileName() {
        return isUnknown() ? "" : callerElement.getFileName();
    }

    /**
     * Gets the line number in the source file of the caller class at which the call is made.
     * 
     * @return the line number in the source file.
     */
    public int getLineNumber() {
        return isUnknown() ? -1 : callerElement.getLineNumber();
    }

    /**
     * Gets the method name of the method, which makes the call, in the caller class.
     *
     * @return the method name.
     */
    public String getMethodName() {
        return isUnknown() ? "" : callerElement.getMethodName();
    }

    /**
     * Returns a string representation of this Caller.
     *
     * @return a string representation of this Caller.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return isUnknown() ? "Unknown Caller" : getClassName() + "["
                + getLineNumber() + "]::" + getMethodName();
    }
}