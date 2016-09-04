/**
 * 
 */
package hk.hku.cecid.edi.sfrm.dao.ds;

import hk.hku.cecid.piazza.commons.test.DAOTest;
import hk.hku.cecid.edi.sfrm.dao.ds.SFRMMessageSegmentDSDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDVO;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
/**
 * @author Patrick Yip
 *
 */
public class SFRMMessageSegmentDSDAOTest extends DAOTest<SFRMMessageSegmentDSDAO> {
	
	@Override
	public String getTableName(){
		return "segment";
	}
	
	@Test
	public void testFindSegmentByMessageIdAndBoxAndTypeAndNos() throws Exception{
		SFRMMessageSegmentDAO dao = this.getTestingTarget();
		List segNums = new ArrayList();
		segNums.add(new Integer(1));
		segNums.add(new Integer(3));
		
		List results = dao.findSegmentByMessageIdAndBoxAndTypeAndNos("a", SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGT_PAYLOAD, segNums);
		
		Assert.assertEquals("Number of segment found should be 2", 2, results.size());
		SFRMMessageSegmentDVO dvo = (SFRMMessageSegmentDVO) results.get(0);
		Assert.assertEquals("Message segment with num 1 not found", 1, dvo.getSegmentNo());
		Assert.assertEquals("Message segment 1 should have status DL", "DL", dvo.getStatus());
		Assert.assertEquals("Message box should be OUTBOX", SFRMConstant.MSGBOX_OUT, dvo.getMessageBox());
		Assert.assertEquals("Message Id should be a", "a", dvo.getMessageId());
		
		dvo = (SFRMMessageSegmentDVO) results.get(1);
		Assert.assertEquals("Message segment with num 3 not found", 3, dvo.getSegmentNo());
		Assert.assertEquals("Message segment 3 should have status DF", "DF", dvo.getStatus());
		Assert.assertEquals("Message box should be OUTBOX", SFRMConstant.MSGBOX_OUT, dvo.getMessageBox());
		Assert.assertEquals("Message Id should be a", "a", dvo.getMessageId());
	}
	
	@Test
	public void testUpdateBatchSegmentsStatus() throws Exception{
		SFRMMessageSegmentDAO dao = this.getTestingTarget();
		List segNums = new ArrayList();
		segNums.add(new Integer(1));
		segNums.add(new Integer(2));
		segNums.add(new Integer(3));
		
		int updated = dao.updateBatchSegmentsRecoveryStatus(SFRMConstant.MSGS_PROCESSED, "c", SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGT_PAYLOAD, segNums);
		
		Assert.assertEquals("Number of updated segment should be 3", 3, updated);
		
		//Assert the segment status
		SFRMMessageSegmentDVO msDVO = (SFRMMessageSegmentDVO) dao.createDVO();
		msDVO.setMessageId("c");
		msDVO.setMessageBox(SFRMConstant.MSGBOX_OUT);
		msDVO.setSegmentType(SFRMConstant.MSGT_PAYLOAD);
		msDVO.setSegmentNo(1);
		
		//These 3 segment should be updated
		dao.retrieve(msDVO);
		Assert.assertEquals("seg 1 should updated to " + SFRMConstant.MSGS_PROCESSED, SFRMConstant.MSGS_PROCESSED, msDVO.getStatus());
		
		msDVO.setSegmentNo(2);
		
		dao.retrieve(msDVO);
		Assert.assertEquals("seg 2 should updated to " + SFRMConstant.MSGS_PROCESSED, SFRMConstant.MSGS_PROCESSED, msDVO.getStatus());
		
		msDVO.setSegmentNo(3);
		
		dao.retrieve(msDVO);
		Assert.assertEquals("seg 3 should updated to " + SFRMConstant.MSGS_PROCESSED, SFRMConstant.MSGS_PROCESSED, msDVO.getStatus());
		
		//This segment should not be updated
		msDVO.setSegmentNo(4);
		
		dao.retrieve(msDVO);
		Assert.assertEquals("seg 4 should remain in " + SFRMConstant.MSGS_DELIVERED, SFRMConstant.MSGS_DELIVERED, msDVO.getStatus());

	}
	
	@Test
	public void testFindNumOfSegmentByMessageIdAndBoxAndTypeAndStatues() throws Exception{
		SFRMMessageSegmentDAO dao = this.getTestingTarget();
		List statues = new ArrayList();
		statues.add("PS");
		statues.add("DL");
		
		Calendar cal = Calendar.getInstance();
		cal.set(2008, 9-1, 19);
		
		long numBytes = dao.findNumOfBytesSentByMessageIdAndBoxAndTypeAndStatues("testFindNumOfSegmentByMessageIdAndBoxAndTypeAndStatues", SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGT_PAYLOAD, cal.getTimeInMillis(), statues);
		Assert.assertEquals("Number of segment which is in PS and DL status should be 100", 100, numBytes);
	}
	
	
}
