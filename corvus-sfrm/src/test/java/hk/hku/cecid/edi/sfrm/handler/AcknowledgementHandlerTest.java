package hk.hku.cecid.edi.sfrm.handler;

import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDVO;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.test.PluginTest;
import hk.hku.cecid.piazza.commons.test.utils.ResSetter;
import hk.hku.cecid.edi.sfrm.handler.AcknowledgementHandler;
import java.io.FileReader;
import java.io.File;

public class AcknowledgementHandlerTest extends PluginTest<SFRMProcessor> {
	
	private SFRMProcessor sfrmProcessor;
	private AcknowledgementHandler ackHandler;
	private String ackFilename = "ack.xml";
	private ResSetter resSetter;
	
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
		ackHandler = sfrmProcessor.getAcknowledgementHandler();
		resSetter = new ResSetter(this.getClass());
	}
	
	@Override
	public void postSetUp() throws Exception{
		resSetter.restore();
	}
	
	@Override
	public void postTearDown() throws Exception{
		resSetter.clean();
	}
	
	/**
	 * Test for dealing with the SD response from receiver
	 * @throws Exception
	 */
	public void testUpdateMessageSD() throws Exception{
		File ackFile = new File(FIXTURE_LOADER.getResource(getName()).getFile(),ackFilename);
		String ackContent = IOHandler.readString(new FileReader(ackFile));
		
		ackHandler.processAcknowledgementResponse(ackContent);
		SFRMMessageDAO dao = (SFRMMessageDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
		SFRMMessageDVO dvo = (SFRMMessageDVO) dao.createDVO();
		
		dvo.setMessageId("testUpdateMessageSD");
		dvo.setMessageBox(SFRMConstant.MSGBOX_OUT);
		
		dao.retrieve(dvo);
		
		assertEquals("Message status should changed to " + SFRMConstant.MSGS_SUSPENDED, SFRMConstant.MSGS_SUSPENDED, dvo.getStatus());
	}
	
	/**
	 * Test for dealing with the PS response from receiver
	 * @throws Exception
	 */
	public void testUpdateMessagePS() throws Exception{
		File ackFile = new File(FIXTURE_LOADER.getResource(getName()).getFile(),ackFilename);
		String ackContent = IOHandler.readString(new FileReader(ackFile));
		
		ackHandler.processAcknowledgementResponse(ackContent);
		SFRMMessageDAO dao = (SFRMMessageDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
		SFRMMessageDVO dvo = (SFRMMessageDVO) dao.createDVO();
		
		dvo.setMessageId("testUpdateMessagePS");
		dvo.setMessageBox(SFRMConstant.MSGBOX_OUT);
		
		dao.retrieve(dvo);
		
		assertEquals("Message status should changed to " + SFRMConstant.MSGS_PROCESSED, SFRMConstant.MSGS_PROCESSED, dvo.getStatus());
		assertNotNull("Message completed timestamp should be set", dvo.getCompletedTimestamp());
	}
	
	/**
	 * Test for dealing with the DF response from receiver
	 * @throws Exception
	 */
	public void testUpdateMessageDF() throws Exception{
		File ackFile = new File(FIXTURE_LOADER.getResource(getName()).getFile(),ackFilename);
		String ackContent = IOHandler.readString(new FileReader(ackFile));
		
		ackHandler.processAcknowledgementResponse(ackContent);
		SFRMMessageDAO dao = (SFRMMessageDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
		SFRMMessageDVO dvo = (SFRMMessageDVO) dao.createDVO();
		
		dvo.setMessageId("testUpdateMessageDF");
		dvo.setMessageBox(SFRMConstant.MSGBOX_OUT);
		
		dao.retrieve(dvo);
		
		assertEquals("Message status should changed to " + SFRMConstant.MSGS_DELIVERY_FAILURE, SFRMConstant.MSGS_DELIVERY_FAILURE, dvo.getStatus());
	}
	/**
	 * Test if the segment was updated corrspending to the message segment acknowledgement response
	 * @throws Exception
	 */
	public void testUpdateMessageSegmentForPRMessage() throws Exception{
		File ackFile = new File(FIXTURE_LOADER.getResource(getName()).getFile(),ackFilename);
		String ackContent = IOHandler.readString(new FileReader(ackFile));
		ackHandler.processAcknowledgementResponse(ackContent);
		
		SFRMMessageDAO mDAO = (SFRMMessageDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
		SFRMMessageDVO mDVO = (SFRMMessageDVO) mDAO.createDVO();
		
		mDVO.setMessageId("testUpdateMessageDF");
		mDVO.setMessageBox(SFRMConstant.MSGBOX_OUT);
		
		mDAO.retrieve(mDVO);
		//Assert for the message status should not be changed
		assertEquals("Message status should not be changed", SFRMConstant.MSGS_PROCESSING, mDVO.getStatus());
		
		SFRMMessageSegmentDAO msDAO = (SFRMMessageSegmentDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageSegmentDAO.class);
		SFRMMessageSegmentDVO msDVO = (SFRMMessageSegmentDVO) msDAO.createDVO();
		
		msDVO.setMessageId("testUpdateMessageSegmentForPRMessage");
		msDVO.setMessageBox(SFRMConstant.MSGBOX_OUT);
		msDVO.setSegmentType(SFRMConstant.MSGT_PAYLOAD);
		msDVO.setSegmentNo(1);
		
		//segment processed case 
		msDAO.retrieve(msDVO);
		assertEquals("segment 1 status should changed to " + SFRMConstant.MSGS_PROCESSED, SFRMConstant.MSGS_PROCESSED, msDVO.getStatus());
		assertNotNull("segment 1 completed timestamp should be set", msDVO.getCompletedTimestamp());
		
		//segment recovery case
		msDVO.setSegmentNo(2);
		msDAO.retrieve(msDVO);
		assertEquals("segment 2 status should changed to " + SFRMConstant.MSGS_PENDING, SFRMConstant.MSGS_PENDING, msDVO.getStatus());
		assertNull("segment 2 completed timestamp should not be set", msDVO.getCompletedTimestamp());
		
		//case for segment status not changed
		msDVO.setSegmentNo(3);
		msDAO.retrieve(msDVO);
		assertEquals("segment 3 status should not be changed and remain in " + SFRMConstant.MSGS_DELIVERED, SFRMConstant.MSGS_DELIVERED, msDVO.getStatus());
		assertNull("segment 3 completed timestamp should not be set", msDVO.getCompletedTimestamp());
	}
	
	/**
	 * Test if the message status will change to pre-processed, accordingly to the acknowledgement response
	 * @throws Exception
	 */
	public void testMarkMessagePreCompleted() throws Exception{
		File ackFile = new File(FIXTURE_LOADER.getResource(getName()).getFile(),ackFilename);
		String ackContent = IOHandler.readString(new FileReader(ackFile));
		ackHandler.processAcknowledgementResponse(ackContent);
		
		SFRMMessageDAO mDAO = (SFRMMessageDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
		SFRMMessageDVO mDVO = (SFRMMessageDVO) mDAO.createDVO();
		mDVO.setMessageId("testMarkMessagePreCompleted");
		mDVO.setMessageBox(SFRMConstant.MSGBOX_OUT);
		
		mDAO.retrieve(mDVO);
		assertEquals("Message status should marked as " + SFRMConstant.MSGS_PRE_PROCESSED, SFRMConstant.MSGS_PRE_PROCESSED, mDVO.getStatus());
		assertNull("Message completed timestamp should not be set", mDVO.getCompletedTimestamp());
		
		SFRMMessageSegmentDAO msDAO = (SFRMMessageSegmentDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageSegmentDAO.class);
		SFRMMessageSegmentDVO msDVO = (SFRMMessageSegmentDVO) msDAO.createDVO();
		
		msDVO.setMessageId("testMarkMessagePreCompleted");
		msDVO.setMessageBox(SFRMConstant.MSGBOX_OUT);
		msDVO.setSegmentType(SFRMConstant.MSGT_PAYLOAD);
		msDVO.setSegmentNo(3);
		
		//segment processed case 
		msDAO.retrieve(msDVO);
		assertEquals("segment 3 status should changed to " + SFRMConstant.MSGS_PROCESSED, SFRMConstant.MSGS_PROCESSED, msDVO.getStatus());
		assertNotNull("segment 3 completed timestamp should be set", msDVO.getCompletedTimestamp());
	}

}
