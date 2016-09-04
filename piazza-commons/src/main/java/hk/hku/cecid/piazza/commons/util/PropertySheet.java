/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.util;

import java.util.Enumeration;
import java.util.Properties;

/**
 * PropertySheet is a common interface of a properties container.
 * 
 * @author Hugo Y. K. Lam
 * 
 */
public interface PropertySheet {

    /**
     * Gets a property with the specified key.
     * 
     * @param key the property key.
     * @return the property with the specified key.
     */
    public String getProperty(String key);

    /**
     * Gets a property with the specified key.
     * 
     * @param key the property key.
     * @param def the default value.
     * @return the property with the specified key.
     */
    public String getProperty(String key, String def);

    /**
     * Gets a list of properties with the specified key.
     * 
     * @param key the property key.
     * @return the properties with the specified key.
     */
    public String[] getProperties(String key);

    /**
     * Gets a two-dimensional list of properties with the specified keys.
     * The first key will define the first dimension of the list and 
     * the second key will define the second dimension.
     * 
     * @param key the first property key.
     * @param key2 the second property key.
     * @return a two-dimensional list of properties with the specified keys.
     */
    public String[][] getProperties(String key, String key2);

    /**
     * Creates a Properties object which stores the properties retrieved by the specified key.
     * 
     * @param key the property key.
     * @return a Properties object which stores the retrieved properties.
     */
    public Properties createProperties(String key);

    /**
     * Sets a property value with the specified key.
     * 
     * @param key the property key.
     * @param value the property value.
     * @return true if the operation is successful. false otherwise.
     */
    public boolean setProperty(String key, String value);

    /**
     * Removes a property with the specified key.
     * 
     * @param key the property key.
     * @return true if the operation is successful. false otherwise.
     */
    public boolean removeProperty(String key);

    /**
     * Gets all the existing property names.
     * 
     * @return all the existing property names.
     */
    public Enumeration propertyNames();

    /**
     * Checks if the specified key exists in this property sheet.
     * 
     * @param key the property key.
     * @return true if the specified key exists in this property sheet.
     */
    public boolean containsKey(String key);

    /**
     * Appends a property sheet to this property sheet.
     * 
     * @param p the property sheet to be appended.
     * @return true if the operation is successful. false otherwise.
     */
    public boolean append(PropertySheet p);

    /**
     * Loads the properties from a persistent storage.
     * 
     * @throws Exception if the operation is unsuccessful.
     */
    public void load() throws Exception;

    /**
     * Stores the properties to a persistent storage.
     * 
     * @throws Exception if the operation is unsuccessful.
     */
    public void store() throws Exception;
}