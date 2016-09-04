package hk.hku.cecid.edi.sfrm.pkg;

import junit.framework.TestCase;
import hk.hku.cecid.edi.sfrm.pkg.SFRMAcknowledgementParser;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.test.utils.FixtureStore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.util.List;

import org.junit.Assert;

public class SFRMAcknowledgementParserTest extends TestCase {
	
	private ClassLoader FIXTURE_LOADER = FixtureStore.createFixtureLoader(false, this.getClass());
	private String xmlContent;
	
	public void setUp() throws Exception{
		System.out.println("------------------- Start up SFRMAcknowledgementParserTest -------------------");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(FIXTURE_LOADER.getResource("base.xml").getFile())))); 
		xmlContent = IOHandler.readString(reader);
	}
	
	public void tearDown() throws Exception{
		System.out.println("------------------- Shutdown SFRMAcknowledgementParserTest -------------------");
	}
	
	public void testGetNumMessages() throws Exception{
		SFRMAcknowledgementParser parser = new SFRMAcknowledgementParser(xmlContent);
		assertEquals("Number of messages should be 2",2, parser.getNumMessages());
	}
	
	public void testGetNumMessageSegment() throws Exception{
		SFRMAcknowledgementParser parser = new SFRMAcknowledgementParser(xmlContent);
		assertEquals("Number of message segment should be 4", 4, parser.getNumMessageSegment("goodday"));
		assertEquals("Number of message segment should be 3", 3, parser.getNumMessageSegment("hello"));
	}
	
	public void testGetMessageList() throws Exception{
		SFRMAcknowledgementParser parser = new SFRMAcknowledgementParser(xmlContent);
		String expected[] = new String[]{"goodday", "hello"};
		List actual = parser.getMessagesIDs();
		Assert.assertArrayEquals("Message list not match", expected, actual.toArray());
	}
	
	public void testGetMessageSegmentList() throws Exception{
		//Test for message 'goodday'
		SFRMAcknowledgementParser parser = new SFRMAcknowledgementParser(xmlContent);
		Integer expected[] = new Integer[]{8,6,4,1};
		Assert.assertArrayEquals("Message segment list for goodday not match", expected, parser.getMessageSegmentNums("goodday").toArray());
		
		//Test for message 'hello'
		expected = new Integer[]{4,9,2};
		Assert.assertArrayEquals("Message segment list for hello not match", expected, parser.getMessageSegmentNums("hello").toArray());			
	}
	
	public void testMessageStatus() throws Exception{
		SFRMAcknowledgementParser parser = new SFRMAcknowledgementParser(xmlContent);
		assertEquals("Message status for 'goodday' is PR", "PR", parser.getMessageStatus("goodday"));
		assertEquals("Message status for 'hello' is PR", "PPS", parser.getMessageStatus("hello"));
	}
	
	public void testMessageSegmentStatus() throws Exception{
		SFRMAcknowledgementParser parser = new SFRMAcknowledgementParser(xmlContent);
		
		assertEquals("Segment status not match", "DL", parser.getMessageSegmentStatus("goodday", 8));
		assertEquals("Segment status not match", "PR", parser.getMessageSegmentStatus("goodday", 1));
		
		assertEquals("Segment status not match", "PR", parser.getMessageSegmentStatus("hello", 4));
		assertEquals("Segment status not match", "DF", parser.getMessageSegmentStatus("hello", 2));
	}

}
