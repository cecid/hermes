/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.http;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Decoder;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.KVPairData;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.test.utils.FixtureStore;
import hk.hku.cecid.piazza.commons.test.utils.SimpleHttpMonitor;

/**
 * The <code>EnvelopQuerySenderUnitTest</code> is unit test of <code>EnvelopQuerySender</code>. 
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   H2O 28/11/2007
 */
public class EnvelopQuerySenderUnitTest extends TestCase 
{
	// TODO: Most of the code is repeated as PartnershipSender, can re-use ?
	
	// Instance logger
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// Fixture loader
	private static ClassLoader 	FIXTURE_LOADER	= FixtureStore.createFixtureLoader(false, EnvelopQuerySender.class);
	
	// Parameters 
	public static final int 	TEST_PORT 		= 1999;	
	public static final String 	TEST_ENDPOINT 	= "http://localhost:" + TEST_PORT;
	public static final String 	USER_NAME		= "corvus";
	public static final String 	PASSWORD		= "corvus";

	/** The testing target which is an PartnershipSender and the associated data*/
	protected EnvelopQuerySender 	target;
	protected KVPairData			kvData;
	protected FileLogger 			testClassLogger;	
	
	/** The helper for capturing the HTTP data */
	private SimpleHttpMonitor	monitor;
		
	/** Setup the fixture. */
	public void setUp() throws Exception {
		this.initTestData();
		this.initTestTarget();
		logger = LoggerFactory.getLogger(this.getName());
		logger.info(this.getName() + " Start ");		
	}
	
	/** Initialize the test data **/
	public void initTestData()
	{
		this.monitor = new SimpleHttpMonitor(TEST_PORT);
		this.kvData = new KVPairData(1);
	}
	
	/** Initialize the test target which is a PartnershipSender. */
	public void initTestTarget() throws Exception 
	{
		URL logURL = FIXTURE_LOADER.getResource(FixtureStore.TEST_LOG);
		if (logURL == null)
			throw new NullPointerException("Missing fixture " + FixtureStore.TEST_LOG + " in the fixture path");
				
		File log = new File(logURL.getFile());
		this.testClassLogger = new FileLogger(log);
		
		// Create an anonymous partnership sender and implement the abstract method for our testing.
		this.target = new EnvelopQuerySender(this.testClassLogger, this.kvData);		
		this.target.setServiceEndPoint(TEST_ENDPOINT);
		this.target.setBasicAuthentication(USER_NAME, PASSWORD);
		this.target.setMessageCriteriaToDownload("test-message-id", "INBOX");
	}
	
	/** Test whether the setMessageCriteriaToDownload throw exception when 1 and 2 argument is null. **/	
	public void testSetMessageCriteriaWithNull(){
		boolean failed = false; 
		try {
			this.target.setMessageCriteriaToDownload(null, null);
		}catch(Exception ex){ failed = true; logger.info("Expected Error: " + ex.getMessage()); }
		assertTrue(failed);
	}
	
	/** Test whether the setMessageCriteriaToDownload throw exception when second argument is null. **/
	public void testSetMessageCriteriaWithNull2(){
		boolean failed = false; 
		try {
			this.target.setMessageCriteriaToDownload("test-message-id", null);
		}catch(Exception ex){ failed = true; logger.info("Expected Error: " + ex.getMessage()); }
		assertTrue(failed);
	}
	
	/** Test whether the setMessageCriteriaToDownload throw exception when second argument is invalid. **/
	public void testSetMessageCriteriaWithInvalid(){
		boolean failed = false; 
		try {
			this.target.setMessageCriteriaToDownload("test-message-id", "fake-message-box");
		}catch(Exception ex){ failed = true; logger.info("Expected Error: " + ex.getMessage()); }
		assertTrue(failed);
	}
	
	/** Test whether the setMessageCriteriaToDownload works well under valid data. **/
	public void testSetMessageCriteriaProperty()
	{
		String testMID = "test-message-id";
		String testMSB = "INBOX";		
		
		this.target.setMessageCriteriaToDownload(testMID, testMSB);
		// #1 Assert message id and message box.
		assertEquals(testMID, this.target.getMessageIdToDownload());
		assertEquals(testMSB, this.target.getMessageBoxToDownload());
		
		testMSB = "OUTBOX";
		this.target.setMessageCriteriaToDownload(testMID, testMSB);
		// #2 Assert message id and message box.
		assertEquals(testMID, this.target.getMessageIdToDownload());
		assertEquals(testMSB, this.target.getMessageBoxToDownload());
	}
	
	/** Test whether the **/
	public void testEnvelopQuery() throws Exception 
	{
		this.monitor.start();	// Start the HTTP monitor.		
		Thread.sleep(1000);						
		this.target.run();			
		this.assertHTTPRequestReceived();
	}
	
	
	/** Stop the HTTP monitor preventing JVM port binding **/ 
	public void tearDown() throws Exception {
		this.monitor.stop();		
		logger.info(this.getName() + " End ");
	}
	
	/**
	 * A Helper method which assert whether the HTTP content received in the HTTP monitor 
	 * is valid request.
	 */
	private void assertHTTPRequestReceived() throws Exception
	{
		// Debug print information.
		Map headers   	= monitor.getHeaders();
		
		// #0 Assertion
		assertFalse("No HTTP header found in the captured data.", headers.isEmpty());
		
		Map.Entry tmp 	= null;
		Iterator itr	= null;
		itr = headers.entrySet().iterator();
		logger.info("Header information");
		while (itr.hasNext()){
			tmp = (Map.Entry) itr.next();
			logger.info(tmp.getKey() + " : " + tmp.getValue());
		}
		
		// #1 Check BASIC authentication value.
		String basicAuth 	= (String) headers.get("Authorization");
		
		// #1 Assertion
		assertNotNull("No Basic Authentication found in the HTTP Header.", basicAuth);
		
		String[] authToken 	= basicAuth.split(" ");
		
		// There are 2 token, one is the "Basic" and another is the base64 auth value.
		assertTrue	(authToken.length == 2);	
		assertTrue	("Missing basic auth prefix 'Basic'", authToken[0].equalsIgnoreCase("Basic"));		
		// #1 Decode the base64 authentication value to see whether it is "corvus:corvus".
		String decodedCredential = new String(new BASE64Decoder().decodeBuffer(authToken[1]), "UTF-8");
		assertEquals("Invalid basic auth content", USER_NAME + ":" + PASSWORD, decodedCredential);
		
		// #2 Check content Type
		String contentType 	= monitor.getContentType();
		String mediaType 	= contentType.split(";")[0];		
		assertEquals("Invalid content type", "application/x-www-form-urlencoded", mediaType);
		
		// #3 Assert content.
		String encodedContent = IOHandler.readString(monitor.getContentStream(), null);
		StringTokenizer st = new StringTokenizer(encodedContent, "&");
		assertTrue	("The POST content should at least has 2 parameters", (st.countTokens() >= 2));
		
		String[] kvPair = null;
		
		// #3.1 Assert message id parameter		
		kvPair = st.nextToken().split("=");		
		assertTrue	("The message id parameter should in the format of 'message_id=<id>'.",
					 (kvPair.length == 2));
		assertEquals("The first parameter name should be 'message_id'",
					 this.target.MSGID_FORM_PARAM, kvPair[0]);
		assertEquals("The content of 'message_id' parameter does not match", 
					 this.target.getMessageIdToDownload(), kvPair[1]);
		
		// #3.2 Assert message_box parameter		
		kvPair = st.nextToken().split("=");
		String box = this.target.getMessageBoxToDownload();
		Map boxMap = this.target.getMessageBoxMapping();
		if (boxMap != null)
			box = (String) boxMap.get(box);
		
		assertTrue	("The message box parameter should in the format of 'message_box=<id>'.",
				 	 (kvPair.length == 2));
		assertEquals("The first parameter name should be 'message_box'",
				 	 this.target.MSGBOX_FORM_PARAM, kvPair[0]);
		assertEquals("The content of 'message_box' parameter does not match", 
					 box, kvPair[1]);
	}
}
