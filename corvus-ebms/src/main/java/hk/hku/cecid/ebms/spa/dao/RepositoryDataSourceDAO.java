/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.dao;

import hk.hku.cecid.piazza.commons.dao.DVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;

/**
 * @author Donahue Sze
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RepositoryDataSourceDAO extends DataSourceDAO implements
        RepositoryDAO {

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.dao.DAO#createNewDVO()
     */
    public DVO createDVO() {
        return new RepositoryDataSourceDVO();
    }

    public boolean findRepository(RepositoryDVO data) throws DAOException {
        return super.retrieve((RepositoryDataSourceDVO) data);
    }

    public void addRepository(RepositoryDVO data) throws DAOException {
        super.create((RepositoryDataSourceDVO) data);
    }

    public boolean updateRepository(RepositoryDVO data) throws DAOException {
        return super.persist((RepositoryDataSourceDVO) data);
    }

    public void deleteRepository(RepositoryDVO data) throws DAOException {
        super.remove((RepositoryDataSourceDVO) data);
    }

}