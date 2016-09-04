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

import java.net.URL;

/**
 * MessagesTree is an implementation of a Messenger.
 * It represents a messenger with a tree structure and
 * is actually backed by a PropertiesTree object. 
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class MessagesTree extends PersistentComponent implements Messages {

    private PropertyTree messages;

    private String defaultMessage = null;

    private String defaultLocale = null;

    /**
     * Creates a new instance of MessagesTree. 
     */
    public MessagesTree() {
        super();
        messages = new PropertyTree();
    }

    /**
     * Creates a new instance of MessagesTree.
     *  
     * @param url the url of the messenger properties source.
     * @throws ComponentException if the properties could not be loaded from the specified url.
     */
    public MessagesTree(URL url) throws ComponentException {
        super(url);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Messages#getMessage(java.lang.String)
     */
    public String getMessage(String name) {
        return getMessage(name, getDefaultMessage());
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Messages#getMessage(java.lang.String, java.lang.String)
     */
    public String getMessage(String name, String def) {
        return getMessage(name, def, "general");
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Messages#getWarningMessage(java.lang.String)
     */
    public String getWarningMessage(String name) {
        return getWarningMessage(name, getDefaultMessage());
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Messages#getWarningMessage(java.lang.String, java.lang.String)
     */
    public String getWarningMessage(String name, String def) {
        return getMessage(name, def, "warn");
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Messages#getErrorMessage(java.lang.String)
     */
    public String getErrorMessage(String name) {
        return getErrorMessage(name, getDefaultMessage());
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Messages#getErrorMessage(java.lang.String, java.lang.String)
     */
    public String getErrorMessage(String name, String def) {
        return getMessage(name, def, "error");
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Messages#getMessage(java.lang.String, java.lang.String, java.lang.String)
     */
    public String getMessage(String name, String def, String type) {
        return getMessage(name, def, type, null);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Messages#getMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public String getMessage(String name, String def, String type, String locale) {
        if (locale == null) {
            locale = getDefaultLocale();
        }
        String xpath = "/messenger/messages/" + type + "/message[@name='"
                + name + "']/localized-message[starts-with(@locale,'" + locale
                + "')]";
        return messages.getProperty(xpath, def);
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Messages#getDefaultLocale()
     */
    public String getDefaultLocale() {
        return defaultLocale == null ? messages.getProperty(
                "/messenger/locale", java.util.Locale.getDefault()
                        .getLanguage()) : defaultLocale;
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Messages#setDefaultLocale(java.lang.String)
     */
    public void setDefaultLocale(String locale) {
        defaultLocale = locale;
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Messages#getDefaultMessage()
     */
    public String getDefaultMessage() {
        return defaultMessage == null ? messages.getProperty(
                "/messenger/messages/default[@locale='" + getDefaultLocale()
                        + "']", "") : defaultMessage;
    }

    /**
     * @see hk.hku.cecid.piazza.commons.util.Messages#setDefaultMessage(java.lang.String)
     */
    public void setDefaultMessage(String string) {
        defaultMessage = string;
    }

    /**
     * Loads the messenger properties from the specified url location.
     * 
     * @param url the url of the messenger properties source.
     * @throws Exception if the operation is unsuccessful. 
     * @see hk.hku.cecid.piazza.commons.module.PersistentComponent#loading(java.net.URL)
     */
    protected void loading(URL url) throws Exception {
        messages = new PropertyTree();
        messages.loading(url);
    }
}