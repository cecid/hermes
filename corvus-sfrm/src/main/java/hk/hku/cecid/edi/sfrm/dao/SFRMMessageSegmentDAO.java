/**
 * Provides inferace for the database access object (DAO and DVO) 
 * for the database layer. 
 */
package hk.hku.cecid.edi.sfrm.dao;

import java.sql.Timestamp;
import java.util.List;

import hk.hku.cecid.piazza.commons.dao.DAO;
import hk.hku.cecid.piazza.commons.dao.DAOException;

/**
 * 
 * Creation Date: 3/10/2006
 * 
 * @author Twinsen Tsang
 * @version 1.0.1
 * @since	1.0.0
 */
public interface SFRMMessageSegmentDAO extends DAO {

	/**
	 * Find a message segment record with specified parameters.<br>
	 * The field "message id", "message box" , "segment no" and 
	 * "segment type" will be used for record searching.
	 * 
	 * @param messageId
	 *            The message id of the message segment.
	 * @param messageBox
	 *            The message box of the message segment.
	 * @param segmentNo
	 * 			  The segment no of the message segment.
	 * @param type
	 *            The type of the message segment
	 * @return A message segment record if found.
	 * @since
	 * 			  1.0.1
	 * @throws DAOException
	 * 			  Any kind of database error.
	 */
	public SFRMMessageSegmentDVO 
	findMessageSegmentByMessageIdAndBoxAndType(
			String 	messageId, 
			String 	messageBox, 
			int 	segmentNo, 
			String 	type) throws 
			DAOException;	
	
	/**
	 * Find a message segment recrod with specified parameters.<br/>
	 * The field "message id", "message box" and "type" will be used
	 * for record searching.<br/><br/>
	 * 
	 * The message segment extracted is the last updated segments
	 * by other module. 
	 * 
	 * @param messageId
	 *            The message id of the message segment. 
	 * @param messageBox
	 *            The message box of the message segment. 
	 * @param type
	 *            The type of the message segment 
	 * @return A message segment record if found.
	 * @since	
	 * 			  1.0.4
	 * @throws DAOException
	 * 			  Any kind of database error. 
	 */
	public SFRMMessageSegmentDVO
	findLastUpdatedMessageSegmentByMessageIdAndBoxAndType(
			String 	messageId,
			String 	messageBox,
			String	type) throws 
			DAOException;
	
	/**
	 * Find a set of message segment record with specified 
	 * message box and message status.<br><br>
	 * 
	 * @param messageBox
	 *            The message box of the message segment. 
	 * @param status
	 * 			  The status of the message segment.
	 * @param limit
	 * 			  The maximum message segment can be retrieved at one invocation. 
	 * @return A set of message segment which meets the specified condition
	 * 		   or empty list if no record matched.
	 * @throws DAOException
	 * 			  Any kind of database error.
	 */
	public List 
	findMessageSegmentsByMessageBoxAndStatus(
			String 	messageBox, 
			String 	status, 
			int 	limit) throws 
			DAOException;
	
	/**
	 * Find a set of message segment record with specified 
	 * message box and message status.<br><br>
	 *  
	 * @param messageBox
	 *            The message box of the message segment. 
	 * @param status
	 * 			  	The status of the message segment.
	 * @param messageStatus
	 * 				The associated main message status of the segment.	 			
	 * @param limit
	 * 			  	The maximum message segment can be retrieved at one invocation. 
	 * @return A set of message segment which meets the specified condition
	 * 		   or empty list if no record matched.
	 * @throws DAOException
	 * 			  Any kind of database error.
	 */
	public List 
	findMessageSegmentsByMessageBoxAndStatusAndMessageStatusNotEqualTo(
			String 	messageBox, 
			String 	status, 
			String 	messageStatus, 
			int 	limit) throws 
			DAOException;
	
	/**
	 * TODO: Refactor
	 * 
	 * @param messageBox
	 * @param status
	 * @param type
	 * @param messageStatus
	 * @param limit
	 * @return
	 * @throws DAOException
	 */
	public List
	findMessageSegmentByMessageBoxAndStatusAndTypeAndMessageStatusNotEqualTo(
			String 	messageBox, 
			String 	status, 
			String  type,
			String 	messageStatus, 
			int 	limit) throws 
			DAOException;
	
	/**
	 * Find-out all segments which are incomplete in SFRM semantic.<br/><br/>
	 *  
	 * Incomplete Segments are defined as their corresponding message 
	 * is not in the status of either 'DF' or 'PS'.<br/><br/>   
	 * 
	 * The query support wildcard on <code>status</code> by using '%' string. 
	 */
	public List
	findIncompleteSegments(
			String messageBox,
			String status,
			String type,
			int    limit) throws
			DAOException; 
	
	/**
	 * Find how many segments is available into the database.
	 * 
	 * @param messageId
	 *            The message id of the message segment.
	 * @param messageBox
	 *            The message box of the message segment.
	 * @param type 
	 * 			  The segment type of the message segment.
	 * @param status 
	 * 			  The status of the message segment.
	 * @return
	 * @throws DAOException
	 */
	public int 
	findNumOfSegmentByMessageIdAndBoxAndTypeAndStatus(
			String messageId, 
			String messageBox,
			String type, 
			String status) throws 
			DAOException;
	
	/**
	 * Find segment by their message Id, nessage box, message type and message status
	 * 
	 * @param messageId
	 *            The message id of the message segment.
	 * @param messageBox
	 *            The message box of the message segment.
	 * @param type 
	 * 			  The segment type of the message segment.
	 * @param status 
	 * 			  The status of the message segment.
	 * @return
	 * @throws DAOException
	 */
	public List findSegmentsByMessageIdAndBoxAndTypeAndStatus(
			String messageId, 
			String messageBox,
			String type, 
			String status) throws 
			DAOException;
		
	/**
	 * Find the maximum number of segment no in
	 * the database from the specified parameters.
	 * 
	 * @param messageId
	 *            The message id of the message segment.
	 * @param messageBox
	 *            The message box of the message segment.
	 * @param type
	 *            The type of the message segment             
	 */
	public int
	findMaxSegmentNoByMessageIdAndBoxAndType(
			String messageId,
			String messageBox,
			String type) throws 
			DAOException;
	
	/**
	 * Find segment by their message id, message box, type and list of segment number
	 * @param messageId message ID
	 * @param messageBox message box
	 * @param type segment type
	 * @param segmentNos list of segment number
	 * @return list of SFRMMessageSegmentDVO
	 * @throws DAOException
	 */
	public List 
	findSegmentByMessageIdAndBoxAndTypeAndNos(
			String messageId,
			String messageBox,
			String type,
			List<Integer> segmentNos) throws
			DAOException;
	
	public int updateBatchSegmentsRecoveryStatus(
			String status,
			String messageId,
			String messageBox,
			String segmentType,
			List<Integer> segNums) throws 
			DAOException;
	
	public int updateBatchSegmentsStatus(
			String status,
			Timestamp completeTime,
			String messageId,
			String messageBox,
			String segmentType,
			List<Integer> segNums) throws 
			DAOException;
	
	
	/**
	 * Find how many segments is available into the database.
	 * 
	 * @param messageId
	 *            The message id of the message segment.
	 * @param messageBox
	 *            The message box of the message segment.
	 * @param type 
	 * 			  The segment type of the message segment.
	 * @param statues 
	 * 			  The statues of the message segment.
	 * @return
	 * @throws DAOException
	 */
	public long 
	findNumOfBytesSentByMessageIdAndBoxAndTypeAndStatues(
			String messageId, 
			String messageBox,
			String type, 
			long proceedTime,
			List<String> statues) throws 
			DAOException;
	
}
