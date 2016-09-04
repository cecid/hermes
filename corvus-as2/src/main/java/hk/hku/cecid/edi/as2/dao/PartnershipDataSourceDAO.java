/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.dao;

import hk.hku.cecid.piazza.commons.dao.DVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;

import java.util.List;

/**
 * @author Donahue Sze
 *  
 */
public class PartnershipDataSourceDAO extends DataSourceDAO implements
        PartnershipDAO {

    public List findAllPartnerships() throws DAOException {
        return super.find("find_all_partnerships", null);
    }

    public PartnershipDVO findByParty(String fromParty, String toParty)
            throws DAOException {
        return (PartnershipDVO) super.findByKey(new Object[]{fromParty,
                toParty});
    }

    public List findPartnershipsByPartyID(String fromParty, String toParty) throws DAOException {
        return super.find("find_partnerships_by_party_id", new Object[]{fromParty,
                toParty});
    }

    public DVO createDVO() {
        return new PartnershipDataSourceDVO();
    }
}