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
import hk.hku.cecid.ebms.spa.dao.InboxDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.module.ActiveTaskList;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * @author Donahue Sze
 * 
 */
public class InboxCollector extends ActiveTaskList {

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

            // get all the pending message and sort by sequence number
            MessageDVO finderDVO = (MessageDVO) dao.createDVO();

            List messageDVOList = dao.findInboxPendingMessagesByTimestamp(finderDVO);
            
            Iterator i = messageDVOList.iterator();
            while (i.hasNext()) {
                MessageDVO message = (MessageDVO) i.next();
                if (message.getSequenceNo() != -1) {
                    // use cpa to find max seq which is in processing
                    MessageDVO findMaxSequenceNoDVO = (MessageDVO) dao
                            .createDVO();
                    findMaxSequenceNoDVO.setCpaId(message.getCpaId());
                    findMaxSequenceNoDVO.setService(message.getService());
                    findMaxSequenceNoDVO.setAction(message.getAction());
                    findMaxSequenceNoDVO.setConvId(message.getConvId());

                    int currentSequenceNumber = dao.findInboxReadyMaxSequenceNoByCpa(findMaxSequenceNoDVO);
                    
                    if (message.getSequenceNo() == (currentSequenceNumber + 1)
                            || message.getSequenceNo() == 0) {
                        // if the sequence number is continue or 0, add to the list
                        currentSequenceNumber = message.getSequenceNo();
                        
                        InboxDAO inboxDAO = (InboxDAO) EbmsProcessor.core.dao.createDAO(InboxDAO.class);
                        InboxTask inboxTask = new InboxTask(message, inboxDAO.findInboxNextOrderNo());
                        messageList.add(inboxTask);
                    }
                } else {
                    InboxDAO inboxDAO = (InboxDAO) EbmsProcessor.core.dao.createDAO(InboxDAO.class);
                    InboxTask inboxTask = new InboxTask(message, inboxDAO.findInboxNextOrderNo());
                    messageList.add(inboxTask);
                }
            }
        } catch (DAOException e) {
            EbmsProcessor.core.log.error(
                    "Error in collecing message from inbox", e);
        }
        return messageList;
    }

}