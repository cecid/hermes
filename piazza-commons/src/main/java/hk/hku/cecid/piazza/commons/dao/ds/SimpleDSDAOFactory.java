package hk.hku.cecid.piazza.commons.dao.ds;

import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp2.datasources.SharedPoolDataSource;

/**
 * SimpleDSDAOFactory is a subclass of DataSourceDAOFactory and provides an
 * implementation for accessing the data source by a simple DataSource object
 * which is backed by the DriverManager and provides no pooling.
 * <p>
 * Notice that the DAO created by the createDAO() method shall be an instance of
 * DataSourceDAO since this factory is a DataSourceDAOFactory.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class SimpleDSDAOFactory extends DataSourceDAOFactory {

    /**
     * Creates a new instance of SimpleDSDAOFactory.
     */
    public SimpleDSDAOFactory() {
        super();
    }

    /**
     * Initializes this DAOFactory.
     */
    public void initFactory() throws DAOException {
        try {
			String driver = null;
			String url = null;
			String username = null;
			String password = null;
						
			boolean isPooling = true;
			int maxIdle = 0;
			int maxActive = 10;
			int maxWait = 50;
			
			boolean testOnBorrow = false;
			boolean testOnReturn = false;
			boolean testWhileIdle = false;
			String validationQuery = null;
			
			try {
	            driver = getParameter("driver");
	            url = getParameter("url");
	            username = getParameter("username", null);
	            password = getParameter("password", null);
	            
	            maxIdle = StringUtilities.parseInt(getParameter("maxIdle", null), 0);
		        maxActive = StringUtilities.parseInt(getParameter("maxActive", null), 0);
		        maxWait = StringUtilities.parseInt(getParameter("maxWait", null), -1);
				
		        validationQuery = StringUtilities.trim(getParameter("validationQuery", null));
		        if (validationQuery != null) {
		            testOnBorrow = StringUtilities.parseBoolean(getParameter("testOnBorrow", "false"));
		            testOnReturn = StringUtilities.parseBoolean(getParameter("testOnReturn", "false"));
		            testWhileIdle = StringUtilities.parseBoolean(getParameter("testWhileIdle", "false"));
		        }
	            
			} catch (Exception e) {
				throw new DAOException("Invalid parameter for SimpleDSDAOFactory.");
			}
			
			if (getParameter("pooling", null) != null) {
				if (!getParameter("pooling").equalsIgnoreCase("true")
						&& !getParameter("pooling").equalsIgnoreCase("false")) {
					throw new DAOException("Invalid parameter for SimpleDSDAOFactory.");
				}
				isPooling = StringUtilities.parseBoolean(getParameter("pooling", "true"));
			}
				
            DataSource datasource;
            
            if (isPooling) {
                DriverAdapterCPDS cpds = new DriverAdapterCPDS();
                cpds.setDriver(driver);
                cpds.setUrl(url);
                cpds.setUser(username);
                cpds.setPassword(password);
    
                SharedPoolDataSource sds = new SharedPoolDataSource();
                sds.setConnectionPoolDataSource(cpds);
				sds.setDefaultMaxIdle(maxIdle);
                sds.setMaxTotal(maxActive);
                sds.setDefaultMaxWaitMillis(maxWait);
                
            	sds.setDefaultTestOnBorrow(testOnBorrow);
            	sds.setDefaultTestOnReturn(testOnReturn);
            	sds.setDefaultTestWhileIdle(testWhileIdle);
                sds.setValidationQuery(validationQuery);
                	 				
                datasource = sds;
            }
            else {
                datasource = new SimpleDataSource(driver, url, username, password);
            }
            
            setDataSource(datasource);
        }
        catch (Exception e) {
            throw new DAOException("Cannot initialize SimpleDSDAOFactory!", e);
        }
    }

    /**
     * SimpleDataSource is an implementation of DataSource and is backed by the
     * DriverManager. It provides no pooling mechcanism. Moreover, the timeout
     * and log writer features are shared among all its instances rather than
     * each has its own.
     * 
     * @author Hugo Y. K. Lam
     *  
     */
    private class SimpleDataSource implements DataSource {

        private String url, username, password;

        /**
         * Creates a new instance of SimpleDataSource.
         * 
         * @param driver the jdbc driver.
         * @param url the url for connecting to the data source.
         * @throws ClassNotFoundException if the driver class was not found.
         * @throws SQLException if the url is invalid or a database access error
         *             occurs.
         */
        public SimpleDataSource(String driver, String url)
                throws ClassNotFoundException, SQLException {
            this(driver, url, null, null);
        }

        /**
         * Creates a new instance of SimpleDataSource.
         * 
         * @param driver the jdbc driver.
         * @param url the url for connecting to the data source.
         * @param username the username used in connection.
         * @param password the password used in connection.
         * @throws ClassNotFoundException if the driver class was not found.
         * @throws SQLException if the url is invalid or a database access error
         *             occurs.
         */
        public SimpleDataSource(String driver, String url, String username,
                String password) throws ClassNotFoundException, SQLException {
            Class.forName(driver);
            DriverManager.getDriver(url);

            this.url = url;
            this.username = username;
            this.password = password;
        }

        /**
         * @see javax.sql.DataSource#getLoginTimeout()
         */
        public int getLoginTimeout() throws SQLException {
            return DriverManager.getLoginTimeout();
        }

        /**
         * @see javax.sql.DataSource#setLoginTimeout(int)
         */
        public void setLoginTimeout(int seconds) throws SQLException {
            DriverManager.setLoginTimeout(seconds);
        }

        /**
         * @see javax.sql.DataSource#getLogWriter()
         */
        public PrintWriter getLogWriter() throws SQLException {
            return DriverManager.getLogWriter();
        }

        /**
         * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
         */
        public void setLogWriter(PrintWriter out) throws SQLException {
            DriverManager.setLogWriter(out);
        }

        /**
         * @see javax.sql.DataSource#getConnection()
         */
        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(url, username, password);
        }

        /**
         * @see javax.sql.DataSource#getConnection(java.lang.String,
         *      java.lang.String)
         */
        public Connection getConnection(String username, String password)
                throws SQLException {
            return DriverManager.getConnection(url, username, password);
        }

        /**
         * This is required since JDK 1.6. The wrapper is <b>NOT</b> implemented.
         * SQLException will be thrown when calling.
         * 
         * @see java.sql.Wrapper#isWrapperFor(Class<?> iface)
         * @since JDK 1.6    
         */
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            throw new SQLException("JDBC wrapper for: " + iface + " is not implemented.");
        }

        /**
         * This is required since JDK 1.6. The wrapper is <b>NOT</b> implemented.
         * SQLException will be thrown when calling.
         * 
         * @see java.sql.Wrapper#unwrap(Class<T> iface)
         * @since JDK 1.6    
         */
        public <T> T unwrap(Class<T> iface) throws SQLException {
            throw new SQLException("JDBC wrapper for: " + iface + " is not implemented.");
        }

        /**
         * This is required since JDK 1.7.
         *
         */
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            throw new SQLFeatureNotSupportedException("Get parent logger is not implemented.");
        }
    }
    
    
}