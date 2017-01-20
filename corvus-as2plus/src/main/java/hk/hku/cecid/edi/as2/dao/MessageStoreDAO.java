package hk.hku.cecid.edi.as2.dao;

import hk.hku.cecid.piazza.commons.dao.DAO;
import hk.hku.cecid.piazza.commons.dao.DAOException;

/**
 * @author Donahue Sze
 * 
 */
public interface MessageStoreDAO extends DAO {
	
	public void storeMessage(final MessageDVO primalMsgDVO, final MessageDVO msgDVO, 
			final RepositoryDVO repoDVO, final RawRepositoryDVO rawRepoDVO) throws DAOException;

    public void storeMessage(MessageDVO[] messageDVO,
            RepositoryDVO[] repositoryDVO) throws DAOException;

    public void storeMessage(MessageDVO messageDVO,
            RepositoryDVO repositoryDVO) throws DAOException;

    public void storeReceipt(MessageDVO receiptMessageDVO,
            RepositoryDVO receiptRepositoryDVO,
            MessageDVO originalMessageDVO) throws DAOException;
}