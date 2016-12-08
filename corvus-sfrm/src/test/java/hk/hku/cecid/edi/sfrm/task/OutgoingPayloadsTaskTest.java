package hk.hku.cecid.edi.sfrm.task;

import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDAO;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;
import hk.hku.cecid.edi.sfrm.com.PackagedPayloadsRepository;
import hk.hku.cecid.edi.sfrm.com.PackagedPayloads;

import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDVO;
import hk.hku.cecid.edi.sfrm.task.OutgoingPayloadsTask;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;

import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDAO;

import hk.hku.cecid.piazza.commons.net.ConnectionException;
import hk.hku.cecid.piazza.commons.test.PluginTest;
import hk.hku.cecid.piazza.commons.test.utils.ResSetter;
import hk.hku.cecid.piazza.commons.test.utils.SimpleHttpMonitor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

public class OutgoingPayloadsTaskTest extends PluginTest<SFRMProcessor> {
	private SFRMProcessor sfrmProcessor;
	
	private ResSetter resSetter;
	
	private int mockPort = 9000;
	
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
	
	public void postSetUp() throws Exception{
		resSetter = new ResSetter(this.getClass());
		resSetter.restore();
	}
	
	public void postTearDown() throws Exception{
		resSetter.clean();
	}
	
	/**
	 * Test if handshaking is success
	 * @throws Exception
	 */
	public void testSendMeta() throws Exception{
		SimpleHttpMonitor mock = new SimpleHttpMonitor(mockPort);
		mock.start();
		
		try {			
			//Getting the payload obejct
			PackagedPayloadsRepository repo = sfrmProcessor.getOutgoingRepository();
			Iterator pIter = repo.getReadyPayloads().iterator();
			
			//There should have only 1 paylaod ready to send
			PackagedPayloads pp = null;
			while(pIter.hasNext()){
				pp = (PackagedPayloads) pIter.next();
				//To make sure the payload to test is only once with message id "testSendMeta"
				if(pp.getRefMessageId().equals("testSendMeta")){
					break;
				}
			}
			
			SFRMPartnershipDAO pDAO = (SFRMPartnershipDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMPartnershipDAO.class);
			SFRMPartnershipDVO pDVO = (SFRMPartnershipDVO) pDAO.createDVO();
			pDVO.setPartnershipId(pp.getPartnershipId());
			pDAO.retrieve(pDVO);
			
			pp.setToProcessing();
			OutgoingPayloadsTask task = new OutgoingPayloadsTask(pp, pDVO, SFRMConstant.MSGS_PACKAGED);

			Thread.sleep(1000);
			
			task.execute();
			
			Thread.sleep(1000);
			
			//Assert whether the new message was inserted to db
			SFRMMessageDAO mDAO = (SFRMMessageDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
			SFRMMessageDVO mDVO = (SFRMMessageDVO) mDAO.createDVO();
			
			mDVO.setMessageId("testSendMeta");
			mDVO.setMessageBox(SFRMConstant.MSGBOX_OUT);
			
			assertTrue("Message should be created with ID testSendMeta", mDAO.retrieve(mDVO));
			assertEquals("Message status should be PR", SFRMConstant.MSGS_PROCESSING, mDVO.getStatus());
			
			//Assert whether the message segment was created
			SFRMMessageSegmentDAO msDAO = (SFRMMessageSegmentDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageSegmentDAO.class);
			List segs = msDAO.findSegmentsByMessageIdAndBoxAndTypeAndStatus("testSendMeta", SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGT_PAYLOAD, SFRMConstant.MSGS_PENDING);
			
			assertEquals("Number of segment should be 1", 1, segs.size());
		} finally {
			mock.stop();
		}
	}
	
	/**
	 * Test with handshaking, that the mock server responsed with 400 bad request
	 * @throws Exception
	 */
	public void testSendMetaFailed() throws Exception{
		SimpleHttpMonitor mock = new SimpleHttpMonitor(mockPort){
			protected void onResponse(final OutputStream out) throws IOException{
				out.write(("HTTP/1.1 400 Bad Request" + CRLF).getBytes());
				out.write(("Server: SFRM Mock Server" + CRLF).getBytes());
				out.write(("Content-Length: 0" + CRLF).getBytes());
				out.write(("Content-Type: text/plain" + CRLF + CRLF).getBytes());
			}
		};
		
		mock.start();
		
		try {		
			//Getting the payload obejct
			PackagedPayloadsRepository repo = sfrmProcessor.getOutgoingRepository();
			Iterator pIter = repo.getReadyPayloads().iterator();
			
			//There should have only 1 paylaod ready to send
			PackagedPayloads pp = null;
			while(pIter.hasNext()){
				pp = (PackagedPayloads) pIter.next();
				//To make sure the payload to test is only once with message id "testSendMeta"
				if(pp.getRefMessageId().equals("testSendMeta")){
					break;
				}
			}
			
			SFRMPartnershipDAO pDAO = (SFRMPartnershipDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMPartnershipDAO.class);
			SFRMPartnershipDVO pDVO = (SFRMPartnershipDVO) pDAO.createDVO();
			pDVO.setPartnershipId(pp.getPartnershipId());
			pDAO.retrieve(pDVO);
			
			pp.setToProcessing();
			OutgoingPayloadsTask task = new OutgoingPayloadsTask(pp, pDVO, SFRMConstant.MSGS_PACKAGED);
			
			boolean thrown = false;
			
			try{
				task.execute();
			}catch(ConnectionException conne){
				thrown = true;
			}
			
			assertTrue("Connection Exception should thrown because of bad response", thrown);
			
			Thread.sleep(1000);
			
			//Assert whether the new message was inserted to db
			SFRMMessageDAO mDAO = (SFRMMessageDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
			SFRMMessageDVO mDVO = (SFRMMessageDVO) mDAO.createDVO();
			
			mDVO.setMessageId("testSendMeta");
			mDVO.setMessageBox(SFRMConstant.MSGBOX_OUT);
			
			assertTrue("Message should be created with ID testSendMeta", mDAO.retrieve(mDVO));
			assertEquals("Message status should be PE", SFRMConstant.MSGS_PROCESSING_ERROR, mDVO.getStatus());
			
			//Assert whether the message segment was created
			SFRMMessageSegmentDAO msDAO = (SFRMMessageSegmentDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageSegmentDAO.class);
			List segs = msDAO.findSegmentsByMessageIdAndBoxAndTypeAndStatus("testSendMeta", SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGT_PAYLOAD, SFRMConstant.MSGS_PENDING);
			
			assertEquals("Number of segment should be 0", 0, segs.size());
		} finally {		
			mock.stop();
		}
	}
	
	/**
	 * Test if the mock server was not started up, not connection establish
	 * @throws Exception
	 */
	public void testSendMetaFailedNotConnection() throws Exception{
		//Getting the payload obejct
		PackagedPayloadsRepository repo = sfrmProcessor.getOutgoingRepository();
		Iterator pIter = repo.getReadyPayloads().iterator();
		
		//There should have only 1 paylaod ready to send
		PackagedPayloads pp = null;
		while(pIter.hasNext()){
			pp = (PackagedPayloads) pIter.next();
			//To make sure the payload to test is only once with message id "testSendMeta"
			if(pp.getRefMessageId().equals("testSendMeta")){
				break;
			}
		}
		
		SFRMPartnershipDAO pDAO = (SFRMPartnershipDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMPartnershipDAO.class);
		SFRMPartnershipDVO pDVO = (SFRMPartnershipDVO) pDAO.createDVO();
		pDVO.setPartnershipId(pp.getPartnershipId());
		pDAO.retrieve(pDVO);
		
		pp.setToProcessing();
		OutgoingPayloadsTask task = new OutgoingPayloadsTask(pp, pDVO, SFRMConstant.MSGS_PACKAGED);
		
		boolean thrown = false;
		
		try{
			task.execute();
		}catch(ConnectionException conne){
			thrown = true;
		}
		
		assertTrue("Connection Exception should thrown because of bad response", thrown);
		
		Thread.sleep(1000);
		
		//Assert whether the new message was inserted to db
		SFRMMessageDAO mDAO = (SFRMMessageDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageDAO.class);
		SFRMMessageDVO mDVO = (SFRMMessageDVO) mDAO.createDVO();
		
		mDVO.setMessageId("testSendMeta");
		mDVO.setMessageBox(SFRMConstant.MSGBOX_OUT);
		
		assertTrue("Message should be created with ID testSendMeta", mDAO.retrieve(mDVO));
		assertEquals("Message status should be PE", SFRMConstant.MSGS_PROCESSING_ERROR, mDVO.getStatus());
		
		//Assert whether the message segment was created
		SFRMMessageSegmentDAO msDAO = (SFRMMessageSegmentDAO) sfrmProcessor.getDAOFactory().createDAO(SFRMMessageSegmentDAO.class);
		List segs = msDAO.findSegmentsByMessageIdAndBoxAndTypeAndStatus("testSendMeta", SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGT_PAYLOAD, SFRMConstant.MSGS_PENDING);
		
		assertEquals("Number of segment should be 0", 0, segs.size());
		
	}
	
}
