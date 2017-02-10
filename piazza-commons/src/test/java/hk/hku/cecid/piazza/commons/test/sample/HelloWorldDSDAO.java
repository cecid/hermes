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
