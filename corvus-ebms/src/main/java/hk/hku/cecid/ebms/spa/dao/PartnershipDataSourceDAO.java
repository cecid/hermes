/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.dao;

import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DVO;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;

import java.util.Iterator;
import java.util.List;

/**
 * @author Donahue Sze
 * 
 */
public class PartnershipDataSourceDAO extends DataSourceDAO implements
        PartnershipDAO {

    public DVO createDVO() {
        return new PartnershipDataSourceDVO();
    }

    public List findPartnershipsByCPA(PartnershipDVO data) throws DAOException {
        return super.find("find_partnerships_by_cpa", new Object[] {
                data.getCpaId(), data.getService(), data.getAction() });
    }

    public boolean findPartnershipByCPA(PartnershipDVO data)
            throws DAOException {
        Iterator i = findPartnershipsByCPA(data).iterator();
        if (i.hasNext()) {
            ((PartnershipDataSourceDVO) data)
                    .setData(((PartnershipDataSourceDVO) i.next()).getData());
            return true;
        }
        return false;

    }

    public List findAllPartnerships() throws DAOException {
        return super.find("find_all_partnerships", null);
    }

}