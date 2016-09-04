/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.dao;

import hk.hku.cecid.piazza.commons.util.Convertor;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * The AbstractDVO, which implements the DVO interface, is simply a
 * convenient abstract class for managing the data of a DVO. It implemented
 * the methods of the DVO interface, provides some convenient methods and is
 * backed by a Hashtable.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public abstract class AbstractDVO implements DVO {

    private Hashtable data;
    
    private Set dirtyFields = Collections.synchronizedSet(new HashSet());

    /**
     * Creates a new instance of AbstractDVO.
     */
    protected AbstractDVO() {
        this(null);
    }

    /**
     * Creates a new instance of AbstractDVO.
     * 
     * @param data the source data of this DVO.
     */
    protected AbstractDVO(Hashtable data) {
        setData(data);
    }

    /**
     * Sets the source data of this DVO.
     * 
     * @param data the source data of this DVO.
     */
    public void setData(Hashtable data) {
        if (data != null) {
            this.data = data;
        }
        else {
            this.data = new Hashtable();
        }
    }
    
    /**
     * Gets the source data of this DVO.
     * 
     * @return the source data of this DVO.
     */
    public Hashtable getData() {
        return data;
    }
    
    /**
     * Gets the keys which reference the dirty values.
     * 
     * @return the keys referencing the dirty values.
     */
    public String[] getDirties() {
        return (String[])dirtyFields.toArray(new String[]{});
    }

    /**
     * Sets a single value to this DVO with a key as its reference.
     * 
     * @param key the key referencing the value to be set.
     * @param value the value object to be set to this DVO.
     * @return the previous value of the specified key in this hashtable, or
     *         null if it did not have one.
     */
    public Object put(Object key, Object value) {
        if (key != null) {
            dirtyFields.add(key);
            if (value == null) {
                return data.remove(key);
            }
            else {
                return data.put(key, value);
            }
        }
        else {
            return null;
        }
    }

    /**
     * Gets a value object back from this DVO by its key.
     * 
     * @param key the key referencing the value to be retrieved.
     * @return the value object retrieved by the specified key.
     */
    public Object get(Object key) {
        if (key == null) {
            return null;
        }
        else {
            return data.get(key);
        }
    }


    /**
     * Removes a value object from this DVO.
     * 
     * @param key the key referencing the value to be removed.
     * @return the value to which the key had been mapped in this DVO, or
     *         null if the key did not have a mapping.
     *  
     */
    public Object remove(Object key) {
        if (key == null) {
            return null;
        }
        else {
            return data.remove(key);
        }
    }

    /**
     * Sets a String value to this DVO.
     * 
     * @param key the key referencing the String value.
     * @param value the String value to be set.
     */
    public void setString(Object key, Object value) {
        if (value != null) {
            value = value.toString();
        }
        put(key, value);
    }

    /**
     * Gets a String value from this DVO by its key reference.
     * 
     * @param key the key referencing the String value.
     * @return the String value retrieved by the specified key.
     */
    public String getString(Object key) {
        Object obj = get(key);
        if (obj == null) {
            return null;
        }
        else {
            return obj.toString();
        }
    }

    /**
     * Retrieves the value by the specified key in this AbstractDVO as an
     * int. 
     * 
     * @param key the key referencing the value to be retrieved.
     * @return the value retrieved by the specified key or Integer.MIN_VALUE if 
     *         the underlying value is null.
     */
    public int getInt(Object key) {
        Object num = get(key);
        if (num instanceof String) {
            return Integer.parseInt((String)num);
        }
        else if (num instanceof Integer) {
            return ((Integer)num).intValue();
        }
        else if (num instanceof Number) {
            return ((Number)num).intValue();
        }
        else {
            return Integer.MIN_VALUE;
        }
    }

    /**
     * Sets an Integer value to this DVO.
     * 
     * @param key the key referencing the Integer value.
     * @param value the Integer value to be set.
     */
    public void setInt(Object key, int value) {
        put(key, new Integer(value));
    }
    
    /**
     * Retrieves the value by the specified key in this AbstractDVO as a
     * long.
     * 
     * @param key the key referencing the value to be retrieved.
     * @return the value retrieved by the specified key or Long.MIN_VALUE if 
     *         the underlying value is null.
     */
    public long getLong(Object key) {
        Object num = get(key);
        if (num instanceof String) {
            return Long.parseLong((String)num);
        }
        else if (num instanceof Long) {
            return ((Long)num).longValue();
        }
        else if (num instanceof Number) {
            return ((Number)num).longValue();
        }
        else {
            return Long.MIN_VALUE;
        }
    }

    /**
     * Sets a Long value to this DVO.
     * 
     * @param key the key referencing the Long value.
     * @param value the Long value to be set.
     */
    public void setLong(Object key, long value) {
        put(key, new Long(value));
    }
    
    /**
     * Retrieves the value by the specified key in this AbstractDVO as a
     * double.
     * 
     * @param key the key referencing the value to be retrieved.
     * @return the value retrieved by the specified key.
     */
    public double getDouble(Object key) {
        Object num = get(key);
        if (num instanceof String) {
            return Double.parseDouble((String)num);
        }
        else if (num instanceof Number) {
            return ((Number)num).doubleValue();
        }
        else {
            return Double.NaN;
        }
    }
    
    /**
     * Sets a Double value to this DVO.
     * 
     * @param key the key referencing the Double value.
     * @param value the Double value to be set.
     */
    public void setDouble(Object key, double value) {
        put(key, new Double(value));
    }
    
    /**
     * Retrieves the value by the specified key in this AbstractDVO as an
     * boolean.
     * 
     * @param key the key referencing the value to be retrieved.
     * @return the value retrieved by the specified key.
     */
    public boolean getBoolean(Object key) {
        Object obj = get(key);
        if (obj instanceof String) {
            return Boolean.parseBoolean((String)obj);
        } else if (obj instanceof Boolean) {
        	return ((Boolean)obj).booleanValue();
        }
        else {
            return false;
        }
    }
    

    /**
     * Sets a Boolean value to this DVO.
     * 
     * @param key the key referencing the Boolean value.
     * @param value the Boolean value to be set.
     */
    public void setBoolean(Object key, boolean value) {
        put(key, new Boolean(value));
    }
    
    /**
     * Gets an Object from this DVO by its referencing key. If the value
     * object is not of the Class specified, it will try to create an instance
     * of the Class with the Object as the parameter.
     * 
     * @param key The key referencing the String value.
     * @param c the Class of the returning Object.
     * @return the Object retrieved by its referencing key. If the value object
     *         is not of the Class specified, it will be an instance of the
     *         Class with the Object as the parameter. Null if the mentioned
     *         cannot be achieved.
     * @deprecated as Convertor.convertObject(Object, Class) is deprecated.       
     */
    public Object getObject(Object key, Class c) {
        return Convertor.convertObject(get(key), c);
    }

    /**
     * Retrieves the value by the specified key in this AbstractDVO as a
     * java.util.Date. 
     * 
     * @param key the key referencing the value to be retrieved.
     * @return the value retrieved by the specified key.
     */
    public java.util.Date getDate(Object key) {
    	return (java.util.Date)getTimestamp(key);
    }
    
    /**
     * Sets a java.util.Date and java.sql.Date value to this DVO.
     * 
     * 
     * @param key the key referencing the Date value.
     * @param obj the Date value to be set.
     */
    public void setDate(Object key, Object obj) {
    	if (obj == null)
    		return;
    	
    	if (obj instanceof java.util.Date)
    		put(key, new java.sql.Timestamp(((java.util.Date)obj).getTime()));
    	else if (obj instanceof java.sql.Date)
    		put(key, new java.sql.Timestamp(((java.sql.Date)obj).getTime()));
    	else 
    		put(key, (java.sql.Timestamp)obj);
    }

    /**
     * Retrieves the value by the specified key in this AbstractDVO as a
     * java.sql.Date.
     * 
     * @param key the key referencing the value to be retrieved.
     * @return the value retrieved by the specified key.
     * @deprecated replaced by getDate() 
     */
    public java.sql.Date getSQLDate(Object key) {
        return (java.sql.Date) getObject(key, java.sql.Date.class);
    }
    
    /**
     * Retrieves the value by the specified key in this AbstractDVO as a
     * java.sql.Timestamp.
     * It can transform java.sql.Date, java.util.Date and Oracle DATE and TIMESTAMP.
     * 
     * @param key the key referencing the value to be retrieved.
     * @return the value retrieved by the specified key.  
     */
    public java.sql.Timestamp getTimestamp(Object key) {
    	Object obj = get(key);
    	if (obj == null)
    		return null;
    	
    	if (obj instanceof java.sql.Timestamp)
    		return (java.sql.Timestamp)obj;
    	else if (obj instanceof java.sql.Date) {
    		return new java.sql.Timestamp(((java.sql.Date)obj).getTime());
    	}
    	else if (obj instanceof java.util.Date) {
    		return new java.sql.Timestamp(((java.util.Date)obj).getTime());
    	}
    	else if (obj.getClass().getName().equals("oracle.sql.DATE") || 
    			obj.getClass().getName().equals("oracle.sql.TIMESTAMP")) {
			try {
				Method m = obj.getClass().getDeclaredMethod("timestampValue", new Class[0]);
				return (java.sql.Timestamp)m.invoke(obj, new Object[0]);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
    	}
    	
    	return null;
    }

    /**
     * Indicates whether the given object is equal to this one.
     * The objects are equal if and only if the given object is an 
     * AbstractDVO object and the values contained in that object
     * match with this one's.
     * 
     * @param obj the object to be compared.
     * @return true if the given object is equal to this one.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof AbstractDVO) {
            AbstractDVO daoData = (AbstractDVO)obj;
            if (data.size() == daoData.data.size()) {
                Enumeration enumeration = data.keys();
                while (enumeration.hasMoreElements()) {
                    Object key = enumeration.nextElement();
                    Object value = data.get(key);
                    Object value2 = daoData.data.get(key);
                    if (value2 == null || !value.equals(value2)) {
                        return false;
                    }
                }
                return true;
            } 
        }
        return false;
    }
    
    /**
     * Returns a string representation of this AbstractDVO object in the
     * form of a set of entries.
     * 
     * @return a string representation of this AbstractDVO.
     */
    public String toString() {
        String s = this.getClass() + ":\n";
        Enumeration enumeration = data.keys();
        while (enumeration.hasMoreElements()) {
            Object key = enumeration.nextElement();
            s += key + "=" + data.get(key) + "\n";
        }
        return s;
    }
}