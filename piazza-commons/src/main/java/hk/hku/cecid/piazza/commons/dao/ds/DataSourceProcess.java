/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.dao.ds;

import hk.hku.cecid.piazza.commons.dao.DAOException;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DataSourceProcess represents a data source transaction process. Subclasses are 
 * required to implement the doTransaction() method for the transaction process.
 * <p>
 * This process will only manage, for example commit or rollback, the transaction
 * encapsulated if its DataSourceDAO is not under a transaction and it is not
 * explicitly instructed, in construction, to use a specified transaction.   
 * 
 * @author Hugo Y. K. Lam
 *
 */
public abstract class DataSourceProcess  {
    
    private DataSourceDAO dao;
    private DataSourceTransaction transaction;
    
    private boolean isStarted = false;
    private boolean isCommitted = false;
    private boolean isRolledBack = false;
    
    private Object result;

    /**
     * Creates a new instance of DataSourceProcess. 
     * 
     * @param dao the DAO to which this process correspond.
     */
    public DataSourceProcess(DataSourceDAO dao) {
        this(dao, null);
    }
    
    /**
     * Creates a new instance of DataSourceProcess. 
     * 
     * @param dao the DAO to which this process correspond.
     * @param transaction the transaction in which this process should execute.
     */
    public DataSourceProcess(DataSourceDAO dao, DataSourceTransaction transaction) {
        this.dao = dao;
        this.transaction = transaction;
    }
    
    /**
     * Starts executing this process. A process can only be started if it 
     * has not yet been started.
     * 
     * @throws DAOException if there is any error in the execution or the 
     *          process has already been started.
     */
    public synchronized void start() throws DAOException {
        
        if (isStarted) {
            throw new DAOException("Process already started.");
        }
        else {
            isStarted = true;
        }

        boolean isTransactionManaged = true;
        
        if (transaction == null) {
            transaction = dao.getTransaction(true);
            if (!dao.isUnderTransaction()) {
                isTransactionManaged = false;
                transaction.begin();
            }
        }
                
        try {
            doTransaction(transaction);
            
            if (!isTransactionManaged) {
                transaction.commit();
                isCommitted = true;
            }
        }
        catch (Throwable e) {
            DAOException de = new DAOException("Error in executing "+this, e);
            if (!isTransactionManaged) {
                try {
                    transaction.rollback();
                    isRolledBack = true;
                }
                catch (Exception ex) {
                    throw new DAOException("Error in rolling back ("+ex+")", de);
                }
            }
            throw de;
        }
    }

    /**
     * Executes the transaction process.
     * 
     * @param tx the DataSourceTransaction object in which this process executes.  
     * @throws DAOException if any error occurred in the execution.
     */
    protected abstract void doTransaction(DataSourceTransaction tx) throws DAOException;
    

    /**
     * Sets a parameter to the prepared statement.
     * 
     * @param pStmt the prepared statement.
     * @param pos the position of the parameter.
     * @param param the parameter.
     * @throws SQLException if unable to set the parameter.
     */
    protected void setParameter(PreparedStatement pStmt, int pos, Object param) 
            throws SQLException {
        if (param == null) {
            int type;
            try {
                type = pStmt.getParameterMetaData().getParameterType(pos);
            }
            catch (Throwable e) {
                type = java.sql.Types.VARCHAR;
            }
            pStmt.setNull(pos, type);
        }
        else if (param instanceof NullableObject) {
            NullableObject no = (NullableObject) param;
            if (no.isNull()) {
                pStmt.setNull(pos, no.getType());
            }
            else {
                pStmt.setObject(pos, no.getObject());
            }
        }
        else if (param instanceof InputStream){
            InputStream ins = (InputStream)param;
            int len;
            try {
                len = ins.available();
            }
            catch (Exception e) {
                len = 0;
            }
            pStmt.setBinaryStream(pos, ins, len);
        }
        else {
            pStmt.setObject(pos, param);
        }
    }

    /**
     * Counts the number of parameters required by the prepared statement.
     * If a parameter meta data is not available, it counts the occurrences of 
     * '?' in the given SQL statement.
     * 
     * @param pStmt the prepared statement.
     * @param sql the SQL statement.
     * @return the numbder of parameters.
     */
    protected int getParameterCount(PreparedStatement pStmt, String sql) {
        try {
            return pStmt.getParameterMetaData().getParameterCount();
        }
        catch (Throwable e) {
            Pattern p = Pattern.compile("\\?(?=(?:[^']*'[^']*')*(?![^']*'))");
            Matcher m = p.matcher(sql);
            int noOfParas = 0;
            while (m.find()) {
                noOfParas++;
            }
            return noOfParas;
        }
    }
    
    /**
     * Gets the DAO to which this process correspond.
     * 
     * @return the DAO to which this process correspond.
     */
    protected DataSourceDAO getDAO() {
        return dao;
    }
    
    /**
     * Checks if this process has committed the transaction.
     * 
     * @return true if this process has committed the transaction.
     */
    public boolean isCommitted() {
        return isCommitted;
    }
    
    /**
     * Checks if this process has rolled back the transaction.
     * 
     * @return true if this process has rolled back the transaction.
     */
    public boolean isRolledBack() {
        return isRolledBack;
    }
    
    /**
     * Checks if this process has already been started.
     * 
     * @return true if the this process has already been started.
     */
    public boolean isStarted() {
        return isStarted;
    }

    /**
     * Sets the result of this process.
     * 
     * @param result the result of this process.
     */
    protected void setResult(Object result) {
        this.result = result;
    }

    /**
     * Gets the result of this process.
     * 
     * @return the result of this process.
     */
    public Object getResult() {
        return result;
    }

    /**
     * Returns a string representation of this process.
     * 
     * @return a string representation of this process.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return dao.getClass().getName()+"::Process@"+Integer.toHexString(hashCode());
    }
}
