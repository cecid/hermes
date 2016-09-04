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
 * Messages is a common interface of a collection of messages.
 * 
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public interface Messages {

    /**
     * Gets a general message.
     * 
     * @param name the name of the message.
     * @return the message with the specified name.
     */
    public String getMessage(String name);

    /**
     * Gets a general message.
     * 
     * @param name the name of the message.
     * @param def a default message.
     * @return the message with the specified name.
     */
    public String getMessage(String name, String def);

    /**
     * Gets a warning message.
     * 
     * @param name the name of the message.
     * @return the message with the specified name.
     */
    public String getWarningMessage(String name);

    /**
     * Gets a warning message.
     * 
     * @param name the name of the message.
     * @param def a default message.
     * @return the message with the specified name.
     */
    public String getWarningMessage(String name, String def);

    /**
     * Gets an error message.
     * 
     * @param name the name of the message.
     * @return the message with the specified name.
     */
    public String getErrorMessage(String name);

    /**
     * Gets an error message.
     * 
     * @param name the name of the message.
     * @param def a default message.
     * @return the message with the specified name.
     */
    public String getErrorMessage(String name, String def);

    /**
     * Gets a message of the specified type.
     * 
     * @param name the name of the message.
     * @param def a default message.
     * @param type the type of the message.
     * @return the message with the specified name.
     */
    public String getMessage(String name, String def, String type);

    /**
     * Gets a message of the specified type and locale.
     * 
     * @param name the name of the message.
     * @param def a default message.
     * @param type the type of the message.
     * @param locale the locale of the message.
     * @return the message with the specified name.
     */
    public String getMessage(String name, String def, String type, String locale);

    /**
     * Gets the default locale of this messenger.
     * 
     * @return the default locale.
     */
    public String getDefaultLocale();

    /**
     * Sets the default locale of this messenger.
     * 
     * @param locale the default locale
     */
    public void setDefaultLocale(String locale);

    /**
     * Gets the default message of this messenger.
     * 
     * @return the default message.
     */
    public String getDefaultMessage();

    /**
     * Sets the default message of this messenger.
     * 
     * @param string the default message.
     */
    public void setDefaultMessage(String string);
}