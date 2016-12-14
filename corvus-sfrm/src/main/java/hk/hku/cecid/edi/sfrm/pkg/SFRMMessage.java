/**
 * Provide the core class for packing the sfrm message 
 * for messaging. 
 */
package hk.hku.cecid.edi.sfrm.pkg;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Cloneable;
import java.nio.ByteBuffer;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.bc.BcRSASignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMESigned;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;

import hk.hku.cecid.edi.sfrm.activation.FileRegionDataSource;
import hk.hku.cecid.edi.sfrm.spa.SFRMException;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

/**
 * This is the SFRM Message used for wrapping the payload and send it 
 * to the receiver side.<br><br>
 * 
 * Specification:<br>

 * Creation Date: 29/9/2006<br><br>
 * 
 * Version 1.0.2 - 
 * <ul>
 *  <li> Add a message classifier for recognizing the message 
 *       nature much easily.</li>             
 *  <li></li>
 * </ul>
 * 
 * Version 1.0.1 - 
 * <ul>
 * 	<li> MIC Value is stored into ContentMD5 in the MimeBodyPart.<li>
 * 	<li> Support cloning.</li>
 * <ul>
 * 
 * Version 2.0.0 -
 * <ul>
 * 	<li> Add Is-Packed header field</li>
 * 	<li> Add Filename header field</li>
 * </ul>
 * 
 * @author Twinsen Tsang
 * @version 1.0.2
 * @since	1.0.0
 */
public class SFRMMessage implements Cloneable {
	
	// signing and encryption algorithm
	public static final String ALG_ENCRYPT_RC2 = "rc2";
	public static final String ALG_ENCRYPT_3DES = "3des";
	public static final String ALG_SIGN_MD5 = "md5";
	public static final String ALG_SIGN_SHA1 = "sha1";
	
	private static final int BUFFER_SIZE = 8192;

        private static final String SECURITY_PROVIDER = "BC";
	
	/**
	 * The InternetHeaders used for managing RFC822 style headers.
	 */
	private InternetHeaders headers;

	/**
	 * The content MIME part.
	 */
	private MimeBodyPart bodyPart;
	
	/**
	 * The message classifier for this message.
	 */
	private transient SFRMMessageClassifier mc;
	
	/**
	 * The current version of SFRM Message.
	 */
	public static final String SFRM_MESSAGE_VERSION = "1.5.0";
	
	// Set DataContentHandler for SFRM MIME part getInputStream()
	static {
        MailcapCommandMap mailcap = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mailcap.addMailcap("application/octet-stream;; x-java-content-handler=hk.hku.cecid.piazza.commons.activation.SFRMDataContentHandler");
        CommandMap.setDefaultCommandMap(mailcap);
	}
	
	/**
	 * Creates a new instance of SFRM Message.
	 */
	public SFRMMessage() {
		this.headers  = new InternetHeaders();
		this.bodyPart = new MimeBodyPart();		  
		setHeader(SFRMHeader.SFRM_VERSION, SFRM_MESSAGE_VERSION);
	}

	/**
	 * Creates a new instance of SFRM Message.
	 * 
	 * @param message 
	 * 			the message as input stream.
	 * @throws SFRMMessageException 
	 * 			if unable to construct from the given input stream.
	 */
	public SFRMMessage(InputStream message) throws SFRMMessageException {
		try {
			BufferedInputStream bis = new BufferedInputStream(message);
			this.load(new InternetHeaders(bis), bis);
			bis.close();
		} catch (Exception e) {
			throw new SFRMMessageException(
				"Unable to construct SFRM message from input stream", e);
		}
	}

	/**
	 * Creates a new instance of SFRMMessage.
	 * 
	 * @param headers 
	 * 				the headers of this message.
	 * @param ins 
	 * 				the content stream.
	 * @throws SFRMMessageException 
	 * 				if unable to construct from the given content stream.
	 */
	public SFRMMessage(InternetHeaders headers, InputStream content)
			throws SFRMMessageException {
		try {
			load(headers, content);
		} catch (Exception e) {
			throw new SFRMMessageException(
					"Unable to construct SFRM message from content stream", e);
		}
	}

	/**
	 * Loads the given headers and content to this message.
	 * 
	 * @param headers
	 *            the message headers.
	 * @param content
	 *            the message content.
	 * @throws MessagingException
	 *             if unable to construct the mime body part.
	 * @throws IOException
	 *             unable to read the content stream.
	 */
	private void load(InternetHeaders headers, InputStream content)
			throws MessagingException, IOException {
		InternetHeaders bodyHeaders = new InternetHeaders();
		copyHeaders(headers, bodyHeaders, "(?i)(?s)content-.*", true);
		
		this.headers = headers;
		// FIXME: why must use IOHandler here.
		this.bodyPart = new MimeBodyPart(bodyHeaders, IOHandler.readBytes(content));
	}

	/**
	 * Set the message id of this message.
	 * 
	 * @param messageId 
	 * 			The message id of this message.
	 */
	public void setMessageID(String messageId) {
		if (messageId != null)
			setHeader(SFRMHeader.MESSAGE_ID, messageId);
	}
	
	/**
	 * Gets the message ID.
	 * 
	 * @return the message ID.
	 */
	public String getMessageID() {
		return getHeader(SFRMHeader.MESSAGE_ID);
	}

	/**
	 * @return the SFRM Version from the message.
	 */
	public String getVersion(){
		return this.getHeader(SFRMHeader.SFRM_VERSION);
	}
	
	/**
	 * Set the conversation id of this message. 
	 * 
	 * @param conversationId
	 * 			The conversation id.
	 */
	public void setConversationId(String conversationId){
		if (conversationId != null && !conversationId.equalsIgnoreCase(""))
			this.setHeader(SFRMHeader.SFRM_CONVERATION, conversationId);
	}
	
	/**
	 * @return the SFRM conversation id from the message.
	 */
	public String getConverationId(){
		return this.getHeader(SFRMHeader.SFRM_CONVERATION);
	}
	
	/**
	 * Set the partnership id of the message. 
	 */
	public void setPartnershipId(String partnershipId){
		if (partnershipId != null && !partnershipId.equalsIgnoreCase(""))
			this.setHeader(SFRMHeader.SFRM_PARTNERSHIP, partnershipId);
	}
	
	/**
	 * @return the SFRM partnership id from the message.
	 */
	public String getPartnershipId(){
		return this.getHeader(SFRMHeader.SFRM_PARTNERSHIP);
	}
	
	/**
	 * Set the segment number of the message.<br><br>
	 * 
	 * The segment number will only be set when it is greater than or 
	 * equal to zero.
	 * 
	 * @param segmentNo
	 * 			The segment number of message.
	 */
	public void setSegmentNo(int segmentNo){
		if (segmentNo >= 0)
			this.setHeader(SFRMHeader.SFRM_SEGMENT_NO, String
					.valueOf(segmentNo));
	}
	
	/**
	 * @return the SFRM segmet no from the message.
	 */
	public int getSegmentNo(){
		return StringUtilities.parseInt(this
				.getHeader(SFRMHeader.SFRM_SEGMENT_NO));
	}
	
	/**
	 * Set the segment type of the message.<br><br>
	 * 
	 * It can only be either "META", "PAYLOAD", "RECEIPT" AND "RECOVERY".
	 * 
	 * @param segmentType
	 * 			The segment type of the message.
	 */
	public void setSegmentType(String segmentType){
		if (segmentType != null && !segmentType.equalsIgnoreCase(""))
			this.setHeader(SFRMHeader.SFRM_SEGMENT_TYPE, segmentType);
	}
	
	/**
 	 * @return the SFRM segment type from the message.
	 */
	public String getSegmentType(){
		return this.getHeader(SFRMHeader.SFRM_SEGMENT_TYPE);
	}
	
	/**
	 * Set the segment offset of this message.
	 * 
	 * The segment offset will only be set when it is greater than or 
	 * equal to zero. 
	 * 
	 * @param offset
	 * 			The offset of this segment.
	 */
	public void setSegmentOffset(long offset){
		if (offset >= 0)
			this.setHeader(SFRMHeader.SFRM_SEGMENT_OFFSET, String
				.valueOf(offset));
	}
	
	/** 
	 * @return the SFRM segment offset position from the message.
	 */
	public long getSegmentOffset(){
		return StringUtilities.parseLong(this
				.getHeader(SFRMHeader.SFRM_SEGMENT_OFFSET), 0);
	}
	
	/**
	 * Set the segment payload into this message.
	 * 
	 * The segment length will only be set when it is greater than or 
	 * equal to zero. 
	 * 
	 * @param length
	 * 			The length of this segment.
	 */
	public void setSegmentLength(long length){
		if (length >= 0)
			this.setHeader(SFRMHeader.SFRM_SEGMENT_LENGTH, String
				.valueOf(length));
	}
	
	/**
	 * @return the SFRM segment length from this message.
	 */
	public long getSegmentLength(){
		return StringUtilities.parseLong(this
				.getHeader(SFRMHeader.SFRM_SEGMENT_LENGTH), 0);
	}
	
	/**
	 * Set the MIC value of the message. I.e. the checksum of this message. 
	 * 
	 * @param micValue 
	 * 			the mic value of this message. 
	 */
	public void setMicValue(String micValue) throws SFRMMessageException{
		//this.setHeader(SFRMHeader.SFRM_MIC, micValue);
		try{
			this.bodyPart.setContentMD5(micValue);
		}catch(Exception e){
			throw new SFRMMessageException("Unable to set MD5 value", e);
		}
	}
	
	/**
	 * @return the mic value of this message.
	 */
	public String getMicValue(){
		try{
			return this.bodyPart.getContentMD5();
		}catch(Exception e){
			return "";
		}
	}
	
	/**
	 * Set the total segment in the meta message.
	 * 
	 * @param totalSegment
	 * 			The total segment of the message.
	 */
	public void setTotalSegment(int totalSegment){
		this.setHeader(SFRMHeader.SFRM_META_TOTAL_SEGMENT, String
				.valueOf(totalSegment));
	}
	
	/** 
	 * @return get the total segment in the meta message. 
	 */
	public int getTotalSegment(){
		return StringUtilities.parseInt(
				this.getHeader(SFRMHeader.SFRM_META_TOTAL_SEGMENT),
				Integer.MIN_VALUE);
	}
	
	/**
	 * Set the total size in the meta message.
	 * 
	 * @param totalSize
	 * 			The total size of the message.
	 */
	public void setTotalSize(long totalSize){
		this.setHeader(SFRMHeader.SFRM_TOTAL_SIZE, String.valueOf(totalSize));
	}
	
	/**
	 * @return the total size of payload in the meta message.
	 */
	public long getTotalSize(){
		return StringUtilities.parseLong(this
				.getHeader(SFRMHeader.SFRM_TOTAL_SIZE));
	}
	
	/**
	 * Set whether the message is signed.
	 * 
	 * @param isSigned true if signed, false vice versa.  
	 */
	public void setIsSigned(boolean isSigned){
		if (this.mc == null)
			this.getClassifier();
		this.mc.setSigned(isSigned);
	}
	
	/**
	 * Set whether the message is encrypted.
	 * 
	 * @param isEncrypted true if encrypted, false vice versa.
	 */
	public void setIsEncrypted(boolean isEncrypted){
		if (this.mc == null)
			this.getClassifier();
		this.mc.setEncrypted(isEncrypted);
	}
	
	/**
	 * Set the filename of the SFRM payload
	 * @param filename the filename of the SFRM payload to be set
	 */
	public void setFilename(String filename){
		this.setHeader(SFRMHeader.SFRM_FILENAME, filename);
	}
	
	/**
	 * Get the filename of SFRM payload
	 * @return filename of the SFRM payload
	 */
	public String getFilename(){
		return this.getHeader(SFRMHeader.SFRM_FILENAME);
	}
	
	/**
	 * @return the host name of the message.
	 */
	public String getHostname(){
		return this.getHeader("Host");
	}
	
	/**
	 * @return a message classifier for this message.	 
	 */
	public SFRMMessageClassifier getClassifier(){
		synchronized(this){
			if (this.mc == null)
				this.mc = new SFRMMessageClassifier(this);
				return this.mc;
		}
	}
		
	/**
	 * Sets a content to this message.
	 * 
	 * @param content
	 * 			the content part.
	 * @param contentType 
	 * 			the content type.
	 * @throws SFRMMessageException 
	 * 			if unable to set the content.
	 */
	public void setContent(Object content, String contentType)
			throws SFRMMessageException {
		setContent(content, contentType, "binary");
	}
	
	public void setContent(Object content, String contentType, String transferEncoding)
		throws SFRMMessageException {
		try {
			bodyPart.setContent(content, contentType);
			bodyPart.setHeader("Content-Type", contentType);
			bodyPart.setHeader("Content-Transfer-Encoding", transferEncoding);
		} catch (MessagingException e) {
			throw new SFRMMessageException("Unable to set SFRM Segment content", e);
		}
	}
	
	/**
	 * Gets the content of payload from this message.
	 * 
	 * @return the content part.
	 * @throws SFRMMessageException if unable to get the content.
	 */
	public Object getContent() throws SFRMMessageException {
		try {
			return bodyPart.getContent();
		} catch (Exception e) {
			throw new SFRMMessageException("Unable to get SFRM content", e);
		}
	}
	
	/**
	 * Gets the content type of the payload from this message.
	 * 
	 * @return the content type.
	 * @throws SFRMMessageException 
	 * 			if unable to get the content type.
	 */
	public String getContentType() throws SFRMMessageException {
		try {
			return bodyPart.getContentType();
		} catch (MessagingException e) {
			throw new SFRMMessageException("Unable to get content type", e);
		}
	}
	
	/**
	 * Gets the content stream of this message.
	 * 
	 * @return the content stream of this message.
	 * 		   null if there is no content.
	 */
	public InputStream getContentStream() throws IOException{
		try {			
			return bodyPart.getRawInputStream();
		} catch (MessagingException e) {
			try {
				// try getting the input stream if there is no raw stream available.
				return bodyPart.getInputStream();
			} catch (Exception ex) {
				return null; 
			}
		}
	}

	/**
	 *  Gets the input stream of this message's content. 
	 *  Any transfer encodings will be decoded before the input stream is provided.
	 * 
	 * @return the input stream of this message's content.
	 * @throws SFRMMessageException if unable to retrieve the stream.
	 */
	public InputStream getInputStream() throws SFRMMessageException {
		try {
			return bodyPart.getInputStream();
		} catch (Exception e) {
			throw new SFRMMessageException("Unable to get input stream of content", e);
		}
	}
	
	/**
	 * Gets a message header of the specified name.
	 * 
	 * @param name the header name.
	 * @return the header value.
	 */
	public String getHeader(String name) {
		String[] hs = headers.getHeader(name);
		if (hs == null || hs.length < 1) {
			return null;
		} else {
			StringBuffer header = new StringBuffer();
			for (int i = 0; i < hs.length; i++) {
				header.append(hs[i]);
				if (i + 1 < hs.length)
					header.append(", ");
			}
			return header.toString();
		}
	}

	/**
	 * Gets a message header of the specified name.
	 * 
	 * @param name the header name.
	 * @param def the default value.
	 * @return the header value.
	 */
	public String getHeader(String name, String def) {
		String header = getHeader(name);
		return header == null ? def : header;
	}

	/**
	 * Sets a message header of the specified name.
	 * 
	 * @param name the header name.
	 * @param value the header value.
	 */
	public void setHeader(String name, String value) {
		if (name != null && value != null)
			headers.setHeader(name, value);
	}

	/**
	 * Removes a message header of the specified name.
	 * 
	 * @param name the header name.
	 */
	public void removeHeader(String name) {
		if (name != null)
			headers.removeHeader(name);
	}

	/**
	 * Adds a message header of the specified name.
	 * 
	 * @param name the header name.
	 * @param value the header value.
	 */
	public void addHeader(String name, String value) {
		if (name != null && value != null)
			headers.addHeader(name, value);
	}

	/**
	 * Gets the MIME body part of this message.
	 * 
	 * @return the MIME body part.
	 */
	public MimeBodyPart getBodyPart() {
		return bodyPart;
	}

	/**
	 * Sets the MIME body part of this message.
	 *
	 * @param bp the new MIME body part.
	 */
	public void setBodyPart(MimeBodyPart bp) {
		if (bp != null)
			bodyPart = bp;
	}
	
	/**
	 * Gets the headers of this message.
	 * 
	 * @return a copy of the headers of this message.
	 */
	public InternetHeaders getHeaders() {
		InternetHeaders h = new InternetHeaders();
		copyHeaders(headers, h, null, false);
		copyHeaders(bodyPart, h, null, false);
		return h;
	}

	/**
	 * Copy the given headers to a specified internet headers object.
	 * 
	 * @param fromHeaders 
	 * 				the headers source.
	 * @param toHeaders 
	 * 				the headers destination.
	 * @param filter 
	 * 				the filter in regular expression.
	 * @param isMovingHeaders 
	 */
	private void copyHeaders(Object 		 fromHeaders
							,InternetHeaders toHeaders
							,String 		 filter
							,boolean 		 isMovingHeaders) {
		if (fromHeaders != null && toHeaders != null) {
			Enumeration enums;
			if (fromHeaders instanceof InternetHeaders) {
				enums = ((InternetHeaders) fromHeaders).getAllHeaderLines();
			} else if (fromHeaders instanceof MimeBodyPart) {
				try {
					enums = ((MimeBodyPart) fromHeaders).getAllHeaderLines();
				} catch (MessagingException e) {
					return;
				}
			} else {
				return;
			}
			while (enums.hasMoreElements()) {
				String headerline = enums.nextElement().toString();
				if (filter == null || headerline.matches(filter)) {
					toHeaders.addHeaderLine(headerline);
					if (isMovingHeaders) {
						String headerName = headerline.split(":")[0];
						if (fromHeaders instanceof InternetHeaders) {
							((InternetHeaders) fromHeaders)
									.removeHeader(headerName);
						} else if (fromHeaders instanceof MimeBodyPart) {
							try {
								((MimeBodyPart) fromHeaders)
										.removeHeader(headerName);
							} catch (MessagingException e) {
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Writes the message to the given output stream.
	 * 
	 * @param outs the output stream to be written.
	 * @throws SFRMMessageException if unable to write the message.
	 */
	public void writeTo(OutputStream outs) throws SFRMMessageException {
		try {
			Enumeration enums = headers.getAllHeaderLines();
			while (enums.hasMoreElements()) {
				outs.write((enums.nextElement() + "\r\n").getBytes());
			}
			bodyPart.writeTo(outs);
			outs.flush();
		} catch (Exception e) {
			throw new SFRMMessageException("Unable to write message", e);
		}
	}
	
	/**
	 * Returns a byte array which represents this message contnet.
	 * 
	 * @return a byte array which represents this message.
	 * @throws SFRMMessageException if unable to convert this message into bytes.
	 */
	public byte[] toByteArray() throws SFRMMessageException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		this.writeTo(baos);
		return baos.toByteArray();
	}
	
	/**
	 * Returns a byte buffer which represents this message content,
	 * i.e. a MIME body part byte buffer.
	 * 
	 * @param hardwareBuffer
	 * 			
	 */
	public ByteBuffer toByteBuffer(boolean hardwareBuffer) throws SFRMMessageException{
		// TODO: Implement NIO byte buffer for SFRM Message
		return null;					
	}
	
	/**
	 * @return 
	 * 			returns a clone for this Message.
	 */
	protected Object clone() throws CloneNotSupportedException {
		SFRMMessage cloneMsg = new SFRMMessage();
		cloneMsg.copyHeaders(this.headers, cloneMsg.headers, null, false);
		return cloneMsg;
	}
		
	/**
	 * Returns a string representation of this message.
	 * 
	 * @return 
	 * 			a string representation of this message.
	 * @see 
	 * 			java.lang.Object#toString()
	 * @since
	 * 			1.0.0
	 */
	public String toString() {
		StringBuffer ret = new StringBuffer(); 
		ret .append("\n")
			.append(this.getClass().getName() + " \n")
			.append("Version     : " + this.getVersion()		+ " \n")
			.append("Message Id  : " + this.getMessageID()		+ " \n")
			.append("Partner Id  : " + this.getPartnershipId() 	+ " \n")
			.append("SegmentNo   : " + this.getSegmentNo()		+ " \n")
			.append("SegmentType : " + this.getSegmentType()	+ " \n")
			.append("MIC value   : " + this.getMicValue()		+ " \n"); 
		return ret.toString();
	}
	
	/*
	 * Generate checksum from SFRMessage content in base64 format.
	 * The class type of content will be checked for incoming and outgoing message.
	 * 
	 * @param inStream stream of data to be digest to produce md5 hash
	 * @return mic value in base64 format
	 */
	public static String digest(InputStream inStream) throws SFRMMessageException{
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
	        DigestInputStream dis = new DigestInputStream(inStream, md);
	        
	        byte[] buf = new byte[BUFFER_SIZE];
	        while (dis.read(buf) != -1) {
	        }
	    
	        return (new sun.misc.BASE64Encoder()).encode(dis.getMessageDigest().digest());
		} catch (Exception e) {
			throw new SFRMMessageException("Unable to generate message digest", e);
		}
	}
	
	public static String digest(FileRegionDataSource frds) throws SFRMMessageException{
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			md.update(frds.getByteBuffer());
			return (new sun.misc.BASE64Encoder()).encode(md.digest());
		} catch (Exception e) {
			throw new SFRMMessageException("Unable to generate message digest", e);
		}
	}	
	
	/*
	 * Generate checksum from SFRMessage content in base64 format.
	 * The class type of content will be checked for incoming and outgoing message.
	 * 
	 * For outgoing payload segment, FileRegionDataSource is expected
	 * For incoming payload segment, InputStream (ByteArrayInputStream) is expected
	 * For incoming acknowledgement request, String is expected
	 * @return mic value in base64 format
	 */
	public String digest() throws SFRMException {
		try {
			Object obj = this.getBodyPart().getContent();
			if (obj instanceof InputStream) 
				return digest((InputStream)obj);
			else if (obj instanceof FileRegionDataSource)
				return digest((FileRegionDataSource)obj);
			else if (obj instanceof String)
				return digest(this.bodyPart.getInputStream());
			else 
				throw new SFRMMessageException("Message content object not supported to be digested - " + obj.getClass().getName());
		} catch(Exception e){
			throw new SFRMException("Unable to generate message digest", e);
		}
    }	
	
    public void sign(X509Certificate cert, PrivateKey privateKey, String digestAlg) throws SFRMException {
        try {
        	
            /* Create the SMIMESignedGenerator */
            SMIMECapabilityVector capabilities = new SMIMECapabilityVector();
            capabilities.addCapability(SMIMECapability.dES_EDE3_CBC);
            capabilities.addCapability(SMIMECapability.rC2_CBC, 128);
            capabilities.addCapability(SMIMECapability.dES_CBC);
                        
            SMIMESignedGenerator signer = new SMIMESignedGenerator();
            
            signer.setContentTransferEncoding("binary");
            
            // if (digestAlg.equalsIgnoreCase(ALG_SIGN_MD5))
            // 	signer.addSigner(privateKey, cert, SMIMESignedGenerator.DIGEST_MD5);
            // else if (digestAlg.equalsIgnoreCase(ALG_SIGN_SHA1))
            // 	signer.addSigner(privateKey, cert, SMIMESignedGenerator.DIGEST_SHA1);
            // else
            // 	throw new SFRMException("Encryption algorithm error - " + digestAlg);
	    
	    String signerDigestAlg = "";
	    
	    if (digestAlg.equalsIgnoreCase(ALG_SIGN_MD5))
		signerDigestAlg = "MD5withRSA";
	    else if (digestAlg.equalsIgnoreCase(ALG_SIGN_SHA1))
		signerDigestAlg = "SHA1withRSA";
	    else
		throw new SFRMException("Encryption algorihtm error - " + digestAlg);

	    signer.addSignerInfoGenerator(new JcaSimpleSignerInfoGeneratorBuilder()
		    .setProvider(SECURITY_PROVIDER)
		    .build(signerDigestAlg, privateKey, cert));

            /* Add the list of certs to the generator */
            ArrayList<X509Certificate> certList = new ArrayList<X509Certificate>();
            certList.add(cert);
            CertStore certs = CertStore.getInstance("Collection",
                    new CollectionCertStoreParameters(certList), "BC");
            // signer.addCertificatesAndCRLs(certs);
	    signer.addCertificates(new JcaCertStore(certList));

            /* Sign the body part */
            MimeMultipart mm = signer.generate(bodyPart);

            InternetHeaders headers = new InternetHeaders();
            headers.setHeader("Content-Type", mm.getContentType());
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mm.writeTo(baos);
           
            this.bodyPart = new MimeBodyPart(headers, baos.toByteArray());
            
            this.setIsSigned(true);
            
        } catch (org.bouncycastle.mail.smime.SMIMEException ex) {
            throw new SFRMException("Unable to sign body part", ex.getUnderlyingException());
        } catch (Exception e) {
            throw new SFRMException("Unable to sign body part", e);
        }
    }	
    
    public void verify(X509Certificate cert) throws SFRMException {
        try {
            SMIMESigned signed = new SMIMESigned((MimeMultipart)bodyPart.getContent());
            SignerInformationStore signers = signed.getSignerInfos();
            Iterator signerInfos = signers.getSigners().iterator();
        
            while (signerInfos.hasNext()) {
                SignerInformation signerInfo = (SignerInformation)signerInfos.next();
		SignerInformationVerifier verifier =
		    new BcRSASignerInfoVerifierBuilder(new DefaultCMSSignatureAlgorithmNameGenerator(),
						  new DefaultSignatureAlgorithmIdentifierFinder(),
						  new DefaultDigestAlgorithmIdentifierFinder(), 
						  new BcDigestCalculatorProvider())
		    .build(new JcaX509CertificateHolder(cert));
                if (!signerInfo.verify(verifier)) {
                    throw new SFRMMessageException("Verification failed");
                }
            }
            
            MimeBodyPart signedPart = signed.getContent();
            if (signedPart == null) {
                throw new SFRMMessageException("Unable to extract signed part");
            }
            else {
            	this.bodyPart = signedPart;
            	this.setIsSigned(true);
            }
            
    	} catch (org.bouncycastle.cms.CMSException ex) {
    		throw new SFRMException("Unable to verify body part", ex.getUnderlyingException());
        } catch (Exception e) {
            throw new SFRMException("Unable to verify body part", e);
        }
    }    
    
    public void encrypt(X509Certificate cert, String encryptAlg) throws SFRMException {
        try {       	
            /* Create the encrypter */
            SMIMEEnvelopedGenerator encrypter = new SMIMEEnvelopedGenerator();
            encrypter.setContentTransferEncoding("binary");
            // encrypter.addKeyTransRecipient(cert);
	    encrypter.addRecipientInfoGenerator(
		new JceKeyTransRecipientInfoGenerator(cert).setProvider(SECURITY_PROVIDER));
    
            /* Encrypt the body part */
        	if (encryptAlg.equalsIgnoreCase(ALG_ENCRYPT_RC2))
		    // this.bodyPart = encrypter.generate(bodyPart, SMIMEEnvelopedGenerator.RC2_CBC, "BC");
		    this.bodyPart = encrypter.generate(bodyPart,
				        new JceCMSContentEncryptorBuilder(
					    new ASN1ObjectIdentifier(SMIMEEnvelopedGenerator.RC2_CBC))
				        .setProvider(SECURITY_PROVIDER).build());
        	else if (encryptAlg.equalsIgnoreCase(ALG_ENCRYPT_3DES))
		    //this.bodyPart = encrypter.generate(bodyPart, SMIMEEnvelopedGenerator.DES_EDE3_CBC, "BC");
		    this.bodyPart = encrypter.generate(bodyPart,
				        new JceCMSContentEncryptorBuilder(
					    new ASN1ObjectIdentifier(SMIMEEnvelopedGenerator.DES_EDE3_CBC))
				        .setProvider(SECURITY_PROVIDER).build());
        	else
        		throw new SFRMException("Encryption algorithm error - " + encryptAlg);
            
            this.setIsEncrypted(true);
            
        } catch (org.bouncycastle.mail.smime.SMIMEException ex) {
            throw new SFRMException("Unable to encrypt body part", ex.getUnderlyingException());
        } catch (Exception e) {
            throw new SFRMException("Unable to encrypt body part", e);
        }
    }    
    
    public void decrypt(X509Certificate cert, PrivateKey privateKey) throws SFRMException {
        try {
            SMIMEEnveloped m = new SMIMEEnveloped(bodyPart);
            // RecipientId recId = new RecipientId();
	    RecipientId recId = new JceKeyTransRecipientId(cert);
    
            // recId.setSerialNumber(cert.getSerialNumber());
            // recId.setIssuer(cert.getIssuerX500Principal().getEncoded());
    
            // RecipientInformationStore  recipients = m.getRecipientInfos();
            // RecipientInformation       recipient = recipients.get(recId);
	    
	    RecipientInformationStore  recipientsInfo = m.getRecipientInfos();	
	    RecipientInformation       recipientInfo = recipientsInfo.get(recId);
    
            if (recipientInfo == null) {
                throw new SFRMMessageException("Invalid encrypted content");
            }

	    JceKeyTransEnvelopedRecipient recipient = new JceKeyTransEnvelopedRecipient(privateKey);
	    recipient.setProvider(SECURITY_PROVIDER);							
	    
            this.bodyPart = new MimeBodyPart(new ByteArrayInputStream(recipientInfo.getContent(recipient)));
            this.setIsEncrypted(true);
    	} catch (org.bouncycastle.cms.CMSException ex) {
    		throw new SFRMException("Unable to decrypt body part", ex.getUnderlyingException());
        } catch (Exception e) {
            throw new SFRMException("Unable to decrypt body part", e);
        }
    }    
    
    public boolean isEncryptedContentType() throws SFRMException {
        try {
        	return bodyPart.isMimeType("application/pkcs7-mime");
        }
        catch (MessagingException e) {
            throw new SFRMException("Unable to check if body part is encrypted.", e);
        }
    }
    
    public boolean isSignedContentType() throws SFRMException {
        try {
            return bodyPart.isMimeType("multipart/signed");
        }
        catch (MessagingException e) {
            throw new SFRMException("Unable to check if body part is signed.", e);
        }
    }
}
