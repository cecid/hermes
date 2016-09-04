/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.task;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.handler.MessageClassifier;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.module.ActiveTaskList;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * @author Donahue Sze
 * 
 */
public class OutboxCollector extends ActiveTaskList {

    boolean isFirstTime = true;

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveTaskList#getTaskList()
     */
    public List getTaskList() {

        List messageList = new Vector();

        try {
            MessageDAO dao = (MessageDAO) EbmsProcessor.core.dao
                    .createDAO(MessageDAO.class);
            MessageDVO finderDVO = (MessageDVO) dao.createDVO();

            // get all the processing message in first time
            if (isFirstTime) {
                List messageDVOList = dao.findOutboxProcessingMessagesByTimestamp(finderDVO);
                
                Iterator i = messageDVOList.iterator();
                int num = 0;
                while (i.hasNext()) {
                    EbmsProcessor.core.log
                            .info("Recover outbox message sending thread "
                                    + ++num + "/" + messageDVOList.size());
                    MessageDVO message = (MessageDVO) i.next();
                    OutboxTask outboxTask = new OutboxTask(message);
                    messageList.add(outboxTask);
                }
                isFirstTime = false;
            }

            // get all the pending message
            List messageDVOList = dao.findOutboxPendingMessagesByTimestamp(finderDVO);
            
            Iterator i = messageDVOList.iterator();
            while (i.hasNext()) {
                MessageDVO message = (MessageDVO) i.next();
                OutboxTask outboxTask = new OutboxTask(message);
                messageList.add(outboxTask);
            }
        } catch (DAOException e) {
            EbmsProcessor.core.log.error(
                    "Error in collecting message from outbox", e);
        }
        return messageList;
    }
}