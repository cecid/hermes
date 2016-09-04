/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.test.sample;

import hk.hku.cecid.piazza.commons.module.ActiveTaskModule;
import hk.hku.cecid.piazza.commons.test.ModuleTest;

import org.junit.Test;

public class HelloWorldModuleUnitTest extends ModuleTest<ActiveTaskModule>
{
	@Override public String getModuleDescription()
	{
		return "hello_world.module.unit.test.xml";
	}

	@Override
	public boolean initAtOnce()
	{
		return true;
	}
	
	@Test public void testStart()
	{
		this.getTestingTarget().start();
	}
}
