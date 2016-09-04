/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * ArrayUtilities is a convenient class for handling some common array
 * processing.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public final class ArrayUtilities {

    /**
     * Creates a new instance of ArrayUtilities.
     */
    private ArrayUtilities() {
    }

    /**
     * Returns the Class object array associated with the class or interface
     * with the given string name array.
     * 
     * @param classNames the fully qualified names of the desired classes.
     * @return the Class object array for the classes with the specified names.
     * @throws ClassNotFoundException
     */
    public static Class[] forNames(String[] classNames)
            throws ClassNotFoundException {
        int size = (classNames == null ? 0 : classNames.length);
        Class[] classes = new Class[size];
        for (int i = 0; i < classes.length; i++) {
            classes[i] = Class.forName(classNames[i]);
        }
        return classes;
    }

    /**
     * Returns the Class object array associated with the class or interface
     * with the given object array.
     * 
     * @param objs the objects of the desired classes.
     * @return the Class object array for the classes with the specified
     *         objects.
     */
    public static Class[] forObjects(Object[] objs) {
        int size = (objs == null ? 0 : objs.length);
        Class[] classes = new Class[size];
        for (int i = 0; i < classes.length; i++) {
            classes[i] = (objs[i] == null ? null : objs[i].getClass());
        }
        return classes;
    }

    public static Class[] toClasses(Object[] objs)
            throws ClassNotFoundException {
        Class[] classes;
        if (objs == null) {
            classes = new Class[]{};
        }
        else if (objs instanceof Class[]) {
            classes = (Class[]) objs;
        }
        else if (objs instanceof String[]) {
            classes = forNames((String[]) objs);
        }
        else {
            classes = forObjects(objs);
        }
        return classes;
    }

    /**
     * Tokenizes a string by the specified delimiter(s) and converts it into a
     * string array.
     * 
     * @param s the string to be tokenized.
     * @param delim the delimiter(s).
     * @return the tokenized strings as a string array.
     */
    public static String[] toArray(String s, String delim) {
        StringTokenizer stk = new StringTokenizer((s == null ? "" : s), delim);
        return (String[]) toArray(stk, new String[]{});
    }

    /**
     * Converts an iterator into an object array.
     * 
     * @param it the iterator to be converted.
     * @return an array representation of the iterator.
     */
    public static Object[] toArray(Iterator it) {
        return toArray(it, null);
    }

    /**
     * Converts an iterator into an object array.
     * 
     * @param it the iterator to be converted.
     * @param a the array into which the elements of this list are to be stored,
     *            if it is big enough; otherwise, a new array of the same
     *            runtime type is allocated for this purpose.
     * @return an array representation of the iterator.
     */
    public static Object[] toArray(Iterator it, Object[] a) {
        ArrayList array = new ArrayList();
        if (it != null) {
            while (it.hasNext()) {
                array.add(it.next());
            }
        }
        return a == null ? array.toArray() : array.toArray(a);

    }

    /**
     * Converts an enumeration into an object array.
     * 
     * @param enumeration the enumeration to be converted.
     * @return an array representation of the enumeration.
     */
    public static Object[] toArray(Enumeration enumeration) {
        return toArray(enumeration, null);
    }

    /**
     * Converts an enumeration into an object array.
     * 
     * @param enumeration the enumeration to be converted.
     * @param a the array into which the elements of this list are to be stored,
     *            if it is big enough; otherwise, a new array of the same
     *            runtime type is allocated for this purpose.
     * @return an array representation of the enumeration.
     */
    public static Object[] toArray(Enumeration enumeration, Object[] a) {
        ArrayList array = new ArrayList();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                array.add(enumeration.nextElement());
            }
        }
        return a == null ? array.toArray() : array.toArray(a);
    }
}