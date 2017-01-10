package hk.hku.cecid.piazza.commons.servlet;

/**
 * StatefulServletContextListener is a listener 
 * for listening the events from a stateful servlet context.
 * 
 * @see StatefulServletContext
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public interface StatefulServletContextListener {

    /**
     * Invoked when the context it is listening has been halted.
     */
    public void servletContextHalted();

    /**
     * Invoked when the context it is listening has been resumed.
     */
    public void servletContextResumed();
}