package hk.hku.cecid.edi.as2.dao;

import hk.hku.cecid.piazza.commons.dao.DVO;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;

/**
 * @author Donahue Sze
 *  
 */
public class RepositoryDataSourceDAO extends DataSourceDAO implements
        RepositoryDAO {

    public DVO createDVO() {
        return new RepositoryDataSourceDVO();
    }
    
    /**
     * The performance is not as good as using Persist method
    public int updateContent
    		(byte[] content, String messageId, String messageBox )
    	throws DAOException{
    	return super.update("update_repository_content",
    				new Object[]{content, messageId, messageBox});
    }
    */
}