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
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.edi.as2.dao.PartnershipDVO;
import hk.hku.cecid.edi.as2.dao.RepositoryDVO;
import hk.hku.cecid.edi.as2.pkg.AS2Header;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.edi.as2.pkg.DispositionNotificationOption;
import hk.hku.cecid.piazza.commons.module.ActiveTask;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;
import hk.hku.cecid.piazza.commons.security.SMimeException;
import hk.hku.cecid.piazza.commons.security.SMimeMessage;

import javax.activation.FileDataSource;



/**
 * OutgoingPayloadTask
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class OutgoingPayloadTask implements ActiveTask {

    // private int retried;
    private PayloadCache payload;
    
    /**
     * @throws AS2Exception
     * 
     */
    public OutgoingPayloadTask(PayloadCache payload) throws AS2Exception {
        try {
            if (payload == null) {
                throw new AS2Exception("No payload data");
            }
            
            this.payload = payload;
            
            if (!this.payload.checkOut()) {
                throw new AS2Exception("Unable to check out payload: "+payload);
            }
        }
        catch (Exception e) {
            throw new AS2Exception("Unable to construct outgoing payload task", e);
        }
    }

    /**
     * execute
     * @throws Exception
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#execute()
     */
    public void execute() throws Exception {

        AS2DAOHandler daoHandler = new AS2DAOHandler(AS2Processor.core.dao);
        
        AS2Message as2Message = new AS2Message();
        as2Message.setMessageID(payload.getMessageID());
        as2Message.setFromPartyID(payload.getFromPartyID());
        as2Message.setToPartyID(payload.getToPartyID());

        PartnershipDVO partnership = daoHandler.findPartnership(as2Message, false);
        
        FileDataSource cacheSource = new FileDataSource(payload.getCache());
        as2Message.setHeader(AS2Header.SUBJECT, partnership.getSubject());
        as2Message.setContent(cacheSource, payload.getContentType());
                
        String micAlg = null;
        if (partnership.isReceiptRequired()) {
            String returnUrl = null;
            if (!partnership.isSyncReply()) {
                returnUrl = partnership.getReceiptAddress();
            }
            if (partnership.isReceiptSignRequired()) {
                micAlg = partnership.getMicAlgorithm();
            }
            
            as2Message.requestReceipt(returnUrl, micAlg);
        }
        
        KeyStoreManager keyman = AS2Processor.getKeyStoreManager();
        SMimeMessage smime = new SMimeMessage(as2Message.getBodyPart(), keyman.getX509Certificate(), keyman.getPrivateKey());
        smime.setContentTransferEncoding(SMimeMessage.CONTENT_TRANSFER_ENC_BINARY);
        
        String mic = calculateMIC(smime, partnership);
        
        if (partnership.isOutboundCompressRequired()) {
            AS2Processor.core.log.info("Compressing outbound "+as2Message);
            smime = smime.compress();
            if (partnership.isOutboundSignRequired()) {
                mic = calculateMIC(smime, partnership);
            }
        }
        if (partnership.isOutboundSignRequired()) {
            AS2Processor.core.log.info("Signing outbound "+as2Message);
            String alg = partnership.getSignAlgorithm();
            if (alg != null && alg.equalsIgnoreCase(PartnershipDVO.ALG_SIGN_MD5)) {
                smime.setDigestAlgorithm(SMimeMessage.DIGEST_ALG_MD5);
            }
            else {
                smime.setDigestAlgorithm(SMimeMessage.DIGEST_ALG_SHA1);
            }
            smime = smime.sign();
        }
        if (partnership.isOutboundEncryptRequired()) {
            AS2Processor.core.log.info("Encrypting outbound "+as2Message);
            String alg = partnership.getEncryptAlgorithm();
            if (alg != null && alg.equalsIgnoreCase(PartnershipDVO.ALG_ENCRYPT_RC2)) {
                smime.setEncryptAlgorithm(SMimeMessage.ENCRYPT_ALG_RC2_CBC);
            }
            else {
                smime.setEncryptAlgorithm(SMimeMessage.ENCRYPT_ALG_DES_EDE3_CBC);
            }
            smime = smime.encrypt(partnership.getEncryptX509Certificate());
        }
        
        as2Message.setBodyPart(smime.getBodyPart());
        
        AS2Processor.core.log.info("Persisting outbound "+as2Message);
        RepositoryDVO repositoryDVO = daoHandler.createRepositoryDVO(as2Message, false);
        MessageDVO messageDVO = daoHandler.createMessageDVO(as2Message, false); 
        messageDVO.setStatus(MessageDVO.STATUS_PENDING);
        messageDVO.setMicValue(mic);
        
        /* Capture the outgoing message */
        AS2Processor.core.log.debug(as2Message + " is being captured");
        AS2Processor.getMessageRepository().persistMessage(as2Message);
        daoHandler.createMessageStore().storeMessage(messageDVO, repositoryDVO);
        
        cacheSource = null;
        AS2Processor.core.log.debug("Clearing cache of "+as2Message+": "+payload.clear());
    }
    
    private String calculateMIC(SMimeMessage smime, PartnershipDVO partnership) throws SMimeException {
        String mic = null;
        if (partnership.isReceiptSignRequired()) {
            boolean isSMime = partnership.isOutboundCompressRequired() ||
                              partnership.isOutboundSignRequired() ||
                              partnership.isOutboundEncryptRequired();
            
            String micAlg = partnership.getMicAlgorithm();
            if (micAlg !=null && micAlg.equalsIgnoreCase(PartnershipDVO.ALG_MIC_MD5)) {
                mic = smime.digest(SMimeMessage.DIGEST_ALG_MD5, isSMime);
                micAlg = DispositionNotificationOption.SIGNED_RECEIPT_MICALG_MD5;
            }
            else {
                mic = smime.digest(SMimeMessage.DIGEST_ALG_SHA1, isSMime);
                micAlg = DispositionNotificationOption.SIGNED_RECEIPT_MICALG_SHA1;
            }
            mic =  mic + ", " + micAlg;
        }
        return mic;
    }

    /**
     * onFailure
     * @param e
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#onFailure(java.lang.Throwable)
     */
    public void onFailure(Throwable e) {
        AS2Processor.core.log.error("Outgoing payload task failure", e);
    }

    /**
     * isRetryEnabled
     * @return boolean
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#isRetryEnabled()
     */
    public boolean isRetryEnabled() {
        return false;
    }

    /**
     * getRetryInterval
     * @return long
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#getRetryInterval()
     */
    public long getRetryInterval() {
        return -1;
    }

    /**
     * getMaxRetries
     * @return int
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#getMaxRetries()
     */
    public int getMaxRetries() {
        return 0;
    }

    /**
     * setRetried
     * @param retried
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#setRetried(int)
     */
    public void setRetried(int retried) {
        // this.retried = retried;
    }

    /**
     * onAwake
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#onAwake()
     */
    public void onAwake() {
    }

    /**
     * isSucceedFast
     * @return boolean
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#isSucceedFast()
     */
    public boolean isSucceedFast() {
        return true;
    }
}
