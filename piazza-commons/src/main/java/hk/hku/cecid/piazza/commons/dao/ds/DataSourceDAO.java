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
import hk.hku.cecid.piazza.commons.dao.DVO;
import hk.hku.cecid.piazza.commons.dao.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * The abstract DataSourceDAO class encapsulates a DataSourceDAOFactory object. 
 * It basically provides methods, such as getTransaction() and createTransaction(),
 * for any subclasses to get or create a transaction for accessing the underlying
 * data source. It also provides some convenient methods for the
 * subclasses to access, like querying and updating, the data source.
 * <p>
 * When using the convenient methods, some basic parameters need to be defined.
 * The parameters should be stored in the Properties object which can be
 * retrieved by calling getProperties().
 * <p>
 * The key-value pairs of the parameters must be in the following format: <table
 * border="1" width="90%">
 * <tr>
 * <td width="33%"><b>Key </b></td>
 * <td width="33%"><b>Value </b></td>
 * <td width="34%"><b>Used by </b></td>
 * </tr>
 * <tr>
 * <td>finder:key</td>
 * <td>The key finder SQL statement</td>
 * <td>findByKey()</td>
 * </tr>
 * <tr>
 * <td>finder: <i>{alias name} </i></td>
 * <td>The finder SQL statement</td>
 * <td>find()</td>
 * </tr>
 * <tr>
 * <td>column: <i>{name in the data source} </i></td>
 * <td>The column's key in the DVO</td>
 * <td>getColumnCodeName(), executeQuery()</td>
 * </tr>
 * <tr>
 * <td>sql: <i>{alias name} </i></td>
 * <td>The SQL statement</td>
 * <td>getSQL(), update()</td>
 * </tr>
 * </table>
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public abstract class DataSourceDAO implements DAO {

    private DataSourceDAOFactory    factory;

    private DataSourceTransaction   transaction;

    private SQLBuilder              builder;

    private Properties              parameters          = new Properties();

    private Properties              columnCodeNames     = new Properties();

    private static final String NAMESPACE_SQL       = "sql";

    private static final String NAMESPACE_FINDER    = "finder";

    private static final String NAMESPACE_COLUMN    = "column";

    private static final String NAMESPACE_SELECTIVE = "selective";

    private static final String NAMESPACE_SEPARATOR = ":";

    private static final String PARAM_TABLE         = "table";

    private static final String PARAM_KEY           = "key";
    
    private static final String PARAM_SEL_INSERT    = "insert";

    private static final String PARAM_SEL_UPDATE    = "update";
    
	private static final String NAMESPACE_FILTER 	= "filter";
	
	private static final String NAMESPACE_ORDER 	= "order";	

    /**
     * Creates a new instance of DataSourceDAO.
     */
    protected DataSourceDAO() {
    }

    /**
     * Initializes this DAO.
     * 
     * @see hk.hku.cecid.piazza.commons.dao.DAO#daoCreated()
     */
    public void daoCreated() {
        String table = parameters.getProperty(PARAM_TABLE);
        String key = parameters.getProperty(PARAM_KEY);

        ArrayList columns = new ArrayList();
        Enumeration paramNames = parameters.keys();

        String colind = NAMESPACE_COLUMN + NAMESPACE_SEPARATOR;
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement().toString();
            if (paramName.startsWith(colind)) {
                String colname = paramName.substring(colind.length());
                columns.add(colname);
                columnCodeNames.setProperty(colname.toUpperCase(), parameters
                        .getProperty(paramName));
            }
        }

        builder = new SQLBuilder(table, key, (String[]) columns
                .toArray(new String[]{}));
    }

    /**
     * Gets the DAO factory of this DAO.
     * 
     * @return the DAO factory.
     */
    public DAOFactory getFactory() {
        return factory;
    }

    /**
     * Sets the DAO factory of this DAO.
     * 
     * @param factory the DAO factory.
     */
    void setFactory(DataSourceDAOFactory factory) {
        this.factory = factory;
    }

    /**
     * Gets the parameters of this DAO.
     * 
     * @return a Properties which stored the parameters for this DAO.
     */
    public Properties getParameters() {
        return parameters;
    }

    /**
     * Gets the code name of the specified column in the data source.
     * 
     * @param name the name of the column.
     * @return the code name of the specified column.
     */
    protected String getColumnCodeName(String name) {
        if (name != null) {
            String codeName = columnCodeNames.getProperty(name.toUpperCase());
            if (codeName != null) {
                return codeName;
            }
        }
        return getParameter(NAMESPACE_COLUMN, name);
    }

    /**
     * Gets the SQL statement with the specified name.
     * 
     * @param name the name which refers to the SQL statement.
     * @return the SQL statement associated with the specified name.
     */
    protected String getSQL(String name) {
        return getParameter(NAMESPACE_SQL, name);
    }

    /**
     * Gets the finder SQL statement with the specified name.
     * 
     * @param name the name which refers to the finder SQL statement.
     * @return the finder SQL statement associated with the specified name.
     */
    protected String getFinder(String name) {
        return getParameter(NAMESPACE_FINDER, name);
    }
    
	/**
	 * Gets the filter partial SQL statement with the specified name.
	 * 
	 * @param name the name which refers to the filter partial SQL statement.
	 * @return the filter partial SQL statement associated with the specified name.
	 */
	protected String getFilter(String name) {
		return getParameter(NAMESPACE_FILTER, name);
	}
	
	/**
	 * Gets the order partial SQL statement with the specified name.
	 * 
	 * @param name the name which refers to the order partial SQL statement.
	 * @return the order partial SQL statement associated with the specified name.
	 */
	protected String getOrder(String name) {
		return getParameter(NAMESPACE_ORDER, name);
	}	

    /**
     * Gets a parameter of this DAO.
     * 
     * @param namespace the namespace or null.
     * @param name the parameter name.
     * @return the parameter value, if any.
     */
    private String getParameter(String namespace, String name) {
        if (name == null) {
            return null;
        }
        else {
            if (namespace != null) {
                name = namespace + NAMESPACE_SEPARATOR + name;
            }
            return parameters.getProperty(name);
        }
    }

    /**
     * Gets the transaction of this DAO. This method returns null if this DAO
     * is not under a transaction.
     * 
     * @return the transaction of this DAO.
     * @throws DAOException if unable to get the transaction.
     * @see hk.hku.cecid.piazza.commons.dao.DAO#getTransaction()
     */
    public Transaction getTransaction() throws DAOException {
        return getTransaction(false);
    }
    
    /**
     * Gets the transaction of this DAO. This method returns a new transaction 
     * if and only if this DAO is not under a transaction and the create 
     * parameter is set to true. Notice that a newly created transaction will 
     * not be set as the transaction of this DAO. It is only created for 
     * convenience of use.
     * 
     * @param create true if a new transaction should be created if this DAO is 
     *               not under a transaction.
     * @return the transaction of this DAO or a new transaction.
     * @throws DAOException if unable get or create a transaction.
     */
    protected DataSourceTransaction getTransaction(boolean create) throws DAOException {
        if (transaction == null && create) {
            return createTransaction();
        }
        else {
            return (DataSourceTransaction)transaction;
        }
    }
    
    /**
     * Creates a new DataSourceTransaction object from the underlying data 
     * source factory.
     * 
     * @return a new DataSourceTransaction object.
     * @throws DAOException if unable to create the transaction.
     */
    protected DataSourceTransaction createTransaction() throws DAOException {
        return (DataSourceTransaction)factory.createTransaction();
    }
    
    /**
     * Sets the transaction of this DAO.
     * 
     * @param tx the transaction of this DAO.
     * @throws DAOException if the given transaction is not supported.
     */
    public void setTransaction(Transaction tx) throws DAOException {
        if (tx == null) {
            transaction = null;
        }
        else if (tx instanceof DataSourceTransaction) {
            transaction = (DataSourceTransaction)tx;
        }
        else {
            throw new DAOException("Unsupported transaction type: " + tx.getClass());
        }
    }
    
    /**
     * Checks if this DAO is under a transaction.
     * 
     * @return true if this DAO is under a transaction.
     */
    public boolean isUnderTransaction() {
        return transaction != null;
    }
    
    /**
     * Retrieves data from the data source by searching with the specified key
     * values.
     * 
     * @param keys the key values for querying the data source.
     * @return a DVO found by the specified key values. If there are more
     *         than one DVO found, the first one will be returned. null will
     *         be returned if nothing was found.
     * @throws DAOException if errors found when retrieving data from the data
     *             source.
     */
    protected DVO findByKey(Object[] keys) throws DAOException {
        List data = find("key", keys);
        if (data.size() > 0) {
            return (DVO) data.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * Retrieves data from the data source as a List of DVO by searching
     * with the specified data values.
     * 
     * @param finder the name of the finder SQL statement.
     * @param paras the parameter values used by the finder statement.
     * @return a List of DVO found by the specified data values. An empty
     *         List will be returned if there is no matching data.
     * @throws DAOException if errors found when retrieving data from the data
     *             source.
     */
    protected List find(String finder, Object[] paras) throws DAOException {
        String sql = getFinder(finder);

        if (sql == null) {
            throw new DAOException("Finder '" + finder + "' not defined");
        }

        return executeQuery(sql, paras);
    }

    /**
     * Updates the data source with the specified data values.
     * 
     * @param sqlname the name of the SQL statement to be used.
     * @param paras the data values to be updated to the data source.
     * @return an integer indicating the update result. Same as the value
     *         returned by java.sql.Statement.executeUpdate()
     * @throws DAOException if errors found when updating data to the data
     *             source.
     */
    protected int update(String sqlname, Object[] paras) throws DAOException {
        String sql = getSQL(sqlname);

        if (sql == null) {
            throw new DAOException("SQL '" + sqlname + "' not defined");
        }

        return executeUpdate(sql, paras);
    }

    /**
     * Executes a SQL query on the data source and returns a List of DVO
     * which matches.
     * 
     * @param sql the SQL query statement.
     * @return a List of DVO resulted from the specified SQL query. An empty
     *         List will be returned if there is no matching data.
     * @throws DAOException if errors found when retrieving data from the data
     *             source.
     */
    protected List executeQuery(String sql) throws DAOException {
        return executeQuery(null, sql);
    }

    /**
     * Executes a SQL query on the data source and returns a List of DVO
     * which matches.
     * 
     * @param tx the DataSourceTransaction to be used in execution.
     * @param sql the SQL query statement.
     * @return a List of DVO resulted from the specified SQL query. An empty
     *         List will be returned if there is no matching data.
     * @throws DAOException if errors found when retrieving data from the data
     *             source.
     */
    protected List executeQuery(DataSourceTransaction tx, String sql)
            throws DAOException {
        return executeQuery(tx, sql, null);
    }

    /**
     * Executes a SQL query on the data source and returns a List of DVO
     * which matches.
     * 
     * @param sql the SQL query statement.
     * @param paras the parameter values used by the specified SQL statement.
     *            The number, format, and sequence of the parameter values
     *            should match the statement. null if there is no parameter for
     *            the statement.
     * @return a List of DVO resulted from the specified SQL query. An empty
     *         List will be returned if there is no matching data.
     * @throws DAOException if errors found when retrieving data from the data
     *             source.
     */
    protected List executeQuery(String sql, Object[] paras) throws DAOException {
        return executeQuery(null, sql, paras);
    }

    /**
     * Executes a SQL query on the data source and returns a List of DVO
     * which matches.
     * 
     * @param tx the DataSourceTransaction to be used in execution.
     * @param sql the SQL query statement.
     * @param paras the parameter values used by the specified SQL statement.
     *            The number, format, and sequence of the parameter values
     *            should match the statement. null if there is no parameter for
     *            the statement.
     * @return a List of DVO resulted from the specified SQL query. An empty
     *         List will be returned if there is no matching data.
     * @throws DAOException if errors found when retrieving data from the data
     *             source.
     */
    protected List executeQuery(DataSourceTransaction tx, String sql, Object[] paras)
            throws DAOException {
        DataSourceQuery query = new DataSourceQuery(this, tx, sql, paras);
        query.start();
        return (List) query.getResult();
    }

    /**
     * Executes a SQL query on the data source and returns a List of raw data 
     * which matches.
     * 
     * @param sql the SQL query statement.
     * @param paras the parameter values used by the specified SQL statement.
     *            The number, format, and sequence of the parameter values
     *            should match the statement. null if there is no parameter for
     *            the statement.
     * @return a List of DVO resulted from the specified SQL query. An empty
     *         List will be returned if there is no matching data.
     * @throws DAOException if errors found when retrieving data from the data
     *             source.
     */
    protected List executeRawQuery(String sql, Object[] paras)
            throws DAOException {
        return executeRawQuery(null, sql, paras);
    }
    
    /**
     * Executes a SQL query on the data source and returns a List of raw data 
     * which matches.
     * 
     * @param tx the DataSourceTransaction to be used in execution.
     * @param sql the SQL query statement.
     * @param paras the parameter values used by the specified SQL statement.
     *            The number, format, and sequence of the parameter values
     *            should match the statement. null if there is no parameter for
     *            the statement.
     * @return a List of DVO resulted from the specified SQL query. An empty
     *         List will be returned if there is no matching data.
     * @throws DAOException if errors found when retrieving data from the data
     *             source.
     */
    protected List executeRawQuery(DataSourceTransaction tx, String sql, Object[] paras)
            throws DAOException {
        DataSourceQuery query = new DataSourceQuery(this, tx, sql, paras);
        query.setRawResult(true);
        query.start();
        return (List) query.getResult();
    }
    
    /**
     * Executes a SQL update on the data source and returns an integer
     * indicating the update result.
     * 
     * @param sql the SQL update statement.
     * @return an integer indicating the update results. Same as the value
     *         returned by java.sql.Statement.executeUpdate().
     * @throws DAOException if errors found when updating data to the data
     *             source.
     */
    protected int executeUpdate(String sql) throws DAOException {
        return executeUpdate(null, sql);
    }

    /**
     * Executes a SQL update on the data source and returns an integer
     * indicating the update result.
     * 
     * @param tx the DataSourceTransaction to be used in execution.
     * @param sql the SQL update statement.
     * @return an integer indicating the update results. Same as the value
     *         returned by java.sql.Statement.executeUpdate().
     * @throws DAOException if errors found when updating data to the data
     *             source.
     */
    protected int executeUpdate(DataSourceTransaction tx, String sql)
            throws DAOException {
        return executeUpdate(tx, sql, null)[0];
    }

    /**
     * Executes a SQL update on the data source and returns an integer
     * indicating the update result.
     * 
     * @param sql the SQL update statement.
     * @param paras the parameter values used by the specified SQL statement.
     *            The number, format, and sequence of the parameter values
     *            should match the statement. null if there is no parameter for
     *            the statement.
     * @return an integer indicating the update results. Same as the value
     *         returned by java.sql.Statement.executeUpdate().
     * @throws DAOException if errors found when updating data to the data
     *             source.
     */
    protected int executeUpdate(String sql, Object[] paras) throws DAOException {
        return executeUpdate(null, sql, paras);
    }

    /**
     * Executes a SQL update on the data source and returns an integer
     * indicating the update result.
     * 
     * @param tx the DataSourceTransaction to be used in execution.
     * @param sql the SQL update statement.
     * @param paras the parameter values used by the specified SQL statement.
     *            The number, format, and sequence of the parameter values
     *            should match the statement. null if there is no parameter for
     *            the statement.
     * @return an integer indicating the update results. Same as the value
     *         returned by java.sql.Statement.executeUpdate()
     * @throws DAOException if errors found when updating data to the data
     *             source.
     */
    protected int executeUpdate(DataSourceTransaction tx, String sql, Object[] paras)
            throws DAOException {
        return executeUpdate(tx, sql, new Object[][]{paras})[0];
    }

    /**
     * Executes a SQL update on the data source and returns an array of integers
     * indicating the update results.
     * 
     * @param sql the SQL update statement.
     * @param paras the parameter values used by the specified SQL statement.
     *            The number, format, and sequence of the parameter values
     *            should match the statement. null if there is no parameter for
     *            the statement and multiple sets if it is a batch update.
     * @return an array of integers indicating the update results. Same as the
     *         value returned by java.sql.Statement.executeUpdate().
     * @throws DAOException if errors found when updating data to the data
     *             source.
     */
    protected int[] executeUpdate(String sql, Object[][] paras)
            throws DAOException {
        return executeUpdate(null, sql, paras);
    }

    /**
     * Executes a SQL update on the data source and returns an array of integers
     * indicating the update results.
     * 
     * @param tx the DataSourceTransaction to be used in execution.
     * @param sql the SQL update statement.
     * @param paras the parameter values used by the specified SQL statement.
     *            The number, format, and sequence of the parameter values
     *            should match the statement. null if there is no parameter for
     *            the statement and multiple sets if it is a batch update.
     * @return an array of integers indicating the update results. Same as the
     *         value returned by java.sql.Statement.executeUpdate().
     * @throws DAOException if errors found when updating data to the data
     *             source.
     */
    protected int[] executeUpdate(DataSourceTransaction tx, String sql, Object[][] paras)
            throws DAOException {
        DataSourceUpdate update = new DataSourceUpdate(this, tx, sql, paras);
        update.start();
        return (int[]) update.getResult();
    }

    /**
     * Creates the given value object in the data source.
     * 
     * @param data the value object.
     * @throws DAOException if unable to create the data.
     * @throws ClassCastException if the value object is not DataSourceDVO.
     * @see hk.hku.cecid.piazza.commons.dao.DAO#create(hk.hku.cecid.piazza.commons.dao.DVO)
     */
    public void create(DVO data) throws DAOException {
        checkSQLBuilder();
        String[] cols;
        if (new Boolean(getParameter(NAMESPACE_SELECTIVE, PARAM_SEL_INSERT))
                .booleanValue()) {
            cols = getColumnNames(((DataSourceDVO) data).getDirties());
        }
        else {
            cols = builder.getColumns();
        }
        create(data, cols);
    }

    /**
     * Creates the given value object in the data source according to the given
     * columns.
     * 
     * @param data the value object.
     * @param columns the columns to be updated.
     * @throws DAOException if unable to create the data.
     * @throws ClassCastException if the value object is not DataSourceDVO.
     * @see #create(DVO)
     */
    private void create(DVO data, String[] columns) throws DAOException {
        String sql = builder.getInsertStatement(columns);
        Object[] values = getValues(data, columns);
        int result = executeUpdate(sql, values);
        if (result != 1) {
            throw new DAOException("Unexpected creation error. Records updated: "+result);
        }
    }

    /**
     * Persists the given value object to the data source.
     * 
     * @param data the value object.
     * @return true if the data is found and persisted.
     * @throws DAOException if unable to persist the data.
     * @throws ClassCastException if the DAO data is not DataSourceDVO.
     * @see hk.hku.cecid.piazza.commons.dao.DAO#persist(hk.hku.cecid.piazza.commons.dao.DVO)
     */
    public boolean persist(DVO data) throws DAOException {
        checkSQLBuilder();
        String[] cols;
        if (new Boolean(getParameter(NAMESPACE_SELECTIVE, PARAM_SEL_UPDATE))
                .booleanValue()) {
            cols = getColumnNames(((DataSourceDVO) data).getDirties());
        }
        else {
            cols = builder.getColumns();
        }
        return persist(data, cols);
    }

    /**
     * Persists the given value object to the data source according to the given
     * columns.
     * 
     * @param data the value object.
     * @param columns the columns to be updated.
     * @return true if the data is found and persisted.
     * @throws DAOException if unable to persist the data.
     * @throws ClassCastException if the value object is not DataSourceDVO.
     * @see #persist(DVO)
     */
    private boolean persist(DVO data, String[] columns) throws DAOException {
        String sql = builder.getUpdateStatement(columns);
        Object[] values1 = getValues(data, columns);
        Object[] values2 = getValues(data, builder.getKeys());
        List values = new ArrayList();
        values.addAll(Arrays.asList(values1));
        values.addAll(Arrays.asList(values2));
        int result = executeUpdate(sql, values.toArray());
        if (result > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Retrieves the given value object from the data source.
     * 
     * @param data the value object.
     * @return true if the data is found and retrieved.
     * @throws DAOException if unable to retrieve the data.
     * @throws ClassCastException if the value object is not DataSourceDVO.
     * @see hk.hku.cecid.piazza.commons.dao.DAO#retrieve(hk.hku.cecid.piazza.commons.dao.DVO)
     */
    public boolean retrieve(DVO data) throws DAOException {
        checkSQLBuilder();
        String sql = builder.getSelectStatement();
        Object[] values = getValues(data, builder.getKeys());
        List result = executeQuery(sql, values);
        if (result.size() > 0) {
            DataSourceDVO d = (DataSourceDVO) result.get(0);
            ((DataSourceDVO) data).setData(d.getData());
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Removes the given value object from the data source.
     * 
     * @param data the value object.
     * @return true if the data is found and removed.
     * @throws DAOException if unable to remove the data.
     * @throws ClassCastException if the value object is not DataSourceDVO.
     * @see hk.hku.cecid.piazza.commons.dao.DAO#remove(hk.hku.cecid.piazza.commons.dao.DVO)
     */
    public boolean remove(DVO data) throws DAOException {
        checkSQLBuilder();
        String sql = builder.getDeleteStatement();
        Object[] values = getValues(data, builder.getKeys());
        int result = executeUpdate(sql, values);
        if (result > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Checks if the SQL builder for this DAO is valid for further operation.
     * 
     * @throws DAOException if the SQL builder is invalid.
     */
    private void checkSQLBuilder() throws DAOException {
        if (!builder.isValid()) {
            throw new DAOException(
                    "Insufficient information provided by this DAO. Operation not supported.");
        }
    }

    /**
     * Gets the values stored in the given value object according to the specified
     * columns.
     * 
     * @param data the value object.
     * @param columns the columns to be retrieved.
     * @throws ClassCastException if the value object is not DataSourceDVO.
     * @return the retrieved values of the given value object.
     */
    private Object[] getValues(DVO data, String[] columns) {
        ArrayList values = new ArrayList();
        for (int i = 0; i < columns.length; i++) {
            String columnCodeName = getColumnCodeName(columns[i]);
            Object value = columnCodeName == null ? null
                    : ((DataSourceDVO) data).get(columnCodeName);
            values.add(value);
        }
        return values.toArray();
    }

    /**
     * Gets the columns names from the column code names.
     * 
     * @param codeNames the column code names.
     * @return the column names.
     */
    private String[] getColumnNames(String[] codeNames) {
        String[] columnNames = new String[codeNames == null ? 0
                : codeNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = getColumnName(codeNames[i]);
        }
        return columnNames;
    }

    /**
     * Gets the column name from the column code name.
     * 
     * @param codeName the column code name.
     * @return the column name.
     */
    private String getColumnName(String codeName) {
        if (codeName != null) {
            Iterator columns = columnCodeNames.entrySet().iterator();
            while (columns.hasNext()) {
                Map.Entry entry = (Map.Entry) columns.next();
                if (entry.getValue().equals(codeName)) {
                    return (String) entry.getKey();
                }
            }
        }
        return null;
    }
}