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

import java.util.List;

/**
 * @author Donahue Sze
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OutboxDataSourceDAO extends DataSourceDAO implements OutboxDAO {

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.dao.DAO#createNewDVO()
     */
    public DVO createDVO() {
        return new OutboxDataSourceDVO();
    }

    public boolean findOutbox(OutboxDVO data) throws DAOException {
        return super.retrieve((OutboxDataSourceDVO) data);
    }

    public void addOutbox(OutboxDVO data) throws DAOException {
        super.create((OutboxDataSourceDVO) data);
    }

    public boolean updateOutbox(OutboxDVO data) throws DAOException {
        return super.persist((OutboxDataSourceDVO) data);
    }

    public void deleteOutbox(OutboxDVO data) throws DAOException {
        super.remove((OutboxDataSourceDVO) data);
    }
    
    public List selectOutbox() throws DAOException {
        return super.find("select_outbox", new Object[]{});
    }

}