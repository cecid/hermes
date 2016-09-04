/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

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