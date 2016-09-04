/**
 * Provides database and message handler and some utility generators at
 * the high level architecture.  
 */
package hk.hku.cecid.edi.sfrm.handler;

import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.edi.sfrm.dao.CacheException;
import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDAO;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;

import hk.hku.cecid.piazza.commons.dao.DAO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DVO;

/**
 * 
 * 
 * Creation Date: 28/9/2006
 *
 * V1.0.1 - supports DVO <code>caching</code>.
 *
 * @author Twinsen
 * @version 1.0.0
 */
public class SFRMPartnershipHandler extends DSHandler{
		
	/**
	 * Create / Get the instance of DAO.
	 */
	protected DAO getInstance() throws DAOException
	{	
		// TODO: Not thread-safety, may lead to partial constructed issue. 
		if (this.dao == null){
			this.dao = (SFRMPartnershipDAO) 
				SFRMProcessor.getInstance().getDAOFactory().createDAO(SFRMPartnershipDAO.class);
			return this.dao;
		}
		return this.dao;
	}
		
	/**
	 * Validate the partnership to whether.<br>
	 * <ul>
	 * 	<li> The partnership is null. </li>
	 * 	<li> The Dao is null. </li>
	 * </ul>
	 * 
	 * @param pDVO The partnership object to be validated.
	 * @return return false if is satifise one of condition in the above, true otherwise.
	 * @throws DAOException
	 */
	private boolean validatePartnership(SFRMPartnershipDVO pDVO) throws DAOException{
		if ( pDVO == null || this.getInstance() == null )			  
			return false;
		return true;
	}
	
	/**
	 * Create a SFRM Partnership object.
	 * 
	 * @return A new SFRM Partnership object.
	 * @throws DAOException
	 */
	public SFRMPartnershipDVO createPartnership() throws DAOException{
		return (SFRMPartnershipDVO) ((SFRMPartnershipDAO) this.getDAOInstance())
				.createDVO();
	}
	
	/**
	 * Select a SFRM Partnership by the service parameter.
 	 *
	 * @param pID
	 * 			The partnership id name of the partnership id to be search.
	 * @param mID
	 * 			The message id for the requesting partnership.
	 * @return
	 * 			null if not found, otherwise the SFRM Partnership.
	 * @throws DAOException 			
	 */
	public SFRMPartnershipDVO retreivePartnership(
			String pID, 
			String mID) throws DAOException
	{		
		SFRMPartnershipDVO ret = null;
		String key = null; 
		if (this.isCacheEnable){
			key = pID + "_" + mID;
			ret = (SFRMPartnershipDVO) this.cacher.getDVO(key);
		}
		if (ret == null){
			ret = ((SFRMPartnershipDAO) this.getInstance()).findPartnershipById(pID);
			if (this.isCacheEnable)
				this.cacher.putOrUpdateDVO(key, ret);
		}				
		return ret;
	}
	
	/**
	 * Select a SFRM Partnership by the keys specified in the 
	 * parameter.
	 * 
	 * @param pDVO 
	 * 			The SFRM Partnership to be found for.
	 * @param mID 
	 *			The message id for the requesting partnership.
	 * @return null if not found, otherwise the SFRM Partnership.
	 * @throws DAOException 
	 */
	public SFRMPartnershipDVO retreivePartnership(
			SFRMPartnershipDVO pDVO,
			String mID) throws DAOException 
	{
		if (!this.validatePartnership(pDVO))
			return null;
		return this.retreivePartnership(pDVO.getPartnershipId(), mID);
	}
	
	/**
	 * Select a SFRM Partnership by the SFRM message.
	 * 
	 * @param msg 
	 * 			The SFRM Message record used for finding it's associate partnership.
	 * @return null if not found, otherwise the SFRM Partnership.
	 * @throws DAOException 
	 */
	public SFRMPartnershipDVO retreivePartnership(
			SFRMMessage msg) throws DAOException
	{
		if (msg == null)
			return null;
		return this.retreivePartnership(msg.getPartnershipId(), msg.getMessageID());
	}
	
	/**
	 * Add a SFRM Partnership by partnership object specified 
	 * in the parmeter.
	 * 
	 * @param pDVO 
	 * 			The SFRM Partnership to be added for.
	 * @param mID 
	 * 			The message id for the requesting partnership.
	 * @return false if operation fails to execute, otherwise true.
	 * @throws DAOException
	 */
	public boolean addPartnership(
			SFRMPartnershipDVO pDVO, 
			String mID) throws DAOException
	{
		if (!this.validatePartnership(pDVO))
			return false;
		try{
			// We should use putDVO here because we can check whether the 
			// partnership DVO is exist or not, which acts as the fast record 
			// validation, does not require to query DB.
			if (this.isCacheEnable)
				this.cacher.putDVO(pDVO.getPartnershipId() + "_" + mID , pDVO);
			((SFRMPartnershipDAO)this.getDAOInstance()).create(pDVO);			
		}catch(CacheException ce){
			throw new DAOException(
				" Duplicate Partnership record violate key constraint"
			   +" with partnership id: " + pDVO.getPartnershipId(), ce);
		}
		return true;
	}
	
	/**
	 * Update a SFRM Partnership by partnership object specified
	 * in the parameter.
	 * 
	 * @param pDVO 
	 * 			The SFRM Partnership to be updated for.
	 * @param mID 
	 * 			The message id for the requesting partnership.
	 * @return false if operation fails to execute, otherwise true.
	 * @throws DAOException
	 */
	public boolean updatePartnership(
			SFRMPartnershipDVO pDVO, 
			String mID) throws DAOException
	{
		if (!this.validatePartnership(pDVO))
			return false;	
		if (this.isCacheEnable)
			this.cacher.putOrUpdateDVO(pDVO.getPartnershipId() + "_" + mID, pDVO);
		return ((SFRMPartnershipDAO)this.getDAOInstance()).persist(pDVO);
	}

	/**
	 * Remove a SFRM Partnership by partnership object specified
	 * in the parameter.
	 * 
	 * @param pDVO
	 * @return false if operation fails to execute, otherwise true.
	 * @throws DAOException
	 */
	public boolean removePartnership(SFRMPartnershipDVO pDVO) throws DAOException{
		if (!this.validatePartnership(pDVO))
			return false;
		if (this.isCacheEnable)
			this.cacher.removeDVO(pDVO.getPartnershipId());
		return ((SFRMPartnershipDAO)this.getDAOInstance()).remove(pDVO);
	}
	
	/**
	 * [Overriden] Clear the cache ONLY by a particular dvo object.<br/><br/>
	 * 
	 * NOT YET IMPLEMENTED.
	 * 
	 * @param dvo The partnership object to be removed in the cache.
	 */
	public void clearCache(DVO dvo){				
		/*if (dvo != null && dvo instanceof SFRMPartnershipDVO){
			String key = ((SFRMPartnershipDVO) dvo).getPartnershipId();
			if (this.isCacheEnable)
				this.cacher.removeDVO(key);
		}*/
		
	}
	
	/**
	 * Clear the cache ONLY by a particular partnership id.  
	 * 
	 * @param pID The partnership object has <code>pID<code> to be removed in the cache. 			
	 * @param mID The message id for the requesting partnership.
	 */
	public void clearCache(String pID, String mID){
		if (pID != null && mID != null && this.isCacheEnable){			
			this.cacher.removeDVO(pID + "_" + mID);
		}
	}
}
