package hk.hku.cecid.piazza.commons.test.sample;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.test.DAOTest;

public class HelloWorldDSDAOUnitTest extends DAOTest<HelloWorldDSDAO>
{
	@Override
	public String getTableName() 
	{
		return "hello_world";
	}
	
	@Test
	public void testFindAll() throws DAOException 
	{		
		HelloWorldDSDAO dao = super.getTestingTarget();
		List<HelloWorldDSDVO> rs = dao.findAll();
		
		Assert.assertEquals("Expected there are three rows in the table!", 3, rs.size());		
	}
}
