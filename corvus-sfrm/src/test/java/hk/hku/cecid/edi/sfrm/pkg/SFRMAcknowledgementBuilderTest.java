/**
 * 
 */
package hk.hku.cecid.edi.sfrm.pkg;

import junit.framework.TestCase;

/**
 * @author Patrick Yip
 *
 */
public class SFRMAcknowledgementBuilderTest extends TestCase {
	protected void setUp() throws Exception{
		System.out.println("----------- Starting SFRMAcknowledgementBuilder2Test -----------");
	}
	
	protected void tearDown() throws Exception{
		System.out.println("----------- Shutdown SFRMAcknowledgementBuilder2Test -----------");
	}
	
	public void testSimpleCreate(){
		SFRMAcknowledgementBuilder builder = new SFRMAcknowledgementBuilder();
		builder.setMessage("abc", "PS");
		System.out.println(SFRMAcknowledgementBuilder.MESSAGE_XPATH);
		System.out.println(SFRMAcknowledgementBuilder.MESSAGE_SEGMENT_XPATH_PARAM);
		builder.setSegment("abc", 1, "PS");
		builder.setSegment("abc", 2, "PK");
		
		builder.setMessage("bbc@localhost", "PR");
		builder.setSegment("bbc@localhost", 1, "PK");
		System.out.println(builder.toString());
	}
}
