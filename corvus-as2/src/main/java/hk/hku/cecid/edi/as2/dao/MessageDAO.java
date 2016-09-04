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

import java.util.List;

/**
 * @author Donahue Sze
 *  
 */
public interface MessageDAO extends DAO {

    public List findMessageByOriginalMessageID(String oriMessageID, String oriMessageBox)
            throws DAOException;

    public List findMessagesByStatus(String status, String messageBox)
            throws DAOException;
    
    public List findMessagesByHistory(MessageDVO data, int numberOfMessage, int offset) 
            throws DAOException;
    
    public List findMessagesByTime(int time_period, MessageDVO data, int numberOfMessage, int offset) throws DAOException;
    
    public List findMessagesBeforeTime(int months) throws DAOException;
    
    public int findNumberOfMessagesByHistory(MessageDVO data)
            throws DAOException;

    public int recoverProcessingMessages()
            throws DAOException;
}