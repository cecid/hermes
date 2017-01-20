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
}