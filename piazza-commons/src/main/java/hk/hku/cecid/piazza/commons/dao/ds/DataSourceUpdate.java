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

import java.sql.PreparedStatement;


/**
 * DataSourceUpdate is a data source DAO process which handles a SQL update
 * process.
 * 
 * @author Hugo Y. K. Lam
 *
 */
class DataSourceUpdate extends DataSourceProcess {

    private String sql;
    private Object[][] params;
    
    /**
     * Creates a new instance of DataSourceUpdate.
     * 
     * @param dao the DAO to which this process correspond.
     * @param transaction the transaction for this process.
     * @param sql the SQL update statement.
     * @param params the parameter values used by the specified SQL statement.
     *            The number, format, and sequence of the parameter values
     *            should match the statement. null if there is no parameter for
     *            the statement and multiple sets if it is a batch update.
     */
    DataSourceUpdate(DataSourceDAO dao, DataSourceTransaction transaction, String sql, Object[][] params) {
        super(dao, transaction);
        this.sql = sql;
        this.params = params;
    }

    /**
     * Executes the update and stores the result as an array of integers which 
     * indicates the update results. 
     * 
     * @throws DAOException if unable to execute the update.
     * @see hk.hku.cecid.piazza.commons.dao.ds.DataSourceProcess#doTransaction(DataSourceTransaction)
     */
    protected void doTransaction(DataSourceTransaction tx) throws DAOException {
        try {
            if (sql == null) {
                throw new DAOException("SQL in update cannot be NULL");
            }
            
            PreparedStatement pStmt = tx.getConnection().prepareStatement(sql);

            int noOfParas = getParameterCount(pStmt, sql);

            if (noOfParas == 0) {
                params = new Object[1][];
            }
            else if (params == null) {
                throw new DAOException("No parameters specified");
            }
            else {
                for (int i = 0; i < params.length; i++) {
                    if (params[i] == null || params[i].length != noOfParas) {
                        throw new DAOException(
                                "Number of parameters at row "+i+" of the specified parameter array does not match the SQL statement");
                    }
                }
            }

            int[] result = new int[params.length];

            int i = 0;
            try {
                // for each set of parameters
                for (; i < params.length; i++) {
                    
                    // set each parameter to the prepared statement and
                    // execute update
                    for (int j = 0; noOfParas > 0 && j < noOfParas; j++) {
                        setParameter(pStmt, j + 1, params[i][j]);
                    }
                    // some jdbc drivers may not support batch execution,
                    // individual update is required.
                    result[i] = pStmt.executeUpdate();
                }
            }
            catch (Exception e) {
                throw new DAOException(
                        "Error occurred when executing update at row " + i
                                + " of the specified parameter array", e);
            }
            finally {
            	if (pStmt!=null) {
            		pStmt.close();
            		pStmt = null;
            	}
            }
            
            setResult(result);
        }
        catch (Exception e) {
            throw new DAOException("Error in executing update: " + sql, e);
        }
    }
}