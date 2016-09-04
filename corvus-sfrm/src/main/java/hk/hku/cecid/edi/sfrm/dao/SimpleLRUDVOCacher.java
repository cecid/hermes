/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.sfrm.dao;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Properties;

import hk.hku.cecid.piazza.commons.dao.DVO;
import hk.hku.cecid.piazza.commons.module.Component;

import hk.hku.cecid.piazza.commons.util.StringUtilities;

/**
 * The <code>SimpleLRUDVOCacher</code> is the simple DVO cacher
 * which possess LRU, Least-Recently Used features. When there 
 * is not enough room to store the DVO as a cache. It tries 
 * to remove the oldest record (the one that has not retrieved 
 * for longest times).<br><br>
 * 
 * Creation Date: 12/2/2007
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	1.0.0
 */
public class SimpleLRUDVOCacher extends Component implements DVOCacher{

	/**
	 * Interval Lock object.
	 */
	private transient Object lock = new Object();
	
	/**
	 * The DVO cache entries.
	 */
	private Map cacheEntries;
	
	/**
	 * The maximum allowed size of the LRU cacher.
	 */
	private int maxSize;
	
	/**
	 * The statistics counter for indicating how many cache has been 
	 * missed.
	 */
	private int cacheMiss;
	
	/**
	 * The statistics counter for indicating how many cache has 
	 * been found.
	 */
	private int cacheHit;
	 	
	/** 
	 * Constructor. 
	 * 
	 * Use {@link #SimpleLRUDVOCacher(int)} instead of this constructor.
	 * This is reserved for Piazza Common component initialization only.
	 */
	public SimpleLRUDVOCacher(){		
	}
	
	/** 
	 * Explicit Constructor.
	 * 
	 * @param cacheSize
	 * 			The maximum size of the cache.
	 */
	public SimpleLRUDVOCacher(int cacheSize){
		Properties p = new Properties();
		p.setProperty("cache-size", String.valueOf(cacheSize));
		this.setParameters(p);
		try{
			this.init();
		}catch(Exception e){}
	}
	
	/**
	 * Invoked for initialization.
	 */
	protected void init() throws Exception {
		super.init();
		Properties p = this.getParameters();
		this.maxSize = StringUtilities.parseInt(p.getProperty("cache-size"), 10);
				
		this.cacheEntries = new LinkedHashMap(this.maxSize, 0.75F, true){
			// The backward compatible UID.
			private static final long serialVersionUID = 554434288301737810L;
			// This method is called just after a new entry has been added
	        public boolean removeEldestEntry(Map.Entry eldest) {
	            return size() > SimpleLRUDVOCacher.this.maxSize;
	        }
		};
	}

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
	public void putDVO(String key, DVO cacheItem) throws CacheException{
		if (key == null)
			throw new NullPointerException(
				"The key field is null.");
		if (cacheEntries.containsKey(key))
			throw new CacheException(
				"The cacheItem: "
			   + cacheItem 
			   +"is exist in the cache memory in this key: " 
			   + key);		
		synchronized(lock){						
			this.cacheEntries.put(key, cacheItem);
		}
	}
	 
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
	public void putOrUpdateDVO(String key, DVO cacheItem){
		if (key == null)
			throw new NullPointerException(
				"The key field is null.");
		synchronized(lock){
			this.cacheEntries.put(key, cacheItem);
		}
	}
	
	/**
	 * Remove the cache record with the key field equal to
	 * <code>key</code>.
	 * 
	 * @param key
	 * 			The DVO's key field to remove.
	 */
	public void removeDVO(String key){
		if (key == null)
			throw new NullPointerException(
				"The key field is null.");
		synchronized(lock){
			this.cacheEntries.remove(key);
		}
	}
	
	/**
	 * Remove all the cache record. 
	 */
	public void removeAll(){
		synchronized(lock){
			this.cacheEntries.clear();
		}
	}
	 		 	 	
	/**
	 * Get the DVO cache from the particular <code>key</code>.
	 * 
	 * @param key
	 * 			The key field of the DVO.
	 * @return
	 * 			Return null if the cache record does not exist.
	 */
	public DVO getDVO(String key){
		if (key == null)
			throw new NullPointerException(
				"The key field is null.");
		DVO ret;
		synchronized(lock){
			ret = (DVO) this.cacheEntries.get(key);
			if (ret != null)
				this.cacheHit++;
			else
				this.cacheMiss++;
		}
		return ret;
	}
	
	/**
	 * The maximum size is defined how many DVO are the 
	 * cacher can store before swapping out happens.
	 *  
	 * @return 
	 * 			Get the maximum size of DVO cacher. 			
	 */
	public int maxSize(){
		return this.maxSize;
	}
	
	/** 
	 * The activs size is defined how many DVO are already 
	 * stored in the cacher. 
	 * 
	 * @return
	 * 			Get the active size of DVO cacher.
	 */
	public int activeSize(){
		return this.cacheEntries.size();
	}
	
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
	public double efficieny(){
		return ((double)this.cacheHit / (this.cacheHit + this.cacheMiss)) * 100;
	}
}

