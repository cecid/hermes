/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.module;

import java.net.URL;
import java.util.Properties;

/**
 * PersistentComponent represents a component that is persistent. Subclasses 
 * should override the loading(URL) and storing(URL) methods to provide their 
 * specific implementations.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public abstract class PersistentComponent extends Component {

    private URL url;

    /**
     * Creates a new instance of PersistentComponent.
     */
    public PersistentComponent() {
    }

    /**
     * Creates a new instance of PersistentComponent.
     * 
     * @param url the url representing this component.
     * @throws Exception when there is any error in loading the source.
     */
    public PersistentComponent(URL url) throws ComponentException {
        load(url);
    }

    /**
     * Loads this component from the specified url.
     * 
     * @param url the url representing this component.
     * @throws Exception when there is any error in loading.
     * 
     * @see #loading(URL)
     */
    public void load(URL url) throws ComponentException {
        try {
            if (url == null) {
                throw new ComponentException("No path specified");
            }
            loading(url);
            this.url = url;
        }
        catch (Throwable e) {
            throw new ComponentException("Unable to load from URL: "
                    + url, e);
        }
    }

    /**
     * Stores this component to the specified url.
     * 
     * @param url the url representing this component.
     * @throws Exception when there is any error in storing.
     * 
     * @see #storing(URL)
     */
    public void store(URL url) throws ComponentException {
        try {
            if (url == null) {
                throw new ComponentException("No path specified");
            }
            storing(url);
            this.url = url;
        }
        catch (Throwable e) {
            throw new ComponentException(
                    "Unable to store to URL: " + url, e);
        }
    }

    /**
     * Initializes this component from the URL specified in the parameter 
     * 'config'. The load() method will then be invoked to handle the loading of 
     * this component. 
     *
     * @see #load(URL)
     * @see Component#init()
     * @see Component#getParameters()
     */
    protected void init() throws Exception {
        super.init();
        Properties params = getParameters();
        if (params != null) {
            String config = params.getProperty("config");
            if (config != null) {
                try {
                    URL resrc = getModule().getResource(config);
                    if (resrc == null) {
                        throw new ComponentException("Resource not found: " + config);
                    }
                    else {
                        load(resrc);
                    }
                }
                catch (Exception e) {
                    throw new ComponentException("Unable to initialize "
                            + getClass().getName(), e);
                }
            }
        }
    }

    /**
     * Invoked by the load() method and should be overridden by subclasses to
     * provide implementation.
     * 
     * @see #load(URL)
     */
    protected void loading(URL url) throws Exception {
        throw new ComponentException("No implementation for loading");
    }

    /**
     * Invoked by the store() method and should be overridden by subclasses to
     * provide implementation.
     * 
     * @see #store(URL)
     */
    protected void storing(URL url) throws Exception {
        throw new ComponentException("No implementation for storing");
    }

    /**
     * Gets the url representing this component. 
     * 
     * @return the url representing this component.
     */
    public URL getURL() {
        return url;
    }

    /**
     * Loads this component from the URL representing it.
     * 
     * @throws ComponentException if unable to load this component from the URL.
     * @see #load(URL)
     */
    public void load() throws ComponentException {
        load(getURL());
    }

    /**
     * Stores this component to the URL representing it.
     * 
     * @throws ComponentException if unable to store this component from the URL.
     * @see #store(URL)
     */
    public void store() throws ComponentException {
        store(getURL());
    }
}