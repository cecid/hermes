package hk.hku.cecid.ebms.spa.dao;

import hk.hku.cecid.piazza.commons.dao.DAO;
import hk.hku.cecid.piazza.commons.dao.DAOException;

import java.util.List;

/**
 * @author Donahue Sze
 * 
 */
public interface PartnershipDAO extends DAO {
    public boolean findPartnershipByCPA(PartnershipDVO data)
            throws DAOException;

    public List findAllPartnerships() throws DAOException;
    
    public List findPartnershipsByCPA(PartnershipDVO data) throws DAOException;
}