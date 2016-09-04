/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

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