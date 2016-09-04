/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.dao.ds;

import java.util.List;

import hk.hku.cecid.piazza.commons.dao.DVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;

/**
 * SimpleDSDAO is a concrete subclass of DataSourceDAO which provides some
 * convenient methods for querying and updating its underlying data source. It
 * should not be used directly when the underlying persistent storage would be
 * changed to any kind that does not support Java DataSource.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class SimpleDSDAO extends DataSourceDAO {

    /**
     * Creates a new instance of SimpleDSDAO.
     */
    public SimpleDSDAO() {
        super();
    }

    /**
     * Creates a new instance of SimpleDSDVO.
     */
    public DVO createDVO() {
        return new SimpleDSDVO();
    }

    /**
     * Retrieves data from the data source by searching with the specified key
     * values.
     * 
     * @param keys the key values for querying the data source. The number,
     *            format, and sequence of the parameter values should match the
     *            key finder statement.
     * @return a SimpleDSDVO found by the specified key values. If there are
     *         more than one DVO found, the first one will be returned. null
     *         will be returned if nothing was found.
     * @throws DAOException if errors found when retrieving data from the data
     *             source.
     */
    public SimpleDSDVO find(Object[] keys) throws DAOException {
        return (SimpleDSDVO) super.findByKey(keys);
    }

    /**
     * Retrieves data from the data source as a List of DVO by searching
     * with the specified data values.
     * 
     * @param finder the name of the finder SQL statement.
     * @param paras the parameter values used by the specified finder statement.
     *            The number, format, and sequence of the parameter values
     *            should match the statement. null if there is no parameter for
     *            the statement.
     * @return a List of SimpleDSDVO found by the specified data values. An
     *         empty List will be returned if there is no matching data.
     * @throws DAOException if errors found when retrieving data from the data
     *             source.
     */
    public List find(String finder, Object[] paras) throws DAOException {
        return super.find(finder, paras);
    }

    /**
     * Update the data source with the specified data values.
     * 
     * @param sqlname the name of the SQL statement to be used.
     * @param paras the data values to be updated to the data source.
     * @return an integer indicating the update result. Same as the value
     *         returned by java.sql.Statement.executeUpdate().
     * @throws DAOException if errors found when updating data to the data
     *             source.
     */
    public int update(String sqlname, Object[] paras) throws DAOException {
        return super.update(sqlname, paras);
    }
}