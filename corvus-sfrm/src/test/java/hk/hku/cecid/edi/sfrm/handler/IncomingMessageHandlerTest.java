package hk.hku.cecid.edi.sfrm.handler;

import hk.hku.cecid.piazza.commons.test.PluginTest;
import hk.hku.cecid.piazza.commons.test.utils.ResSetter;
import hk.hku.cecid.edi.sfrm.handler.IncomingMessageHandler;

import hk.hku.cecid.edi.sfrm.pkg.SFRMAcknowledgementParser;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDVO;
import hk.hku.cecid.piazza.commons.io.IOHandler;

import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import java.net.MalformedURLException;

/**
 * @author Patrick Yip
 *
 */
public class IncomingMessageHandlerTest extends PluginTest<SFRMProcessor> {
	
	private IncomingMessageHandler inHandler;
	private ResSetter resSetter;
	private SFRMProcessor sfrmProcessor;
		
	public void init(){
		sfrmProcessor = (SFRMProcessor) processor;
		isLoadDB = true;
		inHandler = sfrmProcessor.getIncomingMessageHandler();
		resSetter = new ResSetter(this.getClass());
	}
	
	public void postSetUp() throws Exception{
		resSetter.restore();
	}
	
	public void postTearDown() throws Exception{
		resSetter.clean();
	}
	
	public String getDBName(){
		return "dao";
	}
	
	public DataSourceDAO getDSDAO() throws Exception{
		return (DataSourceDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
	}
	
	@Test
	public void testMetaReceived() throws Exception{
		SFRMMessage message = new SFRMMessage();
		message.setSegmentType(SFRMConstant.MSGT_META);
		message.setPartnershipId("loopback");
		message.setMessageID("testMetaReceivd");
		
		message.setFilename("filename.tar");
		message.setTotalSize(1024);
		message.setSegmentNo(0);
		
		inHandler.processIncomingMessage(message, null);
		
		//Assert whether the new message was inserted
		SFRMMessageDAO dao = (SFRMMessageDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
		SFRMMessageDVO dvo = (SFRMMessageDVO) dao.createDVO();
		dvo.setMessageId("testMetaReceivd");
		dvo.setMessageBox(SFRMConstant.MSGBOX_IN);
		assertTrue("Message testMetaReceivd was not inserted", dao.retrieve(dvo));
		assertEquals("Filename should be: filename.tar", "filename.tar", dvo.getFilename());
		assertEquals("Partnership ID should be loopback", "loopback", dvo.getPartnershipId());
		assertEquals("Payload size should be 1024", 1024, dvo.getTotalSize());
		
		// It is not guaranteed the processing time
//		assertEquals("Message status should be HS", "HS", dvo.getStatus());
		//Sleep for let SFRM to created dummy payload
		Thread.sleep(1000);
		
		//After the payload was created
		dvo = (SFRMMessageDVO) dao.createDVO();
		dvo.setMessageId("testMetaReceivd");
		dvo.setMessageBox(SFRMConstant.MSGBOX_IN);
		dao.retrieve(dvo);
		//Look if the message was changed to PR after the payload was created
		assertEquals("Message status should be PR", "PR", dvo.getStatus());
		
		//Assert that whether the dummy payload was created in the file system
		File inRepo = sfrmProcessor.getIncomingRepository().getRepository();
		File payload = new File(inRepo, "~loopback$testMetaReceivd.sfrm");
		System.out.println(payload.getCanonicalPath());
		assertTrue("Dummy payload wasn't created", payload.exists());
	}
	
	public void testMetaReceivedPartnershipNonExist() throws Exception{
		SFRMMessage message = new SFRMMessage();
		message.setSegmentType(SFRMConstant.MSGT_META);
		message.setPartnershipId("non_exist_partnership");
		message.setMessageID("testMetaReceivedPartnershipNonExist");
		
		message.setFilename("filename.tar");
		message.setTotalSize(1024);
		message.setSegmentNo(0);
		boolean thrown = false;
		try{
			inHandler.processIncomingMessage(message, null);
		}catch(MalformedURLException e){
			thrown = true;
		}
		
		assertTrue("Exception should thrown because of the partnership ID not found in the receiver side", thrown);
		
	}
		
	@Test
	public void testProcessPRAck() throws Exception{
		File requestFile = new File(new File(FIXTURE_LOADER.getResource(getName()).getFile()), "ack_request.xml");
		
		String requestContent = IOHandler.readString(new InputStreamReader(new FileInputStream(requestFile)));
		IncomingMessageHandler handler = sfrmProcessor.getIncomingMessageHandler();
		
		SFRMAcknowledgementParser requestParser = new SFRMAcknowledgementParser(requestContent);
		SFRMMessageDVO mDVO = SFRMProcessor.getInstance().getMessageHandler().retrieveMessage("testProcessPRAck", SFRMConstant.MSGBOX_IN);
		
		String responseContent = handler.processPRAck(mDVO, requestParser);
		SFRMAcknowledgementParser responseParser = new SFRMAcknowledgementParser(responseContent);
		
		SFRMMessageSegmentDAO dao = (SFRMMessageSegmentDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageSegmentDAO.class);
		
		//Assert whether the DF segment as deleted
		SFRMMessageSegmentDVO dvo = (SFRMMessageSegmentDVO) dao.createDVO();
		
		dvo.setMessageId("testProcessPRAck");
		dvo.setMessageBox(SFRMConstant.MSGBOX_IN);
		dvo.setSegmentType(SFRMConstant.MSGT_PAYLOAD);
		dvo.setSegmentNo(2);
		
		assertFalse("Message segment 2 should be deleted", dao.retrieve(dvo));
		//Assert the response content
		assertEquals("Status for segment 2 should be " + SFRMConstant.MSGS_DELIVERY_FAILURE, SFRMConstant.MSGS_DELIVERY_FAILURE, responseParser.getMessageSegmentStatus("testProcessPRAck", 2));
		
		
		//Assert if the DL segment was updated to PS
		dvo.setSegmentNo(1);
		assertTrue("Message segment 1 should still exist", dao.retrieve(dvo));
		
		assertEquals("Status of Message segment 1 should be updated to " + SFRMConstant.MSGS_PROCESSED, SFRMConstant.MSGS_PROCESSED, dvo.getStatus());
		assertEquals("Status for segment 1 should be " + SFRMConstant.MSGS_PROCESSED, SFRMConstant.MSGS_PROCESSED, responseParser.getMessageSegmentStatus("testProcessPRAck", 1));
		
		//Assert if the PR segment was not changed status
		dvo.setSegmentNo(3);
		dao.retrieve(dvo);
		assertTrue("Message segment 3 should still exist", dao.retrieve(dvo));
		assertEquals("Status of Message segment 3 should be updated to " + SFRMConstant.MSGS_PROCESSING, SFRMConstant.MSGS_PROCESSING, dvo.getStatus());
		assertEquals("Status for segment 3 should be " + SFRMConstant.MSGS_PROCESSING, SFRMConstant.MSGS_PROCESSING, responseParser.getMessageSegmentStatus("testProcessPRAck", 3));
	}
	
	@Test
	public void testProcessPDFAck() throws Exception{		
		IncomingMessageHandler handler = sfrmProcessor.getIncomingMessageHandler();
		SFRMMessageDVO mDVO = SFRMProcessor.getInstance().getMessageHandler().retrieveMessage("testProcessPDFAck", SFRMConstant.MSGBOX_IN);
		String responseContent = handler.processPDFAck(mDVO);
		SFRMAcknowledgementParser responseParser = new SFRMAcknowledgementParser(responseContent);
		SFRMMessageDAO dao = (SFRMMessageDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
		SFRMMessageDVO dvo = (SFRMMessageDVO) dao.createDVO();
		dvo.setMessageId(getName());
		dvo.setMessageBox(SFRMConstant.MSGBOX_IN);
		//Assert if the message still exist
		assertTrue("Message ID 'testProcessPDFAck' should not be deleted", dao.retrieve(dvo));
		assertEquals("Message status should changed to DF", SFRMConstant.MSGS_DELIVERY_FAILURE, dvo.getStatus());
		assertEquals("Message status in response ack should be DF", SFRMConstant.MSGS_DELIVERY_FAILURE, responseParser.getMessageStatus(getName()));
	}
	
	@Test
	public void testProcessPPSAck() throws Exception{
		IncomingMessageHandler handler = sfrmProcessor.getIncomingMessageHandler();
		SFRMMessageDVO mDVO = SFRMProcessor.getInstance().getMessageHandler().retrieveMessage("testProcessPPSAck", SFRMConstant.MSGBOX_IN);
		String responseContent = handler.processPPSAck(mDVO);
		SFRMAcknowledgementParser responseParser = new SFRMAcknowledgementParser(responseContent);
		SFRMMessageDAO dao = (SFRMMessageDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
		SFRMMessageDVO dvo = (SFRMMessageDVO) dao.createDVO();
		dvo.setMessageId(getName());
		dvo.setMessageBox(SFRMConstant.MSGBOX_IN);
		//Assert if the message still exist
		assertTrue("Message ID 'testProcessPPSAck' should not be deleted", dao.retrieve(dvo));
		assertEquals("Message status should changed to PS", SFRMConstant.MSGS_PROCESSED, dvo.getStatus());
		assertNotNull("Message Completed timestamp should not be null", dvo.getCompletedTimestamp());
		assertEquals("Message status in response ack should be PS", SFRMConstant.MSGS_PROCESSED, responseParser.getMessageStatus(getName()));
		
	}
	
	@Test
	public void testProcessPSDAck() throws Exception{
		IncomingMessageHandler handler = sfrmProcessor.getIncomingMessageHandler();
		SFRMMessageDVO mDVO = SFRMProcessor.getInstance().getMessageHandler().retrieveMessage("testProcessPSDAck", SFRMConstant.MSGBOX_IN);
		String responseContent = handler.processPSDAck(mDVO);
		SFRMAcknowledgementParser responseParser = new SFRMAcknowledgementParser(responseContent);
		SFRMMessageDAO dao = (SFRMMessageDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
		SFRMMessageDVO dvo = (SFRMMessageDVO) dao.createDVO();
		dvo.setMessageId(getName());
		dvo.setMessageBox(SFRMConstant.MSGBOX_IN);
		//Assert if the message still exist
		assertTrue("Message ID 'testProcessPSDAck' should not be deleted", dao.retrieve(dvo));
		assertEquals("Message status should changed to SD", SFRMConstant.MSGS_SUSPENDED, dvo.getStatus());
		assertEquals("Message status in response ack should be SD", SFRMConstant.MSGS_SUSPENDED, responseParser.getMessageStatus(getName()));
	}
	
	@Test
	public void testProcessPRSAck() throws Exception{
		IncomingMessageHandler handler = sfrmProcessor.getIncomingMessageHandler();
		SFRMMessageDVO mDVO = SFRMProcessor.getInstance().getMessageHandler().retrieveMessage("testProcessPRSAck", SFRMConstant.MSGBOX_IN);
		String responseContent = handler.processPRSAck(mDVO);
		SFRMAcknowledgementParser responseParser = new SFRMAcknowledgementParser(responseContent);
		SFRMMessageDAO dao = (SFRMMessageDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
		SFRMMessageDVO dvo = (SFRMMessageDVO) dao.createDVO();
		dvo.setMessageId(getName());
		dvo.setMessageBox(SFRMConstant.MSGBOX_IN);
		//Assert if the message still exist
		assertTrue("Message ID 'testProcessPRSAck' should not be deleted", dao.retrieve(dvo));
		assertEquals("Message status should changed to PR", SFRMConstant.MSGS_PROCESSING, dvo.getStatus());
		assertEquals("Message status in response ack should be PR", SFRMConstant.MSGS_PROCESSING, responseParser.getMessageStatus(getName()));
	}
	
}
