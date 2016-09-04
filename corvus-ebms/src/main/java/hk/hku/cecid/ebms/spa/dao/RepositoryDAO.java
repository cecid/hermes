/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.dao;

import hk.hku.cecid.piazza.commons.dao.DAO;
import hk.hku.cecid.piazza.commons.dao.DAOException;

/**
 * @author Donahue Sze
 * 
 */
public interface RepositoryDAO extends DAO {
    public boolean findRepository(RepositoryDVO data) throws DAOException;

    public void addRepository(RepositoryDVO data) throws DAOException;

    public boolean updateRepository(RepositoryDVO data) throws DAOException;

    public void deleteRepository(RepositoryDVO data) throws DAOException;
}