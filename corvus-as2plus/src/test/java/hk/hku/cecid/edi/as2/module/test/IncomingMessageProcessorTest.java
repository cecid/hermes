/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.module.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import junit.framework.Assert;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.asn1.smime.SMIMEEncryptionKeyPreferenceAttribute;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Test;

import hk.hku.cecid.edi.as2.dao.AS2DAOHandler;
import hk.hku.cecid.edi.as2.dao.MessageDAO;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.edi.as2.dao.PartnershipDAO;
import hk.hku.cecid.edi.as2.dao.PartnershipDVO;
import hk.hku.cecid.edi.as2.dao.RepositoryDVO;
import hk.hku.cecid.edi.as2.module.IncomingMessageProcessor;
import hk.hku.cecid.edi.as2.pkg.AS2Header;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.edi.as2.pkg.DispositionNotification;
import hk.hku.cecid.edi.as2.pkg.DispositionNotificationOption;
import hk.hku.cecid.edi.as2.pkg.DispositionNotificationOptions;
import hk.hku.cecid.piazza.commons.activation.InputStreamDataSource;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;
import hk.hku.cecid.piazza.commons.security.SMimeMessage;
import hk.hku.cecid.piazza.commons.test.SystemComponentTest;

/**
 * Unit Test IncomingMessageProcessor
 * 
 * @author Jumbo Cheung
 *
 */

public class IncomingMessageProcessorTest extends SystemComponentTest<IncomingMessageProcessor> {

	// Testing Resources
	private static final String CREATE_TABLE_SQL = "create.sql";
	private static final String DROP_TABLE_SQL = "drop.sql";
	private static final String MOCK_AS2_MSG = "mock.as2";
	private static final String COMPONENT_KEYSTORE_MANAGER = "keystore-manager";
	
	// Variables
	private PartnershipDVO partnershipDVO;
	private AS2Message as2Message;
	private String msgId;
	private KeyStoreManager keyMan;

        // Constants
        private static final String SECURITY_PROVIDER = "BC";
        public static final String DIGEST_ALG_SHA1 = OIWObjectIdentifiers.sha1WithRSA.getId();
	
	@Override
	public String getSystemComponentId() {
		return "incoming-message-processor";
	}


//	@Before
	@Override
	public void setUp() throws Exception {
		commitSQL(MessageDAO.class, CREATE_TABLE_SQL);
		LOG.debug("Set up");
		
		//Setting Mail Cap
		MailcapCommandMap mailcaps = new MailcapCommandMap();
		mailcaps.addMailcap("application/pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_signature");
		mailcaps.addMailcap("application/pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_mime");
		mailcaps.addMailcap("application/x-pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_signature"); 
		mailcaps.addMailcap("application/x-pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_mime");
		mailcaps.addMailcap("multipart/signed;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.multipart_signed");
		mailcaps.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
	
		mailcaps.addMailcap("application/deflate;; x-java-content-handler=hk.hku.cecid.piazza.commons.activation.ByteStreamDataContentHandler");
		mailcaps.addMailcap("message/disposition-notification;; x-java-content-handler=hk.hku.cecid.piazza.commons.activation.ByteStreamDataContentHandler");
		mailcaps.addMailcap("application/EDI-X12;; x-java-content-handler=hk.hku.cecid.piazza.commons.activation.ByteStreamDataContentHandler");
		mailcaps.addMailcap("application/EDIFACT;; x-java-content-handler=hk.hku.cecid.piazza.commons.activation.ByteStreamDataContentHandler");
		mailcaps.addMailcap("application/edi-consent;; x-java-content-handler=hk.hku.cecid.piazza.commons.activation.ByteStreamDataContentHandler");
		mailcaps.addMailcap("application/XML;; x-java-content-handler=hk.hku.cecid.piazza.commons.activation.ByteStreamDataContentHandler");
		mailcaps.addMailcap("application/octet-stream;; x-java-content-handler=hk.hku.cecid.piazza.commons.activation.ByteStreamDataContentHandler");
		CommandMap.setDefaultCommandMap(mailcaps);
		
		//Prepare the Partnership DVO
		PartnershipDAO partnershipDAO = (PartnershipDAO) TARGET.getDAOFactory().createDAO(PartnershipDAO.class);
		partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
		partnershipDVO.setIsDisabled(false);
		partnershipDVO.setPartnershipId("IncomingMessageProcessorTest");
		partnershipDVO.setAs2From("as2From");
		partnershipDVO.setAs2To("as2To");
		partnershipDVO.setSubject("OutgoingMessageProcessor Unit Test");
		partnershipDVO.setIsSyncReply(false);
		partnershipDVO.setReceiptAddress("http://127.0.0.1:8080/corvus/httpd/as2/inbound");
		partnershipDVO.setRecipientAddress("http://127.0.0.1:8080/corvus/httpd/as2/inbound");
		partnershipDVO.setIsReceiptRequired(false);

		partnershipDVO.setIsReceiptSignRequired(true);
		partnershipDVO.setIsInboundEncryptRequired(false);
		partnershipDVO.setIsInboundSignRequired(false);
		
		partnershipDVO.setIsOutboundCompressRequired(false);
		partnershipDVO.setIsOutboundEncryptRequired(false);
		partnershipDVO.setIsOutboundSignRequired(false);
		
		partnershipDVO.setSignAlgorithm(PartnershipDVO.ALG_SIGN_SHA1);
		partnershipDVO.setEncryptAlgorithm(PartnershipDVO.ALG_ENCRYPT_3DES);
		partnershipDVO.setMicAlgorithm(PartnershipDVO.ALG_MIC_SHA1);
		
		partnershipDVO.setVerifyCert(IOHandler.readBytes(FIXTURE_LOADER.getResourceAsStream("security/corvus.cer")));
		partnershipDVO.setEncryptCert(IOHandler.readBytes(FIXTURE_LOADER.getResourceAsStream("security/corvus.cer")));
		partnershipDAO.create(partnershipDVO);
		
		
		//Initialise AS2 Message
		msgId = RANDOM.toString();
		AS2Message as2Msg = new AS2Message();
		as2Msg.setFromPartyID("as2To");
		as2Msg.setToPartyID("as2From");
		as2Msg.setMessageID(msgId);
		as2Msg.setHeader(AS2Header.SUBJECT, partnershipDVO.getSubject());
		as2Msg.setHeader(AS2Header.RECEIPT_DELIVERY_OPTION, partnershipDVO.getRecipientAddress());
		as2Msg.setHeader(AS2Header.DISPOSITION_NOTIFICATION_TO, partnershipDVO.getReceiptAddress());
	
		  DispositionNotificationOptions dnos = new DispositionNotificationOptions();
          DispositionNotificationOption option = dnos.addOption(DispositionNotificationOptions.SIGNED_RECEIPT_PROTOCOL);
          option.addValue(DispositionNotificationOption.SIGNED_RECEIPT_PROTOCOL_PKCS7);
          option = dnos.addOption(DispositionNotificationOptions.SIGNED_RECEIPT_MICALG);
          option.addValue(SMIMESignedGenerator.DIGEST_SHA1);
          as2Msg.setHeader(AS2Header.DISPOSITION_NOTIFICATION_OPTIONS, option.toString());
		
		// Set Content to Message
		InputStream ins = FIXTURE_LOADER.getResourceAsStream(MOCK_AS2_MSG);
		ByteArrayInputStream bIns = new ByteArrayInputStream(IOHandler.readBytes(ins));
		as2Msg.setContent(new InputStreamDataSource(bIns, "xml", MOCK_AS2_MSG), "application/XML");
		as2Message = as2Msg;
	
		// Initilaize Keystore-Manager
		keyMan = (KeyStoreManager)TARGET.getComponent(COMPONENT_KEYSTORE_MANAGER);
	}
	

//	@After
	@Override
	public void tearDown() throws Exception {
		LOG.debug("Tear down");
		commitSQL(MessageDAO.class, DROP_TABLE_SQL);
	}

	@Test
	public void testSignatureVerfication() {
	    System.out.println("testname: testSignatureVerification");
		try {

			PartnershipDAO partnershipDAO = (PartnershipDAO) TARGET
					.getDAOFactory().createDAO(PartnershipDAO.class);
			PartnershipDVO dvo = (PartnershipDVO) partnershipDAO.createDVO();
			dvo.setPartnershipId("IncomingMessageProcessorTest");
			partnershipDAO.retrieve(dvo);
			Assert.assertEquals("as2From", dvo.getAS2From());
			PartnershipDVO pDvo = (PartnershipDVO) (partnershipDAO.findByParty(
					"as2From", "as2To"));

			// Caculate MIC value
			String expectedMIC = calculateMIC(as2Message.getBodyPart());

			// Sign message
			/*
			 * MimeBodyPart signedPart = signMessage(as2Message.getBodyPart());
			 * as2Message.setBodyPart(encryptedPart);
			 */
			SMimeMessage smime = new SMimeMessage(as2Message.getBodyPart(),
					partnershipDVO.getVerifyX509Certificate(), keyMan
							.getPrivateKey());
			smime.setContentTransferEncoding(SMimeMessage.CONTENT_TRANSFER_ENC_BINARY);
			smime.setDigestAlgorithm(SMimeMessage.DIGEST_ALG_SHA1);
			smime = smime.sign();
			as2Message.setBodyPart(smime.getBodyPart());

			partnershipDVO.setIsInboundSignRequired(true);
			PartnershipDAO partnershioDAO = (PartnershipDAO) TARGET
					.getDAOFactory().createDAO(PartnershipDAO.class);
			partnershioDAO.persist(partnershipDVO);

			// Insert Message Record to Database
			AS2DAOHandler daoHandler = new AS2DAOHandler(TARGET.getDAOFactory());
			RepositoryDVO requestRepositoryDVO = daoHandler
					.createRepositoryDVO(as2Message, true);
			MessageDVO requestMessageDVO = daoHandler.createMessageDVO(
					as2Message, true);
			requestMessageDVO.setStatus(MessageDVO.STATUS_RECEIVED);
			daoHandler.createMessageStore().storeMessage(requestMessageDVO,
					requestRepositoryDVO);

			// Invoke Target Method
			Method m = TARGET.getClass().getDeclaredMethod(
					"processReceivedMessage", AS2Message.class);
			m.setAccessible(true);
			AS2Message responseMsg = (AS2Message) m.invoke(TARGET, as2Message);

			// check if the response message has been generated
			Assert.assertNotNull(responseMsg);

			// Assert the content of the reply
			DispositionNotification dn = responseMsg
					.getDispositionNotification();
			Assert.assertEquals(msgId, dn.getOriginalMessageID());
			Assert.assertNull(dn.getDisposition().getDescription());
			Assert.assertNull(dn.getDisposition().getModifier());
			Assert.assertEquals(expectedMIC, dn.getReceivedContentMIC());
		} catch (Exception exp) {
			exp.printStackTrace();
			Assert.fail();
		}

	}
	
	@Test
	public void testDecryption() throws Exception{
	    System.out.println("testname: test Decryption");
		try{
		// Caculate MIC value
		String expectedMIC = calculateMIC(as2Message.getBodyPart());
		
		//Sign message
		as2Message.setBodyPart(encrypt(as2Message.getBodyPart()));
		
		//Update the Partnership Record in Database
		partnershipDVO.setIsInboundEncryptRequired(true);
		PartnershipDAO partnershioDAO = (PartnershipDAO) TARGET.getDAOFactory().createDAO(PartnershipDAO.class);
		partnershioDAO.persist(partnershipDVO);
		
		// Insert Message Record to Database
		AS2DAOHandler daoHandler = new AS2DAOHandler(TARGET.getDAOFactory());
        RepositoryDVO requestRepositoryDVO = daoHandler.createRepositoryDVO(as2Message, true);
        MessageDVO requestMessageDVO = daoHandler.createMessageDVO(as2Message, true);
        requestMessageDVO.setStatus(MessageDVO.STATUS_RECEIVED);
        daoHandler.createMessageStore().storeMessage(requestMessageDVO, requestRepositoryDVO);
        
		// Invoke Target Method
		Method m = TARGET.getClass().getDeclaredMethod("processReceivedMessage", AS2Message.class);
		m.setAccessible(true);
		AS2Message responseMsg = (AS2Message)m.invoke(TARGET, as2Message);
		
		// check if the response message has been generated
		Assert.assertNotNull(responseMsg);
		
		//Assert the content of the reply
		DispositionNotification dn = responseMsg.getDispositionNotification();
		Assert.assertEquals(msgId, dn.getOriginalMessageID());		
		Assert.assertNull(dn.getDisposition().getDescription());
		Assert.assertNull(dn.getDisposition().getModifier());
		Assert.assertEquals(expectedMIC, dn.getReceivedContentMIC());
		}catch(Exception exp){
			exp.printStackTrace();
			Assert.fail();
		}
		
	}
	
	@Test
	public void testDecryptionSignatureVerfication() throws Exception{
	    System.out.println("testname: testDecryptionSignatureVerification");
		try{
		// Caculate MIC value
		String expectedMIC = calculateMIC(as2Message.getBodyPart());
		
		// Update the Partnership Record
		partnershipDVO.setIsInboundSignRequired(true);
		partnershipDVO.setIsInboundEncryptRequired(true);
		PartnershipDAO partnershioDAO = (PartnershipDAO) TARGET.getDAOFactory().createDAO(PartnershipDAO.class);
		partnershioDAO.persist(partnershipDVO);
		
		//Sign message
		MimeBodyPart signedPart =  signMessage(as2Message.getBodyPart());
		MimeBodyPart encryptedPart = encrypt(signedPart);
		as2Message.setBodyPart(encryptedPart);
		
		// Insert Message Record to Database
		AS2DAOHandler daoHandler = new AS2DAOHandler(TARGET.getDAOFactory());
        RepositoryDVO requestRepositoryDVO = daoHandler.createRepositoryDVO(as2Message, true);
        MessageDVO requestMessageDVO = daoHandler.createMessageDVO(as2Message, true);
        requestMessageDVO.setStatus(MessageDVO.STATUS_RECEIVED);
        daoHandler.createMessageStore().storeMessage(requestMessageDVO, requestRepositoryDVO);
        
		// Invoke Target Method
		Method m = TARGET.getClass().getDeclaredMethod("processReceivedMessage", AS2Message.class);
		m.setAccessible(true);
		AS2Message responseMsg = (AS2Message)m.invoke(TARGET, as2Message);
		
		// check if the response message has been generated
		Assert.assertNotNull(responseMsg);
		
		//Assert the content of the reply
		DispositionNotification dn = responseMsg.getDispositionNotification();
		Assert.assertEquals(msgId, dn.getOriginalMessageID());		
		Assert.assertNull(dn.getDisposition().getDescription());
		Assert.assertNull(dn.getDisposition().getModifier());
		Assert.assertEquals(expectedMIC, dn.getReceivedContentMIC());
		}catch(Exception exp){
			exp.printStackTrace();
			Assert.fail();
		}
	}
	
	private MimeBodyPart signMessage(MimeBodyPart bodyPart) throws Exception{
		X509Certificate cert = partnershipDVO.getVerifyX509Certificate();
		
		/* Create the SMIMESignedGenerator */
        SMIMECapabilityVector capabilities = new SMIMECapabilityVector();
        capabilities.addCapability(SMIMECapability.dES_EDE3_CBC);
        capabilities.addCapability(SMIMECapability.rC2_CBC, 128);
        capabilities.addCapability(SMIMECapability.dES_CBC);

        ASN1EncodableVector attributes = new ASN1EncodableVector();
        attributes.add(new SMIMEEncryptionKeyPreferenceAttribute(
            new IssuerAndSerialNumber(new X509Name(cert.getIssuerDN().getName()), cert.getSerialNumber()))
        );
        attributes.add(new SMIMECapabilitiesAttribute(capabilities));

        SMIMESignedGenerator signer = new SMIMESignedGenerator();
        signer.setContentTransferEncoding("base64");
        // signer.addSigner(keyMan.getPrivateKey(), partnershipDVO.getVerifyX509Certificate(),
        // 		SMIMESignedGenerator.DIGEST_SHA1,
        //    new AttributeTable(attributes), null);
	signer.addSignerInfoGenerator(new JcaSimpleSignerInfoGeneratorBuilder().setProvider(SECURITY_PROVIDER)
				      .setSignedAttributeGenerator(new AttributeTable(attributes))
				      .build("SHA1withRSA",
					     keyMan.getPrivateKey(),
					     partnershipDVO.getVerifyX509Certificate()));
	
        // Add the list of certs to the generator
        ArrayList certList = new ArrayList();
        certList.add(cert);
        CertStore certs = CertStore.getInstance("Collection",
                new CollectionCertStoreParameters(certList), "BC");
        // signer.addCertificatesAndCRLs(certs);
	signer.addCertificates(new JcaCertStore(certList));

        // Sign body part
        // MimeMultipart mm = signer.generate(bodyPart, "BC");
	MimeMultipart mm = signer.generate(bodyPart);

        InternetHeaders headers = new InternetHeaders();
        boolean isContentTypeFolded = new Boolean(System.getProperty("mail.mime.foldtext","true")).booleanValue();
        headers.setHeader("Content-Type", isContentTypeFolded? mm.getContentType():mm.getContentType().replaceAll("\\s", " "));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mm.writeTo(baos);  
        MimeBodyPart signedPart = new MimeBodyPart(headers, baos.toByteArray());
        
        return signedPart;
	}
	
	private MimeBodyPart encrypt(MimeBodyPart bodyPart) throws Exception{
		 // Create Encrypter
        SMIMEEnvelopedGenerator encrypter = new SMIMEEnvelopedGenerator();
        encrypter.setContentTransferEncoding("base64");
        // encrypter.addKeyTransRecipient(partnershipDVO.getEncryptX509Certificate());
	encrypter.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(partnershipDVO.getEncryptX509Certificate())
					    .setProvider(SECURITY_PROVIDER));
		
        // Encrypt BodyPart
        // MimeBodyPart encryptedPart = encrypter.generate(bodyPart, SMIMEEnvelopedGenerator.DES_EDE3_CBC,
        // 		"BC");
	MimeBodyPart encryptedPart = encrypter.generate(bodyPart,
				       new JceCMSContentEncryptorBuilder(
					   new ASN1ObjectIdentifier(SMIMEEnvelopedGenerator.DES_EDE3_CBC))
				       .setProvider(SECURITY_PROVIDER).build());
        return encryptedPart;
	}
	
	private String calculateMIC(MimeBodyPart bodyPart) throws Exception{
		// By default, MIC calculate with Headers
		ByteArrayOutputStream contentBAOS = new ByteArrayOutputStream();
		bodyPart.writeTo(contentBAOS);
	    byte[] content = (contentBAOS.toByteArray());
	       
		MessageDigest md = MessageDigest.getInstance( SMIMESignedGenerator.DIGEST_SHA1, "BC");
		md.update(content);
		
		 byte[] digest = md.digest();
         String digestString = new String(Base64.encode(digest));
         return digestString + ", " + DispositionNotificationOption.SIGNED_RECEIPT_MICALG_SHA1;
	}
}
