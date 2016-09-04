/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.dao;

import java.util.Properties;

/**
 * The Data Access Object is the primary object of the DAO pattern. The Data
 * Access Object abstracts the underlying data access implementation for the
 * Business Object to enable transparent access to the data source. The Business
 * Object also delegates data load and store operations to the Data Access
 * Object.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public interface DAO {

    /**
     * Invoked after the dao has been created successfully.
     */
    public void daoCreated() throws DAOException;
    
    /**
     * Creates the given DAO data, which is managed by this DAO, in the 
     * underlying data source.
     * 
     * @param data the DAO data.
     * @throws DAOException if unable to create the data.
     */
    public void create(DVO data) throws DAOException;
    
    /**
     * Removes the given DAO data, which is managed by this DAO, from the 
     * underlying data source.
     * 
     * @param data the DAO data.
     * @return true if the data is found and removed.
     * @throws DAOException if unable to remove the data.
     */
    public boolean remove(DVO data) throws DAOException;
    
    /**
     * Retrieves the given DAO data, which is managed by this DAO, from the 
     * underlying data source.
     * 
     * @param data the DAO data.
     * @return true if the data is found and retrieved.
     * @throws DAOException if unable to retrieve the data.
     * @throws ClassCastException if the DAO data is not DataSourceDVO.
     */
    public boolean retrieve(DVO data) throws DAOException;
    
    /**
     * Persists the given DAO data, which is managed by this DAO, to the 
     * underlying data source.
     * 
     * @param data the DAO data.
     * @return true if the data is found and persisted.
     * @throws DAOException if unable to persist the data.
     * @throws ClassCastException if the DAO data is not DataSourceDVO.
     */
    public boolean persist(DVO data) throws DAOException;
    
    /**
     * Gets the transaction of this DAO.
     * 
     * @return the transaction of this DAO or null if there is none.
     * @throws DAOException
     */
    public Transaction getTransaction() throws DAOException;
    
    /**
     * Sets a transaction to this DAO.
     * 
     * @param transaction the transaction of this DAO.
     * @throws DAOException if the transaction is not supported by this DAO.
     */
    public void setTransaction(Transaction transaction) throws DAOException;
    
    /**
     * Gets the parameters of this DAO.
     * 
     * @return the parameters of this DAO.
     */
    public Properties getParameters();

    /**
     * Gets the DAO factory of this DAO.
     * 
     * @return the DAO factory.
     */
    public DAOFactory getFactory();
    
    /**
     * Creates a data value object for this DAO.
     * 
     * @return a new data value object for this DAO.
     */
    public DVO createDVO();
}