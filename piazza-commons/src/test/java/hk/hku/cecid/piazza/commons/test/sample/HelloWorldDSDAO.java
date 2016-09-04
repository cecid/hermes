/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.test.sample;

import java.util.List;

import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DVO;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAO;
 
public class HelloWorldDSDAO extends DataSourceDAO 
{
	public DVO createDVO() 
	{
		return new HelloWorldDSDVO();
	}
	
	@SuppressWarnings("unchecked")
	public List<HelloWorldDSDVO> findAll() throws DAOException
	{
		return super.find("find_all", new Object[]{});
	}
}
