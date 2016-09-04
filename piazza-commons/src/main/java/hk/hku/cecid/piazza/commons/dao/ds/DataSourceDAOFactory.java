/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.dao.ds;

import hk.hku.cecid.piazza.commons.dao.DAO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DAOFactory;
import hk.hku.cecid.piazza.commons.dao.Transaction;

import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * The DataSourceDAOFactory has implemented the DAOFactory and provides an
 * implementation for accessing the data source by Java DataSource.
 * 
 * Notice that the DAO created by the createDAO() method is a DataSourceDAO
 * object since this factory is a DataSourceDAOFactory.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class DataSourceDAOFactory extends DAOFactory {

    private DataSource dataSource;

    /**
     * Creates a new instance of DataSourceDAOFactory.
     */
    public DataSourceDAOFactory() {
        super();
    }

    /**
     * Gets the underlying DataSource object.
     * 
     * @return the underlying DataSource object.
     */
    protected DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Sets the underlying DataSource object.
     * 
     * @param source the underlying DataSource object.
     */
    protected void setDataSource(DataSource source) {
        dataSource = source;
    }

    /**
     * Initializes this DAOFactory.
     */
    public void initFactory() throws DAOException {
        try {
            String dsname = getParameter("datasource");

            Object obj = new InitialContext().lookup(dsname);
            setDataSource((DataSource) obj);
        }
        catch (Exception e) {
            throw new DAOException("Cannot initialize DataSourceDAOFactory!", e);
        }
    }

    /**
     * Initializes the given DAO.
     * 
     * @param dao the DAO.
     * @throws DAOException if unable to initialize the DAO.
     */

    protected void initDAO(DAO dao) throws DAOException {
        try {
            ((DataSourceDAO) dao).setFactory(this);
        }
        catch (Exception e) {
            throw new DAOException("Unable to initialize DataSourceDAO '" + dao + "'",
                    e);
        }
    }

    /**
     * Creates a data source transaction. 
     * 
     * @return a new data source transaction.
     * @throws TransactionException if unable to create the data source transaction.
     * @see hk.hku.cecid.piazza.commons.dao.DAOFactory#createTransaction()
     */
    public Transaction createTransaction() throws DAOException {
        try {
            return new DataSourceTransaction(this);
        }
        catch (Exception e) {
            throw new DAOException("Unable to create transaction", e);
        }
    }
}