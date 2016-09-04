package hk.hku.cecid.edi.sfrm.util;

import junit.framework.Assert;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDAO;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.edi.sfrm.util.StatusQuery;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;
import hk.hku.cecid.piazza.commons.test.PluginTest;

/**
 * @author Patrick Yip
 *
 */
public class StatusQueryTest extends PluginTest<SFRMProcessor> {
	
	private SFRMProcessor sfrmProcessor;
	
	@Override
	public String getDBName() {
		return "dao";
	}

	@Override
	public DataSourceDAO getDSDAO() throws Exception {
		return (DataSourceDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
	}

	@Override
	public void init() {
		sfrmProcessor = (SFRMProcessor) processor;
		isLoadDB = true;
	}
	
	/**
	 * Test for checking the message progress status
	 * @throws Exception
	 */
	public void testCheckMessageStatus() throws Exception{
		SFRMMessageSegmentDAO segDAO = (SFRMMessageSegmentDAO) sfrmProcessor.getMessageSegmentHandler().getDAOInstance();
		StatusQuery query = new StatusQuery("testCheckMessageStatus", segDAO);
		query.init();
		query.start();
		query.tick();
		
		Assert.assertEquals("Message status should be PR", "PR", query.getStatus());
		Assert.assertEquals("Message status description should be 'Message is processing'", "Message is Processing", query.getStatusDesc());
		Assert.assertEquals("Number of segment for message should be 3", 3, query.getNumOfSegments());
		Assert.assertEquals("Number of processed segment should be 2", 2, query.getNumOfProcessedSegments());
		Assert.assertEquals("Segment size should be 50", 50, query.getSegmentSize());
	}
	
	/**
	 * Test for if checking the message without called start method of StatusQuery
	 * @throws Exception
	 */
	public void testCheckMessageStatusNotStarted() throws Exception{
		SFRMMessageSegmentDAO segDAO = (SFRMMessageSegmentDAO) sfrmProcessor.getMessageSegmentHandler().getDAOInstance();
		StatusQuery query = new StatusQuery("testCheckMessageStatusNotStarted", segDAO);
		query.init();
		query.tick();
		
		Assert.assertNull("Message status should be null", query.getStatus());
		Assert.assertNull("Message status description should be null", query.getStatusDesc());
		Assert.assertEquals("Number of segment for message should be 1", 1, query.getNumOfSegments());
		Assert.assertEquals("Number of processed segment should be -1", -1, query.getNumOfProcessedSegments());
		Assert.assertEquals("Segment size should be 50", 50, query.getSegmentSize());
	}
	
	/**
	 * Test for checking the message which is non-existing
	 * @throws Exception
	 */
	public void testCheckNonExistingMessageStatus() throws Exception{
		SFRMMessageSegmentDAO segDAO = (SFRMMessageSegmentDAO) sfrmProcessor.getMessageSegmentHandler().getDAOInstance();
		StatusQuery query = new StatusQuery("testCheckNonExistingMessageStatus", segDAO);
		boolean thrown = false;
		try{
			query.init();
		}catch(Exception e){
			thrown = true;
		}
		
		Assert.assertTrue("There should throw an exception, since message ID: testCheckNonExistingMessageStatus not existing", thrown);
	}
	
}
