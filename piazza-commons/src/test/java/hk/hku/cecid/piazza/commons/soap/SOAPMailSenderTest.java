package hk.hku.cecid.piazza.commons.soap;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPConstants;

import hk.hku.cecid.piazza.commons.net.ConnectionException;
import hk.hku.cecid.piazza.commons.test.utils.FixtureStore;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;


public class SOAPMailSenderTest extends TestCase{

	
	private static ClassLoader FIXTURE_LOADER = 
				FixtureStore.createFixtureLoader(false, SOAPMailSenderTest.class);
	private SOAPMailSender mailSender;
	
	private static final String MAIL_FROM = "test_from@cecid.hku.hk";
	private static final String MAIL_TO = "test_from@cecid.hku.hk";	
	
	@Before
	public void setUp() throws Exception {
		mailSender = new SOAPMailSender("127.0.0.1");
		System.out.println();
		System.out.println("---------" + this.getName() + " Start -------");
	}
	
	// Invoked for finalized.
	@After
	public void tearDown() throws Exception {
		mailSender = null;		
		System.out.println("---------" + this.getName() + " End   -------");
	}
	
	@Test
	public void testCreatedMessageLineMaxLimit(){
        SOAPMessage soapMessage = null;
        MimeMessage mimeMsg = null;
        MimeHeaders mimeHeaders = new MimeHeaders();
        
        InputStream contentStream = FIXTURE_LOADER.getResourceAsStream("mime_message_content.dat");		
        mimeHeaders.setHeader("Content-Type", "text/xml; charset=utf-8");
        
        /* 
         * Create an SOAP Message by using the resource loaded by FixtureLoader 
         * And check the soapMessage are successfully created. 
         */ 
        try {
			soapMessage = 
				MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage(mimeHeaders,contentStream);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Failed on loading the content inputstream\n"+e.getMessage());
		} catch (SOAPException e) {
			e.printStackTrace();
			Assert.fail("Create SOAP Message Failed\n"+e.getMessage());			
		}
		if(soapMessage == null)
			Assert.fail("Create SOAP Message Failed\n Message Should not be Null.");
		System.out.println("SoapMessage Created.");
		

        /* 
         * Create an Mime Message by using SOAP message created above.
         * And check the soapMessage are successfully created. 
         */ 
		try {
			mimeMsg = 
					mailSender.createMessage(MAIL_FROM, MAIL_TO, "",
									"SOAPMailSenderTest.testCreatedMessageLineMaxLimit", 
									soapMessage);
		} catch (ConnectionException e) {
			e.printStackTrace();
			Assert.fail("Create MimeMessage Failed\n"+e.getMessage());
		}
		if(mimeMsg == null)
			Assert.fail("Create MimeMessage Failed\n Message Should not be Null.");		
		System.out.println("MimeMessage Created.");
		
		
		
		 /* 
         * Get the content of the MimeMessage as output stream, 
         *  which made sure that there is no converstion has been done 
         */			
		ByteArrayOutputStream bbOutputStream = new ByteArrayOutputStream();
		 try {
			mimeMsg.writeTo(bbOutputStream);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		/**
		 * Assert the stream content 
		 */
		System.out.println("Begin assertion on message content.");
		byte[] msgContent = bbOutputStream.toByteArray();
		System.out.println("Mime Message Size: " + msgContent.length);
		int contentCount = 0;
		for(int index =0; index < msgContent.length; index++){
			boolean startofLine = false;
			// End of line
			if(msgContent[index] == 0x0D &&
							msgContent[index+1] == 0x0A){
				++index;				
				contentCount = 0;
				Assert.assertTrue(contentCount<=990);							
			}// Line content 
			else{				
				contentCount++;
			}
		}
	}
	
	@Test
	public void testCreatedMessage() throws Exception{
		  SOAPMessage soapMessage = null;
	        MimeMessage mimeMsg = null;
	        MimeHeaders mimeHeaders = new MimeHeaders();
	        
	        InputStream contentStream = FIXTURE_LOADER.getResourceAsStream("mime_message_content.dat");		
	        mimeHeaders.setHeader("Content-Type", "text/xml; charset=utf-8");
	        
	        /* 
	         * Create an SOAP Message by using the resource loaded by FixtureLoader 
	         * And check the soapMessage are successfully created. 
	         */ 
	        try {
				soapMessage = 
					MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage(mimeHeaders,contentStream);
			} catch (IOException e) {
				e.printStackTrace();
				Assert.fail("Failed on loading the content inputstream\n"+e.getMessage());
			} catch (SOAPException e) {
				e.printStackTrace();
				Assert.fail("Create SOAP Message Failed\n"+e.getMessage());			
			}
			if(soapMessage == null)
				Assert.fail("Create SOAP Message Failed\n Message Should not be Null.");
			System.out.println("SoapMessage Created.");
			

	        /* 
	         * Create an Mime Message by using SOAP message created above.
	         * And check the soapMessage are successfully created. 
	         */ 
			try {
				mimeMsg = 
						mailSender.createMessage(MAIL_FROM, MAIL_TO, "",
										"SOAPMailSenderTest.testCreatedMessageLineMaxLimit", 
										soapMessage);
			} catch (ConnectionException e) {
				e.printStackTrace();
				Assert.fail("Create MimeMessage Failed\n"+e.getMessage());
			}
			if(mimeMsg == null)
				Assert.fail("Create MimeMessage Failed\n Message Should not be Null.");		
			System.out.println("MimeMessage Created.");
			
			
			/*
			 * Pepare Sample Data 
			 */
			InputStream data = FIXTURE_LOADER.getResourceAsStream("mime_message_content.dat");
			mimeHeaders.setHeader("Content-Type", "text/xml; charset=utf-8");
			soapMessage = 
				MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage(mimeHeaders,data);
			ByteArrayOutputStream sampleContentStream = new ByteArrayOutputStream();
			
			OutputStream os = MimeUtility.encode(sampleContentStream, "base64");
			soapMessage.writeTo(os);
			
			ByteArrayOutputStream actualContentStream = new ByteArrayOutputStream();
			mimeMsg.writeTo(actualContentStream);
			
			BufferedReader sampleDataReader = new BufferedReader(new StringReader(sampleContentStream.toString()));
			BufferedReader actualDataReader = new BufferedReader(new StringReader(actualContentStream.toString()));
			
			// Loop the reader to content head,
			// in order to skip those message header
			for(int i =0; i < 8; i++){
				actualDataReader.readLine();
			}
			
			boolean endOfString = false;
			do{
				String sample = sampleDataReader.readLine();
				String actual = actualDataReader.readLine();
				if(sample == null){
					endOfString = true;
					continue;
				}
				Assert.assertTrue(sample+"\n"+actual, sample.equals(actual));
			}while (!endOfString);
		}
	
	
}
