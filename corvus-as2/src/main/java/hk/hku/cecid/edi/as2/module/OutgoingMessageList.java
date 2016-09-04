/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.module;

import hk.hku.cecid.edi.as2.AS2Processor;
import hk.hku.cecid.edi.as2.dao.MessageDAO;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.piazza.commons.module.ActiveTaskList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * OutgoingMessageList
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class OutgoingMessageList extends ActiveTaskList {

    public List getTaskList() {
        try {
            MessageDAO messageDAO = (MessageDAO)AS2Processor.core.dao.createDAO(MessageDAO.class);
            Iterator messages = messageDAO.findMessagesByStatus(MessageDVO.STATUS_PENDING, MessageDVO.MSGBOX_OUT).iterator();
            
            List tasks = new ArrayList();
            while (messages.hasNext()) {
                MessageDVO messageDAOData = (MessageDVO) messages.next();
                try {
                    OutgoingMessageTask task = new OutgoingMessageTask(messageDAOData);
                    messageDAOData.setStatus(MessageDVO.STATUS_PROCESSING);
                    messageDAOData.setStatusDescription("");
                    messageDAO.persist(messageDAOData);
                    tasks.add(task);
                }
                catch (Exception e) {
                    messageDAOData.setStatus(MessageDVO.STATUS_DELIVERY_FAILURE);
                    messageDAOData.setStatusDescription(e.toString());
                    messageDAO.persist(messageDAOData);
                    AS2Processor.core.log.error("Error in creating outgoing message task", e);
                }
            }
            return tasks;
        }
        catch (Exception e) {
            AS2Processor.core.log.error("Error in retrieving outgoing messages", e);
            return Collections.EMPTY_LIST;
        }
    }
}
