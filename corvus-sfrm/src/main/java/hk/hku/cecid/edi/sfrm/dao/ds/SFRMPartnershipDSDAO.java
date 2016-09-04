/**
 * Provides implementation class for the database access object 
 * (DAO and DVO) for the database layer. 
 */
package hk.hku.cecid.edi.sfrm.dao.ds;

import java.util.Iterator;
import java.util.List;

import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDVO;

import hk.hku.cecid.piazza.commons.dao.DVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;

/**
 * Creation Date: 27/9/2007
 * 
 * @author Twinsen
 * @version 1.0.0
 *
 */
public class SFRMPartnershipDSDAO extends DataSourceDAO implements
		SFRMPartnershipDAO {

	/**
	 * Constructor. 
	 */
	public SFRMPartnershipDSDAO() {
	}

	/**
	 * Create a new SFRM Partnership Object.
	 * 
	 * @return a new SFRM Partnership Object.
	 */		
	public DVO createDVO() {
		return new SFRMPartnershipDSDVO();
	}
	
	/**
	 * Find the partnership by the partnership object 
	 * specified in the parameter object.<br><br>
	 * 
	 * The partnership id field will be retreived and 
	 * used for the finder.<br>   
	 * 
	 * @param partnershipDVO
	 * @return return null if no partnership found, otherwise the a sfrm partnership.
	 * @throws DAOException
	 */
	public SFRMPartnershipDVO findPartnershipBySeq
		(SFRMPartnershipDVO partnershipDVO) throws DAOException{
		if (partnershipDVO == null)
			return null;
		return this.findPartnershipBySeq(partnershipDVO.getPartnershipSeq());
	}
	
	/**
	 * Find the partnership by it's id.
	 * 
	 * @param partnershipId
	 * @return return null if no partnership found, otherwise the a sfrm partnership.
	 */
	public SFRMPartnershipDVO findPartnershipBySeq
		(int partnershipId) throws DAOException{
		return (SFRMPartnershipDVO) 
			super.findByKey(new Object[]{new Integer(partnershipId)});
	}

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
		(SFRMPartnershipDVO partnershipDVO) throws DAOException{
		if (partnershipDVO == null)
			return null;
		return this.findPartnershipById(partnershipDVO.getPartnershipId());
	}
	
	/**
	 * Find the partnership by the service string
	 * specified in the parameter.<br><br>
	 * 
	 * @param partnershipId	
	 * @return return null if no partnership found, otherwise the a sfrm partnership.
	 */
	public SFRMPartnershipDVO findPartnershipById
		(String partnershipId) throws DAOException{
		Iterator itr = 
			super.find("find_partnership_by_id", new Object[]{partnershipId}).iterator();		
		if (itr.hasNext())
			return (SFRMPartnershipDVO) itr.next();
		return null;
	}
	
	/**
	 * Find all partnership order by the partnership id
	 * 
	 * @return return a list of partnership existing in the database
	 */
	public List findAllPartnerships() throws DAOException{
		return super.find("find_all_partnerships", null);
	}
}
