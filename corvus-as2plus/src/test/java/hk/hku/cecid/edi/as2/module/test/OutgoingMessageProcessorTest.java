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
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Iterator;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.junit.Test;
import junit.framework.Assert;

import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.bc.BcRSASignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.ZlibExpanderProvider;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.mail.smime.SMIMECompressed;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.bouncycastle.mail.smime.SMIMESigned;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.util.encoders.Base64;

import hk.hku.cecid.edi.as2.dao.MessageDAO;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.edi.as2.dao.PartnershipDAO;
import hk.hku.cecid.edi.as2.dao.PartnershipDVO;
import hk.hku.cecid.edi.as2.module.OutgoingMessageProcessor;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.piazza.commons.activation.InputStreamDataSource;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;
import hk.hku.cecid.piazza.commons.test.SystemComponentTest;

/**
 * Unit Test OutgoingMessageProcessor
 * 
 * @author Jumbo Cheung
 *
 */
public class OutgoingMessageProcessorTest extends SystemComponentTest<OutgoingMessageProcessor>{

	PartnershipDAO partnershipDAO;
	PartnershipDVO partnershipDVO;
	
	static final String CREATE_TABLE_SQL = "create.sql";
	static final String DROP_TABLE_SQL = "drop.sql";
	
	private static String MOCK_AS2_MSG = "mock.as2";

        private static final String SECURITY_PROVIDER = "BC"; 
	
	@Override
	public String getSystemComponentId() {
		return "outgoing-message-processor";
	}
	
	@Override
	public void setUp() throws Exception {
		commitSQL(MessageDAO.class, CREATE_TABLE_SQL);
		
		//Prepare the Partnership DVO
		partnershipDAO = (PartnershipDAO) TARGET.getDAOFactory().createDAO(PartnershipDAO.class);
		partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
		partnershipDVO.setPartnershipId("OutgoingMessageProcessorTest_P1");
		partnershipDVO.setIsDisabled(false);
		partnershipDVO.setAs2From("as2Form");
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
		
		//Seting Mail Cap
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
	}
	
	@Override
	public void tearDown() throws Exception {
		commitSQL(MessageDAO.class, DROP_TABLE_SQL);
	}
	
	@Test
 	public void testPlainMessageMIC() throws Exception{
		//Prepare Data
		InputStream ins = FIXTURE_LOADER.getResourceAsStream(MOCK_AS2_MSG);
		ByteArrayInputStream bIns = new ByteArrayInputStream(IOHandler.readBytes(ins));
	
		partnershipDVO.setIsReceiptSignRequired(true);
		partnershipDVO.setIsReceiptRequired(true);
		
		String mid = RANDOM.toString();
		AS2Message as2Msg = TARGET.storeOutgoingMessage(
				mid, //MessageID
				"xml", 
				partnershipDVO,
				new InputStreamDataSource(bIns, "xml", MOCK_AS2_MSG),
				null);
		
		//Verify MIC value
		ins = FIXTURE_LOADER.getResourceAsStream(MOCK_AS2_MSG);
		bIns = new ByteArrayInputStream(IOHandler.readBytes(ins));
		byte[] content = new byte[bIns.available()];
		bIns.read(content);
		bIns.close();
		String mic = calculateMIC(content);
		
		MessageDVO msgDVO = getStoredMessage(mid);
		Assert.assertEquals( "MIC value is not valid", mic, msgDVO.getMicValue());
	}
	
	@Test
	public void testSignedAS2Message() throws Exception{
		InputStream ins = FIXTURE_LOADER.getResourceAsStream(MOCK_AS2_MSG);
		ByteArrayInputStream bIns = new ByteArrayInputStream(IOHandler.readBytes(ins));
	
		partnershipDVO.setIsOutboundSignRequired(true);
		String mid = RANDOM.toString();
		
		AS2Message as2Msg = TARGET.storeOutgoingMessage(
				mid, //MessageID
				"xml", 
				partnershipDVO,
				new InputStreamDataSource(bIns, "xml", MOCK_AS2_MSG));
		
		//Verify As2Signing Message
		try{
			SMIMESigned signed = new SMIMESigned((MimeMultipart)as2Msg.getBodyPart().getContent());
			SignerInformationStore  signers = signed.getSignerInfos();
			Iterator signerInfos = signers.getSigners().iterator();
			while (signerInfos.hasNext()) {
				SignerInformation   signerInfo = (SignerInformation)signerInfos.next();

				SignerInformationVerifier verifier =
				    new BcRSASignerInfoVerifierBuilder(new DefaultCMSSignatureAlgorithmNameGenerator(),
								       new DefaultSignatureAlgorithmIdentifierFinder(),
								       new DefaultDigestAlgorithmIdentifierFinder(), 
								       new BcDigestCalculatorProvider())
				    .build(new JcaX509CertificateHolder(partnershipDVO.getEffectiveVerifyCertificate()));
				
				if (!signerInfo.verify(verifier)) {
					Assert.fail("Signature Verfifcation Failed");
				}
			}
			
			//Assert the filename value
			MimeBodyPart signedPart = signed.getContent();
	        String filenameHdr = signedPart.getHeader("Content-Disposition")[0];
	        Assert.assertEquals("Lost Filename Header Information", MOCK_AS2_MSG, getFileName(filenameHdr));
	        
	        
	        // Verify MIC Value
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        signedPart.writeTo(baos);
            byte[] content = (baos.toByteArray());
            String mic = calculateMIC(content);
            
            MessageDVO msgDVO = getStoredMessage(mid);
            Assert.assertEquals("MIC Value is not valid.", mic, msgDVO.getMicValue());
	        
		}catch(Exception exp){
			Assert.fail("Signature Verfifcation Failed");
		}
        Assert.assertTrue(true);
 	}
	
	@Test
	public void testCompressAS2Message() throws Exception{
		InputStream ins = FIXTURE_LOADER.getResourceAsStream(MOCK_AS2_MSG);
		ByteArrayInputStream bIns = new ByteArrayInputStream(IOHandler.readBytes(ins));
		partnershipDVO.setIsOutboundCompressRequired(true);
		String mid = RANDOM.toString();
		
		AS2Message as2Msg = TARGET.storeOutgoingMessage(
				mid, //MessageID
				"xml", 
				partnershipDVO,
				new InputStreamDataSource(bIns, "xml", MOCK_AS2_MSG));
		
		SMIMECompressed compressed = new SMIMECompressed(as2Msg.getBodyPart());
		MimeBodyPart decompressedPart = SMIMEUtil.toMimeBodyPart(compressed.getContent(new ZlibExpanderProvider()));

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOHandler.pipe( decompressedPart.getDataHandler().getInputStream(), baos);
		byte[] decrptedBA = baos.toByteArray();
		byte[] original = IOHandler.readBytes(FIXTURE_LOADER.getResourceAsStream(MOCK_AS2_MSG));
		
		Assert.assertTrue(Arrays.equals(decrptedBA, original));		
		//TODO
	   String filenameHdr = decompressedPart.getHeader("Content-Disposition")[0];
	   Assert.assertEquals("Filename value lost in BodyPart Header", 
			   MOCK_AS2_MSG, getFileName(filenameHdr));
	   
	   
	   // Verify MIC Value
       ByteArrayOutputStream contentBAOS = new ByteArrayOutputStream();
       decompressedPart.writeTo(contentBAOS);
       byte[] content = (contentBAOS.toByteArray());
       String mic = calculateMIC(content);
       Assert.assertEquals( "MIC Value is not valid.",
    		   mic, getStoredMessage(mid).getMicValue());
      
	}
	
	@Test
	public void testEncrytedAS2Message() throws Exception{
	    InputStream ins = FIXTURE_LOADER.getResourceAsStream(MOCK_AS2_MSG);
	    ByteArrayInputStream bIns = new ByteArrayInputStream(IOHandler.readBytes(ins));
	    String mid = RANDOM.toString();
	    
	    partnershipDVO.setIsOutboundEncryptRequired(true);
	    AS2Message as2Msg = TARGET.storeOutgoingMessage(
							    mid, //MessageID
							    "xml", 
							    partnershipDVO,
							    new InputStreamDataSource(bIns, "xml", MOCK_AS2_MSG));
		
		
	    // Decrypt Message
	    SMIMEEnveloped crypted = new SMIMEEnveloped(as2Msg.getBodyPart());
	    
	    // RecipientId recId = new RecipientId();
	    RecipientId recId = new JceKeyTransRecipientId(partnershipDVO.getEncryptX509Certificate());

	    RecipientInformationStore  recipientsInfo = crypted.getRecipientInfos();	
	    RecipientInformation       recipientInfo = recipientsInfo.get(recId);

	    KeyStoreManager keyMan = (KeyStoreManager)TARGET.getSystemModule().getComponent("keystore-manager");

	    JceKeyTransEnvelopedRecipient recipient = new JceKeyTransEnvelopedRecipient(keyMan.getPrivateKey());	
	    recipient.setProvider(SECURITY_PROVIDER);							
	    
	    MimeBodyPart  decrpted = SMIMEUtil.toMimeBodyPart(recipientInfo.getContent(recipient));
        
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    IOHandler.pipe( decrpted.getDataHandler().getInputStream(), baos);
	    byte[] decrptedBA = baos.toByteArray();
	    byte[] originalBA = IOHandler.readBytes(FIXTURE_LOADER.getResourceAsStream(MOCK_AS2_MSG));
        
	    Assert.assertTrue(Arrays.equals(decrptedBA, originalBA));
        
	    //Assert the filename
	    String filenameHdr = decrpted.getHeader("Content-Disposition")[0];
	    Assert.assertEquals("Filename value lost in BodyPartHeader",
				MOCK_AS2_MSG, getFileName(filenameHdr));

	    //Verify MIC
	    ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
	    decrpted.writeTo(contentStream);
	    byte[] content = (contentStream.toByteArray());
	    String mic = calculateMIC(content);
	    Assert.assertEquals( "MIC Value is not valid.", mic, getStoredMessage(mid).getMicValue());
	}
	
	@Test
	public void testSignedEncryptedAS2Message() throws Exception {
	    InputStream ins = FIXTURE_LOADER.getResourceAsStream(MOCK_AS2_MSG);
	    ByteArrayInputStream bIns = new ByteArrayInputStream(IOHandler.readBytes(ins));
		
	    // Prepare Data
	    String mid = RANDOM.toString();
	    partnershipDVO.setIsOutboundEncryptRequired(true);
	    partnershipDVO.setIsOutboundSignRequired(true);
	    //Encrypt message
	    AS2Message as2Msg = TARGET.storeOutgoingMessage(
							    mid, //MessageID
							    "xml", 
							    partnershipDVO,
							    new InputStreamDataSource(bIns, "xml", MOCK_AS2_MSG));
		
	    // Decrypt Message
	    SMIMEEnveloped crypted = new SMIMEEnveloped(as2Msg.getBodyPart());

	    RecipientId recId = new JceKeyTransRecipientId(partnershipDVO.getEncryptX509Certificate());
	    
	    RecipientInformationStore  recipientsInfo = crypted.getRecipientInfos();	
	    RecipientInformation       recipientInfo = recipientsInfo.get(recId);

	    KeyStoreManager keyMan = (KeyStoreManager)TARGET.getSystemModule().getComponent("keystore-manager");

	    JceKeyTransEnvelopedRecipient recipient = new JceKeyTransEnvelopedRecipient(keyMan.getPrivateKey());	
	    recipient.setProvider(SECURITY_PROVIDER);							
	    
	    MimeBodyPart  decrpted = SMIMEUtil.toMimeBodyPart(recipientInfo.getContent(recipient));
		
	    //Verify Signature
	    try{
		SMIMESigned signed = new SMIMESigned((MimeMultipart)decrpted.getContent());
		SignerInformationStore  signers = signed.getSignerInfos();
		Iterator signerInfos = signers.getSigners().iterator();

		while (signerInfos.hasNext()) {
		    SignerInformation   signerInfo = (SignerInformation)signerInfos.next();

		    SignerInformationVerifier verifier =
			new BcRSASignerInfoVerifierBuilder(new DefaultCMSSignatureAlgorithmNameGenerator(),
							   new DefaultSignatureAlgorithmIdentifierFinder(),
							   new DefaultDigestAlgorithmIdentifierFinder(), 
							   new BcDigestCalculatorProvider())
			.build(new JcaX509CertificateHolder(partnershipDVO.getEffectiveVerifyCertificate()));
		    if (!signerInfo.verify(verifier)) {
			Assert.fail("Signature Verfifcation Failed");
		    }
		}

		
			
		//Assert the filename value
		MimeBodyPart signedPart = signed.getContent();
	        String filenameHdr = signedPart.getHeader("Content-Disposition")[0];
	        Assert.assertEquals("Lost Filename Header Information", MOCK_AS2_MSG, getFileName(filenameHdr));
	        
	        
	        // Verify MIC Value
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        signedPart.writeTo(baos);
		byte[] content = (baos.toByteArray());
		String mic = calculateMIC(content);
            
		MessageDVO msgDVO = getStoredMessage(mid);
		Assert.assertEquals("MIC Value is not valid.", mic, msgDVO.getMicValue());
	        
	    }catch(Exception exp){
		Assert.fail("Signature Verfifcation Failed");
	    }
	    Assert.assertTrue(true);
	}
	
	@Test
	public void testSignedCommpressMessage() throws Exception{
		InputStream ins = FIXTURE_LOADER.getResourceAsStream(MOCK_AS2_MSG);
		ByteArrayInputStream bIns = new ByteArrayInputStream(IOHandler.readBytes(ins));
		
		// Prepare Data
		String mid = RANDOM.toString();
		partnershipDVO.setIsOutboundSignRequired(true);
		partnershipDVO.setIsOutboundCompressRequired(true);
		//Process message
		AS2Message as2Msg = TARGET.storeOutgoingMessage(
				mid, //MessageID
				"xml", 
				partnershipDVO,
				new InputStreamDataSource(bIns, "xml", MOCK_AS2_MSG));
        
        try{
        	//Verify Message Signature
			SMIMESigned signed = new SMIMESigned((MimeMultipart)as2Msg.getBodyPart().getContent());
			SignerInformationStore  signers = signed.getSignerInfos();
			Iterator signerInfos = signers.getSigners().iterator();
			while (signerInfos.hasNext()) {
				SignerInformation   signerInfo = (SignerInformation)signerInfos.next();

				SignerInformationVerifier verifier =
				    new BcRSASignerInfoVerifierBuilder(new DefaultCMSSignatureAlgorithmNameGenerator(),
								       new DefaultSignatureAlgorithmIdentifierFinder(),
								       new DefaultDigestAlgorithmIdentifierFinder(), 
								       new BcDigestCalculatorProvider())
				    .build(new JcaX509CertificateHolder(partnershipDVO.getEffectiveVerifyCertificate()));
				
				if (!signerInfo.verify(verifier)) {
					Assert.fail("Signature Verfifcation Failed");
				}
			}
			
			// Verify MIC Value
			MimeBodyPart signedPart = signed.getContent();
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        signedPart.writeTo(baos);
	        byte[] content = (baos.toByteArray());
	        String mic = calculateMIC(content);
	        MessageDVO msgDVO = getStoredMessage(mid);
	        Assert.assertEquals("MIC Value is not valid.", mic, msgDVO.getMicValue());
			
			//Decompress Message
			SMIMECompressed compressed = new SMIMECompressed(signedPart);
			MimeBodyPart decompressedPart = SMIMEUtil.toMimeBodyPart(compressed.getContent(new ZlibExpanderProvider()));
			
			//Assert the filename value
	        String filenameHdr = decompressedPart.getHeader("Content-Disposition")[0];
	        Assert.assertEquals("Lost Filename Header Information", MOCK_AS2_MSG, getFileName(filenameHdr));
	        
		}catch(Exception exp){
			Assert.fail("Signature Verfifcation Failed");
		}
		
	}

	private MessageDVO getStoredMessage(String msgId) throws DAOException{
		MessageDAO msgDAO = (MessageDAO) TARGET.getDAOFactory().createDAO(MessageDAO.class);
		MessageDVO msgDVO = (MessageDVO) msgDAO.createDVO();
		msgDVO.setMessageId(msgId);
		msgDVO.setMessageBox(MessageDVO.MSGBOX_OUT);
		msgDAO.retrieve(msgDVO);
		return msgDVO;
	}

	private String getFileName (String value){
		String filename = null;
		String[] tokens = value.split(";");
		if(tokens!= null && tokens.length > 1 &&
				tokens[0].trim().equalsIgnoreCase("attachment")){
			for(int index =1; index < tokens.length; index++){
				if(tokens[index].trim().startsWith("filename")){
					filename = tokens[index].substring(tokens[index].indexOf("=") +1);
					if(filename.trim().length() == 0){
						filename = null;
						continue;
					}
					break;
				}
			}
		}
		return filename;
	}
	
	private String calculateMIC(byte[] content) throws Exception{
		String digestAlg =
			(partnershipDVO.getMicAlgorithm() == null?PartnershipDVO.ALG_MIC_SHA1:partnershipDVO.getMicAlgorithm());
		MessageDigest md = MessageDigest.getInstance(digestAlg, "BC");
		md.update(content);
		
		 byte[] digest = md.digest();
         String digestString = new String(Base64.encode(digest));
         return digestString + ", " + partnershipDVO.getMicAlgorithm();
	}
	
    /**
     * Canonicalizes the given data by removing the starting new lines.
     * 
     * @param data the data to be canonicalized.
     * @return the canonicalized data
     */
    private byte[] canonicalize(byte[] data) {
        if (data == null) {
            data = new byte[]{};
        }
        
        int pos = 0;
        for (int i=0; i+1<data.length; i+=2) {
            if (data[i] == '\r' && data[i+1] == '\n') {
                pos += 2;
            }
            else {
                break;
            }
        }
        
        byte[] canonicalized = new byte[(data.length - pos + 1)];
        for(int i =0;pos <= data.length;pos++, i++){
        	canonicalized[i] = data[pos];
        }
        
        return canonicalized;
    }
}
