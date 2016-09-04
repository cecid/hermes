/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.task;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.handler.MessageServiceHandler;
import hk.hku.cecid.ebms.spa.listener.EbmsRequest;
import hk.hku.cecid.ebms.spa.listener.EbmsResponse;
import hk.hku.cecid.piazza.commons.module.ActiveTask;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;
import hk.hku.cecid.piazza.commons.security.SMimeMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.mail.Message;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

/**
 * @author Donahue Sze
 * 
 */
public class MailTask implements ActiveTask {

    // private Message message;

    private String errorMessage;

    private EbxmlMessage ebxmlMessage;

    public MailTask(Message message) {

        // this.message = message;

        // String SMIME_ENCRYPTED = "application/pkcs7-mime";

        // MessageServiceHandler msh = MessageServiceHandler.getInstance();

        try {
            KeyStoreManager ksm = EbmsProcessor
                    .getKeyStoreManagerForDecryption();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            message.writeTo(baos);
            MimeBodyPart mimeBodyPart = new MimeBodyPart(
                    new ByteArrayInputStream(baos.toByteArray()));
            baos.close();

            SMimeMessage smsg = new SMimeMessage(mimeBodyPart, ksm
                    .getX509Certificate(), ksm.getPrivateKey());

            if (smsg.isEncrypted()) {
                EbmsProcessor.core.log.info("Decrypt the message");
                smsg = smsg.decrypt();
                MimeBodyPart bp = smsg.getBodyPart();
                baos = new ByteArrayOutputStream();
                bp.writeTo(baos);
                message = new MimeMessage(null, new ByteArrayInputStream(baos
                        .toByteArray()));
                baos.close();
                baos = null;
            }

        } catch (Exception e) {
            EbmsProcessor.core.log.error(
                    "Error in processing the decryption process", e);
            errorMessage = "Error in processing the decryption process"
                    + e.getMessage();
        }

        // extract the ebxml message
        try {
            ebxmlMessage = new EbxmlMessage(message.getInputStream());
        } catch (Exception e) {
            EbmsProcessor.core.log.error(
                    "Error in reconstruct the ebxml message", e);
            errorMessage = "Error in reconstruct the ebxml message"
                    + e.getMessage();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#execute()
     */
    public void execute() throws Exception {

        // error when construct the outbox message
        if (errorMessage != null) {
            EbmsProcessor.core.log
                    .error("Error when construct the message from mail box - "
                            + errorMessage);
            throw new DeliveryException(errorMessage);
        }

        EbmsProcessor.core.log.info("Received an ebxml message from mail box");
        try {
            EbxmlMessage ebxmlResponseMessage = new EbxmlMessage();

            EbmsRequest ebmsRequest = new EbmsRequest();
            ebmsRequest.setMessage(ebxmlMessage);

            EbmsResponse ebmsResponse = new EbmsResponse();
            ebmsResponse.setMessage(ebxmlResponseMessage);

            MessageServiceHandler msh = MessageServiceHandler.getInstance();
            msh.processInboundMessage(ebmsRequest, ebmsResponse);
        } catch (Exception e) {
            EbmsProcessor.core.log
                    .error("Cannot put the message to inbound", e);
            throw new DeliveryException("Cannot put the message to inbound", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#isRetryEnabled()
     */
    public boolean isRetryEnabled() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#getRetryInterval()
     */
    public long getRetryInterval() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#getMaxRetries()
     */
    public int getMaxRetries() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#setRetried(int)
     */
    public void setRetried(int arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#onFailure(java.lang.Throwable)
     */
    public void onFailure(Throwable arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#onAwake()
     */
    public void onAwake() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#isSucceedFast()
     */
    public boolean isSucceedFast() {
        return true;
    }

    public static void main(String[] args) {
        System.out.println(new byte[] {}.toString());
    }

}