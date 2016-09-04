/**
 * Provides inferace for the database access object (DAO and DVO) 
 * for the database layer. 
 */
package hk.hku.cecid.edi.sfrm.dao;

import java.util.List;
import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDVO;

import hk.hku.cecid.piazza.commons.dao.DAO;
import hk.hku.cecid.piazza.commons.dao.DAOException;

/**
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	1.0.0
 */
public interface SFRMPartnershipDAO extends DAO {
 	
	/**
	 * Find the partnership by the partnership object 
	 * specified in the parameter object.<br><br>
	 * 
	 * The partnership seq field will be retreived and 
	 * used for the finder.<br>   
	 * 
	 * @param partnershipDVO
	 * @return return null if no partnership found, otherwise the a sfrm partnership.
	 * @throws DAOException
	 */
	public SFRMPartnershipDVO findPartnershipBySeq
		(SFRMPartnershipDVO partnershipDVO) throws DAOException;
	
	/**
	 * Find the partnership by it's seq id.
	 * 
	 * @param partnershipId
	 * @return return null if no partnership found, otherwise the a sfrm partnership.
	 */
	public SFRMPartnershipDVO findPartnershipBySeq
		(int partnershipId) throws DAOException;
	
	/**
	 * Find the partnership by the partnership object 
	 * specified in the parameter object.<br><br>
	 * 
	 * The partnership id field will be retreived and used for
	 * the finder.<br>  
	 * 
	 * @param partnershipDVO
	 * @return return null if no partnership found, otherwise the a sfrm partnership.
	 */
	public SFRMPartnershipDVO findPartnershipById
		(SFRMPartnershipDVO partnershipDVO)  throws DAOException;
	
	/**
	 * Find the partnership by the service string
	 * specified in the parameter.<br><br>
	 * 
	 * @param partnershipId
	 * 			The partnership id to used for search partnership. 			 
	 * @return return null if no partnership found, otherwise the a sfrm partnership.
	 */
	public SFRMPartnershipDVO findPartnershipById
		(String partnershipId) throws DAOException;
	
	/**
	 * Find all of the partnership existing in the database
	 * 
	 * @return return a list of partnership
	 */
	public List findAllPartnerships() throws DAOException;
}
