package hk.hku.cecid.corvus.ws;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.fileupload.RequestContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.EBMSMessageHistoryRequestData;
import hk.hku.cecid.corvus.ws.EBMSMessageHistoryQuerySender;
import hk.hku.cecid.corvus.ws.data.KVPairData;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.test.utils.FixtureStore;
import hk.hku.cecid.piazza.commons.test.utils.SimpleSoapMonitor;
import junit.framework.Assert;
import junit.framework.TestCase;

public class EBMSMessageHistoryQuerySenderTest extends TestCase{
	
	private static ClassLoader 	FIXTURE_LOADER	= 
					FixtureStore.createFixtureLoader(false, EBMSMessageHistoryQuerySender.class);

	// Parameters 
	public static final int 	TEST_PORT 		= 9000;	
	public static final String 	TEST_ENDPOINT 	= "http://localhost:" + TEST_PORT + "/corvus/httpd/ebms/msg_history";
	public static final String 	USER_NAME		= "corvus";
	public static final String 	PASSWORD		= "corvus";
	
	/** The testing target which is an PartnershipSender and the associated data*/
	protected EBMSMessageHistoryQuerySender 	target;
	protected KVPairData						kvData;
	protected FileLogger 						testClassLogger;
	
	/** The helper for capturing the HTTP data */
	private SimpleSoapMonitor	monitor;
	// Instance logger
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/** Setup the fixture. */

	@Before
	public void setUp() throws Exception {
		
		this.initTest();
		logger = LoggerFactory.getLogger(this.getName());
		logger.info(this.getName() + " Start ");	
		this.monitor.start();
		Thread.sleep(5000);
	}
	
	public void initTest()throws Exception 
	{
		URL logURL = FIXTURE_LOADER.getResource(FixtureStore.TEST_LOG);
		if (logURL == null)
			throw new NullPointerException(
					"Missing fixture " + FixtureStore.TEST_LOG + " in the fixture path");
		
		File log = new File(logURL.getFile());
		this.testClassLogger = new FileLogger(log);
		
		this.monitor 	= new SimpleSoapMonitor(TEST_PORT)
		{
			@Override
			public int onResponseLength()
			{
				return 532;
			}
			
			protected String onResponseContentType()
			{
				return "text/xml;charset=utf-8";
			}
			
			@Override
			public void onResponse(OutputStream os) throws IOException 
			{
				super.onResponse(os);
				/*os.write(STATUS_200);
				os.write(CRLF);
				os.write(HD_SERVIER);
				os.write(CRLF);
				os.write(HD_CT_LEN);		
				os.write(String.valueOf(onResponseLength()).getBytes());
				os.write(CRLF);		
				os.write("Content-type: text/xml;charset=utf-8".getBytes());
				os.write(CRLF);
				os.write(CRLF);*/								
				
				
				InputStream ins = FIXTURE_LOADER.getResourceAsStream("testResponse.log");
				IOHandler.pipe(ins, os);				
			}
		};
	}
	
	@After
	public void tearDown()throws Exception{
		this.monitor.stop();
		Thread.sleep(5000);
	}

	@Test
	public void testNormalCriteriaData ()throws Exception{
		
		EBMSMessageHistoryRequestData expectData = 
						new EBMSMessageHistoryRequestData();
		expectData.setEndPoint(TEST_ENDPOINT);
		
		expectData.setMessageBox("INbox");
		expectData.setStatus("dL");
		
		expectData.setMessageId("20080402-105745-64017@127.0.0.1");
		expectData.setService("cecid:cecid");
		expectData.setAction("Order");
		expectData.setConversationId("convId");
		expectData.setCpaId("cpaid");
		
		
		this.target = new EBMSMessageHistoryQuerySender(this.testClassLogger,expectData);
		try{
			this.target.run();
		}catch(Error err){
			Assert.fail("Error should Not be thrown here.");
		}
		
		EBMSMessageHistoryRequestData actualData = getHttpRequestData();
		Assert.assertEquals(expectData.getMessageId(), actualData.getMessageId());
		Assert.assertEquals(expectData.getService(), actualData.getService());
		Assert.assertEquals(expectData.getAction(), actualData.getAction());
		Assert.assertEquals(expectData.getCpaId(), actualData.getCpaId());
		Assert.assertEquals(expectData.getConversationId(), actualData.getConversationId());
		
		Assert.assertTrue("inbox".equalsIgnoreCase(actualData.getMessageBox()));
		Assert.assertTrue("DL".equalsIgnoreCase(actualData.getStatus()));
		
	}
	
	@Test
	public void testWildcardCriteriaData ()throws Exception{
		
		EBMSMessageHistoryRequestData expectData = 
						new EBMSMessageHistoryRequestData();
		expectData.setEndPoint(TEST_ENDPOINT);
		
		expectData.setMessageBox("inbox");
		expectData.setStatus("DL");
		
		expectData.setMessageId("%");
		expectData.setService("%%%%%%%%");
		expectData.setAction("__%%%");
		expectData.setConversationId("%%%\\\\");
		expectData.setCpaId("%_%");
		
		
		this.target = new EBMSMessageHistoryQuerySender(this.testClassLogger,expectData);
		try{
			this.target.run();
		}catch(Error err){
			Assert.fail("Error should Not be thrown here.");
		}
		
		EBMSMessageHistoryRequestData actualData = getHttpRequestData();
		Assert.assertEquals(expectData.getMessageId(), actualData.getMessageId());
		Assert.assertEquals(expectData.getService(), actualData.getService());
		Assert.assertEquals(expectData.getAction(), actualData.getAction());
		Assert.assertEquals(expectData.getCpaId(), actualData.getCpaId());
		Assert.assertEquals(expectData.getConversationId(), actualData.getConversationId());
		
		Assert.assertTrue("inbox".equals(actualData.getMessageBox()));
		Assert.assertTrue("DL".equals(actualData.getStatus()));
	}	
		
	@Test
	public void testNullCriteriaData ()throws Exception{
		
		EBMSMessageHistoryRequestData expectData = 
						new EBMSMessageHistoryRequestData();
		expectData.setEndPoint(TEST_ENDPOINT);
		
		expectData.setMessageBox("");
		expectData.setStatus("");
		
		this.target = new EBMSMessageHistoryQuerySender(this.testClassLogger,expectData);
		this.target.run();
		
		EBMSMessageHistoryRequestData actualData = getHttpRequestData();
		Assert.assertEquals(null, actualData.getMessageBox());
		Assert.assertEquals(null, actualData.getStatus());
		
		Assert.assertEquals(null, actualData.getMessageId());
		Assert.assertEquals(null, actualData.getConversationId());
		Assert.assertEquals(null, actualData.getCpaId());
		Assert.assertEquals(null, actualData.getService());
		Assert.assertEquals(null, actualData.getAction());
	}
	
	@Test
	public void testSpecialCharacterCriteriaData ()throws Exception{
		
		EBMSMessageHistoryRequestData expectData = 
						new EBMSMessageHistoryRequestData();
		expectData.setEndPoint(TEST_ENDPOINT);
		
		expectData.setMessageBox("INbox");
		expectData.setStatus("dL");
		
		expectData.setMessageId("*msg#Id");
		expectData.setService("cecid:cecid");
		expectData.setAction("<>");
		expectData.setConversationId("?ID%");
		expectData.setCpaId("#^&--");
		
		
		this.target = new EBMSMessageHistoryQuerySender(this.testClassLogger,expectData);
		try{
			this.target.run();
		}catch(Error err){
			Assert.fail("Error should Not be thrown here.");
		}
		
		EBMSMessageHistoryRequestData actualData = getHttpRequestData();
		Assert.assertEquals(expectData.getMessageId(), actualData.getMessageId());
		Assert.assertEquals(expectData.getService(), actualData.getService());
		Assert.assertEquals(expectData.getAction(), actualData.getAction());
		Assert.assertEquals(expectData.getCpaId(), actualData.getCpaId());
		Assert.assertEquals(expectData.getConversationId(), actualData.getConversationId());
		
		Assert.assertTrue("INbox".equals(actualData.getMessageBox()));
		Assert.assertTrue("dL".equals(actualData.getStatus()));
		
	}
	
	@Test
	public void testFail_NegativeQueryLimit ()throws Exception{
		
		EBMSMessageHistoryRequestData expectData = 
						new EBMSMessageHistoryRequestData();
		expectData.setEndPoint(TEST_ENDPOINT);
		
		expectData.setMessageBox("");
		expectData.setStatus("");
		
		this.target = new EBMSMessageHistoryQuerySender(this.testClassLogger,expectData);
		try{
			this.target.run();
		}catch(Error err){
			Assert.fail("Query Limit should transformed to 100");
		}
		EBMSMessageHistoryRequestData actualData = getHttpRequestData();
	}
		
	private EBMSMessageHistoryRequestData getHttpRequestData() throws Exception
	{
		// Check content Type
		String contentType 	= monitor.getContentType();
		
		if(contentType == null){
				System.out.println((monitor==null?"Null Monitor":"Monitor not null"));
				System.out.println("Lengeth " + monitor.getContentLength());
				Assert.fail("Null Content");
		}
		
		String mediaType 	= contentType.split(";")[0];		
		assertEquals("Invalid content type", "text/xml", mediaType);
		
		// Check the multi-part content.
		// Make a request context that bridge the content from our monitor to FileUpload library.    
		RequestContext rc = new RequestContext(){
			public String 		getCharacterEncoding()	{ return "charset=utf-8"; }
			public int 			getContentLength()		{ return monitor.getContentLength(); }
			public String 		getContentType()		{ return monitor.getContentType(); }
			public InputStream 	getInputStream()		{ return monitor.getInputStream(); }
		};
		
		
		BufferedReader bReader = 
						new BufferedReader(new InputStreamReader(monitor.getInputStream()));
		
		String strLine = "";
		do{
			strLine = bReader.readLine();
		}while(!strLine.contains("SOAP-ENV"));
		
		MimeHeaders header = new MimeHeaders();		
		header.addHeader("Content-Type", "text/xml");
		
		SOAPMessage msg = MessageFactory.newInstance().createMessage(
				header, new ByteArrayInputStream(strLine.getBytes()));
		
		EBMSMessageHistoryRequestData data = new EBMSMessageHistoryRequestData();
		data.setMessageId(getElementValue(msg.getSOAPBody(), "tns:messageId"));
		data.setMessageBox(getElementValue(msg.getSOAPBody(), "tns:messageBox"));
		data.setStatus(getElementValue(msg.getSOAPBody(), "tns:status"));
		data.setService(getElementValue(msg.getSOAPBody(), "tns:service"));
		data.setAction(getElementValue(msg.getSOAPBody(), "tns:action"));
		data.setConversationId(getElementValue(msg.getSOAPBody(), "tns:conversationId"));
		data.setCpaId(getElementValue(msg.getSOAPBody(), "tns:cpaId"));
		return data;
	}
	
	private String getElementValue(SOAPElement msgBody, String name){
		Iterator iterator = msgBody.getChildElements();
		while(iterator.hasNext()){
			SOAPElement current = (SOAPElement) iterator.next();
			if(current.getNodeName().equalsIgnoreCase(name))
				return current.getValue();
		}
		return null;
	}
}	
