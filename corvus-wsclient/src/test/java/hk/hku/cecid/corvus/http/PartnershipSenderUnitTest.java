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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import junit.framework.TestCase;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Decoder;

import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.KVPairData;

import hk.hku.cecid.piazza.commons.test.utils.FixtureStore;
import hk.hku.cecid.piazza.commons.test.utils.SimpleHttpMonitor;

/** 
 * The <code>PartnershipSenderUnitTest</code> is unit test of <code>PartnershipSender</code>. 
 *
 * TODO: Inadequate Test-case for error path.
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0 
 * @since   H2O 0908 
 */
public class PartnershipSenderUnitTest extends TestCase
{
	// Instance logger
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// Fixture loader
	private static ClassLoader 	FIXTURE_LOADER	= FixtureStore.createFixtureLoader(false, PartnershipSender.class);
	
	// Parameters 
	public static final int 	TEST_PORT 		= 1999;	
	public static final String 	TEST_ENDPOINT 	= "http://localhost:" + TEST_PORT;
	public static final String 	USER_NAME		= "corvus";
	public static final String 	PASSWORD		= "corvus";
		
	/*
	 * Since the partnership operation verifer requires Internet connectivity 
	 * and therefore i have added this dirty Proxy settings here. Changed if needed.
	 */
	static {
		/*
		System.setProperty("http.proxyHost", "proxy.cs.hku.hk");
		System.setProperty("http.proxyPort", "8282");
		*/
	}
	
	/** The testing target which is an PartnershipSender and the associated data*/
	protected PartnershipSender 	target;
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
		/*
		 * Create an HTTP monitor which mimic the regular HTML response from the partnership administrative page.
		 * Note, both AS2 and EbMS have the same output status line from the administrative page.
		 */ 		
		this.monitor 	= new SimpleHttpMonitor(TEST_PORT)
		{																					  
			private byte[] mockContent = "<td><a name=\"message\">Message: Partnership added successfully</a></td>".getBytes();
			
			protected int  onResponseLength(){
				return mockContent.length;
			}
			
			protected void onResponse(final OutputStream os) throws IOException {
				super.onResponse(os);				
				os.write(mockContent);
			}
		};
		
		
	}
	
	/** Initialize the test target which is a PartnershipSender. */
	public void initTestTarget() throws Exception 
	{
		URL logURL = FIXTURE_LOADER.getResource(FixtureStore.TEST_LOG);
		if (logURL == null)
			throw new NullPointerException("Missing fixture " + FixtureStore.TEST_LOG + " in the fixture path");

		/*
		 * The data is constructed at this method instead of initTestData because 
		 * it has to be referred by the inner method for our testing target.
		 */
		this.kvData = new KVPairData(3);
		
		final Map props 		= this.kvData.getProperties();
		final Map data2webForm 	= new LinkedHashMap();
		for (int i = 0; i < 3; i++){
			props.put("dataKeyName" + i, "testFormParamContent" + i);
			data2webForm.put("dataKeyName" + i, "testWebFormParamName" + i);
		}				
		
		// Create a mock partnership operation mapping.
		final Map partnershipOpMap = new HashMap();
		partnershipOpMap.put(new Integer(0), "add");
		partnershipOpMap.put(new Integer(1), "delete");
		partnershipOpMap.put(new Integer(2), "update");		
		
		File log = new File(logURL.getFile());
		this.testClassLogger = new FileLogger(log);
		
		// Create an anonymous partnership sender and implement the abstract method for our testing.
		this.target = new PartnershipSender(this.testClassLogger, this.kvData){ 			
			public Map getPartnershipOperationMapping()	{ return partnershipOpMap; }  						
			public Map getPartnershipMapping()			{ return data2webForm; }
		};
		this.target.setServiceEndPoint(TEST_ENDPOINT);
		this.target.setBasicAuthentication(USER_NAME, PASSWORD);
	}
	
	/** Test whether the add partnership request operation perform correctly **/
	public void testAddPartnership() throws Exception 
	{
		monitor.start();	// Start the HTTP monitor.
		Thread.sleep(1000);						
		this.target.setExecuteOperation(PartnershipOp.ADD);		
		this.target.run();		
		this.assertHttpRequestReceived();		
	}
	
	/** Test whether the update partnership request operation perform correctly **/
	public void testUpdatePartnership() throws Exception 
	{
		this.monitor.start();	// Start the HTTP monitor.
		Thread.sleep(1000);				
		this.target.setExecuteOperation(PartnershipOp.UPDATE);			
		this.target.run();	
		this.assertHttpRequestReceived();
	}
	
	/** Test whether the update partnership request operation perform correctly **/
	public void testDeletePartnership() throws Exception 
	{
		this.monitor.start();	// Start the HTTP monitor.
		Thread.sleep(1000);				
		this.target.setExecuteOperation(PartnershipOp.DELETE);		
		this.target.run();	
		this.assertHttpRequestReceived();
	}
	
	/** Stop the HTTP monitor preventing JVM port binding **/ 
	public void tearDown() throws Exception {
		this.monitor.stop();		
		Thread.sleep(1500); // Make some delay for releasing the socket.
		logger.info(this.getName() + " End ");
	}
	
	/**
	 * A Helper method which assert whether the HTTP content received in the HTTP monitor 
	 * is a multi-part form data, with basic-auth and well-formed partnership operation request.
	 */
	private void assertHttpRequestReceived() throws Exception
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
		assertEquals("Invalid content type", "multipart/form-data", mediaType);
		
		// #3 Check the multi-part content.
		// Make a request context that bridge the content from our monitor to FileUpload library.    
		RequestContext rc = new RequestContext(){
			public String 		getCharacterEncoding()	{ return "charset=ISO-8859-1"; }
			public int 			getContentLength()		{ return monitor.getContentLength(); }
			public String 		getContentType()		{ return monitor.getContentType(); }
			public InputStream 	getInputStream()		{ return monitor.getInputStream(); }
		};
		
		FileUpload multipartParser = new FileUpload();
		FileItemIterator item  = multipartParser.getItemIterator(rc);
		FileItemStream fstream = null;
		
		/*
		 * For each field in the partnership, we have to check the existence of the 
		 * associated field in the HTTP request. Also we check whether the content
		 * of that web form field has same data to the field in the partnership.    
		 */
		itr 			= this.target.getPartnershipMapping().entrySet().iterator();
		Map data   	 	= ((KVPairData)this.target.properties).getProperties();
		Map.Entry e; 			// an entry representing the partnership data to web form name mapping.
		String formParamName;	// a temporary pointer pointing to the value in the entry.
		Object dataValue;		// a temporary pointer pointing to the value in the partnership data.
		
		while (itr.hasNext()){
			e = (Map.Entry) itr.next();			
			formParamName = (String) e.getValue();
			// Add new part if the mapped key is not null.
			if (formParamName != null){
				assertTrue("Insufficient number of web form parameter hit.", item.hasNext());			
				// Get the next multi-part element.
				fstream = item.next();
				// Assert field name
				assertEquals("Missed web form parameter ?", formParamName, fstream.getFieldName());
				// Assert field content
				dataValue = data.get(e.getKey());
				if (dataValue instanceof String)
				{
					// Assert content equal.
					assertEquals((String)dataValue, IOHandler.readString(fstream.openStream(), null));
				} 
				else if (dataValue instanceof byte[])
				{
					byte[] expectedBytes = (byte[])dataValue;
					byte[] actualBytes 	 = IOHandler.readBytes(fstream.openStream());
					// Assert byte length equal
					assertEquals(expectedBytes.length, actualBytes.length);
					for (int j = 0; j < expectedBytes.length; j++)
						assertEquals(expectedBytes[j], actualBytes[j]);
				}
				else {				
					throw new IllegalArgumentException("Invalid content found in multipart.");
				}
				// Log information.
				logger.info("Field name found and verifed: " + fstream.getFieldName() + " content type:" + fstream.getContentType());				
			}
		}
		/* Check whether the partnership operation in the HTTP request is expected as i thought */
		assertTrue("Missing request_action ?!", item.hasNext());
		fstream = item.next();
		assertEquals("request_action", fstream.getFieldName());
		// Assert the request_action has same content the operation name.
		Map partnershipOpMap = this.target.getPartnershipOperationMapping();
		assertEquals(partnershipOpMap.get(new Integer(this.target.getExecuteOperation())), 
					 IOHandler.readString(fstream.openStream(), null));
	}
}
