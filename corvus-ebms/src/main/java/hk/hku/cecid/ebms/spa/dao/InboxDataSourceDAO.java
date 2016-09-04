/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.dao;

import hk.hku.cecid.piazza.commons.dao.DVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;

import java.util.Iterator;
import java.util.List;

/**
 * @author Donahue Sze
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InboxDataSourceDAO extends DataSourceDAO implements InboxDAO {

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.dao.DAO#createNewDVO()
     */
    public DVO createDVO() {
        return new InboxDataSourceDVO();
    }

    public boolean findInbox(InboxDVO data) throws DAOException {
        return super.retrieve((InboxDataSourceDVO) data);
    }

    public void addInbox(InboxDVO data) throws DAOException {
        super.create((InboxDataSourceDVO) data);
    }

    public boolean updateInbox(InboxDVO data) throws DAOException {
        return super.persist((InboxDataSourceDVO) data);
    }

    public void deleteInbox(InboxDVO data) throws DAOException {
        super.remove((InboxDataSourceDVO) data);
    }

    /**
     * Returns next value of inbox order no. from DB sequence. it determines message order being retrieved by client.
     * For DB, PostgreSQL & Oracle built-in auto number (sequence) will be used.
     * Otherwise for mySQL, max(order_no) + 1 will be returned.
     * Default starting order no is 1.
     * 
     * @return the value of next inbox order no.  
     */    
    public long findInboxNextOrderNo() throws DAOException {
    	try {
    		List result = super.executeRawQuery(super.getFinder("find_inbox_next_order_no"),
    				new Object[] {});
        	List resultEntry = (List) result.get(0);
        	return ((Number) resultEntry.get(0)).longValue();
        } catch (Exception e) {
        	return 1;
        }
    }
    
}