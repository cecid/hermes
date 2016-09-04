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

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

/**
 * PropertyMap is an implementation of a PropertySheet.
 * It represents a property sheet with a map structure and
 * is actually backed by a Properties object. 
 * 
 * @see java.util.Properties
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class PropertyMap extends PersistentComponent implements PropertySheet {

    private Properties props;

    /**
     * Creates a new instance of PropertyMap.
     */
    public PropertyMap() {
        this((Properties)null);
    }

    /**
     * Creates a new instance of PropertyMap.
     * 
     * @param p the Properties object which backs this map. 
     */
    public PropertyMap(Properties p) {
        super();
        if (p == null) {
            props = new java.util.Properties();
        }
        else {
            props = p;
        }
    }

    /**
     * Creates a new instance of PropertyMap.
     * 
     * @param url the url of the properties source.
     * @throws ComponentException if the properties could not be loaded from the specified url.
     */
    public PropertyMap(URL url) throws ComponentException {
        super(url);
    }

    /**
     * Checks if the specified key exists in this property map.
     * 
     * @param key the property key.
     * @return true if the specified key exists in this property map.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#containsKey(java.lang.String)
     */
    public boolean containsKey(String key) {
        return props.containsKey(key);
    }

    /**
     * Gets a property with the specified key.
     * 
     * @param key the property key.
     * @return the property with the specified key.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#getProperty(java.lang.String)
     */
    public String getProperty(String key) {
        return props.getProperty(key);
    }

    /**
     * Gets a property with the specified key.
     * 
     * @param key the property key.
     * @param def the default value.
     * @return the property with the specified key.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#getProperty(java.lang.String,
     *      java.lang.String)
     */
    public String getProperty(String key, String def) {
        return props.getProperty(key, def);
    }

    /**
     * Gets a list of properties with the specified key.
     * 
     * @param keyPrefix the property key prefix.
     * @return the properties with the specified key.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#getProperties(java.lang.String)
     */
    public String[] getProperties(String keyPrefix) {
        String[] keys = getPropertyNames(keyPrefix);
        ArrayList values = new ArrayList();

        for (int i = 0; i < keys.length; i++) {
            String value = getProperty(keys[i]);
            if (value != null) {
                values.add(value);
            }
        }
        return (String[]) values.toArray(new String[] {});
    }

    /**
     * Gets a two-dimensional list of properties with the specified key prefix and key suffixes.
     * The key prefix, along with the suffixes, will define the first dimension of the list while 
     * the key suffixes will define the second dimension. E.g.
     * <p>
     * <pre>
     * # Properties content
     * application.listener1.id=MyListener
     * application.listener1.name=My Listener
     * application.listener2.id=MyListener2
     * application.listener2.name=My Listener 2
     * 
     * Key Prefix: application.listener
     * Key Suffixes: id,name
     * 
     * Note that the resulted array will be sorted alphabetically according to the original
     * keys but not the specified key suffixes order or the property values.
     * 
     * Returned array: 
     * {{"MyListener","My Listener"},{"MyListener2","My Listener 2"}} 
     * </pre>
     * </p>
     * 
     * @param keyPrefix the property key prefix.
     * @param keySuffixes the property key suffixes delimited by either ',', ';' or '|'.
     * @return a two-dimensional list of properties with the specified keys.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#getProperties(java.lang.String,
     *      java.lang.String)
     */
    public String[][] getProperties(String keyPrefix, String keySuffixes) {
        String[] keys = getPropertyNames(keyPrefix);
        Arrays.sort(keys);
        String[] suffixes = ArrayUtilities.toArray(keySuffixes, ",;| ");
        Arrays.sort(suffixes);

        ArrayList values = new ArrayList();
        String[] subValues = new String[suffixes.length];
        int preindex = Integer.MAX_VALUE;

        for (int i = 0; i < keys.length; i++) {
            int index = searchSuffix(suffixes, keys[i]);

            if (index > -1) {
                if (index <= preindex) {
                    subValues = new String[suffixes.length];
                    values.add(subValues);
                }

                subValues[index] = getProperty(keys[i]);
                preindex = index;
            }
        }
        return (String[][]) values.toArray(new String[][] {});
    }

    /**
     * Creates a Properties object which stores the properties retrieved by the specified key prefix.
     * 
     * @param keyPrefix the property key prefix.
     * @return a Properties object which stores the retrieved properties.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#createProperties(java.lang.String)
     */
    public Properties createProperties(String keyPrefix) {
        Properties newProps = new Properties();
        String[] keys = getPropertyNames(keyPrefix);
        int prefixLen = keyPrefix == null ? 0 : fixPrefix(keyPrefix).length();

        for (int i = 0; i < keys.length; i++) {
            String key = keys[i].substring(prefixLen, keys[i].length());
            String value = getProperty(keys[i]);
            if (value != null) {
                newProps.setProperty(key, value);
            }
        }
        return newProps;
    }

    /**
     * Gets all property names with the specified key prefix.
     * 
     * @param keyPrefix the property key prefix.
     * @return the property names with the specified key prefix.
     */
    protected String[] getPropertyNames(String keyPrefix) {
        keyPrefix = fixPrefix(keyPrefix);
        Enumeration keys = propertyNames();
        ArrayList names = new ArrayList();

        while (keys.hasMoreElements()) {
            String key = keys.nextElement().toString();
            if (key.startsWith(keyPrefix)) {
                names.add(key);
            }
        }
        return (String[]) names.toArray(new String[] {});
    }

    /**
     * Fixes a given prefix if it ends with an '*'.
     * 
     * @param keyPrefix the property key prefix.
     * @return the fixed key prefix.
     */
    private String fixPrefix(String keyPrefix) {
        if (keyPrefix != null && keyPrefix.endsWith("*")) {
            keyPrefix = keyPrefix.substring(0, keyPrefix.length() - 1);
        }
        return keyPrefix;
    }

    /**
     * Searches the index of a list of suffixes with which element the specified key ends.  
     * 
     * @param suffixes a list of suffixes to be searched.
     * @param key the property key.
     * @return the index if it is found. -1 otherwise. 
     */
    private int searchSuffix(String[] suffixes, String key) {
        for (int i = 0; i < suffixes.length; i++) {
            if (key.endsWith(suffixes[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Sets a property value with the specified key.
     * 
     * @param key the property key.
     * @param value the property value.
     * @return true if the operation is successful. false otherwise.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#setProperty(java.lang.String,
     *      java.lang.String)
     */
    public boolean setProperty(String key, String value) {
        props.setProperty(key, value);
        return true;
    }

    /**
     * Removes a property with the specified key.
     * 
     * @param key the property key.
     * @return true if the operation is successful. false otherwise.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#removeProperty(java.lang.String)
     */
    public boolean removeProperty(String key) {
        return props.remove(key) != null;
    }

    /**
     * Gets all the existing property names.
     * 
     * @return all the existing property names.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#propertyNames()
     */
    public Enumeration propertyNames() {
        return props.propertyNames();
    }

    /**
     * Appends a property sheet to this property map.
     * The specified property sheet can only be appended if it is of the PropertyMap type.
     * 
     * @param p the property sheet to be appended.
     * @return true if the operation is successful. false otherwise.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#append(hk.hku.cecid.piazza.commons.util.PropertySheet)
     */
    public boolean append(PropertySheet p) {
        if (p instanceof PropertyMap) {
            props.putAll(((PropertyMap) p).getProperties());
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Gets the Properties object which backs this property map.
     * 
     * @return the Properties object.
     */
    private Properties getProperties() {
        return props;
    }

    /**
     * Loads the properties from the specified url location.
     * 
     * @param url the url of the properties source.
     * @throws Exception if the operation is unsuccessful. 
     * @see hk.hku.cecid.piazza.commons.module.PersistentComponent#loading(java.net.URL)
     */
    protected void loading(URL url) throws Exception {
        InputStream ins = url.openStream();
        props = new Properties();
        props.load(ins);
        ins.close();
    }

    /**
     * Stores the properties to the specified url location.
     * 
     * @param url the url of the properties source.
     * @throws Exception if the operation is unsuccessful. 
     * @see hk.hku.cecid.piazza.commons.module.PersistentComponent#storing(java.net.URL)
     */
    protected void storing(URL url) throws Exception {
        FileOutputStream fos = new FileOutputStream(Convertor.toFile(url));
        props.store(fos, "Properties saved automatically by "
                + getClass().getName());
        fos.flush();
        fos.close();
    }
}