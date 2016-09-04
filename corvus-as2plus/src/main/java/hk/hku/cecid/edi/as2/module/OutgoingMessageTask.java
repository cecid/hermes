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
import hk.hku.cecid.edi.as2.AS2PlusProcessor;
import hk.hku.cecid.edi.as2.dao.AS2DAOHandler;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.edi.as2.dao.PartnershipDVO;
import hk.hku.cecid.edi.as2.dao.RepositoryDAO;
import hk.hku.cecid.edi.as2.dao.RepositoryDVO;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.piazza.commons.module.ActiveTask;
import hk.hku.cecid.piazza.commons.net.HttpConnector;
import hk.hku.cecid.piazza.commons.security.TrustedHostnameVerifier;
import hk.hku.cecid.piazza.commons.util.Headers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;


/**
 * OutgoingMessageTask
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class OutgoingMessageTask implements ActiveTask {

    private int retried;
    private MessageDVO message;
    private PartnershipDVO partnership;
    private AS2DAOHandler daoHandler;
    
    /**
     * @throws AS2Exception
     * 
     */
    public OutgoingMessageTask(MessageDVO message) throws AS2Exception {
        try {
            if (message == null) {
                throw new AS2Exception("No message data");
            }

            this.message = message;
            this.daoHandler = new AS2DAOHandler(AS2PlusProcessor.getInstance().getDAOFactory());
            this.partnership = daoHandler.findPartnership(message.getAs2From(), message.getAs2To());
        }
        catch (Exception e) {
            throw new AS2Exception("Unable to construct outgoing message task", e);
        }
    }

    /**
     * execute
     * @throws Exception
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#execute()
     */
    public void execute() throws Exception {

        RepositoryDAO repositoryDAO = daoHandler.createRepositoryDAO();
        RepositoryDVO repositoryDVO = (RepositoryDVO) repositoryDAO.createDVO();
        repositoryDVO.setMessageId(message.getMessageId());
        repositoryDVO.setMessageBox(message.getMessageBox());
        
        if (repositoryDAO.retrieve(repositoryDVO)) {
            ByteArrayInputStream messageContent = new ByteArrayInputStream(repositoryDVO.getContent());
            AS2Message outgoingMessage = new AS2Message(messageContent);
            messageContent.close();
            
            try {
                String url = message.isReceipt()? message.getReceiptUrl() : partnership.getRecipientAddress();
                
                HttpConnector httpConn = new HttpConnector(url);
                if (!partnership.isHostnameVerified()) {
                    httpConn.setHostnameVerifier(new TrustedHostnameVerifier());
                }
                                
                HttpURLConnection conn = httpConn.createConnection();
                Headers headers = new Headers(conn);
                headers.putInternetHeaders(outgoingMessage.getHeaders());
                
                AS2PlusProcessor.getInstance().getLogger().info("Sending outgoing "+outgoingMessage+" to "+url);
                InputStream replyStream = httpConn.send(outgoingMessage.getContentStream(), conn);
                message.setStatus(MessageDVO.STATUS_DELIVERED);
                message.setStatusDescription("");
                daoHandler.createMessageDAO().persist(message);
                
                AS2EventModule eventModule = (AS2EventModule)
                	AS2PlusProcessor.getInstance().getModuleGroup().getModule(AS2EventModule.MODULE_ID);
                eventModule.fireMessageSent(outgoingMessage);
                
                try {
                    if (!message.isReceipt() && outgoingMessage.isReceiptRequested() && outgoingMessage.isReceiptSynchronous()) {
                        AS2Message replyMessage = new AS2Message(headers.getInternetHeaders(), replyStream);
                        AS2PlusProcessor.getInstance().getIncomingMessageProcessor().processReceipt(replyMessage);
                    }
                }
                catch (Throwable t) {
                    AS2PlusProcessor.getInstance().getLogger().error(outgoingMessage + " was sent but exception occurred in receiving receipt", t);
                }
            }
            catch (Exception e) {
                throw new AS2Exception("Error in sending outgoing AS2 message: "+message.getMessageId(), e);
            }
        }
        else {
            throw new AS2Exception("No repository record found for outgoing AS2 message: "+message.getMessageId());
        }
    }

    /**
     * onFailure
     * @param e
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#onFailure(java.lang.Throwable)
     */
    public void onFailure(Throwable e) {
        AS2PlusProcessor.getInstance().getLogger().error("Outgoing message task failure", e);
        if (!isRetryEnabled() || retried == getMaxRetries()) {
            try {
                message.setStatus(MessageDVO.STATUS_DELIVERY_FAILURE);
                message.setStatusDescription(e.toString());
                daoHandler.createMessageDAO().persist(message);
            }
            catch (Exception ex) {
                AS2PlusProcessor.getInstance().getLogger().error("Unable to mark failure to outgoing AS2 message: "+message.getMessageId(), ex);
            }
        }
    }

    /**
     * isRetryEnabled
     * @return boolean
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#isRetryEnabled()
     */
    public boolean isRetryEnabled() {
        return partnership.getRetries() > 0;
    }

    /**
     * getRetryInterval
     * @return long
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#getRetryInterval()
     */
    public long getRetryInterval() {
        return partnership.getRetryInterval();
    }

    /**
     * getMaxRetries
     * @return int
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#getMaxRetries()
     */
    public int getMaxRetries() {
        return partnership.getRetries();
    }

    /**
     * setRetried
     * @param retried
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#setRetried(int)
     */
    public void setRetried(int retried) {
        this.retried = retried;
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
