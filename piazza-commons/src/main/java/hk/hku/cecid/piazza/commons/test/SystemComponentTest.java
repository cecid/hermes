/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.test;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import hk.hku.cecid.piazza.commons.dao.DAO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceProcess;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceTransaction;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.module.ModuleGroup;
import hk.hku.cecid.piazza.commons.module.SystemComponent;
import hk.hku.cecid.piazza.commons.test.utils.FixtureStore;
import hk.hku.cecid.piazza.commons.util.Logger;

public abstract class SystemComponentTest<SC extends SystemComponent> {

	protected static String MODULE_GROUP_XML = "module-group.xml";
	
	protected ClassLoader FIXTURE_LOADER;
	protected ModuleGroup MODULE_GROUP;	
	protected SC TARGET;
	protected Logger LOG;	
	protected Random RANDOM;
	
	protected File TMP_DIR;
	
	public abstract String getSystemComponentId();
	
	@Before
	public void before() throws Exception{
		TMP_DIR = new File(System.getProperty("java.io.tmpdir"));
		
		FIXTURE_LOADER = FixtureStore.createFixtureLoader(false, this.getClass());
		// add folder conf & security to classloader
		FIXTURE_LOADER = FixtureStore.addFixturesToLoader(FIXTURE_LOADER, 
				new URL[] {FIXTURE_LOADER.getResource("conf/"), FIXTURE_LOADER.getResource("security/"), FIXTURE_LOADER.getResource("sql/")});
		MODULE_GROUP = new ModuleGroup(MODULE_GROUP_XML, FIXTURE_LOADER);
		
		TARGET = (SC) MODULE_GROUP.getSystemModule().getComponent(getSystemComponentId());
		Assert.assertNotNull("System component - " + TARGET.getClass() + " not found", TARGET);
		
		LOG = TARGET.getLogger();
		
		RANDOM = new Random(System.currentTimeMillis());
		
		LOG.info("---------- BEGIN -----------");
		
		setUp();
	}
	
	@After
	public void after() throws Exception {
		tearDown();
		
		LOG.info("----------  END  -----------\n");
	}

	public abstract void setUp() throws Exception;
	public abstract void tearDown() throws Exception;
	
	protected DataSourceDAO initDatasourceDAO(Class<? extends DAO> daoClass) throws DAOException {
		DataSourceDAO dao = (DataSourceDAO)TARGET.getDAOFactory().createDAO(daoClass);
		Assert.assertNotNull("DAO - " + daoClass + " not found", dao);
		
		return dao;
	}
	
	protected void commitSQL(Class<? extends DAO> daoClass, String sqlName) throws Exception {

		// Get the create table SQL from file.
		URL sqlUrl = FIXTURE_LOADER.getResource(sqlName);
		Assert.assertNotNull("SQL file - " + sqlUrl + " not found", sqlUrl);
		
		InputStream sqlStream = sqlUrl.openStream();		
		
		// Read the SQL.
		final String sql = IOHandler.readString(sqlStream, null);
		String canonicalizedSql = sql.replace("(?! \\S)\\s+", " ");
		LOG.info("Execute SQL: \n " + canonicalizedSql);
		
		DataSourceDAO dao = initDatasourceDAO(daoClass);
		
		DataSourceProcess process = new DataSourceProcess(dao) {
		  protected void doTransaction(DataSourceTransaction tx) throws DAOException {
			  Statement stmt = null;
			  try {
				  Connection conn = tx.getConnection();
				  stmt = conn.createStatement();
				  Assert.assertTrue(stmt.executeUpdate(sql) != -1);
				  
			  } catch (SQLException sqlex) {
				  throw new DAOException(sqlex);	// re-throw.
			  } finally {
				  if (stmt != null) {
					  try {
						  stmt.close();
					  } catch(SQLException sqlex) {
						  LOG.error("Unable to close statement", sqlex);
					  }
				  }
			  }
		  }
		};
		process.start();
	}

}
