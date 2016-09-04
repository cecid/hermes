/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.sfrm.util;

/**
 * The <code>TimedOutEntryListener</code> is the callback listener for 
 * <code>TimedOutHashTable</code>. It is invoked when a hash key-value
 * pair is considered timed-out and prepare to delete the entry. 
 * 
 * Creation Date: 26/6/2007
 * 
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since	Dwarf 10606
 * 
 * @see hk.hku.cecid.piazza.commons.util.TimedOutHashTable.
 */
public interface TimedOutEntryListener {

	/** 
	 * @param key The key of the entry that prepare to delete in the hashtable. 
	 * @param value The value of the entry that prepare to delete in the hashtable.
	 */
	public void timeOut(Object key, Object value);
}
