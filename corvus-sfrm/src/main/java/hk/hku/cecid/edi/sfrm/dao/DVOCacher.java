/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.sfrm.dao;

import hk.hku.cecid.piazza.commons.dao.DVO;

/**
 * The <code>DVOCacher</code> is a component that caches 
 * the DVO(s) into partiular format designated in the sub-class.<br><br>    
 * 
 * To store DVO that have multiple key, it is <strong>recommended</strong> 
 * to combine the key as a string by a separator "_".<br><br> 
 * 
 * For example, If the messageDVO has the key <em>message_id</em> and
 * <em>message_box</em>, 
 * 
 * You should use combined key <em>message_id</em>_<em>message_box</em>.
 * <br><br>
 * 
 * Creation Date: 12/2/2007
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	1.0.3
 */
public interface DVOCacher {
						
	/**
	 * Store the DVO <code>cacheItem</code> into the cacher 
	 * with the specified <code>Key</code>.<br><br>
	 * 
	 * If the DVO in the <code>key</code> is already exist,
	 * it throws CacheException indicating cache duplication.  
	 * 
	 * @param key
	 * 			The key field of the DVO.
	 * @param cacheItem
	 * 			The DVO item to be cached.
	 * @throws CacheException
	 * 			when the record is already exist.
	 * 
	 * @since 1.0.0
	 * 	 	
	 * @see #putOrUpdateDVO(String, DVO);
	 */
	void putDVO(String key, DVO cacheItem) throws CacheException;

	/**
	 * Store or Update the DVO <code>cacheItem</code> 
	 * into the cacher with the specified <code>Key</code>.
	 * <br><br>
	 * 
	 * @param key
	 * 			The key field of the DVO.
	 * @param cacheItem
	 * 			The DVO item to be cached. 
	 * @since	1.0.0
	 */
	void putOrUpdateDVO(String key, DVO cacheItem);

	/**
	 * Remove the cache record with the key field equal to
	 * <code>key</code>.
	 * 
	 * @param key
	 * 			The DVO's key field to remove.
	 */
	void removeDVO(String key);
	
	/**
	 * Remove all the cache record. 
	 */
	void removeAll();

	/**
	 * Get the DVO cache from the particular <code>key</code>.
	 * 
	 * @param key
	 * 			The key field of the DVO.
	 * @return
	 * 			Return null if the cache record does not exist.
	 */
	DVO getDVO(String key);

	/**
	 * The maximum size is defined how many DVO are the 
	 * cacher can store before swapping out happens.
	 *  
	 * @return 
	 * 			Get the maximum size of DVO cacher. 			
	 */
	int maxSize();

	/** 
	 * The activs size is defined how many DVO are already 
	 * stored in the cacher. 
	 * 
	 * @return
	 * 			Get the active size of DVO cacher.
	 */
	int activeSize();	 	
	
	/**
	 * This method is used to calculate the {@link #getDVO(String)} 
	 * efficieny (eff) of the cacher. It is formulated :<br>  
	 * <pre>
	 *              cacheHit
	 *  eff = (--------------------) * 100
	 *         cacheHit + cacheMiss
	 * </pre>
	 *  
	 * @return the efficiency of the cacher.
	 */
	double efficieny();
}

