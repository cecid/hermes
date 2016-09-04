/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.net.URL;

import org.junit.After;
import org.junit.Assert;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DAOFactory;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceProcess;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceTransaction;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.module.Module;
import hk.hku.cecid.piazza.commons.util.Logger;

/** 
 * The <code>DAOTest</code> is base class for testing DAO. 
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   JDK5.0, H2O 0908
 * 
 * @param <T> The class which implements DataSourceDAO.
 * 
 * @see DataSourceDAO 
 */
public abstract class DAOTest<T extends DataSourceDAO> extends UnitTest<T> 
{		
	protected Module container;
	protected Logger containerLogger;
	protected DAOFactory daoFactory;
	
	/* The suffix of the predefined fixture name */
	public static final String CREATE_SQL_SUFFIX			= ".create.sql";
	public static final String DROP_SQL_SUFFIX				= ".drop.sql";
	public static final String INSERT_SQL_SUFFIX			= ".insert.sql";
	public static final String MODULE_XML_DESCRIPTOR_SUFFIX = ".module.mock.xml";
	
	/**
	 * Create an instance of <code>DAOTest</code>. By default, it disable JMOCK features for reducing dependency. 
	 */
	public DAOTest()
	{
		super(false);
	}

	/**
	 * Create an instance of <code>DAOTest</code>.
	 * 
	 * @param noMocking the flag representing the test requires object mocking or not ?
	 */
	public DAOTest(boolean noMocking)
	{
		super(noMocking);
	}

	// The boolean flag indicating weather the DB is successfully created.
	private boolean created = false;

	/**
	 * Return the DB table name accessed by this DAO.  
	 * 
	 * @return the DB table name accessed by this DAO.  
	 */
	public abstract String getTableName();	
	
	/**
	 * 
	 * @return
	 */
	public Module getTestContainer()
	{
		if (this.container == null)
		{
			throw new IllegalStateException("The container has not been set?, forget to invoke method setUp()?");
		}
		return this.container;
	}
		
	/**
	 * Initialize the test target for this test-case.
	 * <br/><br/>
	 * A Special piazza common module is constructed during this initialization. We will 
	 * call the module as a container of our test-case because the <code>DAO</code>
	 * can only be constructed through <code>DAOFactory</code>.
	 * <br/><br/>
	 * Moreover, we have to create the database table and insert some SQL for testing
	 * because of non-persistence database has been adopted for performing unit-testing.
	 */	
	@SuppressWarnings("unchecked")
	public synchronized void initTestTarget() throws Exception
	{
		// Get the DAO parameter type.
		Class searchClass 		= this.getClass();
		Class actualDAOClass	= null;
		
		while (true)
		{
			Type  type = searchClass.getGenericSuperclass();
			Class typeClass = type.getClass();
			
			if (ParameterizedType.class.isAssignableFrom(typeClass))
			{
				Type [] allTypes = ((ParameterizedType)type).getActualTypeArguments();
				
				if (allTypes.length < 1)
				{
					throw new IllegalArgumentException("Missing DVO type in the generic parameter type.");
				}			
				else
				{
					actualDAOClass = (Class)allTypes[0];
					break;
				}
			}
			else if (Class.class.isAssignableFrom(typeClass))
			{
				searchClass = (Class) type;
			}			
		}
		
		logger.info("Using DAO-class : {}", actualDAOClass);
		
		this.container 		 = this.createDAOContainer();
		this.daoFactory		 = (DAOFactory) this.container.getComponent("daofactory");	
		this.target			 = (T) this.daoFactory.createDAO(actualDAOClass);
		
		if (this.container.getLogger() != null)
		{
			this.containerLogger = this.container.getLogger();
		}
		
		// Create the table.
		this.commitSQL(this.getTableName() + CREATE_SQL_SUFFIX);
		// Insert some sample data.
		this.commitSQL(this.getTableName() + INSERT_SQL_SUFFIX);		
		this.created = true;
	}
	
	/**
	 * This is the factory to create the DAO container (typically it is a common module).
	 * Sub-class may override this to customize the DAO container. 
	 */
	public Module createDAOContainer() throws Exception
	{
		String tableName = this.getTableName();
		
		if (tableName == null || tableName.equals(""))
		{
			throw new NullPointerException("You must return the table name from 'getTableName()'.");
		}
		
		// Get the XML descriptor from resource.
		String resourceName	 	= tableName + MODULE_XML_DESCRIPTOR_SUFFIX;		
		URL xmlDescriptorURL 	= FIXTURE_LOADER.getResource(resourceName);
		String xmlDescriptor	= null;
		
		if (xmlDescriptorURL == null)
		{
			throw new NullPointerException("Unable to load module descriptor '" + resourceName + "' in the classpath.");
		}
		
		xmlDescriptor = xmlDescriptorURL.getFile();
		
		// Start the container.		
		return new Module(xmlDescriptor, FIXTURE_LOADER, true);
	}
	
	/**
	 * This is a helper function for commit SQL from Fixture.
	 */
	protected void commitSQL(String fixtureName) throws Exception
	{
		// Get the create table SQL from file.
		URL resourceURL = FIXTURE_LOADER.getResource(fixtureName);
		
		if (resourceURL == null)
		{
			this.containerLogger.warn("Unable to search '" + fixtureName + "' in the classpath");
			return;
		}
		
		InputStream resourceStream = resourceURL.openStream();		
		
		// Read the SQL.
		final String sql = IOHandler.readString(resourceStream, null);
		final String canonicalizedSql = sql.replace("(?! \\S)\\s+", " ");
		
		this.containerLogger.info("Execute SQL: \n " + canonicalizedSql);
		
		DataSourceProcess process = new DataSourceProcess(this.target)
		{
			  protected void doTransaction(DataSourceTransaction tx) throws DAOException 
			  {
				  Statement stmt = null;
				  try
				  {
					  Connection conn = tx.getConnection();
					  stmt = conn.createStatement();
					  Assert.assertThat(stmt.executeUpdate(sql), not(is(-1)));
					  
				  }
				  catch(SQLException sqlex)
				  {
					  throw new DAOException(sqlex);	// re-throw.
				  }
				  finally
				  {
					  if (stmt != null)
					  {
						  try
						  {
							  stmt.close();
						  }
						  catch(SQLException sqlex)
						  {
							  containerLogger.error("Unable to close statement", sqlex);
						  }
					  }
				  }
			  }
		};
		process.start();
	}
	 	
	@Override
	public void tearDown() throws Exception
	{
		super.tearDown();
		this.dropTable();
	}

	/**
	 * Drop the table for next test-case.
	 */
	public synchronized void dropTable() throws Exception 
	{
		if (this.created)
		{	
			try
			{
				String tableName = this.getTableName();			
				this.commitSQL(tableName + DROP_SQL_SUFFIX);
			}
			catch(DAOException daoex)
			{
				logger.error("Unable to tearDown the {} DB table", this.getTableName());
				throw daoex;
			}
		}
	}
}