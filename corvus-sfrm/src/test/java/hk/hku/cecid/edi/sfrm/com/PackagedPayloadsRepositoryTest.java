package hk.hku.cecid.edi.sfrm.com;

import org.junit.Assert;
import org.junit.Test;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import hk.hku.cecid.piazza.commons.test.SystemComponentTest;

import hk.hku.cecid.edi.sfrm.com.PackagedPayloadsRepository;
import hk.hku.cecid.piazza.commons.test.utils.ResSetter;

public class PackagedPayloadsRepositoryTest extends SystemComponentTest<PackagedPayloadsRepository> {
	
	private PackagedPayloadsRepository repo;
	private ResSetter res;
	
	private int numOfPayloads = 2;
	private int numOfReadyPayloads = 2;
	
	@Override
	public String getSystemComponentId() {
		return "outgoing-payload-repository"; 
	}
	
	public void setUp() throws Exception{
		res = new ResSetter(this.getClass());
		res.restore();

		repo = (PackagedPayloadsRepository)TARGET;
	}
	
	public void tearDown() throws Exception{
		res.clean();
	}
	
	@Test
	public void testFindingRepository() throws Exception{
		Assert.assertNotNull("Repository should not be null", repo.getRepository());	
	}
	
	/**
	 * Test number of payload in repository
	 * @throws Exception
	 */
	@Test
	public void testGetPayloads() throws Exception{
		Collection payloads = repo.getPayloads();
		Assert.assertEquals("Number of payloads should be " + Integer.toString(numOfPayloads) + " but " + Integer.toString(payloads.size()), numOfPayloads, payloads.size());
	}
	
	/**
	 * Test whether it can retreive the payload that is ready to send
	 * @throws Exception
	 */
	@Test
	public void testGetReadyPayloads() throws Exception{
		Collection payloads = repo.getReadyPayloads();
		//Check the number of ready to send payload
		Assert.assertEquals("Number of ready to send payload should be " + Integer.toString(numOfReadyPayloads) + ", but it is " + Integer.toString(payloads.size()), 2, payloads.size());
		Iterator iter = payloads.iterator();
		while(iter.hasNext()){
			PackagedPayloads pp = (PackagedPayloads) iter.next();
			Assert.assertEquals("packaged payload partnership_id is not 'partnership'", "partnership", pp.getPartnershipId());
			Assert.assertTrue("Message id not match", (pp.getRefMessageId().equals("msg_id_1") || pp.getRefMessageId().equals("msg_id_2")));
		}
	}
	
	/**
	 * Test creating a processing payload
	 * @throws Exception
	 */
	@Test
	public void testCreateProcessingPayload() throws Exception{
		String partner = "partnerC";
		String msg = "msg3";
		PackagedPayloads pp = (PackagedPayloads) repo.createPayloads(new Object[]{"partnerC", "msg3"}, PayloadsState.PLS_PROCESSING);
		
		Assert.assertEquals("Partnership id not match", partner, pp.getPartnershipId());
		Assert.assertEquals("Message id not match", msg, pp.getRefMessageId());
	}
	
	/**
	 * Test renaming the payload to processed status
	 * @throws Exception
	 */
	@Test
	public void testRenamePayloadToProcessed() throws Exception{
		NamedPayloads pp = repo.getPayload("partnership$msg_id_1.sfrm");
		boolean flag = pp.setToProcessed();
		Assert.assertTrue("Fail on renaming the payload to processing", flag);
		
		File fileProcessed = new File(repo.getRepository(), "%%partnership$msg_id_1.sfrm");
		File fileReady = new File(repo.getRepository(), "partnership$msg_id_1.sfrm");
		
		Assert.assertFalse("The ready to payload still existing", fileReady.exists());
		Assert.assertTrue("Renamed processing payload didn't exist", fileProcessed.exists());
	}
	
	/**
	 * Test renaming the payload to processing status
	 * @throws Exception
	 */
	@Test
	public void testRenamePayloadToProcessing() throws Exception{
		NamedPayloads pp = repo.getPayload("partnership$msg_id_1.sfrm");
		boolean flag = pp.setToProcessing();
		Assert.assertTrue("Fail on renaming the payload to processing", flag);
		
		File fileProcessing = new File(repo.getRepository(), "##partnership$msg_id_1.sfrm");
		File fileReady = new File(repo.getRepository(), "partnership$msg_id_1.sfrm");
		
		Assert.assertFalse("The ready to payload still existing", fileReady.exists());
		Assert.assertTrue("Renamed processing payload didn't exist", fileProcessing.exists());
	}		
}
