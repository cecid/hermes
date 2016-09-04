package hk.hku.cecid.edi.as2.dao.test;

import java.util.List;

import org.junit.Test;
import junit.framework.Assert;

import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.edi.as2.dao.MessageDataSourceDAO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.test.DAOTest;


public class MessageDataSourcesDAOTest extends DAOTest<MessageDataSourceDAO>{

	@Override
	public String getTableName() {
		return "message";
	}

	@Test
	public void testFindNumberOfMessageByHistory() throws DAOException{
		System.out.println("Start test Dynamic Filter Query on MessageDataSourceDAO . . .");
		logger.info("Start test Dynamic Filter Query on MessageDataSourceDAO . . .");
		
		//Positive Case
		MessageDataSourceDAO dao  = super.getTestingTarget();		
		MessageDVO criteriaDVO = (MessageDVO) dao.createDVO();
		int result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(10, result);
		
		criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setMessageBox(MessageDVO.MSGBOX_IN);		
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(5, result);
		
		criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setMessageId("20090910-120000-12345@127.0.0.1");		
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(1, result);
		
		criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setMessageId("%120000%");
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(10, result);
		
		criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setAs2From("as2From");
		criteriaDVO.setAs2To("as2To");
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(10, result);
		
		criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setStatus(MessageDVO.STATUS_DELIVERY_FAILURE);	
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(2, result);
		
		criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setAs2From(null);
		criteriaDVO.setAs2To(null);
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(10, result);
		
		//Negative Case
		criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setMessageId("ABC");		
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(0, result);
		
		criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setMessageBox("INOUT");		
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(0, result);
		
		criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setAs2From("as2To");
		criteriaDVO.setAs2To("as2From");
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(0, result);
		
		criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setAs2From("as2From");
		criteriaDVO.setAs2To("as2From");
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(0, result);
		
		criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setAs2From("");
		criteriaDVO.setAs2To("   ");
		result = dao.findNumberOfMessagesByHistory(criteriaDVO);
		Assert.assertEquals(0, result);
	}
	
	@Test
	public void testFindfMessageByHistory() throws DAOException{
		System.out.println("Start test Dynamic Filter Query on MessageDataSourceDAO . . .");
		logger.info("Start test Dynamic Filter Query on MessageDataSourceDAO . . .");
		
		// Test Message Box
		MessageDataSourceDAO dao  = super.getTestingTarget();
		MessageDVO criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setMessageBox(MessageDVO.MSGBOX_IN);		
		List<MessageDVO> resultList = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals(5, resultList.size());
		for(MessageDVO dvo: resultList){
			Assert.assertEquals(MessageDVO.MSGBOX_IN, dvo.getMessageBox());
		}
		
		// Test Message Box and Status
		criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setMessageBox(MessageDVO.MSGBOX_IN);
		criteriaDVO.setStatus(MessageDVO.STATUS_PROCESSED);
		resultList = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals(2, resultList.size());
		for(MessageDVO dvo: resultList){
			Assert.assertEquals(MessageDVO.MSGBOX_IN, dvo.getMessageBox());
			Assert.assertEquals(MessageDVO.STATUS_PROCESSED, dvo.getStatus());
		}

		// Test Message AS2 From  and AS2 To
		criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setAs2From("as2From");
		criteriaDVO.setAs2To("as2To");
		resultList = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals(10, resultList.size());
		for(MessageDVO dvo: resultList){
			Assert.assertEquals("as2From", dvo.getAs2From());
			Assert.assertEquals("as2To", dvo.getAs2To());
		}
		
		// Test Message AS2 From  and AS2 To
		criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setMessageId("20090910-120000-23457@127.0.0.1");
		resultList = dao.findMessagesByHistory(criteriaDVO, 100, 0);
		Assert.assertEquals(1, resultList.size());
		for(MessageDVO dvo: resultList){
			Assert.assertEquals("20090910-120000-23457@127.0.0.1", dvo.getMessageId());
			Assert.assertEquals(MessageDVO.MSGBOX_IN, dvo.getMessageBox());
			Assert.assertEquals(MessageDVO.STATUS_PROCESSED, dvo.getStatus());
		}
		
		//Test OFFSET and LIMIT Value
		criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setMessageBox(MessageDVO.MSGBOX_IN);		
		resultList = dao.findMessagesByHistory(criteriaDVO, 100, 2);
		Assert.assertEquals(3, resultList.size());
		
		//Test OFFSET and LIMIT Value
		criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setMessageBox(MessageDVO.MSGBOX_IN);		
		resultList = dao.findMessagesByHistory(criteriaDVO, 3, 3);
		Assert.assertEquals(2, resultList.size());
		
		criteriaDVO = (MessageDVO) dao.createDVO();
		criteriaDVO.setAs2From("");
		criteriaDVO.setAs2To("   ");
		resultList = dao.findMessagesByHistory(criteriaDVO, 3, 3);
		Assert.assertTrue(resultList.isEmpty());
	}
	
}
