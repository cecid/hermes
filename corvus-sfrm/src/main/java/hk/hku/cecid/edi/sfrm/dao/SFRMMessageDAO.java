/**
 * Provides inferace for the database access object (DAO and DVO) 
 * for the database layer. 
 */
package hk.hku.cecid.edi.sfrm.dao;

import java.util.List;

import hk.hku.cecid.piazza.commons.dao.DAO;
import hk.hku.cecid.piazza.commons.dao.DAOException;

/**
 * The Interface of SFRM message dao.
 * 
 * Creation Date: 3/10/2006
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	Hermes 0818
 */
public interface SFRMMessageDAO extends DAO {

	/**
	 * Find a message record with specified message id.
	 * 
	 * @param messageId		
	 * 			The message id of message to be found.
	 * @param messageBox	
	 * 			The message box of message. it should be "INBOX" or "OUTBOX".
	 * @return				return null if not found, otherwise a message record.
	 * @throws DAOException	
	 * 			if any kind of errors. 
	 */
	public SFRMMessageDVO 
	findMessageByMessageIdAndBox(
			String messageId,
			String messageBox) throws DAOException;
	
	/**
	 * Find a message record with specified message record.<br>
	 * The field "message id" and "message box" will be used for record finding.
	 * 
	 * @param message
	 *            The message record object to be used for searching.
	 * @return return null if not found, otherwise a message record.
	 * @throws DAOException
	 * 			if any kind of errors. 
	 */
	public SFRMMessageDVO 
	findMessageByMessageIdAndBox(
			SFRMMessageDVO message)	throws DAOException;
	
	/**
	 * Find a list of message record according to the message box and it's
	 * status. 
	 * 
	 * @param messageBox	
	 * 			The message box of message. it should be "INBOX" or "OUTBOX".
	 * @param status
	 * 			The status of the message.
	 * @return a list of message record that satisfy this condition.
	 * @throws DAOException
	 * 			if any kind of errors. 
	 */
	public List 
	findMessageByMessageBoxAndStatus(
			String messageBox,
			String status) throws DAOException;
	
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
			String status) throws DAOException;
	
	/**
	 * Find number of the message in the message history by a given search criteria given by dao
	 * @param dao 			Search Criteria
	 * @return 				Number of message found
	 * @throw DAOException 	if any kind of errors
	 */
	public int findNumberOfMessagesByHistory(SFRMMessageDVO dao) throws DAOException;
	
	
	/**
     * Find messages order by descending timestamp by different criteria.
     * 
     * @param data 				The message data value object carrying query criteria.
     * @param numberOfMessage 	max no. of message in return.
     * @param offset 			no. of starting record in return.  
     * @throws DAOException		if any kind of errors
     */
    public List findMessagesByHistory(SFRMMessageDVO data, int numberOfMessage,
            int offset) throws DAOException;
    
    /**
     * Find the message by the bound of certain time period
     * @param time_period 		How many month before today
     * @param data 				Criteria to search the message for
     * @param numberOfMessage 	Number of message show in the page
     * @param offset 			Offset of the data in the list of the search result
     * @return 					List of SFRMMessageDVO that contain the search result
     * @throws DAOException		if any kind of errors 
     */
    public List findMessagesByTime(int time_period, SFRMMessageDVO data, int numberOfMessage, int offset) throws DAOException;
    
    /**
     * Find the message that is ready for requesting the acknowledgement
     * @param numberOfMessage	Number of message show in the page
     * @param offset			Offset of the data in the list of the search result
     * @return					List of SFRMMessageDVO that is determined for ready to request for acknowledgement
     * @throws DAOException		if any kind of errors
     * @since 2.0.0
     */
    public List findMessageForAcknowledgement(int numberOfMessage, int offset) throws DAOException;
}
