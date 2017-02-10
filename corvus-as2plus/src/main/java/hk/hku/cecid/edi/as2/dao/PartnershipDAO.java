package hk.hku.cecid.edi.as2.dao;

import hk.hku.cecid.piazza.commons.dao.DAO;
import hk.hku.cecid.piazza.commons.dao.DAOException;

import java.util.List;

/**
 * @author Donahue Sze
 *  
 */
public interface PartnershipDAO extends DAO {

    public List findAllPartnerships() throws DAOException;

    public PartnershipDVO findByParty(String fromParty, String toParty)
            throws DAOException;

    public List findPartnershipsByPartyID(String fromParty, String toParty)
        throws DAOException;
}