/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.dao.ds;

/**
 * NullableObject is a wrapper class for any object which has a corresponding 
 * SQL type. It indicates if the object wrapped is null and provide the type, 
 * java.sql.Types, information of the object.
 */
public class NullableObject {

    private int    type;

    private Object obj;

    /**
     * Creates a new instance of NullableObject.
     * 
     * @param o The object to be wrapped by this NullableObject.
     * @param type The type of the object to be wrapped.
     */
    public NullableObject(Object o, int type) {
        this.type = type;
        this.obj = o;
    }

    /**
     * Reports whether the wrapped object is null.
     * 
     * @return <CODE>true</CODE> if the object wrapped is null and <CODE>
     *         false</CODE> if it is not.
     */
    public boolean isNull() {
        return obj == null;
    }

    /**
     * Retrieves the type information of the object wrapped.
     * 
     * @return the type information of the object wrapped. -1 if not specified
     *         in the creation of this instance.
     */
    public int getType() {
        return type;
    }

    /**
     * Retrieves the object wrapped by this NullableObject.
     * 
     * @return the object wrapped.
     */
    public Object getObject() {
        return obj;
    }

    /**
     * Returns a string representation of the object wrapped.
     * 
     * @return a string representation of the object wrapped. 'null' if the
     *         object wrapped is null.
     */
    public String toString() {
        if (obj == null) {
            return "null";
        }
        else {
            return obj.toString();
        }
    }
}