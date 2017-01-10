package hk.hku.cecid.ebms.spa.dao;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.test.DAOTest;

public class MessageDataSourceDAOTest extends DAOTest<MessageDataSourceDAO> {

	@Override
	public String getTableName() {
		return "message";
	}
	
	@Test
	public void testFindInboxReadyMaxSequenceNoByCpa() throws DAOException {		
		MessageDataSourceDAO dao = super.getTestingTarget();
        MessageDVO dvo = (MessageDVO)dao.createDVO();
        dvo.setService("cecid:cecid");
        dvo.setAction("order");
        dvo.setConvId("convId");

		int currentSequenceNumber;
		
        // first inbox ordered message 
        dvo.setCpaId("cecid1");
        currentSequenceNumber = dao.findInboxReadyMaxSequenceNoByCpa(dvo);
		Assert.assertEquals(-1, currentSequenceNumber);		

        // last ordered message is 'PS'
		dvo.setCpaId("cecid2");
        currentSequenceNumber = dao.findInboxReadyMaxSequenceNoByCpa(dvo);
		Assert.assertEquals(1, currentSequenceNumber);		
	
        // last ordered message is 'DL'
		dvo.setCpaId("cecid3");
        currentSequenceNumber = dao.findInboxReadyMaxSequenceNoByCpa(dvo);
		Assert.assertEquals(2, currentSequenceNumber);		

	}
	
	@Test
	public void testFindMessageByCpa() throws DAOException {		
        MessageDVO dvo;
        List list;
        MessageDVO result;

		MessageDataSourceDAO dao = super.getTestingTarget();
        
        // test cpa_id, service & action and order (3,4,2,5,1)
		dvo = (MessageDVO)dao.createDVO();
		dvo.setCpaId("cecid4");
		dvo.setService("cecid:cecid");
        dvo.setAction("order");
        list = dao.findMessageByCpa(dvo, 10);
		Assert.assertEquals(5, list.size());
		result = (MessageDVO)list.get(0);
		Assert.assertEquals("convId3", result.getConvId());
		result = (MessageDVO)list.get(1);
		Assert.assertEquals("convId4", result.getConvId());
		result = (MessageDVO)list.get(2);
		Assert.assertEquals("convId2", result.getConvId());
		result = (MessageDVO)list.get(3);
		Assert.assertEquals("convId5", result.getConvId());
		result = (MessageDVO)list.get(4);
		Assert.assertEquals("convId1", result.getConvId());
		
		// test no. of message
		list = dao.findMessageByCpa(dvo, 1);
		Assert.assertEquals(1, list.size());

		// test from party ID
		dvo = (MessageDVO)dao.createDVO();
		dvo.setCpaId("cecid4");
		dvo.setService("cecid:cecid");
        dvo.setAction("order");
		dvo.setFromPartyId("fromPartyId1");
        list = dao.findMessageByCpa(dvo, 10);
        result = (MessageDVO)list.get(0);
		Assert.assertEquals("fromPartyId1", result.getFromPartyId());

		// test from party type
		dvo = (MessageDVO)dao.createDVO();
		dvo.setCpaId("cecid4");
		dvo.setService("cecid:cecid");
        dvo.setAction("order");
		dvo.setFromPartyRole("fromPartyType2");
        list = dao.findMessageByCpa(dvo, 10);
        result = (MessageDVO)list.get(0);
		Assert.assertEquals("fromPartyType2", result.getFromPartyRole());
		
		// test to party ID
		dvo = (MessageDVO)dao.createDVO();
		dvo.setCpaId("cecid4");
		dvo.setService("cecid:cecid");
        dvo.setAction("order");
		dvo.setToPartyId("toPartyId3");
        list = dao.findMessageByCpa(dvo, 10);
        result = (MessageDVO)list.get(0);
		Assert.assertEquals("toPartyId3", result.getToPartyId());

		// test to party type
		dvo = (MessageDVO)dao.createDVO();
		dvo.setCpaId("cecid4");
		dvo.setService("cecid:cecid");
        dvo.setAction("order");
		dvo.setToPartyRole("toPartyType4");
        list = dao.findMessageByCpa(dvo, 10);
        result = (MessageDVO)list.get(0);
		Assert.assertEquals("toPartyType4", result.getToPartyRole());

		// test conv ID
		dvo = (MessageDVO)dao.createDVO();
		dvo.setCpaId("cecid4");
		dvo.setService("cecid:cecid");
        dvo.setAction("order");
		dvo.setConvId("convId5");
        list = dao.findMessageByCpa(dvo, 10);
        result = (MessageDVO)list.get(0);
		Assert.assertEquals("convId5", result.getConvId());
		
		// test all parameters
		dvo = (MessageDVO)dao.createDVO();
		dvo.setCpaId("cecid4");
		dvo.setService("cecid:cecid");
        dvo.setAction("order");
		dvo.setConvId("convId5");
		dvo.setFromPartyId("fromPartyId5");
		dvo.setFromPartyRole("fromPartyType5");
		dvo.setToPartyId("toPartyId5");
		dvo.setToPartyRole("toPartyType5");
        list = dao.findMessageByCpa(dvo, 10);
		Assert.assertEquals(1, list.size());
	}
	
	@Test
	public void testFindMessagesByHistory() throws DAOException {		
		// Test Search Criteria one by one
		MessageDVO criteriaDVO;
		MessageDataSourceDAO dao = super.getTestingTarget();
		List result = null;
	        
	    // Search by message_ID only
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setMessageId("20080311-155320-90407@192.168.0.1");
		result = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals(2, result.size());		
		Assert.assertEquals("20080311-155320-90407@192.168.0.1", ((MessageDVO)result.get(0)).getMessageId()); // Assert MessageID
		Assert.assertEquals("inbox", ((MessageDVO)result.get(0)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(0)).getMessageType()); // Assert Message Type
		Assert.assertEquals("20080311-155320-90407@192.168.0.1", ((MessageDVO)result.get(1)).getMessageId()); // Assert MessageID
		Assert.assertEquals("outbox", ((MessageDVO)result.get(1)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(1)).getMessageType()); // Assert Message Type
		
		criteriaDVO = null;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setMessageId("20080311-155320-90407%");
		result = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals(2, result.size());		
		Assert.assertEquals("20080311-155320-90407@192.168.0.1", ((MessageDVO)result.get(0)).getMessageId()); // Assert MessageID
		Assert.assertEquals("inbox", ((MessageDVO)result.get(0)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(0)).getMessageType()); // Assert Message Type
		Assert.assertEquals("20080311-155320-90407@192.168.0.1", ((MessageDVO)result.get(1)).getMessageId()); // Assert MessageID
		Assert.assertEquals("outbox", ((MessageDVO)result.get(1)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(1)).getMessageType()); // Assert Message Type
		
		
	    // Search by message box and messageid
		criteriaDVO = null;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setMessageBox("inbox");
		criteriaDVO.setMessageId("20080311-155320-90407@192.168.0.1");
		result = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("20080311-155320-90407@192.168.0.1", ((MessageDVO)result.get(0)).getMessageId()); // Assert MessageID
		Assert.assertEquals("inbox", ((MessageDVO)result.get(0)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(0)).getMessageType()); // Assert Message Type
		
	    // Search by Service only
		criteriaDVO = null;
		result = null;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setService("testhistory:testhistory");
		result = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("20080311-155227-07708@192.168.0.1", ((MessageDVO)result.get(0)).getMessageId()); // Assert MessageID
		Assert.assertEquals("testhistory:testhistory", ((MessageDVO)result.get(0)).getService()); // Assert Service
		Assert.assertEquals("Order", ((MessageDVO)result.get(0)).getMessageType()); // Assert Message Type
		Assert.assertEquals("outbox", ((MessageDVO)result.get(0)).getMessageBox()); // Assert Message Box
	
		criteriaDVO = null;
		result = null;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setService("%history");
		result = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("20080311-155227-07708@192.168.0.1", ((MessageDVO)result.get(0)).getMessageId()); // Assert MessageID
		Assert.assertEquals("testhistory:testhistory", ((MessageDVO)result.get(0)).getService()); // Assert Service
		Assert.assertEquals("Order", ((MessageDVO)result.get(0)).getMessageType()); // Assert Message Type
		Assert.assertEquals("outbox", ((MessageDVO)result.get(0)).getMessageBox()); // Assert Message Box

	    // Search by Action
		criteriaDVO = null;
		result = null;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setAction("action");
		result = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("20080311-155223-07106@192.168.0.1", ((MessageDVO)result.get(0)).getMessageId()); // Assert MessageID
		Assert.assertEquals("outbox", ((MessageDVO)result.get(0)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(0)).getMessageType()); // Assert Message Type
		Assert.assertEquals("action", ((MessageDVO)result.get(0)).getAction()); // Assert Action
		
		criteriaDVO = null;
		result = null;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setAction("a%i_n");
		result = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("20080311-155223-07106@192.168.0.1", ((MessageDVO)result.get(0)).getMessageId()); // Assert MessageID
		Assert.assertEquals("outbox", ((MessageDVO)result.get(0)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(0)).getMessageType()); // Assert Message Type
		Assert.assertEquals("action", ((MessageDVO)result.get(0)).getAction()); // Assert Action
		
		
	    // Search by Status
		criteriaDVO = null;
		result = null;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setStatus("PD");
		result = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("20080311-155225-71207@192.168.0.1", ((MessageDVO)result.get(0)).getMessageId()); // Assert MessageID
		Assert.assertEquals("outbox", ((MessageDVO)result.get(0)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(0)).getMessageType()); // Assert Message Type
		Assert.assertEquals("PD", ((MessageDVO)result.get(0)).getStatus()); // Assert Message Status
		
		criteriaDVO = null;
		result = null;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setStatus("P_");	
		result = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals(0, result.size());
				
	    // Search by ConvID only
		criteriaDVO = null;
		result = null;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setConvId("convId_test5");	
		result = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("20080311-155228-42309@192.168.0.1", ((MessageDVO)result.get(0)).getMessageId()); // Assert MessageID
		Assert.assertEquals("outbox", ((MessageDVO)result.get(0)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(0)).getMessageType()); // Assert Message Type
		Assert.assertEquals("convId_test5", ((MessageDVO)result.get(0)).getConvId()); // Assert CovID
		
		criteriaDVO = null;
		result = null;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setConvId("%test5");	
		result = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("20080311-155228-42309@192.168.0.1", ((MessageDVO)result.get(0)).getMessageId()); // Assert MessageID
		Assert.assertEquals("outbox", ((MessageDVO)result.get(0)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(0)).getMessageType()); // Assert Message Type
		Assert.assertEquals("convId_test5", ((MessageDVO)result.get(0)).getConvId()); // Assert CovID
		
		// Test The Ordering(By Time Stamp) and limit number
		criteriaDVO = null;
		result = null;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setService("test:test");
		criteriaDVO.setMessageBox("outbox");	
		result = dao.findMessagesByHistory(criteriaDVO, 2, 0);
		Assert.assertEquals(2, result.size());
		Assert.assertEquals("20080311-155228-42309@192.168.0.1", ((MessageDVO)result.get(0)).getMessageId()); // Assert MessageID
		Assert.assertEquals("20080311-155225-71207@192.168.0.1", ((MessageDVO)result.get(1)).getMessageId()); // Assert MessageID
				
		// Test Space Trimming		 
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setMessageId("20080311-155320-90407@192.168.0.1");
		criteriaDVO.setMessageBox("outbox");
		criteriaDVO.setService("          ");
		criteriaDVO.setAction("           ");
		criteriaDVO.setCpaId("           ");
		criteriaDVO.setConvId("           ");
		result = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals(1, result.size());		
		Assert.assertEquals("20080311-155320-90407@192.168.0.1", ((MessageDVO)result.get(0)).getMessageId()); // Assert MessageID
		Assert.assertEquals("outbox", ((MessageDVO)result.get(0)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(0)).getMessageType()); // Assert Message Type
		Assert.assertEquals("test:test", ((MessageDVO)result.get(0)).getService());
		Assert.assertEquals("testhistory5", ((MessageDVO)result.get(0)).getCpaId());
		Assert.assertEquals("convId_test1", ((MessageDVO)result.get(0)).getConvId());
				
		// Search By all criteria case
		criteriaDVO = null;
		result = null;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setMessageId("%@192.168.0.1");
		criteriaDVO.setService("test%history%");
		criteriaDVO.setConvId("convId%");
		criteriaDVO.setAction("order");
		criteriaDVO.setStatus("");
		result = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals("20080311-155227-07708@192.168.0.1", ((MessageDVO)result.get(0)).getMessageId()); // Assert MessageID
		Assert.assertEquals("testhistory:testhistory", ((MessageDVO)result.get(0)).getService()); // Assert Service
		Assert.assertEquals("outbox", ((MessageDVO)result.get(0)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(0)).getMessageType()); // Assert Message Type
				
		// Search By '%' in criteria the criteria that using equal instead of 'like'
		criteriaDVO = null;
		result = null;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setMessageId("%@192.168.0.1");
		criteriaDVO.setService("test%history");		
		result = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals("20080311-155227-07708@192.168.0.1", ((MessageDVO)result.get(0)).getMessageId()); // Assert MessageID
		Assert.assertEquals("outbox", ((MessageDVO)result.get(0)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(0)).getMessageType()); // Assert Message Type
		Assert.assertEquals("testhistory:testhistory", ((MessageDVO)result.get(0)).getService()); // Assert Service
		
		// Test Search By All Message Box and Message Status when service is "test:test"
		criteriaDVO = null;
		result = null;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setService("test:test");
		criteriaDVO.setMessageBox("");
		criteriaDVO.setStatus("");	
		result = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals(5, result.size());
		Assert.assertEquals("20080311-155228-42309@192.168.0.1", ((MessageDVO)result.get(0)).getMessageId()); // Assert MessageID
		Assert.assertEquals("outbox", ((MessageDVO)result.get(0)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(0)).getMessageType()); // Assert Message Type
		
		Assert.assertEquals("20080311-155225-71207@192.168.0.1", ((MessageDVO)result.get(1)).getMessageId()); // Assert MessageID
		Assert.assertEquals("outbox", ((MessageDVO)result.get(1)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(1)).getMessageType()); // Assert Message Type		
		
		Assert.assertEquals("20080311-155223-07106@192.168.0.1", ((MessageDVO)result.get(2)).getMessageId()); // Assert MessageID
		Assert.assertEquals("outbox", ((MessageDVO)result.get(2)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(2)).getMessageType()); // Assert Message Type
		
		Assert.assertEquals("20080311-155320-90407@192.168.0.1", ((MessageDVO)result.get(3)).getMessageId()); // Assert MessageID
		Assert.assertEquals("inbox", ((MessageDVO)result.get(3)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(3)).getMessageType()); // Assert Message Type
				
		Assert.assertEquals("20080311-155320-90407@192.168.0.1", ((MessageDVO)result.get(4)).getMessageId()); // Assert MessageID
		Assert.assertEquals("outbox", ((MessageDVO)result.get(4)).getMessageBox()); // Assert Message Box
		Assert.assertEquals("Order", ((MessageDVO)result.get(4)).getMessageType()); // Assert Message Type		
		
		// Test Negative case
		criteriaDVO = null;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setMessageBox("%box");	
		result = dao.findMessagesByHistory(criteriaDVO, 100, 0);			
		Assert.assertEquals(0, result.size());
		
		criteriaDVO = null;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setStatus("%L");	
		result = dao.findMessagesByHistory(criteriaDVO, 100, 0);			
		Assert.assertEquals(0, result.size());		
	}	
	
	@Test
	public void testFindNumberOFMessagesByHistory() throws DAOException {		
		// Test Search Criteria one by one
		MessageDVO criteriaDVO;
		MessageDataSourceDAO dao = super.getTestingTarget();
		int result = -1;
	        
	    // Search by message_ID only
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setMessageId("20080311-155320-90407@192.168.0.1");
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(2, result);		
		
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setMessageId("20080311-155320-90407%");
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(2, result);		
		
	    // Search by message box and messageid
		criteriaDVO = null;
		result = -1;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setMessageBox("inbox");
		criteriaDVO.setMessageId("20080311-155320-90407@192.168.0.1");
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(1, result);
		
	    // Search by Service only
		criteriaDVO = null;
		result = -1;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setService("testhistory:testhistory");
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(1, result);
		
		criteriaDVO = null;
		result = -1;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setService("test%history");
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(1, result);

	    // Search by Action only
		criteriaDVO = null;
		result = -1;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setAction("action");
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(1, result);
		
		criteriaDVO = null;
		result = -1;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setAction("a%i_n");
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(1, result);
		
	    // Search by Status only
		criteriaDVO = null;
		result = -1;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setStatus("PD");	
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(1, result);
		
	    // Search by ConvID only
		criteriaDVO = null;
		result = -1;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setConvId("convId_test5");		
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(1, result);
		
		criteriaDVO = null;
		result = -1;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setConvId("%test5");		
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(1, result);
		
		// Test Space Triming
		criteriaDVO = null;
		result = -1;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setMessageId("20080311-155320-90407@192.168.0.1");
		criteriaDVO.setMessageBox("outbox");
		criteriaDVO.setService("          ");
		criteriaDVO.setAction("           ");
		criteriaDVO.setCpaId("           ");
		criteriaDVO.setConvId("           ");
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(1, result);		
		
		// Search By all criteria case
		criteriaDVO = null;		
		result = -1;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setMessageId("%@192.168.0.1");
		criteriaDVO.setService("test%history%");
		criteriaDVO.setConvId("convId%");
		criteriaDVO.setAction("order");
		criteriaDVO.setStatus("");
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(1, result);	
		
		// Test Negative case
		criteriaDVO = null;
		result = -1;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setMessageBox("%box");	
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);			
		Assert.assertEquals(0, result);
		
		criteriaDVO = null;
		result = -1;
		criteriaDVO = (MessageDVO)dao.createDVO();
		criteriaDVO.setStatus("%L");	
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);			
		Assert.assertEquals(0, result);		
	}
}
	