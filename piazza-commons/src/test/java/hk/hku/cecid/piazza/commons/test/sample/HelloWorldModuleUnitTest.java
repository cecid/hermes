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
