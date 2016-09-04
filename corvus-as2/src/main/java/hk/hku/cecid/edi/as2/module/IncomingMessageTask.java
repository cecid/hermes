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
import hk.hku.cecid.edi.as2.dao.RepositoryDAO;
import hk.hku.cecid.edi.as2.dao.RepositoryDVO;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.piazza.commons.module.ActiveTask;

import java.io.ByteArrayInputStream;


/**
 * OutgoingMessageTask
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class IncomingMessageTask implements ActiveTask {

    private int retried;
    private MessageDVO message;
    private AS2DAOHandler daoHandler;
    
    /**
     * @throws AS2Exception
     * 
     */
    public IncomingMessageTask(MessageDVO messageData) throws AS2Exception {
        try {
            if (messageData == null) {
                throw new AS2Exception("No message data");
            }

            this.message = messageData;
            this.daoHandler = new AS2DAOHandler(AS2Processor.core.dao);
        }
        catch (Exception e) {
            throw new AS2Exception("Unable to construct incoming message task", e);
        }
    }

    /**
     * execute
     * @throws Exception
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#execute()
     */
    public void execute() throws Exception {

        RepositoryDAO repositoryDAO = daoHandler.createRepositoryDAO();
        RepositoryDVO repositoryDAOData = (RepositoryDVO) repositoryDAO.createDVO();
        repositoryDAOData.setMessageId(message.getMessageId());
        repositoryDAOData.setMessageBox(message.getMessageBox());
        
        if (repositoryDAO.retrieve(repositoryDAOData)) {
            ByteArrayInputStream messageContent = new ByteArrayInputStream(repositoryDAOData.getContent());
            AS2Message incomingMessage = new AS2Message(messageContent);
            messageContent.close();
            
            AS2Processor.getIncomingMessageProcessor().processReceivedMessage(incomingMessage);
        }
        else {
            throw new AS2Exception("No repository found for incoming AS2 message: "+message.getMessageId());
        }
    }

    /**
     * onFailure
     * @param e
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#onFailure(java.lang.Throwable)
     */
    public void onFailure(Throwable e) {
        AS2Processor.core.log.error("Incoming message task failure", e);
        if (retried == getMaxRetries()) {
            try {
                message.setStatus(MessageDVO.STATUS_PROCESSED_ERROR);
                message.setStatusDescription(e.toString());
                daoHandler.createMessageDAO().persist(message);
            }
            catch (Exception ex) {
                AS2Processor.core.log.error("Unable to mark failure to incoming AS2 message: "+message.getMessageId(), ex);
            }
        }
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
