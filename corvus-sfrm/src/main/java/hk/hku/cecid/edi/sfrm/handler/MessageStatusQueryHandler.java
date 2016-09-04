package hk.hku.cecid.edi.sfrm.handler;

import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDAO;
import hk.hku.cecid.edi.sfrm.spa.SFRMComponent;
import hk.hku.cecid.edi.sfrm.util.StatusQuery;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Handler to hold all the speed measurement class for active transferring SFRM Message. 
 * Active transferring message mean the message status is in PS processing
 * @author Patrick Yip
 * @version 2.0.0
 * @since 2.0.0
 */
public class MessageStatusQueryHandler extends SFRMComponent {
	/*
	 * Hash table tool all of the active message speed meter.<br>
	 * key - the message ID<br>
	 * value - NetworkSpeedContainer object
	 */
	private static Hashtable<String, StatusQuery> msgSpeedQuery = new Hashtable<String, StatusQuery>();
	private static MessageStatusQueryHandler msq;
	
	protected void init() throws Exception{
		super.init();
		msq = this;
	}
	
	public static MessageStatusQueryHandler getInstance(){
		return msq;
	}
		
	/**
	 * Add a message for monitoring the message status
	 * @param messageId SFRM Message ID
	 * @return newly created StatusQuery
	 */
	
	public StatusQuery addMessageSpeedQuery(String messageId) throws DAOException{
		if(msgSpeedQuery.containsKey(messageId)){
			msgSpeedQuery.get(messageId);
		}
		
		SFRMMessageSegmentDAO dao = (SFRMMessageSegmentDAO) this.getDAOFactory().createDAO(SFRMMessageSegmentDAO.class);
		StatusQuery speedQuery = new StatusQuery(messageId, dao);
		msgSpeedQuery.put(messageId, speedQuery);
		return speedQuery;
	}
	
	/**
	 * Remove the status query for particular message
	 * @param messageId SFRM Message ID
	 * @return removed SpeedQuery
	 */
	public StatusQuery removeMessageSpeedQuery(String messageId){
		//Check if the Network Speed Query is exist
		if(!msgSpeedQuery.containsKey(messageId))
			return null;
		
		StatusQuery query = msgSpeedQuery.remove(messageId);
		return query;
	}
	
	/**
	 * Get the StatusQuery for a particular message
	 * @param messageId
	 * @return StatusQuery for a given message, null if is non-exist
	 */
	public StatusQuery getMessageSpeedQuery(String messageId){
		return msgSpeedQuery.get(messageId);
	}
	
	/**
	 * Get the list of message ID which is monitoring by this handler
	 * @return iterator of message ID
	 */
	public Iterator<String> getMessageList(){
		return msgSpeedQuery.keySet().iterator();
	}
	
	/**
	 * Get the total speed for the currently transfering message
	 * @return total speed
	 */
	public double getTotalSpeed(){
		Iterator<String> msgIter = getMessageList();
		double totalSpeed = 0.0;
		while(msgIter.hasNext()){
			String msgId = msgIter.next();
			StatusQuery query = getMessageSpeedQuery(msgId);
			totalSpeed += query.getCurrentSpeed();
		}
		return totalSpeed;
	}
	
}
