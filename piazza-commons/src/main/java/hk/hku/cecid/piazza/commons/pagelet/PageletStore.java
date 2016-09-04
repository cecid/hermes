/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.pagelet;

import java.util.Hashtable;
import java.util.Map;

/**
 * A PageletStore is a holder which stores a set of pagelets for a certain 
 * context.
 * 
 * @see Pagelet
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class PageletStore {

    private Map store = new Hashtable();
    
    /**
     * Creates a new instance of PageletStore. 
     */
    public PageletStore() {
        super();
    }
    
    /**
     * Adds a pagelet to this store.
     * 
     * @param pagelet the pagelet to be added.
     * @return the previous pagelet, if any, which has the same ID with the 
     *         given pagelet.
     */
    public Pagelet addPagelet(Pagelet pagelet) {
        if (pagelet == null) {
            return null;
        }
        else {
            return (Pagelet)store.put(pagelet.getId(), pagelet);
        }
    }
    
    /**
     * Removes a pagelet from this store.
     * 
     * @param id the pagelet ID. 
     * @return the removed pagelet, if any.
     */
    public Pagelet removePagelet(String id) {
        if (id == null) {
            return null;
        }
        else {
            return (Pagelet)store.remove(id);
        }
    }
    
    /**
     * Retrieves a pagelet from this store.
     * 
     * @param id the pagelet ID.
     * @return the corresponding pagelet, if any.
     */
    public Pagelet getPagelet(String id) {
        if (id == null) {
            return null;
        }
        else {
            return (Pagelet)store.get(id);
        }
    }
}
