/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Vector;

/**
 * A Convertor is mainly responsible for data format conversion.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public final class Convertor {

    /**
     * Creates a new instance of Convertor.
     */
    private Convertor() {
    }

    /**
     * Converts the given source Object to an instance of the specified target
     * class. The constructor of the target class which has an argument of the
     * source object type will be looked up first. It it succeeds, the source
     * Object will be passed in as the argument. Otherwise, the constructor
     * which has a String argument will be looked up and the source object will
     * be passed in as a String by calling its toString method. Null will be
     * returned if the conversion cannot be performed.
     * 
     * @param source the source Object.
     * @param target the target class.
     * @return an instance of the target class representing the given source
     *         Object.
     * @deprecated may result unpredictable class casting problem and no error can be observed.           
     */
    public static Object convertObject(Object source, Class target) {
        if (source == null || target == null) {
            return null;
        }
        else if (target.isInstance(source)) {
            return source;
        }
        else {
            try {
                Constructor cons = getConstructor(target, source);
                if (cons == null) {
                    try {
                        return getConstructor(target, "").newInstance(
                                new Object[]{source.toString()});
                    }
                    catch (Exception e) {
                        return null;
                    }
                }
                else {
                    return cons.newInstance(new Object[]{source});
                }
            }
            catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Gets a constructor from the specified class which corresponds to the 
     * given constructor parameter.
     * 
     * @param c the target class.
     * @param para the constructor parameter.
     * @return the constructor or null if there is none available.
     * @deprecated as convertObject(Object, Class) is deprecated.
     */
    private static Constructor getConstructor(Class c, Object para) {
        if (c == null || para == null) {
            return null;
        }
        Class pClass = para.getClass();
        while (pClass != null) {
            try {
                return c.getConstructor(new Class[]{pClass});
            }
            catch (Exception e) {
                pClass = pClass.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Translates the request parameters in the given HttpServletRequest to the
     * properties in the given bean Object.
     * 
     * @param request the HttpServletRequest which contains the request
     *            parameters to be translated.
     * @param bean the bean Object into which the request parameters to be
     *            translated.
     */
    public static void translateProperties(
            javax.servlet.http.HttpServletRequest request, Object bean) {
        if (request == null || bean == null) {
            return;
        }

        Class beanClass = bean.getClass();

        java.util.Enumeration names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String paraName = names.nextElement().toString();
            String[] paraValues = request.getParameterValues(paraName);
            try {
                String methodName = "set"
                        + Character.toUpperCase(paraName.charAt(0))
                        + (paraName.length() > 1 ? paraName.substring(1) : "");
                try {
                    Method m = beanClass.getMethod(methodName,
                            new Class[]{String[].class});
                    m.invoke(bean, new Object[]{paraValues});
                }
                catch (Exception e) {
                    Method m = beanClass.getMethod(methodName,
                            new Class[]{String.class});
                    m.invoke(bean, new Object[]{paraValues[0]});
                }
            }
            catch (Exception e) {
                continue;
            }
        }
    }

    /**
     * Converts a java.net.URL into a java.io.File. 
     * 
     * @param url the java.net.URL to be converted.
     * @return a java.io.File representing the given URL.
     */
    public static File toFile(URL url) {
        if (url == null) {
            return null;
        }
        else {
            String path = url.getPath();
            try {
                path = URLDecoder.decode(path, "UTF-8");
            }
            catch (Exception e) {}
            return new File(path);
        }
    }

    /**
     * Converts a java.util.Date to a java.sql.Date.
     * 
     * @param date The java.util.Date to be converted.
     * @return a java.sql.Date representing the given date.
     */
    public static java.sql.Date toSQLDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        else {
            return new java.sql.Date(date.getTime());
        }
    }

    /**
     * Converts a java.util.Date to a java.sql.Timestamp.
     * 
     * @param date The java.util.Date to be converted.
     * @return a java.sql.Timestamp representing the given date.
     */
    public static java.sql.Timestamp toTimestamp(java.util.Date date) {
        if (date == null) {
            return null;
        }
        else {
            return new java.sql.Timestamp(date.getTime());
        }
    }

    /**
     * Converts the given Object to a Vector. If the given Object is already a
     * Vector, the Object will be directly returned. Otherwise, the Oobject will
     * be added to a new Vector and the Vector will be returned.
     * 
     * @param obj The Object to be converted.
     * @return The Vector converted by the given Object.
     */
    public static Vector toVector(Object obj) {
        Vector v;
        if (obj instanceof Vector) {
            v = (Vector) obj;
        }
        else {
            v = new Vector();
            if (obj != null) {
                v.addElement(obj);
            }
        }
        return v;
    }

    /**
     * Performs a deep copy of the given serializable Object.
     * 
     * @param obj the serializable Object to be copied.
     * @return a copy of the given Object.
     */
    public static Object deepCopy(Serializable obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            byte[] obis = bos.toByteArray();
            oos.close();
            bos.close();

            ByteArrayInputStream bis = new ByteArrayInputStream(obis);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object clone = ois.readObject();
            ois.close();
            bis.close();
            return clone;
        }
        catch (Exception e) {
            return new RuntimeException("Unable to deep copy object: "+obj, e);
        }
    }
}