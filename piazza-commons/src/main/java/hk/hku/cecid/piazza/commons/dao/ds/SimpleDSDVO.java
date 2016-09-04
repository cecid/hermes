/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.dao.ds;

import java.util.Hashtable;

/**
 * SimpleDSDVO is just a concrete subclass of DataSourceDVO. It should
 * not be used directly when the underlying persistent storage would be changed
 * to any kind that does not support Java DataSource.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class SimpleDSDVO extends DataSourceDVO {

    /**
     * Creates a new instance of SimpleDSDVO.
     */
    public SimpleDSDVO() {
        super();
    }

    /**
     * Creates a new instance of SimpleDSDVO.
     * 
     * @param data the source data of this DVO.
     */
    public SimpleDSDVO(Hashtable data) {
        super(data);
    }
}