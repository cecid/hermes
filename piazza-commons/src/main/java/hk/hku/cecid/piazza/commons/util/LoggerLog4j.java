/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.util;

import hk.hku.cecid.piazza.commons.module.ComponentException;
import hk.hku.cecid.piazza.commons.module.PersistentComponent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * LoggerLog4j is an implementation of a Logger and is backed by a Log4j logger. 
 * 
 * @see org.apache.log4j.Logger
 * 
 * @author Hugo Y. K. Lam
 * 
 */
public class LoggerLog4j extends PersistentComponent 
    implements hk.hku.cecid.piazza.commons.util.Logger {

    private Logger logger;

    /**
     * Creates a new instance of LoggerLog4j. 
     */
    public LoggerLog4j() {
        super();
    }

    /**
     * Creates a new instance of LoggerLog4j.
     * 
     * @param url the url of the configuration file.
     * @throws ComponentException if the configuration could not be loaded from the specified url.
     */
    public LoggerLog4j(URL url) throws ComponentException {
        super(url);
    }

    /**
     * Creates a new instance of LoggerLog4j.
     *  
     * @param name the logger category name. null if the name should be looked up dynamically in logging.
     */
    public LoggerLog4j(String name) {
        this();
        setLogger(name);
    }

    /**
     * Creates a new instance of LoggerLog4j.
     *  
     * @param name the logger category name. null if the name should be looked up dynamically in logging.
     * @param url the url of the configuration file.
     * @throws UtilitiesException if the configuration could not be loaded from the specified url.
     */
    public LoggerLog4j(String name, URL url) throws Exception {
        this(url);
        setLogger(name);
    }

    /**
     * Initializes this logger and sets a default logger if specified.
     * 
     * @throws Exception if error occurred in initialization.
     * @see hk.hku.cecid.piazza.commons.module.Component#init()
     */
    protected void init() throws Exception {
        super.init();
        setLogger(getParameters().getProperty("category"));
    }
    
    /**
     * Sets the logger category name.
     * 
     * @param name the logger category name.
     */
    private void setLogger(String name) {
        if (name != null && !"".equals(name = name.trim())) {
            logger = Logger.getLogger(name);
        }
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#debug(java.lang.Object)
     */
    public void debug(Object msg) {
        (logger == null ? Logger.getLogger(Caller.getName()) : logger)
                .debug(msg);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#debug(java.lang.Object, java.lang.Throwable)
     */
    public void debug(Object msg, Throwable throwable) {
        (logger == null ? Logger.getLogger(Caller.getName()) : logger).debug(
                msg, throwable);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#error(java.lang.Object)
     */
    public void error(Object msg) {
        (logger == null ? Logger.getLogger(Caller.getName()) : logger)
                .error(msg);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#error(java.lang.Object, java.lang.Throwable)
     */
    public void error(Object msg, Throwable throwable) {
        (logger == null ? Logger.getLogger(Caller.getName()) : logger).error(
                msg, throwable);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#fatal(java.lang.Object)
     */
    public void fatal(Object msg) {
        (logger == null ? Logger.getLogger(Caller.getName()) : logger)
                .fatal(msg);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#fatal(java.lang.Object, java.lang.Throwable)
     */
    public void fatal(Object msg, Throwable throwable) {
        (logger == null ? Logger.getLogger(Caller.getName()) : logger).fatal(
                msg, throwable);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#warn(java.lang.Object)
     */
    public void warn(Object msg) {
        (logger == null ? Logger.getLogger(Caller.getName()) : logger)
                .warn(msg);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#warn(java.lang.Object, java.lang.Throwable)
     */
    public void warn(Object msg, Throwable throwable) {
        (logger == null ? Logger.getLogger(Caller.getName()) : logger).warn(
                msg, throwable);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#info(java.lang.Object)
     */
    public void info(Object msg) {
        (logger == null ? Logger.getLogger(Caller.getName()) : logger)
                .info(msg);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Logger#info(java.lang.Object, java.lang.Throwable)
     */
    public void info(Object msg, Throwable throwable) {
        (logger == null ? Logger.getLogger(Caller.getName()) : logger).info(
                msg, throwable);
    }

    /**
     * Checks if the logger itself is in debug mode.
     * 
     * @return true if the logger itself is in debug mode.
     */
    public boolean isDebugEnabled() {
        return (logger == null ? Logger.getLogger(Caller.getName()) : logger)
                .isDebugEnabled();
    }

    /**
     * Loads the configuration from the specified url location.
     * A DOM configuration will be triggered if the url ends with ".xml". 
     * 
     * @param url the url of the configuration source.
     * @throws Exception if the operation is unsuccessful. 
     * @see hk.hku.cecid.piazza.commons.module.PersistentComponent#loading(java.net.URL)
     */
    protected void loading(URL url) throws Exception {
        if (url.getPath().toLowerCase().endsWith(".xml")) {
			Properties params = getParameters();
			if (params != null) {
				String checkConfig = params.getProperty("checkConfig");
				
				if (checkConfig != null) {
					if (checkConfig.equals("true")) {
					
						SAXReader xmlReader = new SAXReader();
						xmlReader.setEntityResolver(new EntityResolver() {
							public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
								return new InputSource(new ByteArrayInputStream(new byte[0]));
							}
						});
						org.dom4j.Document doc = xmlReader.read(url);
						Node node = doc.selectSingleNode("/*[local-name()='configuration']/category/priority");
						String priority = node.valueOf("@value");
						if (!chechPriorityString(priority)) {
							throw new UtilitiesException("Log4j does not support the value for priority - "
									+ priority);
						}
						
						// Node node2 = doc.selectSingleNode("/*[local-name()='configuration']/root/priority");
						String priority2 = node.valueOf("@value");
						if (!chechPriorityString(priority2)) {
							throw new UtilitiesException("Log4j does not support the value for priority - "
									+ priority2);
						}
					}
				}
			}
			
            DOMConfigurator.configure(url);
        }
        else {
            PropertyConfigurator.configure(url);
        }
    }

	private boolean chechPriorityString(String nodeValue) {
		if (nodeValue.equals("all") || nodeValue.equals("debug") || 
				nodeValue.equals("info") || nodeValue.equals("warn") || 
				nodeValue.equals("error") || nodeValue.equals("fatal") || 
				nodeValue.equals("off") || nodeValue.equals("null") ) {
			return true;
		} else {
			return false;
		}
	}
}