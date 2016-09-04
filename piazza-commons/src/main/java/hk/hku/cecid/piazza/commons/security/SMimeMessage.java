/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.security;

import hk.hku.cecid.piazza.commons.activation.Mailcap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;

import javax.activation.CommandInfo;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Session;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.asn1.smime.SMIMEEncryptionKeyPreferenceAttribute;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMECompressed;
import org.bouncycastle.mail.smime.SMIMECompressedGenerator;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMESigned;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.util.encoders.Base64;


/**
 * SMimeMessage represents a Secure MIME Message. It encapsulates a MIME body part 
 * and provides methods for digital signing, signature verification, encryption,
 * decryption, compression, and decompression.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class SMimeMessage {

    private static Mailcap[] mailcaps = null;
    
    static
    {
      mailcaps = new Mailcap[]{
          new Mailcap("application/pkcs7-signature", "content-handler", "org.bouncycastle.mail.smime.handlers.pkcs7_signature"), 
          new Mailcap("application/pkcs7-mime", "content-handler", "org.bouncycastle.mail.smime.handlers.pkcs7_mime"), 
          new Mailcap("application/x-pkcs7-signature", "content-handler", "org.bouncycastle.mail.smime.handlers.x_pkcs7_signature"), 
          new Mailcap("application/x-pkcs7-mime", "content-handler", "org.bouncycastle.mail.smime.handlers.x_pkcs7_mime"), 
          new Mailcap("multipart/signed", "content-handler", "org.bouncycastle.mail.smime.handlers.multipart_signed"), 
          new Mailcap("text/xml", "content-handler", "com.sun.mail.handlers.text_xml")
      };
    }
    
    /**
     * Digest algorithm: MD5
     */
    public static final String DIGEST_ALG_MD5 = SMIMESignedGenerator.DIGEST_MD5;
    
    /**
     * Digest algorithm: SHA
     */
    public static final String DIGEST_ALG_SHA1 = SMIMESignedGenerator.DIGEST_SHA1;
    
    /**
     * Encryption algorithm: DES EDE3
     */
    public static final String ENCRYPT_ALG_DES_EDE3_CBC = SMIMEEnvelopedGenerator.DES_EDE3_CBC;
    
    /**
     * Encryption algorithm: RC2
     */
    public static final String ENCRYPT_ALG_RC2_CBC = SMIMEEnvelopedGenerator.RC2_CBC;
    
    /**
     * Content transfer encoding: Base 64
     */
    public static final String CONTENT_TRANSFER_ENC_BASE64 = "base64";
    
    /**
     * Content transfer encoding: Binary
     */
    public static final String CONTENT_TRANSFER_ENC_BINARY = "binary";
    
    private static final String SECURITY_PROVIDER = "BC"; 
    
    private MimeBodyPart bodyPart;
    
    private Session session;

    private PrivateKey privateKey;
    
    private X509Certificate cert;
    
    private String digestAlgorithm;
    
    private String encryptAlgorithm;
    
    private String contentTransferEncoding;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    /**
     * Creates a new instance of SMimeMessage.
     * 
     * @param bodyPart the original MIME body part.
     */
    public SMimeMessage(MimeBodyPart bodyPart) {
        this(bodyPart, (X509Certificate)null);
    }

    /**
     * Creates a new instance of SMimeMessage.
     * 
     * @param bodyPart the original MIME body part.
     * @param cert the certificate for signature verification or encryption.
     */
    public SMimeMessage(MimeBodyPart bodyPart, X509Certificate cert) {
        this(bodyPart, cert, null, null);
    }

    /**
     * Creates a new instance of SMimeMessage.
     * 
     * @param bodyPart the original MIME body part.
     * @param cert the certificate for signature verification or encryption.
     * @param session the mail session.
     */
    public SMimeMessage(MimeBodyPart bodyPart, X509Certificate cert, Session session) {
        this(bodyPart, cert, null, session);
    }

    /**
     * Creates a new instance of SMimeMessage.
     * 
     * @param bodyPart the original MIME body part.
     * @param cert the certificate for signature verification or encryption.
     * @param privateKey the private key for digital signing or decryption.
     */
    public SMimeMessage(MimeBodyPart bodyPart, X509Certificate cert, PrivateKey privateKey) {
        this(bodyPart, cert, privateKey, null);
    }
    
    /**
     * Creates a new instance of SMimeMessage.
     * 
     * @param bodyPart the original MIME body part.
     * @param cert the certificate for signature verification or encryption.
     * @param privateKey the private key for digital signing or decryption.
     * @param session the mail session.
     */
    public SMimeMessage(MimeBodyPart bodyPart, X509Certificate cert, PrivateKey privateKey, Session session) {
        this.bodyPart = bodyPart;
        this.cert = cert;
        this.privateKey = privateKey;
        this.session = session;
    }
    
    /**
     * Creates a new instance of SMimeMessage.
     * 
     * @param bodyPart the original MIME body part.
     * @param smime the S/MIME message from which the configuration is copied.
     */
    protected SMimeMessage(MimeBodyPart bodyPart, SMimeMessage smime) {
        this(bodyPart, smime.cert, smime.privateKey, smime.session);
        this.digestAlgorithm = smime.digestAlgorithm;
        this.encryptAlgorithm = smime.encryptAlgorithm;
        this.contentTransferEncoding = smime.contentTransferEncoding;
    }
    
    /**
     * Signs the encapsulated MIME body part.  
     * 
     * @return an S/MIME message encapsulating the signed MIME body part. 
     * @throws SMimeException if unable to sign the body part.
     */
    public SMimeMessage sign() throws SMimeException {
        try {
            if (privateKey==null) {
                throw new SMimeException("Private key not found");
            }
            
            try {
                setDefaults();

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
                signer.setContentTransferEncoding(getContentTransferEncoding());
                signer.addSigner(privateKey, cert, getDigestAlgorithm(),
                    new AttributeTable(attributes), null);
    
                /* Add the list of certs to the generator */
                ArrayList certList = new ArrayList();
                certList.add(cert);
                CertStore certs = CertStore.getInstance("Collection",
                        new CollectionCertStoreParameters(certList), SECURITY_PROVIDER);
                signer.addCertificatesAndCRLs(certs);
    
                /* Sign the body part */
                MimeMultipart mm = signer.generate(bodyPart, SECURITY_PROVIDER);

                InternetHeaders headers = new InternetHeaders();
                boolean isContentTypeFolded = new Boolean(System.getProperty("mail.mime.foldtext","true")).booleanValue();
                headers.setHeader("Content-Type", isContentTypeFolded? mm.getContentType():mm.getContentType().replaceAll("\\s", " "));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mm.writeTo(baos);  
                MimeBodyPart signedPart = new MimeBodyPart(headers, baos.toByteArray());

                return new SMimeMessage(signedPart, this);
            }
            catch (org.bouncycastle.mail.smime.SMIMEException ex) {
                throw new SMimeException(ex.getMessage(), ex.getUnderlyingException());
            }
        }
        catch (Exception e) {
            throw new SMimeException("Unable to sign body part", e);
        }
    }

    /**
     * Unsigns the encapsulated MIME body part.
     * 
     * @return the an S/MIME message encapsulating the signed content.
     * @throws SMimeException if unable to unsign the body part.
     */
    public SMimeMessage unsign() throws SMimeException {
        try {
            setDefaults();

            SMIMESigned signed = new SMIMESigned((MimeMultipart)bodyPart.getContent());
            MimeBodyPart signedPart = signed.getContent();
            if (signedPart == null) {
                throw new SMimeException("No signed part");
            }
            return new SMimeMessage(signedPart, this);
        }
        catch (Exception e) {
            if (e instanceof CMSException) {
                e = ((CMSException)e).getUnderlyingException();
            }
            throw new SMimeException("Unable to unsign body part", e);
        }
    } 
    
    /**
     * Verifies the encapsulated MIME body part.
     * 
     * @return an S/MIME message encapsulating the signed content. 
     * @throws SMimeException if unable to verify the body part.
     */
    public SMimeMessage verify() throws SMimeException {
        return verify(cert);
    }

    /**
     * Verifies the encapsulated MIME body part.
     * 
     * @param cert the certificate for verification.
     * @return an S/MIME message encapsulating the signed content. 
     * @throws SMimeException if unable to verify the body part.
     */
    public SMimeMessage verify(X509Certificate cert) throws SMimeException {
        try {
            if (cert == null) {
                throw new SMimeException("No certificate for verification");
            }

            setDefaults();

            SMIMESigned signed = new SMIMESigned((MimeMultipart)bodyPart.getContent());
            // CertStore cs = signed.getCertificatesAndCRLs("Collection", "BC");
            SignerInformationStore  signers = signed.getSignerInfos();
            Iterator signerInfos = signers.getSigners().iterator();
        
            while (signerInfos.hasNext()) {
                SignerInformation   signerInfo = (SignerInformation)signerInfos.next();
                if (!signerInfo.verify(cert, "BC")) {
                    throw new SMimeException("Verification failed");
                }
            }
            
            MimeBodyPart signedPart = signed.getContent();
            if (signedPart == null) {
                throw new SMimeException("Unable to extract signed part");
            }
            else {
                return new SMimeMessage(signedPart, this);
            }
        }
        catch (Exception e) {
            if (e instanceof CMSException) {
                e = ((CMSException)e).getUnderlyingException();
            }
            throw new SMimeException("Unable to verify body part", e);
        }
    }
    
    /**
     * Encrypts the encapsulated MIME body part.
     * 
     * @return an S/MIME message encapsulating the encrypted MIME body part. 
     * @throws SMimeException if unable to encrpyt the body part.
     */
    public SMimeMessage encrypt() throws SMimeException {
        return encrypt(cert);
    }
    
    /**
     * Encrypts the encapsulated MIME body part.
     * 
     * @param cert the certificate for encryption.
     * @return an S/MIME message encapsulating the encrypted MIME body part. 
     * @throws SMimeException if unable to encrpyt the body part.
     */
    public SMimeMessage encrypt(X509Certificate cert) throws SMimeException {
        try {
            try {
                if (cert == null) {
                    throw new SMimeException("No certificate for encryption");
                }

                setDefaults();
                
                /* Create the encrypter */
                SMIMEEnvelopedGenerator encrypter = new SMIMEEnvelopedGenerator();
                encrypter.setContentTransferEncoding(getContentTransferEncoding());
                encrypter.addKeyTransRecipient(cert);
        
                /* Encrypt the body part */
                MimeBodyPart encryptedPart = encrypter.generate(bodyPart, getEncryptAlgorithm(), SECURITY_PROVIDER);
                return new SMimeMessage(encryptedPart, this);
            }
            catch (org.bouncycastle.mail.smime.SMIMEException ex) {
                throw new SMimeException(ex.getMessage(), ex.getUnderlyingException());
            }
        }
        catch (Exception e) {
            throw new SMimeException("Unable to encrypt body part", e);
        }
    }
    
    /**
     * Decrypts the encapsulated MIME body part.
     * 
     * @return an S/MIME message encapsulating the decrypted MIME body part. 
     * @throws SMimeException if unable to decrpyt the body part.
     */
    public SMimeMessage decrypt() throws SMimeException {
        return decrypt(privateKey);
    }
    
    /**
     * Decrypts the encapsulated MIME body part.
     * 
     * @param privateKey the private key for decryption.
     * @return an S/MIME message encapsulating the decrypted MIME body part. 
     * @throws SMimeException if unable to decrpyt the body part.
     */
    public SMimeMessage decrypt(PrivateKey privateKey) throws SMimeException {
        if (privateKey==null) {
            throw new SMimeException("Private key not found");
        }

        try {
            setDefaults();
            
            SMIMEEnveloped       m = new SMIMEEnveloped(bodyPart);
            RecipientId          recId = new RecipientId();
    
            recId.setSerialNumber(cert.getSerialNumber());
            recId.setIssuer(cert.getIssuerX500Principal().getEncoded());
    
            RecipientInformationStore  recipients = m.getRecipientInfos();
            RecipientInformation       recipient = recipients.get(recId);
    
            if (recipient == null) {
                throw new SMimeException("Invalid encrypted content");
            }
            ByteArrayInputStream ins = new ByteArrayInputStream(recipient.getContent(privateKey, "BC"));
            MimeBodyPart decryptedPart = new MimeBodyPart(ins); 
            return new SMimeMessage(decryptedPart, this);
        }
        catch (Exception e) {
            throw new SMimeException("Unable to decrypt body part", e);
        }
    }
    
    /**
     * Compresses the encapsulated MIME body part.
     * 
     * @return an S/MIME message encapsulating the compressed MIME body part. 
     * @throws SMimeException if unable to compress the body part.
     */
    public SMimeMessage compress() throws SMimeException {
        try {
            try {
                setDefaults();
                
                /* Create the generator for creating an smime/compressed body part */
                SMIMECompressedGenerator compressor = new SMIMECompressedGenerator();
                compressor.setContentTransferEncoding(getContentTransferEncoding());
        
                /* compress the body part */ 
                MimeBodyPart compressedPart = compressor.generate(bodyPart, SMIMECompressedGenerator.ZLIB);
                return new SMimeMessage(compressedPart, this);
            }
            catch (org.bouncycastle.mail.smime.SMIMEException ex) {
                throw new SMimeException(ex.getMessage(), ex.getUnderlyingException());
            }
        }
        catch (Exception e) {
            throw new SMimeException("Unable to compress body part", e);
        }
    }
    
    /**
     * Decompresses the encapsulated MIME body part.
     * 
     * @return an S/MIME message encapsulating the decompressed MIME body part. 
     * @throws SMimeException if unable to decompress the body part.
     */
    public SMimeMessage decompress() throws SMimeException {
        try {
            setDefaults();
            
            SMIMECompressed      m = new SMIMECompressed(bodyPart);
            ByteArrayInputStream ins = new ByteArrayInputStream(m.getContent());
            
            MimeBodyPart decompressedPart = new MimeBodyPart(ins); 
            return new SMimeMessage(decompressedPart, this);
        }
        catch (Exception e) {
            throw new SMimeException("Unable to decompress body part", e);
        }
    }

    /**
     * Digests the encapsulated MIME body part. 
     * 
     * @return the digested value in Base 64 format.
     * @throws SMimeException if unable to compute the digest value.
     */
    public String digest() throws SMimeException {
        return digest(getDigestAlgorithm(), true);
    }
    
    /**
     * Digests the encapsulated MIME body part. 
     * 
     * @param digestAlg digest algorithm.
     * @param isHeadersIncluded true if the digest should be computed on both 
     *        the headers and the content of the encapsulated body part.
     * @return the digested value in Base 64 format.
     * @throws SMimeException if unable to compute the digest value.
     */
    public String digest(String digestAlg, boolean isHeadersIncluded) throws SMimeException {
        try {
            if (digestAlg == null) {
                digestAlg = SMimeMessage.DIGEST_ALG_SHA1;
            }
    
            MessageDigest md = MessageDigest.getInstance(digestAlg, "BC");
        
            InputStream ins;
            
            if (isHeadersIncluded) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bodyPart.writeTo(baos);
                byte[] data = baos.toByteArray();
                ins = canonicalize(data);
            }
            else {
                ins = bodyPart.getInputStream();
            }
        
            DigestInputStream digIns = new DigestInputStream(ins, md);
        
            byte[] buf = new byte[1024];
            while (digIns.read(buf) >= 0) {
            }
        
            byte[] digest = digIns.getMessageDigest().digest();
            String digestString = new String(Base64.encode(digest));
            return digestString;
        }
        catch (Exception e) {
            throw new SMimeException("Unable to compute message digest", e);
        }
    }
    
    /**
     * Canonicalizes the given data by removing the starting new lines.
     * 
     * @param data the data to be canonicalized.
     * @return the canonicalized data as an input stream.
     */
    private static InputStream canonicalize(byte[] data) {
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
        
        return new ByteArrayInputStream(data, pos, data.length);
    }

    
    /**
     * Checks if the encapsulated MIME body part is encrypted.
     * 
     * @return true if the encapsulated MIME body part is encrypted.
     * @throws SMimeException if error occurred in checking.
     */
    public boolean isEncrypted() throws SMimeException {
        try {
            String contentType = bodyPart.getContentType();
            return (contentType != null && contentType.toLowerCase().indexOf("enveloped-data") != -1);
        }
        catch (Exception e) {
            throw new SMimeException("Unable to check if body part is encrypted.", e);
        }
    }
    
    /**
     * Checks if the encapsulated MIME body part is compressed.
     * 
     * @return true if the encapsulated MIME body part is compressed.
     * @throws SMimeException if error occurred in checking.
     */
    public boolean isCompressed() throws SMimeException {
        try {
            String contentType = bodyPart.getContentType();
            return (contentType != null && contentType.toLowerCase().indexOf("compressed-data") != -1);
        }
        catch (Exception e) {
            throw new SMimeException("Unable to check if body part is compressed.", e);
        }
    }
    
    /**
     * Checks if the encapsulated MIME body part is signed.
     * 
     * @return true if the encapsulated MIME body part is signed.
     * @throws SMimeException if error occurred in checking.
     */
    public boolean isSigned() throws SMimeException {
        try {
            return bodyPart.isMimeType("multipart/signed");
        }
        catch (Exception e) {
            throw new SMimeException("Unable to check if body part is signed.", e);
        }
    }
    
    /**
     * Gets the encapsulated MIME body part.
     * 
     * @return the encapsulated MIME body part.
     */
    public MimeBodyPart getBodyPart() {
        return bodyPart;
    }
    
    /**
     * Gets the digest algorithm which will be used in digital signing.
     * 
     * @return the digest algorithm.
     */
    public String getDigestAlgorithm() {
        if (digestAlgorithm == null) {
            if (privateKey == null) {
                return null;
            }
            else {
                return "DSA".equals(privateKey.getAlgorithm()) ?
                        DIGEST_ALG_SHA1 : DIGEST_ALG_MD5;
            }
        }
        else {
            return digestAlgorithm;
        }
    }
    
    /**
     * Sets the digest algorithm to used in digital signing.
     * 
     * @param digestAlgorithm the digest algorithm.
     */
    public void setDigestAlgorithm(String digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }
    
    /**
     * Gets the encryption algorithm which will be used in encryption.
     * 
     * @return the encryption algorithm.
     */
    public String getEncryptAlgorithm() {
        return encryptAlgorithm == null? ENCRYPT_ALG_DES_EDE3_CBC : encryptAlgorithm;
    }
    
    /**
     * Sets the encryption algorithm to be used in encryption.
     * 
     * @param encryptAlgorithm the encryption algorithm.
     */
    public void setEncryptAlgorithm(String encryptAlgorithm) {
        this.encryptAlgorithm = encryptAlgorithm;
    }
    
    /**
     * Gets the content transfer encoding which will be used in encryption,
     * digital signing, and compression. 
     * 
     * @return the content transfer encoding.
     */
    public String getContentTransferEncoding() {
        return contentTransferEncoding == null? CONTENT_TRANSFER_ENC_BASE64:contentTransferEncoding;
    }
    
    /**
     * Sets the content transfer encoding to used in encryption, digital 
     * signing, and compression. 
     * 
     * @param contentTransferEncoding the content transfer encoding.
     */
    public void setContentTransferEncoding(String contentTransferEncoding) {
        this.contentTransferEncoding = contentTransferEncoding;
    }
    
    /**
     * Sets the default mail caps.
     */
    private void setDefaults() {
        MailcapCommandMap mailcap = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        
        for (int i = 0; i < mailcaps.length; i++)
        {
          CommandInfo command = mailcap.getCommand(mailcaps[i].getMimeType(), mailcaps[i].getCommandName());
          if (command == null || !command.getCommandClass().equals(mailcaps[i].getClassName()))
          {
            mailcap.addMailcap(mailcaps[i].toString());
          }
        }
        
        CommandMap.setDefaultCommandMap(mailcap);
    }
}
 