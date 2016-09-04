/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.dao;

import java.util.List;
import java.util.Date;

import hk.hku.cecid.piazza.commons.dao.DAO;
import hk.hku.cecid.piazza.commons.dao.DAOException;

/**
 * @author Donahue Sze, Twinsen Tsang (modifiers)
 * 
 */
public interface MessageDAO extends DAO {
	
    public boolean findMessage(MessageDVO data) throws DAOException;

    public boolean findRefToMessage(MessageDVO data) throws DAOException;

    public List findInboxPendingMessagesByTimestamp(MessageDVO messageDVO)
		throws DAOException;

    public List findOutboxPendingMessagesByTimestamp(MessageDVO messageDVO)
    		throws DAOException;

    public List findOutboxProcessingMessagesByTimestamp(MessageDVO messageDVO)
    	throws DAOException;

    public List findMessageByCpa(MessageDVO data, int numberOfMessage)
            throws DAOException;
    
    public List findMessagesByTime(int time_period, MessageDVO data, int numberOfMessage, int offset) throws DAOException;

    public List findMessagesBeforeTime(int time_period) throws DAOException;
    
    public int findInboxReadyMaxSequenceNoByCpa(MessageDVO data)
    		throws DAOException;

    public int findMaxSequenceNoByMessageBoxAndCpa(MessageDVO data)
    		throws DAOException;
    
    public int findMaxSequenceGroupByMessageBoxAndCpa(MessageDVO data)
            throws DAOException;

    public int findNumOfMessagesByMessageBoxAndCpaAndSequenceGroup(
            MessageDVO data) throws DAOException;

    public boolean findOrderedMessageByMessageBoxAndCpaAndSequenceGroupAndSequenceNo(
            MessageDVO data) throws DAOException;

    public List findOrderedMessagesByMessageBoxAndCpaAndStatus(MessageDVO data)
            throws DAOException;

    public void addMessage(MessageDVO data) throws DAOException;

    public boolean updateMessage(MessageDVO data) throws DAOException;

    public void deleteMessage(MessageDVO data) throws DAOException;

    public List findMessagesByHistory(MessageDVO data, int numberOfMessage,
            int offset) throws DAOException;

    public int findNumberOfMessagesByHistory(MessageDVO data)
            throws DAOException;
    
    // Since H20 01062007
    /**
     * Update the status of all timed-out message to <code>status</code>.
     * A message is considered as timed-out if the timeout timestamp is 
     * earlier than the <code>currentTime</code>.
     * 
     * @throws DAOException
     * 			When <code>status</code> is null or 
     * 			Error in persistence connectivity. 
     * 
     * @see hk.hku.cecid.ebms.spa.dao.MessageDVO#getTimeoutTimestamp()
     * @see hk.hku.cecid.ebms.spa.dao.MessageDVO#setTimeoutTimestamp(java.sql.Timestamp)
     */
    public int updateTimedOutMessageStatus(String status, Date currentTime) throws DAOException;
}