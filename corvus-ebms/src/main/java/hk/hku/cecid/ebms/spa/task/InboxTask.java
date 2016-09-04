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
import hk.hku.cecid.ebms.spa.dao.InboxDAO;
import hk.hku.cecid.ebms.spa.dao.InboxDVO;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.handler.EbxmlMessageDAOConvertor;
import hk.hku.cecid.ebms.spa.handler.MessageClassifier;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.module.ActiveTask;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

/**
 * @author Donahue Sze
 * 
 */
public class InboxTask implements ActiveTask {

    private MessageDVO message;

    private long nextOrderNo;

    public InboxTask(MessageDVO message, long nextOrderNo) {
        this.message = message;
        this.nextOrderNo = nextOrderNo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#execute()
     */
    public void execute() throws Exception {
        try {
            MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                    .createDAO(MessageDAO.class);
            message.setStatus(MessageClassifier.INTERNAL_STATUS_PROCESSED);
            message.setStatusDescription("Message is processed");
            messageDAO.updateMessage(message);

            InboxDAO inboxDAO = (InboxDAO) EbmsProcessor.core.dao
                    .createDAO(InboxDAO.class);
            InboxDVO inboxDVO = (InboxDVO) inboxDAO.createDVO();
            inboxDVO.setMessageId(message.getMessageId());
            inboxDVO.setOrderNo(nextOrderNo);
            inboxDAO.create(inboxDVO);
			
			fireEvent();

            EbmsProcessor.core.log.info("Ebxml Message ("
                    + message.getMessageId()
                    + ") is stored in inbox with order number: " + nextOrderNo);
        } catch (DAOException e) {
            EbmsProcessor.core.log
                    .error("Error in storing message to inbox", e);
            throw new DeliveryException("Error in storing message to inbox", e);
        }
    }
	
	private void fireEvent() throws MessageValidationException {
		EbmsEventModule eventModule = (EbmsEventModule) EbmsProcessor
				.getModuleGroup().getModule(EbmsEventModule.MODULE_ID);
		if (eventModule.hasListeners()) {
			EbxmlMessage ebxmlMessage = EbxmlMessageDAOConvertor
					.getEbxmlMessage(message.getMessageId(),
							MessageClassifier.MESSAGE_BOX_INBOX);

			// Get message type
			MessageClassifier messageClassifier = new MessageClassifier(
					ebxmlMessage);
			String messageType = messageClassifier.getMessageType();

			if (messageType
					.equalsIgnoreCase(MessageClassifier.MESSAGE_TYPE_ERROR)) {
				eventModule.fireErrorOccurred(ebxmlMessage);
			} else if (messageType
					.equalsIgnoreCase(MessageClassifier.MESSAGE_TYPE_ACKNOWLEDGEMENT)) {
				eventModule.fireResponseReceived(ebxmlMessage);
			} else if (messageType
					.equalsIgnoreCase(MessageClassifier.MESSAGE_TYPE_ORDER)) {
				eventModule.fireMessageReceived(ebxmlMessage);
			}
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
        try {
            EbmsProcessor.core.log.error(
                    "Exception when store the msg to inbox", arg0);
            MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                    .createDAO(MessageDAO.class);
            message.setStatus(MessageClassifier.INTERNAL_STATUS_PROCESSED_ERROR);
            message.setStatusDescription("Processing error when put message into inbox: "
                                    + StringUtilities.toString(arg0));
            messageDAO.updateMessage(message);
        } catch (DAOException e) {
            // e.printStackTrace();
        }
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

}