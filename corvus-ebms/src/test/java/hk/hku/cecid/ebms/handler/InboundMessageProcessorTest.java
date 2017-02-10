package hk.hku.cecid.ebms.handler;

import hk.hku.cecid.ebms.spa.handler.InboundMessageProcessor;
import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.piazza.commons.test.utils.FixtureStore;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * The <code>InboundMessageProcessorTest</code> is the testcase for <code>InboundMessageProcessor</code>.
 * 
 * @author 	Philip Wong
 * @since	1.0.0
 * @version	1.0.0
 * @see hk.hku.cecid.ebms.handler.InboundMessageProcessor
 */

public class InboundMessageProcessorTest extends TestCase {

	// Fixture loader
	private static ClassLoader 	FIXTURE_LOADER	= FixtureStore.createFixtureLoader(false, InboundMessageProcessorTest.class);
	
	// Invoked for setup.
	@Before
	public void setUp() throws Exception {
		System.out.println();
		System.out.println("---------" + this.getName() + " Start -------");
	}
	
	// Invoked for finalized.
	@After
	public void tearDown() throws Exception {
		System.out.println("---------" + this.getName() + " End   -------");
	}

	@Test
	public void testCheckExpiredMessage() throws Exception {
		Method m = Class.forName("hk.hku.cecid.ebms.spa.handler.InboundMessageProcessor").
		getDeclaredMethod("checkExpiredMessage", new Class[] {EbxmlMessage.class});
		m.setAccessible(true);

		URL msgURL;
		File msgFile;
		EbxmlMessage msg;
		
		// 2008-07-21T15:26:52.075+08:00
		msgURL = FIXTURE_LOADER.getResource("test1.msg");
		msgFile = new File(msgURL.getFile());		
		msg = new EbxmlMessage(msgFile);
		Assert.assertTrue((Boolean)m.invoke(InboundMessageProcessor.getInstance(), new Object[] {msg}));		

		// 2099-07-21T15:26:52.075+08:00
		msgURL = FIXTURE_LOADER.getResource("test2.msg");
		msgFile = new File(msgURL.getFile());		
		msg = new EbxmlMessage(msgFile);
		Assert.assertFalse((Boolean)m.invoke(InboundMessageProcessor.getInstance(), new Object[] {msg}));		
		
		// Tue Jul 10 20:01:14 HKT 2007
		msgURL = FIXTURE_LOADER.getResource("test3.msg");
		msgFile = new File(msgURL.getFile());		
		msg = new EbxmlMessage(msgFile);
		Assert.assertTrue((Boolean)m.invoke(InboundMessageProcessor.getInstance(), new Object[] {msg}));		

		// Sat Jul 10 20:01:14 HKT 2038
		msgURL = FIXTURE_LOADER.getResource("test4.msg");
		msgFile = new File(msgURL.getFile());		
		msg = new EbxmlMessage(msgFile);
		Assert.assertFalse((Boolean)m.invoke(InboundMessageProcessor.getInstance(), new Object[] {msg}));

	}
	
}
