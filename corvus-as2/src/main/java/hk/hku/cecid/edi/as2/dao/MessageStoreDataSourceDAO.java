package hk.hku.cecid.edi.as2.dao;

import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DVO;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceProcess;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceTransaction;

/**
 * @author Donahue Sze
 * 
 */
public class MessageStoreDataSourceDAO extends DataSourceDAO implements
        MessageStoreDAO {

    public void storeMessage(final MessageDVO messageDVO,
            final RepositoryDVO repositoryDVO) throws DAOException {
        storeMessage(new MessageDVO[]{messageDVO},
                new RepositoryDVO[]{repositoryDVO});
    }

    public void storeMessage(final MessageDVO[] messageDVO,
            final RepositoryDVO[] repositoryDVO) throws DAOException {

        DataSourceProcess process = new DataSourceProcess(this) {
            protected void doTransaction(DataSourceTransaction tx) throws DAOException {
                MessageDAO messageDAO = (MessageDAO) getFactory().createDAO(MessageDAO.class);
                RepositoryDAO repositoryDAO = (RepositoryDAO) getFactory().createDAO(RepositoryDAO.class);
                
                messageDAO.setTransaction(tx);
                repositoryDAO.setTransaction(tx);

                if (messageDVO != null) {
                    for (int i = 0; i < messageDVO.length; i++) {
                        if (messageDVO[i] != null) {
                            messageDAO.create(messageDVO[i]);
                        }
                    }
                }
                if (repositoryDVO != null) {
                    for (int i = 0; i < repositoryDVO.length; i++) {
                        if (repositoryDVO[i] != null) {
                            repositoryDAO.create(repositoryDVO[i]);
                        }
                    }
                }
            }
        };

        process.start();
    }

    public void storeReceipt(final MessageDVO receiptMessageDVO,
            final RepositoryDVO receiptRepositoryDVO,
            final MessageDVO originalMessageDVO) throws DAOException {
        
        DataSourceProcess process = new DataSourceProcess(this) {
            protected void doTransaction(DataSourceTransaction tx) throws DAOException {
                MessageDAO messageDAO = (MessageDAO) getFactory().createDAO(MessageDAO.class);
                RepositoryDAO repositoryDAO = (RepositoryDAO) getFactory().createDAO(RepositoryDAO.class);

                messageDAO.setTransaction(tx);
                repositoryDAO.setTransaction(tx);

                if (receiptMessageDVO != null && receiptRepositoryDVO != null) {
                    messageDAO.create(receiptMessageDVO);
                    repositoryDAO.create(receiptRepositoryDVO);
                }
                messageDAO.persist(originalMessageDVO);
            }
        };

        process.start();
    }

    public DVO createDVO() {
        return null;
    }

}