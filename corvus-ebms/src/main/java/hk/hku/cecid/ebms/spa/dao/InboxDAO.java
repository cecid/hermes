/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.dao;

import hk.hku.cecid.piazza.commons.dao.DAO;
import hk.hku.cecid.piazza.commons.dao.DAOException;

/**
 * @author Donahue Sze
 * 
 */
public interface InboxDAO extends DAO {
    public boolean findInbox(InboxDVO data) throws DAOException;

    public void addInbox(InboxDVO data) throws DAOException;

    public void deleteInbox(InboxDVO data) throws DAOException;

    public boolean updateInbox(InboxDVO data) throws DAOException;

    /**
     * Returns next value of inbox order no. from DB sequence. it determines message order being retrieved by client.
     * 
     * @return the value of next inbox order no.  
     */    
    public long findInboxNextOrderNo() throws DAOException;
}