package hk.hku.cecid.piazza.commons.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceProcess;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceTransaction;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.spa.PluginHandler;
import hk.hku.cecid.piazza.commons.spa.Plugin;
import hk.hku.cecid.piazza.commons.test.utils.FixtureStore;
import junit.framework.TestCase;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Assert;

/**
 * Testing class used to kick up all the necessary thing for testing the plugin
 * 
 * @author Patrick Yip
 * @since 2.0.0
 * @param <T>
 *            the Processor class to test against with
 */
public abstract class PluginTest<T extends PluginHandler> extends TestCase {

	public static final String pluginDescriptor = "plugin.xml";
	public static final String CREATE_SQL_SUFFIX = ".create.sql";
	public static final String DROP_SQL_SUFFIX = ".drop.sql";
	public static final String INSERT_SQL_SUFFIX = ".insert.sql";

	protected ClassLoader FIXTURE_LOADER = FixtureStore.createFixtureLoader(
			false, this.getClass());
	protected File baseFile = new File(FixtureStore.getFixtureURL(
			this.getClass()).getFile());
	protected boolean isLoadDB = false;
	protected PluginHandler processor;

	/**
	 * Setup the plugin module
	 */
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		// Mocking the plugin
		Plugin plugin = new Plugin(null, baseFile, pluginDescriptor);
		Class processorClass = getParameterizedClass();
		processor = (PluginHandler) processorClass.getConstructor(
				new Class[] {}).newInstance(null);
		processor.processActivation(plugin);
		init();
		if (isLoadDB == true) {
			loadDB();
		}
		postSetUp();
	}

	public void tearDown() throws Exception {
		if (isLoadDB == true) {
			unloadDB();
		}
		postTearDown();
	}

	@SuppressWarnings("unchecked")
	public Class getParameterizedClass() {
		Class searchClass = this.getClass();
		Type type = searchClass.getGenericSuperclass();
		Type[] allTypes = ((ParameterizedType) type).getActualTypeArguments();
		return (Class) allTypes[0];
	}

	public void loadDB() throws Exception {
		commitSQL(getDBName() + CREATE_SQL_SUFFIX);
		commitSQL(getDBName() + INSERT_SQL_SUFFIX);
	}

	public void unloadDB() throws Exception {
		commitSQL(getDBName() + DROP_SQL_SUFFIX);
	}

	public void commitSQL(String fixtureName) throws Exception {
		// Get the create table SQL from file.
		URL resourceURL = FIXTURE_LOADER.getResource(fixtureName);

		if (resourceURL == null) {
			return;
		}

		InputStream resourceStream = resourceURL.openStream();

		// Read the SQL.
		final String sql = IOHandler.readString(resourceStream, null);
		final String canonicalizedSql = sql.replace("(?! \\S)\\s+", " ");
		DataSourceProcess process = new DataSourceProcess(getDSDAO()) {
			protected void doTransaction(DataSourceTransaction tx)
					throws DAOException {
				Statement stmt = null;
				try {
					Connection conn = tx.getConnection();
					stmt = conn.createStatement();
					Assert.assertThat(stmt.executeUpdate(sql), not(is(-1)));

				} catch (SQLException sqlex) {
					throw new DAOException(sqlex); // re-throw.
				} finally {
					if (stmt != null) {
						try {
							stmt.close();
						} catch (SQLException sqlex) {
							System.out.println("Unable to close statement");
							sqlex.printStackTrace();
						}
					}
				}
			}
		};
		process.start();
	}

	public abstract void init();

	public abstract DataSourceDAO getDSDAO() throws Exception;

	public abstract String getDBName();

	public void postSetUp() throws Exception {
	};

	public void postTearDown() throws Exception {
	};
}
