package hk.hku.cecid.edi.as2.dao.test;

import java.io.InputStream;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import hk.hku.cecid.edi.as2.dao.RepositoryDVO;
import hk.hku.cecid.edi.as2.dao.RepositoryDataSourceDAO;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.test.DAOTest;
import hk.hku.cecid.piazza.commons.test.utils.FixtureStore;


public class RepositoryDataSourceDAOTest extends DAOTest<RepositoryDataSourceDAO> {
	
	private static final String originalMessageId = "TestPaylodMessage@RepositoryDataSourceDAOTest";
	private static final String msgBox = "inbox";
	private static final String ori_binary_content = "test.bin"; // 55MB binary file to insert to database
	private static final String diff_binary_content = "test_diff.bin"; // 55MB binary file to insert to database
	
	// Fixture loader
	private static ClassLoader 	FIXTURE_LOADER	= FixtureStore.createFixtureLoader(false, RepositoryDataSourceDAOTest.class);
	
	
	@Override
	public String getTableName() {
		return "repository";
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		System.out.println("Preparing Database Data * * *");
		logger.info("Preparing Database Data * * *");
		for(int i =0; i < 100000; i++){
			RepositoryDataSourceDAO dao = super.getTestingTarget();
			RepositoryDVO dvo = (RepositoryDVO) dao.createDVO();
			dvo.setMessageBox(msgBox);
			dvo.setMessageId(AS2Message.generateID());
			dao.persist(dvo);
		}
		
	};
	
	@Before
	public void initialTest() throws Exception{
		System.out.println("Init Test Data.");
		RepositoryDataSourceDAO dao = super.getTestingTarget();
		RepositoryDVO dvo = (RepositoryDVO) dao.createDVO();
		InputStream ins = FIXTURE_LOADER.getResourceAsStream(ori_binary_content);
		
		logger.info("Preparing Database Data * * *");
		dvo.setMessageBox(msgBox);
		dvo.setMessageId(originalMessageId);
		
		// Check if the record is already exists
		boolean result = dao.retrieve(dvo);
		logger.info("Record Found: " + result);
		
		dvo.setContent(IOHandler.readBytes(ins));
		long start = System.currentTimeMillis();
		logger.info("* * * Begin Presist Data: " + new Date(start));
		dao.persist(dvo);
		long end = System.currentTimeMillis();
		logger.info("* * * Finished Presist Data: " + new Date(end) + "  Spent: "+ (end-start) +" ms"  );
	}
	
	@After
	public void dropRecord() throws Exception{
		System.out.println("Clean test data.");
		RepositoryDataSourceDAO dao = super.getTestingTarget();
		RepositoryDVO dvo = (RepositoryDVO) dao.createDVO();
		InputStream ins = FIXTURE_LOADER.getResourceAsStream(ori_binary_content);
		
		logger.info("Dispose Database Data * * *");
		dvo.setMessageBox(msgBox);
		dvo.setMessageId(originalMessageId);
		
		// Check if the record is already exists
		dao.remove(dvo);
	}
	
	@Test
	public void updateRepositoryRecord() throws DAOException, Exception{
		RepositoryDataSourceDAO dao = super.getTestingTarget();
		RepositoryDVO dvo = (RepositoryDVO) dao.createDVO();
		dvo.setMessageId(originalMessageId);
		dvo.setMessageBox(msgBox);
		
		//Retreive Original Record from database
		long start = System.currentTimeMillis();
		logger.info("* * * Begin Query Data: " + new Date(start));
		dao.retrieve(dvo);
		long end = System.currentTimeMillis();
		logger.info("* * * Finished Query Data: " + new Date(end) + "  Spent: "+ (end-start) +" ms"  );
		
		//Update Record by another binary content
		InputStream ins = FIXTURE_LOADER.getResourceAsStream(diff_binary_content);
		dvo.setContent(IOHandler.readBytes(ins));
		start = System.currentTimeMillis();
		logger.info("* * * Begin Update: " + new Date(start));
		dao.persist(dvo);
		end = System.currentTimeMillis();
		logger.info("* * * Finished Updare: " + new Date(end) + "  Spent: "+ (end-start) +" ms"  );
		System.out.println("* * * Finished testUpdateRepositoryRecord: " + new Date(end) + "  Spent: "+ (end-start) +" ms"  );
	}

	@Test
	public void insertRepositoryRecord() throws DAOException, Exception{
		RepositoryDataSourceDAO dao = super.getTestingTarget();
		RepositoryDVO dvo = (RepositoryDVO) dao.createDVO();
		dvo.setMessageId(originalMessageId);
		dvo.setMessageBox(msgBox);
		
		//Retreive Original Record from database
		long start = System.currentTimeMillis();
		logger.info("* * * Begin Query Data: " + new Date(start));
		dao.retrieve(dvo);
		long end = System.currentTimeMillis();
		logger.info("* * * Finished Query Data: " + new Date(end) + "  Spent: "+ (end-start) +" ms"  );
		
		//Delete from databse
		logger.info("# # # Begin Remove: " + new Date(start));
		dao.remove(dvo);
		end = System.currentTimeMillis();
		logger.info("# # # Finished Remove: " + new Date(end) + "  Spent: "+ (end-start) +" ms"  );
		
		//Insert Record by another binary content
		InputStream ins = FIXTURE_LOADER.getResourceAsStream(diff_binary_content);
		RepositoryDVO newDvo = (RepositoryDVO) dao.createDVO();
		newDvo.setContent(IOHandler.readBytes(ins));
		newDvo.setMessageId(ori_binary_content);
		newDvo.setMessageBox(msgBox);
		start = System.currentTimeMillis();
		logger.info("* * * Begin Update: " + new Date(start));
		dao.persist(newDvo);
		end = System.currentTimeMillis();
		logger.info("* * * Finished Updare: " + new Date(end) + "  Spent: "+ (end-start) +" ms"  );
		System.out.println("Finished testInsertRepositoryRecord: " + new Date(end) + "  Spent: "+ (end-start) +" ms"  );
	}
	
	/* calling sql update is not as good as just using presist method
	@Test
	public void testUpdateContentBySQL() throws Exception{
		RepositoryDataSourceDAO dao = super.getTestingTarget();
		
		//Update Record by another binary content
		InputStream ins = FIXTURE_LOADER.getResourceAsStream(diff_binary_content);
		long start = System.currentTimeMillis();
		logger.info("* * * Begin Update: " + new Date(start));
		dao.updateContent(IOHandler.readBytes(ins), originalMessageId, msgBox);
		long end = System.currentTimeMillis();
		logger.info("* * * Finished Updare: " + new Date(end) + "  Spent: "+ (end-start) +" ms"  );
		System.out.println("* * * Finished testUpdateRepositoryRecord: " + new Date(end) + "  Spent: "+ (end-start) +" ms"  );
	}*/
}
