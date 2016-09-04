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
