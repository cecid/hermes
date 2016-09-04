/**
 * Provides database and message handler and some utility generators at
 * the high level architecture.  
 */
package hk.hku.cecid.edi.sfrm.handler;

import java.util.Properties;

import hk.hku.cecid.edi.sfrm.dao.DVOCacher;
import hk.hku.cecid.edi.sfrm.dao.SimpleLRUDVOCacher;
import hk.hku.cecid.piazza.commons.dao.DAO;
import hk.hku.cecid.piazza.commons.dao.DVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.module.SystemComponent;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

/**
 * The class DSHandler provides abstract interface 
 * for getting the actual DAO from the implementation class.<br> 
 * 
 * Creation Date: 3/10/2006.<br><br>
 * 
 * V1.0.2 - support DVO <code>Caching</code>
 * 
 * @author Twinsen Tsang
 * @version 1.0.2
 * @since	1.0.0
 */
public abstract class DSHandler extends SystemComponent {

	/**
	 * The singleton dao.
	 */
	protected DAO dao;
	
	/**
	 * The DVO cache.
	 */
	protected DVOCacher cacher;
	
	/**
	 * The flag indicating whether the cache is enabled ?.
	 */
	protected boolean isCacheEnable; 
	
	/**
	 * Invoked for initialization.<br><br>
	 * 
	 * There is following parameters that can be set in this component:<br>
	 * <ol>
	 * 	<li>cache-enable: The flag whether the cache should be enabled. [Boolean]</li>
	 * 	<li>cache-size	: The size of cache that can hold without LRU swapping. [Integer]</li>
	 * </ol>	 
	 */
	protected void init() throws Exception{				
		Properties p 		= this.getParameters();
		this.isCacheEnable	= StringUtilities.parseBoolean(p.getProperty("cache-enable"));		
		int cacheSize		= StringUtilities.parseInt(p.getProperty("cache-size"), 10);		
		cacher 				= new SimpleLRUDVOCacher(cacheSize);		
	}
		
	/**
	 * Create / Get the instance of DAO.
	 */
	protected abstract DAO getInstance() throws DAOException;		
				
	/** 
	 * The public interface used for other class to access DAO.
	 * 
	 * @return Get the DAO singleton instance.
	 */
	public DAO getDAOInstance(){
		try{			
			return this.getInstance();
		}catch(DAOException daoe){
			getLogger().fatal("Fail to create DAO Instance", daoe);
		}		
		return null;
	}	
	
	/**
	 * Clear the cache ONLY by a particular dvo object; 
	 */
	public abstract void clearCache(DVO dvo);		
}
