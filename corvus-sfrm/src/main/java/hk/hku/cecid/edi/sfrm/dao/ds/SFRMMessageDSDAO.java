/**
 * Provides implementation class for the database access object 
 * (DAO and DVO) for the database layer. 
 */
package hk.hku.cecid.edi.sfrm.dao.ds;

import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;

import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DVO;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

/**
 * The data access object controller for the 
 * database table <code>sfrm_message</code>. It 
 * provides some useful database-level queries.<br><br>
 * 
 * Creation Date: 29/9/2006
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	1.0.0
 */
public class SFRMMessageDSDAO extends DataSourceDAO implements SFRMMessageDAO {
	
	/**
	 * Create a new SFRM Message record Object.
	 * 
	 * @return a new SFRM Message record Object.
	 */		
	public DVO 
	createDVO() 
	{
		return new SFRMMessageDSDVO();
	}
	
	/**
	 * Find a message record with specified message id.
	 * 
	 * @param messageId		The message id of message to be found.
	 * @param messageBox	The message box of message. it should be "INBOX" or "OUTBOX".
	 * @return				return null if not found, otherwise a message record.
	 * @throws DAOException	
	 */
	public SFRMMessageDVO 
	findMessageByMessageIdAndBox(
			String messageId,
			String messageBox) throws DAOException 
	{
		return (SFRMMessageDVO) super.findByKey(new Object[]{messageId, messageBox});			
	}

	/**
	 * Find a message record with specified message record.<br> 
	 * The field "message id" and "message box" will be used for record finding.
	 *
	 * @param message		The message record object to be used for searching.
	 * @return				return null if not found, otherwise a message record.
	 * @throws DAOException	
	 */
	public SFRMMessageDVO 
	findMessageByMessageIdAndBox(
			SFRMMessageDVO message) throws DAOException
	{
		if (message != null){
			return this.findMessageByMessageIdAndBox(
					message.getMessageId(),
					message.getMessageBox());
		}
		return null;
	}			
	
	/**
	 * Find a message record according to the message box and it's 
	 * status
	 * 
	 * @param messageBox	
	 * 			The message box of message. it should be "INBOX" or "OUTBOX".
	 * @param status
	 * 			The status of the message.
	 * @return a list of message record that satisfy this condition.
	 * @throws DAOException
	 */
	public List 
	findMessageByMessageBoxAndStatus(
			String messageBox,
			String status) throws DAOException
	{
		return super.find("find_message_by_message_box_and_status",
				 		  new Object[] { messageBox, status });		
	}
	
	/**
	 * Find a list of message record according to the message box 
	 * and partnership id and status.
	 * 
	 * @param messageBox
	 * 			The message box of the message.
	 * @param partnershipId
	 * 			The partnership id of the message.  
	 * @param status
	 * 			The status of the message.
	 * @return a list of message record the satisfy this condition.
	 * @throws DAOException
	 * 			if any kind of errors.
	 */
	public List
	findMessageByMessageBoxAndPartnershipIdAndStatus(
			String messageBox,
			String partnershipId,
			String status) throws DAOException
	{
		return super.find(
				"find_message_by_message_box_and_partnership_id_status",
				new Object[] { messageBox, partnershipId, status } );
	}
	
	/**
	 * Find number of the message in the message history by a given search criteria given by dao
	 * @param dao Search Criteria
	 * @return Number of message found
	 * @throw DAOException if any kind of errors
	 */
	public int findNumberOfMessagesByHistory(SFRMMessageDVO data) throws DAOException{
		try{
			List parameters = new ArrayList();
			ArrayList paraList = new ArrayList();
			String sql = getFinder("find_number_of_message_by_history");
			if(data.getMessageId()!=null && !data.getMessageId().trim().equals("")){
				paraList.add(getFilter("find_number_of_message_by_history_filter_message_id"));
				parameters.add(data.getMessageId());
			}
			
			if(data.getMessageBox()!=null && !data.getMessageBox().trim().equals("")){
				paraList.add(getFilter("find_number_of_message_by_history_filter_message_box"));
				parameters.add(data.getMessageBox().toUpperCase());
			}
			
			if(data.getStatus()!=null && !data.getStatus().trim().equals("")){
				paraList.add(getFilter("find_number_of_message_by_history_filter_status"));
				parameters.add(data.getStatus());
			}
						
			if(paraList.size()>0){
				String paraListStr[] = toStringArray(paraList.toArray());
				sql += " WHERE " + StringUtilities.concat(paraListStr, " AND ");
			}
			
			List queryResult = executeRawQuery(sql, parameters.toArray());
			List resultEntry = (List) queryResult.get(0);
			return ((Number) resultEntry.get(0)).intValue();			
		}catch(Exception e){
			 throw new DAOException("Unable to find the number of messages by history", e);
		}
	}
	
	/**
     * Find messages order by descending timestamp by different criteria.
     * 
     * @param data 				The message data value object carrying query criteria.
     * @param numberOfMessage 	max no. of message in return.
     * @param offset 			no. of starting record in return.  
     * @throws DAOException		if any kind of errors
     */
    public List findMessagesByHistory(SFRMMessageDVO data, int numberOfMessage, int offset) throws DAOException{
    	try{
			List parameters = new ArrayList();
			String sql = getFinder("find_message_by_history");
			ArrayList paraList = new ArrayList();
								
			applyCommonFilter(data, paraList, parameters);
						
			if(paraList.size()>0){
				String paraListStr[] = toStringArray(paraList.toArray());
				sql += " WHERE " + StringUtilities.concat(paraListStr, " AND ");
			}
			
			sql += " " + getOrder("find_message_by_history_order");
			parameters.add(new Integer(numberOfMessage));
			parameters.add(new Integer(offset));
			SFRMProcessor.getInstance().getLogger().info(sql);
			
			List queryResult = executeQuery(sql, parameters.toArray());
			return queryResult;
		}catch(Exception e){
			 throw new DAOException("Unable to find the number of messages by history", e);
		}
    }
    
    /**
     * Find the message by the bound of certain time period. The bounded time period is in term of completed timestamp of message
     * @param time_period How many month before today
     * @param data Criteria to search the message for
     * @param numberOfMessage Number of message show in the page
     * @param offset Offset of the data in the list of the search result
     * @return List of SFRMMessageDVO that contain the search result 
     */
    public List findMessagesByTime(int time_period, SFRMMessageDVO data, int numberOfMessage, int offset) throws DAOException { 	
    	try{
			List parameters = new ArrayList();
			String sql = getFinder("find_message_by_history");
			ArrayList paraList = new ArrayList();
								
			applyCommonFilter(data, paraList, parameters);
					
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.add(GregorianCalendar.MONTH,-time_period);
			long cutOffDate = calendar.getTimeInMillis();
			
			Timestamp time = new Timestamp(cutOffDate);
			
			paraList.add(getFilter("find_message_by_history_filter_before_date"));
			parameters.add(time);
			
			if(paraList.size()>0){
				String paraListStr[] = toStringArray(paraList.toArray());
				sql += " WHERE " + StringUtilities.concat(paraListStr, " AND ");
			}
			
			sql += " " + getOrder("find_message_by_history_order");
			parameters.add(new Integer(numberOfMessage));
			parameters.add(new Integer(offset));
			
			SFRMProcessor.getInstance().getLogger().info(sql);
			
			List queryResult = executeQuery(sql, parameters.toArray());
			return queryResult;
		}catch(Exception e){
			 throw new DAOException("Unable to find the number of messages by history", e);
		}
	}
    
    public List findMessageForAcknowledgement(int numberOfMessage, int offset) throws DAOException{
    	try{
    		List messagesDVO = new ArrayList();
    		List parameters = new ArrayList();
    		//Construct the SQL
    		String sql = getFinder("find_message_for_acknowledgement");
    		String order = getOrder("find_message_for_acknowledgement_order");
    		sql = sql + " " + order;
    		//Build the parameters
    		parameters.add(new Integer(numberOfMessage));
    		parameters.add(new Integer(offset));
    		List queryResult = executeQuery(sql, parameters.toArray());
    		return queryResult;   		   		
    	}catch(Exception e){
    		e.printStackTrace();
    		throw new DAOException("Unable to find the message that is ready for requesting acknowledgement");
    	}
    }
    
    private void applyCommonFilter(SFRMMessageDVO data, List paraList, List parameters){
    	if(data.getMessageId()!=null && !data.getMessageId().trim().equals("")){
			paraList.add(getFilter("find_number_of_message_by_history_filter_message_id"));
			parameters.add(data.getMessageId());
		}
		
		if(data.getMessageBox()!=null && !data.getMessageBox().trim().equals("")){
			paraList.add(getFilter("find_number_of_message_by_history_filter_message_box"));
			parameters.add(data.getMessageBox().toUpperCase());
		}
		
		if(data.getStatus()!=null && !data.getStatus().trim().equals("")){
			paraList.add(getFilter("find_number_of_message_by_history_filter_status"));
			parameters.add(data.getStatus());
		}
    }
    
    private String[] toStringArray(Object[] objs){
    	String[] strings = new String[objs.length];
    	for(int i=0; objs.length > i; i++){
    		strings[i] = (String)objs[i];
    	}
    	return strings;
    }
}
