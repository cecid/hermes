/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.dao;

import hk.hku.cecid.piazza.commons.dao.DAO;
import hk.hku.cecid.piazza.commons.dao.DAOException;

/**
 * @author Donahue Sze
 * 
 */
public interface MessageStoreDAO extends DAO {

    public void storeMessage(MessageDVO[] messageDVO,
            RepositoryDVO[] repositoryDVO) throws DAOException;

    public void storeMessage(MessageDVO messageDVO,
            RepositoryDVO repositoryDVO) throws DAOException;

    public void storeReceipt(MessageDVO receiptMessageDVO,
            RepositoryDVO receiptRepositoryDVO,
            MessageDVO originalMessageDVO) throws DAOException;
}