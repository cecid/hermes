/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DVO;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;

/**
 * @author Donahue Sze
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MessageDataSourceDAO extends DataSourceDAO implements MessageDAO {

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.dao.DAO#createNewDVO()
     */
    public DVO createDVO() {
        return new MessageDataSourceDVO();
    }

    public boolean findMessage(MessageDVO data) throws DAOException {
        return super.retrieve((MessageDataSourceDVO) data);
    }

    public void addMessage(MessageDVO data) throws DAOException {
        super.create((MessageDataSourceDVO) data);
    }

    public void deleteMessage(MessageDVO data) throws DAOException {
        super.remove((MessageDataSourceDVO) data);
    }

    public boolean updateMessage(MessageDVO data) throws DAOException {
        return super.persist((MessageDataSourceDVO) data);
    }

    // find the ref to message
    public boolean findRefToMessage(MessageDVO data) throws DAOException {
        List l = super.find("find_ref_to_message", new Object[] {
                data.getRefToMessageId(), data.getMessageBox(),
                data.getMessageType() });
        Iterator i = l.iterator();
        if (i.hasNext()) {
            ((MessageDataSourceDVO) data).setData(((MessageDataSourceDVO) i.next()).getData());
            return true;
        }
        return false;
    }
    
	public List findMessagesByTime(int time_period, MessageDVO data, int numberOfMessage, int offset) throws DAOException {
		List messages = findMessagesByHistory(data,numberOfMessage,offset);
		Iterator itr = messages.iterator();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.MONTH,-time_period);
		long cutOffDate = calendar.getTimeInMillis();
		Timestamp time = new Timestamp(cutOffDate);
		ArrayList result = new ArrayList();
		MessageDVO temp;
		for(int dvoi = 0;itr.hasNext();dvoi++){
			if((temp = (MessageDVO)itr.next()).getTimeStamp().after(time))
				result.add(temp);
		}
		return result;
	}

	public List findMessagesBeforeTime(int time_period) throws DAOException {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.MONTH,-time_period);
		long cutOffDate = calendar.getTimeInMillis();
		Timestamp time = new Timestamp(cutOffDate);
		Object[] param = {time};
		return super.find("get_all_before_time",param);
	}
	
    /**
     * Find all the pending messages for inbox collector order by timestamp.
     * 
     * @param data 			The message data value object.
     * @return 				a List of DVO resulted from the specified SQL query. An empty
     *         				List will be returned if there is no matching data.
     * @throws DAOException if errors found when retrieving data from the data
     *             source.
     */
    public List findInboxPendingMessagesByTimestamp(MessageDVO data) throws DAOException {
    	return super.find("find_inbox_pending_messages_by_timestamp",
    			new Object[] {});
    }
	
    /**
     * Find all the pending messages for outbox collector order by timestamp.
     * 
     * @param data 			The message data value object.
     * @return 				a List of DVO resulted from the specified SQL query. An empty
     *         				List will be returned if there is no matching data.
     * @throws DAOException if errors found when retrieving data from the data
     *             source.
     */
    public List findOutboxPendingMessagesByTimestamp(MessageDVO data) throws DAOException {
    	return super.find("find_outbox_pending_messages_by_timestamp",
    			new Object[] {});
    }

    /**
     * Find all the processing messages for outbox collector order by timestamp.
     * 
     * @param data 			The message data value object.
     * @return 				a List of DVO resulted from the specified SQL query. An empty
     *         				List will be returned if there is no matching data.
     * @throws DAOException if errors found when retrieving data from the data
     *             source.
     */
    public List findOutboxProcessingMessagesByTimestamp(MessageDVO data) throws DAOException {
    	return super.find("find_outbox_processing_messages_by_timestamp",
    			new Object[] {});
    }

    /**
     * Find all the message by CPA, status, message type and message box.
     * Only for EbMS Message Collector Service
     * 
     * @param data 				The message data value object.
     * @param numberOfMessage 	The no. of message return.
     * @return 					a List of DVO resulted from the specified SQL query. An empty
     *         					List will be returned if there is no matching data.
     * @throws DAOException 	if errors found when retrieving data from the data
     *             source.
     */
    public List findMessageByCpa(MessageDVO data, int numberOfMessage)
            throws DAOException {
    	List parameters = new ArrayList();

    	String sql = super.getFinder("find_message_by_cpa");
    	parameters.add(data.getCpaId());
    	parameters.add(data.getService());
    	parameters.add(data.getAction());
    	
    	if (data.getConvId() != null) {
    		sql += " AND " + getFilter("find_message_by_cpa_filter_conv_id");
    		parameters.add(data.getConvId());
    	}
    	if (data.getFromPartyId() != null) {
    		sql += " AND " + getFilter("find_message_by_cpa_filter_from_party_id");
    		parameters.add(data.getFromPartyId());
    	}
    	if (data.getFromPartyRole() != null) {
    		sql += " AND " + getFilter("find_message_by_cpa_filter_from_party_role");
    		parameters.add(data.getFromPartyRole());
    	}
    	if (data.getToPartyId() != null) {
    		sql += " AND " + getFilter("find_message_by_cpa_filter_to_party_id");
    		parameters.add(data.getToPartyId());
    	}
    	if (data.getToPartyRole() != null) {
    		sql += " AND " + getFilter("find_message_by_cpa_filter_to_party_role");
    		parameters.add(data.getToPartyRole());
    	}

    	sql += " " + getOrder("find_message_by_cpa_order");
    	parameters.add(new Integer(numberOfMessage));
    	
    	return executeQuery(sql, parameters.toArray());    	
    }

    /**
     * Find max sequence no. of inbox message in PS or DL status by CPA
     * Only for EbMS Inbox Collector Service
     * 
     * @param data 			The message data value object.
     * @return 				max sequence no. -1 will be returned if there is no matching data.
     * @throws DAOException if errors found when retrieving data from the data
     *             source.
     */
    public int findInboxReadyMaxSequenceNoByCpa(MessageDVO data)
    	throws DAOException {
    	List l = super.find(
    			"find_inbox_ready_max_sequence_no_by_cpa",
    			new Object[] { data.getCpaId(),
	                data.getService(), data.getAction(), data.getConvId() });
    	Iterator i = l.iterator();
    	if (i.hasNext()) {
    		MessageDataSourceDVO resultData = (MessageDataSourceDVO) i.next();
    		return resultData.getSequenceNo();
    	}
    	return -1;
	}

    /**
     * Find max sequence no. of message by CPA and message box
     * 
     * @param data 			The message data value object.
     * @return 				max sequence no. -1 will be returned if there is no matching data.
     * @throws DAOException if errors found when retrieving data from the data
     *             source.
     */    
    public int findMaxSequenceNoByMessageBoxAndCpa(MessageDVO data)
            throws DAOException {
        List l = super.find("find_max_sequence_no_by_message_box_and_cpa",
                new Object[] { data.getMessageBox(), data.getCpaId(),
                		data.getService(), data.getAction(), data.getConvId() });
        Iterator i = l.iterator();
        if (i.hasNext()) {
            MessageDataSourceDVO resultData = (MessageDataSourceDVO) i.next();
            return resultData.getSequenceNo();
        }
        return -1;
    }

    public int findMaxSequenceGroupByMessageBoxAndCpa(MessageDVO data)
            throws DAOException {
        List l = super
                .find("find_max_sequence_group_by_message_box_and_cpa",
                        new Object[] { data.getMessageBox(), data.getCpaId(),
                                data.getService(), data.getAction(),
                                data.getConvId() });
        Iterator i = l.iterator();
        if (i.hasNext()) {
            MessageDataSourceDVO resultData = (MessageDataSourceDVO) i.next();
            return resultData.getSequenceGroup();
        }
        return -1;
    }

    public int findNumOfMessagesByMessageBoxAndCpaAndSequenceGroup(
            MessageDVO data) throws DAOException {
        try {
            List result = super
                    .executeRawQuery(
                            super
                                    .getFinder("find_num_of_messages_by_message_box_and_cpa_and_sequence_group"),
                            new Object[] { data.getMessageBox(),
                                    data.getCpaId(), data.getService(),
                                    data.getAction(), data.getConvId(),
                                    new Integer(data.getSequenceGroup()) });
            List resultEntry = (List) result.get(0);
            return ((Number) resultEntry.get(0)).intValue();
        } catch (Exception e) {
            throw new DAOException(
                    "Unable to find the number of messages by history", e);
        }
    }

    public boolean findOrderedMessageByMessageBoxAndCpaAndSequenceGroupAndSequenceNo(
            MessageDVO data) throws DAOException {
        List l = super
                .find(
                        "find_ordered_message_by_message_box_and_cpa_and_sequence_group_and_sequence_no",
                        new Object[] { data.getMessageBox(), data.getCpaId(),
                                data.getService(), data.getAction(),
                                data.getConvId(),
                                new Integer(data.getSequenceGroup()),
                                new Integer(data.getSequenceNo()) });
        Iterator i = l.iterator();
        if (i.hasNext()) {
            ((MessageDataSourceDVO) data).setData(((MessageDataSourceDVO) i
                    .next()).getData());
            return true;
        }
        return false;
    }

    // find all the ordered messages by messagebox, cpa and status
    public List findOrderedMessagesByMessageBoxAndCpaAndStatus(MessageDVO data)
            throws DAOException {
        return super.find(
                "find_ordered_messages_by_message_box_and_cpa_and_status",
                new Object[] { data.getMessageBox(), data.getCpaId(),
                        data.getService(), data.getAction(), data.getConvId(),
                        data.getStatus() });
    }

    /**
     * Find messages order by descending timestamp by different criteria.
     * 
     * @param data 				The message data value object carrying query criteria.
     * @param numberOfMessage 	max no. of message in return.
     * @param offset 			no. of starting record in return.  
     * @throws DAOException
     */
    public List findMessagesByHistory(MessageDVO data, int numberOfMessage,
            int offset) throws DAOException {
    	   	
    	List parameters = new ArrayList();
    	boolean hasSearchCriteria = false;
    	String sql = super.getFinder("find_message_by_history");
    	
    	if (data.getMessageId() != null && !data.getMessageId().trim().equals("")) {
    		sql += " AND " + getFilter("find_message_by_history_filter_message_id");
    		parameters.add(data.getMessageId());
    	}
    	
    	if (data.getMessageBox() != null && !data.getMessageBox().trim().equals("")) {
    		sql += " AND " + getFilter("find_message_by_history_filter_message_box");
    		parameters.add(data.getMessageBox());
    	}
    	
    	if (data.getCpaId() != null && !data.getCpaId().trim().equals("")) {
    		sql += " AND " + getFilter("find_message_by_history_filter_cpa_id");
    		parameters.add(data.getCpaId());
    	}
    	
    	if (data.getService() != null && !data.getService().trim().equals("")) {
    		sql += " AND " + getFilter("find_message_by_history_filter_service");
    		parameters.add(data.getService());
    	}
    	
    	if (data.getAction() != null && !data.getAction().trim().equals("")) {
    		sql += " AND " + getFilter("find_message_by_history_filter_action");
    		parameters.add(data.getAction());
    	}
    	
    	if (data.getStatus() != null  && !data.getStatus().trim().equals("")) {
    		sql += " AND " + getFilter("find_message_by_history_filter_status");
    		parameters.add(data.getStatus());
    	}
    	
    	if (data.getConvId() != null && !data.getConvId().trim().equals("")) {
    		sql += " AND " + getFilter("find_message_by_history_filter_conv_id");
    		parameters.add(data.getConvId());
    	}
    	
    	if (data.getPrimalMessageId() != null && !data.getPrimalMessageId().trim().equals("")) {
    		sql += " AND " + getFilter("find_message_by_history_filter_primal_message_id");
    		parameters.add(data.getPrimalMessageId());
    	}
    	
		sql += " " + getOrder("find_message_by_history_order");
    	parameters.add(new Integer(numberOfMessage));
    	parameters.add(new Integer(offset));

        return executeQuery(sql, parameters.toArray());
    }
    
    /**
     * Find number of messages by different criteria.
     * 
     * @param data 			The message data value object carrying query criteria.
     * @throws DAOException
     */
    public int findNumberOfMessagesByHistory(MessageDVO data)
            throws DAOException {
        try {
        	List parameters = new ArrayList();        	
        	String sql = super.getFinder("find_number_of_message_by_history");
        	
        	if (data.getMessageId() != null && !data.getMessageId().trim().equals("")) {
        		sql += " AND " +  getFilter("find_number_of_message_by_history_filter_message_id");
        		parameters.add(data.getMessageId());        		
        	}
        	
        	if (data.getMessageBox() != null && !data.getMessageBox().trim().equals("")) {
        		sql += " AND " + getFilter("find_number_of_message_by_history_filter_message_box");
        		parameters.add(data.getMessageBox());
        	}
        	
        	if (data.getCpaId() != null && !data.getCpaId().trim().equals("")) {
        		sql += " AND " + getFilter("find_number_of_message_by_history_filter_cpa_id");
        		parameters.add(data.getCpaId());
        	}
        	
        	if (data.getService() != null && !data.getService().trim().equals("")) {
        		sql += " AND " +getFilter("find_number_of_message_by_history_filter_service");
        		parameters.add(data.getService());
        	}
        	
        	if (data.getAction() != null && !data.getAction().trim().equals("")) {
        		sql += " AND " + getFilter("find_number_of_message_by_history_filter_action");
        		parameters.add(data.getAction());
        	}
        	
        	if (data.getStatus() != null && !data.getStatus().trim().equals("")) {
        		sql += " AND " + getFilter("find_number_of_message_by_history_filter_status");
        		parameters.add(data.getStatus());
        	}
        	
        	if (data.getConvId() != null && !data.getConvId().trim().equals("")) {
        		sql += " AND " + getFilter("find_number_of_message_by_history_filter_conv_id");
        		parameters.add(data.getConvId());
        	}
        	
        	if (data.getPrimalMessageId() != null && !data.getPrimalMessageId().trim().equals("")) {
        		sql += " AND " + getFilter("find_number_of_message_by_history_filter_primal_message_id");
        		parameters.add(data.getPrimalMessageId());
        	}        	

    		List queryResult = executeRawQuery(sql, parameters.toArray());
            List resultEntry = (List) queryResult.get(0);
            return ((Number) resultEntry.get(0)).intValue();        	
        } catch (Exception e) {
            throw new DAOException(
                    "Unable to find the number of messages by history", e);
        }
    }
    
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
	public int updateTimedOutMessageStatus(String status, Date currentTime) throws DAOException {
		if (status == null)
			throw new DAOException("The required param 'status' is missing.");
		// Transform the date to timestamp object.
		Timestamp ts = new Timestamp( currentTime == null ? System.currentTimeMillis() : currentTime.getTime());
		// Execute the update
		return this.executeUpdate(this.getSQL("updated_timed_out_message_status"), new Object[]{status, ts});
	}    
}