/**
 * 
 */
package hk.hku.cecid.edi.sfrm.handler;

import org.junit.Assert;
import org.junit.Test;

import java.security.cert.X509Certificate;
import java.util.Map;

import hk.hku.cecid.edi.sfrm.spa.SFRMException;
import hk.hku.cecid.edi.sfrm.dao.ds.SFRMPartnershipDSDVO;
import hk.hku.cecid.edi.sfrm.handler.OutgoingMessageHandler;
import hk.hku.cecid.edi.sfrm.pkg.SFRMHeader;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessageException;
import hk.hku.cecid.edi.sfrm.handler.SFRMMessageFactory;
import hk.hku.cecid.piazza.commons.net.ConnectionException;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;
import hk.hku.cecid.piazza.commons.security.SMimeException;
import hk.hku.cecid.piazza.commons.security.SMimeMessage;
import hk.hku.cecid.piazza.commons.test.SystemComponentTest;
import hk.hku.cecid.piazza.commons.test.utils.SimpleHttpMonitor;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

/**
 * @author Patrick Yip
 *
 */
public class OutgoingMessageHandlerTest extends SystemComponentTest<OutgoingMessageHandler> {
	
	private OutgoingMessageHandler outHandler;
	
	// testing data
	private SimpleHttpMonitor mock;
	private int mockPort = 9000;
	private String mockEndpoint = "http://localhost:" + mockPort;
	private String partnershipId = "loopback";
	private String messageId = "messageId";
	private String filename = "file.tar";
	private int totalSegment = 1;
	private long totalSize = 1024;
	private String contentType = "text/plain";
	private String messageContent = "Hello World!!!";
	private String publicCertMD5 = "3D0483C7D74BC9894B7667ECB5A121A108E01629";
	
	@Override
	public String getSystemComponentId() {
		return "outgoing-message-handler";
	}

	@Override
	public void setUp() throws Exception {
		outHandler = (OutgoingMessageHandler)TARGET;
	}

	@Override
	public void tearDown() throws Exception {	
	}	

	// TODO: suppose in test case SFRMMessageFactoryTest
	@Test
	public void testCreateHandshakingRequest() throws Exception {
		SFRMMessage message = SFRMMessageFactory.getInstance().
			createHandshakingRequest(messageId, partnershipId, totalSegment, totalSize, filename);
		message.setContent(messageContent, contentType);
		
		// why message id need to be <messageId> ?
		Assert.assertEquals("Message-Id should be " + messageId, messageId,  (String)message.getHeader(SFRMHeader.MESSAGE_ID));
		Assert.assertEquals("Partnership-Id should be " + partnershipId, partnershipId, (String)message.getHeader(SFRMHeader.SFRM_PARTNERSHIP));
		Assert.assertEquals("Segment-Type should be META", SFRMConstant.MSGT_META, message.getHeader(SFRMHeader.SFRM_SEGMENT_TYPE));
		//TODO: To see why the assertion fail on the following		
		Assert.assertEquals("Segment No should be 0", 0, Integer.parseInt(message.getHeader(SFRMHeader.SFRM_SEGMENT_NO)));
		Assert.assertEquals("Total number of segment should be " + totalSegment, totalSegment, Integer.parseInt(message.getHeader(SFRMHeader.SFRM_META_TOTAL_SEGMENT)));
		Assert.assertEquals("Total size of segment should be " + totalSize, totalSize, Long.parseLong(message.getHeader(SFRMHeader.SFRM_TOTAL_SIZE)));
		Assert.assertEquals("Filename should be " + filename, filename, message.getHeader(SFRMHeader.SFRM_FILENAME));
	}
	
	
	@Test
	public void testMetaMessgeHeader() throws Exception {
		SFRMMessage message = SFRMMessageFactory.getInstance().
			createHandshakingRequest(messageId, partnershipId, totalSegment, totalSize, filename);
		message.setContent(messageContent, contentType);
		outHandler.packOutgoingMessage(message, null, null, null);
		
		// why message id need to be <messageId> ?
		Assert.assertEquals("Message-Id should be " + messageId, messageId,  (String)message.getHeader(SFRMHeader.MESSAGE_ID));
		Assert.assertEquals("Partnership-Id should be " + partnershipId, partnershipId, (String)message.getHeader(SFRMHeader.SFRM_PARTNERSHIP));
		Assert.assertEquals("Segment-Type should be META", SFRMConstant.MSGT_META, message.getHeader(SFRMHeader.SFRM_SEGMENT_TYPE));
		//TODO: To see why the assertion fail on the following		
		Assert.assertEquals("Segment No should be 0", 0, Integer.parseInt(message.getHeader(SFRMHeader.SFRM_SEGMENT_NO)));
		Assert.assertEquals("Total number of segment should be " + totalSegment, totalSegment, Integer.parseInt(message.getHeader(SFRMHeader.SFRM_META_TOTAL_SEGMENT)));
		Assert.assertEquals("Total size of segment should be " + totalSize, totalSize, Long.parseLong(message.getHeader(SFRMHeader.SFRM_TOTAL_SIZE)));
		Assert.assertEquals("Filename should be " + filename, filename, message.getHeader(SFRMHeader.SFRM_FILENAME));
	}
	
	
	@Test
	public void testPlainMetaMessge() throws Exception {
		SFRMMessage message = SFRMMessageFactory.getInstance().
			createHandshakingRequest(messageId, partnershipId, totalSegment, totalSize, filename);
		message.setContent("Hello World!!!", contentType);
		outHandler.packOutgoingMessage(message, null, null, null);
		
		SFRMMessage newMsg = new SFRMMessage(message.getHeaders(), message.getContentStream());
		Assert.assertTrue("Message type should be META", newMsg.getClassifier().isMeta());
		Assert.assertFalse("Message should be unsigned", newMsg.getClassifier().isSigned());
		Assert.assertFalse("Message should be unencrypted", newMsg.getClassifier().isEncrypted());
		Assert.assertTrue("Incorrect message content", messageContent.equals(newMsg.getContent()));
	}
	
		
	@Test
	public void testSignMD5MetaMessge() throws Exception {
		LOG.info("testSignMessge");
		
		SFRMMessage message = SFRMMessageFactory.getInstance().
			createHandshakingRequest(messageId, partnershipId, totalSegment, totalSize, filename);
		message.setContent(messageContent, contentType);
		outHandler.packOutgoingMessage(message, "md5", null, null);
		
		KeyStoreManager keyman = outHandler.getKeyStoreManager();
		SMimeMessage smime = new SMimeMessage(
				message.getBodyPart(),
				keyman.getX509Certificate(), 
				keyman.getPrivateKey());

		Assert.assertTrue("Message should be signed", smime.isSigned());
		Assert.assertFalse("Message should be not encrypted", smime.isEncrypted());
		
		try {
			smime = smime.verify();
		} catch (SMimeException sme) {
			Assert.fail("Signature verification error");
		}

		Assert.assertTrue("Incorrect message content", messageContent.equals(smime.getBodyPart().getContent()));
	}
	
	@Test
	public void testSignSHA1MetaMessge() throws Exception {
		LOG.info("testSignMessge");
		
		SFRMMessage message = SFRMMessageFactory.getInstance().
			createHandshakingRequest(messageId, partnershipId, totalSegment, totalSize, filename);
		message.setContent(messageContent, contentType);

		outHandler.packOutgoingMessage(message, "sha1", null, null);
		
		KeyStoreManager keyman = outHandler.getKeyStoreManager();
		SMimeMessage smime = new SMimeMessage(
				message.getBodyPart(),
				keyman.getX509Certificate(), 
				keyman.getPrivateKey());

		Assert.assertTrue("Message should be signed", smime.isSigned());
		Assert.assertFalse("Message should be unencrypted", smime.isEncrypted());
		
		try {
			smime = smime.verify();
		} catch (SMimeException sme) {
			Assert.fail("Signature verification error");
		}

		Assert.assertTrue("Incorrect message content", messageContent.equals(smime.getBodyPart().getContent()));
	}
	
	@Test(expected=SMimeException.class)
	public void testSignMetaMessgeFailed() throws Exception {
		LOG.info("testSignMessge");
		
		SFRMMessage message = SFRMMessageFactory.getInstance().
			createHandshakingRequest(messageId, partnershipId, totalSegment, totalSize, filename);
		message.setContent(messageContent, contentType);

		outHandler.packOutgoingMessage(message, "sha1", null, null);
		
		SMimeMessage smime = new SMimeMessage(message.getBodyPart());
		
		Assert.assertTrue("Message should be signed", smime.isSigned());
		Assert.assertFalse("Message should be unencrypted", smime.isEncrypted());
		
		smime = smime.verify();
	}	
	
	@Test
	public void testEncryptDES3MetaMessge() throws Exception {
		SFRMMessage message = SFRMMessageFactory.getInstance().
			createHandshakingRequest(messageId, partnershipId, totalSegment, totalSize, filename);
		message.setContent(messageContent, contentType);

		Method m = SFRMPartnershipDSDVO.class.getDeclaredMethod("getX509Certificate", File.class);
		m.setAccessible(true);
		X509Certificate cert = (X509Certificate)m.invoke(new SFRMPartnershipDSDVO(), 
				new File(FIXTURE_LOADER.getResource(publicCertMD5).getFile()));
		outHandler.packOutgoingMessage(message, null, "3des", cert);
		
		KeyStoreManager keyman = outHandler.getKeyStoreManager();
		SMimeMessage smime = new SMimeMessage(
				message.getBodyPart(),
				keyman.getX509Certificate(), 
				keyman.getPrivateKey());
		
		Assert.assertFalse("Message should be signed", smime.isSigned());
		Assert.assertTrue("Message should be unencrypted", smime.isEncrypted());
		
		try {
			smime = smime.decrypt();
		} catch (SMimeException sme) {
			Assert.fail("Decryption error");
		}

		Assert.assertTrue("Incorrect message content", messageContent.equals(smime.getBodyPart().getContent()));
	}
	
	@Test
	public void testEncryptRC2MetaMessge() throws Exception {
		SFRMMessage message = SFRMMessageFactory.getInstance().
			createHandshakingRequest(messageId, partnershipId, totalSegment, totalSize, filename);
		message.setContent(messageContent, contentType);

		Method m = SFRMPartnershipDSDVO.class.getDeclaredMethod("getX509Certificate", File.class);
		m.setAccessible(true);
		X509Certificate cert = (X509Certificate)m.invoke(new SFRMPartnershipDSDVO(), 
				new File(FIXTURE_LOADER.getResource(publicCertMD5).getFile()));
		
		outHandler.packOutgoingMessage(message, null, "rc2", cert);
		
		KeyStoreManager keyman = outHandler.getKeyStoreManager();
		SMimeMessage smime = new SMimeMessage(
				message.getBodyPart(),
				keyman.getX509Certificate(), 
				keyman.getPrivateKey());
		
		Assert.assertFalse("Message should be signed", smime.isSigned());
		Assert.assertTrue("Message should be unencrypted", smime.isEncrypted());
		
		try {
			smime = smime.decrypt();
		} catch (SMimeException sme) {
			Assert.fail("Decryption error");
		}

		Assert.assertTrue("Incorrect message content", messageContent.equals(smime.getBodyPart().getContent()));
	}
	
	@Test(expected=SMimeException.class)
	public void testEncryptMetaMessgeFailed() throws Exception {
		SFRMMessage message = SFRMMessageFactory.getInstance().
			createHandshakingRequest(messageId, partnershipId, totalSegment, totalSize, filename);
		message.setContent(messageContent, contentType);

		Method m = SFRMPartnershipDSDVO.class.getDeclaredMethod("getX509Certificate", File.class);
		m.setAccessible(true);
		X509Certificate cert = (X509Certificate)m.invoke(new SFRMPartnershipDSDVO(), 
				new File(FIXTURE_LOADER.getResource(publicCertMD5).getFile()));
		
		outHandler.packOutgoingMessage(message, null, "rc2", cert);
		
		SMimeMessage smime = new SMimeMessage(message.getBodyPart());
		
		Assert.assertFalse("Message should be signed", smime.isSigned());
		Assert.assertTrue("Message should be unencrypted", smime.isEncrypted());
		
		smime = smime.decrypt();
	}
	
	@Test
	public void testSignEncryptMetaMessge() throws Exception {
		SFRMMessage message = SFRMMessageFactory.getInstance().
			createHandshakingRequest(messageId, partnershipId, totalSegment, totalSize, filename);
		message.setContent(messageContent, contentType);

		Method m = SFRMPartnershipDSDVO.class.getDeclaredMethod("getX509Certificate", File.class);
		m.setAccessible(true);
		X509Certificate cert = (X509Certificate)m.invoke(new SFRMPartnershipDSDVO(), 
				new File(FIXTURE_LOADER.getResource(publicCertMD5).getFile()));
		
		outHandler.packOutgoingMessage(message, "md5", "3des", cert);

		KeyStoreManager keyman = outHandler.getKeyStoreManager();
		SMimeMessage smime = new SMimeMessage(
				message.getBodyPart(),
				keyman.getX509Certificate(), 
				keyman.getPrivateKey());
		
		// signature is also encrypted
		Assert.assertTrue("Message should be unencrypted", smime.isEncrypted());
		
		try {
			smime = smime.decrypt();
			
			Assert.assertTrue("Message should be signed", smime.isSigned());
			smime = smime.verify();
		} catch (SMimeException sme) {
			Assert.fail("Signature vertification / decryption error");
		}
		
		Assert.assertTrue("Incorrect message content", messageContent.equals(smime.getBodyPart().getContent()));
	}
	
	/**
	 * Test for sending the handshaking message 
	 * @throws ConnectionException 
	 * @throws SFRMMessageException 
	 * @throws Exception
	 */
	@Test
	public void testSendMetaMessage() throws SFRMMessageException, ConnectionException {
		SFRMMessage message = SFRMMessageFactory.getInstance().
			createHandshakingRequest(messageId, partnershipId, totalSegment, totalSize, filename);
		message.setContent(messageContent, contentType);
		
		mock = new SimpleHttpMonitor(mockPort);
		mock.start();
		try {
			outHandler.sendMessage(message, mockEndpoint, false, null, null, null);
			
			Map headers = mock.getHeaders();
			Assert.assertEquals("Message-Id should be " + messageId, messageId, (String)headers.get(SFRMHeader.MESSAGE_ID));
			Assert.assertEquals("Partnership-Id should be " + partnershipId, partnershipId, (String)headers.get(SFRMHeader.SFRM_PARTNERSHIP));
			Assert.assertEquals("Segment-Type should be META", SFRMConstant.MSGT_META, headers.get(SFRMHeader.SFRM_SEGMENT_TYPE));
			//TODO: To see why the assertion fail on the following		
			Assert.assertEquals("Segment No should be 0", 0, Integer.parseInt((String)headers.get(SFRMHeader.SFRM_SEGMENT_NO)));
			Assert.assertEquals("Total number of segment should be " + totalSegment, totalSegment, Integer.parseInt((String)headers.get(SFRMHeader.SFRM_META_TOTAL_SEGMENT)));
			Assert.assertEquals("Total size of segment should be " + totalSize, totalSize, Long.parseLong((String)headers.get(SFRMHeader.SFRM_TOTAL_SIZE)));
			Assert.assertEquals("Content-Type not match", contentType, (String)headers.get("Content-Type"));
			Assert.assertEquals("Filename should be " + filename, filename, headers.get(SFRMHeader.SFRM_FILENAME));
		} finally {
			mock.stop();
		}
	}
	
	/**
	 * Test for sending the handshaking message 
	 * @throws SFRMMessageException 
	 * @throws SFRMException 
	 * @throws ConnectionException 
	 * @throws SFRMMessageException 
	 * @throws Exception
	 */
    @Test(expected=ConnectionException.class)
	public void testOutgoingMetaMessageFail() throws SFRMMessageException, ConnectionException {
		SFRMMessage message = SFRMMessageFactory.getInstance().
			createHandshakingRequest(messageId, partnershipId, totalSegment, totalSize, filename);
		message.setContent(messageContent, contentType);
    	
		//Setting the mock http server		
		mock = new SimpleHttpMonitor(mockPort) {
			protected void onResponse(final OutputStream out) throws IOException{
				out.write(("HTTP/1.1 400 Bad Request" + CRLF).getBytes());
				out.write(("Server: SFRM Mock Server" + CRLF).getBytes());
				out.write(("Content-Length: 0" + CRLF).getBytes());
				out.write(("Content-Type: text/plain" + CRLF + CRLF).getBytes());
			}
		};

		mock.start();
		try {
			outHandler.sendMessage(message, mockEndpoint, false, null, null, null);
		} finally {
			mock.stop();
		}
	}
	
}
