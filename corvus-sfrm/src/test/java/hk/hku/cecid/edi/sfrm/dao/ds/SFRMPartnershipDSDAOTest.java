package hk.hku.cecid.edi.sfrm.dao.ds;

import org.junit.Assert;
import org.junit.Test;
import hk.hku.cecid.piazza.commons.test.DAOTest;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDVO;
import java.util.List;
import java.util.Calendar;

public class SFRMPartnershipDSDAOTest extends DAOTest<SFRMPartnershipDSDAO>{

	@Override
	public String getTableName(){
		return "partnership";
	}
	
	@Test
	public void testFindPartnershipBySeq() throws DAOException{
		//If the parternship is existing
		SFRMPartnershipDSDAO dao = super.getTestingTarget();		
		SFRMPartnershipDSDVO dvo = (SFRMPartnershipDSDVO)dao.findPartnershipBySeq(8);
		Assert.assertEquals("Partnership Seq didn't matched", 8, dvo.getPartnershipSeq());
		Assert.assertEquals("Partnership Id didn't matched", "test1", dvo.getPartnershipId());
		Assert.assertEquals("Transport Endpoint didn't matched", "test1/corvus/httpd/sfrm/inbound", dvo.getPartnerEndpoint());
		
		dao = super.getTestingTarget();		
		dvo = (SFRMPartnershipDSDVO)dao.findPartnershipBySeq(9);
		Assert.assertEquals("Partnership Seq didn't matched", 9, dvo.getPartnershipSeq());
		Assert.assertEquals("Partnership Id didn't matched", "test2", dvo.getPartnershipId());
		Assert.assertEquals("Transport Endpoint didn't matched", "test2/corvus/httpd/sfrm/inbound", dvo.getPartnerEndpoint());
		
		dao = super.getTestingTarget();		
		dvo = (SFRMPartnershipDSDVO)dao.findPartnershipBySeq(10);
		Assert.assertEquals("Partnership Seq didn't matched", 10, dvo.getPartnershipSeq());
		Assert.assertEquals("Partnership Id didn't matched", "test3", dvo.getPartnershipId());
		Assert.assertEquals("Transport Endpoint didn't matched", "test3/corvus/httpd/sfrm/inbound", dvo.getPartnerEndpoint());	
	}
	
	@Test
	//Negative case
	//Test to find the non-existing partnership seq
	public void testFindPartnershipBySeqNeg() throws DAOException{
		SFRMPartnershipDSDAO dao = super.getTestingTarget();		
		SFRMPartnershipDSDVO dvo = (SFRMPartnershipDSDVO)dao.findPartnershipBySeq(8);
		dao = super.getTestingTarget();		
		dvo = (SFRMPartnershipDSDVO)dao.findPartnershipBySeq(-1);
		Assert.assertNull("Partnership with seq id -1 doesn;t exist, but it can find one", dvo);
	}
	
	@Test
	public void testFindPartnershipBySeqObj() throws DAOException{
		//If partnership is existing
		SFRMPartnershipDSDAO dao = super.getTestingTarget();
		SFRMPartnershipDSDVO dvo = (SFRMPartnershipDSDVO)dao.createDVO();
		dvo.setPartnershipSeq(9);
		SFRMPartnershipDVO new_dvo = dao.findPartnershipBySeq(dvo);
		Assert.assertEquals("Partnership Seq didn't matched", 9, new_dvo.getPartnershipSeq());
		Assert.assertEquals("Partnership Id didn't matched", "test2", new_dvo.getPartnershipId());
		Assert.assertEquals("Transport Endpoint didn't matched", "test2/corvus/httpd/sfrm/inbound", new_dvo.getPartnerEndpoint());
		
		dao = super.getTestingTarget();
		dvo = (SFRMPartnershipDSDVO)dao.createDVO();
		dvo.setPartnershipSeq(8);
		new_dvo = dao.findPartnershipBySeq(dvo);
		Assert.assertEquals("Partnership Seq didn't matched", 8, new_dvo.getPartnershipSeq());
		Assert.assertEquals("Partnership Id didn't matched", "test1", new_dvo.getPartnershipId());
		Assert.assertEquals("Transport Endpoint didn't matched", "test1/corvus/httpd/sfrm/inbound", new_dvo.getPartnerEndpoint());
		
		dao = super.getTestingTarget();
		dvo = (SFRMPartnershipDSDVO)dao.createDVO();
		dvo.setPartnershipSeq(10);
		new_dvo = dao.findPartnershipBySeq(dvo);
		Assert.assertEquals("Partnership Seq didn't matched", 10, new_dvo.getPartnershipSeq());
		Assert.assertEquals("Partnership Id didn't matched", "test3", new_dvo.getPartnershipId());
		Assert.assertEquals("Transport Endpoint didn't matched", "test3/corvus/httpd/sfrm/inbound", new_dvo.getPartnerEndpoint());
	}
	
	@Test
	//Negative case
	//Test to find the non-existing partnership seq
	public void testFindPartnershipBySeqObjNeg() throws DAOException{
		SFRMPartnershipDSDAO dao = super.getTestingTarget();
		SFRMPartnershipDSDVO dvo = (SFRMPartnershipDSDVO)dao.createDVO();
		dvo.setPartnershipSeq(-2);
		SFRMPartnershipDVO new_dvo = dao.findPartnershipBySeq(dvo);
		new_dvo = dao.findPartnershipBySeq(dvo);
		Assert.assertNull("Partnership with seq id -2 doesn't exist, but it can find one", new_dvo);
	}
	
	@Test
	public void testFindPartnershipById() throws DAOException{
		//If the partnership id is existing
		SFRMPartnershipDSDAO dao = super.getTestingTarget();
		SFRMPartnershipDVO dvo = (SFRMPartnershipDVO)dao.findPartnershipById("test1");
		Assert.assertEquals("Partnership Seq didn't matched", 8, dvo.getPartnershipSeq());
		Assert.assertEquals("Partnership Id didn't matched", "test1", dvo.getPartnershipId());
		Assert.assertEquals("Transport Endpoint didn't matched", "test1/corvus/httpd/sfrm/inbound", dvo.getPartnerEndpoint());
		
		dao = super.getTestingTarget();
		dvo = (SFRMPartnershipDVO)dao.findPartnershipById("test2");
		Assert.assertEquals("Partnership Seq didn't matched", 9, dvo.getPartnershipSeq());
		Assert.assertEquals("Partnership Id didn't matched", "test2", dvo.getPartnershipId());
		Assert.assertEquals("Transport Endpoint didn't matched", "test2/corvus/httpd/sfrm/inbound", dvo.getPartnerEndpoint());
		
		dao = super.getTestingTarget();
		dvo = (SFRMPartnershipDVO)dao.findPartnershipById("test3");
		Assert.assertEquals("Partnership Seq didn't matched", 10, dvo.getPartnershipSeq());
		Assert.assertEquals("Partnership Id didn't matched", "test3", dvo.getPartnershipId());
		Assert.assertEquals("Transport Endpoint didn't matched", "test3/corvus/httpd/sfrm/inbound", dvo.getPartnerEndpoint());	
	}
	
	
	@Test
	//Negative case
	//Test to find the non-existing partnership ID
	public void testFindPartnershipByIdNeg() throws DAOException{
		SFRMPartnershipDSDAO dao = super.getTestingTarget();
		SFRMPartnershipDVO dvo = (SFRMPartnershipDVO)dao.findPartnershipById("test1");
		dao = super.getTestingTarget();
		dvo = (SFRMPartnershipDVO)dao.findPartnershipById("non_existing_partnership_id");
		Assert.assertNull("Partnership with partnership id 'non_existing_partnership_id' doesn't exist, but it can find one", dvo);
	}
	@Test
	public void testFindPartnershipByIdObj() throws DAOException{
		//If the partnership id is existing
		SFRMPartnershipDSDAO dao = super.getTestingTarget();
		SFRMPartnershipDVO dvo = (SFRMPartnershipDVO)dao.createDVO();
		dvo.setPartnershipId("test3");
		SFRMPartnershipDVO new_dvo = dao.findPartnershipById(dvo);
		Assert.assertEquals("Partnership Seq didn't matched", 10, new_dvo.getPartnershipSeq());
		Assert.assertEquals("Partnership Id didn't matched", "test3", new_dvo.getPartnershipId());
		Assert.assertEquals("Transport Endpoint didn't matched", "test3/corvus/httpd/sfrm/inbound", new_dvo.getPartnerEndpoint());
		
		dao = super.getTestingTarget();
		dvo = (SFRMPartnershipDVO)dao.createDVO();
		dvo.setPartnershipId("test1");
		new_dvo = dao.findPartnershipById(dvo);
		Assert.assertEquals("Partnership Seq didn't matched", 8, new_dvo.getPartnershipSeq());
		Assert.assertEquals("Partnership Id didn't matched", "test1", new_dvo.getPartnershipId());
		Assert.assertEquals("Transport Endpoint didn't matched", "test1/corvus/httpd/sfrm/inbound", new_dvo.getPartnerEndpoint());
		
		dao = super.getTestingTarget();
		dvo = (SFRMPartnershipDVO)dao.createDVO();
		dvo.setPartnershipId("test2");
		new_dvo = dao.findPartnershipById(dvo);
		Assert.assertEquals("Partnership Seq didn't matched", 9, new_dvo.getPartnershipSeq());
		Assert.assertEquals("Partnership Id didn't matched", "test2", new_dvo.getPartnershipId());
		Assert.assertEquals("Transport Endpoint didn't matched", "test2/corvus/httpd/sfrm/inbound", new_dvo.getPartnerEndpoint());
		
	}
	
	@Test
	//Negative case
	//Test to find the non-existing partnership ID
	public void testFindPartnershipByIdObjNeg() throws DAOException{
		SFRMPartnershipDSDAO dao = super.getTestingTarget();
		SFRMPartnershipDVO dvo = (SFRMPartnershipDVO)dao.createDVO();
		SFRMPartnershipDVO new_dvo = dao.findPartnershipById(dvo);
		dvo.setPartnershipId("non_existing_dumnmy_partnership_id");
		new_dvo = dao.findPartnershipById(dvo);
		Assert.assertNull("Partnership with partnership id 'non_existing_dumnmy_partnership_id' doesn't exist, but it can find one", new_dvo);
	}
	
	@Test
	public void testFindAllPartnerships() throws DAOException{
		SFRMPartnershipDSDAO dao = super.getTestingTarget();
		List partnerships = dao.findAllPartnerships();
		Assert.assertEquals("Number of partnership no[t match", 3, partnerships.size());
		
		SFRMPartnershipDVO dvo = (SFRMPartnershipDVO) partnerships.get(0);
		Assert.assertEquals("Partnership Seq didn't matched", 8, dvo.getPartnershipSeq());
		Assert.assertEquals("Partnership Id didn't matched", "test1", dvo.getPartnershipId());
		Assert.assertEquals("Transport Endpoint didn't matched", "test1/corvus/httpd/sfrm/inbound", dvo.getPartnerEndpoint());
		
		dvo = (SFRMPartnershipDVO) partnerships.get(1);
		Assert.assertEquals("Partnership Seq didn't matched", 9, dvo.getPartnershipSeq());
		Assert.assertEquals("Partnership Id didn't matched", "test2", dvo.getPartnershipId());
		Assert.assertEquals("Transport Endpoint didn't matched", "test2/corvus/httpd/sfrm/inbound", dvo.getPartnerEndpoint());
		
		dvo = (SFRMPartnershipDVO) partnerships.get(2);
		Assert.assertEquals("Partnership Seq didn't matched", 10, dvo.getPartnershipSeq());
		Assert.assertEquals("Partnership Id didn't matched", "test3", dvo.getPartnershipId());
		Assert.assertEquals("Transport Endpoint didn't matched", "test3/corvus/httpd/sfrm/inbound", dvo.getPartnerEndpoint());
	}
	
	@Test
	/*
	 * Insert a new partnership with no invalid data
	 */
	public void testInsertPartnership() throws DAOException{
		SFRMPartnershipDSDAO dao = super.getTestingTarget();
		SFRMPartnershipDVO dvo = (SFRMPartnershipDVO) dao.createDVO();
		dvo.setPartnershipSeq(1);
		dvo.setPartnershipId("dummy_id");
		dvo.setDescription("This is the dummy partnership");
		dvo.setEncryptAlgorithm("3des");
		dvo.setSignAlgorithm("sha1");
		dvo.setIsDisabled(false);
		dvo.setIsHostnameVerified(false);
		dvo.setSignAlgorithm(null);
		dvo.setEncryptAlgorithm(null);
		dvo.setPartnerEndPoint("http://localhost");
		dvo.setRetryInterval(6000);
		dvo.setRetryMax(3);
		//Assert if it can insert successfully
		try{
			dao.create(dvo);
		}catch(DAOException daoe){
			Assert.fail("Failure when inserting a new partnership: " + daoe);
		}

		//Assert if it can retrieve the inserted partnership
		Assert.assertEquals("Partnership ID didn't matched", "dummy_id", dvo.getPartnershipId());
	}
	
	@Test
	/**
	 * Negative Case
	 * Test to insert the partnership with invalid data
	 */
	public void testInsertPartnershipNeg() throws DAOException{
		SFRMPartnershipDSDAO dao = super.getTestingTarget();
		SFRMPartnershipDVO dvo = (SFRMPartnershipDVO) dao.createDVO();
		dvo.setPartnershipSeq(1);
		dvo.setDescription("This is the dummy partnership");
		dvo.setEncryptAlgorithm("3des");
		dvo.setSignAlgorithm("sha1");
		dvo.setIsDisabled(false);
		dvo.setIsHostnameVerified(false);
		dvo.setPartnerEndPoint("http://localhost");
		dvo.setRetryInterval(6000);
		dvo.setRetryMax(3);
		boolean isValid = true;
		try{
			dao.create(dvo);
		}catch(DAOException daoe){
			isValid = false;
		}
		Assert.assertFalse("No exception throws when inserting a partnership with invalid data", isValid);
		//Check that whether it is actually inserted into the database
		SFRMPartnershipDVO new_dvo = (SFRMPartnershipDVO) dao.createDVO();
		new_dvo.setDescription("This is the dummy partnership");
		Assert.assertFalse("Invalid partnership was inserted to the database", dao.retrieve(new_dvo));
	}
	
	@Test 
	public void testUpdatePartnership() throws DAOException{
		SFRMPartnershipDSDAO dao = super.getTestingTarget();
		SFRMPartnershipDVO dvo = dao.findPartnershipById("test1");
		dvo.setDescription("test1 description updated");
		
		Assert.assertTrue("Partnership updated not successful", dao.persist(dvo));
		//Check the partnership is really updated
		SFRMPartnershipDVO updated_dvo = dao.findPartnershipById("test1");
		Assert.assertEquals("Description field for partnership wasn't updated", "test1 description updated", updated_dvo.getDescription());	
	}
	
	@Test
	/**
	 * Negative case
	 * Update the partnership with invalid data 
	 */
	public void testUpdatePartnershipNeg() throws DAOException{
		SFRMPartnershipDSDAO dao = super.getTestingTarget();
		SFRMPartnershipDVO dvo = dao.findPartnershipById("test2");
		//Set the transport endpoint to null
		dvo.setPartnerEndPoint(null);
		boolean updateSuccess = true;
		try{
			dao.persist(dvo);
		}catch(DAOException daoe){
			updateSuccess = false;
		}
		Assert.assertFalse("Partnership updated with the invalid data", updateSuccess);
		
		//Check if the partnership is not updated
		SFRMPartnershipDVO new_dvo = dao.findPartnershipById("test2");
		Assert.assertEquals("Partnership updated successsfully with invalid data", "This is test 2", new_dvo.getDescription());
	}
	
	@Test 
	/*
	 * Test for deleting an existing partnership
	 */
	public void testDeletePartnership() throws DAOException{
		SFRMPartnershipDSDAO dao = super.getTestingTarget();
		SFRMPartnershipDVO dvo = dao.findPartnershipById("test1");
		boolean removed = dao.remove(dvo);
		Assert.assertTrue("Existing partenrship cannot be remove from database", removed);
		
		//Check if the partnership is  really removed from the database
		SFRMPartnershipDVO new_dvo = dao.findPartnershipById("test1");
		Assert.assertNull("Partnership deleted before, but it still existing in the database", new_dvo);
	}		
	
	@Test
	/**
	 * Check all the field of partnership DVO, to see whether the retrieve data is the same as in the database
	 */
	public void testCheckAllFields() throws DAOException{
		SFRMPartnershipDSDAO dao = super.getTestingTarget();
		SFRMPartnershipDVO dvo = dao.findPartnershipById("test1");
		Assert.assertEquals("Partnership ID didn't matched", "test1", dvo.getPartnershipId());
		Assert.assertEquals("Sequence No. didn't matched", 8, dvo.getPartnershipSeq());
		Assert.assertEquals("Description didn't matched", "This is test 1", dvo.getDescription());
		Assert.assertEquals("Partner Endpoint didn't match", "test1/corvus/httpd/sfrm/inbound", dvo.getPartnerEndpoint());
		Assert.assertEquals("Orginial Partner Endpoint didn't matched", "test1", dvo.getOrgPartnerEndpoint());
		Assert.assertEquals("Certificate Finger Print didn't matched", "0A51EE7101B535ABC9F39414A93C76E7DC768C7B", dvo.getPartnerCertFingerprint());
		Assert.assertEquals("Is hostname verified didn't matched", false, dvo.isHostnameVerified());
		Assert.assertEquals("Sign Algorithm didn't matched", "sha1", dvo.getSignAlgorithm());
		Assert.assertEquals("Encrypt Algorithm didn't matched", "3des", dvo.getEncryptAlgorithm());
		Assert.assertEquals("Retry Max didn't matched", 3, dvo.getRetryMax());
		Assert.assertEquals("Retry Interval didn't matched", 60000, dvo.getRetryInterval());
		Assert.assertEquals("Is Disabled didn't matched", false, dvo.isDisabled());
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dvo.getCreationTimestamp().getTime());
		
		Assert.assertEquals("Creation year didn't matched", 2008, cal.get(Calendar.YEAR));
		Assert.assertEquals("Creation month didn't matched", 8, cal.get(Calendar.MONTH));
		Assert.assertEquals("Creation day didn't matched", 18, cal.get(Calendar.DATE));
		Assert.assertEquals("Creation hour didn't matched", 3, cal.get(Calendar.HOUR));
		Assert.assertEquals("Creation minute didn't matched", 14, cal.get(Calendar.MINUTE));
		Assert.assertEquals("Creation second didn't matched", 35, cal.get(Calendar.SECOND));
		Assert.assertEquals("Creation timestamp didn't matched", "2008-09-18 15:14:35.794198", dvo.getCreationTimestamp().toString());
		
		cal.setTimeInMillis(dvo.getModifiedTimestamp().getTime());
		Assert.assertEquals("Modified year didn't matched", 2008, cal.get(Calendar.YEAR));
		Assert.assertEquals("Modified month didn't matched", 8, cal.get(Calendar.MONTH));
		Assert.assertEquals("Modified day didn't matched", 18, cal.get(Calendar.DATE));
		Assert.assertEquals("Modified hour didn't matched", 3, cal.get(Calendar.HOUR));
		Assert.assertEquals("Modified minute didn't matched", 14, cal.get(Calendar.MINUTE));
		Assert.assertEquals("Modified second didn't matched", 35, cal.get(Calendar.SECOND));
		Assert.assertEquals("Modified timestamp didn't matched", "2008-09-18 15:14:35.794198", dvo.getModifiedTimestamp().toString());
	}
	
}
