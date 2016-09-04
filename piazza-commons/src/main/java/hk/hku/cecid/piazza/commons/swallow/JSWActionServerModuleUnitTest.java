/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.swallow;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import hk.hku.cecid.piazza.commons.swallow.JSWActionServerModule.COMMAND;
import hk.hku.cecid.piazza.commons.test.ModuleTest;
import hk.hku.cecid.piazza.commons.test.asserts.NetworkAssert;

/** 
 * The <code>JSWActionServerModuleUnitTest</code> is the unit test of <code>JSWActionServerModule</code>.
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   JDK5.0, H2O 0908
 */
@RunWith(Parameterized.class)
public class JSWActionServerModuleUnitTest extends ModuleTest<JSWActionServerModule>
{
	public static final int TEST_DELAY = 1500;
	
	public static final String [] MODULE_DESCRIPTORS = 
	{
		"actionserver.module.def.xml",
		"actionserver.module.full.xml",
		"actionserver.module.none.xml"
	};
	
	public static final Object [] EXPECTED_RESULT = 
	{
		/*
		 * The expected result is started from 
		 * 1. listenPort
		 * 2. localConnectionOnly
		 * 3. shutdown
		 * 4. force terminate
		 * 5. restart
		 * 6. thread dump
		 * 7. access violation
		 * 8. JVM hang
		 * 9. unexpected terminate
		 */
		/*              1     2      3     4     5      6      7        8      9    */
		new Object[]{ 9998, true , true, true , true , true , false,  false,  false },
		new Object[]{ 9997, false, true, true , true , true , true ,  true ,  true  },
		new Object[]{ 9996, true, false, false, false, false, false,  false,  false }
	};
	
	@SuppressWarnings("unchecked")
	@Parameters 
	public static Collection getModuleDescriptionSet()
	{
		List<Object[]> parameters = new ArrayList<Object[]>();
		for (int i = 0; i < MODULE_DESCRIPTORS.length; i++)
		{
			parameters.add(new Object[]{MODULE_DESCRIPTORS[i], EXPECTED_RESULT[i]});	
		}
		return parameters;
	}
	
	private String moduleDescriptor;	
	private Object[] expectedInitialParamValue; 
	
	/**
	 * Create an instance of parameterized (parameter=module descriptor) JSWActionServerModuleUnitTest. 
	 *  
	 * @param moduleDescriptor The parameterized module descriptor.
	 */
	public JSWActionServerModuleUnitTest(String moduleDescriptor, Object[] expectedInitialParamValue)
	{
		this.moduleDescriptor = moduleDescriptor;
		this.expectedInitialParamValue = expectedInitialParamValue;
	}
	
	/* (non-JAVADOC)
	 * @see hk.hku.cecid.piazza.commons.test.ModuleTest#getModuleDescription()
	 */
	@Override
	public String getModuleDescription()
	{
		return this.moduleDescriptor;
	}

	/* (non-JAVADOC)
	 * @see hk.hku.cecid.piazza.commons.test.ModuleTest#initAtOnce()
	 */
	@Override
	public boolean initAtOnce()
	{
		return false;
	}
	
	/**
	 * Test whether all parameters are extracted correctly during the init phrase.
	 */
	@Test
	public void testInit()
	{
		/*
		 * Test initialization the target.
		 */
		JSWActionServerModule target = this.getTestingTarget();
		target.init();
		
		target.dumpEnabledAction();
		
		Assert.assertEquals(
				"The listen port does not as ",
				expectedInitialParamValue[0], 
				new Integer(target.getListenPort())
		);
		
		Assert.assertEquals(
				"The value local connection flag does not as ",
				expectedInitialParamValue[1], 
				new Boolean(target.getIsLocalConnectionOnly()));
				
		int i = 2;	// starting from index 2
		
		/*
		 * Assert each command action is same as expected 
		 */
		for (COMMAND c: COMMAND.values())
		{
			Assert.assertEquals(
				MessageFormat.format("The value of property {0} does not as ", c.getPropertyKey()),				
				expectedInitialParamValue[i++], 
				new Boolean(target.isActionEnabled(c))
			);			
		}
	}
	
	
	
	/**
	 * Test whether the server able to start / stop properly. 
	 * 
	 * @throws Throwable 
	 */
	@Test
	public void testStartStop() throws Throwable
	{
		/*
		 * Test initialization the target.
		 */
		JSWActionServerModule target = this.getTestingTarget();
		target.init();
		
		// =========================================================================
		// Test action server start
		// =========================================================================
		
		/*
		 * Start the server
		 */
		target.start();
		
		logger.debug("[JSW ActServer] Started .. {}", target.getThread().getName());
		
		Thread.sleep(TEST_DELAY);

		String reachableHost   = null;
		// String unreachableHost = null;
		
		boolean isLocalConnectionOnly = target.getIsLocalConnectionOnly();
		int listenPort                = target.getListenPort();
		
		// =========================================================================
		// Test action server reject other connection if localConnectionOnly is set
		// =========================================================================
		
		/*
		 * Here we test extra stuff from the below code fragment, we need to test
		 * whether the action server can be connected  
		 */
		if (isLocalConnectionOnly)
		{
			reachableHost   = InetAddress.getByName("localhost").getHostAddress(); // Use local-host / 127.0.0.1 address.
			// unreachableHost = InetAddress.getLocalHost().getHostAddress(); 
			
			/*
			 * For local connection, we need to check whether the server reject 
			 * connection other than local-host.
			 */
			// NetworkAssert.assertSocketNonReachable(unreachableHost, listenPort, 5000);
			
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			while (nets.hasMoreElements()) {
				Enumeration<InetAddress> addrs = ((NetworkInterface) nets.nextElement()).getInetAddresses();
				while (addrs.hasMoreElements()) {
					InetAddress address = (InetAddress)addrs.nextElement();
					if (!(address instanceof Inet6Address) 
							&& !address.isLoopbackAddress()) {
						logger.debug("Assert nonreachable address {}", address.getHostAddress());
						NetworkAssert.assertSocketNonReachable(address.getHostAddress(), listenPort, 5000);
					}
				}
			}				
			
		}
		else
		{
			reachableHost   = InetAddress.getLocalHost().getHostAddress();
		}
		
		/*
		 * Assert whether the socket for the server is able to listen. 
		 * 
		 * check the wrapper action server has been started or not.
		 */
		NetworkAssert.assertSocketReachable(reachableHost, listenPort, 5000);
				
		
		// ==========================================
		// Test action server stop
		// ==========================================
		
		logger.debug("[JSW ActServer] Going to stop .. {}", target.getThread().getName());
		
		/*
		 * Now stop the server and check whether we should not able to connect to action server
		 * any more.
		 */
		target.stop();
		
		Thread.sleep(TEST_DELAY);
				
		String host = InetAddress.getByName("localhost").getHostAddress();
		
		NetworkAssert.assertSocketNonReachable(host, target.getListenPort(), 5000);
	}
	
	/*@Test
	public void test() throws Throwable
	{
		
		 * Test initialization the target.
		 
		JSWActionServerModule target = this.getTestingTarget();
		target.init();
		
		// =========================================================================
		// Test action server start
		// =========================================================================
		
		
		 * Start the server
		 
		target.start();
		
		target.dumpEnabledAction(System.out);
		
		java.net.Socket s = new java.net.Socket("127.0.0.1", 9997);
		s.getOutputStream().write((int)'V');
		
		Thread.sleep(Integer.MAX_VALUE);
	}*/
}
