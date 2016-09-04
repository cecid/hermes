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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 * DataSourceQuery is a data source DAO process which handles a SQL query 
 * process.
 * 
 * @author Hugo Y. K. Lam
 *
 */
class DataSourceQuery extends DataSourceProcess {

    private String sql;
    private Object[] params;
    private boolean isRawResult;
    
    /**
     * Creates a new instance of DataSourceQuery.
     * 
     * @param dao the DAO to which this process correspond.
     * @param transaction the transaction for this process.
     * @param sql the SQL query statement.
     * @param params the parameter values used by the specified SQL statement.
     *            The number, format, and sequence of the parameter values
     *            should match the statement. null if there is no parameter for
     *            the statement.
     */
    DataSourceQuery(DataSourceDAO dao, DataSourceTransaction transaction, String sql, Object[] params) {
        super(dao, transaction);
        this.sql = sql;
        this.params = params;
    }

    /**
     * Executes the query and stores the result as a list of DataSourceDVO or 
     * raw data list.
     * 
     * @throws DAOException if unable to execute the query.
     * @see hk.hku.cecid.piazza.commons.dao.ds.DataSourceProcess#doTransaction(DataSourceTransaction)
     */
    protected void doTransaction(DataSourceTransaction tx) throws DAOException {
    	
    	ResultSet rs = null;
    	PreparedStatement pStmt = null;
    	
        try {
            if (sql == null) {
                throw new DAOException("SQL in query cannot be NULL");
            }

            pStmt = tx.getConnection().prepareStatement(sql);
            
            int noOfParas = getParameterCount(pStmt, sql);


            // check the input parameters for the SQL statement
            if (noOfParas > 0) {
                if (params == null || params.length != noOfParas) {
                    throw new DAOException(
                            "Number of parameters specified do not match in the SQL");
                }
                else {
                    for (int i = 0; i < noOfParas; i++) {
                        setParameter(pStmt, i + 1, params[i]);
                    }
                }
            }

            // execute the query
            rs = pStmt.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            String[] columnNames = new String[numberOfColumns];
            for (int i = 0; i < columnNames.length; i++) {
                columnNames[i] = rsmd.getColumnName(i + 1);
            }

            // pack the data retrieved into a List of DataSourceDVO
            List result = new ArrayList();

            while (rs.next()) {
                if (isRawResult) {
                    List data = new ArrayList();
                    int cc = rsmd.getColumnCount();
                    for (int i=1; i<=cc; i++) {
                       Object value = rs.getObject(i);
                       data.add(value);
                    }
                    result.add(data);
                }
                else {
                    Hashtable values = new Hashtable();
                    for (int i = 0; i < columnNames.length; i++) {
                        Object value = rs.getObject(columnNames[i]);
                        String columnCodeName = getDAO().getColumnCodeName(columnNames[i]);
                        
                        if (value != null && columnCodeName!=null) {
	                        if (value instanceof java.sql.Blob) {
	                        	java.sql.Blob blob = (java.sql.Blob)value;
	            				byte[] b = new byte[8196];
	            				int j = 0;
	            				InputStream is = blob.getBinaryStream();
	            				ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            				while (( j = is.read(b)) > 0) {
	            					baos.write(b, 0, j);
	            				}
		            			values.put(columnCodeName, baos.toByteArray());	
	                    	} else {
	                            values.put(columnCodeName, value);
	                    	}
                        }
                    }
                    DataSourceDVO data = (DataSourceDVO) getDAO().createDVO();
                    data.setData(values);
                    result.add(data);
                }
            }
            
            setResult(result);
        }
        catch (Exception e) {
            throw new DAOException("Error in executing query: " + sql, e);
        }
        finally {
        	if (rs!=null) {
        		try {
        			rs.close();
        		}
        		catch (Exception ex) {
        			throw new DAOException("Error in closing result set: ", ex);
        		}
        	}
        	if (pStmt!=null) {
        		try {
        			pStmt.close();        			
        		}
        		catch (Exception ex) {
        			throw new DAOException("Error in closing prepared statement: ", ex);
        		}
        	}
        }
    }
    
    /**
     * Checks if the result data is in raw format.
     * 
     * @return true if the result data is in raw format.
     * @see #setRawResult(boolean)
     */
    public boolean isRawResult() {
        return isRawResult;
    }
    
    /**
     * Sets whether the result data should be in raw format. If true, the result
     * data will be a list of DataSourceDVO. Otherwise, it will be a list of list 
     * which contains the data values in order with the query.
     * 
     * @param isRawResult true if the result data should be in raw format.
     */
    public void setRawResult(boolean isRawResult) {
        this.isRawResult = isRawResult;
    }
}
