package hk.hku.cecid.ebms.spa.dao;

import hk.hku.cecid.piazza.commons.dao.DAO;
import hk.hku.cecid.piazza.commons.dao.DAOException;

/**
 * @author Donahue Sze
 * 
 */
public interface MessageServerDAO extends DAO {
    public void storeMessage(MessageDVO messageDVO,
            RepositoryDVO repositoryDVO)
            throws DAOException;
    
    /*public void storeInboxMessage(final MessageDVO messageDVO,
            final InboxDVO inboxDVO) throws DAOException;*/
    
    public void storeOutboxMessage(
    		MessageDVO messageDVO,
            RepositoryDVO repositoryDVO, 
            OutboxDVO outboxDVO,
            MessageDVO primalMsgDVO) throws DAOException;

    public void clearMessage(MessageDVO data) throws DAOException;
}