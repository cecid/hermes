/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.module;

import hk.hku.cecid.edi.as2.AS2Exception;
import hk.hku.cecid.edi.as2.AS2Processor;
import hk.hku.cecid.edi.as2.dao.AS2DAOHandler;
import hk.hku.cecid.edi.as2.dao.PartnershipDVO;
import hk.hku.cecid.edi.as2.pkg.AS2Header;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.edi.as2.pkg.Disposition;
import hk.hku.cecid.edi.as2.pkg.DispositionNotification;
import hk.hku.cecid.edi.as2.pkg.DispositionNotificationOption;
import hk.hku.cecid.edi.as2.pkg.DispositionNotificationOptions;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;
import hk.hku.cecid.piazza.commons.security.SMimeMessage;

import java.util.Iterator;

import javax.mail.internet.MimeBodyPart;


/**
 * IncomingMessage
 * 
 * @author Hugo Y. K. Lam
 *
 */
class IncomingMessage {

    private AS2Message requestMessage;
    
    private SMimeMessage originalMessage;
    private SMimeMessage digestMessage;
    private SMimeMessage processedMessage;
    
    private Disposition disposition;
    
    private KeyStoreManager keyman;
    private AS2DAOHandler daoHandler;
    
    public IncomingMessage(AS2Message requestMessage, KeyStoreManager keyman, AS2DAOHandler daoHandler) {
        this.keyman = keyman;
        this.daoHandler = daoHandler;
        this.disposition = new Disposition();
        this.requestMessage = requestMessage;
    }
    
    public Disposition getDisposition() {
        return disposition;
    }
    
    public Disposition processSMime() throws AS2Exception {
        try {
            originalMessage = new SMimeMessage(requestMessage.getBodyPart(), keyman.getX509Certificate(), keyman.getPrivateKey());
            processedMessage = originalMessage;
            digestMessage = originalMessage;
            
            PartnershipDVO partnership;
            try {
                partnership = daoHandler.findPartnership(requestMessage, true);
            }
            catch (Exception e) {
                AS2Processor.core.log.error("Partnership check failed: "+requestMessage, e);
                disposition.setDescription(Disposition.DESC_AUTHENTICATION_FAILED);
                disposition.setModifier(Disposition.MODIFIER_ERROR);
                return disposition;
            }
    
            try {
                if (partnership.isInboundEncryptRequired() && !processedMessage.isEncrypted()) {
                    throw new AS2Exception("Insufficient message security");
                }
            }
            catch (Exception e) {
                AS2Processor.core.log.error("Encryption enforcement check failed: "+requestMessage, e);
                disposition.setDescription(Disposition.DESC_INSUFFICIENT_MESSAGE_SECURITY);
                disposition.setModifier(Disposition.MODIFIER_ERROR);
                return disposition;
            }
            
            try {
                if (processedMessage.isEncrypted()) {
                    AS2Processor.core.log.debug(requestMessage + " is encrypted");
                    processedMessage = processedMessage.decrypt();
                }
            }
            catch (Exception e) {
                AS2Processor.core.log.error("Unable to decrypt "+requestMessage, e);
                disposition.setDescription(Disposition.DESC_DECRYPTION_FAILED);
                disposition.setModifier(Disposition.MODIFIER_ERROR);
                return disposition;
            }
            
            for (int i=0; i<2; i++) {
                try {
                    if (processedMessage.isCompressed()){
                        AS2Processor.core.log.debug(requestMessage + " is compressed");
                        processedMessage = processedMessage.decompress();
                    }
                }
                catch (Exception e) {
                    AS2Processor.core.log.error("Unable to decompress "+requestMessage, e);
                    disposition.setDescription(Disposition.DESC_DECOMPRESSION_FAILED);
                    disposition.setModifier(Disposition.MODIFIER_ERROR);
                    return disposition;
                }
                
                if (i>0) {
                    continue;
                }
                
                try {
                    if (partnership.isInboundSignRequired() && !processedMessage.isSigned()) {
                        throw new AS2Exception("Authentication failed");
                    }
                }
                catch (Exception e) {
                    AS2Processor.core.log.error("Signature enforcement check failed: "+requestMessage, e);
                    disposition.setDescription(Disposition.DESC_AUTHENTICATION_FAILED);
                    disposition.setModifier(Disposition.MODIFIER_ERROR);
                    return disposition;
                }
                
                try {
                    if (processedMessage.isSigned()) {
                        AS2Processor.core.log.debug(requestMessage + " is signed");
                        processedMessage = processedMessage.verify(partnership.getEffectiveVerifyCertificate());
                    }
                }
                catch (Exception e) {
                    AS2Processor.core.log.error("Unable to verify "+requestMessage, e);
                    disposition.setDescription(Disposition.DESC_AUTHENTICATION_FAILED);
                    disposition.setModifier(Disposition.MODIFIER_ERROR);
                    return disposition;
                }
                
                digestMessage = processedMessage;
            }
            
            return disposition;
        }
        catch (Exception e) {
            throw new AS2Exception("Error in processing S/MIME of message: "+requestMessage.getMessageID(), e);
        }
    }
    
    public AS2Message generateReceipt() throws AS2Exception {
        try {
            AS2Processor.core.log.info(requestMessage + " requested "+(requestMessage.isReceiptSynchronous()? "synchronous":"asynchronous (URL: "+requestMessage.getHeader(AS2Header.RECEIPT_DELIVERY_OPTION)+")")+" receipt");
            
            if (digestMessage == null) {
                throw new AS2Exception("Message not processed yet.");
            }
            
            AS2Message responseMessage = requestMessage.reply();
            DispositionNotification mdn = new DispositionNotification();
            mdn.replyTo(requestMessage, AS2Processor.getModuleGroup().getSystemModule().getName());
            mdn.setDisposition(disposition);
            
            DispositionNotificationOptions dnOptions = requestMessage.getDispositionNotificationOptions();
            MimeBodyPart mdnBodyPart = null;
    
            /* Check if the MDN should be signed */
            if (dnOptions == null) {
                mdnBodyPart = mdn.getBodyPart();
            }
            else {
                AS2Processor.core.log.info(requestMessage + " requested a signed receipt");
    
                DispositionNotificationOption dnOption = dnOptions.getOption(DispositionNotificationOptions.SIGNED_RECEIPT_PROTOCOL);
                if (dnOption != null) {
                    if (dnOption.isRequired() && !DispositionNotificationOption
                            .SIGNED_RECEIPT_PROTOCOL_PKCS7.equalsIgnoreCase(dnOption.getValue())) {
                        AS2Processor.core.log.warn("Unsupported MDN signature requested: "+dnOption);
                    }
                }
    
                String digestAlg = SMimeMessage.DIGEST_ALG_SHA1;
                String micAlg = DispositionNotificationOption.SIGNED_RECEIPT_MICALG_SHA1;
                
                dnOption = dnOptions.getOption(DispositionNotificationOptions.SIGNED_RECEIPT_MICALG);
                if (dnOption != null) {
                    AS2Processor.core.log.info(requestMessage + " has shown preference on MIC algorithm: "+dnOption);
                    boolean isAlgAccepted = false;
                    Iterator algs = dnOption.getValues();
                    while(algs.hasNext() && !isAlgAccepted) {
                        String alg = (String)algs.next();
                        if (DispositionNotificationOption.SIGNED_RECEIPT_MICALG_SHA1.equalsIgnoreCase(alg)) {
                            AS2Processor.core.log.debug("MIC algorithm accepted: "+alg);
                            digestAlg = SMimeMessage.DIGEST_ALG_SHA1;
                            micAlg = DispositionNotificationOption.SIGNED_RECEIPT_MICALG_SHA1;
                            isAlgAccepted = true;
                        }
                        else if (DispositionNotificationOption.SIGNED_RECEIPT_MICALG_MD5.equalsIgnoreCase(alg)) {
                            AS2Processor.core.log.debug("MIC algorithm accepted: "+alg);
                            digestAlg = SMimeMessage.DIGEST_ALG_MD5;
                            micAlg = DispositionNotificationOption.SIGNED_RECEIPT_MICALG_MD5;
                            isAlgAccepted = true;
                        }
                    }
                    if (!isAlgAccepted) {
                        AS2Processor.core.log.warn("Unsupported MIC algorithm requested: "+dnOption);
                    }
                }
    
                String mic = digestMessage.digest(digestAlg, originalMessage.isSigned() || originalMessage.isCompressed() || originalMessage.isEncrypted());
                mdn.setReceivedContentMIC(mic, micAlg);
                AS2Processor.core.log.info(requestMessage + " has an MIC: "+mic);
    
                SMimeMessage sMDN = new SMimeMessage(mdn.getBodyPart(), keyman.getX509Certificate(), keyman.getPrivateKey());
                mdnBodyPart = sMDN.sign().getBodyPart();
            }
            
            responseMessage.setBodyPart(mdnBodyPart);
            return responseMessage;
        }
        catch (Exception e) {
            throw new AS2Exception("Unable to generate AS2 receipt for message: "+requestMessage.getMessageID(), e);
        }
    }
    
    public void dispatchMessage() throws AS2Exception {
        try {
            requestMessage.setBodyPart(processedMessage.getBodyPart());
            String[] transferEncoding = requestMessage.getBodyPart().getHeader("Content-Transfer-Encoding");
            AS2Processor.core.log.debug("Dispatching " + requestMessage + " Content-type: "+requestMessage.getContentType() + " Content-Transfer-Encoding: "+(transferEncoding==null||transferEncoding.length<1?"null":transferEncoding[0]));
            
            PayloadRepository repository = AS2Processor.getIncomingPayloadRepository();
            PayloadCache cache = repository.createPayloadCache(
                    requestMessage.getMessageID(), 
                    requestMessage.getFromPartyID(), 
                    requestMessage.getToPartyID(), 
                    requestMessage.getContentType());
            
            cache.save(requestMessage.getInputStream());
            cache.checkIn();
       }
        catch (Exception e) {
            throw new AS2Exception("Error in dispatching incoming AS2 message: "+requestMessage.getMessageID(), e);
        }
    }
}
