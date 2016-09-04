package hk.hku.cecid.edi.as2.dao;

import hk.hku.cecid.piazza.commons.dao.DVO;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;

public class RawRepositoryDataSourceDAO extends DataSourceDAO implements RawRepositoryDAO {
    public DVO createDVO() {
        return new RawRepositoryDataSourceDVO();
    }
}
