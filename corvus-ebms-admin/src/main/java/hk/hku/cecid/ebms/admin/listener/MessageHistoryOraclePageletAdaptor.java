package hk.hku.cecid.ebms.admin.listener;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.handler.MessageClassifier;

import hk.hku.cecid.piazza.commons.dao.DAOException;

import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor;

import java.util.Iterator;


/**
 * @author Donahue Sze
 *  
 */
public class MessageHistoryOraclePageletAdaptor extends MessageHistoryPageletAdaptor {
	private Iterator findMessageWithPagination(MessageDVO data, MessageDAO messageDAO, int numberOfMessage, int offset,int displayLastInt, boolean isTime) throws DAOException{
		 if(!isTime){
			// oracle only
			return messageDAO.findMessagesByHistory(data, numberOfMessage + offset, offset).iterator();
		}else{
			// oracle only            	
			return messageDAO.findMessagesByTime(displayLastInt,data, numberOfMessage + offset, offset).iterator();
		}
	}
}