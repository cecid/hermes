/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.util;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.module.Module;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * Debugger is a convenient tool for logging different Java objects, 
 * like string, collection, map, and etc., on debugging purpose.
 * Since the logging of such objects may be expensive, the logging
 * will only be turned on when the debug flag in the system module 
 * attached to this debugger is on.   
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public final class Debugger {

    private static Module module = Sys.main;
    
    /**
     * Creates a new instance of Debugger.
     */
    private Debugger() {
    }

    /**
     * Attach a system module to this debugger. 
     * All debugging logs will be logged to the specified system module.
     * 
     * @param m the system module to be attached.
     */
    public static synchronized void attach(Module m) {
        if (module!=null) {
            module = m;
        }
    }
    
    /**
     * Detach a system module from this debugger. 
     * All debugging logs will be logged to the main system module.
     * 
     * @param m the system module to be detached.
     */
    public static synchronized void detach(Module m) {
        if (module==m) {
            module = Sys.main;
        }
    }
    
    /**
     * Prints debugging logs of the specified object.
     * If the given object is a collection set, like iterator and list,
     * its elements will be printed accordingly. 
     * 
     * @param obj the object to be printed.
     */
    public static void print(Object obj) {
        String caller = new Caller().toString();
        String objId = getObjectId(obj);

        print(caller, objId, obj);
    }

    /**
     * Prints debugging logs of the specified object.
     * If the given object is a collection set, like iterator and list,
     * its elements will be printed accordingly. 
     * 
     * @param caller the calling class.
     * @param objId the unique object ID.
     * @param obj the object to be printed.
     */
    private static void print(String caller, String objId, Object obj) {
        if (obj == null) {
            printDebug(caller, objId, "null");
        }
        else if (obj instanceof Collection) {
            printCollection(caller, objId, (Collection) obj);
        }
        else if (obj instanceof Map) {
            printMap(caller, objId, (Map) obj);
        }
        else if (obj instanceof Object[]) {
            printArray(caller, objId, (Object[]) obj);
        }
        else if (obj instanceof Enumeration) {
            printEnumeration(caller, objId, (Enumeration) obj);
        }
        else if (obj instanceof Iterator) {
            printIterator(caller, objId, (Iterator) obj);
        }
        else {
            printDebug(caller, objId, obj);
        }
    }

    /**
     * Prints debugging logs of an iterator.
     * 
     * @param caller the calling class.
     * @param objId the unique object ID.
     * @param it the iterator to be printed.
     */
    private static void printIterator(String caller, String objId, Iterator it) {
        printArray(caller, objId, ArrayUtilities.toArray(it));
    }

    /**
     * Prints debugging logs of an enumeration.
     * 
     * @param caller the calling class.
     * @param objId the unique object ID.
     * @param enumeration the enumeration to be printed.
     */
    private static void printEnumeration(String caller, String objId,
            Enumeration enumeration) {
        printArray(caller, objId, ArrayUtilities.toArray(enumeration));
    }

    /**
     * Prints debugging logs of a collection.
     * 
     * @param caller the calling class.
     * @param objId the unique object ID.
     * @param c the collection to be printed.
     */
    private static void printCollection(String caller, String objId,
            Collection c) {
        printArray(caller, objId, c.toArray());
    }

    /**
     * Prints debugging logs of a map.
     * 
     * @param caller the calling class.
     * @param objId the unique object ID.
     * @param m the map to be printed.
     */
    private static void printMap(String caller, String objId, Map m) {
        printCollection(caller, objId, m.entrySet());
    }

    /**
     * Prints debugging logs of an object array.
     * 
     * @param caller the calling class.
     * @param objId the unique object ID.
     * @param objs the object array to be printed.
     */
    private static void printArray(String caller, String objId, Object[] objs) {
        if (objs.length == 0) {
            printDebug(caller, objId, "Collection set is empty");
        }
        else {
            for (int i = 0; i < objs.length; i++) {
                String itemObjectId = objId + "[" + i + "]";
                print(caller, itemObjectId, objs[i]);
            }
        }
    }

    /**
     * Prints the debugging message.
     * 
     * @param caller the calling class.
     * @param objId the unique object ID.
     * @param msg the message to be printed.
     */
    private static void printDebug(String caller, String objId, Object msg) {
        if (module != null) {
            module.getLogger().debug(caller + " -- " + objId + ": " + msg);
        }
    }

    /**
     * Gets the unique ID of the specified object.
     * 
     * @param obj the object.
     * @return the unique object ID.
     */
    private static String getObjectId(Object obj) {
        if (obj == null) {
            return "NULL";
        }
        else if (obj instanceof String) {
            return "MESSAGE";
        }
        else {
            String name = obj.getClass().getName();
            int lastdot = name.lastIndexOf('.');
            if (lastdot != -1) {
                name = name.substring(lastdot + 1);
            }
            return name + "@" + Integer.toHexString(obj.hashCode());
        }
    }
}