package hk.hku.cecid.edi.sfrm.pkg;

import junit.framework.TestCase;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessageClassifier;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;

public class SFRMMessageClassifierTest extends TestCase {
	public void setUp() throws Exception{
		System.out.println("------------------- Start up SFRMMessageClassifierTest -------------------");
	}
	
	public void tearDown() throws Exception{
		System.out.println("------------------- Shutdown SFRMMessageClassifierTest -------------------");
	}
	
	public void testIsAck(){
		SFRMMessage message = new SFRMMessage();
		message.setSegmentType(SFRMConstant.MSGT_ACK_REQUEST);
		SFRMMessageClassifier classifier = new SFRMMessageClassifier(message);
		assertTrue("Message segment type should be ACK", classifier.isAcknowledgementRequest());
	}
	
	public void testIsNotAck(){
		SFRMMessage message = new SFRMMessage();
		message.setSegmentType(SFRMConstant.MSGT_META);
		SFRMMessageClassifier classifier = new SFRMMessageClassifier(message);
		assertFalse("Message segment type should not be ACK", classifier.isAcknowledgementRequest());
	}
}
