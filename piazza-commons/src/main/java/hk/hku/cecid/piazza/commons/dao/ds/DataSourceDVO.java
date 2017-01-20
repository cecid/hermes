package hk.hku.cecid.piazza.commons.dao.ds;

import hk.hku.cecid.piazza.commons.dao.AbstractDVO;

import java.util.Hashtable;

/**
 * The DataSourceDVO, which implements the DVO interface, is simply a
 * subclass of the AbstractDVO. It is expected to be used by a
 * DataSourceDAOFactory.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public abstract class DataSourceDVO extends AbstractDVO {

    /**
     * Creates a new instance of DataSourceDVO.
     */
    protected DataSourceDVO() {
        super();
    }

    /**
     * Creates a new instance of DataSourceDVO.
     * 
     * @param data the source data of this DVO.
     */
    protected DataSourceDVO(Hashtable data) {
        super(data);
    }
}