/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.servlet.http;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.servlet.StatefulServletContext;
import hk.hku.cecid.piazza.commons.util.Instance;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

/**
 * An HttpDispatcherContext is a StatefulServletContext. Additionally
 * it manages the Http request listeners for the HttpDispatcher. 
 * 
 * @see HttpDispatcher
 * @see HttpRequestListener
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class HttpDispatcherContext extends StatefulServletContext {

    private static final Hashtable contexts = new Hashtable();

    private static HttpDispatcherContext defaultContext = new HttpDispatcherContext();
    
    /**
     * Adds an Http dispatcher context to the embeded context store.
     * 
     * @param id the ID of the Http dispatcher context.
     * @param context the Http dispatcher context.
     */
    public static void addContext(String id, HttpDispatcherContext context) {
        if (id!=null && context!=null) {
            contexts.put(id, context);
        }
    }
    
    /**
     * Gets an Http dispatcher context from the embeded context store.
     * 
     * @param id the ID of the Http dispatcher context.
     * @return the Http dispatcher context.
     */
    public static HttpDispatcherContext getContext(String id) {
        if (id == null) {
            return null;
        }
        else {
            return (HttpDispatcherContext)contexts.get(id);
        }
    }
    
    /**
     * Gets the default Http dispatcher context.
     * 
     * @return the default Http dispatcher context.
     */
    public static HttpDispatcherContext getDefaultContext() {
        return defaultContext;
    }
    
    private Hashtable requestListeners = new Hashtable();
    private Vector requestFilters = new Vector();

    /**
     * Creates a new instance of HttpDispatcherContext.
     */
    public HttpDispatcherContext() {
        super();
    }

    /**
     * Registers an Http request listener at a specified path which is relative to its
     * corresponding servlet's context path.
     * 
     * @param pathInfo the path information.
     * @param requestListener the Http request listener, the class name of the listener, or the class of the listener.
     * @return true if the operation is successful, false otherwise.
     */
    public boolean register(String pathInfo, Object requestListener) {
        return register(pathInfo, requestListener, null);
    }

    /**
     * Registers an Http request listener at a specified path which is relative to its
     * corresponding servlet's context path.
     * 
     * @param pathInfo the path information.
     * @param requestListener the Http request listener, the class name of the listener, or the class of the listener.
     * @param params the parameters of the listener.
     * @return true if the operation is successful, false otherwise.
     */
    public boolean register(String pathInfo, Object requestListener, Properties params) {
        try {
            HttpRequestListener listener = (HttpRequestListener) new Instance(
                    requestListener).getObject();

            if (params != null) {
                Properties listenerProps = listener.getParameters();
                if (listenerProps != null) {
                    listenerProps.putAll(params);
                }
            }
            
            if (!(requestListener instanceof HttpRequestListener)) {
                listener.listenerCreated();
            }

            pathInfo = fixPathInfo(pathInfo);
            requestListeners.put(pathInfo, listener);
            Sys.main.log.info("HTTP request listener '"
                    + listener.getClass().getName()
                    + "' registered successfully at '" + pathInfo + "'");

            return true;
        }
        catch (Exception e) {
            Sys.main.log.error("Unable to register listener '"
                    + requestListener + "' at '" + pathInfo + "'", e);
            return false;
        }
    }

    /**
     * Unregisters an Http request listener at a specified path which is relative to its
     * corresponding servlet's context path.
     * 
     * @param pathInfo the path information.
     * @return true if the operation is successful, false otherwise.
     */
    public boolean unregister(String pathInfo) {
        pathInfo = fixPathInfo(pathInfo);
        HttpRequestListener listener = (HttpRequestListener) requestListeners
                .remove(pathInfo);

        if (listener == null) {
            return false;
        }
        else {
            try {
                listener.listenerDestroyed();
            }
            catch (Exception e) {
                Sys.main.log.error("Error in destroying listener '"
                        + listener.getClass().getName() + "'", e);
            }
            Sys.main.log.info("HTTP request listener '"
                    + listener.getClass().getName()
                    + "' unregistered successfully at '" + pathInfo + "'");
            return true;
        }
    }

    /**
     * Unregisters all Http request listeners in this context.
     */
    public void unregisterAll() {
        Enumeration keys = requestListeners.keys();
        while (keys.hasMoreElements()) {
            unregister(keys.nextElement().toString());
        }
    }

    /**
     * Gets the Http request listener for the specified path which is relative to its
     * corresponding servlet's context path.
     * 
     * If there is an exact match on the specified path, the registered listener
     * will be returned. Else if there is a wildcard path which matches the
     * specified path, its corresponding registered listener will be returned.
     * Otherwise, null will be returned.
     * 
     * @param pathInfo the path information.
     * @return the HttpRequestListener registered at the specified path.
     */
    public HttpRequestListener getListener(String pathInfo) {
        pathInfo = fixPathInfo(pathInfo);
        HttpRequestListener listener = (HttpRequestListener) requestListeners
                .get(pathInfo);

        if (listener != null) {
            return listener;
        }
        else {
            String[] keys = (String[]) requestListeners.keySet().toArray(
                    new String[]{});
            Arrays.sort(keys);

            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i].indexOf('*') > -1) {
                    String srcPath = keys[i].endsWith("/*") ? (pathInfo + "/")
                            : pathInfo;
                    String pattern = keys[i].replaceAll("\\*", ".*");
                    if (srcPath.matches(pattern)) {
                        return (HttpRequestListener) requestListeners
                                .get(keys[i]);
                    }
                }
            }
            return null;
        }
    }

    /**
     * Gets the information of all registered Http request listeners.
     * The resulted properties will contain a set of pathInfo-listenerName pairs.
     * 
     * @return the information as properties.
     */
    public Properties getRegisteredListenersInfo() {
        Properties info = new Properties();
        Enumeration keys = requestListeners.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            String value = requestListeners.get(key).getClass().getName();
            info.setProperty(key.toString(), value);
        }
        return info;
    }

    /**
     * Gets the path information from the specified request. 
     * The path information will be fixed according to the 
     * internal logic of this context if necessary.
     * 
     * @param request the Http servlet request.
     * @return the path information.
     */
    public String getPathInfo(HttpServletRequest request) {
        return fixPathInfo(request.getPathInfo());
    }
    
    /**
     * Fixes a given path, if necessary, such that the path will conform to a
     * consistent format.
     * 
     * @param pathInfo the path information.
     * @return the fixed path.
     */
    private String fixPathInfo(String pathInfo) {
        pathInfo = pathInfo == null ? "/" : pathInfo.trim();

        if (!pathInfo.startsWith("/")) {
            pathInfo = "/" + pathInfo;
        }

        if (pathInfo.endsWith("/") && pathInfo.length() > 1) {
            pathInfo = pathInfo.substring(0, pathInfo.length() - 1);
        }

        return pathInfo;
    }
    
    /**
     * Adds a request filter for receiving request events.
     * 
     * @param requestFilter the request filter.
     * @return true if the operation is successful, false otherwise.
     */
    public boolean addRequestFilter(Object requestFilter) {
        try {
            HttpRequestFilter filter = (HttpRequestFilter) new Instance(
                    requestFilter).getObject();
            requestFilters.addElement(filter);
            return true;
        }
        catch (Exception e) {
            Sys.main.log.error("Unable to add HTTP request filter '"
                    + requestFilter + "'", e);
            return false;
        }
    }

    /**
     * Gets all the request filters in this context. 
     * 
     * @return all the request filters in this context.
     */
    public Collection getRequestFilters() {
        return requestFilters;
    }
}