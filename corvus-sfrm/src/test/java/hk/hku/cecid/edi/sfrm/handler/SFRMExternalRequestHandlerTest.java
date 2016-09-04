package hk.hku.cecid.edi.sfrm.handler;

import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDVO;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.spa.SFRMException;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;
import hk.hku.cecid.piazza.commons.test.PluginTest;

public class SFRMExternalRequestHandlerTest extends PluginTest<SFRMProcessor> {
	
	private SFRMProcessor sfrmProcessor;
	
	@Override
	public String getDBName() {
		// TODO Auto-generated method stub
		return "dao";
	}

	@Override
	public DataSourceDAO getDSDAO() throws Exception {
		// TODO Auto-generated method stub
		return (DataSourceDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		sfrmProcessor = (SFRMProcessor) processor;
		isLoadDB = true;
	}
	
	public void testSuspendMessage() throws Exception{
		SFRMExternalRequestHandler extHandler = sfrmProcessor.getExternalRequestHandler();
		boolean thrown = false;
		try{
			extHandler.suspendMessage(getName());
		}catch(Exception e){
			thrown = true;
		}
			
		assertFalse("There should be no problem during suspending operation", thrown);
		//Check the status of message
		SFRMMessageDAO mDAO = (SFRMMessageDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
		SFRMMessageDVO mDVO = (SFRMMessageDVO) mDAO.createDVO();
		mDVO.setMessageId(getName());
		mDVO.setMessageBox(SFRMConstant.MSGBOX_OUT);
		mDAO.retrieve(mDVO);
		assertEquals("Message status should be " + SFRMConstant.MSGS_PRE_SUSPENDED, SFRMConstant.MSGS_PRE_SUSPENDED, mDVO.getStatus());
		
		//Check the status of the message segment
		SFRMMessageSegmentDAO msDAO = (SFRMMessageSegmentDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageSegmentDAO.class);
		SFRMMessageSegmentDVO msDVO = (SFRMMessageSegmentDVO) msDAO.createDVO();
		msDVO.setMessageId(getName());
		msDVO.setMessageBox(SFRMConstant.MSGBOX_OUT);
		msDVO.setSegmentType(SFRMConstant.MSGT_PAYLOAD);
		msDVO.setSegmentNo(1);
		
		msDAO.retrieve(msDVO);
		assertEquals("Message segment 1 should in status " + SFRMConstant.MSGS_SUSPENDED, SFRMConstant.MSGS_SUSPENDED, msDVO.getStatus());
		
		msDVO.setSegmentNo(2);
		msDAO.retrieve(msDVO);
		assertEquals("Message segment 2 should in status " + SFRMConstant.MSGS_PROCESSED, SFRMConstant.MSGS_PROCESSED, msDVO.getStatus());
		
		msDVO.setSegmentNo(3);
		msDAO.retrieve(msDVO);
		assertEquals("Message segment 3 should in status " + SFRMConstant.MSGS_SUSPENDED, SFRMConstant.MSGS_SUSPENDED, msDVO.getStatus());
	}
	
	//Test if client want to suspend the non-existing message
	public void testSuspendNonExistMessage() throws Exception{
		SFRMExternalRequestHandler extHandler = sfrmProcessor.getExternalRequestHandler();
		boolean thrown = false;
		try{
			extHandler.suspendMessage(getName());
		}catch(DAOException e){
			thrown = true;
		}
		
		assertTrue("DAOException should thrown since the message " + getName() + "didn't exist", thrown);
	}
	
	//Test if client want to suspend the processed message
	public void testSuspendProcessedMessage() throws Exception{
		SFRMExternalRequestHandler extHandler = sfrmProcessor.getExternalRequestHandler();
		boolean thrown = false;
		try{
			extHandler.suspendMessage(getName());
		}catch(SFRMException e){
			thrown = true;
		}
		assertTrue("SFRMExpcetion should thrown since the message " + getName() + "was already processed", thrown);
	}
	
	public void testResumeMessage() throws Exception{
		SFRMExternalRequestHandler extHandler = sfrmProcessor.getExternalRequestHandler();
		boolean thrown = false;
		try{
			extHandler.resumeMessage(getName());
		}catch(Exception e){
			thrown = true;
		}
		
		assertFalse("There should be no problem during resume operation", thrown);
		
		//Check the status of message
		SFRMMessageDAO mDAO = (SFRMMessageDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
		SFRMMessageDVO mDVO = (SFRMMessageDVO) mDAO.createDVO();
		mDVO.setMessageId(getName());
		mDVO.setMessageBox(SFRMConstant.MSGBOX_OUT);
		mDAO.retrieve(mDVO);
		assertEquals("Message status should be " + SFRMConstant.MSGS_PRE_RESUME, SFRMConstant.MSGS_PRE_RESUME, mDVO.getStatus());
		
		//Check the status of the message segment
		SFRMMessageSegmentDAO msDAO = (SFRMMessageSegmentDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageSegmentDAO.class);
		SFRMMessageSegmentDVO msDVO = (SFRMMessageSegmentDVO) msDAO.createDVO();
		msDVO.setMessageId(getName());
		msDVO.setMessageBox(SFRMConstant.MSGBOX_OUT);
		msDVO.setSegmentType(SFRMConstant.MSGT_PAYLOAD);
		msDVO.setSegmentNo(1);
		
		msDAO.retrieve(msDVO);
		assertEquals("Message segment 1 should in status " + SFRMConstant.MSGS_DELIVERED, SFRMConstant.MSGS_DELIVERED, msDVO.getStatus());
		
		msDVO.setSegmentNo(2);
		msDAO.retrieve(msDVO);
		assertEquals("Message segment 2 should in status " + SFRMConstant.MSGS_PENDING, SFRMConstant.MSGS_PENDING, msDVO.getStatus());
		
		msDVO.setSegmentNo(3);
		msDAO.retrieve(msDVO);
		assertEquals("Message segment 3 should in status " + SFRMConstant.MSGS_PENDING, SFRMConstant.MSGS_PENDING, msDVO.getStatus());
	}
	
	/**
	 * Test if client want to resume the non-existing message
	 * @throws Exception
	 */
	public void testResumeNonExistingMessage() throws Exception{
		SFRMExternalRequestHandler extHandler = sfrmProcessor.getExternalRequestHandler();
		boolean thrown = false;
		try{
			extHandler.resumeMessage(getName());
		}catch(DAOException e){
			thrown = true;
		}
		
		assertTrue("DAOException should thrown since the message " + getName() + "didn't exist", thrown);
	}
	
	/**
	 * Test if client want to resume the processed message
	 * @throws Exception
	 */
	public void testResumeProcessedMessage() throws Exception{
		SFRMExternalRequestHandler extHandler = sfrmProcessor.getExternalRequestHandler();
		boolean thrown = false;
		try{
			extHandler.resumeMessage(getName());
		}catch(SFRMException e){
			thrown = true;
		}
		assertTrue("SFRMExpcetion should thrown since the message " + getName() + "was already processed", thrown);
	}
	
//	public void testSuspendSegmentingMessage() throws Exception{
//		SFRMExternalRequestHandler extHandler = sfrmProcessor.getExternalRequestHandler();
//		
//		boolean thrown = false;
//		try{
//			extHandler.suspendMessage(getName());
//		}catch(Exception e){
//			thrown = true;
//			e.printStackTrace();
//		}
//			
//		assertFalse("There should be no problem during suspending operation", thrown);
//		//Check the status of message
//		SFRMMessageDAO mDAO = (SFRMMessageDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
//		SFRMMessageDVO mDVO = (SFRMMessageDVO) mDAO.createDVO();
//		mDVO.setMessageId(getName());
//		mDVO.setMessageBox(SFRMConstant.MSGBOX_OUT);
//		mDAO.retrieve(mDVO);
//		assertEquals("Message status should be " + SFRMConstant.MSGS_PRE_DELIVERY_FAILED, SFRMConstant.MSGS_PRE_DELIVERY_FAILED, mDVO.getStatus());
//		
//		//Check the status of the message segment
//		SFRMMessageSegmentDAO msDAO = (SFRMMessageSegmentDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageSegmentDAO.class);
//		SFRMMessageSegmentDVO msDVO = (SFRMMessageSegmentDVO) msDAO.createDVO();
//		msDVO.setMessageId(getName());
//		msDVO.setMessageBox(SFRMConstant.MSGBOX_OUT);
//		msDVO.setSegmentType(SFRMConstant.MSGT_PAYLOAD);
//		
//		msDVO.setSegmentNo(1);
//		msDAO.retrieve(msDVO);
//		assertEquals("Message segment 1 should in status " + SFRMConstant.MSGS_DELIVERY_FAILURE, SFRMConstant.MSGS_DELIVERY_FAILURE, msDVO.getStatus());
//		
//		msDVO.setSegmentNo(2);
//		msDAO.retrieve(msDVO);
//		assertEquals("Message segment 2 should in status " + SFRMConstant.MSGS_DELIVERY_FAILURE, SFRMConstant.MSGS_DELIVERY_FAILURE, msDVO.getStatus());
//		
//		msDVO.setSegmentNo(3);
//		msDAO.retrieve(msDVO);
//		assertEquals("Message segment 3 should in status " + SFRMConstant.MSGS_DELIVERY_FAILURE, SFRMConstant.MSGS_DELIVERY_FAILURE, msDVO.getStatus());
//		
//	}
}
