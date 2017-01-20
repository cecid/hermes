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