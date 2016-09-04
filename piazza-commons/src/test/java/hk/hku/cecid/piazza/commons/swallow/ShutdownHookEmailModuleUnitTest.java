/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.swallow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.hamcrest.CoreMatchers.*;

import hk.hku.cecid.piazza.commons.swallow.ShutdownHookEmailModule;
import hk.hku.cecid.piazza.commons.swallow.ShutdownHookEmailThread;
import hk.hku.cecid.piazza.commons.test.ModuleTest;
import hk.hku.cecid.piazza.commons.util.Instance;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

/** 
 * The <code>ShutdownHookEmailModuleUnitTest</code> is unit test of <code>ShutdownHookEmailModule</code>.
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   JDK5.0, H2O 0908
 */
@RunWith(Parameterized.class)
public class ShutdownHookEmailModuleUnitTest extends ModuleTest<ShutdownHookEmailModule>
{
	public static final String [] MODULE_DESCRIPTORS = 
	{
		"shutdown.hook.email.module.def.xml",
		"shutdown.hook.email.module.rand.xml",
	};
	
	@SuppressWarnings("unchecked")
	@Parameters 
	public static Collection getModuleDescriptionSet()
	{
		List<Object[]> parameters = new ArrayList<Object[]>();
		for (int i = 0; i < MODULE_DESCRIPTORS.length; i++)
		{
			parameters.add(new Object[]{MODULE_DESCRIPTORS[i]});	
		}
		return parameters;
	}
	
	private String moduleDescriptor;	
	
	
	/* (non-JAVADOC)
	 * @see hk.hku.cecid.piazza.commons.test.ModuleTest#getModuleDescription()
	 */
	@Override
	public String getModuleDescription()
	{
		return moduleDescriptor;
	}

	/* (non-JAVADOC)
	 * @see hk.hku.cecid.piazza.commons.test.ModuleTest#initAtOnce()
	 */
	@Override
	public boolean initAtOnce()
	{
		return true;
	}
 
	/**
	 * Create an instance of parameterized (parameter=module descriptor) ShutdownHookEmailModuleUnitTest. 
	 *  
	 * @param moduleDescriptor The parameterized module descriptor.
	 */
	public ShutdownHookEmailModuleUnitTest(String moduleDescriptor)
	{
		this.moduleDescriptor = moduleDescriptor;
	}	
	
	/**
	 * Test whether {@link ShutdownHookEmailModule#getThread()} always return non null value. 
	 */
	@Test
	public void testGetThread() 
	{
		Assert.assertNotNull("The worker is always not-null", super.getTestingTarget().getThread());
	}
	
	/**
	 * Test whether the thread from {@link ShutdownHookEmailModule#getThread()} is registered inside the runtime shutdown hook. 
	 */	
	@Test
	public void testShutdownHookRegisteredToRuntime() throws Throwable
	{
		Assert.assertTrue(
			"The mail shutdown hook has not been registered to runtime", 
			Runtime.getRuntime().removeShutdownHook(this.getTestingTarget().getThread()));
	}
	
	/**
	 *  This test has not implemented yet because it is not a automated test case (it is for debugging purpose only).
	 */
	@Test
	@Ignore
	public void testStop()
	{
		super.getTestingTarget().stop();
		
	}
	
	public static class CreateShudownHookWorkerAssertionThread extends Thread
	{
		private Properties p;
		private Thread shutdownThread;
		
		public CreateShudownHookWorkerAssertionThread(Properties assertionProperties, Thread shutdownThread)
		{
			this.p = assertionProperties;
			this.shutdownThread = shutdownThread;
		}
		
		public void run()
		{
			Assert.assertThat("Email shutdown hook must be instance of ShutdownHookEmailThread.", 
				shutdownThread, instanceOf(ShutdownHookEmailThread.class));	
			
			final String host  	  = p.getProperty("host");		
			final String protocol = p.getProperty("protocol", "smtp");
			final String username = p.getProperty("username");
			final String password = p.getProperty("password");
			final String from     = p.getProperty("from", "commonDaemon@cecid.hku.hk");
			final String tos      = p.getProperty("to");
			final String ccs      = p.getProperty("cc");
			final String subject  = p.getProperty("subject", ShutdownHookEmailThread.DEFAULT_SHUTDOWN_MAIL_SUBJECT);
			final boolean verbose = StringUtilities.parseBoolean(p.getProperty("verbose"), false); 
			
			ShutdownHookEmailThread shutdownEmailThread = (ShutdownHookEmailThread) shutdownThread;
			Assert.assertEquals("Protocol does not as expect", protocol, shutdownEmailThread.getProtocol());
			Assert.assertEquals("Host does not as expect"    , host    , shutdownEmailThread.getHost());
			Assert.assertEquals("Username does not as expect", username, shutdownEmailThread.getUsername());
			Assert.assertEquals("Password does not as expect", password, shutdownEmailThread.getPassword());
			Assert.assertEquals("'From' does not as expect"  , from    , shutdownEmailThread.getFrom());
			Assert.assertEquals("'Tos' does not as expect"   , tos     , shutdownEmailThread.getTos());
			Assert.assertEquals("'CCs' does not as expect"   , ccs     , shutdownEmailThread.getCcs());
			Assert.assertEquals("Subject does not as expect" , subject , shutdownEmailThread.getSubject());
			Assert.assertEquals("Verbose flag not equal"     , verbose , shutdownEmailThread.getIsVerbose());	
		}
	}
	
	/**
	 * Test whether the {@link ShutdownHookEmailModule#createShutdownHookWorker()} able to wire up 
	 * all properties from the module descriptor and it is an instance of ShutdownHookEmailThread.
	 * 
	 * Note that the assertion actually take place in a helper class called CreateShudownHookWorkerAssertionThread.
	 * It is essential because the ShutdownHook worker thread is loaded through a clone of current class loader    
	 * and therefore the class only appear in that domain.  
	 */
	@Test
	public void testCreateShutdownHookWorker() throws Throwable
	{
		ShutdownHookEmailModule m = super.getTestingTarget();
				
		final Properties p = m.getParameters();
		final Thread shutdownThread = m.createShutdownHookWorker();
		final ClassLoader shutdownThreadCL = shutdownThread.getClass().getClassLoader();
		
		/*
		 * Create the helper thread which is in the same domain to the shutdown hook worker.
		 */
		Instance ins = new Instance(
			CreateShudownHookWorkerAssertionThread.class.getName(),
			shutdownThreadCL,
			new Class[] {Properties.class, Thread.class},
			new Object[]{p, shutdownThread});
		
		Thread helperThread = (Thread) ins.getObject();
		
		/*
		 * Run the helper and assert the properties are wired properly.
		 */
		helperThread.setContextClassLoader(shutdownThreadCL);
		helperThread.run();
	}
	
	/**
	 * This test has not implemented yet because the mail subject is subject to change.
	 */	
	@Test
	@Ignore
	public void testOnCreateMailNotificationSubject() throws Throwable
	{
		
	}

	/**
	 * This test has not implemented yet because the mail subject is subject to change.
	 */	
	@Test
	@Ignore
	public void testOnCreateMailNotificationBody() throws Throwable
	{
		
	}
	
	/* (non-JAVADOC)
	 * @see hk.hku.cecid.piazza.commons.test.UnitTest#tearDown()
	 */
	@Override
	public void tearDown() throws Exception
	{
		super.tearDown();
		
		/*
		 * Stop the module so there is no alert email deliver (suppress some annoying exception)
		 */
		ShutdownHookEmailModule m = this.getTestingTarget();
		if (m != null) m.stop();
	}
}
